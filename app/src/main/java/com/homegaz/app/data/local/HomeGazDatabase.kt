package com.homegaz.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.homegaz.app.data.local.dao.*
import com.homegaz.app.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        GasPointEntity::class,
        ReservationEntity::class,
        OrderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HomeGazDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gasPointDao(): GasPointDao
    abstract fun reservationDao(): ReservationDao
    abstract fun orderDao(): OrderDao
    
    companion object {
        const val DATABASE_NAME = "homegaz_db"
    }
}
