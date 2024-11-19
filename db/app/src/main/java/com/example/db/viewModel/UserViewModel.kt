package com.example.db.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.dao.UserDao
import com.example.db.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import androidx.lifecycle.ViewModelProvider

import kotlinx.coroutines.launch

class UserViewModel(private val userDao: UserDao) : ViewModel() {
    val currentUser: Flow<UserEntity?> = userDao.getUser()

    fun insertUser(user: UserEntity) {
        viewModelScope.launch {
            userDao.insertUser(user)
        }
    }
}

class UserViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}