package cm.horion.homegaz.domain.model.order.dto

enum class OrderState(state: String) {
    STARTING("STARTED"),
    LOADING("LOADING"),
    SENDING("SENDING"),
    SHIPPING("SHIPPING"),
    DELIVERED("DELIVERED"),
    ENDING("ENDED"),
    CANCELLED("CANCELLED")
}