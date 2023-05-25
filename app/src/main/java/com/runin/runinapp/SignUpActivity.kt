package com.runin.runinapp

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageView
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.runin.runinapp.api.APIService
import com.runin.runinapp.settings.ProfileActivity
import com.runin.runinapp.utils.AuthUtils
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_sign_up.*
import javax.inject.Inject

class SignUpActivity : BaseActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    @Inject
    lateinit var sharedPref: SharedPreferences

    @Inject
    lateinit var apiService: APIService
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            val onBackPressedIntent = Intent(this, LoginActivity::class.java)
            startActivity(onBackPressedIntent)
            finish()
        }
        saveUser.setOnClickListener {
            //Login
            val username = signUser.text.toString().toLowerCase()
            val password = signPass.text.toString()
            val confirmPassword = signPassConfirm.text.toString()
            if (password == confirmPassword && username.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                showProgress()
                firebaseAuth.createUserWithEmailAndPassword(username, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        AuthUtils.saveAccessToken(sharedPref, apiService, {
                            hideProgress()
                            val tutorialIntent = Intent(this, ProfileActivity::class.java)
                            startActivity(tutorialIntent)
                            finish()
                        }, {
                            hideProgress()
                        })
                        return@addOnCompleteListener
                    }
                    handleTaskError(it)
                    val bundle = Bundle()
                    bundle.putString("email", username)
                    bundle.putString("error", it.exception?.localizedMessage)
                    Firebase.analytics.logEvent("SignUpError", bundle)
                }
            } else {
                val dialogError = AlertDialog.Builder(this@SignUpActivity)
                dialogError.setMessage(resources.getString(R.string.verify_password))
                dialogError.setCancelable(false)
                dialogError.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                dialogError.show()
            }
        }
    }
}