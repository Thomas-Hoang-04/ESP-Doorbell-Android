package com.thomas.doorbell.keystore

import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserDataSerializer @Inject constructor(
    private val cryptoManager: CryptoManager,
): Serializer<UserData> {
    override val defaultValue: UserData = UserData()

    override suspend fun readFrom(input: InputStream): UserData {
        return try {
            val decryptedData = cryptoManager.decrypt(input.readBytes().decodeToString())
            Json.decodeFromString(
                deserializer = UserData.serializer(),
                string = decryptedData
            )
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: UserData, output: OutputStream) {
        cryptoManager.encrypt(Json.encodeToString(UserData.serializer(), t))
            .also { output.write(it.toByteArray()) }
    }
}