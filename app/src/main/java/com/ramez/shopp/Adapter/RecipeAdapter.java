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
import com.ramez.shopp.Models.LocalModel;
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
    SuggestedProductAdapter adapter;
    int country_id, city_id, userId = 0;
    LocalModel localModel;


    public RecipeAdapter(Activity activity, List<Recipe> list, int selectedPosition, DataFetcherCallBack dataFetcherCallBack) {
        this.activity = activity;
        this.list = list;
        this.dataFetcherCallBack = dataFetcherCallBack;

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(activity);
        country_id = localModel.getCountryId();
        city_id = Integer.parseInt(localModel.getCityId());

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

        holder.binding.selectTxt.setText(activity.getString(R.string.fa_check));
        holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.green));

        if (recipe.isOpen) {
            // to open and change header icon
            holder.binding.toggleBut.setText(activity.getString(R.string.fa_angle_up));
            holder.binding.selectTxt.setVisibility(View.VISIBLE);

            if (recipe.isLoaded) {
                // to check if products loaded before
                adapter = new SuggestedProductAdapter(activity, recipe.productsList, this, 0);
                holder.binding.rv.setAdapter(adapter);

                holder.binding.progressBar1.setVisibility(View.GONE);
                holder.binding.noProductsTv.setVisibility(View.GONE);
                holder.binding.rv.setVisibility(View.VISIBLE);

            } else {
                // if products not loaded
                getProductRecipeList(holder.binding, recipe, country_id, city_id, String.valueOf(userId));
            }

        } else {
            holder.binding.toggleBut.setText(activity.getString(R.string.fa_angle_down));
            holder.binding.selectTxt.setVisibility(View.GONE);
            holder.binding.rv.setVisibility(View.GONE);
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


            LinearLayoutManager llm = new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false);
            binding.rv.setLayoutManager(llm);

            binding.rv.setHasFixedSize(true);
            binding.rv.setItemAnimator(null);


            itemView.setOnClickListener(v -> {

                Recipe recipe = list.get(getBindingAdapterPosition());
                recipe.isOpen = !recipe.isOpen;
                notifyItemChanged(getBindingAdapterPosition());


            });

        }

    }


    private void getProductRecipeList(RowRecipeBinding binding, Recipe recipe, int country_id, int city_id, String user_id) {
//        productList.clear();
        binding.progressBar1.setVisibility(View.VISIBLE);
        binding.rv.setVisibility(View.GONE);
        binding.noProductsTv.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            binding.progressBar1.setVisibility(View.GONE);

            ResultAPIModel<ArrayList<ProductModel>> result = (ResultAPIModel<ArrayList<ProductModel>>) obj;

            if (IsSuccess) {
                if (result.data != null && result.data.size() > 0) {

                    recipe.productsList = result.data;
                    recipe.isLoaded = true;

                    SuggestedProductAdapter adapter = new SuggestedProductAdapter(activity, recipe.productsList, this, 0);
                    binding.rv.setAdapter(adapter);
                    binding.rv.setVisibility(View.VISIBLE);
                    binding.noProductsTv.setVisibility(View.GONE);
                } else {
                    binding.rv.setVisibility(View.GONE);
                    binding.noProductsTv.setVisibility(View.VISIBLE);

                }

            }


        }).getProductRecipeList(recipe.getId(), country_id, city_id, user_id, 0, 10);
    }


}