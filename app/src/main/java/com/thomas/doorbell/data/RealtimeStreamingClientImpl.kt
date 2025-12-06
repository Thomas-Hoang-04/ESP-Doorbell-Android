package com.thomas.doorbell.data

import io.ktor.client.HttpClient
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close

class RealtimeStreamingClientImpl(
    httpClient: HttpClient
): RealtimeStreamingClient {
    private var session: WebSocketSession? = null

    override suspend fun close() {
        session?.close()
        session = null
    }
}