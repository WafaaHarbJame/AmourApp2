package com.amour.shop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Dialogs.ConfirmDialog
import com.amour.shop.Models.AddressModel
import com.amour.shop.Models.MemberModel
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp.getUserData
import com.amour.shop.databinding.RowAddressItemBinding


class AddressAdapter(
    private val context: Context,
    private val isDeleteVisible: Boolean,
    private val addressModelList: MutableList<AddressModel>?,
    private val canSelect: Boolean,
    private val onDeleteClicked: OnDeleteClicked,
    private val onContainerSelect: OnContainerSelect
) :
        RecyclerView.Adapter<AddressAdapter.Holder>() {
    var user: MemberModel? = getUserData()
    private var defaultAddressId: Int
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): Holder {
        val itemView = RowAddressItemBinding.inflate(LayoutInflater.from(context), viewGroup, false)
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val binding = holder.binding
        val addressModel = addressModelList?.get(position)
        holder.binding.rbSelectAddress.isChecked = defaultAddressId == addressModel?.id
        binding.tvAddressMark.text = context.getString(R.string.ph) + " " + addressModel?.mobileNumber
        binding.tvAddressNote.text = addressModel?.fullAddress
        binding.tvaAddressTitle.text = addressModel?.name
        if (isDeleteVisible) {
            holder.binding.deleteAddressBut.visibility = View.VISIBLE
        } else {
            holder.binding.deleteAddressBut.visibility = View.GONE
        }
        binding.deleteAddressBut.setOnClickListener { v ->
            onDeleteClicked.onDeleteClicked(
                addressModel,
                getUserData()!!.lastSelectedAddress == position,
                position
            )
        }
        if (position == itemCount - 1) {
            holder.binding.divider.visibility = View.GONE
        } else {
            holder.binding.divider.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return addressModelList?.size?:0
    }

    fun deleteAddressId(addressId: Int, position: Int) {
        val click: ConfirmDialog.Click = object : ConfirmDialog.Click() {
            override fun click() {
                DataFeacher(false,object :
                    DataFetcherCallBack {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        if (func == Constants.ERROR) {
                            Toast.makeText(
                                context,
                                "" + context.getString(R.string.error_in_data),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (func == Constants.FAIL) {
                            Toast.makeText(
                                context,
                                "" + context.getString(R.string.fail_delete_address),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (func == Constants.NO_CONNECTION) {
                            Toast.makeText(
                                context,
                                "" + context.getString(R.string.no_internet_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            if (IsSuccess) {
                                addressModelList?.removeAt(position)
                                notifyDataSetChanged()
                                notifyItemRemoved(position)
                            } else {
                                Toast.makeText(
                                    context,
                                    "" + context.getString(R.string.fail_to_get_data),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }                    }

                }).deleteAddressHandle(addressId)
            }
        }
        ConfirmDialog(
            context,
            context.getString(R.string.want_to_delete_address),
            R.string.ok,
            R.string.cancel_label,
            click,
            null,
            false
        )
    }

    interface OnContainerSelect {
        fun onContainerSelectSelected(addressesDM: AddressModel?, makeDefault: Boolean)
    }

    interface OnDeleteClicked {
        fun onDeleteClicked(addressModel: AddressModel?, isChecked: Boolean, position: Int)
    }

    inner class Holder(var binding: RowAddressItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            binding.rbSelectAddress.isEnabled = !canSelect
            binding.rbSelectAddress.setOnClickListener { v ->
                val addressModel = addressModelList?.get(bindingAdapterPosition)
                //                user = UtilityApp.getUserData();
                if (addressModel?.id != defaultAddressId) {
                    defaultAddressId = addressModel?.id?:0
                    //                    user.setLastSelectedAddress(addressModel.getId());
//                    UtilityApp.setUserData(user);
                    notifyDataSetChanged()
                    onContainerSelect.onContainerSelectSelected(addressModel, true)
                }
            }
            binding.container.setOnClickListener { view1 ->
                val position = bindingAdapterPosition
                if (position >= 0 && position < (addressModelList?.size ?: 0)) {
                    val addressModel = addressModelList?.get(position)
                    onContainerSelect.onContainerSelectSelected(addressModel, false)
                }
            }
        }
    }

    init {
        defaultAddressId = user!!.lastSelectedAddress
    }
}