package ru.geekbrains.android2.lesson27maps;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String AUSTRALIA = "Австралия";
    private final String ANTARCTICA = "Антарктида";
    private final String AFRICA = "Африка";
    private final String EURASIA = "Евразия";
    private final String NORTH_AMERICA = "Северная Америка";
    private final String SOUTH_AMERICA = "Южная Америка";


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        Map<String, LatLng> continents = new HashMap<>();
        continents.put(AUSTRALIA, new LatLng(-25.274398, 133.775136));
        continents.put(ANTARCTICA, new LatLng(-75.250973, -0.071389));
        continents.put(AFRICA, new LatLng(10.926893210107316, 22.875591851562447));
        continents.put(EURASIA, new LatLng(47.01169651991674, 67.29994131250007));
        continents.put(NORTH_AMERICA, new LatLng(49.01653503645544, -97.22532778124999));
        continents.put(SOUTH_AMERICA, new LatLng(-16.321651861861156, -58.345398374999995));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(55.75143585391288, 37.619023110900834), 1f));

        mMap.addMarker(new MarkerOptions().position(continents.get(AUSTRALIA))
                .title(AUSTRALIA)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .snippet("Самый маленький материк"));
        mMap.addMarker(new MarkerOptions().position(continents.get(ANTARCTICA))
                .title(ANTARCTICA)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .snippet("Самый холодный материк"));
        mMap.addMarker(new MarkerOptions().position(continents.get(AFRICA))
                .title(AFRICA)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Колыбель человечества"));
        mMap.addMarker(new MarkerOptions().position(continents.get(EURASIA))
                .title(EURASIA)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .snippet("Самый густонаселённый материк"))
            .showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(continents.get(NORTH_AMERICA))
                .title(NORTH_AMERICA)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                .snippet("Самый экономически развитый материк"));
        mMap.addMarker(new MarkerOptions().position(continents.get(SOUTH_AMERICA))
                .title(SOUTH_AMERICA)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .snippet("Родина инков :-)"));

    }

}
