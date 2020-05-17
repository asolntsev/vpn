package ru.bspb.vpn

import android.content.ClipData.newPlainText
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private var editing: Boolean = false
    private lateinit var passwordStorage: PasswordStorage
    private lateinit var clipboard: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        passwordStorage = PasswordStorage(applicationContext)
        clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        passwordInput.setOnFocusChangeListener { _, _ ->
            editing = true
        }

        saveButton.setOnClickListener {
            val newPassword: String = findViewById<EditText>(R.id.passwordInput).text.toString()
            passwordStorage.setPassword(newPassword)
            editing = false
            finish()
        }

        Handler().postDelayed({
            if (!editing) {
                copyPasswordToClipboard()
                openAnyConnect()
                scheduleClipboardCleanup()
            }
        }, 1_000)
    }

    private fun copyPasswordToClipboard() {
        val password = passwordStorage.getPassword()
        clipboard.setPrimaryClip(newPlainText("Copied text", password))
        Log.i("MainActivity", "copied password: " + getMaskedClipboard())
    }

    private fun openAnyConnect() {
        startActivity(packageManager.getLaunchIntentForPackage("com.cisco.anyconnect.vpn.android.avf"))
    }

    private fun scheduleClipboardCleanup() {
        Handler().postDelayed(::cleanupClipboard, 15_000)
    }

    private fun cleanupClipboard() {
        clipboard.setPrimaryClip(newPlainText("Reset text", ""))
        Log.i("MainActivity", "reset password: " + getMaskedClipboard())
        finish()
    }

    private fun getMaskedClipboard() =
        clipboard.primaryClip?.getItemAt(0)?.text?.replace(Regex("."), "*")
}
