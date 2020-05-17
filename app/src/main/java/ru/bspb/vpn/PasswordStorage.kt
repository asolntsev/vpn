package ru.bspb.vpn

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class PasswordStorage(applicationContext: Context) {
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        "bspb-vpn",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setPassword(password: String) {
        sharedPreferences.edit().apply() {
            Log.i("PasswordStorage", "save new value ${password.replace(Regex("."), "*")}")
            putString("the-value", password)
            apply()
        }
    }

    fun getPassword(): String {
        return sharedPreferences.getString("the-value", "not saved yet") ?: ""
    }
}