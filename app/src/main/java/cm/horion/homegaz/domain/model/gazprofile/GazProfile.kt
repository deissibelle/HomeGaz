package cm.horion.homegaz.domain.model.gazprofile


data class GazProfile(
    val capacityKg  : String,
    val brand       : String,
    val usageLocation : String,
    val region        : String = "",
    val ville         : String = "",
    val quartier      : String = "",
    val lieuDit       : String = "",

)