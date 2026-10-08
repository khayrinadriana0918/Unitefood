package com.example.groupproject

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    private lateinit var loginButton: MaterialButton
    private lateinit var switchButton: TextView
    private lateinit var title: TextView

    private var isLoginMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        databaseHelper = DatabaseHelper(this)

        usernameInput = findViewById(R.id.editUsername)
        emailInput = findViewById(R.id.editEmail)
        passwordInput = findViewById(R.id.editEmail)

        loginButton = findViewById(R.id.btnLogin)
        switchButton = findViewById(R.id.Switch)
        title = findViewById(R.id.title)

        updateMode()

        loginButton.setOnClickListener {
            if (isLoginMode) {
                login()
            } else {
                register()
            }
        }

        switchButton.setOnClickListener {
            isLoginMode = !isLoginMode
            updateMode()
        }
    }

    private fun updateMode() {

        if (isLoginMode) {

            title.text = "Login"

            usernameInput.visibility = android.view.View.GONE

            loginButton.text = "Login"

            switchButton.text =
                "Don't have an account? Sign Up"

        } else {

            title.text = "Create Account"

            usernameInput.visibility = android.view.View.VISIBLE

            loginButton.text = "Sign Up"

            switchButton.text =
                "Already have an account? Login"
        }
    }

    private fun register() {

        val username = usernameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (username.isEmpty() ||
            email.isEmpty() ||
            password.isEmpty()
        ) {
            Toast.makeText(
                this,
                "Please fill in all fields",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val registered = databaseHelper.registerUser(
            username,
            email,
            password
        )

        if (registered) {

            Toast.makeText(
                this,
                "Account created successfully",
                Toast.LENGTH_SHORT
            ).show()

            isLoginMode = true
            updateMode()

            usernameInput.text.clear()
            emailInput.text.clear()
            passwordInput.text.clear()

        } else {

            Toast.makeText(
                this,
                "Username or email already exists",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun login() {

        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                this,
                "Please enter email and password",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val valid = databaseHelper.loginUser(
            email,
            password
        )

        if (valid) {

            Toast.makeText(
                this,
                "Login successful",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(
                this,
                MainActivity::class.java
            )

            startActivity(intent)

            finish()

        } else {

            Toast.makeText(
                this,
                "Invalid email or password",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}