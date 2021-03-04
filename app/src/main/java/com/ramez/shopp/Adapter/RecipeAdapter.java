package com.ramez.shopp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.Recipe;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowRecipeBinding;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> implements SuggestedProductAdapter.OnItemClick {
    private Activity activity;
    private List<Recipe> list;
    private DataFetcherCallBack dataFetcherCallBack;
    int selectRecipe;
    ArrayList<ProductModel> productList;
    SuggestedProductAdapter adapter;
    int country_id, city_id, userId = 0;
    private boolean toggleButton = false;


    public RecipeAdapter(Activity activity, List<Recipe> list, int selectRecipe, DataFetcherCallBack dataFetcherCallBack) {
        this.activity = activity;
        this.list = list;
        this.dataFetcherCallBack = dataFetcherCallBack;
        this.selectRecipe = selectRecipe;
        productList = new ArrayList<>();
        country_id = UtilityApp.getLocalData().getCountryId();
        city_id = Integer.parseInt(UtilityApp.getLocalData().getCityId());

        if (UtilityApp.getUserData() != null) {
            userId = UtilityApp.getUserData().getId();

        }
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowRecipeBinding itemView = RowRecipeBinding.inflate(LayoutInflater.from(activity), parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = list.get(position);
        holder.binding.nameTxt.setText(recipe.getDescription());

        LinearLayoutManager llm = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
        holder.binding.rv.setLayoutManager(llm);
        holder.binding.rv.setHasFixedSize(true);
        holder.binding.rv.setItemAnimator(null);

        adapter = new SuggestedProductAdapter(activity, productList, this, productList.size());
        holder.binding.rv.setAdapter(adapter);


        if (selectRecipe == recipe.getId()) {
            holder.binding.toggleBut.setText(activity.getString(R.string.fa_angle_up));
            holder.binding.selectTxt.setText(activity.getString(R.string.fa_check));
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.green));
        } else {
            holder.binding.selectTxt.setText("");
            holder.binding.toggleBut.setText(activity.getString(R.string.fa_angle_down));
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.header3));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onItemClicked(int position, ProductModel productModel) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowRecipeBinding binding;

        public ViewHolder(RowRecipeBinding view) {
            super(view.getRoot());
            binding = view;


            itemView.setOnClickListener(v -> {

                Recipe recipe = list.get(getAdapterPosition());
                selectRecipe = recipe.getId();
                toggleButton = !toggleButton;

                if (toggleButton) {

                    getProductRecipeList(binding, recipe, recipe.getId(), country_id, city_id, String.valueOf(userId));
                    binding.toggleBut.setText(activity.getString(R.string.fa_angle_up));
                    binding.rv.setVisibility(View.VISIBLE);
                    binding.selectTxt.setText(activity.getString(R.string.fa_check));
                    binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.green));


                } else {
                    binding.toggleBut.setText(activity.getString(R.string.fa_angle_down));
                    binding.rv.setVisibility(View.GONE);
                    binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.header3));
                    binding.selectTxt.setText("");


                }

                if (dataFetcherCallBack != null) {
                    dataFetcherCallBack.Result(recipe, Constants.success, true);
                }


            });

        }


    }

    private void getProductRecipeList(RowRecipeBinding binding, Recipe recipe, int recipe_id,
                                      int country_id, int city_id, String user_id) {
        productList.clear();
        binding.progressBar1.setVisibility(View.VISIBLE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            binding.progressBar1.setVisibility(View.GONE);

            ResultAPIModel<ArrayList<ProductModel>> result = (ResultAPIModel<ArrayList<ProductModel>>) obj;

            if (IsSuccess) {
                productList = result.data;
                if (result.data != null && result.data.size() > 0) {
                    productList = result.data;

                    adapter = new SuggestedProductAdapter(activity, productList, this, productList.size());

                    binding.rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();



                }
                else {
                    binding.toggleBut.setText(activity.getString(R.string.fa_angle_down));
                    binding.rv.setVisibility(View.GONE);
                    binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.header3));
                    binding.selectTxt.setText("");

                }

            }


        }).getProductRecipeList(recipe_id, country_id, city_id, user_id, 0, 10);
    }


}