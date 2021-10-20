package com.ramez.shopp.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.*
import com.ramez.shopp.Dialogs.CheckLoginDialog
import com.ramez.shopp.Models.CartProcessModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.Models.ProductModel
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.activities.ProductDetailsActivity
import com.ramez.shopp.databinding.RowSuggestedProductItemBinding
import org.greenrobot.eventbus.EventBus
import java.lang.Exception
import java.util.ArrayList


class SuggestedProductAdapter(
    private val context: Context,
    private val productModels: ArrayList<ProductModel>?,
    private val onItemClick: OnItemClick?,
    limit: Int
) :
        RecyclerView.Adapter<SuggestedProductAdapter.Holder>() {
    private var currency = "BHD"
    private var limit = 2
    var fraction = 2
    var localModel: LocalModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = RowSuggestedProductItemBinding.inflate(
            LayoutInflater.from(
                context
            ), parent, false
        )
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val productModel = productModels!![position]
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                context
            )
        currency = localModel?.currencyCode ?:Constants.BHD
        fraction = localModel?.fractional ?:Constants.three
        holder.binding.productNameTv.text = productModel.productName.trim { it <= ' ' }
        if (productModels.size > 0 && productModel.isFavourite) {
            holder.binding.favBut.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.favorite_icon
                )
            )
        } else {
            holder.binding.favBut.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.empty_fav))
        }
        val quantity = productModel.firstProductBarcodes.cartQuantity
        if (quantity > 0) {
            holder.binding.productCartQTY.text = quantity.toString()
            holder.binding.CartLy.visibility = View.VISIBLE
            holder.binding.cartBut.visibility = View.GONE
            if (quantity == 1) {
                holder.binding.deleteCartBtn.visibility = View.VISIBLE
                holder.binding.minusCartBtn.visibility = View.GONE
            } else {
                holder.binding.minusCartBtn.visibility = View.VISIBLE
                holder.binding.deleteCartBtn.visibility = View.GONE
            }
        } else {
            holder.binding.CartLy.visibility = View.GONE
            holder.binding.cartBut.visibility = View.VISIBLE
        }
        if (productModel.firstProductBarcodes.isSpecial) {
            val originalPrice = productModel.firstProductBarcodes.price
            val specialPrice = productModel.firstProductBarcodes.specialPrice
            holder.binding.productPriceBeforeTv.background = ContextCompat.getDrawable(
                context, R.drawable.itlatic_red_line
            )
            holder.binding.productPriceBeforeTv.text = NumberHandler.formatDouble(
                originalPrice,
                fraction
            ) + " " + currency
            holder.binding.productPriceTv.text = NumberHandler.formatDouble(
                specialPrice,
                fraction
            ) + " " + currency
            val discountValue = originalPrice - specialPrice
            val discountPercent = (discountValue.div(originalPrice)) .times(100)


            if (originalPrice > 0) {
                holder.binding.discountTv.text =
                    NumberHandler.arabicToDecimal(discountPercent.toInt().toString()).plus( " % " + "OFF")
            } else {
                holder.binding.discountTv.text =
                    NumberHandler.arabicToDecimal("0").plus( " % " + "OFF")
            }
        } else {
            holder.binding.productPriceTv.text =
                NumberHandler.formatDouble(
                    productModel.firstProductBarcodes.price,
                    fraction
                ) + " " + currency + ""
            holder.binding.productPriceBeforeTv.visibility = View.GONE
            holder.binding.discountTv.visibility = View.GONE
        }
        var photoUrl: String? = ""
        photoUrl =
            if (productModel.images != null && productModel.images[0] != null && !productModel.images[0].isEmpty()) {
                productModel.images[0]
            } else {
                "http"
            }
        try {
            GlobalData.GlideImg(
                context, photoUrl, R.drawable.holder_image, holder.binding.productImg
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return if (limit != 0) Math.min(productModels!!.size, limit) else productModels!!.size
    }

    private fun addToFavorite(v: View, position: Int, productId: Int, userId: Int, storeId: Int) {
        AnalyticsHandler.AddToWishList(productId, currency, productId.toDouble())
        DataFeacher(false,
            object :DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (func == Constants.ERROR) {
                        GlobalData.errorDialogWithButton(
                            context, context.getString(R.string.error),
                            context.getString(R.string.fail_to_add_favorite)
                        )
                    } else if (func == Constants.FAIL) {
                        GlobalData.errorDialogWithButton(
                            context, context.getString(R.string.error),
                            context.getString(R.string.fail_to_add_favorite)
                        )
                    } else {
                        if (IsSuccess) {
                            Toast.makeText(context, context.getString(R.string.success_add), Toast.LENGTH_SHORT)
                                .show()
                            productModels!![position].isFavourite = true
                            notifyItemChanged(position)
                            notifyDataSetChanged()
                        } else {
                            GlobalData.errorDialogWithButton(
                                context, context.getString(R.string.error),
                                context.getString(R.string.fail_to_add_favorite)
                            )
                        }
                    }                }

            }).addToFavoriteHandle(userId, storeId, productId)
    }

    private fun removeFromFavorite(view: View, position: Int, productId: Int, userId: Int, storeId: Int) {
        DataFeacher(false,object :
            DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (func == Constants.ERROR) {
                    GlobalData.errorDialogWithButton(
                        context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_remove_favorite)
                    )
                } else if (func == Constants.FAIL) {
                    GlobalData.errorDialogWithButton(
                        context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_remove_favorite)
                    )
                } else {
                    if (IsSuccess) {
                        productModels!![position].isFavourite = false
                        initSnackBar(context.getString(R.string.success_delete), view)
                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } else {
                        GlobalData.errorDialogWithButton(
                            context, context.getString(R.string.error),
                            context.getString(R.string.fail_to_remove_favorite)
                        )
                    }
                }            }

        }).deleteFromFavoriteHandle(userId, storeId, productId)
    }

    private fun initSnackBar(message: String, viewBar: View) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    interface OnItemClick {
        fun onItemClicked(position: Int, productModel: ProductModel?)
    }

    inner class Holder internal constructor(var binding: RowSuggestedProductItemBinding) :
            RecyclerView.ViewHolder(
                binding.root
            ),
            View.OnClickListener {
        override fun onClick(v: View) {
            if (onItemClick != null) {
                if (productModels != null && productModels.size > 0) {
                    val position = bindingAdapterPosition
                    onItemClick.onItemClicked(position, productModels[position])
                    val productModel = productModels[position]
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.DB_productModel, productModel)
                    context.startActivity(intent)
                }
            }
        }

        private fun addToCart(
            v: View,
            position: Int,
            productId: Int,
            product_barcode_id: Int,
            quantity: Int,
            userId: Int,
            storeId: Int
        ) {
            if (quantity > 0) {
                DataFeacher(false, object :DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        val result = obj as CartProcessModel
                        if (IsSuccess) {
                            val cartId = result.id
                            productModels?.get(position)?.firstProductBarcodes?.cartQuantity  = quantity
                            productModels?.get(position)?.firstProductBarcodes?.cartId = cartId
                            notifyItemChanged(position)
                            println("Log suggest addToCart" + result.total)
                            if (productModels?.size ?: 0 > 0) {
                                UtilityApp.updateCart(1, productModels?.size?:0)
                            }
                            AnalyticsHandler.AddToCart(result.id, currency, quantity.toDouble())
                            EventBus.getDefault()
                                .post(MessageEvent(MessageEvent.TYPE_READ_CART))
                        } else {
                            GlobalData.errorDialogWithButton(
                                context, context.getString(R.string.error),
                                context.getString(R.string.fail_to_add_cart)
                            )
                        }                    }

                }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId)
            } else {
                Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT).show()
            }
        }

        private fun updateCart(
            v: View,
            position: Int,
            productId: Int,
            product_barcode_id: Int,
            quantity: Int,
            userId: Int,
            storeId: Int,
            cart_id: Int,
            update_quantity: String
        ) {
            if (quantity > 0) {
                DataFeacher(false,
                    object :DataFetcherCallBack {
                        override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                            if (IsSuccess) {

                                productModels?.get(position)?.firstProductBarcodes?.cartQuantity  = quantity
                                notifyItemChanged(position)
                            } else {
                                GlobalData.errorDialogWithButton(
                                    context, context.getString(R.string.error),
                                    context.getString(R.string.fail_to_update_cart)
                                )
                            }                        }

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

        private fun deleteCart(
            v: View,
            position: Int,
            productId: Int,
            product_barcode_id: Int,
            cart_id: Int,
            userId: Int,
            storeId: Int
        ) {
            DataFeacher(false,
                object :DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        if (IsSuccess) {
                            productModels!![position].firstProductBarcodes.cartQuantity = 0
                            notifyItemChanged(position)
                            initSnackBar(context.getString(R.string.success_delete_from_cart), v)
                            UtilityApp.updateCart(2, productModels.size)
                            EventBus.getDefault()
                                .post(MessageEvent(MessageEvent.TYPE_READ_CART))
                            AnalyticsHandler.RemoveFromCart(cart_id, currency, 0.0)
                        } else {
                            GlobalData.errorDialogWithButton(
                                context, context.getString(R.string.delete_product),
                                context.getString(R.string.fail_to_delete_cart)
                            )
                        }                    }

                }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId)
        }

        init {
            itemView.setOnClickListener(this)
            binding.favBut.setOnClickListener { view1 ->
                if (!UtilityApp.isLogin()) {
                    val checkLoginDialog = CheckLoginDialog(
                        context,
                        R.string.LoginFirst,
                        R.string.to_add_favorite,
                        R.string.ok,
                        R.string.cancel,
                        null,
                        null
                    )
                    checkLoginDialog.show()
                } else {
                    val position = bindingAdapterPosition
                    val userId = UtilityApp.getUserData().id
                    val storeId = localModel!!.cityId.toInt()
                    val productId = productModels!![position].id
                    val isFavorite = productModels[position].isFavourite
                    if (isFavorite) {
                        removeFromFavorite(view1, position, productId, userId, storeId)
                    } else {
                        addToFavorite(view1, position, productId, userId, storeId)
                    }
                }
            }
            binding.cartBut.setOnClickListener { view1 ->
                if (!UtilityApp.isLogin()) {
                    val checkLoginDialog = CheckLoginDialog(
                        context,
                        R.string.LoginFirst,
                        R.string.to_add_cart,
                        R.string.ok,
                        R.string.cancel,
                        null,
                        null
                    )
                    checkLoginDialog.show()
                } else {
                    val message: String
                    val position = bindingAdapterPosition
                    val productModel = productModels!![position]
                    val productBarcode = productModel.firstProductBarcodes
                    val count = productBarcode.cartQuantity
                    val userId = UtilityApp.getUserData().id
                    val storeId = localModel!!.cityId.toInt()
                    val productId = productModel.id
                    val productBarcodeId = productBarcode.id
                    val limit = productBarcode.limitQty
                    val stock = productBarcode.stockQty
                    Log.i("limit", "Log limit  $limit")
                    Log.i("stock", "Log stock  $stock")
                    if (limit == 0) {
                        if (count + 1 <= stock) {
                            addToCart(
                                view1,
                                position,
                                productId,
                                productBarcodeId,
                                count + 1,
                                userId,
                                storeId
                            )
                        } else {
                            message = context.getString(R.string.stock_empty)
                            GlobalData.errorDialogWithButton(
                                context, context.getString(R.string.error),
                                message
                            )
                        }
                    } else {
                        if (count + 1 <= stock && count + 1 <= limit) {
                            addToCart(
                                view1,
                                position,
                                productId,
                                productBarcodeId,
                                count + 1,
                                userId,
                                storeId
                            )
                        } else {
                            message = if (count + 1 > stock) {
                                context.getString(R.string.limit) + "" + limit
                            } else {
                                context.getString(R.string.stock_empty)
                            }
                            GlobalData.errorDialogWithButton(
                                context, context.getString(R.string.error),
                                message
                            )
                        }
                    }
                }
            }
            binding.plusCartBtn.setOnClickListener { v ->
                var message = ""
                val position = bindingAdapterPosition
                val productModel = productModels!![position]
                val productBarcode = productModel.firstProductBarcodes
                val count = productBarcode.cartQuantity
                val userId = UtilityApp.getUserData().id
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel.id
                val productBarcodeId = productBarcode.id
                val cartId = productBarcode.cartId
                val stock = productBarcode.stockQty
                val limit = productBarcode.limitQty
                Log.i("limit", "Log limit  $limit")
                Log.i("stock", "Log stock  $stock")
                if (limit == 0) {
                    if (count + 1 <= stock) {
                        updateCart(
                            v,
                            position,
                            productId,
                            productBarcodeId,
                            count + 1,
                            userId,
                            storeId,
                            cartId,
                            "quantity"
                        )
                    } else {
                        message = context.getString(R.string.stock_empty)
                        GlobalData.errorDialogWithButton(
                            context, context.getString(R.string.error),
                            message
                        )
                    }
                } else {
                    if (count + 1 <= stock && count + 1 <= limit) {
                        updateCart(
                            v,
                            position,
                            productId,
                            productBarcodeId,
                            count + 1,
                            userId,
                            storeId,
                            cartId,
                            "quantity"
                        )
                    } else {
                        message = if (count + 1 > stock) {
                            context.getString(R.string.limit) + "" + limit
                        } else {
                            context.getString(R.string.stock_empty)
                        }
                        GlobalData.errorDialogWithButton(
                            context, context.getString(R.string.error),
                            message
                        )
                    }
                }
            }
            binding.minusCartBtn.setOnClickListener { v ->
                val position = bindingAdapterPosition
                val productModel = productModels!![position]
                val productBarcode = productModel.firstProductBarcodes
                val count = productBarcode.cartQuantity
                val userId = UtilityApp.getUserData().id
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel.id
                val productBarcodeId = productBarcode.id
                val cartId = productBarcode.cartId
                updateCart(
                    v,
                    position,
                    productId,
                    productBarcodeId,
                    count - 1,
                    userId,
                    storeId,
                    cartId,
                    "quantity"
                )
            }
            binding.deleteCartBtn.setOnClickListener { v ->
                val position = bindingAdapterPosition
                val productModel = productModels!![position]
                val productBarcode = productModel.firstProductBarcodes
                onItemClick!!.onItemClicked(position, productModels[position])
                val userId = UtilityApp.getUserData().id
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel.id
                val productBarcodeId = productBarcode.id
                val cartId = productBarcode.cartId
                deleteCart(v, position, productId, productBarcodeId, cartId, userId, storeId)
            }
        }
    }

    init {
        this.limit = limit
    }
}
