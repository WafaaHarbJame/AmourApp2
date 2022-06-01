package com.amour.shop.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.classes.*
import com.amour.shop.classes.GlobalData.GlideImg
import com.amour.shop.classes.GlobalData.errorDialogWithButton
import com.amour.shop.Dialogs.CheckLoginDialog
import com.amour.shop.Models.*
import com.amour.shop.R
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.databinding.RowEmptyBinding
import com.amour.shop.databinding.RowLoadingBinding
import com.amour.shop.databinding.RowSearchProductItemBinding
import java.lang.Exception
import java.util.ArrayList


class SearchProductAdapter(
    private val context: Context,
    productModels: List<ProductModel?>?,
    country_id: Int,
    city_id: Int,
    user_id: String,
    rv: RecyclerView,
    filter: String,
    private val onItemClick: OnItemClick? /*, int gridNumber*/
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isLoading = false
    var visibleThreshold = 5
    var show_loading = true
    var category_id = 0
    var country_id: Int
    var city_id: Int
    var subID = 0
    var user_id: String
    private var nextPage = 1
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private val productModels: MutableList<ProductModel?>?
    private val discount = 0.0
    private var currency = "BHD"
    private val rv: RecyclerView
    private val filter_text: String

    //    private int gridNumber;
    var fraction = 2
    var localModel: LocalModel? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder? = null
        if (viewType == VIEW_TYPE_ITEM) {
            val itemView = RowSearchProductItemBinding.inflate(
                LayoutInflater.from(
                    context
                ), parent, false
            )
            vh = Holder(itemView)
        } else if (viewType == VIEW_TYPE_LOADING) {
            val itemView = RowLoadingBinding.inflate(LayoutInflater.from(context), parent, false)
            vh = LoadingViewHolder(itemView)
        } else if (viewType == VIEW_TYPE_EMPTY) {
            val itemView = RowEmptyBinding.inflate(LayoutInflater.from(context), parent, false)
            vh = EmptyViewHolder(itemView)
        }
        return vh!!
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is Holder) {
            val holder = viewHolder
            val productModel = productModels!![position]
            localModel = UtilityApp.getLocalData()

            currency = localModel?.currencyCode ?:Constants.BHD
            fraction = localModel?.fractional ?:Constants.three

            holder.binding.productNameTv.text = productModel!!.productName.trim { it <= ' ' }
            if (productModels.size > 0 && productModel.isFavourite) {
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
                val discountPercent = discountValue / originalPrice * 100
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
                GlideImg(
                    context, photoUrl, R.drawable.holder_image, holder.binding.productImg
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (viewHolder is LoadingViewHolder) {
            viewHolder.rowLoadingBinding.progressBar1.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return productModels!!.size
    }

    private fun addToFavorite(v: View, position: Int, productId: Int, userId: Int, storeId: Int) {
        AnalyticsHandler.AddToWishList(productId, currency, productId.toDouble())
        DataFeacher(false,object :
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
                        initSnackBar(context.getString(R.string.success_add), v)
                        productModels!![position]!!.isFavourite = true
                        //                    rv.getRecycledViewPool().clear();
                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } else {
                        errorDialogWithButton(
                            context, context.getString(R.string.error),
                            context.getString(R.string.fail_to_add_favorite)
                        )
                    }
                }            }

        }).addToFavoriteHandle(userId, storeId, productId)
    }

    private fun removeFromFavorite(view: View, position: Int, productId: Int, userId: Int, storeId: Int) {
        DataFeacher(false,object :
            DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (func == Constants.ERROR) {
                    errorDialogWithButton(
                        context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_remove_favorite)
                    )
                } else if (func == Constants.FAIL) {
                    errorDialogWithButton(
                        context, context.getString(R.string.error),
                        context.getString(R.string.fail_to_remove_favorite)
                    )
                } else {
                    if (IsSuccess) {
                        productModels!![position]!!.isFavourite = false
                        initSnackBar(context.getString(R.string.success_delete), view)
                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } else {
                        errorDialogWithButton(
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

    private val adapter: SearchProductAdapter
        get() = this

    private fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener?) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    fun setLoaded() {
        isLoading = false
    }

    private fun setOnloadListener() {
        setOnLoadMoreListener {
            println("Log add loading item")
            if (!productModels!!.contains(null)) {
                productModels.add(null)
                println("Log productDMS size " + productModels.size)
                notifyItemInserted(productModels.size - 1)
                LoadAllData(category_id, country_id, city_id, user_id, filter_text, nextPage, 10)
            }
        }
    }

    private fun LoadAllData(
        category_id: Int,
        country_id: Int,
        city_id: Int,
        user_id: String,
        filter: String,
        page_number: Int,
        page_size: Int
    ) {
        println("Log category_id: $category_id")
        println("Log LoadAllData  page $nextPage")
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as FavouriteResultModel
                val message = context.getString(R.string.fail_to_get_data)
                if (productModels!!.size > 0) {
                    productModels.removeAt(productModels.size - 1)
                    notifyItemRemoved(productModels.size)
                }
                if (IsSuccess) {
                    if (result.data != null && result.data.size > 0) {
                        val products = result.data
                        val pos = productModels.size
                        if (products != null && products.size > 0) {
                            productModels.addAll(products)
                            notifyItemRangeInserted(pos, products.size)
                            nextPage++
                        } else {
                            show_loading = false
                        }
                    } else {
                        show_loading = false
                    }
                    setLoaded()
                }            }

        }).searchTxt(country_id, city_id, user_id, filter, page_number, page_size)
    }

    override fun getItemViewType(position: Int): Int {
        return try {
            if (productModels!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
        } catch (e: Exception) {
            e.printStackTrace()
            VIEW_TYPE_EMPTY
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    interface OnItemClick {
        fun onItemClicked(position: Int, productModel: ProductModel?)
    }

    inner class Holder internal constructor(var binding: RowSearchProductItemBinding) :
            RecyclerView.ViewHolder(
                binding.root
            ),
            View.OnClickListener {
        override fun onClick(v: View) {
            if (onItemClick != null) {
                val position = bindingAdapterPosition
                onItemClick.onItemClicked(position, productModels!![bindingAdapterPosition])
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
                            if (productModels != null && productModels[position]!!
                                        .productBarcodes != null
                            ) {
                                productModels[position]!!.firstProductBarcodes.cartQuantity = quantity
                                productModels[position]!!.firstProductBarcodes.cartId = cartId
                                notifyItemChanged(position)
                                UtilityApp.updateCart(1, productModels.size)
                                AnalyticsHandler.AddToCart(result.id, currency, quantity.toDouble())
                            }
                        } else {
                            errorDialogWithButton(
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
            view: View,
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
                DataFeacher(false,object :
                    DataFetcherCallBack {

                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        if (IsSuccess) {
                            productModels!![position]!!.firstProductBarcodes.cartQuantity = quantity
                            notifyItemChanged(position)
                        } else {
                            errorDialogWithButton(
                                context, context.getString(R.string.error),
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

        private fun deleteCart(
            v: View,
            position: Int,
            productId: Int,
            product_barcode_id: Int,
            cart_id: Int,
            userId: Int,
            storeId: Int
        ) {
            DataFeacher(false,object :
                DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        productModels!![position]!!.firstProductBarcodes.cartQuantity = 0
                        notifyItemChanged(position)
                        initSnackBar(context.getString(R.string.success_delete_from_cart), v)
                        UtilityApp.updateCart(2, productModels.size)
                        AnalyticsHandler.RemoveFromCart(cart_id, currency, 0.0)
                    } else {
                        errorDialogWithButton(
                            context, context.getString(R.string.delete_product),
                            context.getString(R.string.fail_to_delete_cart)
                        )
                    }                }

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
                    val userId = UtilityApp.getUserData()?.id?:0
                    val storeId = localModel!!.cityId.toInt()
                    val productId = productModels!![position]!!.id
                    val isFavorite = productModels[position]!!.isFavourite
                    if (isFavorite) {
                        removeFromFavorite(view1, position, productId, userId, storeId)
                    } else {
                        addToFavorite(view1, position, productId, userId, storeId)
                    }
                }
            }
            binding.cartBut.setOnClickListener { view1 ->
                if (!UtilityApp.isLogin() && UtilityApp.getUserData() == null) {
                    val checkLoginDialog = CheckLoginDialog(
                        context,
                        R.string.LoginFirst, R.string.to_add_cart, R.string.ok, R.string.cancel, null, null
                    )
                    checkLoginDialog.show()
                    return@setOnClickListener
                }
                val position = bindingAdapterPosition
                if (position > 0) {
                    val productModel = productModels!![position]
                    val productBarcode = productModel!!.firstProductBarcodes
                    val count = productBarcode.cartQuantity
                    var message = ""
                    val userId = UtilityApp.getUserData()?.id?:0
                    val storeId = localModel!!.cityId.toInt()
                    val productId = productModel.id
                    val product_barcode_id = productBarcode.id
                    val stock = productBarcode.stockQty
                    val limit = productBarcode.limitQty
                    if (limit == 0) {
                        if (count + 1 <= stock) {
                            addToCart(
                                view1,
                                position,
                                productId,
                                product_barcode_id,
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
                        if (count + 1 <= stock && count + 1 <= limit) {
                            addToCart(
                                view1,
                                position,
                                productId,
                                product_barcode_id,
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
                            errorDialogWithButton(
                                context, context.getString(R.string.error),
                                message
                            )
                        }
                    }
                }
            }
            binding.plusCartBtn.setOnClickListener { view1 ->
                val message: String
                val position = bindingAdapterPosition
                val productModel = productModels!![position]
                val productBarcode = productModel!!.firstProductBarcodes
                // int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                val count = binding.productCartQTY.text.toString().toInt()
                val userId = UtilityApp.getUserData()?.id?:0
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel.id
                val product_barcode_id = productBarcode.id
                val stock = productBarcode.stockQty
                val cart_id = productBarcode.cartId
                val limit = productBarcode.limitQty
                if (limit == 0) {
                    if (count + 1 <= stock) {
                        updateCart(
                            view1,
                            position,
                            productId,
                            product_barcode_id,
                            count + 1,
                            userId,
                            storeId,
                            cart_id,
                            "quantity"
                        )
                    } else {
                        message = context.getString(R.string.stock_empty)
                        errorDialogWithButton(
                            context, context.getString(R.string.error),
                            message
                        )
                    }
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
            binding.minusCartBtn.setOnClickListener { view1 ->
                val position = bindingAdapterPosition
                val productModel = productModels!![position]
                val productBarcode = productModel!!.firstProductBarcodes
                //  int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                val count = binding.productCartQTY.text.toString().toInt()
                val userId = UtilityApp.getUserData()?.id?:0
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel.id
                val product_barcode_id = productBarcode.id
                val cart_id = productBarcode.cartId
                updateCart(
                    view1,
                    position,
                    productId,
                    product_barcode_id,
                    count - 1,
                    userId,
                    storeId,
                    cart_id,
                    "quantity"
                )
            }
            binding.deleteCartBtn.setOnClickListener { view1 ->
                val position = bindingAdapterPosition
                val productModel = productModels!![position]
                val productBarcode = productModel!!.firstProductBarcodes
                val userId = UtilityApp.getUserData()?.id?:0
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel.id
                val product_barcode_id = productBarcode.id
                val cart_id = productBarcode.cartId
                deleteCart(view1, position, productId, product_barcode_id, cart_id, userId, storeId)
            }
        }
    }

    internal class LoadingViewHolder(var rowLoadingBinding: RowLoadingBinding) : RecyclerView.ViewHolder(
        rowLoadingBinding.root
    )

    internal class EmptyViewHolder(var rowEmptyBinding: RowEmptyBinding) : RecyclerView.ViewHolder(
        rowEmptyBinding.root
    )

    companion object {
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_EMPTY = 2
    }

    init {
        this.productModels = ArrayList(productModels)
        this.city_id = city_id
        this.country_id = country_id
        this.user_id = user_id
        this.rv = rv
        filter_text = filter
        //        this.gridNumber = gridNumber;
        val gridLayoutManager = rv.layoutManager as GridLayoutManager?
        gridLayoutManager!!.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    VIEW_TYPE_LOADING, VIEW_TYPE_EMPTY -> gridLayoutManager.spanCount //number of columns of the grid
                    else -> 1
                }
            }
        }
        rv.layoutManager = gridLayoutManager
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = gridLayoutManager.itemCount
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition()
                if (show_loading) {
                    if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener!!.onLoadMore()
                            isLoading = true
                        }
                    }
                }
                setOnloadListener()
            }
        })
    }
}
