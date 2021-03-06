package com.amour.shop.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amour.shop.classes.Constants;
import com.amour.shop.classes.GlobalData;
import com.amour.shop.activities.ProductDetailsActivity;
import com.amour.shop.ApiHandler.DataFeacher;
import com.amour.shop.classes.AnalyticsHandler;
import com.amour.shop.classes.UtilityApp;
import com.amour.shop.Dialogs.CheckLoginDialog;
import com.amour.shop.Models.CartProcessModel;
import com.amour.shop.Models.LocalModel;
import com.amour.shop.Models.ProductBarcode;
import com.amour.shop.Models.ProductModel;
import com.amour.shop.R;
import com.amour.shop.Utils.NumberHandler;
import com.amour.shop.databinding.RowSimilerProductsItemBinding;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class SimilierProductAdapter extends RecyclerView.Adapter<SimilierProductAdapter.Holder> {
    //    public int count = 1;
    private Context context;
    private OnItemClick onItemClick;
    private ArrayList<ProductModel> productModels;
    private double discount = 0.0;
    private String currency = "BHD";
    private int limit = 2;
    int fraction = 2;
    LocalModel localModel;

    public SimilierProductAdapter(Context context, ArrayList<ProductModel> productModels, OnItemClick onItemClick, int limit) {
        this.context = context;
        this.onItemClick = onItemClick;
        this.productModels = productModels;
        this.limit = limit;
        localModel = UtilityApp.INSTANCE.getLocalData();


    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowSimilerProductsItemBinding itemView = RowSimilerProductsItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ProductModel productModel = productModels.get(position);
        localModel = UtilityApp.INSTANCE.getLocalData();
        currency = localModel.getCurrencyCode() != null ? localModel.getCurrencyCode() : UtilityApp.INSTANCE.getLocalData().getCurrencyCode();
        fraction = localModel.getFractional();
        if (productModel != null & productModel.getProductName() != null) {

            holder.binding.productNameTv.setText(productModel.getProductName().trim());


            if (productModel.isFavourite()) {
                holder.binding.favBut.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite_icon));
            } else {
                holder.binding.favBut.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.empty_fav));

            }


            int quantity = productModel.getFirstProductBarcodes().getCartQuantity();
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


            if (productModel.getFirstProductBarcodes().isSpecial()) {

                double originalPrice = productModel.getFirstProductBarcodes().getPrice();
                double specialPrice = productModel.getFirstProductBarcodes().getSpecialPrice();


                holder.binding.productPriceBeforeTv.setBackground(ContextCompat.getDrawable(context, R.drawable.itlatic_red_line));

                holder.binding.productPriceBeforeTv.setText(NumberHandler.formatDouble(originalPrice,
                        fraction) + " " + currency);
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
                holder.binding.productPriceBeforeTv.setVisibility(View.GONE);
                holder.binding.discountTv.setVisibility(View.GONE);


            }

            String photoUrl = "";

            if (productModel.getImages() != null && productModel.getImages().get(0) != null && !productModel.getImages().get(0).isEmpty()) {
                photoUrl = productModel.getImages().get(0);
            } else {
                photoUrl = "http";
            }

            try {

                GlobalData.INSTANCE.INSTANCE.GlideImg(context, photoUrl
                        , R.drawable.holder_image, holder.binding.productImg);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    @Override
    public int getItemCount() {
        if (limit == 10) return Math.min(productModels.size(), limit);
        else return productModels.size();


    }

    private void addToFavorite(View v, int position, int productId, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {

                GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_add_favorite));

            } else if (func.equals(Constants.FAIL)) {

                GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_add_favorite));

            } else {
                if (IsSuccess) {

                    AnalyticsHandler.AddToWishList(productId, currency, productId);
                    Toasty.success(context, context.getString(R.string.success_add), Toast.LENGTH_SHORT, true).show();
                    productModels.get(position).setFavourite(true);
                    notifyItemChanged(position);
                    notifyDataSetChanged();

                } else {
                    GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.error),
                            context.getString(R.string.fail_to_add_favorite));

                }
            }

        }).addToFavoriteHandle(userId, storeId, productId);
    }

    private void removeFromFavorite(View view, int position, int productId, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {
                GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.fail_to_remove_favorite),
                        context.getString(R.string.fail_to_delete_cart));
            } else if (func.equals(Constants.FAIL)) {
                GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.fail_to_remove_favorite),
                        context.getString(R.string.fail_to_delete_cart));
            } else {
                if (IsSuccess) {
                    productModels.get(position).setFavourite(false);
                    Toast.makeText(context, context.getString(R.string.success_delete), Toast.LENGTH_SHORT).show();

                    notifyItemChanged(position);
                    notifyDataSetChanged();


                } else {

                    GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.error),
                            context.getString(R.string.fail_to_remove_favorite));

                }
            }

        }).deleteFromFavoriteHandle(userId, storeId, productId);
    }


    private void initSnackBar(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    public interface OnItemClick {
        void onItemClicked(int position, ProductModel productModel);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RowSimilerProductsItemBinding binding;

        Holder(RowSimilerProductsItemBinding view) {
            super(view.getRoot());
            binding = view;
            itemView.setOnClickListener(this);

            binding.favBut.setOnClickListener(view1 -> {
                if (!UtilityApp.INSTANCE.isLogin()) {

                    CheckLoginDialog checkLoginDialog = new CheckLoginDialog(context, R.string.LoginFirst, R.string.to_add_favorite, R.string.ok, R.string.cancel, null, null);
                    checkLoginDialog.show();

                } else {

                    int position = getBindingAdapterPosition();
                    int userId = UtilityApp.INSTANCE.getUserData().getId();
                    int storeId = Integer.parseInt(localModel.getCityId());
                    int productId = productModels.get(position).getId();
                    boolean isFavorite = productModels.get(position).isFavourite();
                    if (isFavorite) {
                        removeFromFavorite(view1, position, productId, userId, storeId);

                    } else {
                        addToFavorite(view1, position, productId, userId, storeId);

                    }
                }

            });

            binding.cartBut.setOnClickListener(view1 -> {

                if (!UtilityApp.INSTANCE.isLogin()) {

                    CheckLoginDialog checkLoginDialog = new CheckLoginDialog(context, R.string.LoginFirst, R.string.to_add_cart, R.string.ok, R.string.cancel, null, null);
                    checkLoginDialog.show();
                } else {
                    String message = "";

                    int position = getBindingAdapterPosition();

                    if (UtilityApp.INSTANCE.getUserData() != null && UtilityApp.INSTANCE.getUserData().getId() != null) {
                        ProductModel productModel = productModels.get(position);
                        ProductBarcode productBarcode = productModel.getFirstProductBarcodes();
                        int count = productBarcode.getCartQuantity();
                        int stock = productBarcode.getStockQty();
                        Log.i("lll", "Log stock" + stock);

                        int userId = UtilityApp.INSTANCE.getUserData().getId();
                        int storeId = Integer.parseInt(localModel.getCityId());
                        int productId = productModel.getId();
                        int product_barcode_id = productBarcode.getId();
                        int limit = productBarcode.getLimitQty();

                        if (limit == 0) {

                            if (count + 1 <= stock) {
                                addToCart(view1, position, productId, product_barcode_id, count + 1, userId, storeId);
                            } else {
                                message = context.getString(R.string.stock_empty);
                                GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.error),
                                        message);
                            }
                        } else {

                            if (count + 1 <= stock && (count + 1) <= limit) {
                                addToCart(view1, position, productId, product_barcode_id, count + 1, userId, storeId);
                            } else {

                                if (count + 1 > stock) {
                                    message = context.getString(R.string.stock_empty);
                                } else {
                                    message = context.getString(R.string.limit) + "" + limit;

                                }
                                GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.error),
                                        message);

                            }

                        }
                    } else {
                        CheckLoginDialog checkLoginDialog = new CheckLoginDialog(context, R.string.LoginFirst, R.string.to_add_cart, R.string.ok, R.string.cancel, null, null);
                        checkLoginDialog.show();
                    }


                }

            });

            binding.plusCartBtn.setOnClickListener(v -> {
                String message = "";
                int position = getBindingAdapterPosition();

                ProductModel productModel = productModels.get(position);
                ProductBarcode productBarcode = productModel.getFirstProductBarcodes();
//                int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                int count = Integer.parseInt(binding.productCartQTY.getText().toString());
                int stock = productBarcode.getStockQty();
                int userId = UtilityApp.INSTANCE.getUserData().getId();
                int storeId = Integer.parseInt(localModel.getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productBarcode.getId();
                int cart_id = productBarcode.getCartId();
                int limit = productBarcode.getLimitQty();
                Log.i("tag", "Log limit" + limit);
                Log.i("tag", "Log cart_id" + cart_id);
                Log.i("tag", "Log stock" + stock);


                if (limit == 0) {

                    if (count + 1 <= stock) {
                        updateCart(v, position, productId, product_barcode_id, count + 1, userId, storeId, cart_id, "quantity");

                    } else {
                        message = context.getString(R.string.stock_empty);
                        GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.error), message);
                    }
                } else {

                    if (count + 1 <= stock && (count + 1 <= limit)) {
                        updateCart(v, position, productId, product_barcode_id, count + 1, userId, storeId, cart_id, "quantity");

                    } else {

                        if (count + 1 > stock) {
                            message = context.getString(R.string.limit) + "" + limit;

                        } else if (stock == 0) {
                            message = context.getString(R.string.stock_empty);


                        } else {
                            message = context.getString(R.string.limit) + "" + limit;
                        }

                        GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.error), message);
                    }

                }


            });

            binding.minusCartBtn.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                ProductModel productModel = productModels.get(position);
                ProductBarcode productBarcode = productModel.getFirstProductBarcodes();
                // int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                int count = Integer.parseInt(binding.productCartQTY.getText().toString());
                int userId = UtilityApp.INSTANCE.getUserData().getId();
                int storeId = Integer.parseInt(localModel.getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productBarcode.getId();
                int cart_id = productBarcode.getCartId();

                Log.i("tag", "Log limit " + limit);
                Log.i("tag", "Log cart_id " + cart_id);

                updateCart(v, position, productId, product_barcode_id, count - 1, userId, storeId, cart_id, "quantity");


            });

            binding.deleteCartBtn.setOnClickListener(v -> {

                int position = getBindingAdapterPosition();
                ProductModel productModel = productModels.get(position);
                ProductBarcode productBarcode = productModel.getFirstProductBarcodes();

                int userId = UtilityApp.INSTANCE.getUserData().getId();
                int storeId = Integer.parseInt(localModel.getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productBarcode.getId();
                int cart_id = productBarcode.getCartId();
                Log.i("tag", "Log limit" + limit);
                Log.i("tag", "Log cart_id" + cart_id);

                deleteCart(v, position, productId, product_barcode_id, cart_id, userId, storeId);

            });


        }


        @Override
        public void onClick(View v) {
            if (onItemClick != null) {
                int position = getBindingAdapterPosition();
                ProductModel productModel = productModels.get(position);
                onItemClick.onItemClicked(position, productModels.get(position));
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra(Constants.DB_productModel, productModel);
                context.startActivity(intent);

            }
        }


        private void addToCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId) {

            if (quantity > 0) {
                new DataFeacher(false, (obj, func, IsSuccess) -> {

                    CartProcessModel result = (CartProcessModel) obj;

                    if (IsSuccess) {
                        int cartId = result.getId();
                        Log.i("tag", "Log " + UtilityApp.INSTANCE.getCartCount());
                        UtilityApp.INSTANCE.updateCart(1, productModels.size());
                        binding.cartBut.setVisibility(View.GONE);
                        productModels.get(position).getFirstProductBarcodes().setCartId(cartId);
                        productModels.get(position).getFirstProductBarcodes().setCartQuantity(quantity);
                        AnalyticsHandler.AddToCart(cartId, currency, quantity);

                        notifyItemChanged(position);

                    } else {

                        GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.fail_to_add_cart),
                                context.getString(R.string.fail_to_update_cart));

                    }


                }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId);


            } else {
                Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT).show();
            }

        }

        private void updateCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId, int cart_id, String update_quantity) {
            if (quantity > 0) {
                new DataFeacher(false, (obj, func, IsSuccess) -> {
                    if (IsSuccess) {

                        productModels.get(position).getFirstProductBarcodes().setCartQuantity(quantity);
                        /// initSnackBar(context.getString(R.string.success_to_update_cart));
                        notifyItemChanged(position);

                    } else {
                        GlobalData.INSTANCE.INSTANCE.errorDialogWithButton(context, context.getString(R.string.fail_to_add_cart),
                                context.getString(R.string.fail_to_update_cart));


                    }

                }).updateCartHandle(productId, product_barcode_id, quantity, userId, storeId, cart_id, update_quantity);


            } else {
                Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT).show();
            }
        }

        private void deleteCart(View v, int position, int productId, int product_barcode_id, int cart_id, int userId, int storeId) {
            new DataFeacher(false, (obj, func, IsSuccess) -> {

                if (IsSuccess) {
                    UtilityApp.INSTANCE.updateCart(2, productModels.size());
                    Log.i("tag", "Log " + UtilityApp.INSTANCE.getCartCount());
                    productModels.get(position).getFirstProductBarcodes().setCartQuantity(0);
                    notifyItemChanged(position);
                    initSnackBar(context.getString(R.string.success_delete_from_cart));
                    AnalyticsHandler.RemoveFromCart(cart_id, currency, 0);

                } else {
                    GlobalData.INSTANCE.errorDialogWithButton(context, context.getString(R.string.delete_product),
                            context.getString(R.string.fail_to_delete_cart));
                }

            }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId);
        }

    }


}
