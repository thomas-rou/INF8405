package com.example.polyhike

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonSignIn = findViewById<Button>(R.id.buttonSignIn)
        buttonSignIn.setOnClickListener {
            val userName = findViewById<EditText>(R.id.editTextUserName).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                // TODO: check if user exists before going to home page
                val intent = Intent(this, NavManagerActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
