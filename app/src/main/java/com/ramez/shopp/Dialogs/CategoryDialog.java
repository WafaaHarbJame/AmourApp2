package com.ramez.shopp.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Adapter.CategoryAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.DialogCategoryBinding;
import com.ramez.shopp.databinding.FragmentCategoryBinding;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class CategoryDialog extends Dialog  implements CategoryAdapter.OnItemClick{


    Activity activity;
    ArrayList<CategoryModel> categoryModelList;
    GridLayoutManager gridLayoutManager;
    LocalModel localModel;
    private DialogCategoryBinding binding;
    private CategoryAdapter categoryAdapter;
    private DataFetcherCallBack dataFetcherCallBack;

    public CategoryDialog(Context context,final DataFetcherCallBack dataFetcherCallBack) {
        super(context);

        activity = (Activity) context;
        this.dataFetcherCallBack = dataFetcherCallBack;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        requestWindowFeature(Window.FEATURE_NO_TITLE);

         binding = DialogCategoryBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());

//        getWindow().setDimAmount(0);
//        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        getWindow().setGravity(Gravity.CENTER);

        setCancelable(true);


        gridLayoutManager = new GridLayoutManager(activity, 3);
        binding.catRecycler.setHasFixedSize(true);
        binding.catRecycler.setLayoutManager(gridLayoutManager);
        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(context);
        if(UtilityApp.getCategories()!=null&&UtilityApp.getCategories().size()>0){
            categoryModelList=UtilityApp.getCategories();
            initAdapter();

        }
        else {
            getCategories(Integer.parseInt(localModel.getCityId()));

        }

        binding.swipeDataContainer.setOnRefreshListener(() -> {
            binding.swipeDataContainer.setRefreshing(false);
            getCategories(Integer.parseInt(localModel.getCityId()));

        });


        try {
            if (activity != null && !activity.isFinishing()) show();
        } catch (Exception e) {
            dismiss();
        }


    }

    public void getCategories(int storeId) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {

                CategoryResultModel result = (CategoryResultModel) obj;
                String message = activity.getString(R.string.fail_to_get_data);

                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

                if (func.equals(Constants.ERROR)) {

                    if (result != null) {
                        message = result.getMessage();
                    }
                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);

                } else if (func.equals(Constants.FAIL)) {

                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);


                } else if (func.equals(Constants.NO_CONNECTION)) {
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                    binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                    binding.dataLY.setVisibility(View.GONE);

                } else {
                    if (IsSuccess) {
                        if (result.getData() != null && result.getData().size() > 0) {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            categoryModelList = result.getData();

                            Log.i(TAG, "Log productBestList" + categoryModelList.size());
                            UtilityApp.setCategoriesData(categoryModelList);

                            initAdapter();

                        } else {

                            binding.dataLY.setVisibility(View.GONE);
                            binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);

                        }


                    } else {

                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                        binding.failGetDataLY.failTxt.setText(message);


                    }
                }


        }).GetAllCategories(storeId);
    }

    private void initAdapter() {

        categoryAdapter = new CategoryAdapter(activity, categoryModelList,0, this,true);
        binding.catRecycler.setAdapter(categoryAdapter);

    }

    @Override
    public void onItemClicked(int position, CategoryModel categoryModel) {

        if (dataFetcherCallBack != null) {
            dataFetcherCallBack.Result(categoryModel, Constants.success, true);
        }
        dismiss();

    }
}
