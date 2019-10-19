package com.yt8492.grpcchat

import com.yt8492.grpcchat.protobuf.ChatServiceGrpc
import com.yt8492.grpcchat.protobuf.MessageRequest
import com.yt8492.grpcchat.protobuf.MessageResponse
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService

@GRpcService
class GRpcService : ChatServiceGrpc.ChatServiceImplBase() {
    override fun execStream(
            responseObserver: StreamObserver<MessageResponse>?
    ): StreamObserver<MessageRequest> =
            object : StreamObserver<MessageRequest> {
                override fun onNext(value: MessageRequest?) {
                    val res = MessageResponse.newBuilder()
                            .setMessage(value?.message)
                            .build()
                    responseObserver?.onNext(res)
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                }

                override fun onCompleted() {
                    responseObserver?.onCompleted()
                }
            }
}