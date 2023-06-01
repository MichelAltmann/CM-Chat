package com.cmchat.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.cmchat.cmchat.R
import com.cmchat.model.Message
import okhttp3.internal.notify

object MessageNotification {
    private const val CHANNEL_ID = "messageChannel"
    private const val NOTIFICATION_ID = 13
    fun createNotificationChannel(context: Context) {
        val name = "You have new messages."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, name, importance)

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val requestCode = 123 // Replace with your own request code
                val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        .putExtra(Settings.EXTRA_CHANNEL_ID, CHANNEL_ID)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
                val pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle("Enable Notifications")
                    .setContentText("Please enable notifications for this app.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                val notification = builder.build()
                notificationManager.notify(NOTIFICATION_ID, notification)
                return
            }

        }
    }

    fun sendNotification(context: Context, message: Message) {
        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.drawable.ic_user)
                .setContentTitle("You have new messages.").setContentText(message.text)
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(NOTIFICATION_ID, builder.build())
            }

        }
    }

}