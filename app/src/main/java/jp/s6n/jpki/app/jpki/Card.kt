package jp.s6n.jpki.app.jpki

import jp.s6n.jpki.app.ffi.LibJpki

class Card(nfcCard: NfcCard) {
    val ptr = LibJpki.newCard(nfcCard.ptr)
}
