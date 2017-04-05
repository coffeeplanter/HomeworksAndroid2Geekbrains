package ru.geekbrains.android2.lesson27;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Класс активити для удобства установки виджета
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Виджет установлен", Toast.LENGTH_SHORT).show();
        finish();
    }

}
