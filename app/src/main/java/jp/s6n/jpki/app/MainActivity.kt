package jp.s6n.jpki.app

import android.app.ProgressDialog
import android.net.Uri
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import jp.s6n.jpki.app.databinding.ActivityMainBinding
import jp.s6n.jpki.app.jpki.ClientForAuth
import jp.s6n.jpki.app.jpki.NfcCard
import java.nio.ByteBuffer

fun ByteArray.toHex(): String =
    joinToString(separator = " ") { eachByte -> "%02x".format(eachByte) }

fun ByteArray.toBuffer(): ByteBuffer {
    val buffer = ByteBuffer.allocateDirect(this.size)
    buffer.put(this)

    return buffer
}

fun ByteBuffer.toArray(): ByteArray {
    val array = ByteArray(this.remaining())
    this.get(array)

    return array
}

class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var nfcCard: NfcCard

    private val openDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            uri = it
            startTagDiscovery()
        }

    private val createDocumentLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument()) {
            uri = it
            startTagDiscovery()
        }

    private var isVerifyMode = false
    private var uri: Uri? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onTagDiscovered(tag: Tag?) {
        NfcAdapter.getDefaultAdapter(this).disableReaderMode(this)

        val str = tag?.techList?.joinToString(",") ?: return
        Log.d("JPKI.Nfc.Tag", str)

        runOnUiThread {
            progressDialog?.cancel()
            progressDialog = ProgressDialog(this).apply {
                setTitle(R.string.app_name)
                setMessage(if (isVerifyMode) {
                    "Verifying the signature..."
                } else {
                    "Signing to the message..."
                })
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
                show()
            }
        }

        val isoDep = IsoDep.get(tag)?.apply {
            timeout = 30000
            connect()
        } ?: return

        nfcCard = NfcCard {
            try {
                val bytes = ByteArray(it.remaining())
                it.get(bytes)
                Log.d("JPKI.Apdu.Command", bytes.toHex())

                val response = isoDep.transceive(bytes)
                val buffer = response.toBuffer()
                Log.d("JPKI.Apdu.Response", response.toHex())

                buffer
            } catch (e: Throwable) {
                Log.e("JPKI.Apdu.Error", e.message.toString())

                ByteBuffer.allocate(0)
            }
        }

        val client = ClientForAuth(nfcCard)
        val messageBytes = binding.inputMessage.text.toString().toByteArray()

        if (isVerifyMode) {
            val signature = uri
                ?.let { u -> contentResolver.openInputStream(u) }
                ?.use { stream -> stream.readBytes() }
                ?: return

            val result = if (client.verify(messageBytes, signature)) {
                "OK"
            } else {
                "NG"
            }

            runOnUiThread {
                progressDialog?.cancel()

                Toast
                    .makeText(this, "Verification Result: $result", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            val signature = client.sign(binding.inputPin.text.toString(), messageBytes).toArray()
            Log.d("JPKI.Signature", signature.toHex())

            uri
                ?.let { u -> contentResolver.openOutputStream(u) }
                ?.use {
                    it.write(signature)
                    it.flush()
                }

            runOnUiThread {
                progressDialog?.cancel()

                Toast
                    .makeText(this, "Successfully signed to the document.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        client.close()
        isoDep.close()
    }

    fun startSign(v: View) {
        isVerifyMode = false
        createDocumentLauncher.launch("signed.sig")
    }

    fun startVerify(v: View) {
        isVerifyMode = true
        openDocumentLauncher.launch(arrayOf("*/*"))
    }

    private fun startTagDiscovery() {
        runOnUiThread {
            progressDialog?.cancel()
            progressDialog = ProgressDialog(this).apply {
                setTitle(R.string.app_name)
                setMessage("Waiting for your card...")
                setProgressStyle(ProgressDialog.STYLE_SPINNER)
                show()
            }
        }

        NfcAdapter.getDefaultAdapter(this).enableReaderMode(
            this@MainActivity,
            this,
            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                    NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B,
            null
        )
    }
}
