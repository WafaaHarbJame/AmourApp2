//package com.amour.shop.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.amour.shop.CallBack.DataCallback;
//import com.amour.shop.classes.Constants;
//import com.amour.shop.classes.UtilityApp;
//import com.amour.shop.Models.DeliveryTime;
//import com.amour.shop.R;
//import com.amour.shop.Utils.DateHandler;
//import com.amour.shop.databinding.RowDayItemBinding;
//
//import java.util.List;
//
//public class DeliveryDayAdapter extends RecyclerView.Adapter<DeliveryDayAdapter.ViewHolder> {
//
//    Context context;
//    DataCallback dataCallback;
//    private List<DeliveryTime> deliveryDayList;
//    public int lastIndex = 0;
//
//
//    public DeliveryDayAdapter(Context context, List<DeliveryTime> deliveryDayList, DataCallback dataCallback) {
//        this.deliveryDayList = deliveryDayList;
//        this.context = context;
//        this.dataCallback = dataCallback;
//
//
//    }
//
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
//
//        RowDayItemBinding itemView = RowDayItemBinding.inflate(LayoutInflater.from(context), viewGroup, false);
//        return new ViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
//        final DeliveryTime deliveryTimes = deliveryDayList.get(position);
//        String dayName;
//        String monthName="";
//        String lang=UtilityApp.INSTANCE.getLanguage();
//
//
//        String today=DateHandler.GetDateNowString();
//        String day = DateHandler.FormatDate4(deliveryTimes.getDate(), "yyyy-MM-dd", "dd", lang);
//
//
//
//        if (UtilityApp.INSTANCE.getLanguage().equals(Constants.Arabic)) {
//            monthName = DateHandler.FormatDate4(deliveryTimes.getDate(), "yyyy-MM-dd", "MMMM", lang);
//
//            if(today.equals(deliveryTimes.getDate())){
//                dayName=context.getString(R.string.today);
//            }
//            else {
//                dayName = DateHandler.FormatDate4(deliveryTimes.getDate(), "yyyy-MM-dd", "EEEE");
//
//            }
//
//        } else {
//
//            monthName = DateHandler.FormatDate4(deliveryTimes.getDate(), "yyyy-MM-dd", "MMM", lang);
//
//            if(today.equals(deliveryTimes.getDate())){
//                dayName=context.getString(R.string.today);
//            }
//            else {
//                dayName = (DateHandler.FormatDate4(deliveryTimes.getDate(), "yyyy-MM-dd", "EEEE")).substring(0, 3);
//
//            }
//
//
//        }
//
//        viewHolder.binding.dateTxt.setText(day.concat(" "+monthName));
//
//        viewHolder.binding.dayTxt.setText(dayName);
//
//
//
//        if (lastIndex == position) {
//            viewHolder.binding.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.round_medium_corner_red));
//            viewHolder.binding.dayTxt.setTextColor(ContextCompat.getColor(context, R.color.white));
//            viewHolder.binding.dateTxt.setTextColor(ContextCompat.getColor(context, R.color.white));
//
//
//        } else {
//            viewHolder.binding.cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.round_medium_corner_unselected_gray));
//            viewHolder.binding.dayTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
//            viewHolder.binding.dateTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
//
//        }
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return deliveryDayList.size();
//    }
//
//
//
//    class ViewHolder extends RecyclerView.ViewHolder {
//        RowDayItemBinding binding;
//
//        ViewHolder(RowDayItemBinding view) {
//            super(view.getRoot());
//            binding = view;
//
//            itemView.setOnClickListener(view1 -> {
//                DeliveryTime deliveryDay = deliveryDayList.get(getBindingAdapterPosition());
//
//                lastIndex = getBindingAdapterPosition();
//                notifyDataSetChanged();
//
//                if (dataCallback != null) {
//                    dataCallback.dataResult(deliveryDay, "result", true);
//                }
//            });
//
//
//        }
//    }
//
//}