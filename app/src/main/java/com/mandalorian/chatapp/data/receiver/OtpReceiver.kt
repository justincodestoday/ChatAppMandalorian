package com.mandalorian.chatapp.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class OtpReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val bundle = intent.extras
            val status = bundle?.get(SmsRetriever.EXTRA_STATUS) as Status

            when(status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val messageIntent = bundle.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    messageIntent?.let {
                        listener?.onSuccess(it)
                    }
                }

                CommonStatusCodes.TIMEOUT -> {
                    listener?.onFailure()
                }

                else -> {
                    listener?.onFailure()
                }
            }
        }
    }

    companion object {
        var listener: Listener? = null

        fun bind(listener: Listener) {
            this.listener = listener
        }

        interface Listener {
            fun onSuccess(messageIntent: Intent)
            fun onFailure()
        }
    }
}

// Work Manager and Alarm Manager