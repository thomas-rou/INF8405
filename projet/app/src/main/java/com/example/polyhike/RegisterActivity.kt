package com.example.polyhike

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.polyhike.db.PolyHikeDatabase
import kotlinx.coroutines.launch
import com.example.polyhike.db.UserProfileDao
import com.example.polyhike.model.UserProfile
import kotlinx.coroutines.Dispatchers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.core.content.edit
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope

class RegisterActivity : AppCompatActivity() {
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var imageView: ImageView
    private var imageURI: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        userProfileDao = PolyHikeDatabase.getDatabase(this, lifecycleScope).userProfileDao()
        imageView = findViewById(R.id.imageViewSelectedPhoto)

        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextDateOfBirth = findViewById<EditText>(R.id.editTextDateOfBirth)
        val buttonSelectPhoto = findViewById<Button>(R.id.buttonSelectPhoto)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)

        buttonSelectPhoto.setOnClickListener { selectPhoto() }
        buttonRegister.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val dateOfBirth = editTextDateOfBirth.text.toString().trim()
            if (validateInput(name, password, dateOfBirth) and validateDateFormat(dateOfBirth)) {
                CoroutineScope(Dispatchers.IO).launch {
                    val newUser = UserProfile(0, name, password, dateOfBirth, imageURI, 1)
                    userProfileDao.insert(newUser)
                    val user = userProfileDao.getUserByNameAndPassword(name, password)
                    addUserToFirestore(user)
                    runOnUiThread {
                        val sharedPref = getSharedPreferences("session", MODE_PRIVATE)
                        if (user != null) sharedPref.edit() { putInt("userId", user.id) }
                        Toast.makeText(applicationContext, "Inscription réussie", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, NavManagerActivity::class.java)
                        if (user != null) intent.putExtra("USER_ID", user.id)
                        startActivity(intent)
                    }
                }
            } else if (!validateDateFormat(dateOfBirth)) {
                Toast.makeText(this, "Le format de la date est invalide, vueillez utiliser DD/MM/YYYY", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInput(name: String, password: String, dob: String): Boolean {
        return name.isNotEmpty() && password.isNotEmpty() && dob.isNotEmpty()
    }

    private fun validateDateFormat(dateStr: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.isLenient = false
        try {
            dateFormat.parse(dateStr)
            return true
        } catch (e: ParseException) {
            return false
        }
    }

    private fun selectPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri)
            imageURI = selectedImageUri.toString()
        }
    }

    fun addUserToFirestore(userProfile: UserProfile?) {
        val db = Firebase.firestore
        val user = hashMapOf(
            "id" to userProfile?.id,
            "name" to userProfile?.name,
        )
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    companion object {
        private const val PHOTO_PICKER_REQUEST_CODE = 1000
    }
}
