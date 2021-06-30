package com.ramez.shopp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Activities.AddNewAddressActivity;
import com.ramez.shopp.Activities.AddressActivity;
import com.ramez.shopp.Activities.OrderCompleteActivity;
import com.ramez.shopp.Adapter.AddressCheckAdapter;
import com.ramez.shopp.Adapter.DeliveryDayAdapter;
import com.ramez.shopp.Adapter.DeliveryTimeAdapter;
import com.ramez.shopp.Adapter.PaymentAdapter;
import com.ramez.shopp.Adapter.ProductCheckAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.AnalyticsHandler;
import com.ramez.shopp.Classes.CartModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.DeliveryInfo;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.ProductChecker;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.AddressModel;
import com.ramez.shopp.Models.CartResultModel;
import com.ramez.shopp.Models.CheckOrderModel;
import com.ramez.shopp.Models.CheckOrderResponse;
import com.ramez.shopp.Models.DeliveryTime;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.OrderCall;
import com.ramez.shopp.Models.OrderModel;
import com.ramez.shopp.Models.OrdersResultModel;
import com.ramez.shopp.Models.PaymentModel;
import com.ramez.shopp.Models.PaymentResultModel;
import com.ramez.shopp.Models.QuickCall;
import com.ramez.shopp.Models.QuickDeliveryRespond;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.UserDefaultAddress;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.FragmentInvoiceBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class InvoiceFragment extends FragmentBase implements AddressCheckAdapter.OnRadioAddressSelect, AddressCheckAdapter.OnContainerSelect, AddressCheckAdapter.OnEditClick {
    private static final int ADDRESS_CODE = 100;
    public Integer userId;
    public String couponCodeId = "0";
    public String deliveryDate, deliveryTime;
    public Boolean expressDelivery = false;
    public DeliveryDayAdapter deliveryDayAdapter;
    public DeliveryTimeAdapter deliveryTimeAdapter;
    public ProductCheckAdapter productCheckerAdapter;
    ArrayList<PaymentModel> paymentList;
    List<DeliveryTime> deliveryTimesList;
    ArrayList<CartModel> productList;
    GridLayoutManager payLinearLayoutManager;
    List<ProductChecker> productCheckerList;
    private boolean toggleButton = false;

    LocalModel localModel;
    int storeId, productsSize;
    String total = "", currency = "";
    List<DeliveryTime> DayList;
    int fraction = 2;
    ArrayList<AddressModel> addressList;
    private FragmentInvoiceBinding binding;
    private PaymentAdapter paymentAdapter;
    private String addressTitle;
    private String addressFullAddress;
    private double deliveryFees = 0.0;
    private double expressDeliveryCharge = 0.0;
    private CartResultModel cartResultModel;
    private int minimum_order_amount = 0;
    private LinkedHashMap<String, List<DeliveryTime>> datesMap = new LinkedHashMap<>();
    private boolean toggleDeliveryButton = false;
    private boolean toggleProductButton = false;

    private ActivityResultLauncher<Intent> selectAddressLauncher = null;

    public PaymentModel selectedPaymentMethod = null;

    private int addressId = 0;
    public Integer deliveryDateId = 0;
    int itemNotFoundId = 0;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInvoiceBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectAddressLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    initAddressData(result.getResultCode(), result.getData());
                }
        );

        paymentList = new ArrayList<>();
        addressList = new ArrayList<>();
        deliveryTimesList = new ArrayList<>();

        DayList = new ArrayList<>();
        productCheckerList = new ArrayList<>();

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
        binding.ProductCheckRecycler.setHasFixedSize(true);


        LinearLayoutManager deliverTimeLlm = new LinearLayoutManager(getActivityy());
        deliverTimeLlm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.DeliverTimeRecycler.setLayoutManager(deliverTimeLlm);


        LinearLayoutManager checkProductLlm = new LinearLayoutManager(getActivityy());
        checkProductLlm.setOrientation(LinearLayoutManager.VERTICAL);
        binding.ProductCheckRecycler.setLayoutManager(checkProductLlm);


        getExtraIntent();
        getPaymentMethod(storeId);
        getDeliveryTimeList(storeId, userId);
        initListener();
        // checkDeliveryFees();


    }

    private void checkDeliveryFees() {

        if (deliveryFees == 0) {
            binding.deliveryFees.setText(getString(R.string.free));
            binding.freeDelivery.setText(getString(R.string.over1));


        } else {

            binding.deliveryFees.setText(NumberHandler.
                    formatDouble(deliveryFees, localModel.getFractional()).concat("" + currency));
            binding.freeDelivery.setText(getString(R.string.over).concat(" " + minimum_order_amount + " " + currency + "."));

        }
    }

    private void getDefaultAddress() {
        if (UtilityApp.getUserData().lastSelectedAddress > 0) {
            addressId = UtilityApp.getUserData().lastSelectedAddress;
            GetDeliveryInfo(storeId, addressId);
        } else {
            binding.changeAddressBtu.setText(R.string.select_address);
        }


    }

    @SuppressLint("SetTextI18n")
    private void initListener() {

        binding.saveBut.setOnClickListener(view1 -> {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_POSITION, 0));

        });

        binding.sendOrder.setOnClickListener(view1 -> {

            if (selectedPaymentMethod == null) {
                Toast(R.string.please_select_payment_method);
                return;
            }

            if (!selectedPaymentMethod.getShortname().equals("CC") && addressId == 0) {
                Toast(R.string.choose_address);
                binding.tvFullAddress.setFocusable(true);
                binding.tvFullAddress.setError(getString(R.string.choose_address));
                return;
            }

            if (deliveryDateId == 0) {
                Toast(R.string.select_delivery_time);
                return;
            }

            if (itemNotFoundId == 0) {
                Toast(R.string.check_product);
                return;
            }

            OrderCall orderCall = new OrderCall();
            orderCall.user_id = userId;
            orderCall.store_ID = storeId;
            orderCall.address_id = addressId;
            orderCall.payment_method = selectedPaymentMethod.getShortname();
            orderCall.coupon_code_id = couponCodeId;
            orderCall.delivery_date_id = deliveryDateId;
            orderCall.itemNotFoundAction = itemNotFoundId;
            orderCall.expressDelivery = expressDelivery;

            sendOrder(orderCall);


        });


        binding.changeAddressBtu.setOnClickListener(view12 -> {
            Intent intent = new Intent(getActivityy(), AddressActivity.class);
//            intent.putExtra(Constants.delivery_choose, true);
            selectAddressLauncher.launch(intent);

        });

        binding.freeBut.setOnClickListener(view -> {
            if (deliveryFees > 0) {
                if (Double.parseDouble(total) >= minimum_order_amount) {
                    Toast(getString(R.string.getFreeDelivery));

                } else {

//                    double total_price = minimum_order_amount - Double.parseDouble(total);
//
//                    binding.freeBut.setText(getString(R.string.add)+" "+total_price +" "+currency+ " " + getString(R.string.get_Free));
////                    Toast(getString(R.string.Add_more) + " " + NumberHandler.formatDouble(total_price, UtilityApp.getLocalData().getFractional())
////                            + " " + currency + " " + getString(R.string.get_Free));

                    CategoryFragment categoryFragment = new CategoryFragment();
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.mainContainer, categoryFragment, "homeFragment").commit();

                }
            } else {
                Toast(getString(R.string.getFreeDelivery));

            }


        });


        binding.quickLy.setOnClickListener(view -> {


            toggleButton = !toggleButton;

            if (toggleButton) {
                expressDelivery = true;
                deliveryDayAdapter.lastIndex = -1;
                deliveryDayAdapter.notifyDataSetChanged();
                binding.DeliverTimeRecycler.setVisibility(View.GONE);
                binding.quickLy.setBackground(ContextCompat.getDrawable(getActivityy(), R.drawable.round_corner_white_fill_green_border));
                binding.totalTv.setText(NumberHandler.formatDouble(Double.parseDouble(total) + expressDeliveryCharge, fraction).concat(" " + currency));

                if (expressDeliveryCharge == 0 || expressDeliveryCharge == 0.0) {
                    binding.deliveryFees.setText(getString(R.string.free));
                }

            } else {
                expressDelivery = false;
                binding.quickLy.setBackground(ContextCompat.getDrawable(getActivityy(), R.drawable.round_corner_gray_border_fill));
                binding.totalTv.setText(NumberHandler.formatDouble(Double.parseDouble(total) + deliveryFees, fraction).concat(" " + currency));
                checkDeliveryFees();


            }


        });


        binding.choosePaymentType.setOnClickListener(view1 -> {

            showHidePaymentLY(binding.paymentRv.getVisibility() == View.GONE);

        });

        binding.chooseDeliveryBtn.setOnClickListener(view1 -> {

            showHideDeliveryLY(binding.DeliverLY.getVisibility() == View.GONE);

        });

        binding.chooseDeliveryTime.setOnClickListener(view1 -> {

            showHideDateLY(binding.DeliverDayRecycler.getVisibility() == View.GONE);


        });

        binding.checkProductLy.setOnClickListener(view1 -> {
            showHideNoProductLY(binding.ProductCheckRecycler.getVisibility() == View.GONE);

        });
    }

    private void initTimesList() {

        initTimeAdapter(deliveryFees);

    }

    public void initPaymentAdapter() {

        for (int i = 0; i < paymentList.size(); i++) {
            PaymentModel paymentModel = paymentList.get(i);
            if (paymentModel.getId() == 1) {
                paymentModel.setImage(R.drawable.cash);
            } else if (paymentModel.getId() == 2) {
                paymentModel.setImage(R.drawable.collect);
            } else if (paymentModel.getId() == 3) {
                paymentModel.setImage(R.drawable.benefit);
            } else if (paymentModel.getId() == 4) {
                paymentModel.setImage(R.drawable.card);
            }

        }

        paymentAdapter = new PaymentAdapter(getActivityy(), paymentList, (obj, func, IsSuccess) -> {
            selectedPaymentMethod = (PaymentModel) obj;
            Log.i(getClass().getSimpleName(), "Log selectedPaymentMethod " + selectedPaymentMethod.getShortname());


            if (selectedPaymentMethod != null) {
                if (selectedPaymentMethod.getShortname().equals("CC")) {
                    binding.chooseDelivery.setVisibility(View.GONE);
                    initTimeAdapter(0.0);
                    binding.deliveryFees.setText(getString(R.string.free));
                    binding.totalTv.setText(NumberHandler.formatDouble(Double.parseDouble(NumberHandler.formatDouble(Double.parseDouble(total), fraction)) + 0.0, fraction).concat(" " + currency));

                } else {
                    binding.chooseDelivery.setVisibility(View.VISIBLE);
                    initTimeAdapter(deliveryFees);
                    binding.deliveryFees.setText(NumberHandler.formatDouble(deliveryFees, localModel.getFractional()).concat("" + currency));
                    binding.totalTv.setText(NumberHandler.formatDouble(Double.parseDouble(total) + deliveryFees, fraction).concat(" " + currency));

                }

                showHidePaymentLY(false);
                checkData();

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
                            initPaymentAdapter();
                            initData();

                        }


                    }
                }
            }


        }).getPaymentMethod(user_id);
    }

    @SuppressLint("SetTextI18n")
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
            minimum_order_amount = cartResultModel.getData().getMinimumOrderAmount();
            Log.i(getClass().getSimpleName(), "Log minimum_order_amount " + minimum_order_amount);
            Log.i(getClass().getSimpleName(), "Log deliveryFees " + deliveryFees);
            Log.i(getClass().getSimpleName(), "Log total " + total);

            if (deliveryFees > 0) {
                if (Double.parseDouble(total) >= minimum_order_amount) {
                    deliveryFees = 0.0;
                } else {
                    double total_price = minimum_order_amount - Double.parseDouble(total);

                    binding.freeBut.setText(getString(R.string.add) + " " + NumberHandler.roundDouble(total_price) + " " + currency + " " + getString(R.string.get_Free));

                }
            } else {

                double total_price = minimum_order_amount - Double.parseDouble(total);

                binding.freeBut.setText(getString(R.string.add) + " " + NumberHandler.roundDouble(total_price) + " " + currency + " " + getString(R.string.get_Free));
//
            }


        }


    }

    private void sendOrder(OrderCall orderCall) {
        AnalyticsHandler.checkOut(couponCodeId, currency, total, total);

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


                        if (result.getOrder_id() > 0) {
                            AnalyticsHandler.PurchaseEvent(couponCodeId, currency, selectedPaymentMethod.getId(), deliveryFees,
                                    String.valueOf(result.getOrder_id()), total);

                            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_POSITION, 0));

                            System.out.println("Log order deliveryDate " + deliveryDate);
                            System.out.println("Log order deliveryTime " + deliveryTime);
                            OrderModel ordersDM = new OrderModel();
                            ordersDM.setOrderId(result.getOrder_id());
                            ordersDM.setDeliveryDate(deliveryDate);
                            ordersDM.setDeliveryTime(deliveryTime);
                            Intent intent = new Intent(getActivityy(), OrderCompleteActivity.class);
                            intent.putExtra(Constants.ORDER_MODEL, ordersDM);
                            intent.putExtra(Constants.KEY_SHOW, true);
                            startActivity(intent);
                        }


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


    private void initData() {

        binding.freeLY.setVisibility(View.VISIBLE);
        getDeliveryTimeList(storeId, userId);
        getQuickDelivery(storeId, localModel.getCountryId());
        getDefaultAddress();
        getProductCheckerList();
        checkData();

    }

    private void showHidePaymentLY(boolean show) {

        if (show) {
            binding.paymentArrowImg.setImageDrawable(ContextCompat.getDrawable(getActivityy(), R.drawable.ic_angle_up));
            binding.paymentRv.setVisibility(View.VISIBLE);
        } else {
            binding.paymentArrowImg.setImageDrawable(ContextCompat.getDrawable(getActivityy(), R.drawable.ic_angle_down));
            binding.paymentRv.setVisibility(View.GONE);
        }

    }

    private void showHideDeliveryLY(boolean show) {

        if (show) {
            binding.deliveryArrowImg.setImageDrawable(ContextCompat.getDrawable(getActivityy(), R.drawable.ic_angle_up));
            binding.DeliverLY.setVisibility(View.VISIBLE);
        } else {
            binding.deliveryArrowImg.setImageDrawable(ContextCompat.getDrawable(getActivityy(), R.drawable.ic_angle_down));
            binding.DeliverLY.setVisibility(View.GONE);
        }

    }

    private void showHideDateLY(boolean show) {

        if (show) {

            binding.toggleDeliveryBut.setImageDrawable(ContextCompat.getDrawable(getActivityy(), R.drawable.ic_angle_up));
            binding.DeliverDayRecycler.setVisibility(View.VISIBLE);
            binding.DeliverTimeRecycler.setVisibility(View.VISIBLE);

        } else {

            binding.toggleDeliveryBut.setImageDrawable(ContextCompat.getDrawable(getActivityy(), R.drawable.ic_angle_down));
            binding.DeliverDayRecycler.setVisibility(View.GONE);
            binding.DeliverTimeRecycler.setVisibility(View.GONE);
        }

    }


    private void showHideNoProductLY(boolean show) {

        if (show) {
            binding.toggleCheckerBut.setImageDrawable(ContextCompat.getDrawable(getActivityy(), R.drawable.ic_angle_up));
            binding.ProductCheckRecycler.setVisibility(View.VISIBLE);

        } else {
            binding.toggleCheckerBut.setImageDrawable(ContextCompat.getDrawable(getActivityy(), R.drawable.ic_angle_down));
            binding.ProductCheckRecycler.setVisibility(View.GONE);

        }

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
            binding.quickLy.setBackground(ContextCompat.getDrawable(getActivityy(), R.drawable.round_corner_gray_border_fill));

            initTimesList();


        });


        binding.DeliverDayRecycler.setAdapter(deliveryDayAdapter);

        initTimesList();


    }


    private void initTimeAdapter(Double deliveryFee) {
        if (selectedPaymentMethod != null && selectedPaymentMethod.getShortname().equals("CC")) {
            deliveryFee = 0.0;
        }

        deliveryTimeAdapter = new DeliveryTimeAdapter(getActivityy(), deliveryTimesList, deliveryFee, deliveryDateId, (obj, func, IsSuccess) -> {

            DeliveryTime selectedTime = (DeliveryTime) obj;
            deliveryDateId = selectedTime.getId();
            deliveryDate = selectedTime.getDate();
            deliveryTime = selectedTime.getTime();

            Log.i(getClass().getSimpleName(), "Log deliveryTimesList click " + deliveryDateId);
            Log.i(getClass().getSimpleName(), "Log deliveryTimesList click " + selectedTime.getTime());

            // to hide layout when select time
            showHideDateLY(false);
            showHideNoProductLY(true);
//            checkData();
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

    public void initAddressData(int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_invoice));
            if (data != null) {
                Bundle bundle = data.getExtras();
                addressId = bundle.getInt(Constants.ADDRESS_ID);
                addressTitle = bundle.getString(Constants.ADDRESS_TITLE);
                addressFullAddress = bundle.getString(Constants.ADDRESS_FULL);
                binding.delivery.setText(addressTitle);
                binding.tvFullAddress.setText(addressFullAddress);
                binding.changeAddressBtu.setText(R.string.change_address);
                AnalyticsHandler.addShippingInfo(couponCodeId, currency, total, total);
                GetDeliveryInfo(storeId, addressId);
                showHideDeliveryLY(false);
                showHideDateLY(true);
            }

        }

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
                            Log.i(getClass().getSimpleName(), "Log result" + result.data);
                            QuickDeliveryRespond quickDeliveryRespond = result.data;
                            if (quickDeliveryRespond.isHasExpressDelivery()) {
                                binding.quickLy.setVisibility(View.VISIBLE);
                                deliveryFees = quickDeliveryRespond.getExpressDeliveryCharge();
                                expressDeliveryCharge = quickDeliveryRespond.getExpressDeliveryCharge();
                                binding.deliveryTime.setText(quickDeliveryRespond.getExpressDeliverydescription());
                                Log.i(getClass().getSimpleName(), "Log 1 deliveryFees" + deliveryFees);

                                if (expressDeliveryCharge == 0 || expressDeliveryCharge == 0.0) {
                                    binding.deliveryFees.setText(getString(R.string.free));
                                    binding.freeDelivery.setText(getString(R.string.over1));
                                    binding.deliveryPrice.setText(getString(R.string.free));


                                } else {

                                    binding.deliveryFees.setText(NumberHandler.
                                            formatDouble(expressDeliveryCharge, localModel.getFractional()).concat("" + currency));
                                    binding.freeDelivery.setText(getString(R.string.over).concat(" " + minimum_order_amount + " " + currency + "."));

                                    binding.deliveryPrice.setText(quickDeliveryRespond.
                                            getExpressDeliveryCharge() + " ".concat(localModel.getCurrencyCode()));


                                }

                            } else {
                                binding.quickLy.setVisibility(View.GONE);
                            }


                        }


                    }
                }

            }
        }).getQuickDelivery(quickCall1);
    }

    public void GetDeliveryInfo(int storeId, int addressId) {


        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {
                binding.loadingLYPay.setVisibility(View.GONE);
                DeliveryInfo quickDeliveryRespond = (DeliveryInfo) obj;
                if (IsSuccess) {
                    if (quickDeliveryRespond != null) {

                        Log.i(TAG, "Log GetDeliveryInfo");
                        deliveryFees = quickDeliveryRespond.deliveryCharges;
                        if (deliveryFees == 0) {
                            binding.deliveryFees.setText(getString(R.string.free));
                            binding.freeDelivery.setText(getString(R.string.over1));
                            binding.deliveryPrice.setText(getString(R.string.free));


                        } else {

                            binding.deliveryFees.setText(NumberHandler.
                                    formatDouble(deliveryFees, localModel.getFractional()).concat("" + currency));
                            binding.freeDelivery.setText(getString(R.string.over).concat(" " + minimum_order_amount + " " + currency + "."));

                            binding.deliveryPrice.setText(quickDeliveryRespond.expressDeliveryCharges + " ".concat(localModel.getCurrencyCode()));


                        }

                        binding.totalTv.setText(NumberHandler.formatDouble(Double.parseDouble(total) + deliveryFees, fraction).concat(" " + currency));


                    } else {
                        binding.quickLy.setVisibility(View.GONE);
                    }


                }


            }
        }).GetDeliveryInfo(storeId, addressId);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NotNull MessageEvent event) {


    }

    private void getProductCheckerList() {
        productCheckerList.add(new ProductChecker(1, getString(R.string.product_not_found_1)));
        productCheckerList.add(new ProductChecker(2, getString(R.string.product_not_found_2)));
        productCheckerList.add(new ProductChecker(3, getString(R.string.product_not_found_3)));
        productCheckerAdapter = new ProductCheckAdapter(getActivityy(), productCheckerList, itemNotFoundId, (obj, func, IsSuccess) -> {
            ProductChecker productChecker = (ProductChecker) obj;
            itemNotFoundId = productChecker.getId();
            showHideNoProductLY(false);
//            checkData();

        });
        binding.ProductCheckRecycler.setAdapter(productCheckerAdapter);
    }

    private void checkData() {

        Log.i(getClass().getSimpleName(), "Log deliveryDateId " + deliveryDateId);

        if (selectedPaymentMethod == null) {
            showHidePaymentLY(true);
            return;
        }

        if (!selectedPaymentMethod.getShortname().equals("CC") && addressId == 0) {
            showHideDeliveryLY(true);
            binding.changeAddressBtu.performClick();
            return;
        }

//        if (deliveryDateId == 0) {
        if (binding.DeliverDayRecycler.getVisibility() == View.GONE) {

            showHideDateLY(true);

            return;
        }


        if (itemNotFoundId == 0) {

            showHideNoProductLY(true);


        }

    }

    public void getDeliveryTimeList(int storeId, int user_id) {
        datesMap.clear();
        binding.loadingDelivery.setVisibility(View.VISIBLE);


        new DataFeacher(true, (obj, func, IsSuccess) -> {
            if (isVisible()) {
                CheckOrderResponse result = (CheckOrderResponse) obj;
                String message = getString(R.string.fail_to_get_data);

                binding.loadingDelivery.setVisibility(View.GONE);

                if (IsSuccess) {
                    if (result.getData() != null && result.getData().getDeliveryTimes() != null
                            && result.getData().getDeliveryTimes().size() > 0) {

                        if (result.getData() != null && result.getData().getDeliveryTimes().size() > 0) {
                            CheckOrderModel checkOrderResponse = result.getData();
                            UserDefaultAddress userDefaultAddress = checkOrderResponse.getUserAddress();
                            addressId = userDefaultAddress.getId();
                            addressTitle = userDefaultAddress.getName();
                            binding.tvFullAddress.setText(userDefaultAddress.getFullAddress());
                            binding.delivery.setText(addressTitle);
                            Log.i(getClass().getSimpleName(), "Log  CheckOrderResponse AddressId  " + result.getData().getUserAddress().getId());
                            minimum_order_amount = checkOrderResponse.getMinimumOrderAmount();
                            deliveryFees = checkOrderResponse.getDeliveryCharges();
                            checkDeliveryFees();

                            List<DeliveryTime> datesList = result.getData().getDeliveryTimes();
                            DeliveryTime firstTime = datesList.get(0);

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
                            if (deliveryTimesList != null && deliveryTimesList.size() > 0)
                                deliveryDateId = deliveryTimesList.get(0).getId();
                            deliveryDate = firstTime.getDate();
                            deliveryTime = firstTime.getTime();

                            Log.i(getClass().getSimpleName(), "Log deliveryTimesList click " + deliveryDateId);
                            Log.i(getClass().getSimpleName(), "Log deliveryTimesList click " + deliveryTime);

                            initDaysAdapter();


                        } else {
                            binding.noDeliveryTv.setVisibility(View.VISIBLE);
                            GlobalData.Toast(getActivityy(), message);

                        }


                    }
                }
            }

        }).getDeliveryTimeList(storeId, user_id);

    }


}






