package com.mandalorian.chatapp.data.repository

import com.google.firebase.firestore.CollectionReference
import com.mandalorian.chatapp.data.model.User
import com.mandalorian.chatapp.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(private val ref: CollectionReference): UserRepository {
   override suspend fun getUsers(): List<User> {
        val res = ref.get().await()
        val users = mutableListOf<User>()
        res.documents.forEach {
            it.toObject(User::class.java)?.let { user ->
                users.add(user)
            }
        }
        return users
    }

    override suspend fun getUser(uid: String): User? {
        val res = ref.document(uid).get().await()
        return res.toObject(User::class.java)
    }
}

// Model, View, ViewModel