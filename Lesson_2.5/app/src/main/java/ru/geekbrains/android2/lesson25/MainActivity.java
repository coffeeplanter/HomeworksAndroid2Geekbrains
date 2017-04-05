package ru.geekbrains.android2.lesson25;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс основной активности
 */
public class MainActivity extends AppCompatActivity {

     // Авгосгенерированные элементы управления

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private static final String TAG = "MainActivity";
    public static final int SERVICE_FINISHED_CODE = 100;
    public final static String PARAM_PINTENT = "pendingIntent";
    public final static String PARAM_URLS = "urls";

    private Button mStartServiceButton;
    private RecyclerView mPhotoGallery;
    private List<File> mPhotos;
    private List<String> mPhotoUrls;
    private PhotoAdapter mPhotoAdapter;

    static boolean isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        mVisible = true;

        initViews();
        initListeners();
        initData();

        // Установка сетки таблицы в зависимости от ориентации
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mPhotoGallery.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else {
            mPhotoGallery.setLayoutManager(new GridLayoutManager(this, 3));
        }

        mPhotoGallery.setAdapter(mPhotoAdapter);

    }

    private void initViews() {
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mStartServiceButton = (Button) findViewById(R.id.start_service_button);
        mPhotoGallery = (RecyclerView) findViewById(R.id.photo_gallery);
    }

    private void initData() {
        mPhotos = new ArrayList<>();
        mPhotoAdapter = new PhotoAdapter(mPhotos, this);
        mPhotoUrls = new ArrayList<>();
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/131711/32775641.3e/0_babc5_7348972c_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/57110/32775641.3f/0_bac27_8bb2488a_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/101435/32775641.3f/0_babd1_82a67a05_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/67777/32775641.3c/0_b7f3f_43edf89_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/44085/32775641.40/0_bb369_13fa0de9_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/45190/32775641.3e/0_b80c0_acf31dd1_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/47501/32775641.3d/0_b80bb_1a491b34_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/5636/32775641.3d/0_b80a4_334e4113_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/66316/32775641.3c/0_b7f50_bf1f9bdd_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/66316/32775641.3c/0_b7f50_bf1f9bdd_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/64973/32775641.3c/0_b7f45_b3fc52c0_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/28001/32775641.3e/0_b80cf_7dcf3dbc_L.jpg");
        mPhotoUrls.add("https://img-fotki.yandex.ru/get/131711/32775641.3e/0_babca_55c4d2c_L.jpg");

    }

    private void initListeners() {

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        mStartServiceButton.setOnTouchListener(mDelayHideTouchListener);
        mStartServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Запуск сервиса FetchService");
                Intent intent = new Intent(MainActivity.this, FetchService.class);
                intent.putStringArrayListExtra(PARAM_URLS, (ArrayList<String>) mPhotoUrls);
                PendingIntent pendingIntent = createPendingResult(10, new Intent(), 0);
                intent.putExtra(PARAM_PINTENT, pendingIntent);
                startService(intent);
            }
        });

        mPhotoGallery.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN && rv.findChildViewUnder(e.getX(), e.getY()) == null) {
                    toggle();
                    return true;
                }
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

    }

    @SuppressWarnings("ConstantConditions")
    private void updatePhotoFilesList() {
        // Готовим список файлов
        try {
            File storageDir = new File(getApplicationContext().getExternalFilesDir(null).getPath() + "/images");
            mPhotos.clear();
            mPhotos.addAll(new ArrayList<>(Arrays.asList(storageDir.listFiles())));
            if (!isExternalStorageReadable()) {
                showMessage("Внешнее хранилище недоступно для чтения", 2);
                Log.d(TAG, "Ошибка: внешнее хранилище недоступно для чтения");
                return;
            }
            if (storageDir.exists()) {
                File[] array = storageDir.listFiles();
                if ((array != null) && (array.length != 0)) {
                    mPhotos.clear();
                    mPhotos.addAll(new ArrayList<>(Arrays.asList(array)));
                    Log.d(TAG, "Список изображений обновлён");
                }
            }
            else {
                showMessage("Загруженные изображения отсутствуют", 2);
                Log.d(TAG, "Ошибка: загруженные изображения отсутствуют");
            }
        }
        catch (Exception e) {
            showMessage("Ошибка во время загрузки изображений из памяти устройства", 2);
            Log.d(TAG, "Ошибка во время загрузки изображений из памяти устройства", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
        Log.d(TAG, "onStart");
        updatePhotoFilesList();
        mPhotoAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == SERVICE_FINISHED_CODE) {
            Log.d(TAG, "Сервис завершил работу");
            updatePhotoFilesList();
            mPhotoAdapter.notifyDataSetChanged();
            Toast.makeText(this, R.string.files_updated_toast, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(500);
    }

    /**
     * Методы управления сокрытием элементов интерфейса активности
     */

    void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
            delayedHide(5000);
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    /**
     * Методы показа сообщений
     */

    private void showMessage(String msg, int type) {
        switch (type) {
            case 1:
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();;
                return;
            case 2:
                Snackbar.make(mContentView, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                return;
            default:
        }
    }

    private void showMessage(int msgRes, int type) {
        String msg = getString(msgRes);
        switch (type) {
            case 1:
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();;
                return;
            case 2:
                Snackbar.make(mContentView, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                return;
            default:
        }
    }

    /**
     * Методы проверки доступности внешнего приватного хранилища
     */

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) || Environment.MEDIA_MOUNTED.equals(state));
    }

}
