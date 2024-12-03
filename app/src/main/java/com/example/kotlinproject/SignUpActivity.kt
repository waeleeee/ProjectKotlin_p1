package com.example.kotlinproject

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.sign

class SignUpActivity: AppCompatActivity() {

    private lateinit var fullName: EditText
    private lateinit var fullNameLayout: TextInputLayout

    private lateinit var fullNameLabel: TextView
    private lateinit var emailLabel: TextView

    private lateinit var email: EditText


    private lateinit var emailLayout: TextInputLayout

    private lateinit var passwordLayout: TextInputLayout
    private lateinit var password: EditText
    private lateinit var passwordLabel: TextView
    private lateinit var login: TextView
    private lateinit var signup: Button

    private lateinit var bottom: LinearLayout

    private lateinit var customGoogleButton: LinearLayout
    private lateinit var loadingSpinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // The user is already logged in, redirect them to the home screen
            val intent = Intent(this, BaseActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        fullName = findViewById(R.id.name)
        email = findViewById(R.id.email)
        fullNameLabel = findViewById(R.id.fullNameLabel)
        emailLabel = findViewById(R.id.EmailLabel)
        emailLayout = findViewById(R.id.emailLayout)
        fullNameLayout = findViewById(R.id.nameLayout)
        password = findViewById(R.id.password)
        passwordLabel = findViewById(R.id.passwordLabel)
        signup = findViewById(R.id.signup)

        login = findViewById(R.id.login)

      bottom = findViewById(R.id.bottom)
        loadingSpinner = findViewById(R.id.loadingSpinner)
        passwordLayout = findViewById(R.id.passwordLayout)
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(email,emailLabel,emailLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty() && isValidEmail(s.toString())) {
                    emailLayout.error = null
                    emailLayout.isErrorEnabled = false
                }
            }
        })
        fullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(fullName,fullNameLabel,fullNameLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    // Additional actions, like clearing an error
                    emailLayout.error = null
                }
            }
        })
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(password,passwordLabel,passwordLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    passwordLayout.error = null
                    passwordLayout.isErrorEnabled = false
                }
            }
        })



        signup.setOnClickListener {
            // Reset styles first
            resetStyles()

            // Validate input
            val fullNameText = fullName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            var hasError = false
            if (fullNameText.isEmpty() || fullNameText.length < 3) {
                applyErrorStyle(fullName,fullNameLabel,fullNameLayout)
                fullNameLayout.error = if (fullNameText.isEmpty()) {
                    "Required Field."
                } else {
                    "Provide a valid name."
                }
                hasError = true
            }

            // Validate Email
            if (emailText.isEmpty() || !isValidEmail(emailText)) {
                applyErrorStyle(email,emailLabel,emailLayout)
                emailLayout.error = if (emailText.isEmpty()) {
                    "Required Field."
                } else {
                    "Provide a valid email."
                }
                hasError = true
            }
            // Password validation
            if (passwordText.isEmpty() || passwordText.length < 8) {
                applyErrorStyle(password, passwordLabel, passwordLayout)
                passwordLayout.error = if (passwordText.isEmpty()) {
                    "Required Field."
                } else {
                    "Minimum length is 8 characters."
                }
                hasError = true
            }


            if (hasError) {
                return@setOnClickListener
            }
            showLoading(true)
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "auth successful", Toast.LENGTH_SHORT).show()

                        // Registration successful, now save the username to Firestore
                        val user = FirebaseAuth.getInstance().currentUser
                        val db = FirebaseFirestore.getInstance()

                        if (user != null) {
                            val userData = hashMapOf(
                                "username" to fullNameText,
                                "email" to user.email
                            )

                            // Create a new document with the user's UID as the document ID
                            db.collection("users").document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {




                                    // Successfully saved username to Firestore
                                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                    showLoading(false)
                                    startActivity(Intent(this, LoginActivity::class.java))
                                    finish() // Close the sign-up activity
                                }
                                .addOnFailureListener { e ->
                                    // Error saving user data

                                    showLoading(false)

                                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        // Registration failed
                        showLoading(false)

                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }



        }



        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(password,passwordLabel,passwordLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    passwordLayout.error = null
                    passwordLayout.isErrorEnabled = false
                }
            }
        })

        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(email,emailLabel,emailLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty() && isValidEmail(s.toString())) {
                    emailLayout.error = null
                    emailLayout.isErrorEnabled = false
                }
            }
        })
        fullName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Before the text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // While the text is changing

                resetLayout(fullName,fullNameLabel,fullNameLayout)

            }

            override fun afterTextChanged(s: Editable?) {
                // After the text has changed
                if (!s.isNullOrEmpty()) {
                    // Additional actions, like clearing an error
                    emailLayout.error = null
                }
            }
        })

        login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }



    }




    private fun applyErrorStyle(editText: EditText, label: TextView, inputLayout: TextInputLayout) {
        editText.setBackgroundResource(R.drawable.edittext_red_border)
        editText.setHintTextColor(Color.RED)
        editText.setTextColor(Color.RED)
        label.setTextColor(Color.RED)
        inputLayout.errorIconDrawable?.setTint(Color.RED)
        inputLayout.setErrorTextColor(ColorStateList.valueOf(Color.RED))
    }

    private fun resetLayout(editText: EditText, label: TextView, inputLayout:TextInputLayout){
        label.setTextColor(Color.parseColor("#333333")) // Default label color
        editText.setTextColor(Color.parseColor("#333333"))
        editText.setHintTextColor(Color.parseColor("#333333"))
        editText.setBackgroundResource(R.drawable.edittext_border) // Normal border
        inputLayout.error = null
        inputLayout.isErrorEnabled = false

    }
    private fun resetStyles() {
        password.setBackgroundResource(R.drawable.edittext_border) // Normal border
        passwordLabel.setTextColor(Color.parseColor("#333333")) // Default label color
        email.setBackgroundResource(R.drawable.edittext_border) // Normal border
        passwordLayout.error = null
        emailLayout.error = null
        fullName.setBackgroundResource(R.drawable.edittext_border)
fullNameLayout.error = null
        emailLabel.setTextColor(Color.parseColor("#333333")) // Default label color
        fullNameLabel.setTextColor(Color.parseColor("#333333")) // Default label color

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            // Show the loading spinner and disable the button
            loadingSpinner.visibility = View.VISIBLE
            bottom.visibility = View.GONE


        } else {
            // Hide the loading spinner and re-enable the button
            loadingSpinner.visibility = View.GONE
            bottom.visibility = View.VISIBLE

        }
    }



    // Email Validation Method using Regex
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}