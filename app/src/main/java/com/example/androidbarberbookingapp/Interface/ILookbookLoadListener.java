package com.example.androidbarberbookingapp.Interface;

import com.example.androidbarberbookingapp.Model.Banner;

import java.util.List;

public interface ILookbookLoadListener {
    void onLookbookLoadSuccess(List<Banner> banners);
    void onLookbookLoadFailed(String message);
}
