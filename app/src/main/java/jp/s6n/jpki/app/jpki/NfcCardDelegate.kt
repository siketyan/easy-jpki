package jp.s6n.jpki.app.jpki

import java.nio.ByteBuffer

fun interface NfcCardDelegate {
    @Suppress("unused")
    fun handleApdu(command: ByteBuffer): ByteBuffer
}
