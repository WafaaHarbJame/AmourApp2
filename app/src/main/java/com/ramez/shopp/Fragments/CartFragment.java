package com.ramez.shopp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.ramez.shopp.Activities.AddCardActivity;
import com.ramez.shopp.Activities.ProductDetailsActivity;
import com.ramez.shopp.Adapter.CartAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.CallBack.DataCallback;
import com.ramez.shopp.Classes.CartModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Dialogs.EmptyCartDialog;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.CartProcessModel;
import com.ramez.shopp.Models.CartResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.Utils.SharedPManger;
import com.ramez.shopp.databinding.FragmentCartBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import ru.nikartm.support.BadgePosition;

import static android.content.ContentValues.TAG;

public class CartFragment extends FragmentBase implements CartAdapter.OnCartItemClicked {
    ArrayList<CartModel> cartList;
    LinearLayoutManager linearLayoutManager;
    String currency = "BHD";
    int fraction = 2;
    int storeId, userId;
    MemberModel user;
    LocalModel localModel;
    boolean isLogin = false;
    int productsSize;
    String total;
    int productSize;
    private FragmentCartBinding binding;
    private CartAdapter cartAdapter;
    private EmptyCartDialog emptyCartDialog;
    private CheckLoginDialog checkLoginDialog;
    private int minimum_order_amount = 0;
    private Double delivery_charges = 0.0;
    private CartResultModel cartResultModel;
    private Activity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        cartList = new ArrayList<>();
        isLogin = UtilityApp.isLogin();
        activity = getActivity();

        localModel = UtilityApp.getLocalData();
        currency = localModel.getCurrencyCode();
        fraction = localModel.getFractional();


        user = UtilityApp.getUserData();


