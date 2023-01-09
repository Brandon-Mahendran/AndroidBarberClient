package com.example.androidbarberbookingapp.Interface;

import com.example.androidbarberbookingapp.Model.Salon;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);
}
