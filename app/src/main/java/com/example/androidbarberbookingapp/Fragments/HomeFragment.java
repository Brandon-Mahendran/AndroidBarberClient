package com.example.androidbarberbookingapp.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidbarberbookingapp.Adapter.HomeSliderAdapter;
import com.example.androidbarberbookingapp.Adapter.LookbookAdapter;
import com.example.androidbarberbookingapp.BookingActivity;
import com.example.androidbarberbookingapp.CartActivity;
import com.example.androidbarberbookingapp.Common.Common;
import com.example.androidbarberbookingapp.Database.CartDatabase;
import com.example.androidbarberbookingapp.Database.DatabaseUtils;
import com.example.androidbarberbookingapp.HistoryActivity;
import com.example.androidbarberbookingapp.Interface.IBannerLoadListener;
import com.example.androidbarberbookingapp.Interface.IBookingInfoLoadListener;
import com.example.androidbarberbookingapp.Interface.IBookingInformationChangeListener;
import com.example.androidbarberbookingapp.Interface.ICountItemInCartListener;
import com.example.androidbarberbookingapp.Interface.ILookbookLoadListener;
import com.example.androidbarberbookingapp.Model.Banner;
import com.example.androidbarberbookingapp.Model.BookingInformation;
import com.example.androidbarberbookingapp.R;
import com.example.androidbarberbookingapp.Service.PicassoImageLoadingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ILookbookLoadListener, IBannerLoadListener, IBookingInfoLoadListener, IBookingInformationChangeListener, ICountItemInCartListener {

    private Unbinder unbinder;

    CartDatabase cartDatabase;

    AlertDialog dialog;


    @BindView(R.id.notification_badge)
    NotificationBadge notificationBadge;

    @BindView(R.id.layout_user_information)
    LinearLayout layout_user_information;
    @BindView(R.id.txt_user_name)
    TextView txt_user_name;
    @BindView(R.id.banner_slider)
    Slider banner_slider;
    @BindView(R.id.recycler_look_book)
    RecyclerView recycler_look_book;


    @BindView(R.id.card_booking_info)
    CardView card_booking_info;
    @BindView(R.id.txt_salon_address)
    TextView txt_salon_address;
    @BindView(R.id.txt_salon_barber)
    TextView txt_salon_barber;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.txt_time_remain)
    TextView txt_time_remain;

    @OnClick(R.id.btn_delete_booking)
    void deleteBooking()
    {
        deleteBookingFromBarber(false);
    }

    @OnClick(R.id.btn_change_booking)
    void changeBooking()
    {
        changeBookingFromUser();
    }

    private void changeBookingFromUser() {
        //Show dialog confirm
        androidx.appcompat.app.AlertDialog.Builder confirmDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("Hey!")
                .setMessage("Do you really want to change booking information?\nBecause we will delete your old booking information\nJust confirm")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteBookingFromBarber(true); // True because we call from button change
                    }
                });
        confirmDialog.show();

    }

    private void deleteBookingFromBarber(boolean isChange) {
        /* To delete booking , first we need delete from barber collections
        After that , we will delete from User booking collection
        And final , delete event
         */



        //We need Load Common.currentBooking because we need some date from BookingInformation
        if(Common.currentBooking != null)
        {
            dialog.show();

            //Get booking information in barber object
            DocumentReference barberBookingInfo = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.currentBooking.getCityBook())
                    .collection("Branch")
                    .document(Common.currentBooking.getSalonId())
                    .collection("Barber")
                    .document(Common.currentBooking.getBarberId())
                    .collection(Common.convertTimeStampToStringKey(Common.currentBooking.getTimestamp()))
                    .document(Common.currentBooking.getSlot().toString());

            //When We have document , just delete it
            barberBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //After delete on Barber done
                    //we will start delete from User
                    deleteBookingFromUser(isChange);
                }
            });
        }
        else
        {
            Toast.makeText(getContext(),"Current Booking must not be null",Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBookingFromUser(boolean isChange) {
        //First , we need get information from user object
        if(!TextUtils.isEmpty(Common.currentBookingId))
        {
            DocumentReference userBookingInfo = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking")
                    .document(Common.currentBookingId);

            //Delete
            userBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //After delete from "User" , just delete from Calendar
                    //First , we need get save Uri of event we just add
                    Paper.init(getActivity());
                    if(Paper.book().read(Common.EVENT_URI_CACHE) != null)
                    {
                        String eventString = Paper.book().read(Common.EVENT_URI_CACHE).toString();
                        Uri eventUri = null;
                        if(eventString != null && !TextUtils.isEmpty(eventString))
                            eventUri = Uri.parse(eventString);
                        if(eventUri != null)
                            getActivity().getContentResolver().delete(eventUri,null,null);
                    }

                    Toast.makeText(getActivity(),"Success delete booking !",Toast.LENGTH_SHORT).show();


                    //Refresh
                    loadUserBooking();

                    //Check if isChange -> Call from change button , we will fired interface
                    if(isChange)
                        iBookingInformationChangeListener.onBookingInformationChange();

                    dialog.dismiss();
                }
            });
        }
        else
        {
            dialog.dismiss();

            Toast.makeText(getContext(),"Booking information ID must not be empty",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.card_view_booking)
    void booking()
    {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }

    @OnClick(R.id.card_view_history)
    void openHistoryActivity(){
        startActivity(new Intent(getActivity(), HistoryActivity.class));
    }

    @OnClick(R.id.card_view_cart)
    void openCartActivity(){
        startActivity(new Intent(getActivity(), CartActivity.class));
    }


    //FireStore
    CollectionReference bannerRef,lookbookRef;

    //Interface
    IBannerLoadListener iBannerLoadListener;
    ILookbookLoadListener iLookbookLoadListener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;


    ListenerRegistration userBookingListener=null;
    EventListener<QuerySnapshot> userBookingEvent = null;

    public HomeFragment() {
        bannerRef = FirebaseFirestore.getInstance().collection("Banner");
        lookbookRef = FirebaseFirestore.getInstance().collection("Lookbook");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
        countCartItem();
    }

    private void loadUserBooking() {
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        //Get Current date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        //Select booking information from Firebase with done=false and timestamp grater today
        userBooking
                .whereGreaterThanOrEqualTo("timestamp",toDayTimeStamp)
                .whereEqualTo("done",false)
                .limit(1) // Only take 1
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful())
                        {
                            if(!task.getResult().isEmpty())
                            {
                                for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult())
                                {
                                    BookingInformation bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                    iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation,queryDocumentSnapshot.getId());
                                    break; // Exit loop as soon as
                                }
                            }
                            else
                                iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
            }
        });

        //Here , after userBooking has been assign data (collections)
        //We will make realtime listen here
        if(userBookingEvent != null) // If userBookingEvent already init
        {
            if(userBookingListener == null) // Only add if userBookingListener == null
            {
                //That mean we just add 1 time
                userBookingListener = userBooking
                        .addSnapshotListener(userBookingEvent);
            }
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this,view);

        cartDatabase = CartDatabase.getInstance(getContext());


        //Init
        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener = this;
        iLookbookLoadListener = this;
        iBookingInfoLoadListener = this;
        iBookingInformationChangeListener = this;

        //Check is logged ?
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            setUserInformation();
            loadBanner();
            loadLookbook();
            initRealtimeUserBooking(); //Need declare above loadUserbooking();
            loadUserBooking();
            countCartItem();
        }

        return view;
    }

    private void initRealtimeUserBooking() {

        //Warning : Please follow this step is carefully
        //Because if this step you do wrong , this will make
        // Infinitity loop in your app , and you will retrieve QUOTA LIMIT from FireStore
        //If you do wrong , FireStore will get reach limit and you need wait for next 24 hours for reset
        //I've this bug for two days :( small bug but take me alot of time to wait :(

        if(userBookingEvent == null)
        {
            userBookingEvent = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    //In this event , when it fired , we will call loadUserBooking again
                    //to reload all Booking information
                    loadUserBooking();
                }
            };
        }
    }

    private void countCartItem() {
        DatabaseUtils.countItemInCart(cartDatabase,this);
    }

    private void loadLookbook() {
        lookbookRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> lookbooks = new ArrayList<>();
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot bannerSnapShot:task.getResult())
                            {
                                Banner banner = bannerSnapShot.toObject(Banner.class);
                                lookbooks.add(banner);
                            }
                            iLookbookLoadListener.onLookbookLoadSuccess(lookbooks);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iLookbookLoadListener.onLookbookLoadFailed(e.getMessage());
            }
        });
    }

    private void loadBanner() {
        bannerRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> banners = new ArrayList<>();
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot bannerSnapShot:task.getResult())
                            {
                                Banner banner = bannerSnapShot.toObject(Banner.class);
                                banners.add(banner);
                            }
                            iBannerLoadListener.onBannerLoadSuccess(banners);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBannerLoadListener.onBannerLoadFailed(e.getMessage());
            }
        });
    }

    private void setUserInformation() {
        layout_user_information.setVisibility(View.VISIBLE);
        txt_user_name.setText(Common.currentUser.getName());
    }

    @Override
    public void onLookbookLoadSuccess(List<Banner> banners) {
        recycler_look_book.setHasFixedSize(true);
        recycler_look_book.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_look_book.setAdapter(new LookbookAdapter(getActivity(),banners));

    }

    @Override
    public void onLookbookLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
        banner_slider.setAdapter(new HomeSliderAdapter(banners));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        card_booking_info.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation,String bookingId) {

        Common.currentBooking = bookingInformation;
        Common.currentBookingId = bookingId;

        txt_salon_address.setText(bookingInformation.getSalonAddress());
        txt_salon_barber.setText(bookingInformation.getBarberName());
        txt_time.setText(bookingInformation.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
                Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(),0).toString();

        txt_time_remain.setText(dateRemain);


        card_booking_info.setVisibility(View.VISIBLE); // Don't forget it

        dialog.dismiss();
    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInformationChange() {
        //Here we will just start activity Booking
        startActivity(new Intent(getActivity(),BookingActivity.class));
    }

    @Override
    public void onCartItemCountSuccess(int count) {
        notificationBadge.setText(String.valueOf(count));
    }


    @Override
    public void onDestroy() {
        if(userBookingListener != null)
            userBookingListener.remove();
        super.onDestroy();
    }
}
