package com.thomas.doorbell.data

interface RealtimeStreamingClient {
    // TODO: Define methods for connecting, disconnecting, and receiving streaming data
    suspend fun close() // Close the connection
}