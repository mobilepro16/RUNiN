package com.runin.runinapp

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import retrofit2.HttpException

open class BaseActivity : AppCompatActivity() {
    private var progress: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progress = ProgressDialog(this)
        progress?.setMessage("Loading...")
        progress?.setCancelable(false)
    }

    protected fun showProgress() {
        progress?.show()
    }

    protected fun hideProgress() {
        progress?.dismiss()
    }

    protected fun handleTaskError(task: Task<AuthResult>) {
        hideProgress()
        if (task.exception is FirebaseException) {
            Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    protected fun userNotFound(throwable: Throwable) = throwable is HttpException && throwable.code() == 404

    protected val handleNetworkError = { it: Throwable ->
        hideProgress()
        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
    }
}