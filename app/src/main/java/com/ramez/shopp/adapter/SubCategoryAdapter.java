package com.ramez.shopp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Models.ChildCat;
import com.ramez.shopp.R;

import java.util.ArrayList;


public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.Holder> {

    private Context context;
    private ArrayList<ChildCat> mainCategoryDMS;
    private OnSubCategoryItemClicked onSubCategoryItemClicked;
    private int selectedCat;

    public SubCategoryAdapter(Context context, ArrayList<ChildCat> mainCategoryDMS, int selectedId, OnSubCategoryItemClicked onSubCategoryItemClicked) {
        this.context = context;
        this.mainCategoryDMS = mainCategoryDMS;
        this.onSubCategoryItemClicked = onSubCategoryItemClicked;
        this.selectedCat = selectedId;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_subcat, parent, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        ChildCat subCategoryDM = mainCategoryDMS.get(position);

        holder.buttonCategory.setText(subCategoryDM.getCatName());


        if (subCategoryDM.getId() == selectedCat) {
            holder.view_line.setVisibility(View.VISIBLE);

        } else {
            holder.view_line.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mainCategoryDMS.size();
    }

    public interface OnSubCategoryItemClicked {
        void onItemClicked(ChildCat subCategoryDM);
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView buttonCategory;
        View view_line;

        Holder(View itemView) {
            super(itemView);
            buttonCategory = itemView.findViewById(R.id.btnCategory);
            view_line = itemView.findViewById(R.id.view_line);

            buttonCategory.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if(position>=0){
                    ChildCat subCategoryDM = mainCategoryDMS.get(position);
                    selectedCat = subCategoryDM.getId();
                    notifyDataSetChanged();

                    onSubCategoryItemClicked.onItemClicked(subCategoryDM);

                }


            });
        }
    }
}
