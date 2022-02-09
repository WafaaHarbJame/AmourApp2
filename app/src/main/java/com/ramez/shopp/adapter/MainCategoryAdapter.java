package com.ramez.shopp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.ramez.shopp.Models.CategoryModel;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.GlobalData;
import com.ramez.shopp.classes.UtilityApp;
import com.ramez.shopp.R;

import java.util.ArrayList;


public class MainCategoryAdapter extends RecyclerView.Adapter<MainCategoryAdapter.Holder> {

    private Context context;
    private ArrayList<CategoryModel> mainCategoryDMS;
    private OnMainCategoryItemClicked onMainCategoryItemClicked;
    private int selectedPosition;
    private RoundedImageView selectedImageView = null;


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


        if (mainMainCategoryDM.getId() == 0) {
            if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
                holder.catImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.all_ar));

            } else {
                holder.catImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.all_en));

            }
        } else {



            try {


                GlobalData.INSTANCE.GlideImg(context, mainMainCategoryDM.getCatImage()
                        , R.drawable.holder_image, holder.catImage);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (mainCategoryDMS.get(position).getId() == selectedPosition) {
            holder.catImage.setBorderWidth(context.getResources().getDimension(R.dimen._2sdp));
            holder.catImage.setBorderColor(ContextCompat.getColor(context, R.color.green));
            selectedImageView = holder.catImage;
        } else {
            holder.catImage.setBorderWidth(context.getResources().getDimension(R.dimen._2sdp));
            holder.catImage.setBorderColor(ContextCompat.getColor(context, R.color.transparent));
        }


    }

    @Override
    public int getItemCount() {
        return mainCategoryDMS.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        RoundedImageView catImage;

        Holder(View view) {
            super(view);
            catImage = view.findViewById(R.id.catImage);

            view.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                CategoryModel mainMainCategoryDM = mainCategoryDMS.get(position);

                if (mainMainCategoryDM.getId() != selectedPosition) {

                    if (selectedImageView != null) {
                        selectedImageView.setBorderWidth(context.getResources().getDimension(R.dimen._2sdp));
                        selectedImageView.setBorderColor(ContextCompat.getColor(context, R.color.transparent));
                    }

                    selectedPosition = mainMainCategoryDM.getId();
                    selectedImageView = catImage;

                    catImage.setBorderWidth(context.getResources().getDimension(R.dimen._2sdp));
                    catImage.setBorderColor(ContextCompat.getColor(context, R.color.green));
                }
//
//                notifyDataSetChanged();
                onMainCategoryItemClicked.OnMainCategoryItemClicked(mainMainCategoryDM, position);

            });

        }
    }

    public interface OnMainCategoryItemClicked {
        void OnMainCategoryItemClicked(CategoryModel mainCategoryDM, int position);
    }


}
