package com.example.fingerprintauth

import android.app.Activity
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.widget.TextView
import androidx.core.content.ContextCompat


@Suppress("DEPRECATION")
class FingerprintHandler(val context: Context):FingerprintManager.AuthenticationCallback() {
    fun startAuth(manager: FingerprintManager,cryptoObject: FingerprintManager.CryptoObject)
    {
        manager.authenticate(cryptoObject, CancellationSignal(),0,this,null)

    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {

        this.update("Fingerprint Authentication error \n $errString",false)
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence) {
        this.update("Fingerprint Authentication help \n $helpString",false)
    }

    override fun onAuthenticationFailed() {
        this.update("Fingerprint Authentication Failed",false)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        this.update("Fingerprint Authentication Succeeded",true)
    }


    fun update(e:String,success:Boolean)
    {
        val textView=(context as Activity).findViewById<TextView>(R.id.errorText)
        textView.text=e
        if(success)
        {
            textView.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark))
        }
        else
        {
            textView.setTextColor(ContextCompat.getColor(context,R.color.colorAccent))
        }
    }
}