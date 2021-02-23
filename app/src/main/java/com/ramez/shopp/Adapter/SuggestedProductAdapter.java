package com.ramez.shopp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Activities.ProductDetailsActivity;
import com.ramez.shopp.Activities.RegisterLoginActivity;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.RowSuggestedProductItemBinding;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SuggestedProductAdapter extends RecyclerView.Adapter<SuggestedProductAdapter.Holder> {
    //    public int count = 1;
    private Context context;
    private OnItemClick onItemClick;
    private ArrayList<ProductModel> productModels;
    private double discount = 0.0;
    private String currency = "BHD";
    private int limit = 2;


    public SuggestedProductAdapter(Context context, ArrayList<ProductModel> productModels, OnItemClick onItemClick, int limit) {
        this.context = context;
        this.onItemClick = onItemClick;
        this.productModels = productModels;
        this.limit = limit;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowSuggestedProductItemBinding itemView = RowSuggestedProductItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Holder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ProductModel productModel = productModels.get(position);

        currency = UtilityApp.getLocalData().getCurrencyCode();

        holder.binding.productNameTv.setText(productModel.getProductName().trim());

        if (productModel.getFavourite() != null && productModel.getFavourite()) {
            holder.binding.favBut.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite_icon));
        } else {
            holder.binding.favBut.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.empty_fav));

        }

        int quantity = productModel.getProductBarcodes().get(0).getCartQuantity();

        if (quantity > 0) {

            holder.binding.productCartQTY.setText(String.valueOf(quantity));
            holder.binding.CartLy.setVisibility(View.VISIBLE);
            holder.binding.cartBut.setVisibility(View.GONE);

            if (quantity == 1) {
                holder.binding.deleteCartBtn.setVisibility(View.VISIBLE);
                holder.binding.minusCartBtn.setVisibility(View.GONE);
            } else {
                holder.binding.minusCartBtn.setVisibility(View.VISIBLE);
                holder.binding.deleteCartBtn.setVisibility(View.GONE);
            }

        } else {
            holder.binding.CartLy.setVisibility(View.GONE);
            holder.binding.cartBut.setVisibility(View.VISIBLE);
        }

        if (productModel.getProductBarcodes().get(0).getIsSpecial()) {

            holder.binding.productPriceBeforeTv.setBackground(ContextCompat.getDrawable(context, R.drawable.itlatic_red_line));
            if (productModel.getProductBarcodes().get(0).getSpecialPrice() != null) {
                holder.binding.productPriceBeforeTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getPrice())), UtilityApp.getLocalData().getFractional()) + " " + currency);
                holder.binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getSpecialPrice())), UtilityApp.getLocalData().getFractional()) + " " + currency);
                discount = (Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getPrice())) - Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getSpecialPrice()))) / (Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getPrice()))) * 100;
                DecimalFormat df = new DecimalFormat("#");
                String newDiscount_str = df.format(discount);
                holder.binding.discountTv.setText(NumberHandler.arabicToDecimal(newDiscount_str) + " % " + "OFF");
            }


        } else {
            if (productModel.getProductBarcodes().get(0).getPrice() != null) {
                holder.binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getPrice())), UtilityApp.getLocalData().getFractional()) + " " + currency + "");
                holder.binding.productPriceBeforeTv.setVisibility(View.INVISIBLE);
                holder.binding.discountTv.setVisibility(View.INVISIBLE);

            }
        }

        String photoUrl = "";

        if (productModel.getImages() != null && productModel.getImages().get(0) != null && !productModel.getImages().get(0).isEmpty()) {
            photoUrl = productModel.getImages().get(0);
        } else {
            photoUrl = "http";
        }
        Picasso.get().load(photoUrl).placeholder(R.drawable.holder_image).error(R.drawable.holder_image).into(holder.binding.productImg);

