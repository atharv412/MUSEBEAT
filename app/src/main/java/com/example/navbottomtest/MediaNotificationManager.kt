package com.example.navbottomtest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.bumptech.glide.Glide

class MediaNotificationManager(private val context: Context, private val mediaSession: MediaSessionCompat) {

    private val notificationId = 1
    private val channelId = "media_channel"

    init {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Media Playback", NotificationManager.IMPORTANCE_LOW)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("NotificationPermission")
    fun createNotification(songTitle: String, artistName: String, imageUrl: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val largeIcon = getBitmapFromUrl(imageUrl)
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(songTitle)
            .setContentText(artistName)
            .setSmallIcon(R.drawable.welcome2_removebg)
            .setLargeIcon(largeIcon)
            .setStyle(MediaStyle().setMediaSession(mediaSession.sessionToken))
            .addAction(R.drawable.round_pause_24, "Previous", getPendingIntent("ACTION_PAUSE"))
            .addAction(R.drawable.round_play_arrow_24, "Play", getPendingIntent("ACTION_PLAY"))
//
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

//        NotificationManagerCompat.from(context).notify(notificationId,notification)
        notificationManager.notify(notificationId, notification)
    }


    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()  // Load image synchronously
        } catch (e: Exception) {
            null
        }
    }

    private fun getPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, SongPlayer::class.java).setAction(action)
        return PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}