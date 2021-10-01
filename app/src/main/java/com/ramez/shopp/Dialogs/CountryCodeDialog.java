package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.adapter.CountryCodeAdapter;
import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.Models.CountryModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;

import java.util.ArrayList;
import java.util.List;

public class CountryCodeDialog extends Dialog {

    List<CountryModel> countries;
    RecyclerView rv;
    Button okBtn;
    TextView closeBut;
    EditText searchTxt;
    Activity activity;
    private int selectedPos;
    private int selectedCountry;
    private CountryModel selectedCountryModel;
    private int countryCode;
    private LinearLayoutManager linearLayoutManager;
    private CountryCodeAdapter countriesAdapter;
    private DataFetcherCallBack dataFetcherCallBack;

    public CountryCodeDialog(Context context, int countryCode, final DataFetcherCallBack dataFetcherCallBack) {
        super(context);
        activity = (Activity) context;
        this.dataFetcherCallBack = dataFetcherCallBack;
        this.countryCode = countryCode;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_country_code);
        setCancelable(false);
        countries = new ArrayList<>();
        rv = findViewById(R.id.rv);
        okBtn = findViewById(R.id.okBtn);
        closeBut = findViewById(R.id.closeBtn);
        searchTxt = findViewById(R.id.searchTxt);
        linearLayoutManager = new LinearLayoutManager(activity);
        rv.setLayoutManager(linearLayoutManager);
        rv.hasFixedSize();


        countries.add(new CountryModel(4,context.getString(R.string.Oman_ar), context.getString(R.string.Oman), context.getString(R.string.oman_shotname), 968, "OMR", Constants.three, R.drawable.ic_flag_oman));
        countries.add(new CountryModel(17, context.getString(R.string.Bahrain_ar),context.getString(R.string.Bahrain), context.getString(R.string.bahrain_shotname), 973, "BHD", Constants.three, R.drawable.ic_flag_behrain));
        countries.add(new CountryModel(117,context.getString(R.string.Kuwait_ar), context.getString(R.string.Kuwait), context.getString(R.string.Kuwait_shotname), 965, "KWD", Constants.three, R.drawable.ic_flag_kuwait));
        countries.add(new CountryModel(178,context.getString(R.string.Qatar_ar), context.getString(R.string.Qatar), context.getString(R.string.Qatar_shotname), 974, "QAR", Constants.two, R.drawable.ic_flag_qatar));
        countries.add(new CountryModel(191, context.getString(R.string.Saudi_Arabia_ar),context.getString(R.string.Saudi_Arabia), context.getString(R.string.Saudi_Arabia_shortname), 966, "SAR", Constants.two, R.drawable.ic_flag_saudi_arabia));
        countries.add(new CountryModel(229,context.getString(R.string.United_Arab_Emirates_ar), context.getString(R.string.United_Arab_Emirates), context.getString(R.string.United_Arab_Emirates_shotname), 971, "AED", Constants.two, R.drawable.ic_flag_uae));

        initAdapter(countries);


        try {
            if (activity != null && !activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }


        okBtn.setOnClickListener(view -> {

            if (dataFetcherCallBack != null) {
                dataFetcherCallBack.Result(selectedCountryModel, Constants.success, true);
            }
            dismiss();

        });


        closeBut.setOnClickListener(view -> {
            dismiss();

        });


        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchStr = NumberHandler.arabicToDecimal(s.toString());
                List<CountryModel> countriesList = new ArrayList<>();
                for (int i = 0; i < countries.size(); i++) {
                    CountryModel countryModel = countries.get(i);

                    if (countryModel.getCountryName().toLowerCase().contains(searchStr) || countryModel.getPhonecode().toString().contains(searchStr))
                        countriesList.add(countryModel);
                    initAdapter(countriesList);


                }


            }
        });


    }


    public void initAdapter(List<CountryModel> countries) {
        countriesAdapter = new CountryCodeAdapter(activity, countries, countryCode, new DataFetcherCallBack() {
            @Override
            public void Result(Object obj, String func, boolean IsSuccess) {
                selectedCountryModel = (CountryModel) obj;
                selectedCountry = selectedCountryModel.getId();
            }
        });
        rv.setAdapter(countriesAdapter);
        //  rv.scrollToPosition(selectedPos);

    }


}
