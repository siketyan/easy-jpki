package jp.s6n.jpki.app.ffi

fun <T> T.errorChecked(): T {
    val error = LibJpki.lastError()
    if (!error.isNullOrEmpty()) {
        throw FfiException(error)
    }

    return this
}
