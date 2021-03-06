package com.rrdev.roombookingmvvm.ui.home

import androidx.lifecycle.ViewModel
import com.rrdev.roombookingmvvm.data.repositories.RoomRepository
import com.rrdev.roombookingmvvm.util.lazyDeferred

class HomeViewModel(
    private val repository: RoomRepository
) : ViewModel() {

    val room by lazyDeferred{
        repository.getRoom()
    }

    val user = repository.getUser()
}
