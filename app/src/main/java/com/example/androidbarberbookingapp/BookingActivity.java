package com.example.androidbarberbookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidbarberbookingapp.Adapter.MyViewPagerAdapter;
import com.example.androidbarberbookingapp.Common.Common;
import com.example.androidbarberbookingapp.Common.NonSwipeViewPager;
import com.example.androidbarberbookingapp.Model.Barber;
import com.example.androidbarberbookingapp.Model.EventBus.BarberDoneEvent;
import com.example.androidbarberbookingapp.Model.EventBus.ConfirmBookingEvent;
import com.example.androidbarberbookingapp.Model.EventBus.DisplayTimeSlotEvent;
import com.example.androidbarberbookingapp.Model.EventBus.EnableNextButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity {

    AlertDialog dialog;
    CollectionReference barberRef;


    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;


    //Event
    @OnClick(R.id.btn_previous_step)
    void previousStep(){
        if(Common.step == 3  || Common.step > 0)
        {
            Common.step--;
            viewPager.setCurrentItem(Common.step);
            if(Common.step < 3) // Always enable NEXT when Step < 3
            {
                btn_next_step.setEnabled(true);
                setColorButton();
            }
        }
    }

    @OnClick(R.id.btn_next_step)
    void nextClick(){
        if(Common.step < 3 || Common.step == 0)
        {
            Common.step++; //increase
            if(Common.step == 1) //After choose salon
            {
                if(Common.currentSalon != null)
                    loadBarberBySalon(Common.currentSalon.getSalonId());
            }
            else if(Common.step == 2) // Pick time slot
            {
                if(Common.currentBarber != null)
                    loadTimeSlotOfBarber(Common.currentBarber.getBarberId());
            }
            else if(Common.step == 3) // Confirm
            {
                if(Common.currentTimeSlot != -1)
                    confirmBooking();
            }
            viewPager.setCurrentItem(Common.step);
        }
    }

    private void confirmBooking() {

        EventBus.getDefault().postSticky(new ConfirmBookingEvent(true));
    }

    private void loadTimeSlotOfBarber(String barberId) {

        EventBus.getDefault().postSticky(new DisplayTimeSlotEvent(true));
    }

    private void loadBarberBySalon(String salonId) {
        dialog.show();

        //Now , select all barber of Salon
        //    /AllSalon/NewYork/Branch/cdv1Qrs7nF3VosCLDD71/Barber
        if(!TextUtils.isEmpty(Common.city))
        {
            barberRef = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.city)
                    .collection("Branch")
                    .document(salonId)
                    .collection("Barber");

            barberRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            ArrayList<Barber> barbers = new ArrayList<>();
                            for(QueryDocumentSnapshot barberSnapShot:task.getResult())
                            {
                                Barber barber = barberSnapShot.toObject(Barber.class);
                                barber.setPassword(""); // Remove password because in client app
                                barber.setBarberId(barberSnapShot.getId()); // Get Id of barber

                                barbers.add(barber);
                            }

                            EventBus.getDefault()
                                    .postSticky(new BarberDoneEvent(barbers));

                            dialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                        }
                    });
        }


    }



    //Event Bus convert
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public void buttonNextReceiver(EnableNextButton event)
    {
        int step = event.getStep();
        if(step == 1)
            Common.currentSalon = event.getSalon();
        else if(step == 2)
            Common.currentBarber = event.getBarber();
        else if(step == 3)
            Common.currentTimeSlot = event.getTimeSlot();

        btn_next_step.setEnabled(true);
        setColorButton();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();




        setupStepView();
        setColorButton();

        //View
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4); //we have 4 fragment so we need keep state of this 4 screen page
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {

                //show step
                stepView.go(i,true);
                if(i == 0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);

                //Set disable button next here
                btn_next_step.setEnabled(false);
                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setColorButton() {
        if(btn_next_step.isEnabled())
        {
            btn_next_step.setBackgroundResource(R.color.colorButton);
        }
        else
        {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if(btn_previous_step.isEnabled())
        {
            btn_previous_step.setBackgroundResource(R.color.colorButton);
        }
        else
        {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();
        stepList.add("Salon");
        stepList.add("Barber");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);

    }

    //Event Bus


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
