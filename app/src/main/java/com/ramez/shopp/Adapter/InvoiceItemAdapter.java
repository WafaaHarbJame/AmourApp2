package com.ramez.shopp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Classes.CartModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.CartProcessModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.RowInvoiceProductItemBinding;

import java.util.List;

import es.dmoral.toasty.Toasty;


public class InvoiceItemAdapter extends RecyclerSwipeAdapter<InvoiceItemAdapter.Holder> {

    private static final String TAG = "Log CartAdapter";
    public int count;
    public String currency = "BHD";
    DataCallback dataCallback;
    private Context context;
    private List<CartModel> cartDMS;
    private OnInvoiceItemClicked onInvoiceItemClicked;
    int fraction=2;


    public InvoiceItemAdapter(Context context, List<CartModel> cartDMS, OnInvoiceItemClicked onInvoiceItemClicked, DataCallback dataCallback) {
        this.context = context;
        this.cartDMS = cartDMS;
        this.onInvoiceItemClicked = onInvoiceItemClicked;
        this.dataCallback = dataCallback;

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowInvoiceProductItemBinding itemView = RowInvoiceProductItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Holder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        LocalModel localModel= UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(context);
        currency = localModel.getCurrencyCode();
        fraction=localModel.getFractional();
        CartModel cartDM = cartDMS.get(position);

        int quantity = cartDM.getQuantity();


        if (quantity > 0) {
            holder.binding.productCartQTY.setText(String.valueOf(quantity));

            if (quantity == 1) {
                holder.binding.deleteCartBtn.setVisibility(View.VISIBLE);
                holder.binding.minusCartBtn.setVisibility(View.GONE);
            } else {
                holder.binding.minusCartBtn.setVisibility(View.VISIBLE);
                holder.binding.deleteCartBtn.setVisibility(View.GONE);
            }

        }

        holder.binding.productName.setText(cartDM.getName());


        holder.binding.cardViewOuter.setOnClickListener(v -> {
            Log.d(TAG, "name p" + cartDM.getProductName());

            onInvoiceItemClicked.onInvoiceItemClicked(cartDM);
        });


        holder.binding.productImage.setOnClickListener(v -> {
            Log.d(TAG, "name p" + cartDM.getProductName());

            onInvoiceItemClicked.onInvoiceItemClicked(cartDM);
        });


        if (cartDM.getProductPrice() > 0) {
            if (cartDM.getSpecialPrice() == 0) {
                holder.binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(cartDM.getProductPrice().toString()), fraction) + " " + currency);


            } else {
                holder.binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(cartDM.getSpecialPrice().toString()), fraction) + " " + currency);

            }

            Glide.with(context).load(cartDM.getImage()).placeholder(R.drawable.holder_image).into(holder.binding.productImage);


        }

        calculateSubTotalPrice();


        if (Integer.parseInt(holder.binding.productCartQTY.getText().toString()) == 1) {

            holder.binding.deleteCartBtn.setVisibility(View.VISIBLE);
            holder.binding.minusCartBtn.setVisibility(View.GONE);
        } else {
            holder.binding.minusCartBtn.setVisibility(View.VISIBLE);
            holder.binding.deleteCartBtn.setVisibility(View.GONE);

        }
        holder.binding.swipe.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.binding.swipe.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });
    }


    public double calculateSubTotalPrice() {
        double subTotal = 0;
        for (int i = 0; i < cartDMS.size(); i++) {
            if (cartDMS.get(i).getProductPrice() > 0) {
                subTotal += cartDMS.get(i).getProductPrice() * cartDMS.get(i).getQuantity();
            }
        }
        Log.i(TAG, "Log subTotal result" + subTotal);


        return subTotal;
    }


    @Override
    public int getItemCount() {
        return cartDMS.size();
    }

    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    private void initSnackBar(String message, View viewBar) {
        Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();

    }

    public interface OnInvoiceItemClicked {
        void onInvoiceItemClicked(CartModel cartDM);
    }

    class Holder extends RecyclerView.ViewHolder {

        RowInvoiceProductItemBinding binding;

        @SuppressLint("SetTextI18n")
        Holder(RowInvoiceProductItemBinding view) {
            super(view.getRoot());
            binding = view;

            binding.plusCartBtn.setOnClickListener(v -> {


                CartModel productModel = cartDMS.get(getBindingAdapterPosition());
                int count = productModel.getQuantity();
                int product_barcode_id = productModel.getProductBarcodeId();
                int cart_id = productModel.getId();
                int position = getBindingAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getProductId();
                int stock = productModel.getProductQuantity();

                if (count + 1 < stock) {
                    updateCart(v, position, productId, product_barcode_id, count + 1, userId, storeId, cart_id, "quantity");

                } else {
                    Toasty.warning(context, context.getString(R.string.stock_empty), Toast.LENGTH_SHORT, true).show();

                }


            });


            binding.deleteCartBtn.setOnClickListener(view1 -> {

                CartModel productModel = cartDMS.get(getBindingAdapterPosition());
                int count = productModel.getQuantity();
                int product_barcode_id = productModel.getProductBarcodeId();
                int position = getBindingAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getProductId();
                int cart_id = productModel.getId();
                deleteCart(view1, position, productId, product_barcode_id, cart_id, userId, storeId);

            });


            binding.deleteBut.setOnClickListener(view1 -> {

                CartModel productModel = cartDMS.get(getBindingAdapterPosition());
                int count = productModel.getQuantity();
                int product_barcode_id = productModel.getProductBarcodeId();
                int position = getBindingAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getProductId();
                int cart_id = productModel.getId();
                deleteCart(view1, position, productId, product_barcode_id, cart_id, userId, storeId);

            });
            binding.minusCartBtn.setOnClickListener(v -> {

                CartModel productModel = cartDMS.get(getBindingAdapterPosition());
                int count = productModel.getQuantity();
                int product_barcode_id = productModel.getProductBarcodeId();

                int position = getBindingAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getProductId();
                int cart_id = productModel.getId();

                updateCart(v, position, productId, product_barcode_id, count - 1, userId, storeId, cart_id, "quantity");


            });

            binding.deleteCartBtn.setOnClickListener(v -> {

                CartModel productModel = cartDMS.get(getBindingAdapterPosition());
                int count = productModel.getQuantity();
                int product_barcode_id = productModel.getProductBarcodeId();
                int position = getBindingAdapterPosition();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getProductId();
                int cart_id = productModel.getId();
                deleteCart(v, position, productId, product_barcode_id, cart_id, userId, storeId);

            });

        }

        private void updateCart(View v, int position, int productId, int product_barcode_id, int quantity, int userId, int storeId, int cart_id, String update_quantity) {
            if (quantity > 0) {
                new DataFeacher(false, (obj, func, IsSuccess) -> {
                    if (IsSuccess) {

                        CartProcessModel cartProcessModel = (CartProcessModel) obj;

                        calculateSubTotalPrice();
                        initSnackBar(context.getString(R.string.success_to_update_cart), v);
                        cartDMS.get(position).setQuantity(quantity);
                        cartProcessModel.setTotal(calculateSubTotalPrice());


                        notifyItemChanged(position);
                        if (dataCallback != null) {
                            if (calculateSubTotalPrice() > 0)
                                cartProcessModel.setTotal(calculateSubTotalPrice());
                            dataCallback.dataResult(cartProcessModel, "success", true);
                        }


                    } else {

                        Toasty.error(context, context.getString(R.string.fail_to_update_cart), Toast.LENGTH_SHORT, true).show();


                    }

                }).updateCartHandle(productId, product_barcode_id, quantity, userId, storeId, cart_id, update_quantity);

            } else {
                Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT).show();
            }
        }

        private void deleteCart(View v, int position, int productId, int product_barcode_id, int cart_id, int userId, int storeId) {
            new DataFeacher(false, (obj, func, IsSuccess) -> {

                if (IsSuccess) {


                    cartDMS.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                    initSnackBar(context.getString(R.string.success_delete_from_cart), v);

                    calculateSubTotalPrice();
                    getItemCount();

                    CartProcessModel cartProcessModel = (CartProcessModel) obj;
                    cartProcessModel.setTotal(calculateSubTotalPrice());
                    cartProcessModel.setCartCount(cartDMS.size());

                    if (dataCallback != null) {
                        dataCallback.dataResult(cartProcessModel, "success", true);
                    }

                    UtilityApp.updateCart(2, cartDMS.size());


                } else {

                    Toasty.error(context, context.getString(R.string.fail_to_delete_cart), Toast.LENGTH_SHORT, true).show();

                }


            }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId);
        }


    }

}
