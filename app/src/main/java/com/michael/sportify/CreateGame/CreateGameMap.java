package com.michael.sportify.CreateGame;

import android.app.DialogFragment;
import android.app.FragmentManager;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.michael.sportify.R;

import java.util.Date;

/**
 * Created by Michael on 16.05.16.
 */
public class CreateGameMap extends DialogFragment implements GoogleMap.OnMapClickListener {

    private GoogleMap map;
    private static View view;
    private double locationLatitude, locationLongitude;
    private String userLogin, date, time;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Чтобы карта не прогружалась заново и не вызывала NullPointerException, проверяем создан ли View
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.create_game_map, container, false);
        } catch (InflateException e) {
            System.out.print("Ошибка при создании View");
        }

        getDialog().setTitle("Карта");
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_view_create_game);
        Button completeMap = (Button) view.findViewById(R.id.map_confirm);
        map = mapFragment.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMapClickListener(this);
        LatLng create_game_location = new LatLng(54.7104264, 20.4522144);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(create_game_location, 13));
        Bundle arguments = getArguments();
        userLogin = arguments.getString("USER_LOGIN");
        try {
            date = arguments.getString("DATE");
            time = arguments.getString("TIME");
        } catch (Exception e) {
            e.printStackTrace();
            date = "0";
            time = "0";
        }

        completeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateGameFragment createGameFragment = new CreateGameFragment();
                Bundle args = new Bundle();
                String locationLatitudeParams = String.valueOf(locationLatitude);
                String locationLongitudeParams = String.valueOf(locationLongitude);
                args.putString("LOCATION_LATITUDE", locationLatitudeParams);
                args.putString("LOCATION_LONGITUDE", locationLongitudeParams);
                args.putString("USER_LOGIN", userLogin);
                args.putString("TIME", time);
                args.putString("DATE", date);
                createGameFragment.setArguments(args);
                createGameFragment.show(getFragmentManager(), "");
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onMapClick(LatLng point) {

        Location location = new Location("Test");
        location.setLatitude(point.latitude);
        location.setLongitude(point.longitude);
        location.setTime(new Date().getTime());

        LatLng newLatLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        Log.d("Location", String.valueOf(newLatLng));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(newLatLng)
                .title("Игра")
                .snippet("Тут будет проходить игра");

        locationLatitude = location.getLatitude();
        locationLongitude = location.getLongitude();

        Log.d("LOCATION", String.valueOf(locationLatitude));
        Log.d("LOCATION", String.valueOf(locationLongitude));
        Log.d("LOCATION_ALL", String.valueOf(locationLatitude) + "," + String.valueOf(locationLongitude));

        map.clear();
        map.addMarker(markerOptions);

    }
}
