package com.amour.shop.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.amour.shop.classes.Constants
import com.amour.shop.databinding.ActivityScanBarcodeBinding

class FullScannerActivity : ActivityBase() {
    private lateinit var codeScanner: CodeScanner
    var binding: ActivityScanBarcodeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBarcodeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        title = ""
        val scannerView = binding!!.scannerView

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)

        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not


        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {

                Log.i(javaClass.name,"Scan result: ${it.text}")
                Log.i(javaClass.name,"Scan barcodeFormat : ${it.barcodeFormat}")

                val intent = Intent()
                intent.putExtra(Constants.SEARCH_BY_CODE_byCode, true)
                intent.putExtra(Constants.CODE,it.text)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast("Camera initialization error: ${it.message}")

            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}