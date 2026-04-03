package cm.horion.homegaz.presentation.state


data class GazProfileUiState(
    val capacityKg  : String  = "",
    val brand  : String  = "",
    val usageLocation: String  = "",
    val consumption: String  = "",
    val photoUri: String? = null,
    val notes: String  = "",

    val isSaving        : Boolean = false,
    val isSaved         : Boolean = false,
    val errorMessage    : String? = null
) {
    val isFormValid: Boolean
        get() = capacityKg.isNotBlank()
                && brand.isNotBlank()
                && usageLocation.isNotBlank()
                && consumption.isNotBlank()
}