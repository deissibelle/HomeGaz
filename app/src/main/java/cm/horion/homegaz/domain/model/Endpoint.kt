package cm.horion.homegaz.domain.model

sealed class Endpoint( val path : String) {

    data object SaveProfile : Endpoint(path = "/profil")
    data object UpdateProfile : Endpoint(path = "/profil")
    data object DeleteProfile : Endpoint(path = "/profil")
    data object GetProfile : Endpoint(path = "/profil")
    data object GetDepotGaz : Endpoint(path = "/search/gaz")
    data object Status: Endpoint(path = "/status")

    data object GetGaz : Endpoint(path = "/stock/gaz")

    //route order
    data object Order : Endpoint(path = "/order")
    data object UpdateOrder : Endpoint(path = "/order/status")
    data object GetOrder : Endpoint(path = "/order")

    //route payment
    data object Payment: Endpoint(path = "/payment")
}