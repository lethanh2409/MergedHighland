package com.example.testapp.adapter;

import static com.example.testapp.function.Function.formatDateTimeToDate;
import static com.example.testapp.function.Function.formatToVND;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.testapp.AdminCouponListActivity;
import com.example.testapp.R;
import com.example.testapp.function.Function;
import com.example.testapp.model.Coupon;

import java.util.ArrayList;
import java.util.List;

public class CouponAdapter extends ArrayAdapter<Coupon> {
    private Context context;
    private Integer resource;
    private List<Coupon> data;
    private List<Coupon> filterCouponList;



    public CouponAdapter(Context context, List<Coupon> coupons) {
        super(context, 0, coupons);
    }

    public CouponAdapter(@NonNull Context context, int resource, List<Coupon> data){
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;

    }
    static class ViewHolder{
        TextView tvValue, tvQuantity, tvMinium, tvTime, tvCouponId, tvStatus;
        ImageView ivCoupon;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.tvValue = convertView.findViewById(R.id.tv_value);
            holder.tvMinium = convertView.findViewById(R.id.tv_minium);
            holder.tvTime = convertView.findViewById(R.id.tv_time);
            holder.tvQuantity = convertView.findViewById(R.id.tv_quantity);
            holder.ivCoupon = convertView.findViewById(R.id.iv_coupon);
            holder.tvCouponId = convertView.findViewById(R.id.tv_couponId);
            holder.tvStatus = convertView.findViewById(R.id.tv_couponStatus);

            convertView.setTag(holder);
        }
        else{
                holder = (ViewHolder) convertView.getTag();
            }
        Coupon coupon = data.get(position);
        holder.tvValue.setText("Giảm " + formatToVND(coupon.getUse_value()));
//        holder.tvQuantity.setText("Số lượng: "+ String.valueOf(coupon.getQuantity()));
        holder.tvMinium.setText("Đơn tối thiểu " + formatToVND(coupon.getMinimum_value()));
        holder.tvTime.setText(formatDateTimeToDate(coupon.getStart_date()) +" - " + formatDateTimeToDate(coupon.getEnd_date()));
        Glide.with(convertView)
                .load(coupon.getImage())
                .into(holder.ivCoupon);
        if(holder.tvCouponId != null){
            holder.tvCouponId.setText("Mã Coupon: " + coupon.getCoupon_id().toString());
            holder.tvStatus.setText(coupon.getStatus());
            if(coupon.getStatus().equals(1)){
                holder.tvStatus.setText("Active");
                holder.tvStatus.setTextColor(ContextCompat.getColorStateList(CouponAdapter.this.getContext(), R.color.green));

            } else if(coupon.getStatus().equals(2)) {
                holder.tvStatus.setText("Inactive");
                holder.tvStatus.setTextColor(ContextCompat.getColorStateList(CouponAdapter.this.getContext(), R.color.mainColor));
            }
        }
        return convertView;
    }

    public void filterById(String query) {
        filterCouponList.clear();
        if (query.isEmpty()) {
            filterCouponList.addAll(data);
        } else {
            for (Coupon coupon : data) {
                if (coupon.getCoupon_id().toString().toLowerCase().contains(query.toLowerCase())) {
                    filterCouponList.add(coupon);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Tạo getter để lấy danh sách coupon đã lọc
    public Coupon findCouponById(String couponId) {
        for (int i = 0; i < getCount(); i++) {
            Coupon coupon = getItem(i);
            if (coupon.getCoupon_id().toString().equals(couponId)) {
                return coupon;
            }
        }
        return null;
    }
}
