// Hien thi danh sach san pham

package com.example.testapp;



import static com.example.testapp.R.id.spnFilter;
import static com.example.testapp.api.ApiService.apiService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.testapp.adapter.ProductManagerAdapter;
import com.example.testapp.model.Category;
import com.example.testapp.model.Product;
import com.example.testapp.response.CommonResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductListActivity extends AppCompatActivity {

    public static ProductManagerAdapter productManagerAdapter;
    public static RecyclerView rvProduct;
    public static List<Product> productList = new ArrayList<>();
    private ImageView ivBack;
    private AppCompatSpinner spnFilter;
    private ImageButton btnAdd;
    private SearchView svProduct;
    public static String tokenStaff;
    public static List<Category> categoryList = new ArrayList<>();
    boolean isInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_list);
        if (isInit==false){
            khoiTao();
            isInit=true;
        }
        setControl();
        setEvent();

    }
    private void khoiTao() {
        categoryList.add(new Category("Cà phê"));
        categoryList.add(new Category("Trà sữa"));
        categoryList.add(new Category("Trà"));
        categoryList.add(new Category("Freeze"));
        categoryList.add(new Category("Nước ngọt"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, StaffNavigationMenuActivity.class);
        startActivity(intent);
    }

    private void setEvent() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPerfs", Context.MODE_PRIVATE);
        tokenStaff =  "Bearer "+sharedPreferences.getString("token", null);

        // click vào nút Back trên ActionBar
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListActivity.this, StaffNavigationMenuActivity.class);
                startActivity(intent);
            }
        });

        // click vào nút Thêm sẽ chuyển đn giao diện thêm sản phẩm
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductListActivity.this, ProductAddingActivity.class);
                startActivity(intent);
            }
        });

        // gạch ngang dưới chân từng item
        rvProduct.setLayoutManager(new LinearLayoutManager(this));

        // call api lấy danh sách product
        getProductList();

        svProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(String.valueOf(newText));
                return true;
            }

        });
    }

    private void filterList(String newText) {
        List<Product> filterList = new ArrayList<>();

        for(Product itemProduct: productList){
            if(itemProduct.getProductName().toLowerCase().contains(newText.toLowerCase())){
                filterList.add(itemProduct);
            }
        }

        if(filterList.isEmpty()){
            Toast.makeText(ProductListActivity.this, "No data", Toast.LENGTH_SHORT).show();
        } else{
            productManagerAdapter.setFilterList(filterList);
        }
    }


    @SuppressLint("WrongViewCast")
    private void setControl() {
        rvProduct = findViewById(R.id.rvProduct);
        spnFilter = findViewById(R.id.spnFilter);
        btnAdd = findViewById(R.id.btnAdd);
        ivBack = findViewById(R.id.ivBack);
        svProduct = findViewById(R.id.svProduct);
    }


    public static void getProductList(){
        apiService.getProductAll(tokenStaff).enqueue(new Callback<CommonResponse<Product>>() {
            @Override
            public void onResponse(@NonNull Call<CommonResponse<Product>> call, @NonNull Response<CommonResponse<Product>> response) {
                if(response.isSuccessful()){
                    CommonResponse<Product> result = response.body();
                    if(result != null){
                        productList = result.getData();     // lấy list sp từ json
                        productManagerAdapter = new ProductManagerAdapter(rvProduct.getContext(), productList);
                        rvProduct.setAdapter(productManagerAdapter);
                    } else {
                        Toast.makeText(rvProduct.getContext(), "result null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(rvProduct.getContext(), "response false", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<CommonResponse<Product>> call, Throwable t) {
                Toast.makeText(rvProduct.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}