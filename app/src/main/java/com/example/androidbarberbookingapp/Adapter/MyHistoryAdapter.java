package com.example.androidbarberbookingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbarberbookingapp.Model.BookingInformation;
import com.example.androidbarberbookingapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.MyViewHolder> {

    Context context;
    List<BookingInformation> bookingInformationList;

    public MyHistoryAdapter(Context context, List<BookingInformation> bookingInformationList) {
        this.context = context;
        this.bookingInformationList = bookingInformationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.layout_history,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.txt_booking_barber_text.setText(bookingInformationList.get(i)
        .getBarberName());

        myViewHolder.txt_booking_time_text.setText(bookingInformationList.get(i).getTime());

        myViewHolder.txt_salon_address.setText(bookingInformationList.get(i).getSalonAddress());
        myViewHolder.txt_salon_name.setText(bookingInformationList.get(i).getSalonAddress());
    }

    @Override
    public int getItemCount() {
        return bookingInformationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Unbinder unbinder;

        @BindView(R.id.txt_salon_name)
        TextView txt_salon_name;
        @BindView(R.id.txt_salon_address)
        TextView txt_salon_address;
        @BindView(R.id.txt_booking_time_text)
        TextView txt_booking_time_text;
        @BindView(R.id.txt_booking_barber_text)
        TextView txt_booking_barber_text;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
        }
    }
}
