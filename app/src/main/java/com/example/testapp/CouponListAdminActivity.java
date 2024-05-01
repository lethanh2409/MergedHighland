package com.example.testapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;


import com.example.testapp.adapter.CouponAdapter;
import com.example.testapp.api.ApiService;
import com.example.testapp.model.Coupon;
import com.example.testapp.response.CommonResponse;
import com.google.android.gms.common.internal.service.Common;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CouponListAdminActivity extends AppCompatActivity {
    private Button btnAddCoupon;
    public static ListView lvCouponList;
    public static CouponAdapter couponManagerAdapter;
    private ImageView ivBack;
    public static List<Coupon> couponList= new ArrayList<>();
    private SearchView svCoupon;
    public static String tokenStaff;
    public static String tokenAdmin = "Bearer "+"eyJhbGciOiJIUzM4NCJ9.eyJpYXQiOjE3MTQxMTcwMTUsImV4cCI6MTcxNDcyMTgxNSwidXNlcm5hbWUiOiJBRE1JTjIiLCJhdXRob3JpdGllcyI6IkFETUlOIn0.DMlccdkRT1OTq3RC3IoXEOP7rf0nHzxK_hGWlEjfjfBUSmcEzf3cUFSMDVqn8ZniZ";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_coupon_admin);
        setControl();
        setEvent();
    }

    private void setControl() {
        btnAddCoupon = findViewById(R.id.btnAddCoupon);
        lvCouponList = findViewById(R.id.lv_couponList);
        ivBack = findViewById(R.id.ivBack);
        svCoupon = findViewById(R.id.svCoupon);
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
        btnAddCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CouponListAdminActivity.this, CouponAddingAdminActivity.class));
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CouponListAdminActivity.this, StaffNavigationMenuActivity.class);
                startActivity(intent);
            }
        });
        getAllCoupon();
        svCoupon.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Coupon> filteredData = new ArrayList<>();
                for (Coupon coupon : couponList) {
                    if (coupon.getCoupon_id().toString().contains(newText)) {
                        filteredData.add(coupon);
                    }
                }

                if (filteredData.isEmpty()) {
                    Toast.makeText(CouponListAdminActivity.this, "No data", Toast.LENGTH_LONG).show();
                } else {
                    couponManagerAdapter = new CouponAdapter(CouponListAdminActivity.this, R.layout.item_coupon_admin, filteredData);
                    lvCouponList.setAdapter(couponManagerAdapter);
                }

                return false;
            }

        });
    }









    public static void getAllCoupon() {
        ApiService.apiService.adminGetAllCoupon(tokenAdmin).enqueue(new Callback<CommonResponse<Coupon>>() {
            @Override
            public void onResponse(Call<CommonResponse<Coupon>> call, Response<CommonResponse<Coupon>> response) {
                if(response.isSuccessful()){
                    CommonResponse<Coupon> resultResponse = response.body();
                    if(resultResponse != null){
                        couponList = resultResponse.getData();
                        couponManagerAdapter = new CouponAdapter(lvCouponList.getContext(), R.layout.item_coupon_admin, couponList);
                        lvCouponList.setAdapter(couponManagerAdapter);
                    } else{
                        Toast.makeText(lvCouponList.getContext(), "result null",Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(lvCouponList.getContext(), "response false",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CommonResponse<Coupon>> call, Throwable t) {
                Toast.makeText(lvCouponList.getContext(), "ko call dc api",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
