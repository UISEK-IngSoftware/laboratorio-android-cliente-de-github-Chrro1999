package ec.edu.uisek.githubclient.services
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import ec.edu.uisek.githubclient.models.UserCredentials


class SessionManager (context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "user_share_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    companion object{
        private const val USERNAME = "username"
        private const val TOKEN = "token"

    }
    fun saveCredentials(username: String, token: String){
        sharedPreferences.edit()
            .putString(USERNAME, username)
            .putString(TOKEN, token)
            .apply()
    }
    fun getCredentials(): UserCredentials?{
        val username = sharedPreferences.getString(USERNAME, null)
        val token = sharedPreferences.getString(TOKEN, null)
        return if (username != null && token != null){
            UserCredentials(username, token)
    } else{
            null
        }
    }
    fun clearCredentials() {
        sharedPreferences.edit()
            .remove(USERNAME)
            .remove(TOKEN)
            .apply()
    }

}