package com.example.androidbarberbookingapp.Model;

import com.example.androidbarberbookingapp.Common.Common;

public class MyToken {
    private String userPhone;
    private Common.TOKEN_TYPE tokenType;
    private String token;

    public MyToken() {
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public Common.TOKEN_TYPE getTokenType() {
        return tokenType;
    }

    public void setTokenType(Common.TOKEN_TYPE tokenType) {
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}