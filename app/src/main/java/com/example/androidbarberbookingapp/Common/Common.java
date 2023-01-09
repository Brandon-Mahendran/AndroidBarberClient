package com.example.androidbarberbookingapp.Common;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.core.app.NotificationCompat;

import com.example.androidbarberbookingapp.HomeActivity;
import com.example.androidbarberbookingapp.Model.Barber;
import com.example.androidbarberbookingapp.Model.BookingInformation;
import com.example.androidbarberbookingapp.Model.MyToken;
import com.example.androidbarberbookingapp.Model.Salon;
import com.example.androidbarberbookingapp.Model.TimeSlot;
import com.example.androidbarberbookingapp.Model.User;
import com.example.androidbarberbookingapp.R;
import com.example.androidbarberbookingapp.Service.MyFCMService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class Common {
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_SALON_STORE = "SALON_SAVE";
    public static final String KEY_BARBER_LOAD_DONE = "BARBER_LOAD_DONE" ;
    public static final String KEY_DISPLAY_TIME_SLOT = "DISPLAY_TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_BARBER_SELECTED = "BARBER_SELECTED";
    public static final int TIME_SLOT_TOTAL = 20;
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static final String EVENT_URI_CACHE = "URI_EVENT_SAVE";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static final String LOGGED_KEY = "UserLogged";
    public static final String RATING_INFORMATION_KEY = "RATING_INFORMATION";

    public static final String RATING_STATE_KEY = "RATING_STATE";
    public static final String RATING_SALON_ID = "RATING_SALON_ID";
    public static final String RATING_SALON_NAME = "RATING_SALON_NAME";
    public static final String RATING_BARBER_ID = "RATING_BARBER_ID";

    public static String IS_LOGIN = "IsLogin";
    public static User currentUser;
    public static Salon currentSalon;
    public static int step = 0; //Init first step is 0
    public static String city="";
    public static Barber currentBarber;
    public static int currentTimeSlot=-1;
    public static Calendar bookingDate=Calendar.getInstance();
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy"); //Only use when need format key
    public static BookingInformation currentBooking;
    public static String currentBookingId="";

    public static String convertTimeSlotToString(int slot) {
        switch (slot)
        {
            case 0:
                return "9:00-9:30";
            case 1:
                return "9:30-10:00";
            case 2:
                return "10:00-10:30";
            case 3:
                return "10:30-11:00";
            case 4:
                return "11:00-11:30";
            case 5:
                return "11:30-12:00";
            case 6:
                return "12:00-12:30";
            case 7:
                return "12:30-13:00";
            case 8:
                return "13:00-13:30";
            case 9:
                return "13:30-14:00";
            case 10:
                return "14:00-14:30";
            case 11:
                return "14:30-15:00";
            case 12:
                return "15:00-15:30";
            case 13:
                return "15:30-16:00";
            case 14:
                return "16:00-16:30";
            case 15:
                return "16:30-17:00";
            case 16:
                return "17:00-17:30";
            case 17:
                return "17:30-18:00";
            case 18:
                return "18:00-18:30";
            case 19:
                return "18:30-19:00";
            default:
                return "Closed";
        }
    }

    public static String convertTimeStampToStringKey(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        return simpleDateFormat.format(date);
    }

    public static String formatShoppingItemName(String name) {
        return name.length() > 13 ? new StringBuilder(name.substring(0,10)).append("...").toString():name;
    }

    public static void showNotification(Context context, int noti_id, String title, String content, Intent intent) {

        //Copy code from Staff app
        PendingIntent pendingIntent=null;
        if(intent != null)
            pendingIntent = PendingIntent.getActivity(context,
                    noti_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "edmt_barber_client_app";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "EDMT Barber Booking Staff App",NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Staff app");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher));

        if(pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        notificationManager.notify(noti_id,notification);
    }

    public static void showRatingDialog(Context context, String stateName, String salonID,
                                        String salonName, String barberId) {

        //First , we need get DocumentReference of Barber
        DocumentReference barberNeedRateRef = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(stateName)
                .collection("Branch")
                .document(salonID)
                .collection("Barber")
                .document(barberId);

        barberNeedRateRef.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    Barber barberRate = task.getResult().toObject(Barber.class);
                    barberRate.setBarberId(task.getResult().getId());

                    //Create View for dialog
                    View view = LayoutInflater.from(context)
                            .inflate(R.layout.layout_rating_dialog,null);

                    //Widget
                    TextView txt_salon_name = (TextView)view.findViewById(R.id.txt_salon_name);
                    TextView txt_barber_name = (TextView)view.findViewById(R.id.txt_barber_name);
                    AppCompatRatingBar ratingBar = (AppCompatRatingBar)view.findViewById(R.id.rating);

                    //Set Info
                    txt_barber_name.setText(barberRate.getName());
                    txt_salon_name.setText(salonName);

                    //Create Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                            .setView(view)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // IF select OK , we will update
                                    //rating information to FireStore

                                    Double original_rating = barberRate.getRating();
                                    Long ratingTimes = barberRate.getRatingTimes();
                                    float userRating = ratingBar.getRating();

                                    Double finalRating = (original_rating+userRating);

                                    //Update barber
                                    Map<String,Object> data_update = new HashMap<>();
                                    data_update.put("rating",finalRating);
                                    data_update.put("ratingTimes",++ratingTimes);
                                    
                                    barberNeedRateRef.update(data_update)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(context, "Thank you for rating!", Toast.LENGTH_SHORT).show();
                                                        //Remove Key
                                                        Paper.init(context);
                                                        Paper.book().delete(Common.RATING_INFORMATION_KEY);
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //If select Skip , we just dismiss dialog
                                    dialogInterface.dismiss();
                                }
                            }).setNeutralButton("NEVER", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //If select Never ,
                                    //That mean no rating , we will delete key
                                    Paper.init(context);
                                    Paper.book().delete(Common.RATING_INFORMATION_KEY);
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    public static enum TOKEN_TYPE{
        CLIENT,
        BARBER,
        MANAGER
    }

    public static void updateToken(Context context,final String s) {




        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {

                    MyToken myToken = new MyToken();
                    myToken.setToken(s);
                    myToken.setTokenType(TOKEN_TYPE.CLIENT); //Because token come from client app
                    myToken.setUserPhone(user.getPhoneNumber());

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(user.getPhoneNumber())
                            .set(myToken)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });


        }
        else
        {
            Paper.init(context);
            String userss = Paper.book().read(Common.LOGGED_KEY);
            if(userss != null)
            {
                if(!TextUtils.isEmpty(userss))
                {
                    MyToken myToken = new MyToken();
                    myToken.setToken(s);
                    myToken.setTokenType(TOKEN_TYPE.CLIENT); //Because token come from client app
                    myToken.setUserPhone(userss);

                    FirebaseFirestore.getInstance()
                            .collection("Tokens")
                            .document(userss)
                            .set(myToken)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                }
            }
        }
    }
}
