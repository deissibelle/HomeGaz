package cm.horion.homegaz.domain.model.gazprofile

/**
 * Régions et villes du Cameroun (focus Yaoundé et principales villes)
 */
object CameroonData {

    val REGIONS = listOf(
        "Adamaoua",
        "Centre",
        "Est",
        "Extrême-Nord",
        "Littoral",
        "Nord",
        "Nord-Ouest",
        "Ouest",
        "Sud",
        "Sud-Ouest"
    )

    val CITIES_BY_REGION = mapOf(
        "Adamaoua"     to listOf("Ngaoundéré", "Meiganga", "Tibati", "Tignère", "Banyo"),
        "Centre"       to listOf(
            "Yaoundé", "Mbalmayo", "Obala", "Bafia", "Nanga Eboko",
            "Monatélé", "Esse", "Soa", "Mfou", "Akonolinga"
        ),
        "Est"          to listOf("Bertoua", "Abong-Mbang", "Batouri", "Yokadouma", "Lomié"),
        "Extrême-Nord" to listOf("Maroua", "Kousseri", "Mora", "Yagoua", "Kaélé"),
        "Littoral"     to listOf("Douala", "Edéa", "Nkongsamba", "Loum", "Mbanga"),
        "Nord"         to listOf("Garoua", "Guider", "Pitoa", "Lagdo", "Rey Bouba"),
        "Nord-Ouest"   to listOf("Bamenda", "Kumbo", "Wum", "Nkambe", "Fundong"),
        "Ouest"        to listOf("Bafoussam", "Dschang", "Foumban", "Mbouda", "Bangangté"),
        "Sud"          to listOf("Ebolowa", "Sangmélima", "Kribi", "Ambam", "Lolodorf"),
        "Sud-Ouest"    to listOf("Buea", "Limbe", "Kumba", "Mamfe", "Mundemba")
    )

    /** Quartiers de Yaoundé */
    val YAOUNDE_QUARTERS = listOf(
        "Bastos", "Nlongkak", "Mvog-Ada", "Messa", "Omnisports",
        "Tsinga", "Essos", "Elig-Essono", "Briqueterie", "Mokolo",
        "Ngousso", "Mendong", "Nkolbisson", "Biyem-Assi", "Melen",
        "Mvog-Betsi", "Ekounou", "Kondengui", "Nkol-Eton", "Santa Barbara",
        "Obili", "Jouvence", "Nsimeyong", "Mimboman", "Nkoldongo",
        "Emana", "Djoungolo", "Nkolmesseng", "Ahala", "Nkomo",
        "Autre"
    )

    fun getCitiesForRegion(region: String): List<String> =
        CITIES_BY_REGION[region] ?: emptyList()
}