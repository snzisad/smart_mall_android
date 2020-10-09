package com.mrzisad.smartmall.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mrzisad.smartmall.utils.APILink;
import com.mrzisad.smartmall.R;
import com.mrzisad.smartmall.model.Order;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    Context context;
    List<Order> orderList;

    public NotificationAdapter(Context context2, List<Order> order) {
        this.context = context2;
        this.orderList = order;
    }

    public int getCount() {
        return this.orderList.size();
    }

    public Object getItem(int position) {
        return this.orderList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(this.context, R.layout.layout_notification, (ViewGroup) null);
        Glide.with(this.context).load(productImageURL(this.orderList.get(position).getProduct().getPicture())).into((ImageView) v.findViewById(R.id.imageViewProduct));
        ((TextView) v.findViewById(R.id.textViewName)).setText(this.orderList.get(position).getProduct().getName());
        ((TextView) v.findViewById(R.id.textPID)).setText("ID: " + this.orderList.get(position).getProduct().getId());
        ((TextView) v.findViewById(R.id.textViewPrice)).setText(this.orderList.get(position).getProduct().getPrice() + " ৳");
        ((TextView) v.findViewById(R.id.textQuantity)).setText("Quantity: " + this.orderList.get(position).getQuantity() + "\nSize: " + this.orderList.get(position).getSize() + "\nPayment Type: " + this.orderList.get(position).getPayment_type() + "\nTrnx ID: " + this.orderList.get(position).getTrxnid());
        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ");
        sb.append(this.orderList.get(position).getCustomer().getName());
        ((TextView) v.findViewById(R.id.textCustomerName)).setText(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Location: ");
        sb2.append(this.orderList.get(position).getCustomer().getAddress());
        ((TextView) v.findViewById(R.id.textCustomerLocation)).setText(sb2.toString());
        ((TextView) v.findViewById(R.id.textCustomerPhone)).setText("Customer: " + this.orderList.get(position).getCustomer().getPhone());
        return v;
    }

    public String productImageURL(String imagelist) {
        String[] images = imagelist.split("\\|ZISAD\\|");
        return APILink.PictureLink + images[0];
    }

}
