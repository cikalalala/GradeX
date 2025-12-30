package com.example.gradex

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    // Gunakan konstanta untuk menghindari warning parameter channelId
    companion object {
        const val CHANNEL_ID = "ALARM_TUGAS_CHANNEL"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val judulTugas = intent.getStringExtra("JUDUL_TUGAS") ?: "Tugas Baru"

        // 1. Munculkan Toast
        Toast.makeText(context, "Waktunya mengerjakan: $judulTugas", Toast.LENGTH_LONG).show()

        // 2. Putar Suara Alarm Standar
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()

        // 3. Buat Notification Channel
        createNotificationChannel(context)

        // 4. Intent untuk membuka HomeActivity
        val rootIntent = Intent(context, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, rootIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 5. Bangun Notifikasi
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            .setContentTitle("Pengingat Tugas!")
            .setContentText("Waktunya mengerjakan: $judulTugas")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(alarmUri)

        // 6. Tampilkan Notifikasi dengan Cek Izin Eksplisit (Menghilangkan Error Merah)
        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notify(System.currentTimeMillis().toInt(), builder.build())
                }
            } else {
                // Untuk Android di bawah 13, izin POST_NOTIFICATIONS tidak diperlukan secara runtime
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        // Karena minSdk >= 28, pengecekan SDK_INT >= O (26) tidak lagi diperlukan (Menghilangkan Warning Kuning)
        val name = "Alarm Tugas"
        val descriptionText = "Channel untuk pengingat tugas GradeX"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}