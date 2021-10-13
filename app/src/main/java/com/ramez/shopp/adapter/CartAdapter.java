package com.ramez.shopp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Classes.AnalyticsHandler;
import com.ramez.shopp.Classes.CartModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.AddCommentDialog;
import com.ramez.shopp.Models.CartProcessModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.RowCartItemBinding;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerSwipeAdapter<CartAdapter.Holder> {

    private static final String TAG = "Log CartAdapter";
    public int count;
    public String currency = "BHD";
    int fraction = 2;
    LocalModel localModel;
    DataCallback dataCallback;
    AddCommentDialog addCommentDialog;
    private final Context context;
    private final List<CartModel> cartDMS;
    private final OnCartItemClicked onCartItemClicked;
    MemberModel memberModel;

    public CartAdapter(Context context, List<CartModel> cartDMS, OnCartItemClicked onCartItemClicked, DataCallback dataCallback) {
        this.context = context;
        this.cartDMS = new ArrayList<>(cartDMS);
        this.onCartItemClicked = onCartItemClicked;
        this.dataCallback = dataCallback;
        memberModel = UtilityApp.getUserData();

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowCartItemBinding itemView = RowCartItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Holder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final Holder holder, int position) {

        CartModel cartDM = cartDMS.get(position);
        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(context);
        currency = localModel.getCurrencyCode();
        fraction = localModel.getFractional();
        memberModel = UtilityApp.getUserData();
        int quantity = cartDM.getQuantity();

        holder.binding.weightUnitTv.setText(cartDM.getWightName());

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


        if (cartDM.isExtra()) {
            holder.binding.priceTv.setVisibility(View.GONE);
            holder.binding.currencyPriceTv.setVisibility(View.GONE);
            holder.binding.weightUnitTv.setVisibility(View.GONE);
            String ProductName = cartDM.getProductName() == null ? cartDM.gethProductName() : cartDM.getProductName();
            holder.binding.tvName.setText(ProductName);

        } else {
            holder.binding.tvName.setText(cartDM.getName());

        }


        holder.binding.currencyPriceTv.setText(currency);


        if (cartDM.getSpecialPrice() > 0) {

            holder.binding.productPriceBeforeTv.setBackground(ContextCompat.getDrawable(context, R.drawable.itlatic_red_line));
            holder.binding.productPriceBeforeTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(cartDM.getProductPrice())), fraction) + " " + currency);
            holder.binding.priceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(cartDM.getSpecialPrice())), fraction));


        } else {

            holder.binding.priceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(cartDM.getProductPrice())),
                    fraction));
            holder.binding.productPriceBeforeTv.setVisibility(View.GONE);


        }


        try {

            GlobalData.GlideImg(context, cartDM.getImage()
                    , R.drawable.holder_image, holder.binding.imageView1);


        } catch (Exception e) {
            e.printStackTrace();
        }

        calculateSubTotalPrice();
        calculateSavePrice();

        if (Integer.parseInt(holder.binding.productCartQTY.getText().toString()) == 1) {

            holder.binding.deleteCartBtn.setVisibility(View.VISIBLE);
            holder.binding.minusCartBtn.setVisibility(View.GONE);
        } else {
            holder.binding.minusCartBtn.setVisibility(View.VISIBLE);
            holder.binding.deleteCartBtn.setVisibility(View.GONE);

        }

        if (cartDM.getRemark() != null && !cartDM.getRemark().isEmpty()) {
            holder.binding.markTv.setVisibility(View.VISIBLE);
            holder.binding.markTv.setText(cartDM.getRemark());
        } else
            holder.binding.markTv.setVisibility(View.GONE);

        if (cartDM.getQuantity() > cartDM.getProductQuantity() && !cartDM.isExtra()) {
            holder.binding.cardBack.setBackground(ContextCompat.getDrawable(context, R.drawable.round_card_red));

        } else if (cartDM.getProductQuantity() == 0 && !cartDM.isExtra()) {
            holder.binding.cardBack.setBackground(ContextCompat.getDrawable(context, R.drawable.round_card_red));


        } else {
            holder.binding.cardBack.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        }
    }

    public double calculateSubTotalPrice() {

        double subTotal = 0;
        for (int i = 0; i < cartDMS.size(); i++) {
            double price = 0;
            if (cartDMS.get(i).getProductPrice() > 0) {


                if (cartDMS.get(i).getSpecialPrice() > 0) {
                    price = cartDMS.get(i).getSpecialPrice();

                } else {
                    price = cartDMS.get(i).getProductPrice();

                }

                subTotal += price * cartDMS.get(i).getQuantity();
            }
        }
        Log.i(TAG, "Log subTotal result" + subTotal);


        return subTotal;
    }

    public double calculateSavePrice() {
        double savePrice = 0;
        for (int i = 0; i < cartDMS.size(); i++) {
            CartModel cartModel = cartDMS.get(i);

            double price = 0, specialPrice = 0, difference = 0;

            if (cartModel.getProductPrice() > 0) {

                if (cartModel.getSpecialPrice() > 0) {
                    specialPrice = cartModel.getSpecialPrice();
                    price = cartModel.getProductPrice();
                    difference = price - specialPrice;
                    savePrice = savePrice + (difference * cartModel.getQuantity());
                }


            }
        }
        Log.i(TAG, "Log savePrice result" + savePrice);


        return savePrice;
    }

    @Override
    public int getItemCount() {
        return cartDMS.size();
    }

    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    private void initSnackBar(String message, View viewBar) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void updateMark(View v, int position, int cart_id, String remark) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {

                addCommentDialog.dismiss();
                notifyItemChanged(position);
                initSnackBar(context.getString(R.string.success_to_update_cart), v);
                cartDMS.get(position).setRemark(remark);


            } else {
                addCommentDialog.dismiss();
                GlobalData.errorDialogWithButton(context, context.getString(R.string.error), context.getString(R.string.fail_to_update_cart));

            }


        }).updateRemarkCartHandle(cart_id, remark);
    }


    class Holder extends RecyclerView.ViewHolder {

        RowCartItemBinding binding;

        @SuppressLint("SetTextI18n")
        Holder(RowCartItemBinding view) {
            super(view.getRoot());
            binding = view;

            binding.cardView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();

                if (position >= 0) {
                    CartModel cartDM = cartDMS.get(position);
                    onCartItemClicked.onCartItemClicked(cartDM);
                }
            });

            binding.markBtn.setOnClickListener(view1 -> {

                int position = getBindingAdapterPosition();
                CartModel cartModel = cartDMS.get(position);

                AddCommentDialog.Click okBut = new AddCommentDialog.Click() {
                    @Override
                    public void click() {
                        String remark = ((EditText) addCommentDialog.findViewById(R.id.remarkET)).getText().toString();

                        updateMark(view1, position, cartModel.getId(), remark);


                    }
                };

                AddCommentDialog.Click cancelBut = new AddCommentDialog.Click() {
                    @Override
                    public void click() {
                        addCommentDialog.dismiss();


                    }
                };

                addCommentDialog = new AddCommentDialog(context, cartModel.getRemark(), R.string.add_comment, R.string.add_comment, okBut, cancelBut);

                addCommentDialog.show();


            });


            binding.plusCartBtn.setOnClickListener(v -> {
                String message;
                int position = getBindingAdapterPosition();

                CartModel productModel = cartDMS.get(position);
                int count = productModel.getQuantity();
                int stock = productModel.getProductQuantity();
                int product_barcode_id = productModel.getProductBarcodeId();
                int userId = memberModel != null && memberModel.getId() != null ? memberModel.getId() : 0;
                int storeId = Integer.parseInt(localModel.getCityId());
                int productId = productModel.getProductId();
                int cart_id = productModel.getId();

                int limit = productModel.getLimitQty();
                boolean isExtra = productModel.isExtra();
                Log.i("limit", "Log limit" + limit);
                Log.i("stock", "Log stock" + stock);
                if (!isExtra) {

                    if (limit == 0) {

                        if (count + 1 <= stock) {
                            updateCart(position, productId, product_barcode_id, count + 1, count + 1, false, userId, storeId, cart_id, "quantity");

                        } else {
                            message = context.getString(R.string.stock_empty);
                            GlobalData.errorDialogWithButton(context, context.getString(R.string.error), message);
                        }
                    } else {

                        if (count + 1 <= stock && (count + 1 <= limit)) {
                            updateCart(position, productId, product_barcode_id, count + 1, count + 1, false, userId, storeId, cart_id, "quantity");

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

                    }
                } else {
                    updateCart(position, productId, product_barcode_id, count + 1, count + 1, false, userId, storeId, cart_id, "quantity");

                }


            });

            binding.minusCartBtn.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();

                CartModel productModel = cartDMS.get(position);
                int count = productModel.getQuantity();
                int product_barcode_id = productModel.getProductBarcodeId();
                int userId = memberModel != null && memberModel.getId() != null ? memberModel.getId() : 0;
                int storeId = Integer.parseInt(localModel.getCityId());
                int productId = productModel.getProductId();
                int cart_id = productModel.getId();

                updateCart(position, productId, product_barcode_id, count - 1, count - 1, false, userId, storeId, cart_id, "quantity");


            });

            binding.deleteCartBtn.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();

                if (cartDMS != null && cartDMS.size() > 0 & position != -1) {
                    CartModel productModel = cartDMS.get(position);
                    if (productModel != null) {
                        int product_barcode_id = productModel.getProductBarcodeId();
                        int userId = memberModel != null && memberModel.getId() != null ? memberModel.getId() : 0;
                        int storeId = Integer.parseInt(localModel.getCityId());
                        int productId = productModel.getProductId();
                        int cart_id = productModel.getId();

                        deleteCart(position, productId, product_barcode_id, cart_id, userId, storeId);

                    }

                }


            });

            binding.deleteBut.setOnClickListener(view1 -> {

                int position = getBindingAdapterPosition();
                CartModel productModel = cartDMS.get(position);
                int product_barcode_id = productModel.getProductBarcodeId();
                int userId = memberModel != null && memberModel.getId() != null ? memberModel.getId() : 0;
                int storeId = Integer.parseInt(localModel.getCityId());
                int productId = productModel.getProductId();
                int cart_id = productModel.getId();

                deleteCart(position, productId, product_barcode_id, cart_id, userId, storeId);


            });

            binding.swipe.setShowMode(SwipeLayout.ShowMode.LayDown);

            binding.swipe.addSwipeListener(new SwipeLayout.SwipeListener() {
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


    }

    public void updateCart(int position, int productId, int product_barcode_id, int quantity, int quantity2, Boolean fromCart, int userId, int storeId, int cart_id, String update_quantity) {
        if (quantity > 0) {
            new DataFeacher(false, (obj, func, IsSuccess) -> {
                if (IsSuccess) {
                    int cartQuantity = 0;
                    calculateSubTotalPrice();
                    calculateSavePrice();
                    getItemCount();

                    if (fromCart) {
                        cartQuantity = quantity2;
                    } else {
                        cartQuantity = quantity;


                    }
                    if (cartDMS.size() > 0 && position >= 0 && position < cartDMS.size()) {
                        cartDMS.get(position).setQuantity(cartQuantity);
                        notifyItemChanged(position);
                    }

                    CartProcessModel cartProcessModel = (CartProcessModel) obj;
                    cartProcessModel.setTotal(calculateSubTotalPrice());
                    cartProcessModel.setCartCount(cartDMS.size());
                    cartProcessModel.setTotalSavePrice(calculateSavePrice());


                    if (dataCallback != null) {
                        if (calculateSubTotalPrice() > 0 || calculateSavePrice() > 0)
                            dataCallback.dataResult(cartProcessModel, "success", true);
                    }


                } else {

                    GlobalData.errorDialogWithButton(context, context.getString(R.string.error), context.getString(R.string.fail_to_update_cart));


                }

            }).updateCartHandle(productId, product_barcode_id, quantity, userId, storeId, cart_id, update_quantity);


        } else {
            Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT).show();
        }


    }


    public void deleteCart(int position, int productId, int product_barcode_id, int cart_id,
                           int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {
                if (cartDMS.size() > 0 && position < cartDMS.size()) {
                    cartDMS.remove(position);
                    notifyItemRemoved(position);
                }

                Toast.makeText(context, "" + context.getString(R.string.success_delete_from_cart), Toast.LENGTH_SHORT).show();

                CartProcessModel cartProcessModel = (CartProcessModel) obj;
                cartProcessModel.setTotal(calculateSubTotalPrice());
                cartProcessModel.setCartCount(cartDMS.size());
                cartProcessModel.setTotalSavePrice(calculateSavePrice());
                AnalyticsHandler.RemoveFromCart(cart_id, currency, 0);


                if (dataCallback != null) {
                    dataCallback.dataResult(cartProcessModel, Constants.success, true);
                }

                UtilityApp.updateCart(2, cartDMS.size());


            } else {

                GlobalData.errorDialogWithButton(context, context.getString(R.string.error), context.getString(R.string.fail_to_delete_cart));

            }


        }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId);
    }

    public interface OnCartItemClicked {
        void onCartItemClicked(CartModel cartDM);
    }

}
