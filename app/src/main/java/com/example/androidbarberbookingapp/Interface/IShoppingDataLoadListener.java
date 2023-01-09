package com.example.androidbarberbookingapp.Interface;

import com.example.androidbarberbookingapp.Model.ShoppingItem;

import java.util.List;

public interface IShoppingDataLoadListener {
    void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList);
    void onShoppingDataLoadFailed(String message);
}
