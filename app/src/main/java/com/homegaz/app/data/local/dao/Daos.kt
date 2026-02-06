package com.homegaz.app.data.local.dao

import androidx.room.*
import com.homegaz.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE id = :userId")
    fun observeUser(userId: String): Flow<UserEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

@Dao
interface GasPointDao {
    @Query("SELECT * FROM gas_points")
    suspend fun getAllGasPoints(): List<GasPointEntity>
    
    @Query("SELECT * FROM gas_points")
    fun observeAllGasPoints(): Flow<List<GasPointEntity>>
    
    @Query("SELECT * FROM gas_points WHERE id = :id")
    suspend fun getGasPointById(id: String): GasPointEntity?
    
    @Query("SELECT * FROM gas_points WHERE brand = :brand")
    suspend fun getGasPointsByBrand(brand: String): List<GasPointEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasPoints(points: List<GasPointEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGasPoint(point: GasPointEntity)
    
    @Query("DELETE FROM gas_points")
    suspend fun deleteAllGasPoints()
    
    @Query("DELETE FROM gas_points WHERE cachedAt < :timestamp")
    suspend fun deleteOldGasPoints(timestamp: Long)
}

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getUserReservations(userId: String): List<ReservationEntity>
    
    @Query("SELECT * FROM reservations WHERE id = :id")
    suspend fun getReservationById(id: String): ReservationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)
    
    @Update
    suspend fun updateReservation(reservation: ReservationEntity)
    
    @Delete
    suspend fun deleteReservation(reservation: ReservationEntity)
    
    @Query("DELETE FROM reservations WHERE status = 'DRAFT' AND createdAt < :timestamp")
    suspend fun deleteOldDrafts(timestamp: Long)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getUserOrders(userId: String): List<OrderEntity>
    
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeUserOrders(userId: String): Flow<List<OrderEntity>>
    
    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: String): OrderEntity?
    
    @Query("SELECT * FROM orders WHERE id = :id")
    fun observeOrder(id: String): Flow<OrderEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)
    
    @Update
    suspend fun updateOrder(order: OrderEntity)
    
    @Query("DELETE FROM orders")
    suspend fun deleteAllOrders()
}
