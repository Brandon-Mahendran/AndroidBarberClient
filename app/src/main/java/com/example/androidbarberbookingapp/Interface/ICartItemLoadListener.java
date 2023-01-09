package com.example.androidbarberbookingapp.Interface;

import com.example.androidbarberbookingapp.Database.CartItem;

import java.util.List;

public interface ICartItemLoadListener {
    void onGetAllItemFromCartSuccess(List<CartItem> cartItemList);
}
