package com.ramez.shopp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.AddressModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowAddressCheckItemBinding;

import java.util.List;


public class AddressCheckAdapter extends RecyclerView.Adapter<AddressCheckAdapter.Holder> {

    private Context context;
    private List<AddressModel> addressModelList;
    private OnRadioAddressSelect onRadioAddressSelect;
    private OnEditClick onEditClick;
    private OnContainerSelect onContainerSelect;



    public AddressCheckAdapter(Context context, List<AddressModel> addressModelList,
                               OnRadioAddressSelect onRadioAddressSelect, OnEditClick onEditClick, OnContainerSelect OnContainerSelect) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.onRadioAddressSelect = onRadioAddressSelect;
        this.onEditClick = onEditClick;
        this.onContainerSelect=OnContainerSelect;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RowAddressCheckItemBinding itemView = RowAddressCheckItemBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        RowAddressCheckItemBinding binding = holder.binding;
        AddressModel addressModel=addressModelList.get(position);

        if (UtilityApp.getUserData().lastSelectedAddress ==addressModel.getId()) {
            holder.binding.rbSelectAddress.setChecked(true);

        } else {
            holder.binding.rbSelectAddress.setChecked(false);
        }

        binding.tvAddressMark.setText(context.getString(R.string.ph).concat(" "+addressModel.getMobileNumber()));
        binding.tvAddressNote.setText(addressModel.getFullAddress());
        binding.tvaAddressTitle.setText(addressModel.getName());

        binding.rbSelectAddress.setOnClickListener(v -> {
            onRadioAddressSelect.onAddressSelected(addressModel);
            MemberModel memberModel=UtilityApp.getUserData();
            memberModel.setLastSelectedAddress(addressModel.getId());
            UtilityApp.setUserData(memberModel);
            notifyDataSetChanged();
        });



        if (position == getItemCount() - 1) {
            holder.binding.divider.setVisibility(View.GONE);
        } else {
            holder.binding.divider.setVisibility(View.VISIBLE);
        }



        binding.editBut.setOnClickListener(v -> {

            onEditClick.OnEditClicked(addressModel, UtilityApp.getUserData().lastSelectedAddress == position,position);


        });
    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }


    public class Holder extends RecyclerView.ViewHolder {
        RowAddressCheckItemBinding binding;
        public Holder(RowAddressCheckItemBinding view) {
            super(view.getRoot());
            binding = view;

            binding.container.setOnClickListener(view1 -> {
                AddressModel addressModel=addressModelList.get(getBindingAdapterPosition());
              onContainerSelect.onContainerSelectSelected(addressModel);

            });

        }


    }

    public interface OnContainerSelect {
        void onContainerSelectSelected(AddressModel addressesDM);
    }


    public interface OnRadioAddressSelect {
        void onAddressSelected(AddressModel addressesDM);
    }


    public interface OnEditClick {
        void OnEditClicked(AddressModel addressModel, boolean isChecked,int position);

    }


}