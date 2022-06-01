package com.amour.shop.activities

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.amour.shop.Models.CreditCardModel
import com.amour.shop.R
import com.amour.shop.Utils.DateHandler
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.classes.Constants
import com.amour.shop.databinding.ActivityPayUsingCardBinding
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class PayUsingCardActivity : ActivityBase() {
    lateinit var binding: ActivityPayUsingCardBinding
    var currentMonth: String? = null
    var currentYear: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayUsingCardBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)



        currentMonth= DateHandler.GetMonthOnlyString()
        currentYear=DateHandler.GetYearOnlyString()

        Log.i("tag","Log currentMonth ${currentMonth}")
        Log.i("tag","Log currentYear ${currentYear}")
//        currentMonth=  if (currentMonth.startsWith("0")) currentMonth.replaceFirst(
//            "0",
//            ""
//        ) else currentMonth

        initListeners()



    }

    private fun initListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.btnDone.setOnClickListener {
            try {
                val monthStr = NumberHandler.arabicToDecimal(binding.sprmonth.text.toString())
                val yearStr = NumberHandler.arabicToDecimal(binding.spryear.text.toString())
                val cardNumberStr = NumberHandler.arabicToDecimal(binding.edtCardNumber.text.toString())
                val cvvStr = NumberHandler.arabicToDecimal(binding.edtCvvNumber.text.toString())

                var hasError = false

//                monthStr=  if (monthStr.startsWith("0")) monthStr.replaceFirst(
//                    "0",
//                    ""
//                ) else monthStr

                if (monthStr.isEmpty()) {
                    binding.sprmonth.error = getString(R.string.invalid_input)
                    hasError = true
                }

                if (yearStr.isEmpty()) {
                    binding.spryear.error = getString(R.string.invalid_input)
                    hasError = true
                }

                if (cardNumberStr.isEmpty() || cardNumberStr.length <14) {
                    binding.edtCardNumber.error = getString(R.string.invalid_input)
                    hasError = true
                }

                if (cvvStr.isEmpty() ||cvvStr.length <3) {
                    binding.edtCvvNumber.error = getString(R.string.invalid_input)
                    hasError = true
                }

                if (yearStr.toInt() < currentYear?.toInt()?:0) {
                    binding.spryear.error = getString(R.string.invalid_input)
                    hasError = true
                }

                if (monthStr.toInt() > 12) {
                    binding.sprmonth.error = getString(R.string.invalid_input)
                    hasError = true
                }

                Log.i("tag","Log yearStr.toInt() ${yearStr.toInt()}")

                if (hasError)
                    return@setOnClickListener

                val cardModel = CreditCardModel()
                cardModel.number = cardNumberStr
                cardModel.month = monthStr
                cardModel.year = yearStr
                cardModel.securityCode = cvvStr

                sendData(cardModel)



            } catch (e: Exception) {
                e.printStackTrace()

            }

        }
    }

    private fun sendData(cardModel: CreditCardModel) {
        val gson = Gson()
        val json = gson.toJson(cardModel)
//        val EncryptedString: String = Encrypt("{\"number\":\"0123456789123456\",\"month\":\"12\",\"year\":\"22\",\"securityCode\":\"123\"}")
        val EncryptedString: String = Encrypt(json)
        Log.i(javaClass.name,"Log [EncryptedString]:$EncryptedString")
        Log.i(javaClass.name,"Log  [json]:$json")
        Log.i(javaClass.name, "Log EncryptedString$EncryptedString")
        val intent = Intent()
        intent.putExtra(Constants.PAY_TOKEN,EncryptedString)
        intent.putExtra(Constants.CODE,1111)
        setResult(RESULT_OK, intent)
        finish()


    }


    @Throws(java.lang.Exception::class)
    fun Encrypt(text: String?): String {
        val ivkey = "CDDAMQOTMYIAZEPQ"
        val key: String? = generateRandomPassword(32)
//        key="h3tTWAYJ55EGWMgZFs5gW5mquCIsgLhE";
        val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val keyBytes = ByteArray(32)
        var b = key?.toByteArray(charset("UTF-8"))
        var len = b?.size?:0
        if (len > keyBytes.size) len = keyBytes.size
        System.arraycopy(b, 0, keyBytes, 0, len)
        val ivBytes = ByteArray(16)
        b = ivkey.toByteArray(charset("UTF-8"))
        len = b.size
        if (len > ivBytes.size) len = ivBytes.size
        System.arraycopy(b, 0, ivBytes, 0, len)
        val keySpec = SecretKeySpec(keyBytes, "AES")
        val ivSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        val results: ByteArray = cipher.doFinal(text?.toByteArray(charset("UTF-8")))
        val encryptedString: String =  Base64.encodeToString(results, Base64.DEFAULT).trimEnd()
        return (encryptedString.substring(0, encryptedString.length / 2)
                + key
                + encryptedString.substring(encryptedString.length / 2))
    }

    fun generateRandomPassword(len: Int?): String {
        val chars = ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                + "lmnopqrstuvwxyz+/")
        val rnd = Random()
        val sb = StringBuilder(len!!)
        for (i in 0 until len) sb.append(chars[rnd.nextInt(chars.length)])
        return sb.toString()
    }

}