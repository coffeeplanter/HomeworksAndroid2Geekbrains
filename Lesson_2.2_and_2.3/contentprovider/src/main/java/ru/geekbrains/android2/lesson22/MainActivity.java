package ru.geekbrains.android2.lesson22;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.geekbrains.android2.lesson22.cryption.Crypter;
import ru.geekbrains.android2.lesson22.database.DBHelper;

public class MainActivity extends AppCompatActivity {

    RelativeLayout rootLayout;
    EditText editKey;
    EditText editValue;
    Button buttonEncrypt, buttonDecrypt;
    Button buttonAddSharedPrefs, buttonGetSharedPrefs, buttonDeleteSharedPrefs;
    Button buttonAddDB, buttonGetDB, buttonDeleteDB;
    DBHelper dbHelper;
    Button buttonMakePhoto, buttonSaveToInt, buttonLoadFromInt, buttonDeleteFromInt,
            buttonSaveToExt, buttonLoadFromExt, buttonDeleteFromExt;
    ImageView imageView;
    String currentPhotoPath;
    byte[] key = {37,41,16,97,19,72,24,63,43,52,31,75,93,117,103,123}; // Ключ для шифрования AES

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        dbHelper = new DBHelper(this, "prefsDB", null, 1);
        initListeners();
    }

    private void initViews() {
        rootLayout = (RelativeLayout) findViewById(R.id.activity_main);
        editKey = (EditText) findViewById(R.id.editKey);
        editValue = (EditText) findViewById(R.id.editValue);
        buttonEncrypt = (Button) findViewById(R.id.buttonEncrypt);
        buttonDecrypt = (Button) findViewById(R.id.buttonDecrypt);
        buttonAddSharedPrefs = (Button) findViewById(R.id.buttonAddSharedPrefs);
        buttonGetSharedPrefs = (Button) findViewById(R.id.buttonGetSharedPrefs);
        buttonDeleteSharedPrefs = (Button) findViewById(R.id.buttonDeleteSharedPrefs);
        buttonAddDB = (Button) findViewById(R.id.buttonAddDB);
        buttonGetDB = (Button) findViewById(R.id.buttonGetDB);
        buttonDeleteDB = (Button) findViewById(R.id.buttonDeleteDB);
        buttonMakePhoto = (Button) findViewById(R.id.buttonMakePhoto);
        buttonSaveToInt = (Button) findViewById(R.id.buttonSaveToInternal);
        buttonLoadFromInt = (Button) findViewById(R.id.buttonLoadFromInternal);
        buttonDeleteFromInt = (Button) findViewById(R.id.buttonDeleteFromInternal);
        buttonSaveToExt = (Button) findViewById(R.id.buttonSaveToExternal);
        buttonLoadFromExt = (Button) findViewById(R.id.buttonLoadFromExternal);
        buttonDeleteFromExt = (Button) findViewById(R.id.buttonDeleteFromExternal);
        imageView = (ImageView) findViewById(R.id.imageViewPhoto);
    }

    private void initListeners() {

        buttonEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String prefValue = editValue.getText().toString();
                if (!prefValue.equals("")) {
                    Crypter crypter = new Crypter(key);
                    editValue.setText(crypter.encrypt(prefValue));
                    showMessage(R.string.string_encrypted, 2);
                }
                else {
                    showMessage(R.string.string_enc_empty, 2);
                }
            }
        });

        buttonDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String prefValue = editValue.getText().toString();
                if (!prefValue.equals("")) {
                    Crypter crypter = new Crypter(key);
                    editValue.setText(crypter.decrypt(prefValue));
                    showMessage(R.string.string_decrypted, 2);
                }
                else {
                    showMessage(R.string.string_dec_empty, 2);
                }
            }
        });

        buttonAddSharedPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (!editKey.getText().toString().equals("") && !editValue.getText().toString().equals("")) {
                    SharedPreferences options = MainActivity.this.getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = options.edit();
                    editor.putString(editKey.getText().toString(), editValue.getText().toString());
                    editor.apply();
                    showMessage(R.string.sh_prefs_saved, 2);
                }
                else {
                    showMessage(R.string.sh_prefs_incorrect, 2);
                }
            }
        });

        buttonGetSharedPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String prefKey = editKey.getText().toString();
                if (!prefKey.equals("")) {
                    SharedPreferences options = MainActivity.this.getPreferences(MODE_PRIVATE);
                    if (options.contains(prefKey)) {
                        String prefValue = options.getString(prefKey, "");
                        editValue.setText(prefValue);
                        showMessage(getString(R.string.sh_prefs_read_message, prefKey), 2);
                    }
                    else {
                        editValue.setText("");
                        showMessage(R.string.sh_prefs_read_error_message, 2);
                    }
                }
                else {
                    showMessage(R.string.sh_prefs_read_error_empty_message, 2);
                }
            }
        });

        buttonDeleteSharedPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String prefKey = editKey.getText().toString();
                if (!prefKey.equals("")) {
                    SharedPreferences options = MainActivity.this.getPreferences(MODE_PRIVATE);
                    if (options.contains(prefKey)) {
                        SharedPreferences.Editor editor = options.edit();
                        editor.remove(prefKey);
                        editor.apply();
                        editValue.setText("");
                        editKey.setText("");
                        editKey.requestFocus();
                        showMessage(getString(R.string.sh_prefs_delete_message, prefKey), 2);
                    }
                    else {
                        editValue.setText("");
                        showMessage(R.string.sh_prefs_delete_error_message, 2);
                    }
                }
                else {
                    editValue.setText("");
                    showMessage(R.string.sh_prefs_delete_error_message, 2);
                }
            }
        });

        buttonAddDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                if (!editKey.getText().toString().equals("") && !editValue.getText().toString().equals("")) {
                    ContentValues cv = new ContentValues();
                    cv.put("key", editKey.getText().toString());
                    cv.put("value", editValue.getText().toString());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.replace("preferences", null, cv);
                    dbHelper.close();
                    showMessage(R.string.bd_add_message, 2);
                }
                else {
                    showMessage(R.string.sh_prefs_incorrect, 2);
                }
            }
        });

        buttonGetDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String prefKey = editKey.getText().toString();
                if (!prefKey.equals("")) {
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    Cursor cursor = db.query("preferences", new String[]{"value"}, "key = ?", new String[]{prefKey}, null, null,null);
                    if (cursor.moveToFirst()) {
                        editValue.setText(cursor.getString(cursor.getColumnIndex("value")));
                        showMessage(getString(R.string.bd_read_message, prefKey), 2);
                    }
                    else {
                        editValue.setText("");
                        showMessage(R.string.bd_read_error_empty_message, 2);
                    }
                    cursor.close();
                    dbHelper.close();
                }
                else {
                    showMessage(R.string.sh_prefs_read_error_empty_message, 2);
                }
            }
        });

        buttonDeleteDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String prefKey = editKey.getText().toString();
                if (!prefKey.equals("")) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    if (db.delete("preferences", "key = ?", new String[]{prefKey}) != 0) {
                        editValue.setText("");
                        editKey.setText("");
                        editKey.requestFocus();
                        showMessage(getString(R.string.bd_delete_message, prefKey), 2);
                    }
                    else {
                        editValue.setText("");
                        showMessage(R.string.bd_read_error_empty_message, 2);
                    }
                    dbHelper.close();
                }
                else {
                    showMessage(R.string.bd_delete_error_message, 2);
                }
            }
        });

        buttonMakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        buttonSaveToInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream outputStream = null;
                if (imageView.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    try {
                        outputStream = openFileOutput("image.jpg", Context.MODE_PRIVATE);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        showMessage(R.string.photo_save_message, 2);
                    } catch (Exception e) {
                        showMessage(R.string.photo_save_error_message, 2);
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outputStream != null) {
                                outputStream.flush();
                                outputStream.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    showMessage(R.string.photo_save_error_no_image_message, 2);
                }
            }
        });

        buttonLoadFromInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File file = new File(getApplicationContext().getFilesDir(), "image.jpg");
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        imageView.setImageBitmap(bitmap);
                        showMessage(R.string.photo_load_message, 2);
                    }
                    else {
                        showMessage(R.string.photo_load_error_no_file_message, 2);
                    }
                }
                catch (Exception e) {
                    showMessage(R.string.photo_load_error_message, 2);
                    e.printStackTrace();
                }
            }
        });

        buttonDeleteFromInt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getApplicationContext().getFilesDir(), "image.jpg");
                if (file.exists()) {
                    if (file.delete()) {
                        imageView.setImageBitmap(null);
                        showMessage(R.string.photo_delete_message, 2);
                    }
                }
                else {
                    showMessage(R.string.photo_load_error_no_file_message, 2);
                }
            }
        });

        buttonSaveToExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream outputStream = null;
                if (imageView.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    try {
                        //noinspection ConstantConditions
                        File dir = new File(getApplicationContext().getExternalFilesDir(null).getPath());
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, "image.jpg");
                        if (!isExternalStorageWritable()) {
                            showMessage(R.string.ext_stor_no_write_access_message, 2);
                            return;
                        }
                        outputStream = new FileOutputStream(file, false);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        showMessage(R.string.photo_save_message, 2);
                    } catch (Exception e) {
                        showMessage(R.string.photo_save_error_message, 2);
                        e.printStackTrace();
                    } finally {
                        try {
                            if (outputStream != null) {
                                outputStream.flush();
                                outputStream.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    showMessage(R.string.photo_save_error_no_image_message, 2);
                }
            }
        });

        buttonLoadFromExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File file = new File(getApplicationContext().getExternalFilesDir(null), "image.jpg");
                    if (!isExternalStorageReadable()) {
                        showMessage(R.string.ext_stor_no_read_access_message, 2);
                        return;
                    }
                    if (file.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                        imageView.setImageBitmap(bitmap);
                        showMessage(R.string.photo_load_message, 2);
                    }
                    else {
                        showMessage(R.string.photo_load_error_no_file_message, 2);
                    }
                }
                catch (Exception e) {
                    showMessage(R.string.photo_load_error_message, 2);
                    e.printStackTrace();
                }
            }
        });

        buttonDeleteFromExt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getApplicationContext().getExternalFilesDir(null), "image.jpg");
                if (!isExternalStorageWritable()) {
                    showMessage(R.string.ext_stor_no_write_access_message, 2);
                    return;
                }
                if (file.exists()) {
                    if (file.delete()) {
                        imageView.setImageBitmap(null);
                        showMessage(R.string.photo_delete_message, 2);
                    }
                }
                else {
                    showMessage(R.string.photo_load_error_no_file_message, 2);
                }
            }
        });

    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) || Environment.MEDIA_MOUNTED.equals(state));
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Убеждаемся, что камера доступна
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Генерация имени файла изображения
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/Lesson22");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,  /* префикс */
                ".jpg",         /* суффикс */
                storageDir      /* папка */
        );

        // Путь для загрузки изображения из файла
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            setPic();
            showMessage(R.string.photo_got_message, 2);
        }
    }

    private void setPic() {
        // Получаем размеры ImageView
        int targetW = imageView.getWidth();
        int targetH = targetW;

        // Получаем размеры изображения в созданном файле
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Определение коэффициента уменьшения
        int scaleFactor = Math.max(photoW/targetW, photoH/targetH);

        // Загрузить изображение из только что созданного файла
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        // Вырезать квадратную область и повернуть
        if (bitmap.getWidth() > bitmap.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight(),
                    matrix,
                    true
            );
        }
        else {
            bitmap = Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }

        // Загрузить в ImageView
        imageView.setImageBitmap(bitmap);
    }

    private void showMessage(String msg, int type) {
        switch (type) {
            case 1:
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();;
                return;
            case 2:
                Snackbar.make(rootLayout, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
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
                Snackbar.make(rootLayout, msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                return;
            default:
        }
    }

    private void hideKeyboard() {
        View view = MainActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
