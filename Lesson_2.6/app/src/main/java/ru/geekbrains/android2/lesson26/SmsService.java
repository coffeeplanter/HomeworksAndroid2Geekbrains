package ru.geekbrains.android2.lesson26;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class SmsService extends RemoteViewsService {

    private static final String TAG = "SmsService";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "onGetViewFactory");
        return new SmsFactory(getApplicationContext(), intent);
    }

}
