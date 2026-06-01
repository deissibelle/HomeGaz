package cm.horion.homegaz.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorage(private val context: Context) {
    private val prefsName = "secure_prefs"

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val securePrefs: SharedPreferences by lazy {
        createEncryptedPrefs()
    }

    private fun createEncryptedPrefs(): SharedPreferences {
        return try {
            EncryptedSharedPreferences.create(
                context,
                prefsName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // 🔥 Si clé invalide / corrompue
            context
                .getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                .edit {
                    clear()
                }

            EncryptedSharedPreferences.create(
                context,
                prefsName,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    fun save(key: String, value: String) {
        securePrefs.edit { putString(key, value) }
    }

    fun read(key: String): String? =
        securePrefs.getString(key, null)

    fun remove(key: String) {
        securePrefs.edit { remove(key) }
    }

    fun clear() {
        securePrefs.edit { clear() }
    }
}