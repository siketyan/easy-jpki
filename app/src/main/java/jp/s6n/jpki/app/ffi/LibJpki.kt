package jp.s6n.jpki.app.ffi

import jp.s6n.jpki.app.jpki.NfcCardDelegate
import java.nio.ByteBuffer

class LibJpki
{
    companion object {
        @JvmStatic
        external fun init()

        @JvmStatic
        external fun newNfcCard(delegate: NfcCardDelegate): Long

        @JvmStatic
        external fun newCard(delegate: Long): Long

        @JvmStatic
        external fun newJpkiAp(card: Long): Long

        @JvmStatic
        external fun jpkiApReadCertificateAuth(jpkiAp: Long, ca: Boolean): ByteBuffer

        @JvmStatic
        external fun jpkiApReadCertificateSign(jpkiAp: Long, pin: String, ca: Boolean): ByteBuffer

        @JvmStatic
        external fun jpkiApAuth(jpkiAp: Long, pin: String, digest: ByteArray): ByteBuffer

        @JvmStatic
        external fun jpkiApClose(jpkiAp: Long)

        @JvmStatic
        external fun newClientForAuth(delegate: Long): Long

        @JvmStatic
        external fun clientForAuthSign(jpkiAp: Long, pin: String, message: ByteArray): ByteBuffer

        @JvmStatic
        external fun clientForAuthVerify(jpkiAp: Long, message: ByteArray, signature: ByteArray): Boolean

        @JvmStatic
        external fun clientForAuthClose(client: Long)

        init {
            System.loadLibrary("jpki")
            init()
        }
    }
}
