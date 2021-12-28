package com.bonhams.expensemanagement.data.services

import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class TLSSocketFactory : SSLSocketFactory() {

    private val socketFactory: SSLSocketFactory

    init {
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, null, null)
        socketFactory = sslContext.socketFactory
    }

    override fun getDefaultCipherSuites(): Array<String> {
        return socketFactory.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return socketFactory.supportedCipherSuites
    }

    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        return configure(socketFactory.createSocket(s, host, port, autoClose) as SSLSocket)
    }

    override fun createSocket(host: String, port: Int): Socket {
        return configure(socketFactory.createSocket(host, port) as SSLSocket)
    }

    override fun createSocket(host: InetAddress, port: Int): Socket {
        return configure(socketFactory.createSocket(host, port) as SSLSocket)
    }

    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
        return configure(socketFactory.createSocket(host, port, localHost, localPort) as SSLSocket)
    }

    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        return configure(socketFactory.createSocket(address, port, localAddress, localPort) as SSLSocket)
    }

    private fun configure(socket: SSLSocket): SSLSocket {
        socket.enabledProtocols = socket.supportedProtocols
        socket.enabledCipherSuites = socket.supportedCipherSuites
        return socket
    }
}
