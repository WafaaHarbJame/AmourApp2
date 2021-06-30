package com.ramez.shopp.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.ramez.shopp.Adapter.OrderProductsAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.ItemDetailsModel;
import com.ramez.shopp.Models.OrderItemDetail;
import com.ramez.shopp.Models.OrderNewModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.ActivityInvoiceInfoBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class InvoiceInfoActivity extends ActivityBase {
    public String currency = "BHD";
    ActivityInvoiceInfoBinding binding;
    //    List<OrderItemDetail> list;
    ItemDetailsModel orderModel;
    private OrderProductsAdapter orderProductsAdapter;
    private int orderId = 0;
    int store_id, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvoiceInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

//        list = new ArrayList<>();
        currency = UtilityApp.getLocalData().getCurrencyCode();
        if (UtilityApp.isLogin()) {
            userId = UtilityApp.getUserData().getId();
        }

        if (UtilityApp.getLocalData() != null) {
            store_id = Integer.parseInt(UtilityApp.getLocalData().getCityId());
        }

        getIntentData();

        setTitle(R.string.invoice_details);

        binding.reOrderBut.setOnClickListener(view1 -> {
            for (int i = 0; i < orderModel.getOrderItemDetails().size(); i++) {
                OrderItemDetail orderProductsDM = orderModel.getOrderItemDetails().get(i);

                int count = orderProductsDM.getQuantity();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = orderProductsDM.getProductId();
                int product_barcode_id = orderProductsDM.getProductBarcodeId();
                addToCart(view1, i, productId, product_barcode_id, count, userId, storeId);


            }

        });

    }

    private void getIntentData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            OrderNewModel orderNewModel = (OrderNewModel) bundle.getSerializable(Constants.ORDER_MODEL);
            orderId = orderNewModel.getId();
            //  list = orderModel.getOrderProductsDMS();
            getProductList(orderId, userId, store_id, Constants.user_type);
        }
    }

    public void initAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActiviy());
        binding.productsRecycler.setLayoutManager(linearLayoutManager);

        orderProductsAdapter = new OrderProductsAdapter(getActiviy(), orderModel.getOrderItemDetails());
        binding.productsRecycler.setAdapter(orderProductsAdapter);
        if (orderModel.getTotalAmount() > 0) {
            binding.tvTotalPrice.setText((NumberHandler.formatDouble(orderModel.getTotalAmount(), UtilityApp.getLocalData().getFractional()) + " " + currency));

        }


    }

    private void addToCart(View v, int i, int productId, int product_barcode_id, int quantity, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {

                UtilityApp.updateCart(1, orderModel.getOrderItemDetails().size());


                if (i == orderModel.getOrderItemDetails().size() - 1) {
                    initSnackBar(" " + getResources().getString(R.string.success_to_update_cart), v);

                }


            } else {

                initSnackBar(getString(R.string.fail_to_update_cart), v);
            }


        }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId);
    }


    private void initSnackBar(String message, View viewBar) {
        Toasty.success(getActiviy(), message, Toast.LENGTH_SHORT, true).show();

    }


    public void getProductList(int order_id, int user_id, int store_id, String type) {

//        orderModel.getOrderItemDetails().clear();

        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ItemDetailsModel> result = (ResultAPIModel<ItemDetailsModel>) obj;

            String message = getString(R.string.fail_to_get_data);

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

            if (func.equals(Constants.ERROR)) {

                if (result != null && result.message != null) {
                    message = result.message;
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
                    if (result.data != null &&result.data.getOrderItemDetails() != null&& result.data.getOrderItemDetails().size() > 0) {

                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

                        orderModel = result.data;
//                        list = result.data.getOrderItemDetails();

                        initAdapter();


                    } else {

                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);

                    }


                } else {

                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);


                }
            }

        }).getOrderDetails(order_id, user_id, store_id, type);
    }

}