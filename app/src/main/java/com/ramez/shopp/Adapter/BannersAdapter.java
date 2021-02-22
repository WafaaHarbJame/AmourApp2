package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowBannersItemBinding;

import java.util.List;

public class BannersAdapter extends RecyclerView.Adapter<BannersAdapter.Holder> {

    private Context context;
    private List<String> list;
    private OnItemClick onItemClick;

    public BannersAdapter(Context context, List<String> categoryDMS, OnItemClick onItemClick) {
        this.context = context;
        this.list = categoryDMS;
        this.onItemClick = onItemClick;
        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowBannersItemBinding itemView = RowBannersItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {


        GlobalData.PicassoImg(list.get(position)
                ,R.drawable.holder_image,holder.binding.ivCatImage);


        holder.binding.container.setOnClickListener(v -> onItemClick.onItemClicked(position,null));

    }

    public void setCategoriesList(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClick {
        void onItemClicked(int position,CategoryModel categoryModel);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowBannersItemBinding binding;

        Holder(RowBannersItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
