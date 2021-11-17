package jp.s6n.jpki.app.jpki

import jp.s6n.jpki.app.ffi.LibJpki
import jp.s6n.jpki.app.ffi.errorChecked

class Card(nfcCard: NfcCard) {
    val ptr = LibJpki.newCard(nfcCard.ptr).errorChecked()
}
