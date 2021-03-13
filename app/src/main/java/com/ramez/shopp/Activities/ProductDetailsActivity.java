package com.ramez.shopp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ramez.shopp.Adapter.ProductSliderAdapter;
import com.ramez.shopp.Adapter.ReviewAdapter;
import com.ramez.shopp.Adapter.SuggestedProductAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.AddRateDialog;
import com.ramez.shopp.Dialogs.CheckLoginDialog;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.CartProcessModel;
import com.ramez.shopp.Models.MainModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductBarcode;
import com.ramez.shopp.Models.ProductDetailsModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.ReviewModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.ActivityProductDeatilsBinding;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ProductDetailsActivity extends ActivityBase implements SuggestedProductAdapter.OnItemClick {
    ActivityProductDeatilsBinding binding;
    int user_id = 0;
    ArrayList<String> sliderList;
    ArrayList<ProductModel> productList;
    ArrayList<ReviewModel> reviewList;
    String productName;
    ProductModel productModel;
    String currency;
    AddRateDialog addCommentDialog;
    private int category_id = 0, country_id, city_id, product_id;
    private ProductSliderAdapter productSliderAdapter;
    private SuggestedProductAdapter productOfferAdapter;
    private ReviewAdapter reviewAdapter;
    private LinearLayoutManager productLayoutManager;
    private LinearLayoutManager reviewManger;
    private int storeId;
    private boolean isFavorite;
    private boolean FROM_BROSHER=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDeatilsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        MemberModel memberModel = UtilityApp.getUserData();
        country_id = UtilityApp.getLocalData().getCountryId();
        city_id = Integer.parseInt(UtilityApp.getLocalData().getCityId());

        sliderList = new ArrayList<String>();
        productList = new ArrayList<>();
        reviewList = new ArrayList<>();

        storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
        currency = UtilityApp.getLocalData().getCurrencyCode();


        productLayoutManager = new LinearLayoutManager(getActiviy(), RecyclerView.HORIZONTAL, false);
        binding.offerRecycler.setLayoutManager(productLayoutManager);
        binding.offerRecycler.setHasFixedSize(true);

        binding.offerRecycler.setItemAnimator(null);

        reviewManger = new LinearLayoutManager(getActiviy(), RecyclerView.VERTICAL, false);
        binding.reviewRecycler.setLayoutManager(reviewManger);
        binding.reviewRecycler.setHasFixedSize(true);


        if (UtilityApp.isLogin()) {
            user_id = Integer.parseInt(String.valueOf(memberModel.getId()));

        }

        getIntentExtra();

        initListener();


    }

    private void initListener() {

        binding.backBtn.setOnClickListener(view1 -> {
            GlobalData.REFRESH_CART=true;
            onBackPressed();
        });


        binding.rateBtn.setOnClickListener(view -> {


            if (!UtilityApp.isLogin()) {

                CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_comment, R.string.ok, R.string.cancel_label, null, null);
                checkLoginDialog.show();

            } else {

                ReviewModel reviewModel = new ReviewModel();

                AddRateDialog.Click okClick = new AddRateDialog.Click() {

                    @Override
                    public void click() {

                        if (!UtilityApp.isLogin()) {

                            CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_comment, R.string.ok, R.string.cancel_label, null, null);
                            checkLoginDialog.show();

                        } else {
                            EditText note = addCommentDialog.findViewById(R.id.rateEt);
                            RatingBar ratingBar = addCommentDialog.findViewById(R.id.ratingBar);
                            String notes = note.getText().toString();

                            reviewModel.setComment(notes);
                            reviewModel.setProductId(product_id);
                            reviewModel.setStoreId(storeId);
                            reviewModel.setUser_id(user_id);
                            reviewModel.setRate((int) ratingBar.getRating());

                            if (ratingBar.getRating() == 0) {

                                Toasty.error(getActiviy(), R.string.please_fill_rate, Toast.LENGTH_SHORT, true).show();
                                YoYo.with(Techniques.Shake).playOn(ratingBar);
                                ratingBar.requestFocus();

                            } else if (note.getText().toString().isEmpty()) {

                                note.requestFocus();
                                note.setError(getString(R.string.please_fill_comment));


                            } else {
                                addComment(view, reviewModel);
                            }

                        }


                    }

                };

                AddRateDialog.Click cancelClick = new AddRateDialog.Click() {
                    @Override
                    public void click() {

                        addCommentDialog.dismiss();


                    }
                };

                addCommentDialog = new AddRateDialog(getActiviy(), getString(R.string.add_comment), R.string.ok, R.string.cancel_label, okClick, cancelClick);
                addCommentDialog.show();

            }


        });
        binding.favBut.setOnClickListener(v -> {
            Log.i("tag", "Log isFavorite" + isFavorite);

            if (!UtilityApp.isLogin()) {

                CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_favorite, R.string.ok, R.string.cancel_label, null, null);
                checkLoginDialog.show();

            } else {
                if (isFavorite) {
                    removeFromFavorite(v, product_id, user_id, storeId);

                } else {
                    addToFavorite(v, product_id, user_id, storeId);

                }

            }


        });


        binding.cartBut.setOnClickListener(view1 -> {


            if (!UtilityApp.isLogin()) {

                CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_cart, R.string.ok, R.string.cancel_label, null, null);
                checkLoginDialog.show();

            } else {
                String message;
                int count = productModel.getProductBarcodes().get(0).getCartQuantity();
                int userId = UtilityApp.getUserData().getId();
                int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
                int productId = productModel.getId();
                int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
                int stock = productModel.getProductBarcodes().get(0).getStockQty();
                int limit = productModel.getProductBarcodes().get(0).getLimitQty();
                Log.i("limit","Log limit  "+limit);
                Log.i("stock","Log stock  "+stock);




                if (limit == 0) {

                    if (count + 1 <= stock) {
                        addToCart(view1, productId, product_barcode_id, count + 1, userId, storeId);

                    } else {
                        message = getString(R.string.stock_empty);
                        GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                                message);
                    }
                } else {

                    if (count + 1 <= stock && (count + 1) <= limit) {
                        addToCart(view1, productId, product_barcode_id, count + 1, userId, storeId);

                    } else {

                        if(count+1 > stock){
                            message = getString(R.string.stock_empty);
                        }
                        else {
                            message = getString(R.string.limit) + "" + limit;

                        }
                        GlobalData.errorDialogWithButton(getActiviy(),getString(R.string.error),
                                message);
                    }

                }



            }


        });

        binding.plusCartBtn.setOnClickListener(v -> {
            String message="";

            int count = Integer.parseInt(binding.productCartQTY.getText().toString());
            int stock = productModel.getProductBarcodes().get(0).getStockQty();
            int userId = UtilityApp.getUserData().getId();
            int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
            int productId = productModel.getId();
            int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
            int cartId = productModel.getProductBarcodes().get(0).getCartId();
            int limit = productModel.getProductBarcodes().get(0).getLimitQty();
            Log.i("limit","Log limit  "+limit);
            Log.i("limit","Log limit  "+limit);
            Log.i("stock","Log cartId  "+cartId);


            if (limit == 0) {

                if (count + 1 <= stock) {
                    updateCart(v, productId, product_barcode_id, count + 1, userId, storeId, cartId, "quantity");

                } else {
                    message = getString(R.string.stock_empty);
                    GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                            message);
                }
            } else {

                if (count + 1 <= stock && (count + 1 )<= limit) {
                    updateCart(v, productId, product_barcode_id, count + 1, userId, storeId, cartId, "quantity");

                } else {

                    if(count+1 > stock){
                        message = getString(R.string.stock_empty);

                    }
                    else if(stock==0){
                        message = getString(R.string.stock_empty);

                    }
                    else {
                        message = getString(R.string.limit) + "" + limit;

                    }
                    GlobalData.errorDialogWithButton(getActiviy(),getString(R.string.error),
                            message);
                }

            }



        });

        binding.minusCartBtn.setOnClickListener(v -> {

            // int count = productModel.getProductBarcodes().get(0).getCartQuantity();
            int count = Integer.parseInt(binding.productCartQTY.getText().toString());
            int userId = UtilityApp.getUserData().getId();
            int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
            int productId = productModel.getId();
            int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
            int cart_id = productModel.getProductBarcodes().get(0).getCartId();

            updateCart(v, productId, product_barcode_id, count - 1, userId, storeId, cart_id, "quantity");


        });

        binding.deleteCartBtn.setOnClickListener(v -> {

            int userId = UtilityApp.getUserData().getId();
            int storeId = Integer.parseInt(UtilityApp.getLocalData().getCityId());
            int productId = productModel.getId();
            int product_barcode_id = productModel.getProductBarcodes().get(0).getId();
            int cart_id = productModel.getProductBarcodes().get(0).getCartId();

            deleteCart(v, productId, product_barcode_id, cart_id, userId, storeId);

        });
    }


    private void getIntentExtra() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {

            FROM_BROSHER=bundle.getBoolean(Constants.FROM_BROSHER);
            if(FROM_BROSHER){
                product_id= Integer.parseInt(bundle.getString(Constants.product_id));

            }
            else {
                ProductModel productModel = (ProductModel) bundle.getSerializable(Constants.DB_productModel);
                if (productModel != null) {
                    product_id = productModel.getId();


                    if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
                        productName = productModel.getHName();

                    } else {
                        productName = productModel.getName();

                    }

                    binding.productNameTv.setText(productName);
                    binding.mainTitleTv.setText(productName);

                }
            }


        }

        getSingleProduct(country_id, city_id, product_id, String.valueOf(user_id));


    }

    public void initAdapter() {

        productSliderAdapter = new ProductSliderAdapter(this, sliderList);
        binding.viewPager.setAdapter(productSliderAdapter);
    }

    public void initReviewAdapter() {
        reviewAdapter = new ReviewAdapter(getActiviy(), reviewList);
        binding.reviewRecycler.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClicked(int position, ProductModel productModel) {

        //getSingleProduct(country_id,city_id,productModel.getId(), String.valueOf(user_id));

    }

    @SuppressLint("SetTextI18n")
    public void getSingleProduct(int country_id, int city_id, int product_id, String user_id) {
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
        binding.cartBut.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ProductDetailsModel result = (ProductDetailsModel) obj;
            String message = getString(R.string.fail_to_get_data);

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

            if (func.equals(Constants.ERROR)) {

                if (result != null) {
                    message = result.getMessage();
                }
                binding.dataLY.setVisibility(View.GONE);
                binding.cartBut.setVisibility(View.VISIBLE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);

            } else if (func.equals(Constants.FAIL)) {

                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);
                binding.cartBut.setVisibility(View.GONE);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                binding.dataLY.setVisibility(View.GONE);

            } else {
                if (IsSuccess) {
                    if (result.getData() != null && result.getData().size() > 0) {

                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                        binding.cartBut.setVisibility(View.VISIBLE);
                        productModel = result.getData().get(0);

                        if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
                            productName = productModel.getHName();

                        } else {
                            productName = productModel.getName();

                        }

                        binding.productNameTv.setText(productName);
                        binding.mainTitleTv.setText(productName);

                        if(productModel.getDescription()!=null&&productModel.getHDescription()!=null){

                            if(UtilityApp.getLanguage().equals(Constants.Arabic)){
                                binding.productDescTv.setText(Html.fromHtml(productModel.getHDescription().toString()));

                            }
                            else {
                                binding.productDescTv.setText(Html.fromHtml(productModel.getDescription().toString()));

                            }

                        }

                        ProductBarcode productBarcode = productModel.getProductBarcodes().get(0);

                        if (productBarcode.getIsSpecial()) {
                            binding.productPriceBeforeTv.setPaintFlags(binding.productPriceBeforeTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            if (productModel.getProductBarcodes().get(0).getSpecialPrice() != null) {
                                binding.productPriceBeforeTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(productBarcode.getPrice())), UtilityApp.getLocalData().getFractional()) + " " + currency);
                               binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(productBarcode.getSpecialPrice())), UtilityApp.getLocalData().getFractional()) + " " + currency);

                            }


                        } else {
                               binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(productBarcode.getPrice())), UtilityApp.getLocalData().getFractional()) + " " + currency + "");
                              binding.productPriceBeforeTv.setVisibility(View.GONE);

                            }



                        sliderList = productModel.getImages();
                        binding.ratingBar.setRating((float) productModel.getRate());


                        String wightName = UtilityApp.getLanguage().equals(Constants.Arabic) ? productBarcode.getProductUnits().getHName() : productBarcode.getProductUnits().getName();

                        binding.weightUnitTv.setText(NumberHandler.formatDouble(productBarcode.getWeight(), 0) + " " + wightName);

                        isFavorite = productModel.getFavourite();

                        if (productModel.getFavourite() != null && isFavorite) {

                            binding.favBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.favorite_icon));
                        } else {
                            binding.favBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.empty_fav));

                        }



                        int quantity = productModel.getProductBarcodes().get(0).getCartQuantity();

                        if (quantity > 0) {
                            binding.productCartQTY.setText(String.valueOf(quantity));
                            binding.CartLy.setVisibility(View.VISIBLE);
                            binding.cartBut.setVisibility(View.GONE);

                            if (quantity == 1) {
                                binding.deleteCartBtn.setVisibility(View.VISIBLE);
                                binding.minusCartBtn.setVisibility(View.GONE);
                            } else {
                                binding.minusCartBtn.setVisibility(View.VISIBLE);
                                binding.deleteCartBtn.setVisibility(View.GONE);
                            }

                        } else {
                            binding.CartLy.setVisibility(View.GONE);
                            binding.cartBut.setVisibility(View.VISIBLE);
                        }



                        getSuggestedProduct();

                        getReviews(product_id, storeId);



                        initAdapter();

                    } else {

                        binding.dataLY.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
                        binding.cartBut.setVisibility(View.GONE);

                    }


                } else {

                    binding.dataLY.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);
                    binding.cartBut.setVisibility(View.GONE);


                }
            }

        }).GetSingleProduct(country_id, city_id, product_id, user_id);
    }

    private void addToFavorite(View v, int productId, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {

                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                        getString(R.string.fail_to_add_favorite));
            } else if (func.equals(Constants.FAIL)) {

                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                        getString(R.string.fail_to_add_favorite));
            } else {
                if (IsSuccess) {
                    binding.favBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.favorite_icon));

                    Toasty.success(getActiviy(), R.string.success_add, Toast.LENGTH_SHORT, true).show();

                    isFavorite = true;

                } else {
                    GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                            getString(R.string.fail_to_add_favorite));

                }
            }

        }).addToFavoriteHandle(userId, storeId, productId);
    }


    private void removeFromFavorite(View view, int productId, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {
                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                        getString(R.string.fail_to_remove_favorite));

            } else if (func.equals(Constants.FAIL)) {

                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                        getString(R.string.fail_to_remove_favorite));


            } else {
                if (IsSuccess) {
                    binding.favBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.empty_fav));
                    isFavorite = false;
                    Toasty.success(getActiviy(), R.string.success_delete, Toast.LENGTH_SHORT, true).show();


                } else {
                    GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                            getString(R.string.fail_to_remove_favorite));

                }
            }

        }).deleteFromFavoriteHandle(userId, storeId, productId);
    }


    public void getSuggestedProduct() {
        productList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            binding.dataLY.setVisibility(View.VISIBLE);

            MainModel result = (MainModel) obj;
            String message = getString(R.string.fail_to_get_data);

            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

            if (func.equals(Constants.ERROR)) {

                if (result != null && result.getMessage() != null) {
                    message = result.getMessage();
                }
                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);

            } else if (func.equals(Constants.FAIL)) {

                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);


            } else {
                if (IsSuccess) {
                    if (result.getQuickProducts() != null && result.getQuickProducts().size() > 0) {

                        binding.dataLY.setVisibility(View.VISIBLE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                        productList = result.getQuickProducts();
                        initProductsAdapter();

                    } else {
                        binding.noOffers.setText(getString(R.string.no_products));
                        binding.noOffers.setVisibility(View.VISIBLE);

                    }


                }
            }

        }).GetMainPage(0, country_id, city_id, String.valueOf(user_id));
    }

    private void initProductsAdapter() {
        productOfferAdapter = new SuggestedProductAdapter(getActiviy(), productList, this, 0);
        binding.offerRecycler.setAdapter(productOfferAdapter);

    }

    private void addToCart(View v, int productId, int product_barcode_id, int quantity, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            CartProcessModel result = (CartProcessModel) obj;

            if (IsSuccess) {
                int cartId=result.getId();

                productModel.getProductBarcodes().get(0).setCartId(cartId);
                productModel.getProductBarcodes().get(0).setCartQuantity(quantity);

                binding.productCartQTY.setText(String.valueOf(quantity));
                binding.CartLy.setVisibility(View.VISIBLE);
                binding.cartBut.setVisibility(View.GONE);

                if (quantity == 1) {
                 binding.deleteCartBtn.setVisibility(View.VISIBLE);
                  binding.minusCartBtn.setVisibility(View.GONE);
                } else {
                  binding.minusCartBtn.setVisibility(View.VISIBLE);
                   binding.deleteCartBtn.setVisibility(View.GONE);
                }

//                initSnackBar(getString(R.string.success_added_to_cart), v);
                UtilityApp.updateCart(1,productList.size());


            } else {


                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                        getString(R.string.fail_to_add_cart));

            }


        }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId);
    }

    private void initSnackBar(String message, View viewBar) {
        Toasty.success(getActiviy(), message, Toast.LENGTH_SHORT, true).show();

    }

    private void deleteCart(View v, int productId, int product_barcode_id, int cart_id, int userId, int storeId) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            if (IsSuccess) {
                initSnackBar(getString(R.string.success_delete_from_cart), v);
                binding.cartBut.setVisibility(View.VISIBLE);
                binding.CartLy.setVisibility(View.GONE);
                UtilityApp.updateCart(1,productList.size());


            } else {
                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                        getString(R.string.fail_to_delete_cart));

            }


        }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId);
    }

    private void updateCart(View v, int productId, int product_barcode_id, int quantity, int userId, int storeId, int cart_id, String update_quantity) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (IsSuccess) {

                binding.productCartQTY.setText(String.valueOf(quantity));

               // initSnackBar(getString(R.string.success_to_update_cart), v);
                productModel.getProductBarcodes().get(0).setCartQuantity(quantity);
                if (quantity > 0) {
                    binding.productCartQTY.setText(String.valueOf(quantity));
                    binding.CartLy.setVisibility(View.VISIBLE);
                    binding.cartBut.setVisibility(View.GONE);

                    if (quantity == 1) {
                        binding.deleteCartBtn.setVisibility(View.VISIBLE);
                        binding.minusCartBtn.setVisibility(View.GONE);
                    } else {
                        binding.minusCartBtn.setVisibility(View.VISIBLE);
                        binding.deleteCartBtn.setVisibility(View.GONE);
                    }

                } else {
                    binding.CartLy.setVisibility(View.GONE);
                    binding.cartBut.setVisibility(View.VISIBLE);
                }



            } else {
                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
                        getString(R.string.fail_to_update_cart));



            }

        }).updateCartHandle(productId, product_barcode_id, quantity, userId, storeId, cart_id, update_quantity);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NotNull MessageEvent event) {


        if (event.type.equals(MessageEvent.TYPE_main)) {
            binding.backBtn.setOnClickListener(view -> {
                Intent intent = new Intent(getActiviy(), MainActivity.class);
                startActivity(intent);
            });

        }


    }

    public void getReviews(int product_id, int storeId) {
        reviewList.clear();

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ArrayList<ReviewModel>> result = (ResultAPIModel<ArrayList<ReviewModel>>) obj;

            if (IsSuccess) {
                if (result.data != null && result.data.size() > 0) {


                    reviewList = result.data;
                    binding.productReviewTv.setVisibility(View.VISIBLE);
                    binding.reviewRecycler.setVisibility(View.VISIBLE);
                    initReviewAdapter();


                } else {

                    binding.productReviewTv.setVisibility(View.GONE);
                    binding.reviewRecycler.setVisibility(View.GONE);

                }


            }


        }).getRate(product_id, storeId);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();

        if (bundle != null) {

            ProductModel productModel = (ProductModel) bundle.getSerializable(Constants.DB_productModel);
            product_id = productModel.getId();

            if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
                productName = productModel.getHName();

            } else {
                productName = productModel.getName();

            }

            getSingleProduct(country_id, city_id, product_id, String.valueOf(user_id));

            getReviews(product_id, storeId);

            getSuggestedProduct();

            binding.productNameTv.setText(productName);

            binding.mainTitleTv.setText(productName);


        }

    }


    private void addComment(View v, ReviewModel reviewModel) {
        GlobalData.progressDialog(getActiviy(), R.string.add_comm, R.string.please_wait_sending);
        new DataFeacher(false, (obj, func, IsSuccess) -> {


            String message = getString(R.string.fail_add_comment);

            ResultAPIModel<ReviewModel> result = (ResultAPIModel<ReviewModel>) obj;

            if(result!=null){
                message=result.message;
            }


            if (func.equals(Constants.ERROR)) {

                GlobalData.errorDialog(getActiviy(), R.string.rate_app, message);


            } else if (func.equals(Constants.FAIL)) {
                GlobalData.errorDialog(getActiviy(), R.string.rate_app,message);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.errorDialog(getActiviy(), R.string.rate_app, getString(R.string.no_internet_connection));

            } else {

                if (IsSuccess) {

                    addCommentDialog.dismiss();
                    GlobalData.hideProgressDialog();
                    getReviews(product_id, storeId);
                    GlobalData.successDialog(getActiviy(), getString(R.string.rate_product), getString(R.string.success_rate_product));


                } else {
                    addCommentDialog.dismiss();
                    GlobalData.hideProgressDialog();
                    GlobalData.errorDialog(getActiviy(), R.string.rate_product, getString(R.string.fail_add_comment));

                }

            }


        }).setRate(reviewModel);
    }



}