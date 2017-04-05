package ru.geekbrains.android2.lesson27;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

/**
 * Класс виджета для отображения текущего местоположения
 */

public class LocationWidget extends AppWidgetProvider {

    private static final String TAG = "LocationWidget";
    private static final String NO_DATA_MESSAGE = "Нет данных.";
    private static final String ADDRESS_PREFIX = "Адрес: ";
    private static final String COORDINATES_PREFIX = "Координаты: ";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.location_widget);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Location location;

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: " + location.toString());
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged: " + provider);
            }
            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled: " + provider);
            }
            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled: " + provider);
            }
        };

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000L, 10F, locationListener);
            Log.d(TAG, "Отправлен запрос на определение местоположения по базовым станциям");
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            views.setTextViewText(R.id.gsm_address, ADDRESS_PREFIX + getAddressByLocation(context, location));
            views.setTextViewText(R.id.gsm_coordinates, COORDINATES_PREFIX + locationToString(location));
        } catch (SecurityException e) { // В случае отсутствия разрешений
            String errorText = context.getString(R.string.no_gps_permission);
            views.setTextViewText(R.id.gsm_address, ADDRESS_PREFIX + errorText.toLowerCase());
            views.setTextViewText(R.id.gsm_coordinates, COORDINATES_PREFIX + errorText.toLowerCase());
            Log.d(TAG, errorText, e);
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000L, 10F, locationListener);
            Log.d(TAG, "Отправлен запрос на определение местоположения по спутникам");
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            views.setTextViewText(R.id.gps_address, ADDRESS_PREFIX + getAddressByLocation(context, location));
            views.setTextViewText(R.id.gps_coordinates, COORDINATES_PREFIX + locationToString(location));
        } catch (SecurityException e) { // В случае отсутствия разрешений
            String errorText = context.getString(R.string.no_gps_permission);
            views.setTextViewText(R.id.gps_address, ADDRESS_PREFIX + errorText.toLowerCase());
            views.setTextViewText(R.id.gps_coordinates, COORDINATES_PREFIX + errorText.toLowerCase());
            Log.d(TAG, errorText, e);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    // Преобразование координат в строку
    private static String locationToString(Location location) {
        StringBuilder locationTextBuffer = new StringBuilder();
        if (location != null) {
            locationTextBuffer.append("широта: ");
            locationTextBuffer.append(String.valueOf(location.getLatitude()));
            locationTextBuffer.append("; долгота: ");
            locationTextBuffer.append(String.valueOf(location.getLongitude())).append(".");
        }
        else {
            locationTextBuffer.append(NO_DATA_MESSAGE.toLowerCase()).append(" Ошибка получения координат.");
        }
        return locationTextBuffer.toString();
    }

    // Получение адреса по координатам
    private static String getAddressByLocation(Context context, Location location) {
        if (location == null) {
            Log.d(TAG, NO_DATA_MESSAGE + " Ошибка получения координат.");
            return NO_DATA_MESSAGE.toLowerCase() + " Ошибка получения координат.";
        }
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            Log.d(TAG, NO_DATA_MESSAGE + "Ошибка получения адреса по координатам", e);
            return NO_DATA_MESSAGE.toLowerCase() + " Ошибка получения адреса по координатам, ошибка ввода-вывода.";
        }
        if (addressList.isEmpty()) {
            Log.d(TAG, NO_DATA_MESSAGE + "Ошибка получения адреса по координатам");
            return NO_DATA_MESSAGE.toLowerCase() + " Ошибка получения адреса по координатам, список пуст.";
        }
        Address address = addressList.get(0);
        StringBuilder addressTextBuffer = new StringBuilder();
        addressTextBuffer.append("Адрес: ")
                .append(address.getPostalCode()).append(", ")
                .append(address.getCountryName()).append(", ")
                .append(address.getAdminArea()).append(", ")
                .append(address.getThoroughfare()).append(", ")
                .append(address.getSubThoroughfare()).append(".");
        return addressTextBuffer.toString();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // TODO: Enter relevant functionality for when the first widget is created
        Log.d(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        // TODO: Enter relevant functionality for when the last widget is disabled
        Log.d(TAG, "onDisabled");
    }

}
