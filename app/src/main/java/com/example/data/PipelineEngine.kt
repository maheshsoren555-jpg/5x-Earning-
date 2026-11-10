package com.example.data

import com.example.model.*

object PipelineEngine {

    fun computeTelemetry(config: PipelineConfig, estimatedViewers: Int = 12500): PipelineTelemetry {
        // Base latency components
        val ingestBaseMs = when (config.ingestProtocol) {
            IngestProtocol.WEBRTC_WHIP -> 220
            IngestProtocol.SRT -> 650
            IngestProtocol.RTMP -> 1600
            IngestProtocol.RTMPS -> 1850
        }

        val transcoderMs = when (config.transcodingEngine) {
            TranscodingEngine.GPU_NVENC -> 90
            TranscodingEngine.INTEL_QSV -> 130
            TranscodingEngine.SOFTWARE_X264 -> 320
        }

        // Chunk duration influence (LL-HLS uses chunk/part segments)
        val chunkDelayMs = (config.chunkDurationSec * 750).toInt()

        val playbackBaseMs = when (config.playbackProtocol) {
            PlaybackProtocol.WEBRTC_WHEP -> 150
            PlaybackProtocol.LL_HLS -> 850 + (config.chunkDurationSec * 400).toInt()
            PlaybackProtocol.STANDARD_HLS -> 4500 + (config.chunkDurationSec * 1500).toInt()
        }

        val networkCdnMs = if (config.originShieldEnabled) 65 else 115

        val totalLatencyMs = ingestBaseMs + transcoderMs + chunkDelayMs + playbackBaseMs + networkCdnMs

        // Cache hit ratio depends on origin shield and chunk duration
        val baseCacheHit = when (config.cdnProvider) {
            CdnProvider.CLOUDFLARE -> 98.6f
            CdnProvider.AKAMAI -> 98.9f
            CdnProvider.FASTLY -> 98.2f
            CdnProvider.CLOUDFRONT -> 97.8f
        }
        val cacheHitRatio = if (config.originShieldEnabled) baseCacheHit else (baseCacheHit - 8.5f)

        // Bandwidth
        val ingressMbps = (config.targetIngestBitrateKbps.toFloat() / 1000f) * 1.05f // headers overhead
        // ABR ladder distribution: 40% on top 1080p, 35% on 720p, 18% on 480p, 7% on 360p
        val avgViewerBitrateMbps = if (config.enableAbrLadder) {
            (ingressMbps * 0.40f) + (3.5f * 0.35f) + (1.5f * 0.18f) + (0.8f * 0.07f)
        } else {
            ingressMbps
        }
        val egressGbps = (avgViewerBitrateMbps * estimatedViewers) / 1000f

        val edgeLatency = when (config.cdnProvider) {
            CdnProvider.FASTLY -> 18
            CdnProvider.CLOUDFLARE -> 22
            CdnProvider.AKAMAI -> 25
            CdnProvider.CLOUDFRONT -> 28
        }

        val droppedFrames = if (config.targetIngestBitrateKbps > 8000 && config.ingestProtocol == IngestProtocol.RTMP) {
            0.85f
        } else {
            0.02f
        }

        val bufferHealthSec = if (config.playbackProtocol == PlaybackProtocol.STANDARD_HLS) {
            12.5f
        } else if (config.playbackProtocol == PlaybackProtocol.LL_HLS) {
            3.2f
        } else {
            0.6f
        }

        val isOptimal = totalLatencyMs < 3000 && cacheHitRatio > 95f

        return PipelineTelemetry(
            estimatedGlassToGlassLatencyMs = totalLatencyMs,
            cdnCacheHitRatioPct = cacheHitRatio,
            ingressBandwidthMbps = ingressMbps,
            egressBandwidthGbps = egressGbps,
            edgePoPResponseMs = edgeLatency,
            droppedFramesRate = droppedFrames,
            bufferHealthSec = bufferHealthSec,
            isOptimal = isOptimal
        )
    }

    fun getArchitectureExplanation(config: PipelineConfig): List<Pair<String, String>> {
        return listOf(
            "1. Live Ingest Stage" to "Streamer captures video via ${config.ingestProtocol.label} encoding in ${config.videoCodec.label} at ${config.targetIngestBitrateKbps} kbps with keyframe interval of ${config.keyframeIntervalSec}s (GOP size). Low GOP is required for fast chunk generation.",
            "2. Cloud Transcoding Ladder" to "Hardware engine (${config.transcodingEngine.label}) ingests master stream and instantly generates ABR profiles (1080p, 720p, 480p, 360p) split into ${config.chunkDurationSec}s CMAF fragments for seamless client bitrate switching.",
            "3. Global CDN Delivery" to "Edge distribution powered by ${config.cdnProvider.label} (${config.cdnProvider.globalPops}+ PoPs). Origin Shield is ${if (config.originShieldEnabled) "ENABLED (consolidates edge pulls to reduce origin load)" else "DISABLED"}. Edge cache TTL is ${config.edgeCacheTtlSec}s.",
            "4. Player Playback UI" to "Clients receive manifest via ${config.playbackProtocol.label} with estimated end-to-end glass latency of ~${computeTelemetry(config).estimatedGlassToGlassLatencyMs / 1000f}s. Supports adaptive bitrate switching and zero stall recovery."
        )
    }
}
