package com.oxy_creative.mybroadcastreceiver

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.oxy_creative.mybroadcastreceiver.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var downloadManagerBroadcastReceiver: BroadcastReceiver

    companion object {
        const val NOTIFICATION_ID = 101
        private const val SMS_REQUEST_CODE = 101
        const val ACTION_DOWNLOAD_STATUS = "download_status"
        const val NOTIFICATION_CHANNEL = "NOTIFICATION_CHANNEL"
        const val NOTIFICATION_DESCRIPTION = "NOTIFICATION_DESCRIPTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnDownload.setOnClickListener(this)
        binding.btnPermission.setOnClickListener(this)
        binding.btnNotification.setOnClickListener(this)
        binding.btnDetail.setOnClickListener(this)

        downloadManagerBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(DownloadService.TAG, "Download Selesai")
                Toast.makeText(context, "Download Selesai", Toast.LENGTH_SHORT).show()
            }
        }

        val downloadIntentFilter = IntentFilter(ACTION_DOWNLOAD_STATUS)
        registerReceiver(downloadManagerBroadcastReceiver, downloadIntentFilter)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_permission -> {
                check(this, Manifest.permission.RECEIVE_SMS, SMS_REQUEST_CODE)
            }
            R.id.btn_download -> {
                val downloadServiceIntent = Intent(this, DownloadService::class.java)
                startService(downloadServiceIntent)
            }
            R.id.btn_notification -> {
                showNotification()
            }
            R.id.btn_detail -> {
                val detailIntent = Intent(this@MainActivity, DetaiActivity::class.java)
                detailIntent.putExtra(DetaiActivity.EXTRA_TITLE, "Yoo!")
                detailIntent.putExtra(DetaiActivity.EXTRA_MESSAGE, "hi nama saya mahmud")
                startActivity(detailIntent)
            }
        }
    }
    private fun showNotification() {

        // val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
        // val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val intent = Intent(this, DetaiActivity::class.java)
        intent.putExtra(DetaiActivity.EXTRA_TITLE, "Yo!!!")
        intent.putExtra(DetaiActivity.EXTRA_MESSAGE, "Hi my name is mahmud")

        val pendingIntent = TaskStackBuilder.create(this).addParentStack(DetaiActivity::class.java)
            .addNextIntent(intent)
            .getPendingIntent(101, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBundler = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_circle_notifications_24))
            .setContentTitle("Application")
            .setContentText("My Notification")
            .setSubText("hello my name is mahmud")
            .setAutoCancel(true)

        val notification = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_DESCRIPTION, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = NOTIFICATION_DESCRIPTION
            mBundler.setChannelId(NOTIFICATION_CHANNEL)
            notification.createNotificationChannel(channel)
        }
        notification.notify(NOTIFICATION_ID, mBundler.build())
    }

    private fun check(activity: Activity, permission: String, requestCode: Int) {
        if(Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == SMS_REQUEST_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Sms receiver permission diterima", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "Sms receiver permission ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadManagerBroadcastReceiver)
    }
}