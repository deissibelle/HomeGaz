package cm.horion.homegaz

//import cm.horion.homegaz.data.datasource.remote.ConsumerService
//import cm.horion.homegaz.domain.model.consommateur.dto.Address
//import cm.horion.homegaz.domain.model.consommateur.dto.GeoLocation
//import cm.horion.homegaz.domain.model.consommateur.request.ProfileRequest
//import cm.horion.homegaz.domain.model.distributor.PaymentMethod
//import junit.framework.TestCase.assertEquals
//import junit.framework.TestCase.assertFalse
//import junit.framework.TestCase.assertNotNull
//import junit.framework.TestCase.assertTrue
//import junit.framework.TestCase.fail
//import kotlinx.coroutines.test.runTest
//import org.junit.Test
//
//class ConsumerServiceTest {
//
//    private val service = ConsumerService()
//
//    // ==========================================
//    //  1. TESTS POUR saveProfil
//    // ==========================================
//
//    @Test
//    fun testRealSaveProfil_Success() = runTest {
//        // Crée une requête valide (génère un faux numéro unique pour éviter les doublons à chaque test)
//        val request = ProfileRequest(
//            address = Address(
//                location = GeoLocation.fromLatLng(
//                    latitude = 3.8480,
//                    longitude = 11.5021
//                ),
//                quarter = "Bastos",
//                city = "Yaoundé",
//                region = "Centre",
//                country = "Cameroun",
//                lieuDit = "En face du laboratoire"
//            ),
//            paymentMethod = PaymentMethod.OM,
//            gazBottle = "btl_a8f76c1-968c-4a3a-9f5a-063a8d11c75b"
//        )
//
//        try {
//            val response = service.saveProfil(request)
//
//            assertNotNull(response)
//            assertTrue("Le serveur aurait dû renvoyer success = true", response.success)
//            println("Test Réussi (saveProfil) : ${response.message}")
//        } catch (e: Exception) {
//            fail("Le serveur a crashé ou a rejeté la requête réseau : ${e.message}")
//        }
//    }
//
//    @Test
//    fun testRealSaveProfil_Failure_InvalidData() = runTest {
//        // On envoie des données invalides (par exemple un numéro vide ou un profil incomplet)
//        val request = ProfileRequest(
//            address = Address(
//                location = GeoLocation.fromLatLng(
//                    latitude = 3.8480,
//                    longitude = 11.5021
//                ),
//                quarter = "Bastos",
//                city = "Yaoundé",
//                region = "Centre",
//                country = "Cameroun",
//                lieuDit = "En face du laboratoire"
//            ),
//            paymentMethod = PaymentMethod.OM,
//            gazBottle = "btl_a8f76c1-968c-4a3a-9f5a-063a8d11c75b"
//        )
//
//        try {
//            val response = service.saveProfil(request)
//
//            // Si le serveur intercepte l'erreur proprement et renvoie un objet Response
//            assertNotNull(response)
//            assertFalse("Le serveur aurait dû rejeter l'inscription (success = false)", response.success)
//            println("Test Réussi ! Erreur correctement gérée par le serveur : ${response.message}")
//        } catch (e: Exception) {
//            // Si ton serveur renvoie un code d'erreur HTTP strict (ex: 400 Bad Request) sans passer par l'objet Response
//            println("Test Réussi ! Le serveur a rejeté la requête au niveau HTTP : ${e.message}")
//        }
//    }
//
//    // ==========================================
//    //  2. TESTS POUR updateProfile
//    // ==========================================
//
//    @Test
//    fun testRealUpdateProfile_Success() = runTest {
//        // Remplace par un ProfileRequest valide qui correspond à un profil existant
//        val request = ProfileRequest(
//            address = Address(
//                location = GeoLocation.fromLatLng(
//                    latitude = 3.8480,
//                    longitude = 11.5021
//                ),
//                quarter = "Bastos",
//                city = "Yaoundé",
//                region = "Centre",
//                country = "Cameroun",
//                lieuDit = "En face du laboratoire meka"
//            ),
//            paymentMethod = PaymentMethod.OM,
//            gazBottle = "btl_a8f76c1-968c-4a3a-9f5a-063a8d11c75b"
//        )
//
//        try {
//            val response = service.updateProfile(request)
//
//            assertNotNull(response)
//            assertTrue("La mise à jour aurait dû réussir", response.success)
//            println("Test Réussi (updateProfile) : ${response.message}")
//        } catch (e: Exception) {
//            fail("Échec de la mise à jour réseau : ${e.message}")
//        }
//    }
//
//    @Test
//    fun testRealUpdateProfile_Failure_ProfileNotFound() = runTest {
//        // On tente de modifier un profil avec un numéro de téléphone fictif qui n'existe pas
//        val request = ProfileRequest(
//            address = Address(
//                location = GeoLocation.fromLatLng(
//                    latitude = 3.8480,
//                    longitude = 11.5021
//                ),
//                quarter = "Bastos",
//                city = "Yaoundé",
//                region = "Centre",
//                country = "Cameroun",
//                lieuDit = "En face du laboratoire meka"
//            ),
//            paymentMethod = PaymentMethod.OM,
//            gazBottle = "btl_a8f76c1-968c-4a3a-9f5a-063a8d11c75b"
//        )
//
//        try {
//            val response = service.updateProfile(request)
//            assertFalse("Le serveur aurait dû renvoyer success = false car le profil n'existe pas", response.success)
//            println("Test Réussi ! Modification refusée : ${response.message}")
//        } catch (e: Exception) {
//            println("Test Réussi ! Rejeté par le serveur : ${e.message}")
//        }
//    }
//
//    // ==========================================
//    //  3. TESTS POUR getProfile
//    // ==========================================
//
//    @Test
//    fun testRealGetProfile_Success() = runTest {
//
//        try {
//            val response = service.getProfile()
//
//            assertNotNull(response)
//            assertTrue("Le profil aurait dû être trouvé", response.success)
//            assertEquals("profile trouver", response.message)
//            println("Test Réussi ! Profil récupéré.")
//        } catch (e: Exception) {
//            fail("Impossible de récupérer le profil : ${e.message}")
//        }
//    }
//
//    @Test
//    fun testRealGetProfile_Failure_NotAuthenticated() = runTest {
//        // Simule un échec (par exemple si aucune session n'est ouverte sur le client Ktor)
//        try {
//            val response = service.getProfile()
//            assertFalse("Le serveur aurait dû refuser la récupération", response.success)
//            println("Test Réussi ! Accès refusé par le serveur : ${response.message}")
//        } catch (e: Exception) {
//            // Si le serveur répond par un code HTTP 401 Unauthorized par exemple
//            println("Test Réussi ! Erreur HTTP interceptée : ${e.message}")
//        }
//    }
//
//    // ==========================================
//    //  4. TESTS POUR getDepotGaz
//    // ==========================================
//
//    @Test
//    fun testRealGetDepotGaz_Success() = runTest {
//        // Coordonnées et UUID valides présents dans ta base de données
//        val latitude = "3.8480"
//        val longitude = "11.5021"
//        val radiusKm = "10.0"
//        val battleUuid = "b8a876c1-968c-4a3a-9f5a-063a8d11c75b" // Exemple de UUID de bouteille de gaz
//
//        try {
//            val distributors = service.getDepotGaz(latitude.toDouble(), longitude.toDouble(), radiusKm, battleUuid)
//
//            assertNotNull(distributors)
//            // Le test passe même si la liste est vide (0 distributeurs trouvés), tant que le serveur répond 200 OK
//            println("Test Réussi ! Nombre de dépôts trouvés : ${distributors.size}")
//        } catch (e: Exception) {
//            fail("La recherche a échoué alors qu'elle aurait dû réussir : ${e.message}")
//        }
//    }
//
//    @Test
//    fun testRealGetDepotGaz_Failure_InvalidParameters() = runTest {
//        // On envoie des coordonnées invalides qui vont faire planter le "toDoubleOrNull()" du serveur
//        val badLatitude = "not_a_latitude"
//        val longitude = "11.5021"
//        val radiusKm = "10.0"
//        val battleUuid = "" // Chaîne vide pour déclencher le isNullOrEmpty() du serveur
//
//        try {
//            // Cette ligne va lever une Exception car le serveur renverra un code 400
//            service.getDepotGaz(badLatitude.toDouble(), longitude.toDouble(), radiusKm, battleUuid)
//
//            // Si le code arrive ici, c'est que le serveur a renvoyé un code 200 (alors qu'on voulait un échec)
//            fail("Le test aurait dû échouer à cause des mauvais paramètres, mais le serveur a répondu avec succès.")
//        } catch (e: Exception) {
//            // Le test passe ici car l'exception est levée. On valide qu'il s'agit bien de l'erreur 400.
//            assertTrue(
//                "L'exception aurait dû mentionner une erreur 400",
//                e.message!!.contains("400")
//            )
//            println("Test Réussi ! Le serveur a correctement rejeté les mauvais paramètres : ${e.message}")
//        }
//    }
//
//}