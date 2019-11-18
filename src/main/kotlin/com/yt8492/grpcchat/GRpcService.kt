package com.yt8492.grpcchat

import com.yt8492.grpcchat.protobuf.ChatServiceGrpc
import com.yt8492.grpcchat.protobuf.Empty
import com.yt8492.grpcchat.protobuf.MessageRequest
import com.yt8492.grpcchat.protobuf.MessageResponse
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import java.util.concurrent.ConcurrentHashMap

@GRpcService
class GRpcService : ChatServiceGrpc.ChatServiceImplBase() {

    private val observers = ConcurrentHashMap.newKeySet<StreamObserver<MessageResponse>?>()

    override fun execStream(
            responseObserver: StreamObserver<MessageResponse>?
    ): StreamObserver<MessageRequest> {
        observers.add(responseObserver)
        return object : StreamObserver<MessageRequest> {
            override fun onNext(value: MessageRequest?) {
                println(value?.message)
                val res = MessageResponse.newBuilder()
                        .setMessage(value?.message)
                        .build()
                observers.forEach {
                    it?.onNext(res)
                }
            }

            override fun onError(t: Throwable?) {
                t?.printStackTrace()
                observers.remove(responseObserver)
                responseObserver?.onCompleted()
            }

            override fun onCompleted() {
                println("onCompleted")
            }
        }
    }

    override fun healthCheck(request: Empty?, responseObserver: StreamObserver<Empty>?) {
        responseObserver?.onNext(request)
    }
}