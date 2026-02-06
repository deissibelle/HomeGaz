package com.homegaz.app.domain.repository

import com.homegaz.app.domain.model.GasPoint
import com.homegaz.app.domain.model.GasPointFilter
import com.homegaz.app.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface GasPointRepository {
    
    // Récupération des points
    suspend fun getAllGasPoints(): Result<List<GasPoint>>
    suspend fun getGasPointById(id: String): Result<GasPoint>
    suspend fun getNearbyGasPoints(
        latitude: Double,
        longitude: Double,
        radiusKm: Int = 100
    ): Result<List<GasPoint>>
    
    // Filtrage
    suspend fun filterGasPoints(filter: GasPointFilter): Result<List<GasPoint>>
    
    // Recherche
    suspend fun searchGasPoints(query: String): Result<List<GasPoint>>
    
    // Cache local
    suspend fun saveGasPointsLocally(points: List<GasPoint>)
    suspend fun getLocalGasPoints(): List<GasPoint>
    fun observeLocalGasPoints(): Flow<List<GasPoint>>
    
    // Calcul de distance
    fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double
}
