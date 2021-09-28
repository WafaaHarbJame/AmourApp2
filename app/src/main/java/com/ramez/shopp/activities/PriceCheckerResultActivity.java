//package com.ramez.shopp.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.ramez.shopp.adapter.SimilierProductAdapter;
//import com.ramez.shopp.Models.ProductModel;
//import com.ramez.shopp.databinding.ActivityPriceCheckerResultBinding;
//
//import java.util.ArrayList;
//
//public class PriceCheckerResultActivity extends ActivityBase  implements SimilierProductAdapter.OnItemClick{
//    ActivityPriceCheckerResultBinding binding;
//    private int SEARCH_CODE = 2000;
//    private String CODE = "";
//    ArrayList<ProductModel> productList;
//    private SimilierProductAdapter adapter;
//    private ActivityResultLauncher<Intent> scanLauncher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityPriceCheckerResultBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        setTitle("");
//
//         LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActiviy(), RecyclerView.HORIZONTAL, false);
//        binding.offerRecycler.setLayoutManager(linearLayoutManager);
//        binding.offerRecycler.setHasFixedSize(true);
//        binding.offerRecycler.setItemAnimator(null);
//
//
//        productList = new ArrayList<>();
//        productList.add(new ProductModel());
//        productList.add(new ProductModel());
//        productList.add(new ProductModel());
//        productList.add(new ProductModel());
//        productList.add(new ProductModel());
//
//        initAdapter();
//
//        scanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result != null && result.getData() != null) {
//
//
//                    }
//                }
//        );
//
//
//        binding.scanAgainBut.setOnClickListener(v -> {
//            Intent intent = new Intent(getActiviy(), FullScannerActivity.class);
////            startActivityForResult(intent, SEARCH_CODE);
//            scanLauncher.launch(intent);
//
//        });
//
//    }
//
////    @Override
////    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////
////        if (requestCode == SEARCH_CODE) {
////
////            if (data != null) {
////                CODE = data.getStringExtra(Constants.CODE);
////
////
////            }
////
////
////        }
////    }
//
//
//
//
//
//
//    private void initAdapter() {
//        adapter = new SimilierProductAdapter(getActiviy(), productList, this, 0);
//        binding.offerRecycler.setAdapter(adapter);
//
//    }
//
//
//    @Override
//    public void onItemClicked(int position, ProductModel productModel) {
//
//    }
//
//
//
//}