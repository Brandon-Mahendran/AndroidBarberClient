package com.example.androidbarberbookingapp.Model.EventBus;

public class ConfirmBookingEvent {
    private boolean isConfirm;

    public ConfirmBookingEvent(boolean isConfirm) {
        this.isConfirm = isConfirm;
    }

    public boolean isConfirm() {
        return isConfirm;
    }

    public void setConfirm(boolean confirm) {
        isConfirm = confirm;
    }
}
