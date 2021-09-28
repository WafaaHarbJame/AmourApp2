package com.ramez.shopp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Models.MostSearchModel;
import com.ramez.shopp.databinding.RowMostsearchItemBinding;

import java.util.List;

public class MostSearchAdapter extends RecyclerView.Adapter<MostSearchAdapter.Holder> {

    private Context context;
    private List<MostSearchModel> list;
    private OnTagClick onTagClick;
    private int limit = 6;


    public MostSearchAdapter(Context context, List<MostSearchModel> mostSearchModelList, OnTagClick onTagClick) {
        this.context = context;
        this.list = mostSearchModelList;
        this.onTagClick = onTagClick;

        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowMostsearchItemBinding itemView = RowMostsearchItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        MostSearchModel mostSearchModel = list.get(position);
       holder.binding.searchTv.setText(mostSearchModel.getTagName());
       holder.binding.container.setOnClickListener(v -> onTagClick.onTagClicked(position,mostSearchModel));



    }

    public void setCategoriesList(List<MostSearchModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

         return list.size();
    }

    public interface OnTagClick {
        void onTagClicked(int position, MostSearchModel mostSearchModel);
    }

    static class Holder extends RecyclerView.ViewHolder {

        RowMostsearchItemBinding binding;

        Holder(RowMostsearchItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
