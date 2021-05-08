package com.dicoding.picodiploma.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.dicoding.picodiploma.capstone.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var mAuth: FirebaseAuth

    private lateinit var user: FirebaseUser
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim {it <= ' '}
        val password = binding.etPassword.text.toString().trim {it <= ' '}

        if (email.isEmpty()) {
            binding.etEmail.error = "Email Is Required"
            binding.etEmail.requestFocus()
            Toast.makeText(
                    this@LoginActivity,
                    "Please enter your E-Mail",
                    Toast.LENGTH_SHORT
            ). show()
        }

        if (password.isEmpty()) {
            binding.etEmail.error = "Email Is Required"
            binding.etEmail.requestFocus()
            Toast.makeText(
                    this@LoginActivity,
                    "Please enter your E-Mail",
                    Toast.LENGTH_SHORT
            ). show()
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please Provide valid E-Mail"
            binding.etEmail.requestFocus()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                user = FirebaseAuth.getInstance().currentUser
                reference = FirebaseDatabase.getInstance().getReference("Users")
                val userId = user.uid

                reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        when (snapshot.child("role").value.toString()) {
                            "Passenger" -> {
                                startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            }
                            "Driver" -> {
                                startActivity(Intent(this@LoginActivity, DriverActivity::class.java))
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity, "Failed To Login", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginActivity, "Something Wrong Happened", Toast.LENGTH_SHORT). show()
                    }
                })

            } else {
                Toast.makeText(this@LoginActivity, "Failed To Login! Please Check Your Credential", Toast.LENGTH_SHORT). show()
            }
        }
    }
}