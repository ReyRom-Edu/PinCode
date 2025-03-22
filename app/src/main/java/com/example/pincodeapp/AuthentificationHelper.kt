package com.example.pincodeapp

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class AuthentificationHelper(private val context: Context,
                             private val onAuthSuccess: ()->Unit,
                             private val onAuthFailed: ()->Unit) {
    private val biometricManager = BiometricManager.from(context)
    private val correctPin = "1234"

    fun isBiometricAvailable():Boolean{
        return biometricManager.canAuthentificate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authentificateBiometric(){
        val executor = ContextCompat.getMainExecutor(context)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Биометрическая аутентификация")
            .setDescription("Используйте отпечаток пальца или камеру для аутентификации")
            .setNegativeButtonText("Отмена")
            .build()

        val biometricPrompt = BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    onAuthSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onAuthFailed()
                }
            })
        biometricPrompt.authenticate(promptInfo)
    }

    fun authentificate(pinCode:String){
        if (pinCode == correctPin)
            onAuthSuccess()
        else
            onAuthFailed()
    }
}