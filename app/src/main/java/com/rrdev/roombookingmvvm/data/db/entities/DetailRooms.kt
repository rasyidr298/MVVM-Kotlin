package com.rrdev.roombookingmvvm.data.db.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DetailRooms(
    val idRoom: Int,
    val namaRoom: String,
    val kapasitas: Int,
    val fasilitas1: String,
    val fasilitas2: String,
    val fasilitas3: String,
    val fasilitas4: String,
    val deskripsi: String
)