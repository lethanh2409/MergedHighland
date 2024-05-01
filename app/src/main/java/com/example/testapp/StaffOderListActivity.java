package com.example.testapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;

import com.example.testapp.adapter.OrderListAdapter;
import com.example.testapp.adapter.OrderStatusAdapter;
import com.example.testapp.api.ApiService;
import com.example.testapp.model.Order;
import com.example.testapp.model.OrderStatus;
import com.example.testapp.response.CommonResponse;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffOderListActivity extends AppCompatActivity {
    private List<OrderStatus> listStatus = new ArrayList<>();
    OrderListAdapter orderAdapter;
    OrderStatusAdapter statusAdapter;
    ListView lvOrderList;
    TextView txtStatus, tvSelectStarDate, tvSelectEndDate, tvLine, tvNoData;
    Spinner spinnerList;
    ProgressBar proBarShowList;
    CardView btnShow;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_oder_list);
        setControl();
        setEvent();
    }

    private void setControl() {
        lvOrderList = findViewById(R.id.lvCustomerOrderList);

        spinnerList = (Spinner) findViewById(R.id.spnOrderFilter);

        txtStatus = findViewById(R.id.txtOrderStatus);
        tvSelectStarDate = findViewById(R.id.tv_selectStarDate);
        tvSelectEndDate = findViewById(R.id.tv_selectEndDate);
        tvLine = findViewById(R.id.tv_line);
        tvNoData = findViewById(R.id.tv_noData1);

        proBarShowList = findViewById(R.id.proBar_showList);

        btnShow = findViewById(R.id.btn_showDate);
    }

    private void setEvent() {
        SetData();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPerfs", Context.MODE_PRIVATE);
        String token = "Bearer " + sharedPreferences.getString("token", null);

        proBarShowList.setVisibility(View.VISIBLE);
        getAllOrder(token);

        tvNoData.setVisibility(View.GONE);

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v) {
            openDatePicker(token);
        }
        });

        statusAdapter = new OrderStatusAdapter(this, R.layout.layout_item_order_status, listStatus);
        spinnerList.setAdapter(statusAdapter);

        spinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = String.valueOf(listStatus.get(position).getName());
//                Toast.makeText(StaffOderListActivity.this, selectedStatus, Toast.LENGTH_SHORT).show();
                int statusId = listStatus.get(position).getId();
                if(statusId != -1)
                    getOrderById(token, statusId);
                else
                    getAllOrder(token);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void SetData() {
        listStatus.add(new OrderStatus(-1,"Tất cả"));
        listStatus.add(new OrderStatus(0,"Chờ xác nhận"));
        listStatus.add(new OrderStatus(1,"Đã xác nhận"));
        listStatus.add(new OrderStatus(2,"Đang thực hiện"));
        listStatus.add(new OrderStatus(3,"Đang vận chuyển"));
        listStatus.add(new OrderStatus(4,"Đã hoàn thành"));
        listStatus.add(new OrderStatus(5,"Đã hủy"));
    }

    private void getAllOrder(String token){
        ApiService.apiService.getAllOrder(token).enqueue(new Callback<CommonResponse<Order>>() {
            @Override
            public void onResponse(Call<CommonResponse<Order>> call, Response<CommonResponse<Order>> response) {
                if(response.isSuccessful()){
                    CommonResponse<Order> resultResponse = response.body();
                    if(resultResponse != null){
                    List<Order> orderList = resultResponse.getData();
                        if(orderList.isEmpty()){
                            tvNoData.setVisibility(View.VISIBLE);
                        }else {
                            orderAdapter = new OrderListAdapter(StaffOderListActivity.this, R.layout.layout_item_order, orderList);
                            lvOrderList.setAdapter(orderAdapter);
                            proBarShowList.setVisibility(View.GONE);
                        }
                        Log.i("get all order: ", resultResponse.getMessage());
                    }
                }else{
                    tvNoData.setVisibility(View.VISIBLE);
                    proBarShowList.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<CommonResponse<Order>> call, Throwable t) {
                Log.i("error get all order: ", t.getMessage());
            }
        });
    }

    private  void getOrderById(String token, Integer statusId){
        ApiService.apiService.getOrderByStatus(token, statusId).enqueue(new Callback<CommonResponse<Order>>() {
            @Override
            public void onResponse(Call<CommonResponse<Order>> call, Response<CommonResponse<Order>> response) {
                if(response.isSuccessful()){
                    CommonResponse<Order> resultResponse = response.body();
                    if(resultResponse != null){
                        List<Order> listOrder = resultResponse.getData();
                        if(listOrder.isEmpty()){
                            tvNoData.setVisibility(View.VISIBLE);
                        }else {
                            orderAdapter = new OrderListAdapter(StaffOderListActivity.this, R.layout.layout_item_order, listOrder);
                            lvOrderList.setAdapter(orderAdapter);
                        }
                        Log.i("get order by status:", resultResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse<Order>> call, Throwable t) {
                Log.i("error", t.getMessage());
            }
        });
    }

    private void openDatePicker(String token) {
        MaterialDatePicker<Pair<Long, Long>> materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().setSelection(new Pair<>(
                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                MaterialDatePicker.todayInUtcMilliseconds()
        )).build();
        materialDatePicker. addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                String dateStart = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selection.first));
                String dateEnd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selection.second));

                String showStartDate =  new SimpleDateFormat("MM-dd", Locale.getDefault()).format(new Date(selection.first));
                String showEndDate =  new SimpleDateFormat("MM-dd", Locale.getDefault()).format(new Date(selection.second));

                tvSelectStarDate.setText(showStartDate);
                tvSelectEndDate.setText(showEndDate);
                tvLine.setVisibility(View.VISIBLE);
                tvSelectEndDate.setVisibility(View.VISIBLE);
                Log.i("test", dateStart + " " + dateEnd);
                getOrderByDate(token, dateStart, dateEnd);
            }
        });
        materialDatePicker.show(getSupportFragmentManager(), "tag");
    }

    private void getOrderByDate(String token, String startDate, String endDate){
        ApiService.apiService.getOrderByDate(token, startDate, endDate).enqueue(new Callback<CommonResponse<Order>>() {
            @Override
            public void onResponse(Call<CommonResponse<Order>> call, Response<CommonResponse<Order>> response) {
                if(response.isSuccessful()){
                    CommonResponse<Order> resultResponse = response.body();
                    if(resultResponse != null){
                        List<Order> listOrder = resultResponse.getData();
                        orderAdapter = new OrderListAdapter(StaffOderListActivity.this, R.layout.layout_item_order, listOrder);
                        lvOrderList.setAdapter(orderAdapter);
                        Log.i("get order by status:", resultResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse<Order>> call, Throwable t) {
                Log.i("error api get order by date ", t.getMessage());
            }
        });
    }
}