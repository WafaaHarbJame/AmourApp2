package com.ramez.shopp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.activities.ProductDetailsActivity;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.AnalyticsHandler;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.CartProcessModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.OrderItemDetail;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;

import java.util.List;

import es.dmoral.toasty.Toasty;


public class OrderProductsAdapter extends RecyclerView.Adapter<OrderProductsAdapter.Holder> {

    private Context context;
    private List<OrderItemDetail> orderProductsDMS;
    private String currency = "BHD";
    private OnItemClick onItemClick;
    int fraction = 2;
    LocalModel localModel;

    public OrderProductsAdapter(Context context, List<OrderItemDetail> orderProductsDM) {
        this.context = context;
        this.orderProductsDMS = orderProductsDM;
        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(context);
        fraction = localModel.getFractional();
        currency = localModel.getCurrencyCode();

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

          try {

              GlobalData.GlideImg(context, orderProductsDM.getImage()
                      , R.drawable.holder_image, holder.productImage);

          } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return orderProductsDMS.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView textQTY, textItemName, textItemPrice;
        ImageView productImage;
        Button reOrderProductBtn;
        LinearLayout cardView;


        public Holder(View view) {
            super(view);
            textQTY = view.findViewById(R.id.qty_text);
            textItemName = view.findViewById(R.id.item_text);
            textItemPrice = view.findViewById(R.id.price_text);
            productImage = view.findViewById(R.id.imageView1);
            reOrderProductBtn = view.findViewById(R.id.reOrderProductBtn);
            cardView = view.findViewById(R.id.card_view_outer);

            reOrderProductBtn.setOnClickListener(v -> {

                int position = getBindingAdapterPosition();

                OrderItemDetail orderProductsDM = orderProductsDMS.get(position);
                int count = orderProductsDM.getQuantity();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(localModel.getCityId());
                int productId = orderProductsDM.getProductId();
                int product_barcode_id = orderProductsDM.getProductBarcodeId();

                addToCart(v, position, productId, product_barcode_id, count, userId, storeId);


            });


            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                int position = getBindingAdapterPosition();
                OrderItemDetail orderProductsDM = orderProductsDMS.get(position);
                ProductModel productModel = new ProductModel();
                productModel.setId(orderProductsDM.getProductId());
                productModel.sethName(orderProductsDM.getHProductName());
                productModel.setName(orderProductsDM.getName());
                intent.putExtra(Constants.DB_productModel, productModel);
                intent.putExtra(Constants.product_id, orderProductsDM.getProductId());
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            });


        }
    }


    private void addToCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId) {

        if (quantity > 0) {
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

        } else {
            Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT).show();
        }
    }


    private void initSnackBar(String message, View viewBar) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();

    }


    public interface OnItemClick {
        void onItemClicked(int position, ProductModel productModel);
    }

}



