package com.ramez.shopp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowBookletItemBinding;

import java.util.List;

public class BookletAdapter extends RecyclerView.Adapter<BookletAdapter.Holder> {

    private Context context;
    private List<BookletsModel> bookletsList;
    private OnBookletClick onItemClick;
    private RequestOptions requestOptions;
    private int limit = 4;


    public BookletAdapter(Context context, List<BookletsModel> bookletsList, int limit, OnBookletClick onItemClick) {
        this.context = context;
        this.bookletsList = bookletsList;
        this.onItemClick = onItemClick;
        this.limit=limit;
        requestOptions = new RequestOptions();

    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        RowBookletItemBinding itemView = RowBookletItemBinding.inflate(LayoutInflater.from(context), parent, false);

        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        BookletsModel bookletsModel = bookletsList.get(position);

        Glide.with(context).asBitmap().load(bookletsModel.getImage()).placeholder(R.drawable.holder_image).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).addListener(new RequestListener<Bitmap>() {
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


        holder.binding.container.setOnClickListener(v -> onItemClick.onBookletClicked(position, bookletsModel));
    }

    public void setCategoriesList(List<BookletsModel> list) {
        bookletsList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (limit == 4) return Math.min(bookletsList.size(), limit);
        else return bookletsList.size();

    }

    public interface OnBookletClick {
        void onBookletClicked(int position, BookletsModel bookletsModel);
    }

    class Holder extends RecyclerView.ViewHolder {

        RowBookletItemBinding binding;

        Holder(RowBookletItemBinding view) {
            super(view.getRoot());
            binding = view;
        }
    }
}
