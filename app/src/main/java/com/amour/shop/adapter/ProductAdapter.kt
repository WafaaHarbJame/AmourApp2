package com.amour.shop.adapter

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
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.BuildConfig
import com.amour.shop.Dialogs.CheckLoginDialog
import com.amour.shop.Models.CartProcessModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.Models.ProductModel
import com.amour.shop.R
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.activities.ProductDetailsActivity
import com.amour.shop.classes.AnalyticsHandler
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData.GlideImg
import com.amour.shop.classes.GlobalData.errorDialogWithButton
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.RowProductsItemBinding
import es.dmoral.toasty.Toasty
import kotlin.math.roundToInt


class ProductAdapter(
    private val context: Context,
    private val productModels: MutableList<ProductModel?>?,
    private val onItemClick: OnItemClick?,
    limit: Int
) :
        RecyclerView.Adapter<ProductAdapter.Holder>() {
    private var currency = "BHD"
    private var limit = 2
    var fraction = 2
    var localModel: LocalModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = RowProductsItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val productModel = productModels?.get(position)
        localModel = UtilityApp.getLocalData()
        currency = localModel?.currencyCode ?: Constants.BHD
        fraction = localModel?.fractional ?: Constants.three
        holder.binding.productNameTv.text =
            (productModel?.productName?.trim { it <= ' ' } ?: if (productModel?.isFavourite == true) {
                holder.binding.favBut.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.favorite_icon
                    )
                )
            } else {
                holder.binding.favBut.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.empty_fav
                    )
                )
            }).toString()
        val quantity = productModel?.firstProductBarcodes?.cartQuantity ?: 0
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
        if (productModel?.firstProductBarcodes?.isSpecial == true) {
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
            val discountPercent = discountValue / originalPrice * 100
            if (originalPrice > 0) {
                holder.binding.discountTv.text =
                    NumberHandler.arabicToDecimal("${discountPercent.roundToInt()} % OFF")
            } else {
                holder.binding.discountTv.text =
                    NumberHandler.arabicToDecimal(0.toString() + " % " + "OFF")
            }
        } else {
            holder.binding.productPriceTv.text =
                NumberHandler.formatDouble(
                    productModel?.firstProductBarcodes?.price.toString().toDouble(),
                    fraction
                ) + " " + currency + ""
            holder.binding.productPriceBeforeTv.visibility = View.GONE
            holder.binding.discountTv.visibility = View.GONE
        }
        var photoUrl: String? = ""
        if (productModels?.isNotEmpty() == true) {

            if (productModel != null) {
                photoUrl =
                    if (productModel.images != null && !productModel.images[0].isNullOrEmpty()) {
                        productModel.images[0]
                    } else {
                        "http"
                    }

                try {
                    GlideImg(
                        context, photoUrl, R.drawable.holder_image, holder.binding.productImg
                    )

//            holder.binding.productImg.load(photoUrl) {
//                placeholder(R.drawable.holder_image)
//                error(R.drawable.holder_image)
//            }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }


//        holder.binding.shimmerViewContainer.stopShimmer();
    }

    override fun getItemCount(): Int {
        return if (limit == 10) (productModels?.size ?: 0).coerceAtMost(limit) else productModels?.size ?: 0
    }

    private fun addToFavorite(v: View, position: Int, productId: Int, userId: Int, storeId: Int) {
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (func == Constants.ERROR) {
                    errorDialogWithButton(
                        context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_add_favorite)
                    )
                } else if (func == Constants.FAIL) {
                    errorDialogWithButton(
                        context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_add_favorite)
                    )
                } else {
                    if (IsSuccess) {
                        AnalyticsHandler.AddToWishList(productId, currency, productId.toDouble())
                        Toasty.success(
                            context,
                            context.getString(R.string.success_add),
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        productModels?.get(position)?.isFavourite = true
                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } else {
                        errorDialogWithButton(
                            context, context.getString(R.string.error),
                            context.getString(R.string.fail_to_add_favorite)
                        )
                    }
                }
            }

        }).addToFavoriteHandle(userId, storeId, productId)
    }

    private fun removeFromFavorite(view: View, position: Int, productId: Int, userId: Int, storeId: Int) {
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                if (func == Constants.ERROR) {
                    errorDialogWithButton(
                        context, context.getString(R.string.fail_to_remove_favorite),
                        context.getString(R.string.fail_to_delete_cart)
                    )
                } else if (func == Constants.FAIL) {
                    errorDialogWithButton(
                        context, context.getString(R.string.fail_to_remove_favorite),
                        context.getString(R.string.fail_to_delete_cart)
                    )
                } else {
                    if (IsSuccess) {
                        productModels?.get(position)?.isFavourite = false
                        Toast.makeText(
                            context,
                            context.getString(R.string.success_delete),
                            Toast.LENGTH_SHORT
                        ).show()
                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } else {
                        errorDialogWithButton(
                            context, context.getString(R.string.error),
                            context.getString(R.string.fail_to_remove_favorite)
                        )
                    }
                }
            }

        }).deleteFromFavoriteHandle(userId, storeId, productId)
    }

    private fun initSnackBar(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    interface OnItemClick {
        fun onItemClicked(position: Int, productModel: ProductModel?)
    }

    inner class Holder internal constructor(var binding: RowProductsItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ),
            View.OnClickListener {
        override fun onClick(v: View) {
            if (onItemClick != null) {
                if ((productModels?.size ?: 0) > 0) {
                    val position = bindingAdapterPosition
                    val productModel = productModels?.get(position)
                    onItemClick.onItemClicked(position, productModels?.get(position))
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
                DataFeacher(false, object : DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        val result = obj as CartProcessModel?
                        if (IsSuccess && result != null) {
                            val cartId = result.id
                            Log.i("tag", "Log " + UtilityApp.getCartCount())
                            Log.i("tag", "Log Status " + result.status)
                            UtilityApp.updateCart(1, productModels?.size ?: 0)
                            binding.cartBut.visibility = View.GONE
                            productModels?.get(position)?.firstProductBarcodes?.cartId = cartId
                            productModels?.get(position)?.firstProductBarcodes?.cartQuantity = quantity
                            AnalyticsHandler.AddToCart(cartId, currency, quantity.toDouble())
                            notifyItemChanged(position)
                        } else {
                            errorDialogWithButton(
                                context, context.getString(R.string.fail_to_add_cart),
                                context.getString(R.string.fail_to_update_cart)
                            )
                        }
                    }

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
                DataFeacher(false, object :
                        DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        if (IsSuccess) {
                            productModels?.get(position)?.firstProductBarcodes?.cartQuantity = quantity
                            /// initSnackBar(context.getString(R.string.success_to_update_cart));
                            notifyItemChanged(position)
                        } else {
                            errorDialogWithButton(
                                context, context.getString(R.string.fail_to_add_cart),
                                context.getString(R.string.fail_to_update_cart)
                            )
                        }
                    }

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
            DataFeacher(false, object :
                    DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        UtilityApp.updateCart(2, productModels?.size ?: 0)
                        Log.i("tag", "Log " + UtilityApp.getCartCount())
                        productModels?.get(position)?.firstProductBarcodes?.cartQuantity = 0
                        notifyItemChanged(position)
                        initSnackBar(context.getString(R.string.success_delete_from_cart))
                        AnalyticsHandler.RemoveFromCart(cart_id, currency, 0.0)
                    } else {
                        errorDialogWithButton(
                            context, context.getString(R.string.delete_product),
                            context.getString(R.string.fail_to_delete_cart)
                        )
                    }
                }

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
                    if (position >= 0 && position < (productModels?.size ?: 0)) {
                        val userId = UtilityApp.getUserData()?.id ?: 0
                        val storeId = localModel?.cityId?.toInt() ?: UtilityApp.getLocalData().cityId.toInt()
                        val productId = productModels?.get(position)?.id ?: 0
                        val isFavorite = productModels?.get(position)?.isFavourite
                        if (isFavorite == true) {
                            removeFromFavorite(view1, position, productId, userId, storeId)
                        } else {
                            addToFavorite(view1, position, productId, userId, storeId)
                        }
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
                    var message = ""
                    val position = bindingAdapterPosition
                    if (UtilityApp.getUserData() != null && UtilityApp.getUserData()?.id != null) {
                        val productModel = productModels?.get(position)
                        val productBarcode = productModel?.firstProductBarcodes
                        val count = productBarcode?.cartQuantity ?: 0
                        val stock = productBarcode?.stockQty ?: 0
                        Log.i("lll", "Log stock$stock")
                        val userId = UtilityApp.getUserData()?.id ?: 0
                        val storeId = localModel?.cityId?.toInt() ?: UtilityApp.getLocalData().cityId.toInt()
                        val productId = productModel?.id ?: 0
                        val productBarcodeId = productBarcode?.id ?: 0
                        val limit = productBarcode?.limitQty ?: 0
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
                                errorDialogWithButton(
                                    context, context.getString(R.string.error),
                                    message
                                )
                            }
                        } else {
                            if (count.plus(1) <= stock && count.plus(1) <= limit) {
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
                                message = if (count.plus(1) > stock) {
                                    context.getString(R.string.stock_empty)
                                } else {
                                    context.getString(R.string.limit) + "" + limit
                                }
                                errorDialogWithButton(
                                    context, context.getString(R.string.error),
                                    message
                                )
                            }
                        }
                    } else {
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
                    }
                }
            }
            binding.plusCartBtn.setOnClickListener { v ->
                var message = ""
                val position = bindingAdapterPosition
                val productModel = productModels?.get(position)
                val productBarcode = productModel?.firstProductBarcodes
                //                int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                val count = binding.productCartQTY.text.toString().toInt()
                val stock = productBarcode?.stockQty ?: 0
                val userId = UtilityApp.getUserData()?.id ?: 0
                val storeId =
                    localModel?.cityId?.toInt() ?: UtilityApp.getLocalData().cityId.toInt()
                val productId = productModel?.id ?: 0
                val productBarcodeId = productBarcode?.id ?: 0
                val cartId = productBarcode?.cartId ?: 0
                val limit = productBarcode?.limitQty ?: 0
                Log.i("tag", "Log limit$limit")
                Log.i("tag", "Log cart_id$cartId")
                Log.i("tag", "Log stock$stock")
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
                        errorDialogWithButton(context, context.getString(R.string.error), message)
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
                        } else if (stock == 0) {
                            context.getString(R.string.stock_empty)
                        } else {
                            context.getString(R.string.limit) + "" + limit
                        }
                        errorDialogWithButton(context, context.getString(R.string.error), message)
                    }
                }
            }
            binding.minusCartBtn.setOnClickListener { v ->
                val position = bindingAdapterPosition
                val productModel = productModels?.get(position)
                val productBarcode = productModel?.firstProductBarcodes
                // int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                val count = binding.productCartQTY.text.toString().toInt()
                val userId = UtilityApp.getUserData()?.id ?: 0
                val storeId =
                    localModel?.cityId?.toInt() ?: UtilityApp.getLocalData().cityId.toInt()
                val productId = productModel?.id ?: 0
                val productBarcodeId = productBarcode?.id ?: 0
                val cartId = productBarcode?.cartId ?: 0
                Log.i("tag", "Log limit $limit")
                Log.i("tag", "Log cart_id $cartId")
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
                if ((productModels?.size ?: 0) > 0) {
                    val position = bindingAdapterPosition
                    val productModel = productModels?.get(position)
                    val productBarcode = productModel?.firstProductBarcodes
                    val userId = UtilityApp.getUserData()?.id ?: 0
                    val storeId =
                        localModel?.cityId?.toInt() ?: UtilityApp.getLocalData().cityId.toInt()
                    val productId = productModel?.id ?: 0
                    val productBarcodeId = productBarcode?.id ?: 0
                    val cartId = productBarcode?.cartId ?: 0
                    Log.i("tag", "Log limit$limit")
                    Log.i("tag", "Log cart_id$cartId")
                    deleteCart(v, position, productId, productBarcodeId, cartId, userId, storeId)
                }
            }
        }
    }

    init {
        this.limit = limit
    }
}