package jp.s6n.jpki.app.jpki

import java.nio.ByteBuffer

fun interface NfcCardDelegate {
    fun handleApdu(command: ByteBuffer): ByteBuffer
}
