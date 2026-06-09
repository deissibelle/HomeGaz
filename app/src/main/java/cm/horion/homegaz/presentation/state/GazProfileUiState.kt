package cm.horion.homegaz.presentation.state

import cm.horion.homegaz.domain.model.consommateur.dto.GazBottle


data class GazProfileUiState(
    val capacityKg    : String  = "",
    val brand         : String  = "",
    val usageLocation : String  = "",
    val region        : String  = "",
    val ville         : String  = "",
    val quartier      : String  = "",
    val lieuDit       : String  = "",

    val latitude      : Double? = null,
    val longitude     : Double? = null,

    val battleUuid: String = "",
    val availableBottles: List<GazBottle> = emptyList(),

    val isLoading     : Boolean = false,
    val isUpdateOption     : Boolean = false,
    val isSavedProfilSuccess : Boolean = false,
    val isSaving      : Boolean = false,
    val isSaved       : Boolean = false,
    val errorMessage  : String? = null
) {
    val isFormValid: Boolean
        get() = capacityKg.isNotBlank()
                && brand.isNotBlank()
                && usageLocation.isNotBlank()
}