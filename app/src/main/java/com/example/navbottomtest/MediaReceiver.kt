package com.example.navbottomtest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MediaReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        when(intent.action){
            "ACTION_PLAY"->Exoplayer.getInstance()?.play()
            "ACTION_PAUSE"->Exoplayer.getInstance()?.pause()
        }
    }
}