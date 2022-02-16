//package com.ramez.shopp.activities;
//
//import static com.ramez.shopp.classes.JavaApplication1.generateRandomPassword;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.google.gson.Gson;
//import com.ramez.shopp.R;
//import com.ramez.shopp.Utils.DateHandler;
//import com.ramez.shopp.Utils.NumberHandler;
//import com.ramez.shopp.classes.CardInformation;
//import com.ramez.shopp.classes.Constants;
//import com.ramez.shopp.databinding.ActivityPayUsingCardBinding;
//
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Base64;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//
// public class PayUsingCardActivity extends ActivityBase {
//
//        ActivityPayUsingCardBinding binding;
//    String currentMonth, currentYear;
//
//
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityPayUsingCardBinding.inflate(this.getLayoutInflater());
//        this.setContentView(binding.getRoot());
//        currentMonth = DateHandler.GetMonthOnlyString();
//        currentYear = DateHandler.GetYearOnlyString();
//        Log.i("tag", "Log currentMonth " + currentMonth);
//        Log.i("tag", "Log currentYear " + currentYear);
//        binding.btnDone.setOnClickListener(v -> {
//
//
//            try {
//                String monthStr = NumberHandler.arabicToDecimal(binding.sprmonth.getText().toString());
//                String yearStr = NumberHandler.arabicToDecimal(binding.spryear.getText().toString());
//                String cardNumberStr = NumberHandler.arabicToDecimal(binding.edtCardNumber.getText().toString());
//                String cvvStr = NumberHandler.arabicToDecimal(binding.edtCvvNumber.getText().toString());
//
//                boolean hasError = false;
//
//
//                if (monthStr.isEmpty()) {
//                    binding.sprmonth.setError(getString(R.string.invalid_input));
//                    hasError = true;
//                }
//
//                if (yearStr.isEmpty()) {
//                    binding.spryear.setError(getString(R.string.invalid_input));
//                    hasError = true;
//                }
//
//                if (cardNumberStr.isEmpty() || cardNumberStr.length() < 14) {
//                    binding.edtCardNumber.setError(getString(R.string.invalid_input));
//                    hasError = true;
//                }
//
//                if (cvvStr.isEmpty() || cvvStr.length() < 3) {
//                    binding.edtCvvNumber.setError(getString(R.string.invalid_input));
//                    hasError = true;
//                }
//
//                if (Integer.parseInt(yearStr) < Integer.parseInt(currentYear)) {
//                    binding.spryear.setError(getString(R.string.invalid_input));
//                    hasError = true;
//                }
//
//                if (Integer.parseInt(monthStr) > 12) {
//                    binding.sprmonth.setError(getString(R.string.invalid_input));
//                    hasError = true;
//                }
//
//                Log.i("tag", "Log yearStr.toInt() "+Integer.parseInt(yearStr));
//
//                if (hasError)
//                    return;
//
//                CardInformation c = new CardInformation();
//                c.number = cardNumberStr;
//                c.month = monthStr;
//                c.year = yearStr;
//                c.securityCode = cvvStr;
//                sendData(c);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//
//            }
//
//        });
//
//
//    }
//
//    private void sendData(CardInformation cardModel) {
//        Gson gson = new Gson();
//        String json = gson.toJson(cardModel);
//        String EncryptedString = null;
//        try {
//           // EncryptedString = Encrypt("{\"number\":\"0123456789123456\",\"month\":\"12\",\"year\":\"22\",\"securityCode\":\"123\"}");
//            EncryptedString = Encrypt(json);
//            String var5 = "Log [EncryptedString]: " + EncryptedString;
//            System.out.println(var5);
//            var5 = "Log  [json]: " + json;
//            System.out.println(var5);
//            Intent intent = new Intent();
//            intent.putExtra(Constants.PAY_TOKEN, EncryptedString);
//            setResult(-1, intent);
//            finish();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//    public static String Encrypt(String text)
//            throws Exception {
//        String ivkey = "CDDAMQOTMYIAZEPQ";
////        String key = "h3tTWAYJ55EGWMgZFs5gW5mquCIsgLhE";
//          String key = generateRandomPassword(32);
//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        byte[] keyBytes = new byte[32];
//        byte[] b = key.getBytes("UTF-8");
//        int len = b.length;
//        if (len > keyBytes.length) len = keyBytes.length;
//        System.arraycopy(b, 0, keyBytes, 0, len);
//
//        byte[] ivBytes = new byte[16];
//        b = ivkey.getBytes("UTF-8");
//        len = b.length;
//        if (len > ivBytes.length) len = ivBytes.length;
//        System.arraycopy(b, 0, ivBytes, 0, len);
//
//        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
//
//        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
//        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
//
//        byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
//        @SuppressLint({"NewApi", "LocalSuppress"})
//        String EncryptedString = Base64.getEncoder().encodeToString(results);
//        return EncryptedString.substring(0, EncryptedString.length() / 2)
//                + key
//                + EncryptedString.substring(EncryptedString.length() / 2);
//    }
//}
