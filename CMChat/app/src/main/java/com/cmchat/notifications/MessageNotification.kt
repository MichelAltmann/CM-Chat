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
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.NotificationTarget
import com.cmchat.cmchat.R
import com.cmchat.model.Message
import com.cmchat.util.ImageHandler


object MessageNotification {
    private const val CHANNEL_ID = "messageChannel"
    private const val NOTIFICATION_ID = 13
    lateinit var messageClick: (Int) -> Unit
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
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
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


        val contentView = RemoteViews(context.packageName, R.layout.notification_message)
        contentView.setTextViewText(R.id.notification_message_sender, message.senderName)
        contentView.setTextViewText(R.id.notification_message_text, message.text)
        contentView.setImageViewResource(R.id.notification_message_image, R.drawable.ic_user)


        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setCustomContentView(contentView)

        builder.setContent(contentView)

        val notificationTarget = NotificationTarget(
            context,
            R.id.notification_message_image,
            contentView,
            builder.build(),
            NOTIFICATION_ID
        )


        Glide.with(context)
            .asBitmap()
            .load(ImageHandler.IMAGE_GETTER_URL + message.senderImage)
            .into(notificationTarget)



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