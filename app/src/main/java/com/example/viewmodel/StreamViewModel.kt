package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.PipelineEngine
import com.example.data.StreamDataSource
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class BroadcastStudioState(
    val isBroadcasting: Boolean = false,
    val broadcastSeconds: Int = 0,
    val currentFps: Float = 60.0f,
    val currentIngestBitrate: Int = 6120,
    val streamKey: String = "live_sp_98fa72cd310e",
    val ingestServerUrl: String = "rtmp://ingest.streampulse.io/live",
    val isTorchOn: Boolean = false,
    val isFrontCamera: Boolean = false,
    val isMicMuted: Boolean = false,
    val activeViewers: Int = 0,
    val droppedFramesCount: Int = 0
)

data class StreamUiState(
    val currentStream: LiveStream = StreamDataSource.availableStreams.first(),
    val allStreams: List<LiveStream> = StreamDataSource.availableStreams,
    val selectedCategory: String = "All",
    val searchQuery: String = "",
    val selectedQuality: StreamQuality = StreamDataSource.qualityProfiles.first(),
    val qualityOptions: List<StreamQuality> = StreamDataSource.qualityProfiles,
    val chatMessages: List<ChatMessage> = StreamDataSource.initialChatMessages,
    val pinnedSuperChats: List<SuperChatDonation> = StreamDataSource.initialSuperChats,
    val activeReactions: List<String> = emptyList(),
    val isSubscribedToCurrent: Boolean = false,
    val pipelineConfig: PipelineConfig = PipelineConfig(),
    val pipelineTelemetry: PipelineTelemetry = PipelineEngine.computeTelemetry(PipelineConfig()),
    val broadcastStudioState: BroadcastStudioState = BroadcastStudioState(),
    val creatorEarnings: CreatorEarnings = CreatorEarnings()
)

class StreamViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StreamUiState())
    val uiState: StateFlow<StreamUiState> = _uiState.asStateFlow()

    private var broadcastTimerJob: Job? = null
    private var simulatedChatJob: Job? = null

    init {
        startSimulatedChatTraffic()
    }

    private fun startSimulatedChatTraffic() {
        simulatedChatJob?.cancel()
        simulatedChatJob = viewModelScope.launch {
            val randomViewers = listOf(
                "AeroGamer" to "What a counter-attack! 🔥",
                "DevKitten" to "1080p60 stream is super smooth, zero dropped frames!",
                "VortexEcho" to "LMAO that timing was insane 🚀",
                "PixelNova" to "Sub hype!! Let's get the win!",
                "StreamEnthusiast" to "How low is the latency right now? Less than a second!",
                "FrostByte" to "GG in the chat everyone!! 🎮👏",
                "SpeedyCaster" to "The cloud transcoding ladder is handling mobile switching well"
            )
            val randomBadges = listOf(UserBadge.VIEWER, UserBadge.SUBSCRIBER, UserBadge.VIP)

            while (true) {
                delay(Random.nextLong(3500, 7000))
                val randomEntry = randomViewers.random()
                val newMsg = ChatMessage(
                    id = "msg_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
                    userName = randomEntry.first,
                    badge = randomBadges.random(),
                    message = randomEntry.second,
                    timestamp = "Now"
                )
                _uiState.update { state ->
                    state.copy(
                        chatMessages = (state.chatMessages + newMsg).takeLast(40)
                    )
                }
            }
        }
    }

    fun selectStream(stream: LiveStream) {
        _uiState.update { it.copy(currentStream = stream) }
    }

    fun setQuality(quality: StreamQuality) {
        _uiState.update { it.copy(selectedQuality = quality) }
    }

    fun sendMessage(text: String, userName: String = "You") {
        val newMsg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            userName = userName,
            badge = if (_uiState.value.isSubscribedToCurrent) UserBadge.SUBSCRIBER else UserBadge.VIEWER,
            message = text,
            timestamp = "Just now"
        )
        _uiState.update { it.copy(chatMessages = it.chatMessages + newMsg) }
    }

    fun sendReaction(emoji: String) {
        _uiState.update {
            it.copy(activeReactions = (it.activeReactions + emoji).takeLast(8))
        }
    }

    fun sendSuperChat(donation: SuperChatDonation) {
        val superChatMessage = ChatMessage(
            id = "msg_sc_${donation.id}",
            userName = donation.senderName,
            badge = UserBadge.SUBSCRIBER,
            message = donation.message,
            timestamp = "Just now",
            superChat = donation
        )
        _uiState.update { state ->
            state.copy(
                chatMessages = state.chatMessages + superChatMessage,
                pinnedSuperChats = (state.pinnedSuperChats + donation).takeLast(5),
                activeReactions = state.activeReactions + "🎉",
                creatorEarnings = state.creatorEarnings.copy(
                    totalRevenue = state.creatorEarnings.totalRevenue + donation.amount,
                    superChatsTotal = state.creatorEarnings.superChatsTotal + donation.amount
                )
            )
        }
    }

    fun dismissSuperChat(id: String) {
        _uiState.update { state ->
            state.copy(pinnedSuperChats = state.pinnedSuperChats.filterNot { it.id == id })
        }
    }

    fun toggleSubscription() {
        _uiState.update { state ->
            val newSub = !state.isSubscribedToCurrent
            val earningsBonus = if (newSub) 4.99 else 0.0
            state.copy(
                isSubscribedToCurrent = newSub,
                creatorEarnings = state.creatorEarnings.copy(
                    activeSubscribers = state.creatorEarnings.activeSubscribers + (if (newSub) 1 else -1),
                    totalRevenue = state.creatorEarnings.totalRevenue + earningsBonus,
                    subscriptionsTotal = state.creatorEarnings.subscriptionsTotal + earningsBonus
                )
            )
        }
    }

    fun updatePipelineConfig(newConfig: PipelineConfig) {
        val telemetry = PipelineEngine.computeTelemetry(newConfig)
        _uiState.update {
            it.copy(
                pipelineConfig = newConfig,
                pipelineTelemetry = telemetry
            )
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // Broadcast Studio Controls
    fun toggleBroadcast() {
        val currentlyBroadcasting = _uiState.value.broadcastStudioState.isBroadcasting
        if (currentlyBroadcasting) {
            broadcastTimerJob?.cancel()
            _uiState.update {
                it.copy(
                    broadcastStudioState = it.broadcastStudioState.copy(
                        isBroadcasting = false,
                        broadcastSeconds = 0,
                        activeViewers = 0
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    broadcastStudioState = it.broadcastStudioState.copy(
                        isBroadcasting = true,
                        activeViewers = 142
                    )
                )
            }
            broadcastTimerJob = viewModelScope.launch {
                while (true) {
                    delay(1000)
                    _uiState.update { state ->
                        val studio = state.broadcastStudioState
                        val jitterBitrate = (6120 + Random.nextInt(-180, 220)).coerceAtLeast(3000)
                        val jitterViewers = (studio.activeViewers + Random.nextInt(-2, 5)).coerceAtLeast(1)
                        state.copy(
                            broadcastStudioState = studio.copy(
                                broadcastSeconds = studio.broadcastSeconds + 1,
                                currentIngestBitrate = jitterBitrate,
                                activeViewers = jitterViewers
                            )
                        )
                    }
                }
            }
        }
    }

    fun toggleCameraFacing() {
        _uiState.update {
            it.copy(
                broadcastStudioState = it.broadcastStudioState.copy(
                    isFrontCamera = !it.broadcastStudioState.isFrontCamera
                )
            )
        }
    }

    fun toggleTorch() {
        _uiState.update {
            it.copy(
                broadcastStudioState = it.broadcastStudioState.copy(
                    isTorchOn = !it.broadcastStudioState.isTorchOn
                )
            )
        }
    }

    fun toggleMic() {
        _uiState.update {
            it.copy(
                broadcastStudioState = it.broadcastStudioState.copy(
                    isMicMuted = !it.broadcastStudioState.isMicMuted
                )
            )
        }
    }
}
