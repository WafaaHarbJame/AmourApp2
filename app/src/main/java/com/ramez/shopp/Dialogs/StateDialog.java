package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Adapter.StatesAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.AreasModel;
import com.ramez.shopp.Models.AreasResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class StateDialog extends Dialog {

    List<AreasModel> areasModels;
    RecyclerView rv;
    TextView okBtn;
    TextView closeBut;
    TextView searchTxt;
    Activity activity;
    int countryId;
    private int selectedPos;
    private int selectedCountry;
    private AreasModel selectedCountryModel;
    private int countryCode;
    private LinearLayoutManager linearLayoutManager;
    private StatesAdapter statesAdapter;
    private DataFetcherCallBack dataFetcherCallBack;
    private LinearLayout loadingProgressLY;

    public StateDialog(Context context, int countryCode, final DataFetcherCallBack dataFetcherCallBack) {
        super(context);
        activity = (Activity) context;
        this.dataFetcherCallBack = dataFetcherCallBack;
        this.countryCode = countryCode;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_state);

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//        getWindow().setGravity(Gravity.BOTTOM);

        setCancelable(false);
        areasModels = new ArrayList<>();
        rv = findViewById(R.id.rv);
        okBtn = findViewById(R.id.okBtn);
        closeBut = findViewById(R.id.closeBtn);
        searchTxt = findViewById(R.id.searchTxt);
        linearLayoutManager = new LinearLayoutManager(activity);
        rv.setLayoutManager(linearLayoutManager);
        rv.hasFixedSize();
        LocalModel localModel = UtilityApp.getLocalData();
        countryId = localModel.getCountryId();
        loadingProgressLY = findViewById(R.id.loadingLY);

        GetAreas(countryId);


        try {
            if (activity != null && !activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }


        okBtn.setOnClickListener(view -> {
            if(selectedCountryModel==null){

                Toast.makeText(context, ""+context.getString(R.string.select_area), Toast.LENGTH_SHORT).show();
            }
            else {

                if (dataFetcherCallBack != null) {
                    dataFetcherCallBack.Result(selectedCountryModel, Constants.success, true);
                }

                dismiss();
            }



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
                List<AreasModel> countriesList = new ArrayList<>();
                for (int i = 0; i < areasModels.size(); i++) {
                    AreasModel countryModel = areasModels.get(i);

                    if (countryModel.getNameAe().toLowerCase().contains(searchStr) || countryModel.getNameEn().toString().contains(searchStr))
                        countriesList.add(countryModel);
                    initAdapter(countriesList);


                }


            }
        });


    }


    public void initAdapter(List<AreasModel> countries) {
        statesAdapter = new StatesAdapter(activity, countries, countryCode, (obj, func, IsSuccess) -> {
            selectedCountryModel = (AreasModel) obj;
            selectedCountry = selectedCountryModel.getId();
        });
        rv.setAdapter(statesAdapter);

    }


    public void GetAreas(int country_id) {
        loadingProgressLY.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            rv.setVisibility(View.VISIBLE);
            loadingProgressLY.setVisibility(View.GONE);

            AreasResultModel result = (AreasResultModel) obj;

            if (func.equals(Constants.ERROR)) {


            } else if (func.equals(Constants.FAIL)) {


            } else {
                if (IsSuccess) {
                    if (result.getData() != null && result.getData().size() > 0) {
                        areasModels = result.getData();
                        initAdapter(areasModels);

                    }


                }
            }

        }).GetAreasHandle(country_id);
    }


}
