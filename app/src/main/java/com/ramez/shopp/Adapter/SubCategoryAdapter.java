package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Models.ChildCat;
import com.ramez.shopp.R;

import java.util.ArrayList;


public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.Holder> {

    private Context context;
    private ArrayList<ChildCat> mainCategoryDMS;
    private OnSubCategoryItemClicked onSubCategoryItemClicked;
    private int selectedPosition;

    public SubCategoryAdapter(Context context, ArrayList<ChildCat> mainCategoryDMS, OnSubCategoryItemClicked onSubCategoryItemClicked, int selectedPosition) {
        this.context = context;
        this.mainCategoryDMS = mainCategoryDMS;
        this.onSubCategoryItemClicked = onSubCategoryItemClicked;
        this.selectedPosition = selectedPosition;

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


        if (subCategoryDM.getId() == selectedPosition) {
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
                int position = getAdapterPosition();
                ChildCat subCategoryDM = mainCategoryDMS.get(position);
                onSubCategoryItemClicked.onItemClicked(subCategoryDM);
                notifyDataSetChanged();
                selectedPosition = subCategoryDM.getId();


            });
        }
    }
}
