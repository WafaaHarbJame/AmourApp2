package com.ramez.shopp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowCategoryBinding;
import com.ramez.shopp.databinding.RowProductsItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.Holder> {

    private Context context;
    private List<CategoryModel> categoryDMS;
    private OnItemClick onItemClick;
    private int limit = 6;


    public CategoryAdapter(Context context, List<CategoryModel> categoryDMS,int limit, OnItemClick onItemClick) {
        this.context = context;
        this.categoryDMS = categoryDMS;
        this.onItemClick = onItemClick;
        this.limit = limit;

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
        GlobalData.PicassoImg(categoryModel.getImage()
                ,R.drawable.holder_image,holder.binding.ivCatImage);

        holder.binding.container.setOnClickListener(v -> onItemClick.onItemClicked(position,categoryModel));

    }

    public void setCategoriesList(List<CategoryModel> list) {
        categoryDMS = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        if (limit == 6) return Math.min(categoryDMS.size(), limit);
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
}
