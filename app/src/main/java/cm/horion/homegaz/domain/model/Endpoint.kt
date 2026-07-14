package cm.horion.homegaz.domain.model

sealed class Endpoint( val path : String) {

    data object RefreshToken: Endpoint(path = "/refresh")
    data object Token: Endpoint(path = "/token")
    data object Exchange : Endpoint(path = "/token/exchange")

    data object SaveProfile : Endpoint(path = "/profil")
    data object UpdateProfile : Endpoint(path = "/profil")
    data object DeleteProfile : Endpoint(path = "/profil")
    data object GetProfile : Endpoint(path = "/profil")
    data object GetDepotGaz : Endpoint(path = "/orders")
    data object Status: Endpoint(path = "/status")

    data object GetGaz : Endpoint(path = "/stock/gaz")

    //route order
    data object Order : Endpoint(path = "/order")
    data object GetOrder : Endpoint(path = "/order")
    data object GetListOrder : Endpoint(path = "/orders")


    //route payment
    data object Payment: Endpoint(path = "/payment")
}