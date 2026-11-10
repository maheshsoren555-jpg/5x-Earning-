package com.example.data

import com.example.R
import com.example.model.*

object StreamDataSource {

    val availableStreams = listOf(
        LiveStream(
            id = "stream_1",
            title = "🔥 GRAND FINALS: Cyber Strike Nexus World Championship 2026",
            streamerName = "NexusVanguard",
            streamerHandle = "@nexusvanguard",
            category = "Esports",
            viewersCount = 38450,
            isLive = true,
            uptimeFormatted = "02:14:30",
            description = "Welcome to the Grand Finals of Cyber Strike Nexus! Match point round in progress. Casted by Alex & Sarah with 4K 60FPS LL-HLS stream.",
            tags = listOf("Esports", "CyberStrike", "Finals", "Competitive", "Drops"),
            currentBitrateKbps = 6200,
            currentResolution = "1080p60",
            latencyMs = 1250,
            cdnEdgeNode = "sjc-edge-02 (San Jose)",
            previewDrawableRes = R.drawable.img_live_gaming_thumb_1794288464759
        ),
        LiveStream(
            id = "stream_2",
            title = "⚡ Live Coding a High-Performance Low-Latency Video Pipeline in Rust & Kotlin",
            streamerName = "DevStreamLab",
            streamerHandle = "@devstreamlab",
            category = "Software & Tech",
            viewersCount = 4210,
            isLive = true,
            uptimeFormatted = "01:05:12",
            description = "Building a custom WHIP/WHEP WebRTC ingest bridge with hardware NVENC transcoding ladder. Answering video architecture questions in chat!",
            tags = listOf("Rust", "Kotlin", "Streaming", "WebRTC", "Architecture"),
            currentBitrateKbps = 5400,
            currentResolution = "1080p60",
            latencyMs = 380,
            cdnEdgeNode = "iad-edge-09 (Virginia)",
            previewDrawableRes = R.drawable.img_pipeline_hero_1794288481817
        ),
        LiveStream(
            id = "stream_3",
            title = "🎧 Synthwave & Chill Beats: Interactive Live Modular Synthesizer Jam",
            streamerName = "AuraSonic",
            streamerHandle = "@aurasonic",
            category = "Music & Creative",
            viewersCount = 2890,
            isLive = true,
            uptimeFormatted = "03:42:00",
            description = "Live analog synth patch sessions. High-fidelity 320kbps Opus audio stream. Request chord progressions in chat!",
            tags = listOf("Synthwave", "Music", "LiveJam", "Chill", "OpusAudio"),
            currentBitrateKbps = 4800,
            currentResolution = "1080p60",
            latencyMs = 1800,
            cdnEdgeNode = "fra-edge-01 (Frankfurt)",
            previewDrawableRes = null
        ),
        LiveStream(
            id = "stream_4",
            title = "🗼 Night Walk through Cyberpunk Shinjuku & Akihabara Street Food Tour",
            streamerName = "TokyoWanderer",
            streamerHandle = "@tokyowanderer",
            category = "IRL Travel",
            viewersCount = 8920,
            isLive = true,
            uptimeFormatted = "00:48:19",
            description = "Live mobile backpack bonded cellular SRT stream exploring rainy neon streets, ramen bars, and retro arcade halls.",
            tags = listOf("IRL", "Tokyo", "Travel", "StreetFood", "BondedCellular"),
            currentBitrateKbps = 5100,
            currentResolution = "1080p60",
            latencyMs = 1950,
            cdnEdgeNode = "nrt-edge-03 (Tokyo)",
            previewDrawableRes = null
        )
    )

    val initialSuperChats = listOf(
        SuperChatDonation(
            id = "sc_1",
            senderName = "QuantumPilot",
            amount = 50.00,
            message = "Unbelievable flick shot in round 14!! You guys are taking home the trophy tonight!! 🏆🔥",
            colorHex = 0xFFFB8C00, // Orange
            timestamp = "2m ago",
            durationSeconds = 300
        ),
        SuperChatDonation(
            id = "sc_2",
            senderName = "Elena_Tech",
            amount = 20.00,
            message = "Big fan of this stream! Audio mixing and video quality is crisp! 🚀💜",
            colorHex = 0xFF00ACC1, // Cyan
            timestamp = "5m ago",
            durationSeconds = 180
        ),
        SuperChatDonation(
            id = "sc_3",
            senderName = "PixelMaster99",
            amount = 100.00,
            message = "TEAM NEXUS LET'S GOOO! Celebrating 1 year sub anniversary! 🎉✨",
            colorHex = 0xFFE53935, // Red
            timestamp = "8m ago",
            durationSeconds = 600
        )
    )

    val initialChatMessages = listOf(
        ChatMessage(
            id = "msg_1",
            userName = "Mod_Valkyrie",
            badge = UserBadge.MODERATOR,
            message = "Welcome everyone to the Grand Finals! Keep chat respectful and spam-free! 🛡️",
            timestamp = "12:00"
        ),
        ChatMessage(
            id = "msg_2",
            userName = "CyberWolf_99",
            badge = UserBadge.SUBSCRIBER,
            message = "LETS GOOOOO! That crosshair placement was surgical!",
            timestamp = "12:01"
        ),
        ChatMessage(
            id = "msg_3",
            userName = "NeonSpecter",
            badge = UserBadge.VIP,
            message = "The 1080p60 low latency stream is buttery smooth, zero buffering on my 5G",
            timestamp = "12:01"
        ),
        ChatMessage(
            id = "msg_4",
            userName = "QuantumPilot",
            badge = UserBadge.SUBSCRIBER,
            message = "Unbelievable flick shot in round 14!! You guys are taking home the trophy tonight!! 🏆🔥",
            timestamp = "12:02",
            superChat = initialSuperChats[0]
        ),
        ChatMessage(
            id = "msg_5",
            userName = "CodeRunner",
            badge = UserBadge.VIEWER,
            message = "What bitrate are they running? Video looks super crisp even during high-motion team fights",
            timestamp = "12:02"
        ),
        ChatMessage(
            id = "msg_6",
            userName = "NexusVanguard",
            badge = UserBadge.BROADCASTER,
            message = "Thanks everyone for tuning in! We're running SRT 6.2Mbps to Cloudflare edge nodes! 🚀",
            timestamp = "12:03"
        ),
        ChatMessage(
            id = "msg_7",
            userName = "Elena_Tech",
            badge = UserBadge.SUBSCRIBER,
            message = "Big fan of this stream! Audio mixing and video quality is crisp! 🚀💜",
            timestamp = "12:04",
            superChat = initialSuperChats[1]
        )
    )

    val qualityProfiles = listOf(
        StreamQuality("Auto (ABR)", "1080p60", 6200, 60),
        StreamQuality("1080p60 HD", "1920x1080", 6000, 60),
        StreamQuality("720p60 HD", "1280x720", 3500, 60),
        StreamQuality("480p", "854x480", 1500, 30),
        StreamQuality("360p (Data Saver)", "640x360", 800, 30)
    )
}
