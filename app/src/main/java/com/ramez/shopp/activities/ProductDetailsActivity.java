//package com.ramez.shopp.activities;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Html;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.RatingBar;
//import android.widget.Toast;
//
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentStatePagerAdapter;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager.widget.ViewPager;
//
//import com.daimajia.androidanimations.library.Techniques;
//import com.daimajia.androidanimations.library.YoYo;
//import com.google.firebase.analytics.FirebaseAnalytics;
//import com.ramez.shopp.Classes.Constants;
//import com.ramez.shopp.adapter.ProductOptionAdapter;
//import com.ramez.shopp.adapter.ReviewAdapter;
//import com.ramez.shopp.adapter.SuggestedProductAdapter;
//import com.ramez.shopp.ApiHandler.DataFeacher;
//import com.ramez.shopp.Classes.AnalyticsHandler;
//import com.ramez.shopp.Classes.GlobalData;
//import com.ramez.shopp.Classes.UtilityApp;
//import com.ramez.shopp.Dialogs.AddRateDialog;
//import com.ramez.shopp.Dialogs.CheckLoginDialog;
//import com.ramez.shopp.fragments.ImageFragment;
//import com.ramez.shopp.fragments.SuggestedProductsFragment;
//import com.ramez.shopp.Models.CartProcessModel;
//import com.ramez.shopp.Models.FavouriteResultModel;
//import com.ramez.shopp.Models.LocalModel;
//import com.ramez.shopp.Models.MemberModel;
//import com.ramez.shopp.Models.ProductBarcode;
//import com.ramez.shopp.Models.ProductDetailsModel;
//import com.ramez.shopp.Models.ProductModel;
//import com.ramez.shopp.Models.ResultAPIModel;
//import com.ramez.shopp.Models.ReviewModel;
//import com.ramez.shopp.R;
//import com.ramez.shopp.Utils.ActivityHandler;
//import com.ramez.shopp.Utils.DateHandler;
//import com.ramez.shopp.Utils.NumberHandler;
//import com.ramez.shopp.databinding.ActivityProductDeatilsBinding;
//
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//import es.dmoral.toasty.Toasty;
//
//public class ProductDetailsActivity extends ActivityBase implements SuggestedProductAdapter.OnItemClick {
//    ActivityProductDeatilsBinding binding;
//    int user_id = 0;
//    //    ArrayList<String> sliderList;
//    ArrayList<ProductModel> productList;
//    ArrayList<ReviewModel> reviewList;
//    String productName;
//    ProductModel productModel;
//    String currency;
//    boolean isNotify = false;
//    AddRateDialog addCommentDialog;
//    private int  country_id, city_id, product_id;
//    private SuggestedProductAdapter productOfferAdapter;
//    private ReviewAdapter reviewAdapter;
//    private LinearLayoutManager productLayoutManager;
//    private LinearLayoutManager reviewManger;
//    private int storeId;
//    private boolean isFavorite;
//    private boolean FROM_BROSHER = false;
//    private ProductBarcode selectedProductBarcode;
//    private int selectedProductPos;
//    private FirebaseAnalytics mFirebaseAnalytics;
//    LocalModel localModel;
//    private int fraction=2;
//    private int categoryId=0;
//    String filter;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityProductDeatilsBinding.inflate(getLayoutInflater());
//        View view = binding.getRoot();
//        setContentView(view);
//
//        MemberModel memberModel = UtilityApp.getUserData();
//
//        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData()
//                : UtilityApp.getDefaultLocalData(getActiviy());
//        country_id = localModel.getCountryId() ;
//
//        city_id = Integer.parseInt(localModel.getCityId());
//
//        productList = new ArrayList<>();
//        reviewList = new ArrayList<>();
//
//        currency = localModel.getCurrencyCode();
//
//        fraction=localModel.getFractional();
//
//        storeId = Integer.parseInt(localModel.getCityId());
//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//
//        productLayoutManager = new LinearLayoutManager(getActiviy(), RecyclerView.HORIZONTAL, false);
//        binding.offerRecycler.setLayoutManager(productLayoutManager);
//        binding.offerRecycler.setHasFixedSize(true);
//        binding.offerRecycler.setItemAnimator(null);
//
//
//        LinearLayoutManager productOptionLayoutManager = new LinearLayoutManager(getActiviy(), RecyclerView.HORIZONTAL, false);
//        binding.productOptionRv.setLayoutManager(productOptionLayoutManager);
//
//        binding.productOptionRv.setHasFixedSize(true);
//        binding.offerRecycler.setHasFixedSize(true);
//        binding.reviewRecycler.setHasFixedSize(true);
//
//        binding.productOptionRv.setItemAnimator(null);
//        binding.offerRecycler.setItemAnimator(null);
//        binding.reviewRecycler.setItemAnimator(null);
//
//
//        reviewManger = new LinearLayoutManager(getActiviy(), RecyclerView.VERTICAL, false);
//        binding.reviewRecycler.setLayoutManager(reviewManger);
//        binding.reviewRecycler.setHasFixedSize(true);
//
//
//        if (UtilityApp.isLogin()) {
//            if (memberModel != null) {
//                user_id = Integer.parseInt(String.valueOf(memberModel.getId()));
//
//            }
//
//
//        }
//
//        getIntentExtra();
//
//        initListener();
//
//
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void initListener() {
//
//
//        binding.backBtn.setOnClickListener(view1 -> {
//            GlobalData.REFRESH_CART = true;
//
//            if (isNotify) {
//                Intent intent = new Intent(getActiviy(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            } else onBackPressed();
//
//        });
//
//        binding.moreBoughtBut.setOnClickListener(v -> {
//            Intent intent = new Intent(getActiviy(), AllListActivity.class);
//            intent.putExtra(Constants.LIST_MODEL_NAME, getString(R.string.best_sell));
//            intent.putExtra(Constants.FILTER_NAME, Constants.quick_filter);
//            startActivity(intent);
//
//        });
//
//
//        binding.showAllBut.setOnClickListener(v -> {
//
//            binding.showAllBut.setText(binding.productDesc1Tv.isExpanded() ? R.string.ShowAll : R.string.Show_less);
//            binding.productDesc1Tv.toggle();
//
//        });
//
//        binding.addRateBut.setOnClickListener(view -> {
//
//
//            if (!UtilityApp.isLogin()) {
//
//                CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_comment, R.string.ok, R.string.cancel_label, null, null);
//                checkLoginDialog.show();
//
//            } else {
//
//                ReviewModel reviewModel = new ReviewModel();
//
//                AddRateDialog.Click okClick = new AddRateDialog.Click() {
//
//                    @Override
//                    public void click() {
//
//                        if (!UtilityApp.isLogin()) {
//
//                            CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_comment, R.string.ok, R.string.cancel_label, null, null);
//                            checkLoginDialog.show();
//
//                        } else {
//                            EditText note = addCommentDialog.findViewById(R.id.rateEt);
//                            RatingBar ratingBar = addCommentDialog.findViewById(R.id.ratingBar);
//                            String notes = note.getText().toString();
//
//                            reviewModel.setComment(notes);
//                            reviewModel.setProductId(product_id);
//                            reviewModel.setStoreId(storeId);
//                            reviewModel.setUser_id(user_id);
//                            reviewModel.setRate((int) ratingBar.getRating());
//
//                            if (ratingBar.getRating() == 0) {
//                                Toasty.error(getActiviy(), R.string.please_fill_rate, Toast.LENGTH_SHORT, true).show();
//                                YoYo.with(Techniques.Shake).playOn(ratingBar);
//                                ratingBar.requestFocus();
//
//                            } else if (note.getText().toString().isEmpty()) {
//
//                                note.requestFocus();
//                                note.setError(getString(R.string.please_fill_comment));
//
//
//                            } else {
//                                addComment(view, reviewModel);
//                            }
//
//                        }
//
//
//                    }
//
//                };
//
//                AddRateDialog.Click cancelClick = new AddRateDialog.Click() {
//                    @Override
//                    public void click() {
//
//                        addCommentDialog.dismiss();
//
//
//                    }
//                };
//
//                addCommentDialog = new AddRateDialog(getActiviy(), getString(R.string.add_comment), R.string.ok, R.string.cancel_label, okClick, cancelClick);
//                addCommentDialog.show();
//
//            }
//
//
//        });
//
//        binding.shareBtn.setOnClickListener(v -> {
//
//            ActivityHandler.shareTextUrlDeep(getActiviy(), getString(R.string.share_note)
//                            + "  https://ramezshopping.com/product/" + product_id + "/store/" + storeId,
//                    null, null);
//
//
//        });
//        binding.addToFavBut.setOnClickListener(v -> {
//            Log.i("tag", "Log isFavorite" + isFavorite);
//
//
//            if (!UtilityApp.isLogin()) {
//
//                CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_favorite, R.string.ok, R.string.cancel_label, null, null);
//                checkLoginDialog.show();
//
//            } else {
//                if (isFavorite) {
//                    removeFromFavorite(v, product_id, user_id, storeId);
//
//                } else {
//                    addToFavorite(v, product_id, user_id, storeId);
//
//                }
//
//            }
//
//
//        });
//
//        binding.ratingBar.setOnTouchListener((v, event) -> {
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                if (!UtilityApp.isLogin()) {
//
//                    CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_comment, R.string.ok, R.string.cancel_label, null, null);
//                    checkLoginDialog.show();
//
//                } else {
//
//                    ReviewModel reviewModel = new ReviewModel();
//
//                    AddRateDialog.Click okClick = new AddRateDialog.Click() {
//
//                        @Override
//                        public void click() {
//
//                            if (!UtilityApp.isLogin()) {
//
//                                CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_comment, R.string.ok, R.string.cancel_label, null, null);
//                                checkLoginDialog.show();
//
//                            } else {
//                                EditText note = addCommentDialog.findViewById(R.id.rateEt);
//                                RatingBar ratingBar = addCommentDialog.findViewById(R.id.ratingBar);
//                                String notes = note.getText().toString();
//
//                                reviewModel.setComment(notes);
//                                reviewModel.setProductId(product_id);
//                                reviewModel.setStoreId(storeId);
//                                reviewModel.setUser_id(user_id);
//                                reviewModel.setRate((int) ratingBar.getRating());
//
//                                if (ratingBar.getRating() == 0) {
//
//                                    Toasty.error(getActiviy(), R.string.please_fill_rate, Toast.LENGTH_SHORT, true).show();
//                                    YoYo.with(Techniques.Shake).playOn(ratingBar);
//                                    ratingBar.requestFocus();
//
//                                } else if (note.getText().toString().isEmpty()) {
//
//                                    note.requestFocus();
//                                    note.setError(getString(R.string.please_fill_comment));
//
//
//                                } else {
//                                    addComment(v, reviewModel);
//                                }
//
//                            }
//
//
//                        }
//
//                    };
//
//                    AddRateDialog.Click cancelClick = new AddRateDialog.Click() {
//                        @Override
//                        public void click() {
//
//                            addCommentDialog.dismiss();
//
//
//                        }
//                    };
//
//                    addCommentDialog = new AddRateDialog(getActiviy(), getString(R.string.add_comment), R.string.ok, R.string.cancel_label, okClick, cancelClick);
//                    addCommentDialog.show();
//
//                }
//            }
//
//            return true;
//        });
//
//
//        binding.cartBut.setOnClickListener(view1 -> {
//
//
//            if (!UtilityApp.isLogin()) {
//
//                CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_cart, R.string.ok, R.string.cancel_label, null, null);
//                checkLoginDialog.show();
//
//            } else {
//                String message;
//
//
//                int cart_id = selectedProductBarcode.getCartId();
//                if (cart_id > 0) {
//                    int count = Integer.parseInt(binding.productCartQTY.getText().toString());
//                    int stock = selectedProductBarcode.getStockQty();
//                    int userId = UtilityApp.getUserData().getId();
//                    int storeId = Integer.parseInt(localModel.getCityId());
//                    int productId = productModel.getId();
//                    int cartId = selectedProductBarcode.getCartId();
//                    int limit = selectedProductBarcode.getLimitQty();
//                    Log.i("limit", "Log limit  " + limit);
//                    Log.i("limit", "Log limit  " + limit);
//                    Log.i("stock", "Log cartId  " + cartId);
//
//                    if (limit == 0) {
//                        if (count + 1 <= stock) {
//                            updateCart(view1, productId, selectedProductBarcode.getId(), count + 1, userId, storeId, cartId, "quantity");
//                        } else {
//                            message = getString(R.string.stock_empty);
//                            GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error), message);
//                        }
//                    } else {
//
//                        if (count + 1 <= stock && (count + 1) <= limit) {
//                            updateCart(view1, productId, selectedProductBarcode.getId(), count + 1, userId, storeId, cartId, "quantity");
//                        } else {
//                            if (count + 1 > stock) {
//                                message = getString(R.string.stock_empty);
//                            } else if (stock == 0) {
//                                message = getString(R.string.stock_empty);
//                            } else {
//                                message = getString(R.string.limit) + "" + limit;
//                            }
//                            GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error), message);
//                        }
//
//                    }
//                } else {
//                    int count = selectedProductBarcode.getCartQuantity();
//                    if (UtilityApp.getUserData() != null && UtilityApp.getUserData().getId() != null) {
//                        int userId = UtilityApp.getUserData().getId();
//
//                        int storeId = Integer.parseInt(localModel.getCityId());
//                        int productId = productModel.getId();
//                        int stock = selectedProductBarcode.getStockQty();
//                        int limit = selectedProductBarcode.getLimitQty();
//
//                        Log.i("limit", "Log limit  " + limit);
//                        Log.i("stock", "Log stock  " + stock);
//
//
//                        if (limit == 0) {
//                            if (count + 1 <= stock) {
//                                addToCart(productId, selectedProductBarcode.getId(), count + 1, userId, storeId);
//                            } else {
//                                message = getString(R.string.stock_empty);
//                                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error), message);
//                            }
//                        } else {
//                            if (count + 1 <= stock && (count + 1) <= limit) {
//                                addToCart(productId, selectedProductBarcode.getId(), count + 1, userId, storeId);
//                            } else {
//                                if (count + 1 > stock) {
//                                    message = getString(R.string.stock_empty);
//                                } else {
//                                    message = getString(R.string.limit) + "" + limit;
//                                }
//                                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error), message);
//                            }
//
//                        }
//                    }
//
//
//                }
//
//
//            }
//
//
//        });
//
//        binding.plusCartBtn.setOnClickListener(v -> {
//
//            if (!UtilityApp.isLogin()) {
//
//                CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_cart, R.string.ok, R.string.cancel_label, null, null);
//                checkLoginDialog.show();
//
//            } else {
//                if (UtilityApp.getUserData() != null && UtilityApp.getUserData().getId() != null) {
//                    String message = "";
//                    int count = Integer.parseInt(binding.productCartQTY.getText().toString());
//                    int stock = selectedProductBarcode.getStockQty();
//                    int userId = UtilityApp.getUserData().getId();
//                    int storeId = Integer.parseInt(localModel.getCityId());
//                    int productId = productModel.getId();
//                    int cartId = selectedProductBarcode.getCartId();
//                    int limit = selectedProductBarcode.getLimitQty();
//                    Log.i("limit", "Log limit  " + limit);
//                    Log.i("limit", "Log limit  " + limit);
//                    Log.i("stock", "Log cartId  " + cartId);
//
//                    if (cartId > 0) {
//                        // increase cart quantity
//                        if (limit == 0) {
//                            if (count + 1 <= stock) {
//                                updateCart(v, productId, selectedProductBarcode.getId(), count + 1, userId, storeId, cartId, "quantity");
//
//                            } else {
//                                message = getString(R.string.stock_empty);
//                                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                                        message);
//                            }
//                        } else {
//
//                            if (count + 1 <= stock && (count + 1) <= limit) {
//                                updateCart(v, productId, selectedProductBarcode.getId(), count + 1, userId, storeId, cartId, "quantity");
//
//                            } else {
//
//                                if (count + 1 > stock) {
//                                    message = getString(R.string.stock_empty);
//
//                                } else if (stock == 0) {
//                                    message = getString(R.string.stock_empty);
//
//                                } else {
//                                    message = getString(R.string.limit) + "" + limit;
//
//                                }
//                                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                                        message);
//                            }
//
//                        }
//                    } else {
//                        // add product to cart for first time
//
//                        checkProductToAdd();
//                    }
//                }
//
//
//            }
//
//
//        });
//
//        binding.minusCartBtn.setOnClickListener(v -> {
//
//            if (!UtilityApp.isLogin()) {
//
//                CheckLoginDialog checkLoginDialog = new CheckLoginDialog(getActiviy(), R.string.LoginFirst, R.string.to_add_cart, R.string.ok, R.string.cancel_label, null, null);
//                checkLoginDialog.show();
//
//            } else {
//                int count = Integer.parseInt(binding.productCartQTY.getText().toString());
//                int userId = UtilityApp.getUserData().getId();
//                int storeId = Integer.parseInt(localModel.getCityId());
//                int productId = productModel.getId();
//                int cart_id = selectedProductBarcode.getCartId();
//
//                updateCart(v, productId, selectedProductBarcode.getId(), count - 1, userId, storeId, cart_id, "quantity");
//
//            }
//
//
//        });
//
//        binding.deleteCartBtn.setOnClickListener(v -> {
//
//            int userId = UtilityApp.getUserData().getId();
//            int storeId = Integer.parseInt(localModel.getCityId());
//            int productId = productModel.getId();
//            int cart_id = selectedProductBarcode.getCartId();
//
//            deleteCart(v, productId, selectedProductBarcode.getId(), cart_id, userId, storeId);
//
//        });
//    }
//
//    private void getIntentExtra() {
//
//        Bundle bundle = getIntent().getExtras();
//
//        if(bundle!=null){
//            isNotify = bundle.getBoolean(Constants.isNotify, false);
//
//            FROM_BROSHER = bundle.getBoolean(Constants.FROM_BROSHER);
//
//            if (FROM_BROSHER) {
//                product_id = Integer.parseInt(bundle.getString(Constants.product_id));
//
//
//            } else {
//                ProductModel productModel = (ProductModel) bundle.getSerializable(Constants.DB_productModel);
//                if (productModel != null) {
//                    product_id = productModel.getId();
//
//
//                    if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
//                        productName = productModel.gethName();
//
//                    } else {
//                        productName = productModel.getName();
//
//                    }
//
//                    binding.productNameTv.setText(productName);
//
//                }
//            }
//        }
//
//
//
//        getSingleProduct(country_id, city_id, product_id, String.valueOf(user_id));
//
//
//    }
//
//    private void checkProductToAdd() {
//        String message = "";
//        int count = selectedProductBarcode.getCartQuantity();
//        int userId = UtilityApp.getUserData().getId();
//        int storeId = Integer.parseInt(localModel.getCityId());
//        int productId = productModel.getId();
//        int stock = selectedProductBarcode.getStockQty();
//        int limit = selectedProductBarcode.getLimitQty();
//
//        Log.i("limit", "Log limit  " + limit);
//        Log.i("stock", "Log stock  " + stock);
//
//
//        if (limit == 0) {
//
//            if (count + 1 <= stock) {
//                addToCart(productId, selectedProductBarcode.getId(), count + 1, userId, storeId);
//
//            } else {
//                message = getString(R.string.stock_empty);
//                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                        message);
//            }
//        } else {
//
//            if (count + 1 <= stock && (count + 1) <= limit) {
//                addToCart(productId, selectedProductBarcode.getId(), count + 1, userId, storeId);
//
//            } else {
//
//                if (count + 1 > stock) {
//                    message = getString(R.string.stock_empty);
//                } else {
//                    message = getString(R.string.limit) + "" + limit;
//
//                }
//                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                        message);
//            }
//
//        }
//    }
//
//    public void initReviewAdapter() {
//        reviewAdapter = new ReviewAdapter(getActiviy(), reviewList);
//        binding.reviewRecycler.setAdapter(reviewAdapter);
//        reviewAdapter.notifyDataSetChanged();
//    }
//
//
//    @Override
//    public void onItemClicked(int position, ProductModel productModel) {
//
//    }
//
//    public void getSingleProduct(int country_id, int city_id, int product_id, String user_id) {
//
//        AnalyticsHandler.ViewItem(product_id, currency, 0);
//
//        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
//        binding.dataLY.setVisibility(View.GONE);
//        binding.noDataLY.noDataLY.setVisibility(View.GONE);
//        binding.CartLy.setVisibility(View.GONE);
//        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
//
//        new DataFeacher(false, (obj, func, IsSuccess) -> {
//            ProductDetailsModel result = (ProductDetailsModel) obj;
//            String message = getString(R.string.fail_to_get_data);
//
//            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
//
//            if (func.equals(Constants.ERROR)) {
//
//                if (result != null) {
//                    message = result.getMessage();
//                }
//                binding.dataLY.setVisibility(View.GONE);
//                binding.cartBut.setVisibility(View.VISIBLE);
//                binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                binding.CartLy.setVisibility(View.GONE);
//
//                binding.failGetDataLY.failTxt.setText(message);
//
//            } else if (func.equals(Constants.FAIL)) {
//
//                binding.dataLY.setVisibility(View.GONE);
//                binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                binding.failGetDataLY.failTxt.setText(message);
//                binding.CartLy.setVisibility(View.GONE);
//                binding.cartBut.setVisibility(View.GONE);
//
//
//            } else if (func.equals(Constants.NO_CONNECTION)) {
//                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
//                binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
//                binding.CartLy.setVisibility(View.GONE);
//
//                binding.dataLY.setVisibility(View.GONE);
//
//            } else {
//                if (IsSuccess) {
//                    if (result.getData() != null && result.getData().size() > 0) {
//                        binding.dataLY.setVisibility(View.VISIBLE);
//                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
//                        productModel = result.getData().get(0);
//                        binding.CartLy.setVisibility(View.VISIBLE);
//                        categoryId=productModel.getCategoryId();
//                        Log.i(getClass().getSimpleName(), "Log getSingleProduct categoryId " + categoryId);
//
//                        if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
//                            productName = productModel.gethName();
//                        } else {
//                            productName = productModel.getName();
//                        }
//
//                        binding.productNameTv.setText(productName);
//
//                        if (productModel.getDescription() != null && productModel.gethDescription() != null) {
//                            if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
//                                binding.productDesc1Tv.setText(Html.fromHtml(productModel.gethDescription()));
//
//                            } else {
//                                binding.productDesc1Tv.setText(Html.fromHtml(productModel.getDescription()));
//
//                            }
//
//
//                        }
//
//
////                        sliderList = productModel.getImages();
//                        String[] list = productName.split(" ");
//                         filter = list[0];
//
//                        Log.i(getClass().getSimpleName(), "Log  productName " + productName);
//                        Log.i(getClass().getSimpleName(), "Log search filter " + filter);
//
//
//                        binding.ratingBar.setRating((float) productModel.getRate());
//
//                        if (productModel.getImages().size() > 0) {
//                            setupViewPager(binding.viewPager);
//                            binding.pager.setViewPager(binding.viewPager);
//                        }
//
////                        optionModelsList.clear();
//                        if (productModel.getProductBarcodes() != null && productModel.getProductBarcodes().size() > 0) {
//                            selectedProductBarcode = productModel.getFirstProductBarcodes();
//
//                            for (int i = 0; i < productModel.getProductBarcodes().size(); i++) {
//                                ProductBarcode productBarcode1 = productModel.getProductBarcodes().get(i);
//
//                                if (selectedProductPos == 0 && productBarcode1.getCartId() != 0) {
//                                    selectedProductBarcode = productBarcode1;
//                                    selectedProductPos = i;
//                                }
//
//                            }
//
//
//                            initProductData();
//
//
//                        }
//
//
//                        if (productModel.getProductBarcodes()!=null && productModel.getProductBarcodes().size() > 0) {
//                            initOptionAdapter();
//                            binding.productOptionTv.setVisibility(View.VISIBLE);
//                            binding.productOptionRv.setVisibility(View.VISIBLE);
//
//                        } else {
//                            binding.productOptionTv.setVisibility(View.GONE);
//                            binding.productOptionRv.setVisibility(View.GONE);
//                        }
//
//                        isFavorite = productModel.isFavourite();
//
//                        if (productModel!= null && isFavorite) {
//
//                            binding.favBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.favorite_icon));
//                        } else {
//                            binding.favBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.empty_fav));
//
//                        }
//
//
//                        getSuggestedProduct();
//
//                        getReviews(product_id, storeId);
//
//
//                    } else {
//
//                        binding.dataLY.setVisibility(View.GONE);
//                        binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
//
//                    }
//
//
//                } else {
//
//                    binding.dataLY.setVisibility(View.GONE);
//                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                    binding.failGetDataLY.failTxt.setText(message);
//                    binding.cartBut.setVisibility(View.GONE);
//
//
//                }
//            }
//
//        }).GetSingleProduct(country_id, city_id, product_id, user_id);
//    }
//
//    private void addToFavorite(View v, int productId, int userId, int storeId) {
//        new DataFeacher(false, (obj, func, IsSuccess) -> {
//            if (func.equals(Constants.ERROR)) {
//
//                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                        getString(R.string.fail_to_add_favorite));
//            } else if (func.equals(Constants.FAIL)) {
//
//                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                        getString(R.string.fail_to_add_favorite));
//            } else {
//                if (IsSuccess) {
//                    AnalyticsHandler.AddToWishList(productId, currency, productId);
//
//                    binding.favBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.favorite_icon));
//
//                    Toasty.success(getActiviy(), R.string.success_add, Toast.LENGTH_SHORT, true).show();
//
//                    isFavorite = true;
//
//                } else {
//                    GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                            getString(R.string.fail_to_add_favorite));
//
//                }
//            }
//
//        }).addToFavoriteHandle(userId, storeId, productId);
//    }
//
//
//    private void removeFromFavorite(View view, int productId, int userId, int storeId) {
//        new DataFeacher(false, (obj, func, IsSuccess) -> {
//            if (func.equals(Constants.ERROR)) {
//                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                        getString(R.string.fail_to_remove_favorite));
//
//            } else if (func.equals(Constants.FAIL)) {
//
//                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                        getString(R.string.fail_to_remove_favorite));
//
//
//            } else {
//                if (IsSuccess) {
//                    binding.favBut.setImageDrawable(ContextCompat.getDrawable(getActiviy(), R.drawable.empty_fav));
//                    isFavorite = false;
//                    Toasty.success(getActiviy(), R.string.success_delete, Toast.LENGTH_SHORT, true).show();
//
//
//                } else {
//                    GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                            getString(R.string.fail_to_remove_favorite));
//
//                }
//            }
//
//        }).deleteFromFavoriteHandle(userId, storeId, productId);
//    }
//
//
//    public void getSuggestedProduct() {
//        productList.clear();
//        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
//        binding.dataLY.setVisibility(View.GONE);
//        binding.noDataLY.noDataLY.setVisibility(View.GONE);
//        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
//
//        new DataFeacher(false, (obj, func, IsSuccess) -> {
//            binding.dataLY.setVisibility(View.VISIBLE);
//
//            FavouriteResultModel result = (FavouriteResultModel) obj;
//            String message = getString(R.string.fail_to_get_data);
//
//            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
//
//            if (func.equals(Constants.ERROR)) {
//
//                if (result != null && result.getMessage() != null) {
//                    message = result.getMessage();
//                }
//                binding.dataLY.setVisibility(View.GONE);
//                binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                binding.failGetDataLY.failTxt.setText(message);
//
//            } else if (func.equals(Constants.FAIL)) {
//
//                binding.dataLY.setVisibility(View.GONE);
//                binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                binding.failGetDataLY.failTxt.setText(message);
//
//
//            } else {
//                if (IsSuccess) {
//                    if (result.getData() != null && result.getData().size() > 0) {
//
//                        binding.dataLY.setVisibility(View.VISIBLE);
//                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
//                        productList = result.getData();
//                        initProductsAdapter();
//
//                    } else {
//                        binding.noOffers.setText(getString(R.string.no_products));
//                        binding.noOffers.setVisibility(View.VISIBLE);
//
//                    }
//
//
//                }
//            }
//
//        }).getFavorite(categoryId, country_id, city_id, String.valueOf(user_id), filter, 0, 0, 12);;
//    }
//
//    private void initProductsAdapter() {
//        productOfferAdapter = new SuggestedProductAdapter(getActiviy(), productList, this, 0);
//        binding.offerRecycler.setAdapter(productOfferAdapter);
//
//    }
//
//    private void initOptionAdapter() {
//        ProductOptionAdapter productOptionAdapter = new ProductOptionAdapter(getActiviy(), productModel.getProductBarcodes(), (obj, func, IsSuccess) -> {
//            selectedProductBarcode = (ProductBarcode) obj;
//            initProductData();
//
//
//        });
//        binding.productOptionRv.setAdapter(productOptionAdapter);
//
//    }
//
//    /*
//       private void addToCart(int productId, int product_barcode_id, int quantity, int userId, int storeId) {
//        if(quantity>0){
//
//        }
//          else {
//            Toast(getString(R.string.quanity_wrong));
//        }
//     */
//
//    private void addToCart(int productId, int product_barcode_id, int quantity, int userId, int storeId) {
//        if (quantity > 0) {
//            new DataFeacher(false, (obj, func, IsSuccess) -> {
//                CartProcessModel result = (CartProcessModel) obj;
//
//                if (IsSuccess) {
//                    int cartId = result.getId();
//
//                    selectedProductBarcode.setCartId(cartId);
//                    selectedProductBarcode.setCartQuantity(quantity);
//                    productModel.getProductBarcodes().set(selectedProductPos, selectedProductBarcode);
//
//                    AnalyticsHandler.AddToCart(cartId, currency, quantity);
//
//                    binding.productCartQTY.setText(String.valueOf(quantity));
//
//                    if (quantity == 1) {
//                        binding.deleteCartBtn.setVisibility(View.VISIBLE);
//                        binding.minusCartBtn.setVisibility(View.GONE);
//                    } else {
//                        binding.minusCartBtn.setVisibility(View.VISIBLE);
//                        binding.deleteCartBtn.setVisibility(View.GONE);
//                    }
//
//                    UtilityApp.updateCart(1, productList.size());
//
//
//                } else {
//
//
//                    GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                            getString(R.string.fail_to_add_cart));
//
//                }
//
//
//            }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId);
//
//        } else {
//            Toast(getString(R.string.quanity_wrong));
//        }
//    }
//
//    private void initSnackBar(String message, View viewBar) {
//        Toasty.success(getActiviy(), message, Toast.LENGTH_SHORT, true).show();
//
//    }
//
//    private void deleteCart(View v, int productId, int product_barcode_id, int cart_id, int userId, int storeId) {
//
//        new DataFeacher(false, (obj, func, IsSuccess) -> {
//
//            if (IsSuccess) {
//                initSnackBar(getString(R.string.success_delete_from_cart), v);
//                UtilityApp.updateCart(1, productList.size());
//                selectedProductBarcode.setCartQuantity(0);
//                selectedProductBarcode.setCartId(0);
//                productModel.getProductBarcodes().set(selectedProductPos, selectedProductBarcode);
////                int quantity = selectedProductBarcode.getCartQuantity();
//                AnalyticsHandler.RemoveFromCart(cart_id, currency, 0);
//
//                binding.productCartQTY.setText(String.valueOf(1));
//                binding.deleteCartBtn.setVisibility(View.GONE);
//                binding.minusCartBtn.setVisibility(View.VISIBLE);
//                binding.plusCartBtn.setVisibility(View.VISIBLE);
//
//
//            } else {
//                GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                        getString(R.string.fail_to_delete_cart));
//
//            }
//
//
//        }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId);
//    }
//
//    private void updateCart(View v, int productId, int product_barcode_id, int quantity, int userId, int storeId, int cart_id, String update_quantity) {
//        if (quantity > 0) {
//            new DataFeacher(false, (obj, func, IsSuccess) -> {
//                if (IsSuccess) {
//
//                    binding.productCartQTY.setText(String.valueOf(quantity));
//
//                    // initSnackBar(getString(R.string.success_to_update_cart), v);
//                    selectedProductBarcode.setCartQuantity(quantity);
//                    productModel.getProductBarcodes().set(selectedProductPos, selectedProductBarcode);
//
//                    if (quantity > 0) {
//                        binding.productCartQTY.setText(String.valueOf(quantity));
//
//                        if (quantity == 1) {
//                            binding.deleteCartBtn.setVisibility(View.VISIBLE);
//                            binding.minusCartBtn.setVisibility(View.GONE);
//                        } else {
//                            binding.minusCartBtn.setVisibility(View.VISIBLE);
//                            binding.deleteCartBtn.setVisibility(View.GONE);
//                        }
//
//                    }
//
//
//                } else {
//                    GlobalData.errorDialogWithButton(getActiviy(), getString(R.string.error),
//                            getString(R.string.fail_to_update_cart));
//
//
//                }
//
//            }).updateCartHandle(productId, product_barcode_id, quantity, userId, storeId, cart_id, update_quantity);
//
//        } else {
//            Toast(getString(R.string.quanity_wrong));
//        }
//    }
//
//
////    @Subscribe(threadMode = ThreadMode.MAIN)
////    public void onMessageEvent(@NotNull MessageEvent event) {
////
////
////        if (event.type.equals(MessageEvent.TYPE_main)) {
////            binding.backBtn.setOnClickListener(view -> {
////                Intent intent = new Intent(getActiviy(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
////                startActivity(intent);
////            });
////
////        }
////
////
////    }
//
//    public void getReviews(int product_id, int storeId) {
//        reviewList.clear();
//
//        new DataFeacher(false, (obj, func, IsSuccess) -> {
//            ResultAPIModel<ArrayList<ReviewModel>> result =
//                    (ResultAPIModel<ArrayList<ReviewModel>>) obj;
//
//            if (IsSuccess) {
//                if (result.data != null && result.data.size() > 0) {
//
//
//                    reviewList = result.data;
//                    binding.productReviewTv.setVisibility(View.VISIBLE);
//                    binding.reviewRecycler.setVisibility(View.VISIBLE);
//                    initReviewAdapter();
//
//
//                } else {
//
//                    binding.productReviewTv.setVisibility(View.GONE);
//                    binding.reviewRecycler.setVisibility(View.GONE);
//
//                }
//
//
//            }
//
//
//        }).getRate(product_id, storeId);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        Bundle bundle = intent.getExtras();
//
//        if (bundle != null) {
//
//            ProductModel productModel = (ProductModel) bundle.getSerializable(Constants.DB_productModel);
//            FROM_BROSHER = bundle.getBoolean(Constants.FROM_BROSHER);
//            isNotify = bundle.getBoolean(Constants.isNotify, false);
//
//            if (FROM_BROSHER) {
//                product_id = Integer.parseInt(bundle.getString(Constants.product_id));
//
//
//            } else {
//                if (productModel != null) {
//
//                    product_id = productModel.getId();
//
//                    if (UtilityApp.getLanguage().equals(Constants.Arabic)) {
//                        productName = productModel.gethName();
//
//                    } else {
//                        productName = productModel.getName();
//
//                    }
//                }
//
//            }
//
//
//            getSingleProduct(country_id, city_id, product_id, String.valueOf(user_id));
//
//            binding.productNameTv.setText(productName);
//
//
//        }
//
//    }
//
//
//    private void addComment(View v, ReviewModel reviewModel) {
//        GlobalData.progressDialog(getActiviy(), R.string.add_comm, R.string.please_wait_sending);
//        new DataFeacher(false, (obj, func, IsSuccess) -> {
//
//
//            String message = getString(R.string.fail_add_comment);
//
//            ResultAPIModel<ReviewModel> result = (ResultAPIModel<ReviewModel>) obj;
//
//            if (result != null) {
//                message = result.message;
//            }
//
//
//            if (func.equals(Constants.ERROR)) {
//
//                GlobalData.errorDialog(getActiviy(), R.string.rate_app, message);
//
//
//            } else if (func.equals(Constants.FAIL)) {
//                GlobalData.errorDialog(getActiviy(), R.string.rate_app, message);
//
//
//            } else if (func.equals(Constants.NO_CONNECTION)) {
//                GlobalData.errorDialog(getActiviy(), R.string.rate_app, getString(R.string.no_internet_connection));
//
//            } else {
//
//                if (IsSuccess) {
//
//                    addCommentDialog.dismiss();
//                    GlobalData.hideProgressDialog();
//                    getReviews(product_id, storeId);
//                    GlobalData.successDialog(getActiviy(), getString(R.string.rate_product), getString(R.string.success_rate_product));
//
//
//                } else {
//                    addCommentDialog.dismiss();
//                    GlobalData.hideProgressDialog();
//                    GlobalData.errorDialog(getActiviy(), R.string.rate_product, getString(R.string.fail_add_comment));
//
//                }
//
//            }
//
//
//        }).setRate(reviewModel);
//    }
//
//    private void initProductData() {
//
//        int quantity = selectedProductBarcode.getCartQuantity();
//
//        if (quantity > 0) {
//            binding.productCartQTY.setText(String.valueOf(quantity));
//
//            if (quantity == 1) {
//                binding.deleteCartBtn.setVisibility(View.VISIBLE);
//                binding.minusCartBtn.setVisibility(View.GONE);
//            } else {
//                binding.minusCartBtn.setVisibility(View.VISIBLE);
//                binding.deleteCartBtn.setVisibility(View.GONE);
//            }
//
//        } else {
//            binding.productCartQTY.setText(String.valueOf(1));
//
//        }
//
//        if (selectedProductBarcode.isSpecial()) {
//            binding.productPriceBeforeTv.setBackground(ContextCompat.getDrawable(getActiviy(), R.drawable.itlatic_red_line));
//
////            if (selectedProductBarcode.getSpecialPrice() != null) {
//                binding.productPriceBeforeTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(selectedProductBarcode.getPrice())), fraction) + " " + currency);
//                binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(selectedProductBarcode.getSpecialPrice())), fraction) + " " + currency);
//
//                Double discount = (Double.parseDouble(String.valueOf(selectedProductBarcode.getPrice()))
//                        - Double.parseDouble(String.valueOf(selectedProductBarcode.getSpecialPrice())))
//                        / (Double.parseDouble(String.valueOf(selectedProductBarcode.getPrice()))) * 100;
//                DecimalFormat df = new DecimalFormat("#");
//                String newDiscount_str = df.format(discount);
//                binding.discountTv.setText(NumberHandler.arabicToDecimal(newDiscount_str) + " % " + "OFF");
//
//                // this is different by seconds
//                long diff = DateHandler.GetDateOnlyLong(selectedProductBarcode.getEndOffer()) - DateHandler.GetDateOnlyLong(DateHandler.GetDateNowString());
//                System.out.println("Log diff " + diff);
//                int day = (int) (diff / (24 * 60 * 60));
//                diff = diff % (24 * 60 * 60);
//                System.out.println("Log day " + day);
//                System.out.println("Log diff " + diff);
//                int hour = (int) (diff / (60 * 60));
//                diff = diff % (60 * 60);
//                System.out.println("Log hour " + hour);
//                System.out.println("Log diff " + diff);
//                int minutes = (int) (diff / 60);
//                System.out.println("Log minutes " + minutes);
//
//                String formatedOfferTime = "";
//                if (day > 0)
//                    formatedOfferTime += day + " " + getString(R.string.day) + ",";
//                if (hour > 0)
//                    formatedOfferTime += hour + " " + getString(R.string.hour) + ",";
//                if (minutes > 0)
//                    formatedOfferTime += minutes + " " + getString(R.string.minute);
//                if (formatedOfferTime.endsWith(","))
//                    formatedOfferTime = formatedOfferTime.substring(0, formatedOfferTime.length() - 1);
//                binding.endOfferTv.setText(formatedOfferTime);
//
////            }
//
//        } else {
//            binding.productPriceTv.setText(NumberHandler.formatDouble(Double.parseDouble(String.valueOf(selectedProductBarcode.getPrice())), fraction) + " " + currency + "");
//            binding.productPriceBeforeTv.setVisibility(View.GONE);
//            binding.offerLy.setVisibility(View.GONE);
//
//        }
//
//
//    }
//
//
//    private void setupViewPager(ViewPager viewPager) {
//        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//
////        add list of photos fragments
//
//        for (String image : productModel.getImages()) {
//            Bundle imageBundle = new Bundle();
//            imageBundle.putString(Constants.KEY_IMAGE_URL, image);
//            Fragment imageFragment = new ImageFragment();
//            imageFragment.setArguments(imageBundle);
//            adapter.addFragment(imageFragment, "");
//
//        }
//
//        String[] list = productName.split(" ");
//        String filter = list[0];
//
//        // add suggestions fragment
//
//        Fragment suggestedProductsFragment = new SuggestedProductsFragment();
//        Bundle suggestedBundle = new Bundle();
//        suggestedBundle.putString(Constants.KEY_FILTER, filter);
//        suggestedProductsFragment.setArguments(suggestedBundle);
//        adapter.addFragment(suggestedProductsFragment, "");
//
//        viewPager.setAdapter(adapter);
//
//    }
//
//    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
//        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<String> mFragmentTitleList = new ArrayList<>();
//
//        public ViewPagerAdapter(FragmentManager manager) {
//            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            return mFragmentList.get(position);
//        }
//
//        @Override
//        public int getCount() {
//            return mFragmentList.size();
//        }
//
//        public void addFragment(Fragment fragment, String title) {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return mFragmentTitleList.get(position);
//        }
//    }
//
//
//}