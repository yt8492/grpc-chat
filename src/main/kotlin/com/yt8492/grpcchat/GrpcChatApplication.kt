package com.yt8492.grpcchat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrpcChatApplication

fun main(args: Array<String>) {
    runApplication<GrpcChatApplication>(*args)
}
