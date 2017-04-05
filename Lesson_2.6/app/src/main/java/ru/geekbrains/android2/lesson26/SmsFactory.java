package ru.geekbrains.android2.lesson26;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class SmsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final static String TAG = "SmsFactory";

    private List<SmsMessage> data;
    private Context context;
    private int widgetId;
    private int smsCount;

    SmsFactory(Context context, Intent intent) {
        Log.d(TAG, "Объект SmsFactory создан");
        this.context = context;
        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        smsCount = intent.getIntExtra(SmsWidget.SMS_COUNT, 20);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        data = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {
        Log.d(TAG, "onDataSetChanged");
        prepareData();
        Log.d(TAG, "Данные подготовлены");
    }

    // Подготовка списка сообщений
    private void prepareData() {
        SharedPreferences prefs = context.getSharedPreferences(SmsWidgetConfigureActivity.PREFS_NAME, 0);
        smsCount = prefs.getInt(SmsWidgetConfigureActivity.PREF_PREFIX_KEY + widgetId + SmsWidgetConfigureActivity.PREF_COUNT_SUFFIX_KEY, 20);
        Log.d(TAG, "smsCount считан из настроек: " + smsCount);
        data.clear();
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.d(TAG, "Начало цикла перебора сообщений");
            int j = 0;
            do {
                SmsMessage message = new SmsMessage();
                message.setFrom(getNameByPhoneNumber(cursor.getString(2)));
                message.setDate(cursor.getLong(4));
                message.setBody(cursor.getString(12));
                data.add(message);
                j++;
            } while (cursor.moveToNext() && j < smsCount);
            Log.d(TAG, "smsCount в конце перебора смс: " + smsCount);
        } else {
            Log.d(TAG, "Сообщений не найдено");
        }
        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    // Получение имени контакта по номеру
    private String getNameByPhoneNumber(String phoneNumber) {
        String contactName;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            Log.d(TAG, "Получено имя контакта: " + contactName);
        }
        else {
            contactName = phoneNumber;
            Log.d(TAG, "Телефон не найден в списке контактов: " + phoneNumber);
        }
        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.sms_widget_item);
        SmsMessage message = data.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        remoteViews.setTextViewText(R.id.appwidget_item_from, message.getFrom());
        remoteViews.setTextViewText(R.id.appwidget_item_date, formatter.format(message.getDate().getTime()));
        remoteViews.setTextViewText(R.id.appwidget_item_text, message.getBody());
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
