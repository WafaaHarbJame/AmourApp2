//package com.ramez.shopp.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.ramez.shopp.Models.ChildCat;
//import com.ramez.shopp.R;
//import com.ramez.shopp.classes.GlobalData;
//import com.ramez.shopp.databinding.RowCategoryBinding;
//import com.ramez.shopp.databinding.RowNewSubcatBinding;
//
//import java.util.List;
//
//public class SubCategoryNewAdapter extends RecyclerView.Adapter<SubCategoryNewAdapter.Holder> {
//
//    private Context context;
//    private List<ChildCat> categoryDMS;
//    private OnItemClick onItemClick;
//    private int limit = 6;
//    private boolean isHoriz;
//
//
//    public SubCategoryNewAdapter(Context context, List<ChildCat> categoryDMS, OnItemClick onItemClick) {
//        this.context = context;
//        this.categoryDMS = categoryDMS;
//        this.onItemClick = onItemClick;
//
//
//        ;
//    }
//
//    @Override
//    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        RowNewSubcatBinding itemView = RowNewSubcatBinding.inflate(LayoutInflater.from(context), parent, false);
//
//        return new Holder(itemView);
//    }
//
//
//    @Override
//    public void onBindViewHolder(final Holder holder, int position) {
//
//        ChildCat childCat = categoryDMS.get(position);
//        try {
//            GlobalData.INSTANCE.GlideImg(context,childCat.getImage()
//                    , R.drawable.holder_image,holder.binding.ivCatImage);
//            holder.binding.categoryNameTv.setText(childCat.getCatName());
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        holder.binding.container.setOnClickListener(v -> {
//                 //   onItemClick.onItemClicked(position,childCat);
//
//
//                }
//              );
//
//    }
//
//    public void setCategoriesList(List<ChildCat> list) {
//        categoryDMS = list;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getItemCount() {
//
//        if (limit != 0) return Math.min(categoryDMS.size(), limit);
//        else return categoryDMS.size();
//    }
//
//    public interface OnItemClick {
//        void onItemClicked(int position,ChildCat childCat);
//    }
//
//    static class Holder extends RecyclerView.ViewHolder {
//
//        RowNewSubcatBinding binding;
//
//        Holder(RowNewSubcatBinding view) {
//            super(view.getRoot());
//            binding = view;
//        }
//    }
//
//    static class HorizontalHolder extends RecyclerView.ViewHolder {
//
//        RowCategoryBinding binding;
//
//        HorizontalHolder(RowCategoryBinding view) {
//            super(view.getRoot());
//            binding = view;
//        }
//    }
//}
