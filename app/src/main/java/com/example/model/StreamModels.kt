package com.example.model

enum class UserBadge {
    BROADCASTER,
    MODERATOR,
    VIP,
    SUBSCRIBER,
    VIEWER
}

data class SuperChatDonation(
    val id: String,
    val senderName: String,
    val senderAvatar: String = "",
    val amount: Double,
    val message: String,
    val colorHex: Long,
    val timestamp: String,
    val durationSeconds: Int = 120
)

data class ChatMessage(
    val id: String,
    val userName: String,
    val userAvatar: String = "",
    val badge: UserBadge = UserBadge.VIEWER,
    val message: String,
    val timestamp: String,
    val superChat: SuperChatDonation? = null,
    val reactionEmoji: String? = null
)

data class StreamQuality(
    val label: String,
    val resolution: String,
    val bitrateKbps: Int,
    val fps: Int
)

data class LiveStream(
    val id: String,
    val title: String,
    val streamerName: String,
    val streamerHandle: String,
    val streamerAvatar: String = "",
    val category: String,
    val viewersCount: Int,
    val isLive: Boolean = true,
    val uptimeFormatted: String = "01:42:15",
    val description: String,
    val tags: List<String>,
    val currentBitrateKbps: Int = 5850,
    val currentResolution: String = "1080p60",
    val latencyMs: Int = 1450,
    val cdnEdgeNode: String = "iad-edge-04 (Ashburn)",
    val previewDrawableRes: Int? = null
)

enum class IngestProtocol(val label: String, val typicalLatency: String) {
    RTMP("RTMP", "2.0s - 4.0s"),
    RTMPS("RTMPS (Secure)", "2.0s - 4.0s"),
    SRT("SRT (Caller)", "1.0s - 2.0s"),
    WEBRTC_WHIP("WebRTC WHIP", "< 500ms")
}

enum class VideoCodec(val label: String, val efficiency: String) {
    H264("H.264 / AVC", "Standard Compatibility"),
    H265("H.265 / HEVC", "+35% Bandwidth Savings"),
    AV1("AV1 (Next-Gen)", "+50% Bandwidth Savings")
}

enum class TranscodingEngine(val label: String, val type: String) {
    GPU_NVENC("Cloud GPU (NVENC)", "Hardware Accelerated"),
    INTEL_QSV("Intel QuickSync", "Hardware Dedicated"),
    SOFTWARE_X264("FFmpeg CPU (x264)", "High Quality CPU")
}

enum class CdnProvider(val label: String, val globalPops: Int) {
    CLOUDFLARE("Cloudflare Stream", 310),
    FASTLY("Fastly Media Shield", 185),
    AKAMAI("Akamai Edge", 420),
    CLOUDFRONT("AWS CloudFront", 450)
}

enum class PlaybackProtocol(val label: String, val latencyCategory: String) {
    LL_HLS("Low-Latency HLS (CMAF)", "1.2s - 2.5s"),
    WEBRTC_WHEP("WebRTC WHEP", "200ms - 600ms"),
    STANDARD_HLS("Standard HLS", "6.0s - 12.0s")
}

data class PipelineConfig(
    val ingestProtocol: IngestProtocol = IngestProtocol.SRT,
    val videoCodec: VideoCodec = VideoCodec.H264,
    val targetIngestBitrateKbps: Int = 6000,
    val keyframeIntervalSec: Float = 1.0f,
    val transcodingEngine: TranscodingEngine = TranscodingEngine.GPU_NVENC,
    val enableAbrLadder: Boolean = true,
    val chunkDurationSec: Float = 1.0f,
    val cdnProvider: CdnProvider = CdnProvider.CLOUDFLARE,
    val edgeCacheTtlSec: Int = 4,
    val originShieldEnabled: Boolean = true,
    val playbackProtocol: PlaybackProtocol = PlaybackProtocol.LL_HLS
)

data class PipelineTelemetry(
    val estimatedGlassToGlassLatencyMs: Int,
    val cdnCacheHitRatioPct: Float,
    val ingressBandwidthMbps: Float,
    val egressBandwidthGbps: Float,
    val edgePoPResponseMs: Int,
    val droppedFramesRate: Float,
    val bufferHealthSec: Float,
    val isOptimal: Boolean
)

data class CreatorEarnings(
    val totalRevenue: Double = 3450.75,
    val superChatsTotal: Double = 1890.50,
    val subscriptionsTotal: Double = 1240.25,
    val bitsGemsTotal: Double = 320.00,
    val activeSubscribers: Int = 384,
    val peakViewers: Int = 14200,
    val totalHoursStreamed: Float = 84.5f
)
