package com.example.navbottomtest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MediaReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        context?.let {
            // Ensure Exoplayer is initialized with context
            val exoPlayer = Exoplayer.getInstance(it)

            when (intent.action) {
                "ACTION_PLAY" -> {
                    // Ensure Exoplayer instance is not null before playing
                    exoPlayer?.play()
                }
                "ACTION_PAUSE" -> {
                    // Ensure Exoplayer instance is not null before pausing
                    exoPlayer?.pause()
                }
                else -> {Log.e("MediaReceiver", "Unhandled action: ${intent.action}")}
            }
        }
    }
}
