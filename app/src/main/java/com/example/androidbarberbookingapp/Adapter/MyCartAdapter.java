package com.example.androidbarberbookingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidbarberbookingapp.Database.CartDatabase;
import com.example.androidbarberbookingapp.Database.CartItem;
import com.example.androidbarberbookingapp.Database.DatabaseUtils;
import com.example.androidbarberbookingapp.Interface.ICartItemUpdateListener;
import com.example.androidbarberbookingapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    Context context;
    List<CartItem> cartItemList;
    CartDatabase cartDatabase;
    ICartItemUpdateListener iCartItemUpdateListener;

    public MyCartAdapter(Context context, List<CartItem> cartItemList, ICartItemUpdateListener iCartItemUpdateListener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.iCartItemUpdateListener = iCartItemUpdateListener;
        this.cartDatabase = CartDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.layout_cart_item, viewGroup, false
        );
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        Picasso.get().load(cartItemList.get(i).getProductImage()).into(myViewHolder.img_product);
        myViewHolder.txt_cart_name.setText(new StringBuilder(cartItemList.get(i).getProductName()));
        myViewHolder.txt_cart_price.setText(new StringBuilder("$").append(cartItemList.get(i).getProductPrice()));
        myViewHolder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));

        //Event
        myViewHolder.setListener(new IImageButtonListener() {
            @Override
            public void onImageButtonClick(View view, int pos, boolean isDecrease) {
                if (isDecrease) {
                    if (cartItemList.get(pos).getProductQuantity() > 0) {
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList
                                        .get(pos)
                                        .getProductQuantity() - 1);
                        DatabaseUtils.updateCart(cartDatabase, cartItemList.get(pos));

                        myViewHolder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));

                    } else if (cartItemList.get(pos).getProductQuantity() == 0) {
                        DatabaseUtils.deleteCart(cartDatabase, cartItemList.get(pos));
                        cartItemList.remove(pos);
                        notifyItemRemoved(pos);
                    }

                } else {
                    if (cartItemList.get(pos).getProductQuantity() < 99) {
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList
                                        .get(pos)
                                        .getProductQuantity() + 1);
                        DatabaseUtils.updateCart(cartDatabase, cartItemList.get(pos));


                        myViewHolder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));

                    }
                }
                iCartItemUpdateListener.onCartItemUpdateSuccess();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    interface IImageButtonListener {
        void onImageButtonClick(View view, int pos, boolean isDecrease);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_cart_name, txt_cart_price, txt_quantity;
        ImageView img_decrease, img_increase, img_product;

        IImageButtonListener listener;

        public void setListener(IImageButtonListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_cart_name = (TextView) itemView.findViewById(R.id.txt_cart_name);
            txt_cart_price = (TextView) itemView.findViewById(R.id.txt_cart_price);
            txt_quantity = (TextView) itemView.findViewById(R.id.txt_cart_quantity);

            img_decrease = (ImageView) itemView.findViewById(R.id.img_decrease);
            img_increase = (ImageView) itemView.findViewById(R.id.img_increase);
            img_product = (ImageView) itemView.findViewById(R.id.cart_img);

            //Event
            img_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onImageButtonClick(view, getAdapterPosition(), true);
                }
            });

            img_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onImageButtonClick(view, getAdapterPosition(), false);
                }
            });
        }
    }
}
