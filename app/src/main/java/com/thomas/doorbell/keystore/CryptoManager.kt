package com.thomas.doorbell.keystore

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.*
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.inject.Inject

class CryptoManager @Inject constructor() {
    private val keystore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.ENCRYPT_MODE, getPublicKey(), OAEPParam)
    }

    private val decryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
        init(Cipher.DECRYPT_MODE, getPrivateKey(), OAEPParam)
    }

    private fun generateRSAKey() {
        val keyPairGenerator: KeyPairGenerator
                = KeyPairGenerator.getInstance(ALGORITHM, "AndroidKeyStore")

        val keyGenSpec = KeyGenParameterSpec.Builder(
            ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(BLOCK_MODE)
            .setDigests(DIGEST)
            .setEncryptionPaddings(PADDING)
            .setKeySize(2048)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyPairGenerator.initialize(keyGenSpec)
        keyPairGenerator.generateKeyPair()
    }

    private fun getKeyEntry(): KeyStore.PrivateKeyEntry {
        if (!keystore.containsAlias(ALIAS)) {
            generateRSAKey()
        }
        return keystore.getEntry(ALIAS, null) as KeyStore.PrivateKeyEntry
    }

    private fun getPublicKey(): PublicKey {
        val entry = getKeyEntry()
        return entry.certificate.publicKey
    }

    private fun getPrivateKey(): PrivateKey {
        val entry = getKeyEntry()
        return entry.privateKey
    }

    fun encrypt(data: String): String {
        val encrypted = encryptCipher.doFinal(data.encodeToByteArray())
        return Base64.encodeToString(encrypted, Base64.URL_SAFE)
    }

    fun decrypt(data: String): String {
        val decoded = Base64.decode(data, Base64.URL_SAFE)
        return decryptCipher.doFinal(decoded).decodeToString()
    }

    companion object {
        private const val ALIAS = "bike_rental_key"
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_RSA
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_ECB
        private const val DIGEST = KeyProperties.DIGEST_SHA256
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_RSA_OAEP
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private val OAEPParam = OAEPParameterSpec(
            "SHA-256",
            "MGF1",
            MGF1ParameterSpec.SHA1,
            PSource.PSpecified.DEFAULT
        )
    }
}