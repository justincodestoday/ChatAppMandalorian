package com.mandalorian.chatapp.domain.repository

import com.mandalorian.chatapp.data.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface RealTimeRepository {

    // For group messages
//    suspend fun addMessage(msg: Message)

    suspend fun addMessage(uid1: String, uid2: String, msg: Message)

    fun getAllMessages(uid1: String, uid2: String): Flow<List<Message>>

    // For group messages
//    fun getAllMessages(): Flow<List<Message>>
}