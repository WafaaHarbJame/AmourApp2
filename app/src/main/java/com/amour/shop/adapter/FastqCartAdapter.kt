package com.amour.shop.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.CallBack.DataCallback
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.*
import com.amour.shop.R
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.databinding.RowFastqCartItemBinding
import com.amour.shop.BuildConfig


class FastqCartAdapter(
    private val activity: Activity,
    private val isAllowClick: Boolean,
    private val list: MutableList<CartFastQModel>?,
    private val dataCallback: DataCallback
) :
        RecyclerView.Adapter<FastqCartAdapter.ItemViewHolder>() {
    var currency = "BHD"
    var fraction = 2
    var localModel: LocalModel? = null
    var storeId = 0
    var userId = 0
    var user: MemberModel? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = RowFastqCartItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ItemViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val cartModel = list!![position]

        localModel = UtilityApp.getLocalData()
        currency = localModel?.currencyCode ?: Constants.BHD
        fraction = localModel?.fractional ?: Constants.three
        storeId =
            localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()

        user = UtilityApp.getUserData()

        userId = user?.id ?: 0
        holder.binding.tvName.text = cartModel.name()
        holder.binding.productCartQTY.text = cartModel.qty.toString()
        holder.binding.priceTv.text = NumberHandler.formatDouble(
            cartModel.price, fraction
        ) + " " + currency

        if (!isAllowClick) {
            holder.binding.deleteBut.isEnabled = false
            holder.binding.plusCartBtn.isEnabled = false
            holder.binding.minusCartBtn.isEnabled = false
            holder.binding.swipe.isEnabled = false

        }


    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class ItemViewHolder internal constructor(val binding: RowFastqCartItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {

            }

            binding.plusCartBtn.setOnClickListener {
                val position = bindingAdapterPosition
                val productModel: CartFastQModel = list!![position]
                val count = productModel.qty
                val userId = UtilityApp.getUserData()?.id ?: 0
                val cartId = productModel.id
                updateCart(position, cartId, count + 1, storeId, userId.toString(), false)

            }

            binding.minusCartBtn.setOnClickListener {
                val position = bindingAdapterPosition
                val productModel: CartFastQModel = list!![position]
                val count = productModel.qty
                val userId = UtilityApp.getUserData()?.id ?: 0
                val cartId = productModel.id

                if (count == 1) {
//                    GlobalData.Toast(activity, R.string.min_quantity_is_one)
                    binding.deleteBut.performClick()
                    return@setOnClickListener
                }

                updateCart(position, cartId, count - 1, storeId, userId.toString(), false)

            }


            binding.deleteBut.setOnClickListener {
                val position = bindingAdapterPosition
                val productModel: CartFastQModel = list!![position]
                val count = 0
                val userId = UtilityApp.getUserData()?.id ?: 0
                val cartId = productModel.id

                updateCart(position, cartId, count, storeId, userId.toString(), true)

            }

//            binding.swipe.showMode = SwipeLayout.ShowMode.LayDown


        }


    }


    private fun updateCart(
        position: Int,
        cartId: Int,
        quantity: Int,
        cityId: Int,
        userId: String?,
        isDelete: Boolean,
    ) {

        DataFeacher(false,
            object : DataFetcherCallBack {

                @SuppressLint("SetTextI18n")
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    val result = obj as ResultAPIModel<ScanModel>?

                    var message: String? = activity.getString(R.string.fail_to_update_cart)
                    if (func == Constants.ERROR) {
                        if (result?.message != null) {
                            message = result.message
                        }
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                            .show()


                    } else {
                        if (IsSuccess) {
                            if (result?.data != null) {

                                if (isDelete) {
                                    list?.removeAt(position)
                                    notifyItemRemoved(position)
                                    GlobalData.refresh_cart = true
                                } else {
                                    list?.get(position)?.qty = quantity
                                    notifyItemChanged(position)

                                }

                                val size = list?.size
                                val cartProcessModel = CartProcessModel()
                                cartProcessModel.total = calculateSubTotalPrice(list)
                                cartProcessModel.setCartCount(size)
                                if (size != null) {
                                    UtilityApp.setFastQCartCount(size)
                                    UtilityApp.setFastQCartTotal(cartProcessModel.total.toFloat())
                                }



                                dataCallback.dataResult(
                                    cartProcessModel,
                                    "success",
                                    true
                                )


                            }
                            else{
                                message = result?.message ?: activity.getString(R.string.fail_to_get_data)

                                Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                                    .show()
                            }

                        } else {

                                message = result?.message ?: activity.getString(R.string.fail_to_get_data)

                            Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                                .show()

                        }

                    }


                }

            }).updateCartFastQ(cartId, quantity, cityId, userId?:"")


    }

    fun calculateSubTotalPrice(cartList: MutableList<CartFastQModel>?): Double {
        var subTotal = 0.0
        for (i in cartList!!.indices) {
            val cartFastQModel = cartList[i]
            if (cartFastQModel.price > 0) {
                val price = cartFastQModel.price
                subTotal += price * cartFastQModel.qty
            }

        }
        UtilityApp.setFastQCartTotal(subTotal.toFloat())
        Log.i(javaClass.name, "Log subTotal result$subTotal")
        return subTotal
    }
}
