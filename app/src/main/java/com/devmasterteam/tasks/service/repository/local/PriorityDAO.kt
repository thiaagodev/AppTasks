package com.devmasterteam.tasks.service.repository.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.devmasterteam.tasks.service.model.PriorityModel

@Dao
interface PriorityDAO {

    @Insert
    fun insert(list: List<PriorityModel>): Long

    @Query("SELECT * FROM Priority")
    fun list(): List<PriorityModel>

}