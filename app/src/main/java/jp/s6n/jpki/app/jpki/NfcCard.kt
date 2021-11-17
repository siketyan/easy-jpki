package jp.s6n.jpki.app.jpki

import jp.s6n.jpki.app.ffi.LibJpki
import jp.s6n.jpki.app.ffi.errorChecked

class NfcCard(delegate: NfcCardDelegate) {
    val ptr: Long = LibJpki.newNfcCard(delegate).errorChecked()
}
