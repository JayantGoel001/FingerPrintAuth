@file:Suppress("DEPRECATION")

package com.example.fingerprintauth

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var cipher:Cipher
    var KEY_NAME="AndroidExamples"
    lateinit var keyStore:KeyStore
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val keygaurdManager=getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        val fingerprintManager=getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        if(!fingerprintManager.isHardwareDetected)
        {
            errorText.text="Your Device Does Not have a Fingerprint Sensor"
        }
        else
        {
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_FINGERPRINT)!=PackageManager.PERMISSION_GRANTED)
            {
                errorText.text="Fingerprint authentication permission not enabled"
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.USE_FINGERPRINT),1234)
            }
            else
            {
                if(!fingerprintManager.hasEnrolledFingerprints())
                {
                    errorText.text="Register at least one fingerprint in Settings."
                }
                else{
                    if(!keygaurdManager.isKeyguardSecure)
                    {
                        errorText.text="Lock Screen Security Not Enabled in Settings."
                    }
                    else {
                        generateKey()
                        if(cipherInit())
                        {
                            val cryptoObject=FingerprintManager.CryptoObject(cipher)
                            val helper=FingerprintHandler(this)
                            helper.startAuth(fingerprintManager,cryptoObject)

                        }
                    }
                }
            }
        }
    }

    private fun cipherInit(): Boolean {
        try {
            cipher= Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7)
        }catch (e:NoSuchAlgorithmException)
        {
            throw RuntimeException("Failed to get Cipher",e)
        }
        catch (e:NoSuchPaddingException)
        {
            throw RuntimeException("Failed to get Cipher",e)
        }

        try {
            keyStore.load(null)
            val key=keyStore.getKey(KEY_NAME,null)
            cipher.init(Cipher.ENCRYPT_MODE,key)
            return true
        }catch (e:KeyPermanentlyInvalidatedException)
        {
            return false
        }
        catch (e:KeyStoreException)
        {
            throw RuntimeException("Failed to init Cipher",e)
        }
        catch (e:CertificateException)
        {
            throw RuntimeException("Failed to init Cipher",e)
        }
        catch (e:UnrecoverableKeyException)
        {
            throw RuntimeException("Failed to init Cipher",e)
        }

        catch (e:IOException)
        {
            throw RuntimeException("Failed to init Cipher",e)
        }

        catch (e:NoSuchAlgorithmException)
        {
            throw RuntimeException("Failed to init Cipher",e)
        }

        catch (e:InvalidKeyException)
        {
            throw RuntimeException("Failed to init Cipher",e)
        }

    }

    private fun generateKey() {
        try {
            keyStore= KeyStore.getInstance("AndroidKeyStore")
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        val keyGenerator:KeyGenerator
        try {
            keyGenerator= KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore")
        }catch (e:NoSuchAlgorithmException)
        {
            throw RuntimeException("Failed To get KeyGenerator instance",e)
        }
        catch (e:NoSuchProviderException)
        {
            throw RuntimeException("Failed To get KeyGenerator instance",e)
        }
        try {
            keyStore.load(null)
            keyGenerator.init(KeyGenParameterSpec.Builder(KEY_NAME,KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build())
            keyGenerator.generateKey()
        }catch (e:NoSuchAlgorithmException)
        {
            throw RuntimeException(e)
        }
        catch (e:InvalidAlgorithmParameterException)
        {
            throw RuntimeException(e)
        }
        catch (e:CertificateException)
        {
            throw RuntimeException(e)
        }
        catch (e: IOException)
        {
            throw RuntimeException(e)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==1234)
        {
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.USE_FINGERPRINT)==PackageManager.PERMISSION_GRANTED)
            {
                errorText.text="Fingerprint authentication permission not enabled"
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
