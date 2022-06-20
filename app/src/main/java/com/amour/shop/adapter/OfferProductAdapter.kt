package com.amour.shop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.CallBack.DataCallback
import com.amour.shop.Dialogs.CheckLoginDialog
import com.amour.shop.Models.*
import com.amour.shop.Models.request.ProductRequest
import com.amour.shop.R
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData.GlideImg
import com.amour.shop.classes.GlobalData.errorDialogWithButton
import com.amour.shop.classes.OnLoadMoreListener
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.RowEmptyBinding
import com.amour.shop.databinding.RowLoadingBinding
import com.amour.shop.databinding.RowSearchProductItemBinding
import kotlin.math.roundToInt


class OfferProductAdapter(
    private val activity: Context, productList: List<ProductModel?>?, category_id: Int, subID: Int,
    country_id: Int, city_id: Int, user_id: String?, limit: Int, rv: RecyclerView,
    filter: String?, onItemClicked: OnItemClick?, callback: DataCallback, spanCount: Int
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isLoading = false
    var visibleThreshold = 5
    var show_loading = true
    var categoryId = 0
    var countryId = 0
    var cityId = 0
    var subId = 0
    var kind_id = 0
    var userId: String? = null
    var sortType = ""
    private var nextPage = 1
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var mOnLoadMoreListener: OnLoadMoreListener? = null
    private val brand_id = 0
    private val onItemClick: OnItemClick?
    private lateinit var productModels: MutableList<ProductModel?>
    private var currency = "BHD"
    private val rv: RecyclerView? = null
    private var filter_text: String? = null
    private var gridNumber = 2
    private val limit = 2
    private val dataCallback: DataCallback
    private var fraction = 3
    var localModel: LocalModel? = null
    private var productRequest: ProductRequest? = null
    private fun setAdapterData(
        productList: List<ProductModel?>?, category_id: Int, subID: Int,
        country_id: Int, city_id: Int, user_id: String?,
        filter: String?, spanCount: Int
    ) {
        productModels = ArrayList(productList)
        categoryId = category_id
        subId = subID
        countryId = country_id
        cityId = city_id
        userId = user_id
        filter_text = filter
        gridNumber = spanCount
        nextPage = 1
        isLoading = false
        show_loading = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder? = null
        if (viewType == VIEW_TYPE_ITEM) {
            val itemView = RowSearchProductItemBinding.inflate(LayoutInflater.from(activity), parent, false)
            vh = Holder(itemView)
        } else if (viewType == VIEW_TYPE_LOADING) {
            val itemView = RowLoadingBinding.inflate(LayoutInflater.from(activity), parent, false)
            vh = LoadingViewHolder(itemView)
        } else if (viewType == VIEW_TYPE_EMPTY) {
            val itemView = RowEmptyBinding.inflate(LayoutInflater.from(activity), parent, false)
            vh = EmptyViewHolder(itemView)
        }
        return vh!!
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is Holder) {
            val productModel = productModels[position]
            localModel = UtilityApp.getLocalData()
            currency = localModel?.currencyCode ?: Constants.BHD
            fraction = localModel?.fractional ?: Constants.three
            viewHolder.binding.productNameTv.text = productModel!!.productName.trim { it <= ' ' }
            if (productModel.isFavourite) {
                viewHolder.binding.favBut.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.favorite_icon
                    )
                )
            } else {
                viewHolder.binding.favBut.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
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
                val originalPrice = productModel.firstProductBarcodes.price
                val specialPrice = productModel.firstProductBarcodes.specialPrice
                viewHolder.binding.productPriceBeforeTv.background =
                    ContextCompat.getDrawable(activity, R.drawable.itlatic_red_line)
                viewHolder.binding.productPriceBeforeTv.text = NumberHandler.formatDouble(
                    originalPrice,
                    fraction
                ) + " " + currency
                viewHolder.binding.productPriceTv.text = NumberHandler.formatDouble(
                    specialPrice,
                    fraction
                ) + " " + currency
                val discountValue = originalPrice - specialPrice
                val discountPercent = discountValue / originalPrice * 100
                if (originalPrice > 0) {
                    viewHolder.binding.discountTv.text =
                        NumberHandler.arabicToDecimal("${discountPercent.roundToInt()} % OFF")
                } else {
                    viewHolder.binding.discountTv.text =
                        NumberHandler.arabicToDecimal(0.toString() + " % " + "OFF")
                }
            } else {
                viewHolder.binding.productPriceTv.text =
                    NumberHandler.formatDouble(
                        productModel.firstProductBarcodes.price,
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
                GlideImg(activity, photoUrl, R.drawable.holder_image, viewHolder.binding.productImg)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (viewHolder is LoadingViewHolder) {
            viewHolder.rowLoadingBinding.progressBar1.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return productModels.size
    }

    private fun addToFavorite(v: View, position: Int, productId: Int, userId: Int, storeId: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (func == Constants.ERROR) {
                    errorDialogWithButton(
                        activity,
                        activity.getString(R.string.error),
                        activity.getString(R.string.fail_to_add_favorite)
                    )
                } else if (func == Constants.FAIL) {
                    errorDialogWithButton(
                        activity,
                        activity.getString(R.string.error),
                        activity.getString(R.string.fail_to_add_favorite)
                    )
                } else {
                    if (IsSuccess) {
                        initSnackBar(activity.getString(R.string.success_add), v)
                        productModels[position]!!.isFavourite = true
                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } else {
                        errorDialogWithButton(
                            activity,
                            activity.getString(R.string.error),
                            activity.getString(R.string.fail_to_add_favorite)
                        )
                    }
                }
            }
        }
        ).addToFavoriteHandle(userId, storeId, productId)
    }

    private fun removeFromFavorite(view: View, position: Int, productId: Int, userId: Int, storeId: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (func == Constants.ERROR) {
                    errorDialogWithButton(
                        activity,
                        activity.getString(R.string.error),
                        activity.getString(R.string.fail_to_remove_favorite)
                    )
                } else if (func == Constants.FAIL) {
                    errorDialogWithButton(
                        activity,
                        activity.getString(R.string.error),
                        activity.getString(R.string.fail_to_remove_favorite)
                    )
                } else {
                    if (IsSuccess) {
                        productModels[position]!!.isFavourite = false
                        initSnackBar(activity.getString(R.string.success_delete), view)
                        notifyItemChanged(position)
                        notifyDataSetChanged()
                    } else {
                        errorDialogWithButton(
                            activity,
                            activity.getString(R.string.error),
                            activity.getString(R.string.fail_to_remove_favorite)
                        )
                    }
                }
            }
        }).deleteFromFavoriteHandle(userId, storeId, productId)
    }


    private fun initSnackBar(message: String, viewBar: View) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    private val adapter: OfferProductAdapter
        get() = this

    private fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener?) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    fun setLoaded() {
        isLoading = false
    }

    private fun setOnloadListener() {

//        mOnLoadMoreListener = null;
        setOnLoadMoreListener {
//            System.out.println("Log add loading item");
            if (!productModels.contains(null)) {
                productModels.add(null)
                //                System.out.println("Log productDMS size " + productModels.size());
                notifyItemInserted(productModels.size - 1)
                productRequest = ProductRequest(
                    categoryId,
                    countryId,
                    cityId,
                    filter_text,
                    brand_id,
                    nextPage,
                    10,
                    kind_id,
                    null,
                    null
                )
                LoadAllData(productRequest!!)
            }
        }
    }

    private fun LoadAllData(productRequest: ProductRequest) {

        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                productModels.removeAt(productModels.size - 1)
                notifyItemRemoved(productModels.size)
                if (IsSuccess) {
                    val result = obj as FavouriteResultModel?
//                if (result.getData() != null && result.getData().size() > 0) {
                    val products = result?.data
                    val pos = productModels.size
                    if (products != null && products.size > 0) {
                        productModels.addAll(products)
                        notifyItemRangeInserted(pos, products.size)
                        nextPage++
                    } else {
                        show_loading = false
                    }

//                } else {
//                    show_loading = false;
//                }
                    setLoaded()
                }
            }

        }).getProductList(productRequest)
    }

    override fun getItemViewType(position: Int): Int {
        return try {
            if (productModels[position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
        } catch (e: Exception) {
            e.printStackTrace()
            VIEW_TYPE_EMPTY
        }
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
                onItemClick.onItemClicked(position, productModels[position])
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
                        if (IsSuccess) {
                            val result = obj as CartProcessModel?
                            val cartId = result?.id?:0
                            productModels[position]!!.firstProductBarcodes.cartQuantity = quantity
                            productModels[position]!!.firstProductBarcodes.cartId = cartId
                            notifyItemChanged(position)
                            UtilityApp.updateCart(1, productModels.size)
                        } else {
                            errorDialogWithButton(
                                activity,
                                activity.getString(R.string.error),
                                activity.getString(R.string.fail_to_add_cart)
                            )
                        }
                    }
                }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId)
            } else {
                Toast.makeText(activity, activity.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT)
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
                DataFeacher(false, object : DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        if (IsSuccess) {
                            productModels[position]!!.firstProductBarcodes.cartQuantity = quantity
                            notifyItemChanged(position)
                        } else {
                            errorDialogWithButton(
                                activity,
                                activity.getString(R.string.error),
                                activity.getString(R.string.fail_to_update_cart)
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
                Toast.makeText(activity, activity.getString(R.string.quanity_wrong), Toast.LENGTH_SHORT)
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
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        productModels[position]!!.firstProductBarcodes.cartQuantity = 0
                        notifyItemChanged(position)
                        initSnackBar(activity.getString(R.string.success_delete_from_cart), v)
                        UtilityApp.updateCart(2, productModels.size)
                    } else {
                        errorDialogWithButton(
                            activity, activity.getString(R.string.delete_product),
                            activity.getString(R.string.fail_to_delete_cart)
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
                        activity,
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
                        activity,
                        R.string.LoginFirst,
                        R.string.to_add_cart,
                        R.string.ok,
                        R.string.cancel,
                        null,
                        null
                    )
                    checkLoginDialog.show()
                } else {
                    if (UtilityApp.getUserData() != null && UtilityApp.getUserData()?.id != null) {
                        val position = bindingAdapterPosition
                        val productModel = productModels[position]
                        val productBarcode = productModel!!.firstProductBarcodes
                        val count = productBarcode.cartQuantity
                        var message = ""
                        val userId = UtilityApp.getUserData()?.id?:0
                        val storeId = localModel!!.cityId.toInt()
                        val productId = productModel.id
                        val productBarcodeId = productBarcode.id
                        val stock = productBarcode.stockQty
                        val limit = productBarcode.limitQty
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
                                message = activity.getString(R.string.stock_empty)
                                errorDialogWithButton(
                                    activity,
                                    activity.getString(R.string.error),
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
                                    activity.getString(R.string.limit) + "" + limit
                                } else {
                                    activity.getString(R.string.stock_empty)
                                }
                                errorDialogWithButton(
                                    activity,
                                    activity.getString(R.string.error),
                                    message
                                )
                            }
                        }
                    } else {
                        val checkLoginDialog = CheckLoginDialog(
                            activity,
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
            binding.plusCartBtn.setOnClickListener { view1 ->
                val message: String
                val position = bindingAdapterPosition
                val productModel = productModels[position]
                val productBarcode = productModel!!.firstProductBarcodes
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
                        message = activity.getString(R.string.stock_empty)
                        errorDialogWithButton(
                            activity,
                            activity.getString(R.string.error),
                            message
                        )
                    }
                } else {
                    if (count + 1 <= stock && count + 1 <= limit) {
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
                        message = if (count + 1 > stock) {
                            activity.getString(R.string.limit) + "" + limit
                        } else if (stock == 0) {
                            activity.getString(R.string.stock_empty)
                        } else {
                            activity.getString(R.string.limit) + "" + limit
                        }
                        errorDialogWithButton(
                            activity,
                            activity.getString(R.string.error),
                            message
                        )
                    }
                }
            }
            binding.minusCartBtn.setOnClickListener { view1 ->
                val position = bindingAdapterPosition
                val productModel = productModels[position]
                //  int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                val productBarcode = productModel!!.firstProductBarcodes
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
                val productModel = productModels[position]
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

    internal inner class LoadingViewHolder(var rowLoadingBinding: RowLoadingBinding) :
            RecyclerView.ViewHolder(
                rowLoadingBinding.root
            ) {
        var progressBar: ProgressBar?
            get() = rowLoadingBinding.progressBar1
            set(var1) {
                var var1 = var1
                var1 = rowLoadingBinding.progressBar1
            }

    }

    internal inner class EmptyViewHolder(var rowEmptyBinding: RowEmptyBinding) : RecyclerView.ViewHolder(
        rowEmptyBinding.root
    )

    companion object {
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_LOADING = 0
        const val VIEW_TYPE_EMPTY = 2
    }

    init {
        setAdapterData(productList, category_id, subID, country_id, city_id, user_id, filter, spanCount)
        onItemClick = onItemClicked
        dataCallback = callback
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
        rv.clearOnScrollListeners()
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = gridLayoutManager.itemCount
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition()
                if (show_loading) {
                    if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
                        if (mOnLoadMoreListener != null) {
                            isLoading = true
                            mOnLoadMoreListener!!.onLoadMore()
                        }
                    }
                }
                setOnloadListener()
            }
        })
    }
}