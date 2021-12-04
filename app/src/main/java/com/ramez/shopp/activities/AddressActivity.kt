package com.ramez.shopp.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.GlobalData
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Dialogs.CheckLoginDialog
import com.ramez.shopp.Dialogs.ConfirmDialog
import com.ramez.shopp.Models.AddressModel
import com.ramez.shopp.Models.AddressResultModel
import com.ramez.shopp.Models.MemberModel
import com.ramez.shopp.R
import com.ramez.shopp.adapter.AddressAdapter
import com.ramez.shopp.adapter.AddressAdapter.OnDeleteClicked
import com.ramez.shopp.databinding.ActivityAddressBinding
import java.util.ArrayList


class AddressActivity : ActivityBase(), AddressAdapter.OnContainerSelect,
        OnDeleteClicked {
   private lateinit var binding: ActivityAddressBinding
    var addressList: ArrayList<AddressModel>? = null
    var addNewAddress = false
    private var addressAdapter: AddressAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var defaultAddressId = 0
    private var user: MemberModel? = null
    private val addAddress = 4000
    private var addAddressLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        user = UtilityApp.getUserData()
        setTitle(R.string.address)
        addressList = ArrayList()
        linearLayoutManager = LinearLayoutManager(activity)
        binding.addressRecycler.layoutManager = linearLayoutManager

        address
        binding.addNewAddressBut.setOnClickListener { addNewAddress() }
        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = false
            address
        }
        binding.failGetDataLY.refreshBtn.setOnClickListener { address }
        binding.noDataLY.addAddressBut.setOnClickListener { addNewAddress() }
        binding.acceptBtu.setOnClickListener {
            Log.i(javaClass.simpleName, "Log defaultAddressId  $defaultAddressId")
            if (defaultAddressId != 0) setDefaultAddress(
                user?.id ?:0,
                defaultAddressId
            ) else Toast(R.string.please_select_default_address)
        }


        addAddressLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result != null && result.data != null) {

                if (result.resultCode == RESULT_OK) {
                        if (result.data != null) {
                            val bundle = result.data?.extras
                            addNewAddress = bundle?.getBoolean(Constants.KEY_ADD_NEW, false) ?: false
                            Log.i(
                                javaClass.simpleName,
                                "Log addNewAddress onActivityResult  $addNewAddress"
                            )
                            if (addNewAddress) {
                                address
                            }
                        }
                    }


            }
        }


    }


    @SuppressLint("NotifyDataSetChanged")
    fun initAdapter() {
        for (addressModel in addressList ?: mutableListOf()) {
            if (addressModel.default && addressModel.id != user?.getLastSelectedAddress()) {
                user?.setLastSelectedAddress(addressModel.id)
                UtilityApp.setUserData(user)
            }
        }
        val canSelect = callingActivity != null
        addressAdapter = AddressAdapter(activity, addressList, canSelect, this, this)
        binding.addressRecycler.adapter = addressAdapter
        addressAdapter?.notifyDataSetChanged()
    }

    private fun addNewAddress() {
        val intent = Intent(activity, AddNewAddressActivity::class.java)
        addAddressLauncher?.launch(intent)

    }

    fun getUserAddress(user_id: Int) {
        addressList?.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                binding.dataLY.visibility = View.VISIBLE
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                if (func == Constants.ERROR || func == Constants.FAIL) {
                    binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                    binding.failGetDataLY.failTxt.setText(R.string.error_in_data)
                    binding.dataLY.visibility = View.GONE
                } else if (func == Constants.NO_CONNECTION) {
                    binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                    binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                    binding.failGetDataLY.noInternetIv.visibility = View.VISIBLE
                    binding.dataLY.visibility = View.GONE
                } else {
                    if (IsSuccess) {
                        val result = obj as AddressResultModel
                        binding.dataLY.visibility = View.VISIBLE
                        if (result.data != null && result.data.size > 0) {
                            addressList = result.data
                            initAdapter()
                        } else {
                            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                            binding.failGetDataLY.failTxt.setText(R.string.no_address)
                            binding.dataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataLY.visibility = View.VISIBLE
                        }
                    } else {
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.setText(R.string.no_address)
                        binding.dataLY.visibility = View.VISIBLE
                    }
                }            }

        }).GetAddressHandle(user_id)
    }


    override fun onContainerSelectSelected(addressesDM: AddressModel, makeDefault: Boolean) {
        if (makeDefault) {
            defaultAddressId = addressesDM.id
            return
        }
        if (callingActivity != null) {
            val intent = Intent()
            intent.putExtra(Constants.ADDRESS_ID, addressesDM.id)
            intent.putExtra(Constants.ADDRESS_TITLE, addressesDM.name)
            intent.putExtra(Constants.ADDRESS_FULL, addressesDM.fullAddress)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            val intent = Intent(activity, AddNewAddressActivity::class.java)
            intent.putExtra(Constants.KEY_EDIT, true)
            intent.putExtra(Constants.KEY_ADDRESS_ID, addressesDM.id)
            startActivity(intent)
        }
    }

    private fun setDefaultAddress(user_id: Int, address_id: Int) {
        GlobalData.progressDialog(activity, R.string.default_address, R.string.please_wait_sending)
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                binding.dataLY.visibility = View.VISIBLE
                if (func == Constants.ERROR) {
                    Toast(R.string.error_in_data)
                } else if (func == Constants.FAIL) {
                    Toast(R.string.fail_to_get_data)
                } else {
                    if (IsSuccess) {
                        GlobalData.hideProgressDialog()
                        GlobalData.successDialog(
                            activity,
                            getString(R.string.default_address),
                            getString(R.string.address_default)
                        )
                        user?.setLastSelectedAddress(defaultAddressId)
                        UtilityApp.setUserData(user)
                    }
                }            }


        }).setDefaultAddress(user_id, address_id)
    }

    private fun deleteAddressId(addressId: Int, position: Int) {
        val click: ConfirmDialog.Click = object : ConfirmDialog.Click() {
            override fun click() {

                DataFeacher(false,
                    object :DataFetcherCallBack {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                            if (func == Constants.ERROR) {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.error_in_data),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (func == Constants.FAIL) {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.fail_delete_address),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (func == Constants.NO_CONNECTION) {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.no_internet_connection),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if (IsSuccess) {
                                    if (user?.lastSelectedAddress == addressId) {
                                        user?.setLastSelectedAddress(0)
                                        UtilityApp.setUserData(user)
                                    }

                                    val size=addressList?.size ?: 0
                                    if (size > 0 && position>=0 && position <size) {
                                        addressList?.removeAt(position)
                                        addressAdapter?.notifyDataSetChanged()
//                                        addressAdapter?.notifyItemRemoved(position)
                                    }
                                    if (addressList?.size == 0) {
                                        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                        binding.failGetDataLY.failTxt.setText(R.string.no_address)
                                        binding.dataLY.visibility = View.VISIBLE
                                        binding.noDataLY.noDataLY.visibility = View.VISIBLE
                                    }
                                } else {
                                    Toast.makeText(
                                        activity,
                                        getString(R.string.fail_to_get_data),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }                        }

                    }).deleteAddressHandle(addressId)
            }
        }
        ConfirmDialog(
            activity,
            getString(R.string.want_to_delete_address),
            R.string.ok,
            R.string.cancel_label,
            click,
            null,
            false
        )
    }

    override fun onDeleteClicked(addressModel: AddressModel, isChecked: Boolean, position: Int) {
        deleteAddressId(addressModel.id, position)
    }



    private fun showDialogs(message: Int) {
        val checkLoginDialog =
            CheckLoginDialog(activity, R.string.LoginFirst, message, R.string.ok, R.string.cancel, null, null)
        checkLoginDialog.show()
        checkLoginDialog.show()
    }

    private val address: Unit
        get() {
            if (UtilityApp.isLogin()) {
                if (user != null && user?.id != null) {
                    getUserAddress(user?.id ?:0)
                }
            } else {
                showDialogs(R.string.to_show_address)
            }
        }

}

