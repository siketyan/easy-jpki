package jp.s6n.jpki.app.jpki

import jp.s6n.jpki.app.ffi.LibJpki
import jp.s6n.jpki.app.ffi.errorChecked
import java.io.Closeable

class ClientForAuth(card: NfcCard): Closeable {
    private val ptr = LibJpki.newClientForAuth(card.ptr).errorChecked()

    fun sign(pin: String, message: ByteArray) =
        LibJpki.clientForAuthSign(ptr, pin, message).errorChecked()

    fun verify(message: ByteArray, signature: ByteArray) =
        LibJpki.clientForAuthVerify(ptr, message, signature).errorChecked()

    override fun close() {
        LibJpki.clientForAuthClose(ptr).errorChecked()
    }
}
