package com.ramez.shopp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.ApiHandler.DataFetcherCallBack;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Models.AreasModel;
import com.ramez.shopp.Models.Recipe;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowCityBinding;
import com.ramez.shopp.databinding.RowRecipeBinding;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private Activity activity;
    private List<Recipe> list;
    private DataFetcherCallBack dataFetcherCallBack;
    int  selectRecipe;
    public RecipeAdapter(Activity activity, List<Recipe> list, int selectRecipe,DataFetcherCallBack dataFetcherCallBack) {
        this.activity = activity;
        this.list = list;
        this.dataFetcherCallBack = dataFetcherCallBack;
        this.selectRecipe=selectRecipe;
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


        if (selectRecipe ==recipe.getId()) {
            holder.binding.selectTxt.setText(activity.getString(R.string.fa_check));
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity,R.color.green));
        } else {
            holder.binding.selectTxt.setText("");
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.header3));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RowRecipeBinding binding;

        public ViewHolder(RowRecipeBinding view) {
            super(view.getRoot());
            binding = view;

            itemView.setOnClickListener(v -> {

                Recipe recipe = list.get(getAdapterPosition());
                selectRecipe =recipe.getId();
                notifyDataSetChanged();
                if (dataFetcherCallBack != null) {
                    dataFetcherCallBack.Result(recipe, Constants.success, true);
                }


            });

        }


    }

}