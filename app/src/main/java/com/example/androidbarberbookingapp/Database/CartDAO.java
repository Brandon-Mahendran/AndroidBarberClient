package com.example.androidbarberbookingapp.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDAO {

    @Query("SELECT SUM(productPrice*productQuantity) from Cart where userPhone=:userPhone")
    long sumPrice(String userPhone);

    @Query("SELECT * FROM Cart WHERE userPhone=:userPhone")
    List<CartItem> getAllItemFromCart(String userPhone);

    @Query("SELECT COUNT(*) from Cart where userPhone=:userPhone")
    int countItemInCart(String userPhone);

    @Query("SELECT * from Cart where productId=:productId AND userPhone=:userPhone")
    CartItem getProductInCart(String productId,String userPhone);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    void insert(CartItem...carts);

    @Update(onConflict = OnConflictStrategy.FAIL)
    void update(CartItem cart);

    @Delete
    void delete(CartItem cartItem);

    @Query("DELETE FROM Cart WHERE userPhone=:userPhone")
    void clearCart(String userPhone);
}
