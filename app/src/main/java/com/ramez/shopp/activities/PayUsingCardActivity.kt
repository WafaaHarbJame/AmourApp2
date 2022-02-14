package com.ramez.shopp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.ramez.shopp.Models.CreditCardModel
import com.ramez.shopp.R
import com.ramez.shopp.Utils.DateHandler
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.classes.Constants
import com.ramez.shopp.databinding.ActivityPayUsingCardBinding
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class PayUsingCardActivity : ActivityBase() {
    lateinit var binding: ActivityPayUsingCardBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayUsingCardBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        var currentMonth: String? = null
        var currentYear: String? = null

        currentMonth= DateHandler.GetMonthOnlyString()
        currentYear=DateHandler.GetYearOnlyString()

        Log.i("tag","Log currentMonth ${currentMonth}")
        Log.i("tag","Log currentYear ${currentYear}")
//        currentMonth=  if (currentMonth.startsWith("0")) currentMonth.replaceFirst(
//            "0",
//            ""
//        ) else currentMonth
        binding.btnDone.setOnClickListener {
            try {
                var monthStr = NumberHandler.arabicToDecimal(binding.sprmonth.text.toString())
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

                if (yearStr.toInt() < currentYear.toInt()) {
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
        val EncryptedString: String = Encrypt(json)
        println("Log [EncryptedString]:$EncryptedString")
        println("Log  [json]:$json")
        val intent = Intent()
        intent.putExtra(Constants.PAY_TOKEN,EncryptedString)
        setResult(RESULT_OK, intent)
        finish()


    }

    @Throws(java.lang.Exception::class)
    fun Encrypt(text: String?): String {
        val ivkey = "CDDAMQOTMYIAZEPQ"
        val key: String? = generateRandomPassword(32)
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
        val EncryptedString: String =  Base64.encodeToString(results,0)
        return (EncryptedString.substring(0, EncryptedString.length / 2)
                + key
                + EncryptedString.substring(EncryptedString.length / 2))
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