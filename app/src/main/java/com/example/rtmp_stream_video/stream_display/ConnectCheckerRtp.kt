package com.example.rtmp_stream_video.stream_display

import com.pedro.rtsp.utils.ConnectCheckerRtsp
import net.ossrs.rtmp.ConnectCheckerRtmp

interface ConnectCheckerRtp: ConnectCheckerRtmp{

    /**
     * Commons
     */
    fun onConnectionSuccessRtp()

    fun onConnectionFailedRtp(reason: String)

    fun onNewBitrateRtp(bitrate: Long)

    fun onDisconnectRtp()

    fun onAuthErrorRtp()

    fun onAuthSuccessRtp()

    /**
     * RTMP
     */
    override fun onConnectionSuccessRtmp() {
        onConnectionSuccessRtp()
    }

    override fun onConnectionFailedRtmp(reason: String) {
        onConnectionFailedRtp(reason)
    }

    override fun onNewBitrateRtmp(bitrate: Long) {
        onNewBitrateRtp(bitrate)
    }

    override fun onDisconnectRtmp() {
        onDisconnectRtp()
    }

    override fun onAuthErrorRtmp() {
        onAuthErrorRtp()
    }

    override fun onAuthSuccessRtmp() {
        onAuthSuccessRtp()
    }
}