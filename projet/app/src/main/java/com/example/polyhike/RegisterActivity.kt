package com.example.polyhike

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            val name = findViewById<EditText>(R.id.editTextName).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

            if (name.isNotEmpty() && password.isNotEmpty()) {
                // TODO: add userProfile to database before going to home page
                val intent = Intent(this, NavManagerActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
