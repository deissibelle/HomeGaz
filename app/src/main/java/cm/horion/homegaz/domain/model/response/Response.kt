package cm.horion.homegaz.domain.model.response

import cm.horion.homegaz.domain.model.consommateur.dto.Profile
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val success: Boolean,
    val message: String,
    val profile: Profile? = null,
    val errors: List<String>? = null
)
