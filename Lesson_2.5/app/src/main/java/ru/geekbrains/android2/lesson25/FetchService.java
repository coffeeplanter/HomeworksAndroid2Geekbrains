package ru.geekbrains.android2.lesson25;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchService extends Service {

    public static final String TAG = "FetchService";

    public FetchService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand, сервис запущен");

        final ArrayList<String> mPhotoUrls = intent.getStringArrayListExtra(MainActivity.PARAM_URLS);
        final PendingIntent pendingIntent = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);

        new Thread(new Runnable() {
            @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
            @Override
            public void run() {
                URL url;
                HttpURLConnection urlConnection;
                InputStream in;
                File storageDir;
                String fileName;
                FileOutputStream outputStream;
                try {
                    // Получение внешнего приватного хранилища
                    storageDir = new File(getApplicationContext().getExternalFilesDir(null).getPath() + "/images");

                    if (!isExternalStorageWritable()) {
                        Log.d(TAG, "Ошибка: внешнее хранилище недоступно для записи");
                        return;
                    }
                    if (storageDir.exists()) {
                        clearDirectory(storageDir);
                    }
                    else {
                        storageDir.mkdirs();
                    }
                    Log.d(TAG, "Папка для сохранения файлов: " + storageDir.toString());

                    // Загрузка изображений
                    for (String urlString : mPhotoUrls) {
                        url = new URL(urlString);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        in = new BufferedInputStream(urlConnection.getInputStream());
                        int responseCode = urlConnection.getResponseCode();
                        if (responseCode != HttpURLConnection.HTTP_OK) {
                            Log.d(TAG, "Ошибка HTTP: " + responseCode + " при обращении по адресу: " + url);
                        } else {
                            Bitmap bitmap = BitmapFactory.decodeStream(in);
                            fileName = url.getPath();
                            File imageFile = new File(storageDir + "/" + fileName.substring(fileName.lastIndexOf('/') + 1));
                            outputStream = new FileOutputStream(imageFile, false);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                            Log.d(TAG, "Файл сохранён: " + imageFile.toString());
                            outputStream.flush();
                            outputStream.close();
                        }
                        in.close();
                        urlConnection.disconnect();
                    }

                    // Уведомление основной активности или отправка уведомления в строку состояния
                    if (MainActivity.isActive) {
                        pendingIntent.send(MainActivity.SERVICE_FINISHED_CODE);
                    } else {
                        makeNotification();
                    }

                    Log.d(TAG, "Завершение потока сервиса и успешная остановка работы сервиса");

                } catch (Exception e) {
                    Log.d(TAG, "Ошибка во время загрузки изображений из Интернета", e);
                    e.printStackTrace();
                }
            }
        }).start();

        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return null;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void clearDirectory(File path) {
        if (path.isDirectory())
            for (File child : path.listFiles()) {
                child.delete();
            }
    }

    /**
     * Метод для показа уведомления об окончании загрузки файлов
     */
    private void makeNotification() {

        Context context = getApplicationContext();

        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_file_download_white_24dp)
                .setTicker("Загрузка изображений завершена")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Загрузка завершена")
                .setContentText("Нажмите, чтобы просмотреть загруженные снимки");

        Notification notification = builder.build();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(1, notification);

    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

}
