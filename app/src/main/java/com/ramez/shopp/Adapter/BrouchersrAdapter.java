package com.ramez.shopp.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ramez.shopp.Activities.ProductDetailsActivity;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.OnLoadMoreListener;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.BrousherDialog;
import com.ramez.shopp.Models.BrochuresModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.Product;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowLoadingBinding;
import com.ramez.shopp.databinding.RowShopBroshurBinding;

import java.util.ArrayList;
import java.util.List;


public class BrouchersrAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_LOADING = 0;
    public boolean isLoading;
    public int visibleThreshold = 5;
    public boolean show_loading = true;
    double image_width, image_height;
    int shopID;
    ProgressDialog progressDialog;
    private Context context;
    private List<BrochuresModel> brousherDm;
    private onBroucherClick onItemClick;
    private int lastVisibleItem;
    private int totalItemCount;
    private int nextPage = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private int booklet_id;
    private BrousherDialog brousherDialog;

    private int oldPosition = -1, imageViewWidth, imageViewHeight;
    private int selectedPosition = -1;
    private boolean toggleShowProducts = false;
    private List<Product> products = null;
    private LocalModel localModel;

    public BrouchersrAdapter(Context context, List<BrochuresModel> broushserDm, int booklet_id, RecyclerView rv, onBroucherClick onItemClick) {
        this.context = context;
        this.brousherDm = broushserDm;
        this.onItemClick = onItemClick;
        this.booklet_id = booklet_id;
        localModel = UtilityApp.getLocalData();

        initProgress();
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);


