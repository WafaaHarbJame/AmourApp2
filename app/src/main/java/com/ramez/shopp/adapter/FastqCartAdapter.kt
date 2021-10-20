package com.ramez.shopp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.CallBack.DataCallback
import com.ramez.shopp.Classes.CartModel
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.GlobalData
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.databinding.RowFastqCartItemBinding
import retrofit2.http.DELETE


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

        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
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
                                    Constants.refresh_cart = true
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

                        } else {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT)
                                .show()

                        }

                    }


                }

            }).updateCartFastQ(cartId, quantity, cityId, userId)


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
