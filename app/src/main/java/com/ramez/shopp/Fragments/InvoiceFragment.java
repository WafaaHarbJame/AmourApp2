package com.ramez.shopp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.ramez.shopp.Activities.AddNewAddressActivity;
import com.ramez.shopp.Activities.AddressActivity;
import com.ramez.shopp.Adapter.AddressCheckAdapter;
import com.ramez.shopp.Adapter.DeliveryDayAdapter;
import com.ramez.shopp.Adapter.DeliveryTimeAdapter;
import com.ramez.shopp.Adapter.PaymentAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CartModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.AddressModel;
import com.ramez.shopp.Models.AddressResultModel;
import com.ramez.shopp.Models.CartResultModel;
import com.ramez.shopp.Models.DeliveryResultModel;
import com.ramez.shopp.Models.DeliveryTime;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.OrderCall;
import com.ramez.shopp.Models.OrdersResultModel;
import com.ramez.shopp.Models.PaymentModel;
import com.ramez.shopp.Models.PaymentResultModel;
import com.ramez.shopp.Models.QuickCall;
import com.ramez.shopp.Models.QuickDeliveryRespond;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.FragmentInvoiceBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class InvoiceFragment extends FragmentBase implements AddressCheckAdapter.OnRadioAddressSelect, AddressCheckAdapter.OnContainerSelect, AddressCheckAdapter.OnEditClick {
    private static final int ADDRESS_CODE = 100;
    public Integer userId;
    public String paymentMethod = "COD";
    public String couponCodeId = "0";
    public Integer deliveryDateId = 0;
    public Boolean expressDelivery = false;
    public DeliveryDayAdapter deliveryDayAdapter;
    public DeliveryTimeAdapter deliveryTimeAdapter;
    public int selectedPosition = 0;
    ArrayList<PaymentModel> paymentList;
    List<DeliveryTime> deliveryTimesList;
    ArrayList<CartModel> productList;
    GridLayoutManager payLinearLayoutManager;
    LinearLayoutManager linearLayoutManager;
    LocalModel localModel;
    int storeId, productsSize;
    String total, currency;
    List<DeliveryTime> DayList;
    List<DeliveryTime> TimeList;
    int fraction = 2;
    ArrayList<AddressModel> addressList;
    private FragmentInvoiceBinding binding;
    private PaymentAdapter paymentAdapter;
    private int addressId = 0;
    private String addressTitle;
    private String addressFullAddress;
    private double deliveryFees = 0.0;
    private CartResultModel cartResultModel;
    private int minimum_order_amount = 0;
    private LinkedHashMap<String, List<DeliveryTime>> datesMap = new LinkedHashMap<>();
    private boolean toggleButton = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInvoiceBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        paymentList = new ArrayList<>();
        addressList = new ArrayList<>();

        DayList = new ArrayList<>();
        TimeList = new ArrayList<>();

        localModel = UtilityApp.getLocalData();
        currency = localModel.getCurrencyCode();
        fraction = localModel.getFractional();
        minimum_order_amount = localModel.getMinimum_order_amount();


        storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
        currency = localModel.getCurrencyCode();

        userId = UtilityApp.getUserData().getId();

        payLinearLayoutManager = new GridLayoutManager(getActivityy(), 2, RecyclerView.VERTICAL, false);
        binding.paymentRv.setLayoutManager(payLinearLayoutManager);
        binding.paymentRv.setHasFixedSize(true);
        binding.paymentRv.setAnimation(null);


        binding.DeliverDayRecycler.setHasFixedSize(true);
        LinearLayoutManager deliverDayLlm = new LinearLayoutManager(getActivityy());
        deliverDayLlm.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.DeliverDayRecycler.setLayoutManager(deliverDayLlm);


        binding.DeliverTimeRecycler.setHasFixedSize(true);
        LinearLayoutManager deliverTimeLlm = new LinearLayoutManager(getActivityy());
        deliverTimeLlm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.DeliverTimeRecycler.setLayoutManager(deliverTimeLlm);


        getExtraIntent();

        getDefaultAddress();

        getDeliveryTimeList(storeId);

        getPaymentMethod(storeId);

        getQuickDelivery(storeId, localModel.getCountryId());


        initListener();


        if (deliveryFees == 0) {
            binding.deliveryFees.setText(getString(R.string.free));
            binding.freeDelivery.setText(getString(R.string.over1));


        } else {

            binding.deliveryFees.setText(NumberHandler.
                    formatDouble(deliveryFees, localModel.getFractional()).concat("" + currency));
            binding.freeDelivery.setText(getString(R.string.over).concat(" " + minimum_order_amount + " " + currency + "."));

        }


        //  binding.freeDelivery.setText(getString(R.string.over).concat(" " +NumberHandler.formatDouble(minimum_order_amount, localModel.getFractional()).concat("" + currency)));


        return view;
    }

    private void getDefaultAddress() {
        if (UtilityApp.getUserData().lastSelectedAddress > 0) {
            addressId = UtilityApp.getUserData().lastSelectedAddress;
            GetUserAddress(addressId);

        } else {
            binding.acceptBtu.setText(R.string.select_address);
        }


    }

    private void initListener() {


        binding.saveBut.setOnClickListener(view1 -> {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_cart));
        });

        binding.sendOrder.setOnClickListener(view1 -> {

            OrderCall orderCall = new OrderCall();
            orderCall.user_id = userId;
            orderCall.store_ID = storeId;
            orderCall.address_id = addressId;
            orderCall.payment_method = paymentMethod;
            orderCall.coupon_code_id = couponCodeId;
            orderCall.delivery_date_id = deliveryDateId;
            orderCall.expressDelivery = expressDelivery;

            if (deliveryTimesList != null && deliveryTimesList.size() > 0) {
                if (paymentMethod.equals("CC")) {
                    binding.chooseDelivery.setVisibility(View.GONE);
                    sendOrder(orderCall);
                } else {
                    if (addressId == 0) {
                        Toast(R.string.choose_address);
                        binding.tvFullAddress.setFocusable(true);
                        binding.tvFullAddress.setError(getString(R.string.choose_address));
                    } else {
                        sendOrder(orderCall);
                    }
                }
            } else {
                GlobalData.errorDialog(getActivityy(), R.string.make_order, getString(R.string.fail_to_send_order_no_times));
            }

        });


        binding.acceptBtu.setOnClickListener(view12 -> {
            Intent intent = new Intent(getActivityy(), AddressActivity.class);
            intent.putExtra(Constants.delivery_choose, true);
            startActivityForResult(intent, ADDRESS_CODE);


        });

        binding.freeBut.setOnClickListener(view -> {
            if (deliveryFees > 0) {
                if (Double.parseDouble(total) >= minimum_order_amount) {
                    Toast(getString(R.string.getFreeDelivery));

                } else {

                    double total_price = minimum_order_amount - Double.parseDouble(total);
                    Toast(getString(R.string.Add_more) + " " + NumberHandler.formatDouble(total_price, UtilityApp.getLocalData().getFractional()) + " " + currency + " " + getString(R.string.get_Free));


                }
            } else {
                Toast(getString(R.string.getFreeDelivery));

            }


        });


        binding.quickButton.setOnClickListener(view -> {
            expressDelivery = true;
            deliveryDayAdapter.lastIndex = -1;
            deliveryDayAdapter.notifyDataSetChanged();
            binding.DeliverTimeRecycler.setVisibility(View.GONE);
            binding.quickButton.setBackground(ContextCompat.getDrawable(getActivityy(), R.drawable.round_corner_red_selected));


        });
    }


    private void initTimesList() {
        TimeList.clear();

        int timeId = 0;
        TimeList = new ArrayList<>();

        for (DeliveryTime timeModel : deliveryTimesList) {
            DeliveryTime deliveryTime = new DeliveryTime(timeId, timeModel.getTime(), timeModel.getTime());
            TimeList.add(deliveryTime);
            timeId++;
        }

        initTimeAdapter(deliveryFees);


    }


    public void initAdapter() {

        for (int i = 0; i < paymentList.size(); i++) {
            PaymentModel paymentModel = paymentList.get(i);
            if (paymentModel.getId() == 1) {
                paymentModel.setImage(R.drawable.cash);
            }

            if (paymentModel.getId() == 2) {
                paymentModel.setImage(R.drawable.card);
            }
            if (paymentModel.getId() == 3) {
                paymentModel.setImage(R.drawable.benefit);
            }
            if (paymentModel.getId() == 4) {
                paymentModel.setImage(R.drawable.collect);
            }

        }

        paymentAdapter = new PaymentAdapter(getActivityy(), paymentList, (obj, func, IsSuccess) -> {
            PaymentModel paymentModel = (PaymentModel) obj;
            if (paymentMethod != null) {
                paymentMethod = paymentModel.getShortname();

                if (paymentMethod.equals("CC")) {
                    binding.chooseDelivery.setVisibility(View.GONE);
                    expressDelivery = true;
                    initTimeAdapter(0.0);
                    binding.deliveryFees.setText(getString(R.string.free));
                    binding.totalTv.setText(NumberHandler.formatDouble(Double.parseDouble(NumberHandler.formatDouble(Double.parseDouble(total), fraction)) + 0.0, fraction).concat(" " + currency));


                } else {
                    binding.chooseDelivery.setVisibility(View.VISIBLE);
                    expressDelivery = false;
                    initTimeAdapter(deliveryFees);
                    binding.deliveryFees.setText(NumberHandler.formatDouble(deliveryFees, localModel.getFractional()).concat("" + currency));
                    binding.totalTv.setText(NumberHandler.formatDouble(Double.parseDouble(total) + deliveryFees, fraction).concat(" " + currency));

                }


            }
        });
        binding.paymentRv.setAdapter(paymentAdapter);


    }


    public void getPaymentMethod(int user_id) {

        paymentList.clear();
        binding.loadingLYPay.setVisibility(View.VISIBLE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                String message = getString(R.string.fail_to_get_data);
                binding.loadingLYPay.setVisibility(View.GONE);
                PaymentResultModel result = (PaymentResultModel) obj;
                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.getMessage() != null) {
                        message = result.getMessage();
                    }
                    GlobalData.Toast(getActivityy(), message);


                } else if (func.equals(Constants.FAIL)) {
                    GlobalData.Toast(getActivityy(), message);

                } else if (func.equals(Constants.NO_CONNECTION)) {
                    GlobalData.Toast(getActivityy(), R.string.no_internet_connection);
                } else {
                    if (IsSuccess) {
                        if (result.getData() != null && result.getData().size() > 0) {
                            paymentList = result.getData();
                            paymentMethod = result.getData().get(0).getShortname();
                            initAdapter();


                        }


                    }
                }
            }


        }).getPaymentMethod(user_id);
    }


    public void getDeliveryTimeList(int storeId) {

        datesMap.clear();
//        deliveryTimesList.clear();

        binding.loadingDelivery.setVisibility(View.VISIBLE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                binding.loadingDelivery.setVisibility(View.GONE);
                String message = getString(R.string.fail_to_get_data);
                DeliveryResultModel result = (DeliveryResultModel) obj;


                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.getMessage() != null) {
                        message = result.getMessage();
                    }
                    GlobalData.Toast(getActivityy(), message);


                } else if (func.equals(Constants.FAIL)) {
                    GlobalData.Toast(getActivityy(), message);

                } else if (func.equals(Constants.NO_CONNECTION)) {
                    GlobalData.Toast(getActivityy(), R.string.no_internet_connection);
                } else {
                    if (IsSuccess) {
                        if (result.getData() != null && result.getData().size() > 0) {
                            List<DeliveryTime> datesList = result.getData();

                            DeliveryTime firstTime = datesList.get(0);
                            deliveryDateId = firstTime.getId();

                            String currentDate = firstTime.getDate();
                            List<DeliveryTime> timesList = new ArrayList<>();

                            while (datesList.size() > 0) {
                                DeliveryTime deliveryTime = datesList.get(0);

                                if (deliveryTime.getDate().equals(currentDate)) {
                                    timesList.add(deliveryTime);
                                    datesList.remove(0);
                                    if (datesList.isEmpty()) {
                                        datesMap.put(deliveryTime.getDate(), timesList);
                                    }
                                } else {
                                    datesMap.put(currentDate, timesList);
                                    currentDate = deliveryTime.getDate();
                                    timesList = new ArrayList<>();
                                }
                            }


                            deliveryTimesList = datesMap.get(firstTime.getDate());


                            initDaysAdapter();


                        } else {
                            binding.noDeliveryTv.setVisibility(View.VISIBLE);
                        }

                    }
                }
            }


        }).getDeliveryTimeList(storeId);
    }

    private void getExtraIntent() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            total = bundle.getString(Constants.CART_SUM);
            productsSize = bundle.getInt(Constants.CART_PRODUCT_COUNT, 0);
            cartResultModel = (CartResultModel) bundle.getSerializable(Constants.CART_MODEL);
            deliveryFees = bundle.getDouble(Constants.delivery_charges);
            productList = cartResultModel.getData().getCartData();
            binding.productsSizeTv.setText(total.concat(" " + currency));
            binding.totalTv.setText(NumberHandler.formatDouble(Double.parseDouble(total) + deliveryFees, fraction).concat(" " + currency));
            minimum_order_amount = cartResultModel.getMinimumOrderAmount();
            Log.i("tag","Log minimum_order_amount "+minimum_order_amount);
            Log.i("tag","Log deliveryFees "+deliveryFees);
            Log.i("tag","Log total "+total);

            if (deliveryFees > 0) {
                if (Double.parseDouble(total) >= minimum_order_amount) {
                    deliveryFees = 0.0;
                }
            }


        }


    }


    private void sendOrder(OrderCall orderCall) {

        GlobalData.progressDialog(getActivityy(), R.string.make_order, R.string.please_wait_sending);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();
            OrdersResultModel result = (OrdersResultModel) obj;
            if (func.equals(Constants.ERROR)) {
                String message = getString(R.string.fail_to_send_order);
                if (result != null && result.getMessage() != null) {
                    message = result.getMessage();
                }
                GlobalData.errorDialog(getActivityy(), R.string.make_order, message);
            } else {
                if (IsSuccess) {
                    if (result.getStatus() == 200) {
                        UtilityApp.setCartCount(0);
                        AwesomeSuccessDialog successDialog = new AwesomeSuccessDialog(getActivityy());
                        successDialog.setTitle(R.string.make_order).setMessage(R.string.sending_order).setColoredCircle(R.color.dialogSuccessBackgroundColor).setDialogIconAndColor(R.drawable.ic_check, R.color.white).show().setOnDismissListener(dialogInterface -> {
                            startMain();
                        });
                        successDialog.show();
                    } else {
                        String message = getString(R.string.fail_to_send_order);
                        if (result != null && result.getMessage() != null) {
                            message = result.getMessage();
                        }
                        GlobalData.errorDialog(getActivityy(), R.string.make_order, message);

                    }


                } else {
                    Toast(getString(R.string.fail_to_send_order));

                }
            }


        }).makeOrder(orderCall);

    }

    public void startMain() {
        Intent intent = new Intent(getActivityy(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    @Override
    public void onContainerSelectSelected(AddressModel addressesDM) {

        addressId = addressesDM.getId();

    }


    @Override
    public void onAddressSelected(AddressModel addressesDM) {
        addressId = addressesDM.getId();

    }


    private void initDaysAdapter() {

        int dateId = 0;
        DayList = new ArrayList<>();
        for (String s : datesMap.keySet()) {
            DeliveryTime deliveryTime = new DeliveryTime(dateId, s, s);
            DayList.add(deliveryTime);
            dateId++;
        }


        deliveryDayAdapter = new DeliveryDayAdapter(getActivityy(), DayList, (obj, func, IsSuccess) -> {
            DeliveryTime deliveryTime = (DeliveryTime) obj;
            deliveryTimesList = datesMap.get(deliveryTime.getTime());

            binding.DeliverTimeRecycler.setVisibility(View.VISIBLE);
            expressDelivery = false;
            binding.quickButton.setBackground(ContextCompat.getDrawable(getActivityy(), R.drawable.round_big_corner_dark_gray));

            initTimesList();
            deliveryTimeAdapter.notifyDataSetChanged();


        });


        binding.DeliverDayRecycler.setAdapter(deliveryDayAdapter);

        initTimesList();


    }


    private void initTimeAdapter(Double deliveryFee) {
        if (paymentMethod.equals("CC")) {
            deliveryFee = 0.0;
        }

        deliveryTimeAdapter = new DeliveryTimeAdapter(getActivityy(), TimeList, deliveryFee, selectedPosition, (obj, func, IsSuccess) -> {

            DeliveryTime searchListItem = (DeliveryTime) obj;
            deliveryDateId = searchListItem.getId();


        });

        binding.DeliverTimeRecycler.setAdapter(deliveryTimeAdapter);
        deliveryTimeAdapter.notifyDataSetChanged();


    }

    @Override
    public void OnEditClicked(AddressModel addressModel, boolean isChecked, int position) {
        Intent intent = new Intent(getActivityy(), AddNewAddressActivity.class);
        intent.putExtra(Constants.KEY_EDIT, true);
        intent.putExtra(Constants.KEY_ADDRESS_ID, addressModel.getId());
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == ADDRESS_CODE) {
                assert data != null;
                Bundle bundle = data.getExtras();
                addressId = bundle.getInt(Constants.ADDRESS_ID);
                addressTitle = bundle.getString(Constants.ADDRESS_TITLE);
                addressFullAddress = bundle.getString(Constants.ADDRESS_FULL);
                binding.delivery.setText(addressTitle);
                binding.tvFullAddress.setText(addressFullAddress);
                binding.acceptBtu.setText(R.string.change_address);

            }


        }
    }


    public void GetUserAddress(int addressId) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            AddressResultModel result = (AddressResultModel) obj;
            if (IsSuccess) {
                binding.dataLY.setVisibility(View.VISIBLE);
                if (result.getData() != null && result.getData().size() > 0) {
                    AddressModel addressModel = result.getData().get(0);
                    addressTitle = addressModel.getName();
                    addressFullAddress = addressModel.getFullAddress();
                    binding.delivery.setText(addressTitle);
                    binding.tvFullAddress.setText(addressFullAddress);


                }


            }


        }).GetAddressByIdHandle(addressId);
    }


    @SuppressLint("SetTextI18n")
    public void getQuickDelivery(int storeId, int countryId) {

        QuickCall quickCall1 = new QuickCall();
        quickCall1.store_id = storeId;
        quickCall1.country_id = countryId;

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                String message = getString(R.string.fail_to_get_data);
                binding.loadingLYPay.setVisibility(View.GONE);
                ResultAPIModel<QuickDeliveryRespond> result = (ResultAPIModel<QuickDeliveryRespond>) obj;
                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.message != null) {
                        message = result.message;
                    }
                    GlobalData.Toast(getActivityy(), message);


                } else if (func.equals(Constants.FAIL)) {
                    GlobalData.Toast(getActivityy(), message);

                } else if (func.equals(Constants.NO_CONNECTION)) {
                    GlobalData.Toast(getActivityy(), R.string.no_internet_connection);
                } else {
                    if (IsSuccess) {
                        if (result.data != null) {
                            Log.i("tag", "Log result" + result.data);
                            QuickDeliveryRespond quickDeliveryRespond = result.data;
                            if (quickDeliveryRespond.isHasExpressDelivery()) {
                                binding.quickLy.setVisibility(View.VISIBLE);
                                deliveryFees = quickDeliveryRespond.getExpressDeliveryCharge();

                                if (deliveryFees == 0) {
                                    binding.deliveryFees.setText(getString(R.string.free));
                                    binding.freeDelivery.setText(getString(R.string.over1));
                                    binding.deliveryPrice.setText(getString(R.string.free));


                                } else {

                                    binding.deliveryFees.setText(NumberHandler.
                                            formatDouble(deliveryFees, localModel.getFractional()).concat("" + currency));
                                    binding.freeDelivery.setText(getString(R.string.over).concat(" " + minimum_order_amount + " " + currency + "."));

                                    binding.deliveryPrice.setText(quickDeliveryRespond.
                                            getExpressDeliveryCharge() + " ".concat(localModel.getCurrencyCode()));


                                }

                                binding.totalTv.setText(NumberHandler.formatDouble(Double.parseDouble(total) + deliveryFees, fraction).concat(" " + currency));


                            } else {
                                binding.quickLy.setVisibility(View.GONE);
                            }


                        }


                    }
                }

            }
        }).getQuickDelivery(quickCall1);
    }


}






