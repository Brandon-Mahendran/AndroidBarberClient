package com.example.androidbarberbookingapp.Interface;

import com.example.androidbarberbookingapp.Model.BookingInformation;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation,String documentId);
    void onBookingInfoLoadFailed(String message);
}
