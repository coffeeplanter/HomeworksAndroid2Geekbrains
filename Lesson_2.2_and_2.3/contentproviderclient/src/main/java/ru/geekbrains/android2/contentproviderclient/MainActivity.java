package ru.geekbrains.android2.contentproviderclient;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    final String TAG = "Content Provider Client";

    final Uri PREF_URI = Uri.parse("content://ru.geekbrains.android2/preferences");

    static final String PREF_KEY = "key";
    static final String PREF_VALUE = "value";

    private static final String[] PROJECTION = new String[] {"_id", "key", "value"};

    private TextView mTextMessage;

    private static final int LOADER_ID = 1;
    private LoaderManager.LoaderCallbacks<Cursor> callbacks;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Layout set");

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        String from[] = {"key", "value"};
        int to[] = {android.R.id.text1, android.R.id.text2};
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, from, to, 0);
        ListView listviewPrefs = (ListView) findViewById(R.id.listview_prefs);
        listviewPrefs.setAdapter(adapter);
        Log.d(TAG, "Adapter set");
        callbacks = this;
        LoaderManager lm = getSupportLoaderManager();
        Log.d(TAG, "LoaderManager got");
        lm.initLoader(LOADER_ID, null, callbacks);
        Log.d(TAG, "Loader initialized, onCreate finished");

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Uri uri;
            ContentValues cv;
            switch (item.getItemId()) {
                case R.id.navigation_insert:
                    mTextMessage.setText(R.string.label_insert);
                    cv = new ContentValues();
                    cv.put(PREF_KEY, "new");
                    cv.put(PREF_VALUE, "new value");
                    uri = getContentResolver().insert(PREF_URI, cv);
                    Log.d(TAG, "insert, result Uri : " + uri.toString());
                    return true;
                case R.id.navigation_update:
                    mTextMessage.setText(R.string.label_update);
                    cv = new ContentValues();
                    cv.put(PREF_KEY, "new");
                    cv.put(PREF_VALUE, "updated value");
                    uri = Uri.parse("content://ru.geekbrains.android2/preferences" + "/new");
                    int cnt = getContentResolver().update(uri, cv, null, null);
                    Log.d(TAG, "update, count = " + cnt);
                    return true;
                case R.id.navigation_delete:
                    mTextMessage.setText(R.string.label_delete);
                    uri = Uri.parse("content://ru.geekbrains.android2/preferences" + "/new");
                    int cnt1 = getContentResolver().delete(uri, null, null);
                    Log.d(TAG, "delete, count = " + cnt1);
                    return true;
                case R.id.navigation_error:
                    mTextMessage.setText(R.string.label_error);
                    uri = Uri.parse("content://ru.geekbrains.android2/prefs" + "/new");
                    try {
                        getContentResolver().query(uri, null, null, null, null);
                    } catch (Exception ex) {
                        Log.d(TAG, "Error: " + ex.getClass() + ", " + ex.getMessage());
                    }
                    return true;
            }
            return false;
        }

    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(MainActivity.this, PREF_URI, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_ID:
                adapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
