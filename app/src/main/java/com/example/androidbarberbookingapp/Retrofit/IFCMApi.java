package com.example.androidbarberbookingapp.Retrofit;

import com.example.androidbarberbookingapp.Model.FCMResponse;
import com.example.androidbarberbookingapp.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    //Copy from Staff app
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAqOKo_v8:APA91bGZqlFu6Q6GOIBU2WtrU4c1lvYipXz5a0AR-oA8MG2B9_JIm3T7Rh9tBqOQnypCgHGKGRDyjKTNyMaWPZDfZ5R62BVFzopLWOpddL1f_D7ZSCOn6Mm-4UYuhGIphKEdUzWU882g"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}
