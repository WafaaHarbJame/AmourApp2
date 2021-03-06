package com.amour.shop.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.amour.shop.Models.CategoryModel;
import com.amour.shop.classes.GlobalData;
import com.amour.shop.R;
import com.amour.shop.databinding.RowCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {

    private Context context;
    private List<CategoryModel> categoryDMS;
    private OnItemClick onItemClick;
    private int limit = 6;
    private boolean isHoriz;


    public CategoryAdapter(Context context, List<CategoryModel> categoryDMS,int limit, OnItemClick onItemClick,boolean isHoriz) {
        this.context = context;
        this.categoryDMS = categoryDMS;
        this.onItemClick = onItemClick;
        this.limit = limit;
        this.isHoriz=isHoriz;

        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowCategoryBinding itemView = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        CategoryModel categoryModel = categoryDMS.get(position);
        try {
            GlobalData.INSTANCE.GlideImg(context,categoryModel.getCatImage()
                    ,R.drawable.holder_image,holder.binding.ivCatImage);

        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.binding.container.setOnClickListener(v -> {
                    onItemClick.onItemClicked(position,categoryModel);
                }
              );

    }

    public void setCategoriesList(List<CategoryModel> list) {
        categoryDMS = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (limit != 0) return Math.min(categoryDMS.size(), limit);
        else return categoryDMS.size();
    }

    public interface OnItemClick {
        void onItemClicked(int position,CategoryModel categoryModel);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowCategoryBinding binding;

        Holder(RowCategoryBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }

    static class HorizontalHolder extends RecyclerView.ViewHolder {

        RowCategoryBinding binding;

        HorizontalHolder(RowCategoryBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
