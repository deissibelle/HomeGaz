package cm.horion.homegaz.domain.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val success: Boolean ,
    val message: String,
    val errors: List<String>? = null
)
