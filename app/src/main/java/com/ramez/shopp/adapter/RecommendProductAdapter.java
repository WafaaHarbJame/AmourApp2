package com.ramez.shopp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.GlobalData;
import com.ramez.shopp.activities.ProductDetailsActivity;
import com.ramez.shopp.classes.UtilityApp;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.RowDiscoverMoreBinding;
import com.ramez.shopp.databinding.RowRecommendProductsItemBinding;

import java.util.List;

public class RecommendProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_DISCOVER = 1;
    private Context context;
    private OnItemClick onItemClick;
    private List<ProductModel> productModels;
    private String currency = "BHD";
    private String filter = "";
    private int fraction = 2;
    LocalModel localModel;

    public RecommendProductAdapter(Context context, String filter, List<ProductModel> productModels) {
        this.context = context;
        this.productModels = productModels;
        this.filter = filter;
        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(context);


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_TYPE_ITEM) {
            RowRecommendProductsItemBinding itemView = RowRecommendProductsItemBinding.inflate(LayoutInflater.from(context), parent, false);
            vh = new ProductHolder(itemView);

        } else if (viewType == VIEW_TYPE_DISCOVER) {
            RowDiscoverMoreBinding itemView = RowDiscoverMoreBinding.inflate(LayoutInflater.from(context), parent, false);
            vh = new DiscoverViewHolder(itemView);
        }
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(context);

        if (viewHolder instanceof ProductHolder) {
            ProductHolder holder = (ProductHolder) viewHolder;
            ProductModel productModel = productModels.get(position);
            currency = localModel.getCurrencyCode();
            fraction = localModel.getFractional();

            if (productModel.getFirstProductBarcodes().isSpecial()) {

                double originalPrice = productModel.getFirstProductBarcodes().getPrice();
                double specialPrice = productModel.getFirstProductBarcodes().getSpecialPrice();

                holder.binding.productPriceTv.setText(NumberHandler.formatDouble(specialPrice,
                        fraction) + " " + currency);

                double discountValue = originalPrice - specialPrice;
                double discountPercent = (discountValue / originalPrice) * 100;

                if (originalPrice > 0) {
                    holder.binding.discountTv.setText(NumberHandler.arabicToDecimal((int) discountPercent + " % " + "OFF"));

                } else {
                    holder.binding.discountTv.setText(NumberHandler.arabicToDecimal((int) 0 + " % " + "OFF"));

                }

            } else {

                holder.binding.productPriceTv.setText(NumberHandler.formatDouble(productModel.getFirstProductBarcodes().getPrice(), fraction) + " " + currency + "");
                holder.binding.discountTv.setVisibility(View.INVISIBLE);


            }

            String photoUrl = "";

            if (productModel.getImages() != null && productModel.getImages().get(0) != null && !productModel.getImages().get(0).isEmpty()) {
                photoUrl = productModel.getImages().get(0);
            } else {
                photoUrl = "http";
            }

            try {
                GlobalData.INSTANCE.GlideImg(context, photoUrl
                        , R.drawable.holder_image, holder.binding.productImg);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (viewHolder instanceof DiscoverViewHolder) {
            DiscoverViewHolder discoverViewHolder = (DiscoverViewHolder) viewHolder;

            discoverViewHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
                intent.putExtra(Constants.inputType_text, filter);
                intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_SEARCH);
                intent.putExtra(Constants.isNotify, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            });


        }

    }


    @Override
    public int getItemCount() {
        return productModels.size();


    }


    @Override
    public int getItemViewType(int position) {
        try {
            return productModels.get(position) == null ? VIEW_TYPE_DISCOVER : VIEW_TYPE_ITEM;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return VIEW_TYPE_DISCOVER;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public interface OnItemClick {
        void onItemClicked(int position, ProductModel productModel);
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        RowRecommendProductsItemBinding binding;

        ProductHolder(RowRecommendProductsItemBinding view) {
            super(view.getRoot());
            binding = view;

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                ProductModel productModel = productModels.get(position);
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra(Constants.DB_productModel, productModel);
                context.startActivity(intent);
            });


        }


    }

    public class DiscoverViewHolder extends RecyclerView.ViewHolder {

        RowDiscoverMoreBinding discoverMoreBinding;

        DiscoverViewHolder(RowDiscoverMoreBinding view) {
            super(view.getRoot());
            discoverMoreBinding = view;

            itemView.setOnClickListener(v -> {

                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra(Constants.inputType_text, filter);
                intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_SEARCH);
                intent.putExtra(Constants.isNotify, true);
                context.startActivity(intent);

            });


        }


    }

}
