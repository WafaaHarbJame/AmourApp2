package com.ramez.shopp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Dialogs.ConfirmDialog;
import com.ramez.shopp.Models.AddressModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.RowAddressItemBinding;

import java.util.List;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.Holder> {

    private Context context;
    private List<AddressModel> addressModelList;
    private OnDeleteClicked onDeleteClicked;
    private OnContainerSelect onContainerSelect;
    private boolean canSelect;
    MemberModel user;
    private int defaultAddressId;

    public AddressAdapter(Context context, List<AddressModel> addressModelList, boolean canSelect, OnDeleteClicked onDeleteClicked, OnContainerSelect OnContainerSelect) {
        this.context = context;
        this.addressModelList = addressModelList;
        this.onDeleteClicked = onDeleteClicked;
        this.onContainerSelect = OnContainerSelect;
        this.canSelect = canSelect;
        user = UtilityApp.getUserData();
        defaultAddressId = user.lastSelectedAddress;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RowAddressItemBinding itemView = RowAddressItemBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        RowAddressItemBinding binding = holder.binding;
        AddressModel addressModel = addressModelList.get(position);

        if (defaultAddressId == addressModel.getId()) {
//            MemberModel user = UtilityApp.getUserData();
//            user.setLastSelectedAddress(addressModel.getId());
//            UtilityApp.setUserData(user);
//            if (UtilityApp.getUserData().lastSelectedAddress == addressModel.getId()) {
            holder.binding.rbSelectAddress.setChecked(true);
//            }
        } else {
            holder.binding.rbSelectAddress.setChecked(false);
        }

        binding.tvAddressMark.setText(context.getString(R.string.ph).concat(" " + addressModel.getMobileNumber()));
        binding.tvAddressNote.setText(addressModel.getFullAddress());
        binding.tvaAddressTitle.setText(addressModel.getName());


        binding.deleteAddressBut.setOnClickListener(v -> {

            onDeleteClicked.onDeleteClicked(addressModel, UtilityApp.getUserData().lastSelectedAddress == position, position);
//            deleteAddressId(addressModelList.get(position).getId(),position);


        });

        if (position == getItemCount() - 1) {
            holder.binding.divider.setVisibility(View.GONE);
        } else {
            holder.binding.divider.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return addressModelList.size();
    }

    public void deleteAddressId(int addressId, int position) {
        ConfirmDialog.Click click = new ConfirmDialog.Click() {
            @Override
            public void click() {
                new DataFeacher(false, (obj, func, IsSuccess) -> {
                    if (func.equals(Constants.ERROR)) {
                        Toast.makeText(context, "" + context.getString(R.string.error_in_data), Toast.LENGTH_SHORT).show();
                    } else if (func.equals(Constants.FAIL)) {
                        Toast.makeText(context, "" + context.getString(R.string.fail_delete_address), Toast.LENGTH_SHORT).show();

                    } else if (func.equals(Constants.NO_CONNECTION)) {
                        Toast.makeText(context, "" + context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

                    } else {
                        if (IsSuccess) {
                            addressModelList.remove(position);
                            notifyDataSetChanged();
                            notifyItemRemoved(position);


                        } else {

                            Toast.makeText(context, "" + context.getString(R.string.fail_to_get_data), Toast.LENGTH_SHORT).show();
                        }
                    }

                }).deleteAddressHandle(addressId);
            }
        };

        new ConfirmDialog(context, context.getString(R.string.want_to_delete_address), R.string.ok, R.string.cancel_label, click, null, false);

    }

    public interface OnContainerSelect {
        void onContainerSelectSelected(AddressModel addressesDM, boolean makeDefault);
    }

    public interface OnDeleteClicked {
        void onDeleteClicked(AddressModel addressModel, boolean isChecked, int position);

    }

    public class Holder extends RecyclerView.ViewHolder {
        RowAddressItemBinding binding;

        public Holder(RowAddressItemBinding view) {
            super(view.getRoot());
            binding = view;

            binding.rbSelectAddress.setEnabled(!canSelect);

            binding.rbSelectAddress.setOnClickListener(v -> {
                AddressModel addressModel = addressModelList.get(getBindingAdapterPosition());
//                user = UtilityApp.getUserData();
                if (addressModel.getId() != defaultAddressId) {
                    defaultAddressId = addressModel.getId();
//                    user.setLastSelectedAddress(addressModel.getId());
//                    UtilityApp.setUserData(user);
                    notifyDataSetChanged();
                    onContainerSelect.onContainerSelectSelected(addressModel, true);
                }

            });

            binding.container.setOnClickListener(view1 -> {
                AddressModel addressModel = addressModelList.get(getBindingAdapterPosition());
                onContainerSelect.onContainerSelectSelected(addressModel, false);

            });

        }


    }
}