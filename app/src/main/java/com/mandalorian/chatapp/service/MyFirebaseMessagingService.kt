package com.mandalorian.chatapp.service

import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mandalorian.chatapp.data.model.Token
import com.mandalorian.chatapp.utils.Constants
import com.mandalorian.chatapp.utils.NotificationUtils
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(Constants.DEBUG, "Token: $token")

        Firebase.firestore.collection("tokens").add(Token(token))
            .addOnSuccessListener {
                Log.d(Constants.DEBUG, "Token saved successfully")
            }
            .addOnFailureListener {
                Log.d(Constants.DEBUG, "Failed to save token")
                it.printStackTrace()
            }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

//        val data: Map<String, String> = message.data
//        val jsonObject = JSONObject(data)
//        Log.d(Constants.DEBUG, jsonObject.toString())
//        data.values.forEach {
//            Log.d(Constants.DEBUG, "Notification $it")
//        }
        val title = message.data["title"].toString()
        val body = message.data["body"].toString()

        if (title == "Broadcast") {
            val intent = Intent()
            intent.action = "com.mandalorian.MyBroadcast"
            intent.putExtra("message", body)
            sendBroadcast(intent)
        }

        NotificationUtils.createNotification(
            this,
            title,
            body
        )
        Log.d(Constants.DEBUG, "Notification $title")
    }
}