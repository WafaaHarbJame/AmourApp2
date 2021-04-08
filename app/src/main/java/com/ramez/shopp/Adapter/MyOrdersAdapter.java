package com.ramez.shopp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Activities.InvoiceInfoActivity;
import com.ramez.shopp.Activities.RatingActivity;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.OnLoadMoreListener;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.OrderModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.DateHandler;
import com.ramez.shopp.databinding.RowCurrentMyOrderItemBinding;
import com.ramez.shopp.databinding.RowLoadingBinding;

import java.util.List;


public class MyOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_LOADING = 3;
    private static final String TAG = "MyOrdersAdapter";
    public boolean isLoading;
    public int nextPage = 1;
    public boolean show_loading = true;
    public int visibleThreshold = 5;
    public Context context;
    String lang;
    private List<OrderModel> list;
    private int lastVisibleItem;
    private int totalItemCount;
    private int userId;
    private OnLoadMoreListener mOnLoadMoreListener;


    public MyOrdersAdapter(Context context, RecyclerView rv, List<OrderModel> ordersDMS, int userId) {
        this.context = context;
        this.list = ordersDMS;
        this.userId = userId;


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

        rv.setLayoutManager(linearLayoutManager);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

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

            }

        });

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        if (viewType == VIEW_TYPE_ITEM) {
            RowCurrentMyOrderItemBinding itemView = RowCurrentMyOrderItemBinding.inflate(LayoutInflater.from(context), parent, false);
            vh = new ItemHolder(itemView);
        } else if (viewType == VIEW_TYPE_LOADING) {
            RowLoadingBinding itemView = RowLoadingBinding.inflate(LayoutInflater.from(context), parent, false);
            vh = new LoadingViewHolder(itemView);

        }
        return vh;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (UtilityApp.getLanguage() == null) {
            lang = Constants.English;
        } else {
            lang = UtilityApp.getLanguage();

        }
        if (viewHolder instanceof ItemHolder) {
            ItemHolder holder = (ItemHolder) viewHolder;
            final OrderModel ordersDM = list.get(position);

            holder.binding.tvInvID.setText(ordersDM.getOrderCode());
            holder.binding.tvShopName.setText(R.string.app_name);

            //delivery_status = PN:Pending || RC:Received || IP:Processing || CA:CheckoutArea ||
            // PP:PendingPayment || OH:OnHold || OP:Open  || CM:Complete  || CL:Canceled  || DV:Delivered || Not Defined || CheckoutArea


            if (ordersDM.getDeliveryStatus().equals("Processing") || ordersDM.getDeliveryStatus().equals("Received") || ordersDM.getDeliveryStatus().equals("Pending") || ordersDM.getDeliveryStatus().equals("Open") || ordersDM.getDeliveryStatus().equals("CheckoutArea") || ordersDM.getDeliveryStatus().equals("Checkout Area") || ordersDM.getDeliveryStatus().equals("PendingPayment") || ordersDM.getDeliveryStatus().equals("OnHold") || ordersDM.getDeliveryStatus().equals("Not Defined")) {

                holder.binding.completeOrderLy.setVisibility(View.GONE);
                holder.binding.currentLY.setVisibility(View.VISIBLE);
                holder.binding.currentLY1.setVisibility(View.VISIBLE);

            } else {
                holder.binding.completeOrderLy.setVisibility(View.VISIBLE);
                holder.binding.currentLY.setVisibility(View.GONE);
                holder.binding.currentLY1.setVisibility(View.GONE);

            }

            holder.binding.noteCTv.setText(ordersDM.getDeliveryStatus());


            String orderDayTime = (DateHandler.FormatDate4(ordersDM.getCreatedAt(), "yyyy-MM-dd'T'HH:mm:ss", "hh:mm aa", lang));

            holder.binding.tvOrderTime.setText(orderDayTime);

            String OrderDayName = (DateHandler.FormatDate4(ordersDM.getCreatedAt(), "yyyy-MM-dd'T'HH:mm:ss", "EEEE", lang));

            String deliveryDayName = (DateHandler.FormatDate4(ordersDM.getDeliveryDate(), "yyyy-MM-dd", "EEEE", lang));

            holder.binding.TvDeliveryDay.setText(deliveryDayName);
            holder.binding.tvOrderDay.setText(OrderDayName);


            holder.binding.TvDeliveryTime.setText(ordersDM.getDeliveryTime());

            Log.i("tag", "Log getDeliveryStatus " + ordersDM.getDeliveryStatus());

            if (ordersDM.getDeliveryStatus().equals("Not Defined") || ordersDM.getDeliveryStatus().equals("Pending") || ordersDM.getDeliveryStatus().equals("Received")) {

                holder.binding.doneImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));
                holder.binding.deliveryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_not_choose));
                holder.binding.processImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_not_choose));
                holder.binding.doneDeliveryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_not_choose));


            } else if (ordersDM.getDeliveryStatus().equals("Processing") || ordersDM.getDeliveryStatus().equals("Checkout Area") ||
                    ordersDM.getDeliveryStatus().equals("On Hold") || ordersDM.getDeliveryStatus().equals("Open")) {
                holder.binding.doneImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));
                holder.binding.processImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));
                holder.binding.deliveryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_not_choose));
                holder.binding.doneDeliveryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_not_choose));



            } else if (ordersDM.getDeliveryStatus().equals("Pending Payment")) {
                holder.binding.doneImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));
                holder.binding.processImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));
                holder.binding.deliveryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));
                holder.binding.doneDeliveryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_not_choose));



            } else if (ordersDM.getDeliveryStatus().equals("Delivered") || ordersDM.getDeliveryStatus().equals("Canceled") || ordersDM.getDeliveryStatus().equals("Complete")) {
                holder.binding.doneDeliveryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));
                holder.binding.deliveryImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));
                holder.binding.processImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));
                holder.binding.doneImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.request_choose));


            }

            if (position == getItemCount() - 1) {
                holder.binding.divider.setVisibility(View.GONE);
            } else {
                holder.binding.divider.setVisibility(View.VISIBLE);
            }


        } else if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) viewHolder;
            loadingViewHolder.rowLoadingBinding.progressBar1.setIndeterminate(true);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) == null) return VIEW_TYPE_LOADING;
        else return VIEW_TYPE_ITEM;

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
            if (!list.contains(null)) {
                list.add(null);
                notifyItemInserted(list.size() - 1);
                LoadData();
            }

        });

    }

    public void LoadData() {
        Log.d(TAG, "Log LoadData " + "LoadData");

    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {

        RowLoadingBinding rowLoadingBinding;

        LoadingViewHolder(RowLoadingBinding view) {
            super(view.getRoot());
            rowLoadingBinding = view;

        }

    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        RowCurrentMyOrderItemBinding binding;

        public ItemHolder(RowCurrentMyOrderItemBinding view) {
            super(view.getRoot());
            binding = view;


            binding.rateOrderBtn.setOnClickListener(view1 -> {
                int position = getAdapterPosition();
                OrderModel ordersDM = list.get(position);
                Intent intent = new Intent(context, RatingActivity.class);
                intent.putExtra(Constants.inv_id, ordersDM.getOrderCode() + "");
                Log.d(TAG, "inv_id" + ordersDM.getOrderCode() + "");
                context.startActivity(intent);
            });

            binding.container.setOnClickListener(view1 -> {
                int position = getAdapterPosition();

                OrderModel ordersDM = list.get(position);
                Intent intent = new Intent(context, InvoiceInfoActivity.class);
                intent.putExtra(Constants.ORDER_MODEL, ordersDM);
                context.startActivity(intent);


            });

            binding.orderDetailsBut.setOnClickListener(view1 -> {
                int position = getAdapterPosition();
                OrderModel ordersDM = list.get(position);
                Intent intent = new Intent(context, InvoiceInfoActivity.class);
                intent.putExtra(Constants.ORDER_MODEL, ordersDM);
                context.startActivity(intent);
            });


        }
    }


}