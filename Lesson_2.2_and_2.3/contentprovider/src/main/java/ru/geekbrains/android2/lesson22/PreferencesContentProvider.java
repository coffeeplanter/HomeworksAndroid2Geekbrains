package ru.geekbrains.android2.lesson22;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import ru.geekbrains.android2.lesson22.database.DBHelper;

public class PreferencesContentProvider extends ContentProvider {

    final String TAG = "PrefContentProvider";

    // Константы для БД
    static final String DB_NAME = "prefsDB";
    static final int DB_VERSION = 1;

    // Таблица
    static final String PREF_TABLE = "preferences";

    // Поля
    static final String PREF_KEY = "key";
    static final String PREF_VALUE = "value";

    // Uri authority
    static final String AUTHORITY = "ru.geekbrains.android2";

    // Uri path
    static final String PREF_PATH = "preferences";

    // Uri
    public static final Uri PREF_CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + PREF_PATH);

    // Тип данных для всех настроек
    static final String PREF_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + PREF_PATH;

    // Тип данных для одной настройки
    static final String PREF_CONTENT_KEY_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + PREF_PATH;

    // UriMatcher
    // общий Uri
    static final int URI_PREFS = 1;

    // Uri с указанным ID
    static final int URI_PREFS_KEY = 2;


    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PREF_PATH, URI_PREFS);
        uriMatcher.addURI(AUTHORITY, PREF_PATH + "/*", URI_PREFS_KEY);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    public PreferencesContentProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete, " + uri.toString());
        if (selectionArgs == null) {
            selectionArgs = new String[]{uri.getLastPathSegment()};
        }
        switch (uriMatcher.match(uri)) {
            case URI_PREFS:
                Log.d(TAG, "URI_PREFS");
                break;
            case URI_PREFS_KEY:
                String key = uri.getLastPathSegment();
                Log.d(TAG, "URI_PREFS_KEY, " + key);
                if (TextUtils.isEmpty(selection)) {
                    selection = PREF_KEY + " = ?";
                } else {
                    selection = selection + " AND " + PREF_KEY + " = ?";
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Log.d(TAG, "selection: " + selection);
        try {
            Log.d(TAG, "selectionArgs: " + selectionArgs.toString());
        } catch (NullPointerException e) {
            Log.d(TAG, "NullPointerException on selectionArgs");
            e.printStackTrace();
        }
        int cnt = db.delete(PREF_TABLE, selection, selectionArgs);
        try {
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (NullPointerException e) {
            Log.d(TAG, "ContentResolver returned null");
            e.printStackTrace();
        }
        return cnt;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_PREFS:
                return PREF_CONTENT_TYPE;
            case URI_PREFS_KEY:
                return PREF_CONTENT_KEY_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_PREFS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(PREF_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(PREF_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        try {
            getContext().getContentResolver().notifyChange(resultUri, null);
        } catch (NullPointerException e) {
            Log.d(TAG, "ContentResolver returned null");
            e.printStackTrace();
        }


        return resultUri;
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        dbHelper = new DBHelper(getContext(), DB_NAME, null, DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query, " + uri.toString());
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_PREFS: // общий Uri
                Log.d(TAG, "UriMatcher: URI_PREFS");
                // если сортировка не указана, ставим свою - по имени
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = PREF_KEY + " ASC";
                }
                break;
            case URI_PREFS_KEY: // Uri с ID
                String key = uri.getLastPathSegment();
                Log.d(TAG, "URI_PREFS_KEY, " + key);
                // добавляем ID к условию выборки
                if (TextUtils.isEmpty(selection)) {
                    selection = PREF_KEY + " = " + key;
                } else {
                    selection = selection + " AND " + PREF_KEY + " = " + key;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        Log.d(TAG, "Switch finished");
        db = dbHelper.getReadableDatabase();
        Log.d(TAG, "DB created");
        Cursor cursor = db.query(PREF_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        Log.d(TAG, "Cursor created");
        // просим ContentResolver уведомлять этот курсор об изменениях данных в PREF_CONTENT_URI
        try {
            Log.d(TAG, "Notification to ContentResolver sent");
            cursor.setNotificationUri(getContext().getContentResolver(), PREF_CONTENT_URI);
        } catch (NullPointerException e) {
            Log.d(TAG, "ContentResolver returned null");
            e.printStackTrace();
        }
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_PREFS:
                Log.d(TAG, "URI_PREFS");

                break;
            case URI_PREFS_KEY:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "URI_PREFS_KEY, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = PREF_KEY + " = " + id;
                } else {
                    selection = selection + " AND " + PREF_KEY + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Log.d(TAG, "values: " + values.toString());
        Log.d(TAG, "selection: " + selection);
//        Log.d(TAG, "selectionArgs: " + selectionArgs.toString()); // null по ходу передаётся
//        int cnt = db.update(PREF_TABLE, values, selection, selectionArgs);
        int cnt = (int)db.replace(PREF_TABLE, null, values);
        try {
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (NullPointerException e) {
            Log.d(TAG, "ContentResolver returned null");
            e.printStackTrace();
        }

        return cnt;
    }
}
