package com.example.cicoinminer // Ganti com.example.cicoinminer sesuai nama paket Anda

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cicoinminer.databinding.ActivityLoginBinding
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val CICOIN_WALLET_REGEX = Pattern.compile("^(ci1q)[0-9A-Za-z]{38}$")
    
    // Nanti, token ini akan Anda dapatkan dari server (backend)
    private val PREFS_NAME = "CicoinUserPrefs"
    private val KEY_USER_TOKEN = "user_token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek apakah user sudah pernah login (punya token)
        if (isUserLoggedIn()) {
            goToMainActivity()
            return
        }

        binding.btnLoginRegister.setOnClickListener {
            handleLoginRegister()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_USER_TOKEN, null) != null
    }

    private fun handleLoginRegister() {
        val email = binding.etEmail.text.toString().trim()
        val wallet = binding.etWalletAddress.text.toString().trim()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        if (!CICOIN_WALLET_REGEX.matcher(wallet).matches()) {
            Toast.makeText(this, "Alamat wallet Cicoin tidak valid", Toast.LENGTH_SHORT).show()
            return
        }

        // --- ⚠️ TODO WAJIB (BACKEND) ---
        // 1. Kirim email dan wallet ini ke SERVER (BACKEND) Anda (misal: /loginOrRegister).
        // 2. Server Anda akan memvalidasi, menyimpan ke database, dan mengembalikan "TOKEN".
        // --------------------------------

        // (Simulasi) Anggap kita dapat token dari server
        val userTokenFromBackend = "abc123xyz789_TokenUnikDariServer" 
        
        // Simpan token ini
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString(KEY_USER_TOKEN, userTokenFromBackend)
            apply()
        }

        Toast.makeText(this, "Registrasi/Login Berhasil", Toast.LENGTH_SHORT).show()
        goToMainActivity()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}