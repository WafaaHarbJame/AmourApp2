package com.ramez.shopp.adapter

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
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Dialogs.CheckLoginDialog
import com.ramez.shopp.Models.CartProcessModel
import com.ramez.shopp.Models.FavouriteResultModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.Models.ProductModel
import com.ramez.shopp.Models.request.ProductRequest
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.classes.*
import com.ramez.shopp.classes.GlobalData.GlideImg
import com.ramez.shopp.classes.GlobalData.errorDialogWithButton
import com.ramez.shopp.databinding.RowEmptyBinding
import com.ramez.shopp.databinding.RowLoadingBinding
import com.ramez.shopp.databinding.RowSearchProductItemBinding
import retrofit2.Call
import kotlin.math.roundToInt

class ProductCategoryAdapter(
    kindId: Int,
    sortType: String,
    private val context: Context,
    rv: RecyclerView,
    productModels: List<ProductModel?>?,
    category_id: Int,
    country_id: Int,
    city_id: Int,
    user_id: String,
    limit: Int,
    filter: String?,
    private val onItemClick: OnItemClick?,
    gridNumber: Int,
    brand_id: Int,
    sortList: MutableList<SortModel>?,
    filterList: MutableList<FilterModel>?,

    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isLoading = false
    var visibleThreshold = 5
    var show_loading = true
    var categoryId: Int
    var countryId: Int
    var cityId: Int
    var userId1: String
    private var kindId = 0
    private var sortType: String = ""
    private var nextPage = 1
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private val productModels: MutableList<ProductModel?>
    private val discount = 0.0
    private var currency = "BHD"
    private var limit = 2
    private val rv: RecyclerView
    private val filterText: String?
    private val gridNumber: Int
    private var brandId = 0
    var isCanceled: Boolean
    var apiCall: Call<*>? = null
    var fraction = 2
    var localModel: LocalModel? = null
    var sortList: MutableList<SortModel>? = null
    var filterList: MutableList<FilterModel>? = null
    private var sortByTypes = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder? = null
        when (viewType) {
            VIEW_TYPE_ITEM -> {
                val itemView: RowSearchProductItemBinding = RowSearchProductItemBinding.inflate(
                    LayoutInflater.from(
                        context
                    ), parent, false
                )
                vh = Holder(itemView)
            }
            VIEW_TYPE_LOADING -> {
                val itemView: RowLoadingBinding =
                    RowLoadingBinding.inflate(LayoutInflater.from(context), parent, false)
                vh = LoadingViewHolder(itemView)
            }
            VIEW_TYPE_EMPTY -> {
                val itemView: RowEmptyBinding =
                    RowEmptyBinding.inflate(LayoutInflater.from(context), parent, false)
                vh = EmptyViewHolder(itemView)
            }
        }
        return vh!!
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                context
            )
        currency = localModel?.currencyCode ?: Constants.BHD
        fraction = localModel?.fractional ?: Constants.two

        if (viewHolder is Holder) {
            val productModel = productModels[position]
            localModel =
                if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                    context
                )

            viewHolder.binding.productNameTv.text = productModel!!.productName.trim { it <= ' ' }
            if (productModel.isFavourite) {
                viewHolder.binding.favBut.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.favorite_icon
                    )
                )
            } else {
                viewHolder.binding.favBut.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.empty_fav
                    )
                )
            }
            val quantity = productModel.firstProductBarcodes.cartQuantity
            if (quantity > 0) {
                viewHolder.binding.productCartQTY.text = quantity.toString()
                viewHolder.binding.CartLy.visibility = View.VISIBLE
                viewHolder.binding.cartBut.visibility = View.GONE
                if (quantity == 1) {
                    viewHolder.binding.deleteCartBtn.visibility = View.VISIBLE
                    viewHolder.binding.minusCartBtn.visibility = View.GONE
                } else {
                    viewHolder.binding.minusCartBtn.visibility = View.VISIBLE
                    viewHolder.binding.deleteCartBtn.visibility = View.GONE
                }
            } else {
                viewHolder.binding.CartLy.visibility = View.GONE
                viewHolder.binding.cartBut.visibility = View.VISIBLE
            }
            if (productModel.firstProductBarcodes.isSpecial) {
                val productBarcode = productModel.firstProductBarcodes
                val originalPrice = productBarcode.price
                val specialPrice = productBarcode.specialPrice
                viewHolder.binding.productPriceBeforeTv.background = ContextCompat.getDrawable(
                    context, R.drawable.itlatic_red_line
                )
                viewHolder.binding.productPriceBeforeTv.text = NumberHandler.formatDouble(
                    originalPrice,
                    fraction
                ) + " " + currency
                viewHolder.binding.productPriceTv.text = NumberHandler.formatDouble(
                    specialPrice,
                    fraction
                ) + " " + currency
                val discountValue = originalPrice - specialPrice
//                val discountPercent = NumberHandler.roundDouble(discountValue / originalPrice * 100)
                val discountPercent = discountValue / originalPrice * 100
                if (originalPrice > 0) {
                    viewHolder.binding.discountTv.text = NumberHandler.arabicToDecimal(
                        "${discountPercent.roundToInt()} % OFF"
                    )
                } else {
                    viewHolder.binding.discountTv.text = NumberHandler.arabicToDecimal(
                        0.toString() + " % " + "OFF"
                    )
                }
            } else {
                val price =
                    if (productModel.firstProductBarcodes != null) productModel.firstProductBarcodes.price else 0.0
                viewHolder.binding.productPriceTv.text = NumberHandler.formatDouble(
                    price,
                    fraction
                ) + " " + currency + ""
                viewHolder.binding.productPriceBeforeTv.visibility = View.GONE
                viewHolder.binding.discountTv.visibility = View.GONE
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
                    context, photoUrl, R.drawable.holder_image, viewHolder.binding.productImg
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (viewHolder is LoadingViewHolder) {
            viewHolder.rowLoadingBinding.progressBar1.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return if (limit == 2) Math.min(productModels.size, limit) else productModels.size
    }

    private fun addToFavorite(v: View, position: Int, productId: Int, userId: Int, storeId: Int) {
        AnalyticsHandler.AddToWishList(productId, currency, productId.toDouble())
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (func == Constants.ERROR) {
                    errorDialogWithButton(
                        context,
                        context.getString(R.string.error),
                        context.getString(R.string.fail_to_add_favorite)
                    )
                } else if (func == Constants.FAIL) {
                    errorDialogWithButton(
                        context,
                        context.getString(R.string.error),
                        context.getString(R.string.fail_to_add_favorite)
                    )
                } else {
                    if (IsSuccess) {
                        Toast.makeText(
                            context,
                            "" + context.getString(R.string.success_add),
                            Toast.LENGTH_SHORT
                        ).show()
                        productModels[position]!!.isFavourite = true
                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } else {
                        errorDialogWithButton(
                            context,
                            context.getString(R.string.error),
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
                        context,
                        context.getString(R.string.error),
                        context.getString(R.string.fail_to_remove_favorite)
                    )
                } else if (func == Constants.FAIL) {
                    errorDialogWithButton(
                        context,
                        context.getString(R.string.error),
                        context.getString(R.string.fail_to_remove_favorite)
                    )
                } else {
                    if (IsSuccess) {
                        productModels[position]!!.isFavourite = false
                        Toast.makeText(
                            context,
                            "" + context.getString(R.string.success_delete),
                            Toast.LENGTH_SHORT
                        ).show()
                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } else {
                        errorDialogWithButton(
                            context,
                            context.getString(R.string.error),
                            context.getString(R.string.fail_to_remove_favorite)
                        )
                    }
                }
            }

        }).deleteFromFavoriteHandle(userId, storeId, productId)
    }

    private fun initSnackBar(message: String, viewBar: View) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private val adapter: ProductCategoryAdapter
        private get() = this

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener?) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    fun setLoaded() {
        isLoading = false
    }

    private fun setOnLoadListener() {
        setOnLoadMoreListener {
            println("Log add loading item")
            if (!productModels.contains(null)) {
                productModels.add(null)
                println("Log productDMS size " + productModels.size)
                notifyItemInserted(productModels.size - 1)
                val productRequest = ProductRequest(
                    categoryId,
                    countryId,
                    cityId,
                    "",
                    brandId,
                    nextPage,
                    10,
                    kindId,
                    sortList,
                    filterList
                )
                LoadAllData(productRequest)
            }
        }
    }

    private fun LoadAllData(productRequest: ProductRequest?) {
        println("Log category_id: ${productRequest?.categoryId}")
        println("Log LoadAllData  page $nextPage")
        val dataFeacher =
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isCanceled) {
                        return
                    }
                    val result = obj as FavouriteResultModel
                    if (productModels.size > 0) {
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

//                                when {
//                                    sortByTypes == 1 -> {
//                                        productModels.sortBy { it?.firstProductBarcodes?.price }
//                                    }
//                                    sortByTypes == 2 -> {
//                                        productModels.sortByDescending { it?.firstProductBarcodes?.price  }
//
//                                    }
//                                    sortByTypes == 3 -> {
//                                        productModels.sortBy { it?.id }
//
//                                    }
//                                    sortByTypes == 4 -> {
//                                        productModels.sortByDescending { it?.id  }
//                                    }
//                                }
//
//                                notifyDataSetChanged()

                            }
                        } else {
                            show_loading = false
                        }

                        setLoaded()
                    }
                }

            })
        apiCall = dataFeacher.getProductList(
            productRequest
        )
    }

    override fun getItemViewType(position: Int): Int {
        return try {
            if (productModels[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
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

    inner class Holder internal constructor(view: RowSearchProductItemBinding) :
            RecyclerView.ViewHolder(view.getRoot()),
            View.OnClickListener {
        var binding: RowSearchProductItemBinding
        override fun onClick(v: View) {
            if (onItemClick != null) {
                val position = bindingAdapterPosition
                if (position >= 0 && position < productModels.size) {
                    onItemClick.onItemClicked(bindingAdapterPosition, productModels[position])
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
                        val result = obj as CartProcessModel
                        if (IsSuccess) {
                            val cartId = result.id
                            val productBarcode =
                                productModels[position]!!.firstProductBarcodes
                            productBarcode.cartQuantity = quantity
                            productBarcode.cartId = cartId
                            notifyItemChanged(position)
                            UtilityApp.updateCart(1, productModels.size)
                            AnalyticsHandler.AddToCart(cartId, currency, quantity.toDouble())
                        } else {
                            errorDialogWithButton(
                                context,
                                context.getString(R.string.fail_to_add_cart),
                                context.getString(R.string.fail_to_delete_cart)
                            )
                        }
                    }

                }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId)
            } else {
                Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT)
                    .show()
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
                DataFeacher(false, object :
                        DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        if (IsSuccess) {
                            productModels[position]!!.firstProductBarcodes.cartQuantity = quantity
                            notifyItemChanged(position)
                        } else {
                            errorDialogWithButton(
                                context,
                                context.getString(R.string.fail_to_update_cart),
                                context.getString(R.string.fail_to_delete_cart)
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
                Toast.makeText(context, context.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT)
                    .show()
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
                        productModels[position]!!.firstProductBarcodes.cartQuantity = 0
                        notifyItemChanged(position)
                        initSnackBar(context.getString(R.string.success_delete_from_cart), v)
                        UtilityApp.updateCart(2, productModels.size)
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
            binding = view
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
                    val productId = productModels[position]!!.id
                    val isFavorite = productModels[position]!!.isFavourite
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
                    if (position >= 0 && productModels.size > 0 && position < productModels.size) {
                        if (UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
                            val productModel = productModels[bindingAdapterPosition]
                            val productBarcode = productModel!!.firstProductBarcodes
                            val count = productBarcode.cartQuantity
                            val userId = UtilityApp.getUserData().id
                            val storeId = localModel!!.cityId.toInt()
                            val productId = productModel.id
                            val product_barcode_id = productBarcode.id
                            val limit = productBarcode.limitQty
                            val stock = productBarcode.stockQty
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
                                        context,
                                        context.getString(R.string.error),
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
                                        context.getString(R.string.stock_empty)
                                    } else {
                                        context.getString(R.string.limit) + "" + limit
                                    }
                                    errorDialogWithButton(
                                        context,
                                        context.getString(R.string.error),
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
            }
            binding.plusCartBtn.setOnClickListener { view1 ->
                val message: String
                val productModel = productModels[bindingAdapterPosition]
                val count: Int = binding.productCartQTY.getText().toString().toInt()
                val position = bindingAdapterPosition
                val userId = UtilityApp.getUserData().id
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel!!.id
                val productBarcode = productModel.firstProductBarcodes
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
                            context,
                            message,
                            context.getString(R.string.fail_to_delete_cart)
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
                val productModel = productModels[bindingAdapterPosition]
                val count: Int = binding.productCartQTY.getText().toString().toInt()
                val position = bindingAdapterPosition
                val userId = UtilityApp.getUserData().id
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel!!.id
                val productBarcode = productModel.firstProductBarcodes
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
                val productModel = productModels[bindingAdapterPosition]
                val position = bindingAdapterPosition
                val userId = UtilityApp.getUserData().id
                val storeId = localModel!!.cityId.toInt()
                val productId = productModel!!.id
                val productBarcode = productModel.firstProductBarcodes
                val product_barcode_id = productBarcode.id
                val cart_id = productBarcode.cartId
                deleteCart(view1, position, productId, product_barcode_id, cart_id, userId, storeId)
            }
        }
    }

    internal class LoadingViewHolder(view: RowLoadingBinding) : RecyclerView.ViewHolder(view.getRoot()) {
        var rowLoadingBinding = view

    }

    internal class EmptyViewHolder(view: RowEmptyBinding) : RecyclerView.ViewHolder(view.getRoot()) {
        private var rowEmptyBinding: RowEmptyBinding = view

    }

    companion object {
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_EMPTY = 2
    }

    init {
        this.productModels = ArrayList(productModels)
        this.limit = limit
        this.categoryId = category_id
        this.cityId = city_id
        this.countryId = country_id
        this.userId1 = user_id
        this.kindId = kindId
        this.sortType = sortType
        this.rv = rv
        filterText = filter
        this.gridNumber = gridNumber
        isCanceled = false
        this.brandId = brand_id
        this.sortList = sortList
        this.filterList = filterList
        this.sortByTypes = sortByTypes
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
                setOnLoadListener()
            }
        })
    }
}