        if (!isLogin) {
            binding.dataLY.setVisibility(View.GONE);
            binding.contBut.setVisibility(View.GONE);
            showLoginDialog();
        } else {
            storeId = Integer.parseInt(localModel.getCityId());
            userId = user.getId();

            linearLayoutManager = new LinearLayoutManager(getActivityy());
            binding.cartRecycler.setLayoutManager(linearLayoutManager);


            binding.cartRecycler.setHasFixedSize(false);
            binding.cartRecycler.setAnimation(null);

            binding.contBut.setOnClickListener(view1 -> {

                EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_invoice));
                FragmentManager fragmentManager = getParentFragmentManager();
                InvoiceFragment invoiceFragment = new InvoiceFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.CART_PRODUCT_COUNT, productsSize);
                bundle.putString(Constants.CART_SUM, total);
                bundle.putDouble(Constants.delivery_charges, delivery_charges);
                bundle.putSerializable(Constants.CART_MODEL, cartResultModel);
                invoiceFragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.mainContainer, invoiceFragment, "InvoiceFragment").commit();


            });


            getCarts(storeId, userId);

            if (cartAdapter != null) {

                cartAdapter.notifyDataSetChanged();
            }


        }


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
            getCarts(storeId, userId);
        });

        return view;

    }

    @SuppressLint("SetTextI18n")
    private void initAdapter() {
        cartAdapter = new CartAdapter(getActivityy(), cartList, this, (obj, func, IsSuccess) -> {
            CartProcessModel cartProcessModel = (CartProcessModel) obj;
            productSize = cartProcessModel.getCartCount();

            if (cartProcessModel.getCartCount() == 0) {
                getCarts(storeId, userId);

            } else {
                total = NumberHandler.formatDouble(cartProcessModel.getTotal(), fraction);
                binding.totalTv.setText(total.concat(" " + currency));

                if (cartProcessModel.getTotal() >= minimum_order_amount) {

                    binding.tvFreeDelivery.setText(R.string.getFreeDelivery);


                } else {

                    double total_price = minimum_order_amount - cartProcessModel.getTotal();
                    binding.tvFreeDelivery.setText(getString(R.string.Add_more) + " " + NumberHandler.formatDouble(total_price, UtilityApp.getLocalData().getFractional()) + " " + currency + " " + getString(R.string.get_Free));

                }

            }


        });
        binding.cartRecycler.setAdapter(cartAdapter);
        productsSize = cartList.size();
        total = NumberHandler.formatDouble(cartAdapter.calculateSubTotalPrice(), fraction);
        binding.totalTv.setText(total.concat(" " + currency));
        cartAdapter.notifyDataSetChanged();


    }

    @Override
    public void onCartItemClicked(CartModel cartDM) {
        Intent intent = new Intent(getActivityy(), ProductDetailsActivity.class);
        ProductModel productModel = new ProductModel();
        productModel.setId(cartDM.getProductId());
        productModel.setName(cartDM.getName());
        productModel.setHName(cartDM.getHProductName());
        intent.putExtra(Constants.DB_productModel, productModel);
        startActivity(intent);
    }

    private void startAddCardActivity() {
        Intent intent = new Intent(getActivityy(), AddCardActivity.class);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    public void getCarts(int storeId, int userId) {

        cartList.clear();

        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
        binding.contBut.setVisibility(View.GONE);


        new DataFeacher(false, (obj, func, IsSuccess) -> {

            cartResultModel = (CartResultModel) obj;
            String message = getString(R.string.fail_to_get_data);

            if (isVisible()) {
                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

                if (func.equals(Constants.ERROR)) {

                    if (cartResultModel != null) {
                        message = cartResultModel.getMessage();
                    }
                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);

                } else if (func.equals(Constants.FAIL)) {

                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);

                } else if (func.equals(Constants.NO_CONNECTION)) {
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                    binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                    binding.dataLY.setVisibility(View.GONE);

                } else {
                    if (IsSuccess) {
                        if (cartResultModel.getData().getCartData() != null && cartResultModel.getData().getCartData().size() > 0) {

                            binding.dataLY.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            cartList = cartResultModel.getData().getCartData();
                            binding.contBut.setVisibility(View.VISIBLE);
                            minimum_order_amount = cartResultModel.getMinimumOrderAmount();
                            localModel.setMinimum_order_amount(minimum_order_amount);
                            UtilityApp.setLocalData(localModel);

                            delivery_charges = cartResultModel.getDeliveryCharges();
                            Log.i(TAG, "Log cart" + cartResultModel.getData().getCartData().size());
                            UtilityApp.setCartCount(cartResultModel.getCartCount());
                            initAdapter();
                            cartAdapter.notifyDataSetChanged();
                            if(delivery_charges>0){
                                if (cartAdapter.calculateSubTotalPrice() >= minimum_order_amount) {

                                    binding.tvFreeDelivery.setText(R.string.getFreeDelivery);


                                } else {

                                    double total_price = minimum_order_amount - cartAdapter.calculateSubTotalPrice();
                                    binding.tvFreeDelivery.setText(getString(R.string.Add_more) + " " + NumberHandler.formatDouble(total_price, UtilityApp.getLocalData().getFractional()) + " " + currency + " " + getString(R.string.get_Free));

                                }
                            }
                            else {
                                binding.tvFreeDelivery.setText(R.string.getFreeDelivery);

                            }




                        } else {
                            binding.contBut.setVisibility(View.GONE);
                            binding.dataLY.setVisibility(View.GONE);
                            showEmptyCartDialog();
                        }


                    } else {

                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                        binding.failGetDataLY.failTxt.setText(message);


                    }
                }
            }

        }).GetCarts(storeId, userId);
    }

    private void showEmptyCartDialog() {
        EmptyCartDialog.Click okClick = new EmptyCartDialog.Click() {
            @Override
            public void click() {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_main));

            }
        };


        EmptyCartDialog.Click cancelClick = new EmptyCartDialog.Click() {
            @Override
            public void click() {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_main));

            }
        };

        emptyCartDialog = new EmptyCartDialog(getActivityy(), R.string.please_login, R.string.text_login_login, R.string.register, okClick, cancelClick);
        emptyCartDialog.show();
    }

    private void showLoginDialog() {
        CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActivityy(), R.string.please_login, R.string.account_data, R.string.ok, R.string.cancel, null, null);
        checkLoginDialog.show();
    }


}