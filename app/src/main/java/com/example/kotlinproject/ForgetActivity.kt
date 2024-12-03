package com.example.kotlinproject

import android.annotation.SuppressLint
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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinproject.LoginActivity
import com.example.kotlinproject.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ForgetActivity: AppCompatActivity() {

    private lateinit var back : View;
    private lateinit var email: EditText
    private lateinit var emailLayout: TextInputLayout
    private lateinit var emailLabel: TextView
    private lateinit var cont: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingSpinner: ProgressBar


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)

        email = findViewById(R.id.email)
        emailLabel = findViewById(R.id.EmailLabel)
        emailLayout = findViewById(R.id.emailLayout)
        cont = findViewById(R.id.Continue)
        loadingSpinner = findViewById(R.id.loadingSpinner)

        back = findViewById(R.id.back)
        auth = FirebaseAuth.getInstance()

        back.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        cont.setOnClickListener{

            resetStyles()
            val emailText = email.text.toString().trim()
            var hasError = false

            if (emailText.isEmpty() || !isValidEmail(emailText)) {
                applyErrorStyle(email,emailLabel,emailLayout)
                emailLayout.error = if (emailText.isEmpty()) {
                    "Required Field."
                } else {
                    "Provide a valid email."
                }
                hasError = true
            }

            if (hasError) {
                return@setOnClickListener
            }



            if (emailText.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                showLoading(true)
                resetPassword(emailText)
            }

        }
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


    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "A password reset link has been sent to your email.",
                        Toast.LENGTH_LONG
                    ).show()
                    showLoading(false)

                    // Redirect to Login screen (Optional)
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showLoading(false)

                    Toast.makeText(
                        this,
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun resetStyles() {
        email.setBackgroundResource(R.drawable.edittext_border) // Normal border
        emailLayout.error = null
        emailLabel.setTextColor(Color.parseColor("#333333")) // Default label color
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

    // Email Validation Method using Regex
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            // Show the loading spinner and disable the button
            loadingSpinner.visibility = View.VISIBLE
            cont.visibility = View.GONE


        } else {
            // Hide the loading spinner and re-enable the button
            loadingSpinner.visibility = View.GONE
            cont.visibility = View.VISIBLE

        }
    }
}