package com.amour.shop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.amour.shop.Models.ReviewModel;
import com.amour.shop.R;
import com.amour.shop.databinding.RowItemReviewBinding;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Holder> {

    private Context context;
    private List<ReviewModel> list;

    public ReviewAdapter(Context context, List<ReviewModel> list) {
        this.context = context;
        this.list = list;
        ;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {

        RowItemReviewBinding itemView = RowItemReviewBinding.inflate(LayoutInflater.from(context), parent, false);
        return new Holder(itemView);

    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        ReviewModel reviewModel = list.get(position);
        holder.binding.reviewTv.setText(reviewModel.getComment());

        holder.binding.tvUserName.setText(reviewModel.getUserName());

        holder.binding.ratingBar.setRating((float) reviewModel.getRate());

        Glide.with(context).load(reviewModel.getUserImage()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).placeholder(R.drawable.avatar).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                return false;
            }
        }).into(holder.binding.userImage);

//
//        if (position == getItemCount() - 1) {
//            holder.binding.divider.setVisibility(View.GONE);
//
//        } else {
//            holder.binding.divider.setVisibility(View.VISIBLE);
//        }
    }

    public void setReviewList(List<ReviewModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class Holder extends RecyclerView.ViewHolder {

        RowItemReviewBinding binding;

        Holder(RowItemReviewBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
