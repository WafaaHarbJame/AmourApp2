package com.ramez.shopp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;


public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.Holder> {

    private Context context;
    private ArrayList<CategoryModel> mainCategoryDMS;
    private OnMainCategoryItemClicked onMainCategoryItemClicked;
    private int selectedPosition;
    private int lastIndex = 0;


    public MainCategoryAdapter(Context context, ArrayList<CategoryModel> mainCategoryDMS,
                               OnMainCategoryItemClicked onMainCategoryItemClicked, int selectedPosition) {
        this.context = context;
        this.mainCategoryDMS = mainCategoryDMS;
        this.onMainCategoryItemClicked = onMainCategoryItemClicked;
        this.selectedPosition = selectedPosition;




    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cat_page, parent, false);


        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        CategoryModel mainMainCategoryDM = mainCategoryDMS.get(position);

        holder.buttonCategory.setText(mainMainCategoryDM.getCatName());

        GlobalData.PicassoImg(mainMainCategoryDM.getImage()
        ,R.drawable.holder_image,holder.catImage);
//        Picasso.get()
//                .load(mainMainCategoryDM.getImage())
//                .placeholder(R.drawable.holder_image)
//                .error(R.drawable.holder_image)
//                .into(holder.catImage);


        if (mainCategoryDMS.get(position).getId() == selectedPosition) {
            holder.buttonCategory.setTextColor(context.getResources().getColor(R.color.blue1));
            holder.line.setVisibility(View.VISIBLE);

        } else {
            holder.buttonCategory.setTextColor(context.getResources().getColor(R.color.very_dark_gray));
            holder.line.setVisibility(View.GONE);

        }


    }

    @Override
    public int getItemCount() {
        return mainCategoryDMS.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView buttonCategory;
        ImageView catImage;
        View line;

        Holder(View view) {
            super(view);
            buttonCategory = view.findViewById(R.id.btnCategory);
            line = view.findViewById(R.id.view_line);
            catImage = view.findViewById(R.id.ivCatImage);


            view.setOnClickListener(v -> {
                int position=getAdapterPosition();
                CategoryModel mainMainCategoryDM = mainCategoryDMS.get(position);

                onMainCategoryItemClicked.OnMainCategoryItemClicked(mainMainCategoryDM,position);
                lastIndex = getAdapterPosition();
                notifyDataSetChanged();
                selectedPosition = mainMainCategoryDM.getId();


            });

        }
    }

    public interface OnMainCategoryItemClicked {
        void OnMainCategoryItemClicked(CategoryModel mainCategoryDM,int position);
    }


}
