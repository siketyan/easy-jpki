package jp.s6n.jpki.app.jpki

import jp.s6n.jpki.app.ffi.LibJpki
import jp.s6n.jpki.app.ffi.errorChecked
import java.io.Closeable

class JpkiAp(card: Card): Closeable {
    private val ptr = LibJpki.newJpkiAp(card.ptr).errorChecked()

    fun readCertificateSign(pin: String, ca: Boolean = false) =
        LibJpki.jpkiApReadCertificateSign(ptr, pin, ca).errorChecked()

    fun readCertificateAuth(ca: Boolean = false) =
        LibJpki.jpkiApReadCertificateAuth(ptr, ca).errorChecked()

    fun auth(pin: String, digest: ByteArray) =
        LibJpki.jpkiApAuth(ptr, pin, digest).errorChecked()

    override fun close() {
        LibJpki.jpkiApClose(ptr).errorChecked()
    }
}
