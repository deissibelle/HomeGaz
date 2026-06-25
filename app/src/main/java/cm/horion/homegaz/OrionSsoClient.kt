package cm.horion.homegaz

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class OrionSsoClient(
    private val activity: ComponentActivity,
    private val serviceName: String, // Rendu dynamique pour éviter les conflits GAZ/CV
    private val onLoginSuccess: (accessToken: String) -> Unit,
    private val onLogoutSuccess: () -> Unit,
    private val onErrorOrCancel: (message: String) -> Unit
) {

    companion object {
        private const val AUTH_PACKAGE = "io.horion.service_auth_orion"
        private const val SCHEME = "authapp"
    }

    private val authLauncher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val status = result.data?.getStringExtra("status")
            if (status == "SUCCESS") {
                startExchange()
            } else {
                onErrorOrCancel("Échec de l'authentification")
            }
        } else {
            onErrorOrCancel("Connexion annulée")
        }
    }

    private val exchangeLauncher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val accessToken = result.data?.getStringExtra("accessToken")
            if (accessToken != null) {
                onLoginSuccess(accessToken)
            } else {
                onErrorOrCancel("Token d'accès manquant")
            }
        } else {
            onErrorOrCancel("Échange de token annulé")
        }
    }

    private val logoutLauncher: ActivityResultLauncher<Intent> = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onLogoutSuccess()
        } else {
            onErrorOrCancel("Erreur lors de la déconnexion globale")
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Fonctions Publiques Sécurisées
    // ─────────────────────────────────────────────────────────────────

    fun launchAuth() {
        val authUri = Uri.parse("$SCHEME://login")
            .buildUpon()
            .appendQueryParameter("service", serviceName)
            .build()

        val intent = Intent(Intent.ACTION_VIEW, authUri).apply {
            setPackage(AUTH_PACKAGE)
        }

        safelyLaunchIntent(intent)
    }

    fun startLogout() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("$SCHEME://logout")
            setPackage(AUTH_PACKAGE)
        }
        safelyLaunchIntent(intent, isLogout = true)
    }

    fun startExchange() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("$SCHEME://exchange-token?service=$serviceName")
            setPackage(AUTH_PACKAGE)
        }
        try {
            exchangeLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            onErrorOrCancel("Application Orion Auth introuvable lors de l'échange.")
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Gestion de l'absence de l'application
    // ─────────────────────────────────────────────────────────────────

    private fun safelyLaunchIntent(intent: Intent, isLogout: Boolean = false) {
        try {
            if (isLogout) {
                logoutLauncher.launch(intent)
            } else {
                authLauncher.launch(intent)
            }
        } catch (e: ActivityNotFoundException) {
            // L'application n'est pas installée sur l'appareil !
            redirectToPlayStoreOrShowDialog()
        }
    }

    private fun redirectToPlayStoreOrShowDialog() {
        // Option A : Rediriger directement sur la page du Play Store de ton App Auth
        try {
            val playStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$AUTH_PACKAGE")
            )
            activity.startActivity(playStoreIntent)
        } catch (anfe: ActivityNotFoundException) {
            // Au cas où le Play Store lui-même n'est pas dispo (ex: Huawei sans GMS)
            val webPlayStoreIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$AUTH_PACKAGE")
            )
            activity.startActivity(webPlayStoreIntent)
        }

        // Option B : Tu peux aussi notifier ton UI Compose via le callback d'erreur
        onErrorOrCancel("L'application Orion Authentification est requise. Redirection vers le store...")
    }
}