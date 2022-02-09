package com.ramez.shopp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.UtilityApp;
import com.ramez.shopp.Dialogs.ShowImageDialog;
import com.ramez.shopp.Models.ChatModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.DateHandler;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.RowChatReceiverBinding;
import com.ramez.shopp.databinding.RowChatSenderBinding;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {

    private static final String TAG = "ChatAdapter";

    private final int SENDER_VIEW = 1;
    private final int RECEIVER_VIEW = 2;

    private Context context;
    private ArrayList<ChatModel> chatMessages;


    public ChatAdapter(Context context, ArrayList<ChatModel> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_VIEW) {
            RowChatSenderBinding itemView = RowChatSenderBinding.inflate(LayoutInflater.from(context), parent, false);
            return new SenderHolder(itemView);

        } else if (viewType == RECEIVER_VIEW) {
            RowChatReceiverBinding itemView = RowChatReceiverBinding.inflate(LayoutInflater.from(context), parent, false);
            return new ReceiverHolder(itemView);
        }

        return null;
    }


    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SenderHolder) {
            SenderHolder holder = (SenderHolder) viewHolder;
            ChatModel chatModel = chatMessages.get(position);

            holder.binding.dateTxt.setText(NumberHandler.arabicToDecimal(DateHandler.convertLongDateToString(String.valueOf(chatModel.getMessageTime()), "yyyy-MM-dd hh:mm aa", UtilityApp.getLanguage())));
            holder.binding.messageTxt.setText(chatModel.getMessageBody().trim());
            Glide.with(context).asBitmap().load(chatModel.getSenderImage()).placeholder(R.drawable.avatar).placeholder(R.drawable.avatar).into(holder.binding.userImg);

            Glide.with(context).asBitmap().load(chatModel.getImageUrl()).placeholder(R.drawable.avatar).placeholder(R.drawable.avatar).into(holder.binding.chatImage);

            holder.binding.chatImage.setVisibility(chatModel.getInputType().equalsIgnoreCase(Constants.inputType_image) ? View.VISIBLE : View.GONE);

            if (chatModel.isIs_read()) {
                holder.binding.readImv.setVisibility(View.VISIBLE);
            }

            if (chatModel.getMessageBody().equals("") || chatModel.getMessageBody() == null) {
                holder.binding.messageTxt.setVisibility(View.GONE);
            } else {

                holder.binding.messageTxt.setVisibility(View.VISIBLE);

            }


        } else if (viewHolder instanceof ReceiverHolder) {
            ReceiverHolder receiverHolder = (ReceiverHolder) viewHolder;
            ChatModel chatModel = chatMessages.get(position);
            receiverHolder.binding.dateTxt.setText(NumberHandler.arabicToDecimal(DateHandler.convertLongDateToString(String.valueOf(chatModel.getMessageTime()), "yyyy-MM-dd hh:mm aa", UtilityApp.getLanguage())));
            if (chatModel.getMessageBody().equals("") || chatModel.getMessageBody() == null) {
                receiverHolder.binding.messageTxt.setVisibility(View.GONE);
            } else {

                receiverHolder.binding.messageTxt.setVisibility(View.VISIBLE);

            }

            receiverHolder.binding.messageTxt.setText(chatModel.getMessageBody().trim());
            Glide.with(context).asBitmap().load(chatModel.getReceiverImage()).placeholder(R.drawable.avatar).placeholder(R.drawable.avatar).into(receiverHolder.binding.userImg);

            receiverHolder.binding.chatImage.setVisibility(chatModel.getInputType().equalsIgnoreCase(Constants.inputType_image) ? View.VISIBLE : View.GONE);

        }
    }


    @Override
    public int getItemCount() {
        return chatMessages == null ? 0 : chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel obj = chatMessages.get(position);
        String typeSender = Constants.Sender;
        return obj.getMessageType().equals(typeSender) ? SENDER_VIEW : RECEIVER_VIEW;
    }

    class SenderHolder extends RecyclerView.ViewHolder {

        RowChatSenderBinding binding;

        public SenderHolder(RowChatSenderBinding view) {

            super(view.getRoot());
            binding = view;

            binding.chatImage.setOnClickListener(view1 -> {
                ShowImageDialog showImageDialog = new ShowImageDialog((Activity) context, chatMessages.get(getBindingAdapterPosition()).getImageUrl());
                showImageDialog.show();

            });

        }
    }


    class ReceiverHolder extends RecyclerView.ViewHolder {
        RowChatReceiverBinding binding;

        public ReceiverHolder(RowChatReceiverBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;

            binding.chatImage.setOnClickListener(view -> {
                ShowImageDialog showImageDialog = new ShowImageDialog((Activity) context, chatMessages.get(getBindingAdapterPosition()).getImageUrl());
                showImageDialog.show();

            });

        }

    }


}
