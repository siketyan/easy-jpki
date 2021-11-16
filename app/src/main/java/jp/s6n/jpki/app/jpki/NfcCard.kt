package jp.s6n.jpki.app.jpki

import jp.s6n.jpki.app.ffi.LibJpki

class NfcCard(delegate: NfcCardDelegate) {
    val ptr: Long = LibJpki.newNfcCard(delegate)
}
