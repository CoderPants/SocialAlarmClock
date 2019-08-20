package com.donteco.alarmClock.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.help.ConstantsForApp;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimeFragment extends Fragment {

    private Activity activity;
    private Timer timer;
    private TextView currentTimeTV;
    private TextView currentCity;

    //Permission creation
    private boolean noView = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        try {
            activity = getActivity();
            return inflater.inflate(R.layout.show_time_fragment, container, false);
        }
        catch (Exception e) {
            Log.wtf(ConstantsForApp.LOG_TAG, "Exception in TimeFragment in onCreateView method " + e);
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentTimeTV = view.findViewById(R.id.show_time_tv_current_time);
        showCurrentTime();

        currentCity = view.findViewById(R.id.tv_current_location);

        //I need to show permission request only once
        if(noView)
        {
            setCurrentLocation();
            noView = false;
        }

        currentCity.setOnClickListener(view1 -> setCurrentLocation());
    }

    private void setCurrentLocation()
    {
        if(activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ConstantsForApp.LOCATION_PERMITION_REQUEST);
        }
        else
        {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            String city = getCurrentLocation(location.getLatitude(), location.getLongitude());

            currentCity.setText(city);
        }
    }

    private String getCurrentLocation(double lat, double lon)
    {
        String cityName = "";

        Geocoder geocoder = new Geocoder(activity.getApplicationContext(), Locale.ENGLISH);
        List<Address> addresses;

        try
        {
            addresses = geocoder.getFromLocation(lat, lon, 10);

            if(addresses.size() > 0)
            {
                for (Address address : addresses)
                {
                    if(address.getLocality() != null && address.getLocality().length() > 0)
                    {
                        cityName = address.getLocality();
                        break;
                    }
                }
            }
        }
        catch (IOException e)
        {
            Log.e(ConstantsForApp.LOG_TAG, "Error caused by getting current location in Time Fragment" +
                    "in getCurrentLocation method", e);
        }

        return cityName;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == ConstantsForApp.LOCATION_PERMITION_REQUEST)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                //Just getting rid of this error
                @SuppressLint("MissingPermission")
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                String city = getCurrentLocation(location.getLatitude(), location.getLongitude());
                currentCity.setText(city);
            }
            else
                Toast.makeText(activity, "Permission denied!", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //For timer deleting
    @Override
    public void onPause() {

        if(timer != null)
        {
            currentTimeTV.setText("");
            timer.cancel();
            timer.purge();
            timer = null;
        }
        Log.i(ConstantsForApp.LOG_TAG, "Timer stopped");

        super.onPause();
    }

    //For timer resuming
    @Override
    public void onResume() {
        showCurrentTime();

        Log.i(ConstantsForApp.LOG_TAG, "Timer resumed");
        super.onResume();
    }

    //Get info about threadsafe state
    //Not on destroy, cos we won't close all out app
    //Ony one fragment
    @Override
    public void onDestroyView()
    {
        if(timer != null)
        {
            currentTimeTV.setText("");
            timer.cancel();
            timer.purge();
            timer = null;
            Log.i(ConstantsForApp.LOG_TAG, "In onDestroy view. Timer instance has been destroyed ");
        }

        super.onDestroyView();
    }

    private void showCurrentTime()
    {
        if(timer != null)
            timer.cancel();

        //If JVM stop it before quiting from app
        timer = new Timer(true);
        TimeFragment.TimeHandler timeHandler = new TimeFragment.TimeHandler();
        timer.schedule(timeHandler, 0, ConstantsForApp.ONE_SECOND_PAUSE);
    }


    //----------------------------------------------------------------------------------------------
    //Thread for time showing
    private class TimeHandler extends TimerTask
    {
        private String timeFormat;

        private TimeHandler() {
            timeFormat = "HH:mm:ss";
        }

        //Main thread for getting time
        @Override
        public void run()
        {
            activity.runOnUiThread(this::updateTime);
        }

        //Need to find better way to handle it
        private void updateTime()
        {
            currentTimeTV.setText(android.text.format.DateFormat.format( timeFormat,
                    Calendar.getInstance().getTime() ));
        }
    }
}
