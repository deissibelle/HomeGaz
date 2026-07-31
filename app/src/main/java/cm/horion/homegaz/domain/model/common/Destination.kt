package cm.horion.homegaz.domain.model.common


import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
sealed interface Destination : NavKey {

    @Serializable data object Onboarding : Destination
    @Serializable data object Home : Destination
    @Serializable data object LocationPermission : Destination

    @Serializable data class DistributorDetail(val pointId: String) : Destination

    @Serializable data object Payment : Destination
    @Serializable data object Confirmation : Destination
    @Serializable data object PaymentInitiated : Destination
    @Serializable data object PaymentSuccess : Destination
    @Serializable data object GazProfile : Destination
    @Serializable data object HelpCenter : Destination
    @Serializable data object PrivacySettings : Destination
    @Serializable data object Advices : Destination
}