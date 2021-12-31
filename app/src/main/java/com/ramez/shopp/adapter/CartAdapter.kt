package com.ramez.shopp.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.SwipeLayout.SwipeListener
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.CallBack.DataCallback
import com.ramez.shopp.Classes.*
import com.ramez.shopp.Dialogs.AddCommentDialog
import com.ramez.shopp.Models.CartProcessModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.Models.MemberModel
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.databinding.RowCartItemBinding
import java.lang.Exception
import java.util.ArrayList


class CartAdapter(
    private val context: Context,
    cartDMS: List<CartModel>?,
    onCartItemClicked: OnCartItemClicked,
    dataCallback: DataCallback?
) :
        RecyclerSwipeAdapter<CartAdapter.Holder>() {
    var count = 0
    var currency = "BHD"
    var fraction = 2
    var localModel: LocalModel? = null
    var dataCallback: DataCallback?
    var addCommentDialog: AddCommentDialog? = null
    private val cartDMS:MutableList<CartModel>?

    private val onCartItemClicked: OnCartItemClicked
    var memberModel: MemberModel?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = RowCartItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val cartDM = cartDMS!![position]
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                context
            )
        currency = localModel?.currencyCode ?:Constants.BHD
        fraction = localModel?.fractional ?:Constants.three
        memberModel = UtilityApp.getUserData()
        val quantity = cartDM.quantity
        holder.binding.weightUnitTv.text = cartDM.wightName
        if (quantity > 0) {
            holder.binding.productCartQTY.text = quantity.toString()
            if (quantity == 1) {
                holder.binding.deleteCartBtn.visibility = View.VISIBLE
                holder.binding.minusCartBtn.visibility = View.GONE
            } else {
                holder.binding.minusCartBtn.visibility = View.VISIBLE
                holder.binding.deleteCartBtn.visibility = View.GONE
            }
        }
        if (cartDM.isExtra) {
            holder.binding.priceTv.visibility = View.GONE
            holder.binding.currencyPriceTv.visibility = View.GONE
            holder.binding.weightUnitTv.visibility = View.GONE
            val productName = if (cartDM.productName == null) cartDM.gethProductName() else cartDM.productName
            holder.binding.tvName.text = productName
        } else {
            holder.binding.tvName.text = cartDM.name
        }
        holder.binding.currencyPriceTv.text = currency
        if (cartDM.specialPrice > 0) {
            holder.binding.productPriceBeforeTv.background = ContextCompat.getDrawable(
                context, R.drawable.itlatic_red_line
            )
            holder.binding.productPriceBeforeTv.text =
                NumberHandler.formatDouble(
                    cartDM.productPrice.toString().toDouble(),
                    fraction
                ) + " " + currency
            holder.binding.priceTv.text =
                NumberHandler.formatDouble(cartDM.specialPrice.toString().toDouble(), fraction)
        } else {
            holder.binding.priceTv.text = NumberHandler.formatDouble(
                cartDM.productPrice.toString().toDouble(),
                fraction
            )
            holder.binding.productPriceBeforeTv.visibility = View.GONE
        }
        try {
            GlobalData.GlideImg(
                context, cartDM.image, R.drawable.holder_image, holder.binding.imageView1
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        calculateSubTotalPrice()
        calculateSavePrice()
        if (holder.binding.productCartQTY.text.toString().toInt() == 1) {
            holder.binding.deleteCartBtn.visibility = View.VISIBLE
            holder.binding.minusCartBtn.visibility = View.GONE
        } else {
            holder.binding.minusCartBtn.visibility = View.VISIBLE
            holder.binding.deleteCartBtn.visibility = View.GONE
        }
        if (cartDM.remark != null && !cartDM.remark.isEmpty()) {
            holder.binding.markTv.visibility = View.VISIBLE
            holder.binding.markTv.text = cartDM.remark
        } else holder.binding.markTv.visibility = View.GONE
        if (cartDM.quantity > cartDM.productQuantity && !cartDM.isExtra) {
            holder.binding.cardBack.background = ContextCompat.getDrawable(context, R.drawable.round_card_red)
        } else if (cartDM.productQuantity == 0 && !cartDM.isExtra) {
            holder.binding.cardBack.background = ContextCompat.getDrawable(context, R.drawable.round_card_red)
        } else {
            holder.binding.cardBack.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    fun calculateSubTotalPrice(): Double {
        var subTotal = 0.0
        for (i in cartDMS!!.indices) {
            var price = 0.0
            if (cartDMS[i].productPrice > 0) {
                price = if (cartDMS[i].specialPrice > 0) {
                    cartDMS[i].specialPrice
                } else {
                    cartDMS[i].productPrice
                }
                subTotal += price * cartDMS[i].quantity
            }
        }
        Log.i(TAG, "Log subTotal result$subTotal")
        return subTotal
    }

    fun calculateSavePrice(): Double {
        var savePrice = 0.0
        for (i in cartDMS!!.indices) {
            val cartModel = cartDMS[i]
            var price = 0.0
            var specialPrice = 0.0
            var difference = 0.0
            if (cartModel.productPrice > 0) {
                if (cartModel.specialPrice > 0) {
                    specialPrice = cartModel.specialPrice
                    price = cartModel.productPrice
                    difference = price - specialPrice
                    savePrice = savePrice + difference * cartModel.quantity
                }
            }
        }
        Log.i(TAG, "Log savePrice result$savePrice")
        return savePrice
    }

    override fun getItemCount(): Int {
        return cartDMS!!.size
    }

    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    private fun initSnackBar(message: String, viewBar: View) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateMark(v: View, position: Int, cart_id: Int, remark: String) {
        DataFeacher(false,object :
            DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    addCommentDialog!!.dismiss()
                    notifyItemChanged(position)
                    initSnackBar(context.getString(R.string.success_to_update_cart), v)
                    cartDMS!![position].remark = remark
                } else {
                    addCommentDialog!!.dismiss()
                    GlobalData.errorDialogWithButton(
                        context,
                        context.getString(R.string.error),
                        context.getString(R.string.fail_to_update_cart)
                    )
                }            }

        }).updateRemarkCartHandle(cart_id, remark)
    }

    inner  class Holder @SuppressLint("SetTextI18n") constructor(var binding: RowCartItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cardView.setOnClickListener { v ->
                val position = bindingAdapterPosition
                if (position >= 0) {
                    val cartDM = cartDMS!![position]
                    onCartItemClicked.onCartItemClicked(cartDM)
                }
            }
            binding.markBtn.setOnClickListener { view1 ->
                val position = bindingAdapterPosition
                val cartModel = cartDMS!![position]
                val okBut: AddCommentDialog.Click = object : AddCommentDialog.Click() {
                    override fun click() {
                        val remark =
                            (addCommentDialog!!.findViewById<View>(R.id.remarkET) as EditText).text
                                .toString()
                        updateMark(view1, position, cartModel.id, remark)
                    }
                }
                val cancelBut: AddCommentDialog.Click = object : AddCommentDialog.Click() {
                    override fun click() {
                        addCommentDialog!!.dismiss()
                    }
                }
                addCommentDialog = AddCommentDialog(
                    context,
                    cartModel.remark,
                    R.string.add_comment,
                    R.string.add_comment,
                    okBut,
                    cancelBut
                )
                addCommentDialog!!.show()
            }
            binding.plusCartBtn.setOnClickListener { v ->

                val message: String
                val position = bindingAdapterPosition
                val productModel = cartDMS!![position]
                val count = productModel.quantity
                val stock = productModel.productQuantity
                val product_barcode_id = productModel.productBarcodeId
                val userId =
                    if (memberModel != null && memberModel!!.id != null) memberModel!!.id else 0
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel.productId
                val cart_id = productModel.id
                val limit = productModel.limitQty
                val isExtra = productModel.isExtra
                Log.i("limit", "Log limit$limit")
                Log.i("stock", "Log stock$stock")
                if (!isExtra) {
                    if (limit == 0) {
                        if (count + 1 <= stock) {
                            updateCart(
                                position,
                                productId,
                                product_barcode_id,
                                count + 1,
                                count + 1,
                                false,
                                userId,
                                storeId,
                                cart_id,
                                "quantity"
                            )
                        } else {
                            message = context.getString(R.string.stock_empty)
                            GlobalData.errorDialogWithButton(
                                context,
                                context.getString(R.string.error),
                                message
                            )
                        }
                    } else {
                        if (count + 1 <= stock && count + 1 <= limit) {
                            updateCart(
                                position,
                                productId,
                                product_barcode_id,
                                count + 1,
                                count + 1,
                                false,
                                userId,
                                storeId,
                                cart_id,
                                "quantity"
                            )
                        } else {
                            message = if (count + 1 > stock) {
                                context.getString(R.string.limit) + "" + limit
                            } else if (stock == 0) {
                                context.getString(R.string.stock_empty)
                            } else {
                                context.getString(R.string.limit) + "" + limit
                            }
                            GlobalData.errorDialogWithButton(
                                context,
                                context.getString(R.string.error),
                                message
                            )
                        }
                    }
                } else {
                    updateCart(
                        position,
                        productId,
                        product_barcode_id,
                        count + 1,
                        count + 1,
                        false,
                        userId,
                        storeId,
                        cart_id,
                        "quantity"
                    )
                }
            }
            binding.minusCartBtn.setOnClickListener { v ->
                val position = bindingAdapterPosition
                val productModel = cartDMS!![position]
                val count = productModel.quantity
                val product_barcode_id = productModel.productBarcodeId
                val userId =
                    if (memberModel != null && memberModel!!.id != null) memberModel!!.id else 0
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel.productId
                val cart_id = productModel.id
                updateCart(
                    position,
                    productId,
                    product_barcode_id,
                    count - 1,
                    count - 1,
                    false,
                    userId,
                    storeId,
                    cart_id,
                    "quantity"
                )
            }
            binding.deleteCartBtn.setOnClickListener { v ->
                val position = bindingAdapterPosition
                if (cartDMS != null && cartDMS.size > 0 && position != -1) {
                    val productModel = cartDMS[position]
                    val productBarcodeId = productModel.productBarcodeId
                    val userId =
                        if (memberModel != null && memberModel!!.id != null) memberModel!!.id else 0
                    val storeId = localModel!!.cityId.toInt()
                    val productId = productModel.productId
                    val cart_id = productModel.id
                    deleteCart(position, productId, productBarcodeId, cart_id, userId, storeId)
                }
            }
            binding.deleteBut.setOnClickListener {
                val position = bindingAdapterPosition
                val productModel = cartDMS!![position]
                val productBarcodeId = productModel.productBarcodeId
                val userId =
                    if (memberModel != null && memberModel!!.id != null) memberModel!!.id else 0
                val storeId = localModel?.cityId?.toInt() ?:Constants.default_storeId.toInt()
                val productId = productModel.productId
                val cartId = productModel.id
                deleteCart(position, productId, productBarcodeId, cartId, userId, storeId)
            }
            binding.swipe.showMode = SwipeLayout.ShowMode.LayDown
            binding.swipe.addSwipeListener(object : SwipeListener {
                override fun onStartOpen(layout: SwipeLayout) {}
                override fun onOpen(layout: SwipeLayout) {}
                override fun onStartClose(layout: SwipeLayout) {}
                override fun onClose(layout: SwipeLayout) {}
                override fun onUpdate(layout: SwipeLayout, leftOffset: Int, topOffset: Int) {}
                override fun onHandRelease(layout: SwipeLayout, xvel: Float, yvel: Float) {}
            })
        }
    }

    fun updateCart(
        position: Int,
        productId: Int,
        product_barcode_id: Int,
        quantity: Int,
        quantity2: Int,
        fromCart: Boolean,
        userId: Int,
        storeId: Int,
        cart_id: Int,
        update_quantity: String?
    ) {
        if (quantity > 0) {
            DataFeacher(false,
                object:DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        if (IsSuccess) {
                            var cartQuantity = 0
                            calculateSubTotalPrice()
                            calculateSavePrice()
                            itemCount
                            cartQuantity = if (fromCart) {
                                quantity2
                            } else {
                                quantity
                            }
                            if (cartDMS!!.isNotEmpty() && position >= 0 && position < cartDMS.size) {
                                cartDMS[position].quantity = cartQuantity
                                notifyItemChanged(position)
                            }
                            val cartProcessModel = obj as CartProcessModel
                            cartProcessModel.total = calculateSubTotalPrice()
                            cartProcessModel.setCartCount(cartDMS.size)
                            cartProcessModel.totalSavePrice = calculateSavePrice()
                            if (dataCallback != null) {
                                if (calculateSubTotalPrice() > 0 || calculateSavePrice() > 0) dataCallback!!.dataResult(
                                    cartProcessModel,
                                    "success",
                                    true
                                )
                            }
                        } else {
                            GlobalData.errorDialogWithButton(
                                context,
                                context.getString(R.string.error),
                                context.getString(R.string.fail_to_update_cart)
                            )
                        }                    }

                }).updateCartHandle(
                productId,
                product_barcode_id,
                quantity,
                userId,
                storeId,
                cart_id,
                update_quantity
            )
        } else {
            Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteCart(
        position: Int, productId: Int, product_barcode_id: Int, cart_id: Int,
        userId: Int, storeId: Int
    ) {
        DataFeacher(false,
            object :DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        if (cartDMS!!.isNotEmpty() && position < cartDMS.size) {
                            cartDMS.removeAt(position)
                            notifyItemRemoved(position)
                        }
                        Toast.makeText(
                            context,
                            "" + context.getString(R.string.success_delete_from_cart),
                            Toast.LENGTH_SHORT
                        ).show()
                        val cartProcessModel = obj as CartProcessModel
                        cartProcessModel.total = calculateSubTotalPrice()
                        cartProcessModel.setCartCount(cartDMS.size)
                        cartProcessModel.totalSavePrice = calculateSavePrice()
                        AnalyticsHandler.RemoveFromCart(cart_id, currency, 0.0)
                        if (dataCallback != null) {
                            dataCallback!!.dataResult(
                                cartProcessModel,
                                Constants.success,
                                true
                            )
                        }
                        UtilityApp.updateCart(2, cartDMS.size)
                    } else {
                        GlobalData.errorDialogWithButton(
                            context,
                            context.getString(R.string.error),
                            context.getString(R.string.fail_to_delete_cart)
                        )
                    }                }

            }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId)
    }

    interface OnCartItemClicked {
        fun onCartItemClicked(cartDM: CartModel?)
    }

    companion object {
        private const val TAG = "Log CartAdapter"
    }

    init {
        this.cartDMS = ArrayList(cartDMS)
        this.onCartItemClicked = onCartItemClicked
        this.dataCallback = dataCallback
        memberModel = UtilityApp.getUserData()
    }
}