//        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                totalItemCount = linearLayoutManager.getItemCount();
//                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//
//
//                if (show_loading) {
//                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
//                        if (mOnLoadMoreListener != null) {
//                            mOnLoadMoreListener.onLoadMore();
//                            isLoading = true;
//                        }
//                    }
//                }
//                setOnloadListener();
//
//            }
//
//        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_TYPE_ITEM) {
            RowShopBroshurBinding itemView = RowShopBroshurBinding.inflate(LayoutInflater.from(context), parent, false);
            vh = new Holder(itemView);
        } else if (viewType == VIEW_TYPE_LOADING) {

            RowLoadingBinding itemView = RowLoadingBinding.inflate(LayoutInflater.from(context), parent, false);
            vh = new LoadingViewHolder(itemView);

        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof Holder) {
            Holder holder = (Holder) viewHolder;
            BrochuresModel brochuresModel = brousherDm.get(position);
            Log.d("getBooklet_image", "" + brochuresModel.getImage());


            Glide.with(context)
                    .asBitmap()
                    .load(brochuresModel.getImage())
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            holder.binding.loadingLY.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            holder.binding.loadingLY.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(holder.binding.ivCatImage);

//            boolean hasDrawable = (holder.binding.ivCatImage.getDrawable() != null);
//            if (hasDrawable) holder.binding.loadingLY.setVisibility(View.GONE);
//            else holder.binding.loadingLY.setVisibility(View.VISIBLE);

            if (selectedPosition == position && products != null && products.size() > 0 && toggleShowProducts) {

                holder.binding.brContainer.setVisibility(View.VISIBLE);
                holder.binding.transLy.setVisibility(View.VISIBLE);
                holder.binding.touchImg.setVisibility(View.VISIBLE);
                holder.binding.touchHintTv.setVisibility(View.VISIBLE);


                double widthPercent = image_width / imageViewWidth;
                double heightPercent = image_height / imageViewHeight;

                holder.binding.brContainer.removeAllViews();
                for (int i = 0; i < products.size(); i++) {
                    Product productsBean = products.get(i);
                    String x_axis = String.valueOf(products.get(i).getxAxis());
                    String y_axis = String.valueOf(products.get(i).getyAxis());
                    if (!x_axis.isEmpty() && !y_axis.isEmpty()) {
                        double new_x_axis = Math.round(Double.parseDouble(x_axis) / widthPercent);
                        double new_y_axis = Math.round(Double.parseDouble(y_axis) / heightPercent);
                        addProducts(holder.binding, productsBean, new_x_axis, new_y_axis);

                    }


                }


            } else {
                holder.binding.brContainer.setVisibility(View.GONE);
                holder.binding.transLy.setVisibility(View.GONE);
                holder.binding.touchImg.setVisibility(View.GONE);
                holder.binding.touchHintTv.setVisibility(View.GONE);
            }

        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.rowLoadingBinding.progressBar1.setIndeterminate(true);


        }
    }

    @Override
    public int getItemCount() {
        return brousherDm.size();
    }

    @Override
    public int getItemViewType(int position) {
        return brousherDm.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void setLoaded() {
        isLoading = false;
    }

    private void setOnloadListener() {

        setOnLoadMoreListener(() -> {
            System.out.println("Log add loading item");
            if (!brousherDm.contains(null)) {
                brousherDm.add(null);
                notifyItemInserted(brousherDm.size() - 1);
                // LoadData(booklet_id, lang);
            }

        });

    }

    private BrouchersrAdapter getAdapter() {
        return this;
    }


//    public void LoadData(int booklet_id, String lang) {
//        System.out.println("Log LoadAllData page " + nextPage);
//
//        AndroidNetworking.get(Urls.GetBrouchers).setTag("test").setPriority(Priority.LOW).
//                addQueryParameter("booklet_id", String.valueOf(booklet_id)).addQueryParameter("page_no", String.valueOf(nextPage)).addHeaders("lang", lang).build().getAsObject(BrouchersModel.class, new ParsedRequestListener<BrouchersModel>() {
//            @Override
//            public void onResponse(BrouchersModel brouchersModel) {
//                Log.d(TAG, "Get Brouchers " + brouchersModel.getData());
//                int status = brouchersModel.getStatus();
//                String message = brouchersModel.getMessage();
//                if (status == 1) {
//
//                    brousherDm.remove(brousherDm.size() - 1);
//                    notifyItemRemoved(brousherDm.size());
//
//                    int pos = brousherDm.size();
//                    if (brouchersModel.getData() != null && brouchersModel.getData().size() > 0) {
//                        brousherDm.addAll(brouchersModel.getData());
//                        notifyItemRangeInserted(pos, brousherDm.size());
//                        nextPage++;
//                        setLoaded();
//                    } else {
//                        setLoaded();
//                        show_loading = false;
//                    }
//                }
//
//
//            }
//
//            @Override
//            public void onError(ANError anError) {
//
//                brousherDm.remove(brousherDm.size() - 1);
//                notifyItemRemoved(brousherDm.size());
//                setLoaded();
//                anError.printStackTrace();
//            }
//
//
//        });
//
//
//    }


    public void getSingleBroucher(int store_id, int booklet_id, int position) {
        Log.i("TAG", "Log store_ID " + store_id);
        Log.i("TAG", "Log booklet_id " + booklet_id);
        Log.i("TAG", "Log position " + position);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ArrayList<BrochuresModel>> result = (ResultAPIModel<ArrayList<BrochuresModel>>) obj;

            if (IsSuccess) {
                if (result.data != null && result.data.size() > 0) {

                    products = result.data.get(position).getProducts();
                    if (products != null && products.size() > 0) {
                        Log.d("TAG", "Log getSingle Bro " + products.size());

                        image_width = Double.parseDouble(String.valueOf(result.data.get(position).getImageWidth()));
                        image_height = Double.parseDouble(String.valueOf((result.data.get(position).getImageHeight())));

                        if (oldPosition != -1) notifyItemChanged(oldPosition);

                        notifyItemChanged(selectedPosition);


                    }

                }


            }

        }).getBrochuresList(store_id, booklet_id);
    }


    public void addProducts(RowShopBroshurBinding view, Product productsBean, double new_x_axis, double new_y_axis) {

        View v; // Creating an instance for View Object
        RelativeLayout rl = view.brContainer;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.add_products_view, null);
        v.setId(productsBean.getProductId());

        int buttonSize = (int) context.getResources().getDimension(R.dimen._30sdp);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(buttonSize, buttonSize);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.leftMargin = (int) new_x_axis;
        params.topMargin = (int) new_y_axis;

        rl.addView(v, params);

        v.setOnClickListener(view1 -> {

            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra(Constants.product_id, String.valueOf(productsBean.getProductId()));
            intent.putExtra(Constants.FROM_BROSHER, true);
            context.startActivity(intent);
        });

    }

    private void initProgress() {
        brousherDialog = new BrousherDialog((Activity) context, null);

    }

    private void showDailog() {
        brousherDialog.show();
    }

    private void hideDailog() {

        brousherDialog.hide();
    }


    public interface onBroucherClick {
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        RowLoadingBinding rowLoadingBinding;

        LoadingViewHolder(RowLoadingBinding view) {
            super(view.getRoot());
            rowLoadingBinding = view;


        }

        public ProgressBar getProgressBar() {
            return rowLoadingBinding.progressBar1;
        }

        public void setProgressBar(ProgressBar var1) {
            var1 = rowLoadingBinding.progressBar1;

        }

    }

    class Holder extends RecyclerView.ViewHolder {

        RowShopBroshurBinding binding;

        Holder(RowShopBroshurBinding view) {
            super(view.getRoot());
            binding = view;

            view.rowLY.setOnClickListener(view1 -> {
                int position = getAdapterPosition();

                imageViewWidth = binding.ivCatImage.getWidth();
                imageViewHeight = binding.ivCatImage.getHeight();

                toggleShowProducts = !toggleShowProducts;
                if (selectedPosition != -1 && selectedPosition != position) {
                    oldPosition = selectedPosition;
                    products = null;
                    toggleShowProducts = true;
                    binding.brContainer.removeAllViews();
                }
                selectedPosition = position;
                if (products == null) {

                    products = brousherDm.get(getAdapterPosition()).getProducts();
                    if (products != null && products.size() > 0) {


                        image_width = Double.parseDouble(String.valueOf(brousherDm.get(getAdapterPosition()).getImageWidth()));
                        image_height = Double.parseDouble(String.valueOf((brousherDm.get(getAdapterPosition()).getImageHeight())));

                        if (oldPosition != -1) notifyItemChanged(oldPosition);

                        notifyItemChanged(selectedPosition);


                    }

                    //    getSingleBroucher(Integer.parseInt(localModel.getCityId()),booklet_id, position);

                } else {
                    notifyItemChanged(selectedPosition);
                }
            });
        }
    }

}