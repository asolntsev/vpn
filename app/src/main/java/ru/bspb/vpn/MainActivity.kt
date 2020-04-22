package ru.bspb.vpn

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys


class MainActivity : AppCompatActivity() {
    private var editing: Boolean = false

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = EncryptedSharedPreferences.create(
                "bspb-vpn",
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        findViewById<EditText>(R.id.password).setOnFocusChangeListener { _, _ ->
            editing = true
        }

        findViewById<Button>(R.id.save).setOnClickListener {
            sharedPreferences.edit().apply() {
                val newValue: String = findViewById<EditText>(R.id.password).text.toString()
                Log.i("MainActivity", "save new value ${newValue.replace(".", "*")}")
                putString("the-value", newValue)
                apply()
            }
            editing = false
            finish()
        }

        Handler().postDelayed({
            if (!editing) {
                val theValue = sharedPreferences.getString("the-value", "not saved yet")
                val clip = ClipData.newPlainText("Copied Text", theValue)
                val service = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                service.setPrimaryClip(clip)
                Log.i("MainActivity", "pasted: " + service.primaryClip.toString().replace(".", "*"))

                val i = packageManager.getLaunchIntentForPackage("com.cisco.anyconnect.vpn.android.avf")
                startActivity(i)

                finish()
            }
        }, 1000)
    }
}
