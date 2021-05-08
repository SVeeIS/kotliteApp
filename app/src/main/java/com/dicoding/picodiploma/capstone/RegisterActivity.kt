package com.dicoding.picodiploma.capstone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Toast
import com.dicoding.picodiploma.capstone.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.util.regex.Pattern

@Suppress("NAME_SHADOWING", "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        val role = resources.getStringArray(R.array.role)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, role)
        binding.etRole.setAdapter(arrayAdapter)

        binding.btnRegister.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.etUsername.text.toString().trim {it <= ' '}) -> {
                    binding.etUsername.error = "Username Is Required"
                    binding.etUsername.requestFocus()
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please Enter Your Username",
                        Toast.LENGTH_SHORT
                    ). show()
                }

                TextUtils.isEmpty(binding.etEmail.text.toString().trim {it <= ' '}) -> {
                    binding.etEmail.error = "Email Is Required"
                    binding.etEmail.requestFocus()
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter your E-Mail",
                        Toast.LENGTH_SHORT
                    ). show()
                }

                TextUtils.isEmpty(binding.etPassword.text.toString().trim {it <= ' '}) -> {
                    binding.etPassword.error = "Password Is Required"
                    binding.etPassword.requestFocus()
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please Enter Your Password",
                        Toast.LENGTH_SHORT
                    ). show()
                }

                else -> {
                    val username: String = binding.etUsername.text.toString().trim {it <= ' '}
                    val email: String = binding.etEmail.text.toString().trim {it <= ' '}
                    val password: String = binding.etPassword.text.toString().trim {it <= ' '}
                    val role: String = binding.etRole.text.toString().trim {it <= ' '}

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        binding.etEmail.error = "Please Provide valid E-Mail"
                        binding.etEmail.requestFocus()
                        return@setOnClickListener
                    }

                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val users = User(username, email, role)

                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(mAuth.currentUser.uid)
                                    .setValue(users).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this, "User Has Been Registered Succesfully", Toast.LENGTH_SHORT).show()
                                            if (role == "Passenger") {
                                                startActivity(Intent(this, HomeActivity::class.java))
                                            } else if (role == "Driver") {
                                                startActivity(Intent(this, DriverActivity::class.java))
                                            } else {
                                                Toast.makeText(this, "Failed to Register! Try Again", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(this, "Failed to Register! Try Again", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Failed to Register! Try Again", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
    }
}