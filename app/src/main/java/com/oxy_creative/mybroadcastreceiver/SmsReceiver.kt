package com.oxy_creative.mybroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import java.lang.Exception

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val bundle = intent.extras as Bundle
        try {
            val pdus = bundle.get("pdus") as Array<*>

            for(item in pdus) {
                val currentMessage: SmsMessage = getIncomingMessage(item as Any, bundle)
                val senderNum = currentMessage.displayOriginatingAddress
                val messageBody = currentMessage.messageBody

                val showIntent = Intent(context, SmsReceiverActivity::class.java)

                showIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                showIntent.putExtra(SmsReceiverActivity.EXTRA_SMS_NO, senderNum)
                showIntent.putExtra(SmsReceiverActivity.EXTRA_SMS_MESSAGE, messageBody)
                context.startActivity(showIntent)

            }
        } catch (e: Exception) {

        }
    }

    private fun getIncomingMessage(aObject: Any, bundle: Bundle): SmsMessage {
        val currentMessage: SmsMessage
        val format = bundle.getString("format")
        currentMessage = if (Build.VERSION.SDK_INT >= 23) {
            SmsMessage.createFromPdu(aObject as ByteArray, format)
        } else SmsMessage.createFromPdu(aObject as ByteArray)
        return currentMessage
    }
}