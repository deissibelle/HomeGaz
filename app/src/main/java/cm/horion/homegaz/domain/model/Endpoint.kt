package cm.horion.homegaz.domain.model

sealed class Endpoint( val path : String) {

    data object SaveProfile : Endpoint(path = "/profil")
    data object UpdateProfile : Endpoint(path = "/profil")
    data object DeleteProfile : Endpoint(path = "/profil")
    data object GetProfile : Endpoint(path = "/profil")
    data object GetDepotGaz : Endpoint(path = "/search/gaz")

    data object GetGaz : Endpoint(path = "/stock/gaz")


}