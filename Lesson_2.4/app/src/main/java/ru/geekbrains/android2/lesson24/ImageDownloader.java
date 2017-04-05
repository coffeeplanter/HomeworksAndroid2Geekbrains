package ru.geekbrains.android2.lesson24;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    private OnCompleteListener onCompleteListener;

    ImageDownloader(OnCompleteListener l) {
        super();
        if(l != null) {
            onCompleteListener = l;
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }

    private Bitmap downloadBitmap(String param) {
        URL url;
        try {
            url = new URL(param);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.w("ImageDownloader", "Error " + responseCode + " while retrieving bitmap from " + url);
                return null;
            }
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            in.close();
            urlConnection.disconnect();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(onCompleteListener != null)
            onCompleteListener.onComplete(bitmap);
    }

}
