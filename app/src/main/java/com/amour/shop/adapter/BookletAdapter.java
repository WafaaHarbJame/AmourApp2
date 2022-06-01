package com.amour.shop.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.amour.shop.classes.GlobalData;
import com.amour.shop.Models.BookletsModel;
import com.amour.shop.R;
import com.amour.shop.databinding.RowBookletItemBinding;

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
        Log.i(getClass().getSimpleName(),"Log ImageUrl"+bookletsList.get(position).getImage());
        try {

            GlobalData.INSTANCE.GlideImgGifSize(context,bookletsModel.getImage(),R.drawable.holder_image,holder.binding.ivCatImage);

        } catch (Exception e) {
            e.printStackTrace();
        }





        holder.binding.container.setOnClickListener(v -> onItemClick.onBookletClicked(position, bookletsModel));
    }

    public void setCategoriesList(List<BookletsModel> list) {
        bookletsList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
//        if(bookletsList.size()>4){
//            if (limit == 4) return Math.min(bookletsList.size(), limit);
//            else return bookletsList.size();
//        }
//        else {
//            return bookletsList.size();
//        }

        return bookletsList.size();

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
