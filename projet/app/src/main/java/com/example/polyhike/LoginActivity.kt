package com.example.polyhike

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.polyhike.db.PolyHikeDatabase
import com.example.polyhike.db.UserProfileDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var userProfileDao: UserProfileDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()


        userProfileDao = PolyHikeDatabase.getDatabase(this, lifecycleScope).userProfileDao()

        val buttonSignIn = findViewById<Button>(R.id.buttonSignIn)
        buttonSignIn.setOnClickListener {
            val userName = findViewById<EditText>(R.id.editTextUserName).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()

            login(userName, password)
        }
    }

    private fun login(name: String, password: String) {
        if (name.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                val user = userProfileDao.getUserByNameAndPassword(name, password)
                runOnUiThread {
                    if (user != null) {
                        Toast.makeText(applicationContext, "Connexion réussie", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, NavManagerActivity::class.java)
                        intent.putExtra("USER_ID", user.id)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Identifiants incorrects", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
        }
    }
}
