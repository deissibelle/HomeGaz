package com.homegaz.app.domain.model

data class GasPoint(
    val id: String,
    val name: String,
    val brand: GasBrand,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val phone: String,
    val openingHours: String,
    val isOpen: Boolean,
    val availableWeights: List<GasWeight>,
    val rating: Float = 0f,
    val reviewsCount: Int = 0,
    val distance: Double? = null, // Distance depuis l'utilisateur en km
    val pricePerKg: Double
)

enum class GasBrand(val displayName: String, val color: Long) {
    TOTAL("Total", 0xFFFF6200),
    TRADEX("Tradex", 0xFF2196F3),
    DELTA("Delta", 0xFF4CAF50),
    SCTM("SCTM", 0xFFFFC107)
}

enum class GasWeight(val kg: Int, val displayName: String, val consignePrice: Int) {
    SMALL(6, "6kg", 5000),
    MEDIUM(12, "12.5kg", 7500),
    LARGE(25, "25kg", 10000),
    XLARGE(38, "38kg", 15000);
    
    companion object {
        fun fromKg(kg: Int): GasWeight? = values().find { it.kg == kg }
    }
}

data class GasPointFilter(
    val brands: Set<GasBrand> = emptySet(),
    val weights: Set<GasWeight> = emptySet(),
    val maxDistance: Int? = null, // en km
    val onlyOpen: Boolean = false
)
