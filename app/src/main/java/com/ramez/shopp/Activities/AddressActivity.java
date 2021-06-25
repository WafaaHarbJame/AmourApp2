package com.ramez.shopp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ramez.shopp.Adapter.AddressAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.Dialogs.ConfirmDialog;
import com.ramez.shopp.Fragments.InvoiceFragment;
import com.ramez.shopp.Models.AddressModel;
import com.ramez.shopp.Models.AddressResultModel;
import com.ramez.shopp.Models.GeneralModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.RootApplication;
import com.ramez.shopp.Utils.MapHandler;
import com.ramez.shopp.databinding.ActivityAddressBinding;

import java.util.ArrayList;

public class AddressActivity extends ActivityBase implements AddressAdapter.OnRadioAddressSelect, AddressAdapter.OnContainerSelect, AddressAdapter.OnDeleteClicked {
    ActivityAddressBinding binding;
    ArrayList<AddressModel> addressList;
    boolean deliveryChoose, addNewAddress;
    private AddressAdapter addressAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int defaultAddressId;
    private MemberModel user;
    private int ADD_ADDRESS = 4000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        user = UtilityApp.getUserData();

        setTitle(R.string.address);

        addressList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(getActiviy());
        binding.addressRecycler.setLayoutManager(linearLayoutManager);

        getIntentExtra();


        if (UtilityApp.isLogin()) {
            if (user != null&&user.getId()!=null) {
                GetUserAddress(user.getId());

            }

        }


        binding.addNewAddressBut.setOnClickListener(view1 -> {

            addNewAddress();
        });

