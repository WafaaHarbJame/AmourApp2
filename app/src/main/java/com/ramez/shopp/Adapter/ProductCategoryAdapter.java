package com.ramez.shopp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ramez.shopp.Activities.RegisterLoginActivity;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.AnalyticsHandler;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.OnLoadMoreListener;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Models.CartProcessModel;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.RootApplication;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.RowEmptyBinding;
import com.ramez.shopp.databinding.RowLoadingBinding;
import com.ramez.shopp.databinding.RowSearchProductItemBinding;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class ProductCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_LOADING = 0;
    public static final int VIEW_TYPE_EMPTY = 2;

    public boolean isLoading = false;
    public int visibleThreshold = 5;
    public boolean show_loading = true;
    public int category_id, country_id, city_id;
    public String user_id;
    private int nextPage = 1;
    private int lastVisibleItem;
    private int totalItemCount;
    private OnLoadMoreListener mOnLoadMoreListener;

    private Context context;
    private OnItemClick onItemClick;
    private List<ProductModel> productModels;
    private double discount = 0.0;
    private String currency = "BHD";
    private int limit = 2;
    private RecyclerView rv;
    private String filter_text;
    private int gridNumber;
    private int brand_id = 0;
    public boolean isCanceled;
    public Call apiCall;


    public ProductCategoryAdapter(Context context, RecyclerView rv, List<ProductModel> productModels, int category_id, int country_id, int city_id, String user_id, int limit, String filter, OnItemClick onItemClick, int gridNumber, int brand_id) {
        this.context = context;
        this.onItemClick = onItemClick;
        this.productModels = new ArrayList<>(productModels);
        this.limit = limit;
        this.category_id = category_id;
        this.city_id = city_id;
        this.country_id = country_id;
        this.user_id = user_id;
        this.rv = rv;
        this.filter_text = filter;
        this.gridNumber = gridNumber;
        isCanceled = false;
        this.brand_id = brand_id;

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, gridNumber);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (getAdapter().getItemViewType(position)) {
                    case VIEW_TYPE_LOADING:
                    case VIEW_TYPE_EMPTY:
                        return gridNumber; //number of columns of the grid
                    default:
                        return 1;
                }
            }
        });
        rv.setLayoutManager(gridLayoutManager);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = gridLayoutManager.getItemCount();
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();


                if (show_loading) {
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                            isLoading = true;
                        }
                    }
                }
                setOnloadListener();

            }

        });


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_TYPE_ITEM) {
            RowSearchProductItemBinding itemView = RowSearchProductItemBinding.inflate(LayoutInflater.from(context), parent, false);
            vh = new Holder(itemView);

        } else if (viewType == VIEW_TYPE_LOADING) {
            RowLoadingBinding itemView = RowLoadingBinding.inflate(LayoutInflater.from(context), parent, false);
            vh = new LoadingViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_EMPTY) {
            RowEmptyBinding itemView = RowEmptyBinding.inflate(LayoutInflater.from(context), parent, false);

            vh = new EmptyViewHolder(itemView);

        }
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof Holder) {
            Holder holder = (Holder) viewHolder;
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
                holder.binding.productPriceBeforeTv.setPaintFlags(holder.binding.productPriceBeforeTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                if (productModel.getProductBarcodes().get(0).getSpecialPrice() != null) {
                    holder.binding.productPriceBeforeTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getPrice())), UtilityApp.getLocalData().getFractional()) + " " + currency);
                    holder.binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getSpecialPrice())), UtilityApp.getLocalData().getFractional()) + " " + currency);
                    discount = (Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getPrice())) - Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getSpecialPrice()))) / (Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getPrice()))) * 100;
                    DecimalFormat df = new DecimalFormat("#");
                    String newDiscount_str = df.format(discount);
                    holder.binding.discountTv.setText(NumberHandler.arabicToDecimal(newDiscount_str) + " % " + "OFF");
                }


            } else {
                holder.binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(productModel.getProductBarcodes().get(0).getPrice())), UtilityApp.getLocalData().getFractional()) + " " + currency + "");
                holder.binding.productPriceBeforeTv.setVisibility(View.GONE);
                holder.binding.discountTv.setVisibility(View.GONE);

            }

            String photoUrl = "";

            if (productModel.getImages() != null && productModel.getImages().get(0) != null && !productModel.getImages().get(0).isEmpty()) {
                photoUrl = productModel.getImages().get(0);
            } else {
                photoUrl = "http";
            }
            Picasso.get().load(photoUrl).placeholder(R.drawable.holder_image).error(R.drawable.holder_image).into(holder.binding.productImg);


        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.rowLoadingBinding.progressBar1.setIndeterminate(true);


        }

    }


    @Override
    public int getItemCount() {
        if (limit == 2) return Math.min(productModels.size(), limit);
        else return productModels.size();


    }

    private void addToFavorite(View v, int position, int productId, int userId, int storeId) {
        AnalyticsHandler.AddToWishList(productId, currency, productId);


        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {
                GlobalData.errorDialogWithButton(context, context.getString(R.string.error), context.getString(R.string.fail_to_add_favorite));
            } else if (func.equals(Constants.FAIL)) {
                GlobalData.errorDialogWithButton(context, context.getString(R.string.error), context.getString(R.string.fail_to_add_favorite));
            } else {
                if (IsSuccess) {
                    Toast.makeText(context, "" + context.getString(R.string.success_add), Toast.LENGTH_SHORT).show();
                    productModels.get(position).setFavourite(true);
                    notifyItemChanged(position);
                    notifyDataSetChanged();

                } else {
                    GlobalData.errorDialogWithButton(context, context.getString(R.string.error), context.getString(R.string.fail_to_add_favorite));
                }
            }

        }).addToFavoriteHandle(userId, storeId, productId);
    }


    private void removeFromFavorite(View view, int position, int productId, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {
                GlobalData.errorDialogWithButton(context, context.getString(R.string.error), context.getString(R.string.fail_to_remove_favorite));
            } else if (func.equals(Constants.FAIL)) {
                GlobalData.errorDialogWithButton(context, context.getString(R.string.error), context.getString(R.string.fail_to_remove_favorite));

            } else {
                if (IsSuccess) {
                    productModels.get(position).setFavourite(false);
                    Toast.makeText(context, "" + context.getString(R.string.success_delete), Toast.LENGTH_SHORT).show();
                    notifyItemChanged(position);
                    notifyDataSetChanged();


                } else {

                    GlobalData.errorDialogWithButton(context, context.getString(R.string.error), context.getString(R.string.fail_to_remove_favorite));
                }
            }

        }).deleteFromFavoriteHandle(userId, storeId, productId);
    }


    private void initSnackBar(String message, View viewBar) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    private ProductCategoryAdapter getAdapter() {
        return this;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }


    private void setOnloadListener() {

        setOnLoadMoreListener(() -> {
            System.out.println("Log add loading item");
            if (!productModels.contains(null)) {
                productModels.add(null);
                System.out.println("Log productDMS size " + productModels.size());

                notifyItemInserted(productModels.size() - 1);

                LoadAllData(category_id, country_id, city_id, user_id, filter_text, brand_id, nextPage, 10);

            }

        });

    }

    private void LoadAllData(int category_id, int country_id, int city_id, String user_id, String filter, int brand_id, int page_number, int page_size) {

        System.out.println("Log category_id: " + category_id);
        System.out.println("Log LoadAllData  page " + nextPage);

        DataFeacher dataFeacher = new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isCanceled) {
                return;
            }
            FavouriteResultModel result = (FavouriteResultModel) obj;

            if (productModels.size() > 0) {
                productModels.remove(productModels.size() - 1);
                notifyItemRemoved(productModels.size());
            }

            if (IsSuccess) {

                if (result.getData() != null && result.getData().size() > 0) {

                    ArrayList<ProductModel> products = result.getData();
                    int pos = productModels.size();

                    if (products != null && products.size() > 0) {
                        productModels.addAll(products);
                        notifyItemRangeInserted(pos, products.size());
                        nextPage++;
                    }

                } else {
                    show_loading = false;
                }
                setLoaded();

            }

        });
        apiCall = dataFeacher.getFavorite(category_id, country_id, city_id, user_id, filter, brand_id, page_number, page_size);
    }

    @Override
    public int getItemViewType(int position) {
        try {
            return productModels.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;

        } catch (Exception e) {
            e.printStackTrace();
            return VIEW_TYPE_EMPTY;
        }


    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public interface OnItemClick {
        void onItemClicked(int position, ProductModel productModel);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RowSearchProductItemBinding binding;

        Holder(RowSearchProductItemBinding view) {
            super(view.getRoot());
            binding = view;

            itemView.setOnClickListener(this);
            binding.favBut.setOnClickListener(view1 -> {

                if (!UtilityApp.isLogin()) {

                    CheckLoginDialog checkLoginDialog = new CheckLoginDialog(context, R.string.LoginFirst, R.string.to_add_favorite, R.string.ok, R.string.cancel, null, null);
                    checkLoginDialog.show();

                } else {

                    int position = getBindingAdapterPosition();
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
                    if (productModels.size() > 0) {
                        if(UtilityApp.getUserData()!=null&&UtilityApp.getUserData().getId()!=null){
                            ProductModel productModel = productModels.get(getBindingAdapterPosition());
                            int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                            int position = getBindingAdapterPosition();

                            int userId = UtilityApp.getUserData().getId();
                            int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                            int productId = productModel.getId();
                            int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
                            int limit = productModel.getProductBarcodes().get(0).getLimitQty();
                            int stock = productModel.getProductBarcodes().get(0).getStockQty();


                            if (limit == 0) {

                                if (count + 1 <= stock) {
                                    addToCart(view1, position, productId, product_barcode_id, count + 1, userId, storeId);
                                } else {
                                    message = context.getString(R.string.stock_empty);
                                    GlobalData.errorDialogWithButton(context, context.getString(R.string.error), message);
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
                                    GlobalData.errorDialogWithButton(context, context.getString(R.string.error), message);

                                }

                            }
                        }
                        else {
                            CheckLoginDialog checkLoginDialog = new CheckLoginDialog(context, R.string.LoginFirst, R.string.to_add_cart, R.string.ok, R.string.cancel, null, null);
                            checkLoginDialog.show();
                        }



                    }


                }

            });

            binding.plusCartBtn.setOnClickListener(view1 -> {
                String message;
                ProductModel productModel = productModels.get(getBindingAdapterPosition());

                int count = Integer.parseInt(binding.productCartQTY.getText().toString());
                int position = getBindingAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
                int stock = productModel.getProductBarcodes().get(0).getStockQty();
                int cart_id = productModel.getProductBarcodes().get(0).getCartId();
                int limit = productModel.getProductBarcodes().get(0).getLimitQty();


                if (limit == 0) {

                    if (count + 1 <= stock) {
                        updateCart(view1, position, productId, product_barcode_id, count + 1, userId, storeId, cart_id, "quantity");

                    } else {
                        message = context.getString(R.string.stock_empty);
                        GlobalData.errorDialogWithButton(context, message, context.getString(R.string.fail_to_delete_cart));
                    }
                } else {

                    if (count + 1 > stock) {
                        message = context.getString(R.string.limit) + "" + limit;

                    } else if (stock == 0) {
                        message = context.getString(R.string.stock_empty);


                    } else {
                        message = context.getString(R.string.limit) + "" + limit;

                    }

                    GlobalData.errorDialogWithButton(context, context.getString(R.string.error), message);

                }


            });

            binding.minusCartBtn.setOnClickListener(view1 -> {

                ProductModel productModel = productModels.get(getBindingAdapterPosition());

                int count = Integer.parseInt(binding.productCartQTY.getText().toString());
                int position = getBindingAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
                int cart_id = productModel.getProductBarcodes().get(0).getCartId();

                updateCart(view1, position, productId, product_barcode_id, count - 1, userId, storeId, cart_id, "quantity");


            });

            binding.deleteCartBtn.setOnClickListener(view1 -> {

                ProductModel productModel = productModels.get(getBindingAdapterPosition());
                int position = getBindingAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
                int cart_id = productModel.getProductBarcodes().get(0).getCartId();

                deleteCart(view1, position, productId, product_barcode_id, cart_id, userId, storeId);

            });


        }


        @Override
        public void onClick(View v) {
            if (onItemClick != null) {
                onItemClick.onItemClicked(getBindingAdapterPosition(), productModels.get(getBindingAdapterPosition()));
            }
        }


        private void addToCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId) {

            if(quantity>0){
                new DataFeacher(false, (obj, func, IsSuccess) -> {
                    CartProcessModel result = (CartProcessModel) obj;

                    if (IsSuccess) {
                        int cartId = result.getId();
                        productModels.get(position).getProductBarcodes().get(0).setCartQuantity(quantity);
                        productModels.get(position).getProductBarcodes().get(0).setCartId(cartId);

                        notifyItemChanged(position);
                        UtilityApp.updateCart(1, productModels.size());

                        AnalyticsHandler.AddToCart(cartId, currency, quantity);

                    } else {

                        GlobalData.errorDialogWithButton(context, context.getString(R.string.fail_to_add_cart), context.getString(R.string.fail_to_delete_cart));

                    }


                }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId);

            }
            else {
                Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT).show();
            }
        }

        private void updateCart(View view, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId, int cart_id, String update_quantity) {


            if(quantity>0){

                new DataFeacher(false, (obj, func, IsSuccess) -> {
                    if (IsSuccess) {

                        productModels.get(position).getProductBarcodes().get(0).setCartQuantity(quantity);
                        notifyItemChanged(position);

                    } else {

                        GlobalData.errorDialogWithButton(context, context.getString(R.string.fail_to_update_cart), context.getString(R.string.fail_to_delete_cart));

                    }

                }).updateCartHandle(productId, product_barcode_id, quantity, userId, storeId, cart_id, update_quantity);

            }
            else {
                Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT).show();
            }

        }

        private void deleteCart(View v, int position, int productId, int product_barcode_id, int cart_id, int userId, int storeId) {
            new DataFeacher(false, (obj, func, IsSuccess) -> {

                if (IsSuccess) {
                    productModels.get(position).getProductBarcodes().get(0).setCartQuantity(0);
                    notifyItemChanged(position);
                    initSnackBar(context.getString(R.string.success_delete_from_cart), v);
                    UtilityApp.updateCart(2, productModels.size());
                    AnalyticsHandler.RemoveFromCart(cart_id, currency, 0);


                } else {

                    GlobalData.errorDialogWithButton(context, context.getString(R.string.delete_product),
                            context.getString(R.string.fail_to_delete_cart));
                }


            }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId);
        }

    }


    class LoadingViewHolder extends RecyclerView.ViewHolder {

        RowLoadingBinding rowLoadingBinding;

        LoadingViewHolder(RowLoadingBinding view) {
            super(view.getRoot());
            rowLoadingBinding = view;


        }


    }


    class EmptyViewHolder extends RecyclerView.ViewHolder {

        RowEmptyBinding rowEmptyBinding;

        EmptyViewHolder(RowEmptyBinding view) {
            super(view.getRoot());
            rowEmptyBinding = view;


        }


    }


}