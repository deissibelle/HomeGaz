package cm.horion.homegaz.presentation.state


data class GazProfileUiState(
    val capacityKg  : String  = "",
    val brand  : String  = "",
    val usageLocation: String  = "",
    val region        : String  = "",
    val ville         : String  = "",
    val quartier      : String  = "",
    val lieuDit       : String  = "",
    val latitude  : Double? = null,
    val longitude : Double? = null,
    val isSaving        : Boolean = false,
    val isSaved         : Boolean = false,
    val errorMessage    : String? = null
) {
    val isFormValid: Boolean
        get() = capacityKg.isNotBlank()
                && brand.isNotBlank()
                && usageLocation.isNotBlank()
}