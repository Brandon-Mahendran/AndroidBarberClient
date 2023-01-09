package com.example.androidbarberbookingapp.Interface;

import com.example.androidbarberbookingapp.Model.Banner;

import java.util.List;

public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
