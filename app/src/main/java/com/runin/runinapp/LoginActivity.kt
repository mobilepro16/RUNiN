package com.runin.runinapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.runin.runinapp.api.APIService
import com.runin.runinapp.data.User
import com.runin.runinapp.indoor.IndoorDashboardActivity
import com.runin.runinapp.settings.ProfileActivity
import com.runin.runinapp.tutorial.TutorialActivity
import com.runin.runinapp.utils.AuthUtils
import com.runin.runinapp.utils.saveUser
import dagger.android.AndroidInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber
import javax.inject.Inject

class LoginActivity : BaseActivity() {

    companion object {
        val TAG: String = FacebookRuninActivity::class.java.simpleName
        const val REQUEST_CODE_GOOGLE_SIGN = 100
    }

    @Inject
    lateinit var user: User

    @Inject
    lateinit var apiService: APIService

    @Inject
    lateinit var sharedPref: SharedPreferences

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val fbCallbackManager = CallbackManager.Factory.create()
    private var googleSignInOptions: GoogleSignInOptions? = null
    private var googleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init()
        //SignUp
        signUp.setOnClickListener {
            val signUpIntent = Intent(this, SignUpActivity::class.java)
            startActivity(signUpIntent)
        }

        recoverPassword.setOnClickListener {
            val alert = ViewDialog()
            alert.showDialog(this)
        }

        btnLogin.setOnClickListener {
            val username = signUser.text.toString().toLowerCase()
            val password = signPass.text.toString()
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                val dialogError = AlertDialog.Builder(this)
                dialogError.setTitle(resources.getString(R.string.newUpdateMessage1))
                dialogError.setMessage(resources.getString(R.string.newUpdateMessage1))
                dialogError.setCancelable(false)
                dialogError.setNegativeButton(resources.getString(R.string.cancel)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
                dialogError.show()
            } else {
                signInWithEmailAndPass(username, password)
            }
        }

    }

    private fun init() {
        // Fb
        btnFacebook.setReadPermissions("email", "public_profile")
        btnFacebook.registerCallback(fbCallbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    if (loginResult != null && loginResult.accessToken != null && loginResult.accessToken.token != null) {
                        val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
                        signInWithCredential(credential)
                    }
                }
            }

            override fun onCancel() {
                Timber.tag(TAG).w("Facebook login canceled")
            }

            override fun onError(exception: FacebookException) {
                Timber.e(exception, "Facebook login error")
                val bundle = Bundle()
                bundle.putString("error", exception.localizedMessage)
                Firebase.analytics.logEvent("FacebookSignInError", bundle)
            }
        })
        btnFacebookLayout.setOnClickListener {
            btnFacebook.performClick()
        }
        //Google
        configureGoogleSignIn()
        btnGoogle.setOnClickListener { signIn() }
    }

    private fun configureGoogleSignIn() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("912081236116-tarf3e9ga0miuggnas5klj0t45qm29vj.apps.googleusercontent.com")
                .requestEmail()
                .build()
        googleSignInOptions?.let {
            googleSignInClient = GoogleSignIn.getClient(this, it)
        }
    }

    private fun signIn() {
        googleSignInClient?.signInIntent?.let {
            startActivityForResult(it, REQUEST_CODE_GOOGLE_SIGN)
        }
    }

    private fun signInWithEmailAndPass(email: String, password: String) {
        showProgress()
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful) {
                        AuthUtils.saveAccessToken(sharedPref, apiService, onLoginSuccess)
                        return@addOnCompleteListener
                    }
                    handleTaskError(it)
                    val bundle = Bundle()
                    bundle.putString("email", email)
                    bundle.putString("error", it.exception?.localizedMessage)
                    Firebase.analytics.logEvent("SignInWithEmailAndPassError", bundle)
                }
    }

    private fun signInWithCredential(credential: AuthCredential) {
        showProgress()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                user.name = FirebaseAuth.getInstance().currentUser?.displayName ?: ""
                AuthUtils.saveAccessToken(sharedPref, apiService, onLoginSuccess)
                return@addOnCompleteListener
            }
            handleTaskError(it)
            val bundle = Bundle()
            bundle.putString("provider", credential.provider)
            bundle.putString("method", credential.signInMethod)
            bundle.putString("error", it.exception?.localizedMessage)
            Firebase.analytics.logEvent("SignInWithCredentialError", bundle)
        }
    }

    private var disposable: Disposable? = null
    private val onLoginSuccess = {
        disposable = apiService.getUserInfo()
                .subscribeOn(Schedulers.io())
                .doFinally(this::hideProgress)
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    sharedPref.saveUser(user, it)
                    val isTutorialVisited = sharedPref.getBoolean(App.sharedPreferencesPropertyTutorialVisited, false)
                    if (isTutorialVisited) {
                        startActivity(Intent(this, IndoorDashboardActivity::class.java))
                    } else {
                        startActivity(Intent(this, TutorialActivity::class.java))
                    }
                    finish()
                }, {
                    if (userNotFound(it)) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    } else {
                        handleNetworkError
                    }
                })
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GOOGLE_SIGN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    val credential = GoogleAuthProvider.getCredential(it.idToken, null)
                    signInWithCredential(credential)
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: " + e.localizedMessage, Toast.LENGTH_LONG).show()
                val bundle = Bundle()
                bundle.putString("error", e.localizedMessage)
                Firebase.analytics.logEvent("GoogleSignInError", bundle)
            }
        }
    }


    private fun recoverPassword(usernameRecover: String) {

    }

    private inner class ViewDialog {
        fun showDialog(activity: LoginActivity?) {
            val dialog = Dialog(activity!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_recover_password)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val btnRecoverPassword = dialog.findViewById<View>(R.id.btnRecoverPassword) as LinearLayout
            btnRecoverPassword.setOnClickListener {
                val usernameRecover: String
                val signUser = dialog.findViewById<View>(R.id.usernameRecover) as EditText
                usernameRecover = signUser.text.toString().toLowerCase()
                recoverPassword(usernameRecover)
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}