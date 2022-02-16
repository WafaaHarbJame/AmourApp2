//package com.ramez.shopp.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.ramez.shopp.Models.CategoryModel;
//import com.ramez.shopp.Models.ChildCat;
//import com.ramez.shopp.databinding.RowCategoryBinding;
//import com.ramez.shopp.databinding.RowNewCategoryBinding;
//
//import java.util.List;
//
//public class CategoryNewAdapter extends RecyclerView.Adapter<CategoryNewAdapter.Holder> {
//
//    private Context context;
//    private List<CategoryModel> categoryDMS;
//    private OnItemClick onItemClick;
//    private int limit = 6;
//    private boolean isHoriz;
//
//
//    public CategoryNewAdapter(Context context, List<CategoryModel> categoryDMS, int limit, OnItemClick onItemClick, boolean isHoriz) {
//        this.context = context;
//        this.categoryDMS = categoryDMS;
//        this.onItemClick = onItemClick;
//        this.limit = limit;
//        this.isHoriz=isHoriz;
//
//        ;
//    }
//
//    @Override
//    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        RowNewCategoryBinding itemView = RowNewCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
//
//        return new Holder(itemView);
//    }
//
//
//    @Override
//    public void onBindViewHolder(final Holder holder, int position) {
//
//        CategoryModel categoryModel = categoryDMS.get(position);
//        try {
//            holder.binding.categoryNameTv.setText(categoryModel.getCategoryName());
//           RecyclerView.LayoutManager lln = new LinearLayoutManager(context);
//            holder.binding.subCatRecycler.setLayoutManager(lln);
//            SubCategoryNewAdapter subCategoryNewAdapter = new SubCategoryNewAdapter(context, categoryModel.getChildCat(), new SubCategoryNewAdapter.OnItemClick() {
//                @Override
//                public void onItemClicked(int position, ChildCat childCat) {
//
//                }
//            });
//
//            holder.binding.subCatRecycler.setAdapter(subCategoryNewAdapter);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        holder.binding.container.setOnClickListener(v -> {
//                    onItemClick.onItemClicked(position,categoryModel);
//                }
//              );
//
//    }
//
//    public void setCategoriesList(List<CategoryModel> list) {
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
//        void onItemClicked(int position,CategoryModel categoryModel);
//    }
//
//    static class Holder extends RecyclerView.ViewHolder {
//
//        RowNewCategoryBinding binding;
//
//        Holder(RowNewCategoryBinding view) {
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