//        Picasso.get().load(productModel.getImages().get(0)).placeholder(R.drawable.holder_image).error(R.drawable.holder_image).into(holder.binding.productImg);

    }

    @Override
    public int getItemCount() {
        if (limit == 2) return Math.min(productModels.size(), limit);
        else return productModels.size();


    }

    private void addToFavorite(View v, int position, int productId, int userId, int storeId) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {
                GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                        context.getString(R.string.fail_to_add_favorite));

            } else if (func.equals(Constants.FAIL)) {

                GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                        context.getString(R.string.fail_to_add_favorite));
            } else {
                if (IsSuccess) {

                    Toast.makeText(context, context.getString(R.string.success_add), Toast.LENGTH_SHORT).show();

                    productModels.get(position).setFavourite(true);
                    notifyItemChanged(position);
                    notifyDataSetChanged();

                } else {

                    GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                            context.getString(R.string.fail_to_add_favorite));
                }
            }

        }).addToFavoriteHandle(userId, storeId, productId);
    }

    private void removeFromFavorite(View view, int position, int productId, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {


                GlobalData.errorDialogWithButton(context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_remove_favorite));

            } else if (func.equals(Constants.FAIL)) {
                GlobalData.errorDialogWithButton(context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_remove_favorite));

            } else {
                if (IsSuccess) {

                    productModels.get(position).setFavourite(false);
                    initSnackBar(context.getString(R.string.success_delete), view);
                    notifyItemChanged(position);
                    notifyDataSetChanged();


                } else
                    {

                        GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                                context.getString(R.string.fail_to_remove_favorite));

                }
            }

        }).deleteFromFavoriteHandle(userId, storeId, productId);
    }

    private void loginFirst() {
        Toast.makeText(context, context.getString(R.string.textLoginFirst), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, RegisterLoginActivity.class);
        intent.putExtra(Constants.LOGIN, true);
        context.startActivity(intent);

    }

    private void initSnackBar(String message, View viewBar) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    public interface OnItemClick {
        void onItemClicked(int position, ProductModel productModel);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RowSuggestedProductItemBinding binding;

        Holder(RowSuggestedProductItemBinding view) {
            super(view.getRoot());
            binding = view;
            itemView.setOnClickListener(this);

            binding.favBut.setOnClickListener(view1 -> {

                if (!UtilityApp.isLogin()) {

                    CheckLoginDialog checkLoginDialog = new CheckLoginDialog(context, R.string.LoginFirst, R.string.to_add_favorite, R.string.ok, R.string.cancel, null, null);
                    checkLoginDialog.show();

                } else {
                    int position = getAdapterPosition();
                    int userId = UtilityApp.getUserData().getId();
                    int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                    int productId = productModels.get(position).getId();
                    boolean isFavorite = productModels.get(position).getFavourite();
                    if (isFavorite) {
                        removeFromFavorite(view1, position, productId, userId, storeId);

                    } else {
                        addToFavorite(view1, position, productId, userId, storeId);

                    }

                }

            });

            binding.cartBut.setOnClickListener(view1 -> {

                if (!UtilityApp.isLogin()) {

                    CheckLoginDialog checkLoginDialog = new CheckLoginDialog(context, R.string.LoginFirst, R.string.to_add_cart, R.string.ok, R.string.cancel, null, null);
                    checkLoginDialog.show();

                } else {
                    String message;

                    ProductModel productModel = productModels.get(getAdapterPosition());
                    int count = productModel.getProductBarcodes().get(0).getCartQuantity();

                    int position = getAdapterPosition();
                    int userId = UtilityApp.getUserData().getId();
                    int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                    int productId = productModel.getId();
                    int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
                    int limit = productModel.getProductBarcodes().get(0).getLimitQty();
                    int stock = productModel.getProductBarcodes().get(0).getStockQty();


                    Log.i("limit","Log limit  "+limit);
                    Log.i("stock","Log stock  "+stock);

                    if(limit==0){

                        if (count + 1 <= stock) {
                            addToCart(view1, position, productId, product_barcode_id, count + 1, userId, storeId);
                        } else {
                            message = context.getString(R.string.stock_empty);
                            GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                                    message);
                        }
                    } else {

                        if (count + 1 <= stock && (count + 1 )<= limit) {
                            addToCart(view1, position, productId, product_barcode_id, count + 1, userId, storeId);
                        }
                        else {

                            if(count+1 > stock){
                                message = context.getString(R.string.stock_empty);
                            }
                            else {
                                message = context.getString(R.string.limit) + "" + limit;

                            }
                            GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                                    message);

                        }

                    }


                }

            });

            binding.plusCartBtn.setOnClickListener(v -> {
                String message="";

                ProductModel productModel = productModels.get(getAdapterPosition());
                int count = productModel.getProductBarcodes().get(0).getCartQuantity();

                int position = getAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
                int cart_id = productModel.getProductBarcodes().get(0).getCartId();

                int stock = productModel.getProductBarcodes().get(0).getStockQty();

                int limit = productModel.getProductBarcodes().get(0).getLimitQty();

                Log.i("limit","Log limit  "+limit);
                Log.i("stock","Log stock  "+stock);


                if(limit==0){

                    if (count + 1 <= stock) {
                        updateCart(v, position, productId, product_barcode_id, count + 1, userId, storeId, cart_id, "quantity");

                    }
                    else {
                        message = context.getString(R.string.stock_empty);
                        GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                                message);

                    }
                }
                else {

                    if (count + 1 <= stock && (count + 1 )<= limit) {
                        updateCart(v, position, productId, product_barcode_id, count + 1, userId, storeId, cart_id, "quantity");

                    }
                    else {
                        if(count+1 > stock){
                            message = context.getString(R.string.stock_empty);
                        }
                        else {
                            message = context.getString(R.string.limit) + "" + limit;

                        }
                        GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                                message);
                    }


                }

            });

            binding.minusCartBtn.setOnClickListener(v -> {

                ProductModel productModel = productModels.get(getAdapterPosition());
                int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                int position = getAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productModel.getProductBarcodes().get(0).getId();

                updateCart(v, position, productId, product_barcode_id, count - 1, userId, storeId, 0, "quantity");


            });

            binding.deleteCartBtn.setOnClickListener(v -> {

                ProductModel productModel = productModels.get(getAdapterPosition());
                int position = getAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
                int cart_id = productModel.getProductBarcodes().get(0).getCartId();

                deleteCart(v, position, productId, product_barcode_id, cart_id, userId, storeId);

            });


        }


        @Override
        public void onClick(View v) {

            if (onItemClick != null) {
                onItemClick.onItemClicked(getAdapterPosition(), productModels.get(getAdapterPosition()));
                if(productModels.size()>0){
                    ProductModel productModel = productModels.get(getAdapterPosition());
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    intent.putExtra(Constants.DB_productModel, productModel);
                    context.startActivity(intent);
                }



            }
        }


        private void addToCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId) {
            new DataFeacher(false, (obj, func, IsSuccess) -> {

                if (IsSuccess) {

//                    initSnackBar(context.getString(R.string.success_added_to_cart), v);
                    productModels.get(position).getProductBarcodes().get(0).setCartQuantity(quantity);
                    notifyItemChanged(position);
                    System.out.println("Log suggest addToCart");
                    UtilityApp.updateCart(1, productModels.size());

                } else {
                    GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                            context.getString(R.string.fail_to_add_cart));


                }

            }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId);
        }

        private void updateCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId, int cart_id, String update_quantity) {
            new DataFeacher(false, (obj, func, IsSuccess) -> {
                if (IsSuccess) {

//                    initSnackBar(context.getString(R.string.success_to_update_cart), v);
                    productModels.get(position).getProductBarcodes().get(0).setCartQuantity(quantity);
                    notifyItemChanged(position);

                } else {
                    GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                            context.getString(R.string.fail_to_update_cart));




                }

            }).updateCartHandle(productId, product_barcode_id, quantity, userId, storeId, cart_id, update_quantity);
        }

        private void deleteCart(View v, int position, int productId, int product_barcode_id, int cart_id, int userId, int storeId) {
            new DataFeacher(false, (obj, func, IsSuccess) -> {

                if (IsSuccess) {
                    productModels.get(position).getProductBarcodes().get(0).setCartQuantity(0);
                    notifyItemChanged(position);
                    initSnackBar(context.getString(R.string.success_delete_from_cart), v);
                    UtilityApp.updateCart(2, productModels.size());


                } else {

                    GlobalData.errorDialogWithButton(context,context.getString(R.string.error),
                            context.getString(R.string.fail_to_delete_cart));


                }


            }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId);
        }

    }

}
