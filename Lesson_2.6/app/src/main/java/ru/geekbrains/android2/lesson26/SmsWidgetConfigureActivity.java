package ru.geekbrains.android2.lesson26;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * The configuration screen for the {@link SmsWidget SmsWidget} AppWidget.
 */
public class SmsWidgetConfigureActivity extends Activity {

    private static final String TAG = "SmsWidgetConfigure";

    static final String PREFS_NAME = "ru.geekbrains.android2.lesson26.SmsWidget";
    static final String PREF_PREFIX_KEY = "appwidget_";
    static final String PREF_COUNT_SUFFIX_KEY = "_sms_count";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    EditText mAppWidgetSmsCount;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = SmsWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            int smsCount;
            try {
                smsCount = Integer.parseInt(mAppWidgetSmsCount.getText().toString());
            }
            catch (NumberFormatException e) {
                smsCount = 20;
            }
            if ((smsCount < 1) || (smsCount > 20)) {
                smsCount = 20;
            }
            saveTitlePref(context, mAppWidgetId, widgetText, smsCount);
            Log.d(TAG, "smsCount в Sharedpreferences: " + String.valueOf(loadSmsCountPref(context, mAppWidgetId)));

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            SmsWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public SmsWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text, int smsCount) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId + PREF_COUNT_SUFFIX_KEY, smsCount);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static int loadSmsCountPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(PREF_PREFIX_KEY + appWidgetId + PREF_COUNT_SUFFIX_KEY, 20);
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    static void deleteSmsCountPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId + PREF_COUNT_SUFFIX_KEY);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Log.d(TAG, "onCreate");

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.sms_widget_configure);
        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        mAppWidgetSmsCount = (EditText) findViewById(R.id.appwidget_items_count);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAppWidgetText.setText(loadTitlePref(SmsWidgetConfigureActivity.this, mAppWidgetId));
        mAppWidgetSmsCount.setText(String.valueOf(loadSmsCountPref(SmsWidgetConfigureActivity.this, mAppWidgetId)));
    }

}
