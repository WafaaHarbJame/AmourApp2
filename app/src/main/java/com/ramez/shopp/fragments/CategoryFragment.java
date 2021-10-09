//package com.ramez.shopp.fragments;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.FragmentManager;
//import androidx.recyclerview.widget.GridLayoutManager;
//
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionDeniedResponse;
//import com.karumi.dexter.listener.PermissionGrantedResponse;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.single.PermissionListener;
//import com.kcode.permissionslib.main.OnRequestPermissionsCallBack;
//import com.kcode.permissionslib.main.PermissionCompat;
//import com.ramez.shopp.Classes.Constants;
//import com.ramez.shopp.activities.FullScannerActivity;
//import com.ramez.shopp.adapter.CategoryAdapter;
//import com.ramez.shopp.ApiHandler.DataFeacher;
//import com.ramez.shopp.Classes.CategoryModel;
//import com.ramez.shopp.Classes.MessageEvent;
//import com.ramez.shopp.Classes.UtilityApp;
//import com.ramez.shopp.Models.CategoryResultModel;
//import com.ramez.shopp.Models.LocalModel;
//import com.ramez.shopp.R;
//import com.ramez.shopp.databinding.FragmentCategoryBinding;
//
//import org.greenrobot.eventbus.EventBus;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//
//import static android.content.ContentValues.TAG;
//
//public class CategoryFragment extends FragmentBase implements CategoryAdapter.OnItemClick {
//    ArrayList<CategoryModel> categoryModelList;
//    GridLayoutManager gridLayoutManager;
//    LocalModel localModel;
//    private FragmentCategoryBinding binding;
//    private CategoryAdapter categoryAdapter;
//    private Activity activity;
//    int cityId;
//    private ActivityResultLauncher<Intent> scanLauncher;
//
//
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentCategoryBinding.inflate(inflater, container, false);
//        View view = binding.getRoot();
//
//        activity = getActivity();
//
//        gridLayoutManager = new GridLayoutManager(getActivityy(), 3);
//        binding.catRecycler.setHasFixedSize(true);
//        binding.catRecycler.setLayoutManager(gridLayoutManager);
//
//        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());
//
//        cityId = Integer.parseInt(localModel.getCityId());
//
//        if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size() > 0) {
//            categoryModelList = UtilityApp.getCategories();
//            initAdapter();
//
//        } else {
//            getCategories(cityId);
//
//        }
//
//        binding.swipeDataContainer.setOnRefreshListener(() -> {
//            binding.swipeDataContainer.setRefreshing(false);
//            getCategories(Integer.parseInt(localModel.getCityId()));
//
//        });
//
//
//        binding.searchBut.setOnClickListener(view1 -> {
//
//            Bundle bundle = new Bundle();
//            bundle.putInt(Constants.KEY_FRAGMENT_ID,  R.id.searchFragment);
//            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle));
//
//        });
//
//
//        binding.barcodeBut.setOnClickListener(view1 -> {
//
//            checkCameraPermission();
//
//        });
//
//        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
//
//            getCategories(Integer.parseInt(localModel.getCityId()));
//
//        });
//
//        scanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result != null && result.getData() != null) {
//
//                        Bundle bundle = result.getData().getExtras();
//                        boolean SEARCH_BY_CODE_byCode = bundle.getBoolean(Constants.SEARCH_BY_CODE_byCode, false);
//                        String CODE = bundle.getString(Constants.CODE);
//                        FragmentManager fragmentManager = getParentFragmentManager();
//
//                        SearchFragment searchFragment = new SearchFragment();
//                        Bundle data = new Bundle();
//                        data.putString(Constants.CODE, CODE);
//                        data.putBoolean(Constants.SEARCH_BY_CODE_byCode, SEARCH_BY_CODE_byCode);
//                        searchFragment.setArguments(data);
//                        fragmentManager.beginTransaction().replace(R.id.mainContainer, searchFragment, "searchFragment").commitAllowingStateLoss();
//
//                    }
//                }
//        );
//
//        return view;
//    }
//
//    private void initAdapter() {
//
//        categoryAdapter = new CategoryAdapter(activity, categoryModelList, 0, this, false);
//        binding.catRecycler.setAdapter(categoryAdapter);
//
//    }
//
//
//    @Override
//    public void onItemClicked(int position, CategoryModel categoryModel) {
//
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constants.CAT_LIST, categoryModelList);
//        bundle.putInt(Constants.MAIN_CAT_ID, categoryModelList.get(position).getId());
//        bundle.putInt(Constants.position, position);
//        bundle.putInt(Constants.KEY_FRAGMENT_ID,  R.id.categoryProductsFragment);
//        EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle));
//
//    }
//
//    public void getCategories(int storeId) {
//        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
//        binding.dataLY.setVisibility(View.GONE);
//        binding.noDataLY.noDataLY.setVisibility(View.GONE);
//        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
//
//        new DataFeacher(false, (obj, func, IsSuccess) -> {
//
//            if (isVisible()) {
//                CategoryResultModel result = (CategoryResultModel) obj;
//                String message = activity.getString(R.string.fail_to_get_data);
//
//                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
//
//                if (func.equals(Constants.ERROR)) {
//
//                    if (result != null) {
//                        message = result.getMessage();
//                    }
//                    binding.dataLY.setVisibility(View.GONE);
//                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                    binding.failGetDataLY.failTxt.setText(message);
//
//                } else if (func.equals(Constants.FAIL)) {
//
//                    binding.dataLY.setVisibility(View.GONE);
//                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                    binding.failGetDataLY.failTxt.setText(message);
//
//
//                } else if (func.equals(Constants.NO_CONNECTION)) {
//                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                    binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
//                    binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
//                    binding.dataLY.setVisibility(View.GONE);
//
//                } else {
//                    if (IsSuccess) {
//                        if (result.getData() != null && result.getData().size() > 0) {
//
//                            binding.dataLY.setVisibility(View.VISIBLE);
//                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
//                            categoryModelList = result.getData();
//
//                            Log.i(TAG, "Log productBestList" + categoryModelList.size());
//                            UtilityApp.setCategoriesData(categoryModelList);
//
//                            initAdapter();
//
//                        } else {
//
//                            binding.dataLY.setVisibility(View.GONE);
//                            binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
//
//                        }
//
//
//                    } else {
//
//                        binding.dataLY.setVisibility(View.GONE);
//                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
//                        binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
//                        binding.failGetDataLY.failTxt.setText(message);
//
//
//                    }
//                }
//            }
//
//        }).GetAllCategories(storeId);
//    }
//
//
//    private void checkCameraPermission() {
//        Dexter.withContext(getActivity()).withPermission(Manifest.permission.CAMERA)
//                .withListener(new PermissionListener() {
//            @Override
//            public void onPermissionGranted(PermissionGrantedResponse response) {
//                startScan();
//
//
//            }
//
//            @Override
//            public void onPermissionDenied(PermissionDeniedResponse response) {
//                Toast.makeText(getActivity(), "" + getString(R.string.permission_camera_rationale), Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                token.continuePermissionRequest();
//
//            }
//        }).withErrorListener(error -> Toast.makeText(getActivityy(), "" + getString(R.string.error_in_data), Toast.LENGTH_SHORT).show()).onSameThread().check();
//    }
//
//
//    private void startScan() {
//
//        try {
//            PermissionCompat.Builder builder = new PermissionCompat.Builder((getActivityy()));
//            builder.addPermissions(new String[]{Manifest.permission.CAMERA});
//            builder.addPermissionRationale(getString(R.string.should_allow_permission));
//            builder.addRequestPermissionsCallBack(new OnRequestPermissionsCallBack() {
//                public void onGrant() {
//                    Intent intent = new Intent(getActivityy(), FullScannerActivity.class);
//                    scanLauncher.launch(intent);
//
//
//                }
//
//                public void onDenied(@NotNull String permission) {
//                    Toast(R.string.some_permission_denied);
//
//                }
//            });
//            builder.build().request();
//        } catch (Exception var2) {
//            var2.printStackTrace();
//        }
//
//    }
//
//
//
//}