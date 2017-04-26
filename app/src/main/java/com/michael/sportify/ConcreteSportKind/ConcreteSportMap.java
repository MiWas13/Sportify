package com.michael.sportify.ConcreteSportKind;


import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.michael.sportify.R;


/**
 * Created by Michael on 12.05.16.
 */
public class ConcreteSportMap extends DialogFragment {

    private static View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Чтобы карта не прогружалась заново и не вызывала NullPointerException, проверяем создан ли View
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.concrete_sport_map, container, false);
        } catch (InflateException e) {

        }

        Bundle arguments = getArguments();
        double locationLatitude = arguments.getDouble("LOCATION_LATITUDE");
        double locationLongitude = arguments.getDouble("LOCATION_LONGITUDE");

        getDialog().setTitle("Карта");
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_view);
        GoogleMap map = mapFragment.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng game_location = new LatLng(locationLatitude, locationLongitude);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(game_location, 13));
        map.addMarker(new MarkerOptions()
                .position(game_location)
                .title("Игра"));
        return view;
    }

}
