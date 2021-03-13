package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.BrandModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowBandItemBinding;

import java.util.List;

public class BrandsAdapter extends RecyclerView.Adapter<BrandsAdapter.Holder> {

    private Context context;
    private List<BrandModel> list;
    private OnBrandClick onBrandClick;
    private int limit;

    public BrandsAdapter(Context context, List<BrandModel> categoryDMS, OnBrandClick onBrandClick,int limit) {
        this.context = context;
        this.list = categoryDMS;
        this.onBrandClick = onBrandClick;
        this.limit=limit;
        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowBandItemBinding itemView = RowBandItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        BrandModel brandModel=list.get(position);
        String imageUrl="";

        if (UtilityApp.getLanguage().equals(Constants.English)) {
            imageUrl=brandModel.getImage();

        }
        else {
            imageUrl=brandModel.getImage2();

        }

        GlobalData.PicassoImg(imageUrl
                ,R.drawable.holder_image,holder.binding.ivCatImage);


        holder.binding.container.setOnClickListener(v ->
        {
            onBrandClick.onBrandClicked(position,brandModel);
        });
    }

    public void setCategoriesList(List<BrandModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (limit != 0) return Math.min(list.size(), limit);
        else return list.size();
    }

    public interface OnBrandClick {
        void onBrandClicked(int position, BrandModel brandModel);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowBandItemBinding binding;

        Holder(RowBandItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
