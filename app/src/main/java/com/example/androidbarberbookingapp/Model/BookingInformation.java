package com.example.androidbarberbookingapp.Model;

import com.example.androidbarberbookingapp.Database.CartItem;
import com.google.firebase.Timestamp;

import java.util.List;

public class BookingInformation {
    private String cityBook,customerName,customerPhone,time,barberId,barberName,salonId,salonName,salonAddress;
    private Long slot;
    private Timestamp timestamp;
    private boolean done;
    private List<CartItem> cartItemList;

    public BookingInformation() {
    }

    public BookingInformation(String customerName, String customerPhone, String time, String barberId, String barberName, String salonId, String salonName, String salonAddress, Long slot) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.time = time;
        this.barberId = barberId;
        this.barberName = barberName;
        this.salonId = salonId;
        this.salonName = salonName;
        this.salonAddress = salonAddress;
        this.slot = slot;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getSalonId() {
        return salonId;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getSalonAddress() {
        return salonAddress;
    }

    public void setSalonAddress(String salonAddress) {
        this.salonAddress = salonAddress;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getCityBook() {
        return cityBook;
    }

    public void setCityBook(String cityBook) {
        this.cityBook = cityBook;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }
}
