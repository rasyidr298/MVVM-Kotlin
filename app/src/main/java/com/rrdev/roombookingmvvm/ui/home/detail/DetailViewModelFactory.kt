package com.rrdev.roombookingmvvm.ui.home.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rrdev.roombookingmvvm.data.repositories.RoomRepository
import com.rrdev.roombookingmvvm.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class DetailViewModelFactory(
    private val namaRoom: String,
    private val repository: RoomRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailViewModel(namaRoom,repository) as T
    }
}