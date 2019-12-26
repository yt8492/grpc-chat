package com.yt8492.grpcchat

import com.yt8492.grpcchat.protobuf.ChatServiceCoroutineGrpc
import com.yt8492.grpcchat.protobuf.Empty
import com.yt8492.grpcchat.protobuf.MessageRequest
import com.yt8492.grpcchat.protobuf.MessageResponse
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import org.lognet.springboot.grpc.GRpcService
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@GRpcService
class ChatServiceImpl : ChatServiceCoroutineGrpc.ChatServiceImplBase() {

    private val channels = ConcurrentHashMap.newKeySet<SendChannel<MessageResponse>>()

    override val initialContext: CoroutineContext
        get() = Dispatchers.Default + SupervisorJob()

    override suspend fun execStream(
            requestChannel: ReceiveChannel<MessageRequest>,
            responseChannel: SendChannel<MessageResponse>
    ) {
        channels.add(responseChannel)
        requestChannel.consumeEach {
            val res = MessageResponse {
                message = it.message
            }
            channels.forEach {
                try {
                    it.send(res)
                } catch (e: StatusRuntimeException) {
                    channels.remove(responseChannel)
                }
            }
        }
    }

    override suspend fun healthCheck(request: Empty): Empty {
        return request
    }
}