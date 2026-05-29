package cm.horion.homegaz.util

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings

object LocationUtils {
    // Vérifie si le bouton GPS est activé dans les réglages
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // Lance l'intention système pour envoyer l'utilisateur vers les réglages GPS
    fun showLocationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }
}