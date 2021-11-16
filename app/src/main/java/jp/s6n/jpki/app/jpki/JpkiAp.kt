package jp.s6n.jpki.app.jpki

import jp.s6n.jpki.app.ffi.LibJpki
import java.io.Closeable

class JpkiAp(card: Card): Closeable {
    private val ptr = LibJpki.newJpkiAp(card.ptr)

    fun readCertificateSign(pin: String, ca: Boolean = false) =
        LibJpki.jpkiApReadCertificateSign(ptr, pin, ca)

    fun readCertificateAuth(ca: Boolean = false) =
        LibJpki.jpkiApReadCertificateAuth(ptr, ca)

    fun auth(pin: String, digest: ByteArray) =
        LibJpki.jpkiApAuth(ptr, pin, digest)

    override fun close() {
        LibJpki.jpkiApClose(ptr)
    }
}
