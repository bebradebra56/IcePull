package com.icepull.app.rgerpofk.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.icepull.app.IcePullActivity
import com.icepull.app.R
import com.icepull.app.rgerpofk.presentation.app.IcePullApplication

private const val ICE_PULL_CHANNEL_ID = "ice_pull_notifications"
private const val ICE_PULL_CHANNEL_NAME = "IcePull Notifications"
private const val ICE_PULL_NOT_TAG = "IcePull"

class IcePullPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                icePullShowNotification(it.title ?: ICE_PULL_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                icePullShowNotification(it.title ?: ICE_PULL_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            icePullHandleDataPayload(remoteMessage.data)
        }
    }

    private fun icePullShowNotification(title: String, message: String, data: String?) {
        val icePullNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ICE_PULL_CHANNEL_ID,
                ICE_PULL_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            icePullNotificationManager.createNotificationChannel(channel)
        }

        val icePullIntent = Intent(this, IcePullActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val icePullPendingIntent = PendingIntent.getActivity(
            this,
            0,
            icePullIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val icePullNotification = NotificationCompat.Builder(this, ICE_PULL_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ice_pull_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(icePullPendingIntent)
            .build()

        icePullNotificationManager.notify(System.currentTimeMillis().toInt(), icePullNotification)
    }

    private fun icePullHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(IcePullApplication.ICE_PULL_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}