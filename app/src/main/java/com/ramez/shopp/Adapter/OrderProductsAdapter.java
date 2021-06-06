package com.ramez.shopp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.AnalyticsHandler;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.CartProcessModel;
import com.ramez.shopp.Models.OrderItemDetail;
import com.ramez.shopp.Models.OrderProductModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.RootApplication;
import com.squareup.picasso.Picasso;

import java.util.List;

import es.dmoral.toasty.Toasty;


public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.Holder> {

    private Context context;
    private List<OrderItemDetail> orderProductsDMS;
    private String currency = "BHD";

    public OrderProductsAdapter(Context context, List<OrderItemDetail> orderProductsDMS) {
        this.context = context;
        this.orderProductsDMS = orderProductsDMS;
        currency = UtilityApp.getLocalData().getCurrencyCode();

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_products_invoive_details, parent, false);
        return new Holder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        OrderItemDetail orderProductsDM = orderProductsDMS.get(position);

        holder.textItemName.setText(orderProductsDM.getName());

        holder.textQTY.setText(orderProductsDM.getQuantity() + " * " + orderProductsDM.getCartPrice() + " " + currency);
        holder.textItemPrice.setText(orderProductsDM.getCartPrice() + " " + currency);

        GlobalData.PicassoImg(orderProductsDM.getImage()
                , R.drawable.holder_image, holder.productImage);

        //        Picasso.get()
//                .load(orderProductsDM.getImage())
//                .placeholder(R.drawable.holder_image)
//                .error(R.drawable.holder_image)
//                .into(holder.productImage);


    }


    @Override
    public int getItemCount() {
        return orderProductsDMS.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView textQTY, textItemName, textItemPrice;
        ImageView productImage;
        Button reOrderProductBtn;


        public Holder(View view) {
            super(view);
            textQTY = view.findViewById(R.id.qty_text);
            textItemName = view.findViewById(R.id.item_text);
            textItemPrice = view.findViewById(R.id.price_text);
            productImage = view.findViewById(R.id.imageView1);
            reOrderProductBtn = view.findViewById(R.id.reOrderProductBtn);

            reOrderProductBtn.setOnClickListener(v -> {

                int position = getBindingAdapterPosition();

                OrderItemDetail orderProductsDM = orderProductsDMS.get(position);
                int count = orderProductsDM.getQuantity();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = orderProductsDM.getProductId();
                int product_barcode_id = orderProductsDM.getProductBarcodeId();

                addToCart(v, position, productId, product_barcode_id, count, userId, storeId);


            });


        }
    }


    private void addToCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {
                initSnackBar(context.getString(R.string.success_added_to_cart), v);
                CartProcessModel result = (CartProcessModel) obj;
                AnalyticsHandler.AddToCart(result.getId(), currency, quantity);
                UtilityApp.updateCart(1, orderProductsDMS.size());


            } else {
                Toasty.error(context, context.getString(R.string.fail_to_add_cart), Toast.LENGTH_SHORT, true).show();

            }


        }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId);
    }


    private void initSnackBar(String message, View viewBar) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();

    }


}



