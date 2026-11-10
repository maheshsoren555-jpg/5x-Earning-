package com.example

import com.example.data.PipelineEngine
import com.example.data.StreamDataSource
import com.example.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testPipelineTelemetryLowLatency() {
    val webrtcConfig = PipelineConfig(
      ingestProtocol = IngestProtocol.WEBRTC_WHIP,
      playbackProtocol = PlaybackProtocol.WEBRTC_WHEP,
      chunkDurationSec = 0.5f,
      originShieldEnabled = true
    )
    val telemetry = PipelineEngine.computeTelemetry(webrtcConfig)
    assertTrue("WebRTC latency should be sub-second", telemetry.estimatedGlassToGlassLatencyMs < 1200)
    assertTrue("Origin shield should maintain high cache hit ratio", telemetry.cdnCacheHitRatioPct > 95f)
  }

  @Test
  fun testPipelineStandardHlsLatency() {
    val hlsConfig = PipelineConfig(
      ingestProtocol = IngestProtocol.RTMP,
      playbackProtocol = PlaybackProtocol.STANDARD_HLS,
      chunkDurationSec = 4.0f
    )
    val telemetry = PipelineEngine.computeTelemetry(hlsConfig)
    assertTrue("Standard HLS with 4s chunks should have higher latency", telemetry.estimatedGlassToGlassLatencyMs > 6000)
  }

  @Test
  fun testStreamDataSourcePopulated() {
    val streams = StreamDataSource.availableStreams
    assertTrue("Available streams should not be empty", streams.isNotEmpty())
    val superChats = StreamDataSource.initialSuperChats
    assertTrue("Super chats should be populated", superChats.isNotEmpty())
  }
}
