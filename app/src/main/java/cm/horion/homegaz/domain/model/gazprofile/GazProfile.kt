package cm.horion.homegaz.domain.model.gazprofile


data class GazProfile(
    val capacityKg  : String,
    val brand       : String,
    val usageLocation : String,
    val consumption : String,
    val photoUri : String? = null,
    val notes   : String? = null
)