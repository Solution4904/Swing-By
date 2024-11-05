package app.solution.swing_by

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import app.solution.swing_by.feature.AuthActivity

class NotificationManager(
    private val context: Context,
    private val channelId: String = "channel_id",
    private val channelName: String = "channel_name",
    private val channelDescription: String = "channel_description"
) {
    // TODO: https://thdbs523.tistory.com/373
    private var notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            description = channelDescription
        }

        notificationManager.createNotificationChannel(notificationChannel)
    }

    fun showNotification(title: String, description: String) {
        val intent = Intent(context, AuthActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .apply {
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setContentTitle(title)
                setContentText(description)
                setContentIntent(pendingIntent)
                setAutoCancel(true)
                setDefaults(NotificationCompat.DEFAULT_ALL)
            }

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}