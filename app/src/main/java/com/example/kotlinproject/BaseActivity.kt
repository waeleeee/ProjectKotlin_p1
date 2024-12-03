package com.example.kotlinproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class BaseActivity: AppCompatActivity()  {


    private lateinit var logoutB: Button;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)




        logoutB = findViewById(R.id.logoutB)


        logoutB.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Logs out the user from Firebase
            val intent = Intent(this, LoginActivity::class.java) // Navigate to the Login screen
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear backstack
            startActivity(intent)
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }
    }

}