        binding.swipe.setOnRefreshListener(() -> {
            binding.swipe.setRefreshing(false);
            GetUserAddress(user.getId());

        });

        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
            GetUserAddress(user.getId());
        });


        binding.noDataLY.addAddressBut.setOnClickListener(view1 -> {
            addNewAddress();

        });


        binding.acceptBtu.setOnClickListener(view1 -> {

            setDefaultAddress(user.getId(), defaultAddressId);

        });



    }



    @Override
    public void onAddressSelected(AddressModel addressesDM) {
        defaultAddressId = addressesDM.getId();

        if (deliveryChoose) {
            Intent intent = new Intent(AddressActivity.this, InvoiceFragment.class);
            intent.putExtra(Constants.ADDRESS_ID, addressesDM.getId());
            intent.putExtra(Constants.ADDRESS_TITLE, addressesDM.getName());
            intent.putExtra(Constants.ADDRESS_FULL, addressesDM.getFullAddress());
            setResult(Activity.RESULT_OK, intent);

        }


    }


    public void initAdapter() {

        addressAdapter = new AddressAdapter(getActiviy(), addressList, this, this, this);
        binding.addressRecycler.setAdapter(addressAdapter);
        addressAdapter.notifyDataSetChanged();


    }

    private void addNewAddress() {
        Intent intent = new Intent(getActiviy(), AddNewAddressActivity.class);
        startActivityForResult(intent, ADD_ADDRESS);
    }

    public void GetUserAddress(int user_id) {

        addressList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);


        new DataFeacher(false, (obj, func, IsSuccess) -> {
            binding.dataLY.setVisibility(View.VISIBLE);
            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

            AddressResultModel result = (AddressResultModel) obj;

            if (func.equals(Constants.ERROR) || func.equals(Constants.FAIL)) {

                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(R.string.error_in_data);
                binding.dataLY.setVisibility(View.GONE);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                binding.dataLY.setVisibility(View.GONE);

            } else {

                if (IsSuccess) {
                    binding.dataLY.setVisibility(View.VISIBLE);

                    if (result.getData() != null && result.getData().size() > 0) {
                        addressList = result.getData();
                        initAdapter();

                    } else {
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failTxt.setText(R.string.no_address);
                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);

                    }


                } else {
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(R.string.no_address);
                    binding.dataLY.setVisibility(View.VISIBLE);

                }
            }

        }).GetAddressHandle(user_id);
    }

    private void getIntentExtra() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            deliveryChoose = bundle.getBoolean(Constants.delivery_choose, false);

        }
    }

    @Override
    public void onContainerSelectSelected(AddressModel addressesDM) {
        if (deliveryChoose) {
            Intent intent = new Intent(AddressActivity.this, InvoiceFragment.class);
            intent.putExtra(Constants.ADDRESS_ID, addressesDM.getId());
            intent.putExtra(Constants.ADDRESS_TITLE, addressesDM.getName());
            intent.putExtra(Constants.ADDRESS_FULL, addressesDM.getFullAddress());
            setResult(Activity.RESULT_OK, intent);
            finish();

        } else {

            Intent intent = new Intent(getActiviy(), AddNewAddressActivity.class);
            intent.putExtra(Constants.KEY_EDIT, true);
            intent.putExtra(Constants.KEY_ADDRESS_ID, addressesDM.getId());
            startActivity(intent);
        }

    }

    public void setDefaultAddress(int user_id, int address_id) {
        GlobalData.progressDialog(getActiviy(), R.string.default_address, R.string.please_wait_sending);
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
            binding.dataLY.setVisibility(View.VISIBLE);

            if (func.equals(Constants.ERROR)) {

                Toast(R.string.error_in_data);

            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_get_data);
            } else {

                if (IsSuccess) {

                    GlobalData.hideProgressDialog();

                    GlobalData.successDialog(getActiviy(), getString(R.string.default_address), getString(R.string.address_default));

                    user.setLastSelectedAddress(defaultAddressId);
                    UtilityApp.setUserData(user);
                    addressAdapter.notifyDataSetChanged();

                }
            }

        }).setDefaultAddress(user_id, address_id);
    }


    public void deleteAddressId(int addressId,int position) {
        ConfirmDialog.Click click = new ConfirmDialog.Click() {
            @Override
            public void click() {
                new DataFeacher(false, (obj, func, IsSuccess) -> {
                    if (func.equals(Constants.ERROR)) {
                        Toast.makeText(getActiviy(), getString(R.string.error_in_data), Toast.LENGTH_SHORT).show();
                    } else if (func.equals(Constants.FAIL)) {
                        Toast.makeText(getActiviy(), getString(R.string.fail_delete_address), Toast.LENGTH_SHORT).show();

                    }

                    else if (func.equals(Constants.NO_CONNECTION)) {
                        Toast.makeText(getActiviy(),getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                    }
                    else {
                        if (IsSuccess) {

                            if(UtilityApp.getUserData().lastSelectedAddress==addressId){
                                MemberModel memberModel=UtilityApp.getUserData();
                                memberModel.setLastSelectedAddress(0);
                                UtilityApp.setUserData(memberModel);
                            }
                            if(addressList.size()>0){
                                addressList.remove(position);
                                addressAdapter.notifyDataSetChanged();
                                addressAdapter.notifyItemRemoved(position);

                            }

                            if(addressList.size()==0){

                                binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                                binding.failGetDataLY.failTxt.setText(R.string.no_address);
                                binding.dataLY.setVisibility(View.VISIBLE);
                                binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
                            }

                        } else {

                            Toast.makeText(getActiviy(),getString(R.string.fail_to_get_data), Toast.LENGTH_SHORT).show();
                        }
                    }

                }).deleteAddressHandle(addressId);
            }
        };

        new ConfirmDialog(getActiviy(), getString(R.string.want_to_delete_address), R.string.ok, R.string.cancel_label, click, null,false);

    }

    @Override
    public void onDeleteClicked(AddressModel addressModel, boolean isChecked, int position) {

        deleteAddressId(addressModel.getId(),position);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_ADDRESS) {
                if (data != null) {
                    Bundle bundle=data.getExtras();

                    addNewAddress = bundle.getBoolean(Constants.KEY_ADD_NEW, false);
                    Log.i("TAG","Log addNewAddress onActivityResult  "+addNewAddress);

                    if (addNewAddress) {
                        GetUserAddress(user.getId());

                    }


                }

            }



        }
    }




}

