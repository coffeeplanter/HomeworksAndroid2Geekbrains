package ru.geekbrains.android2.lesson24;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ImageView downloadedImage;
    private Toolbar toolbar;
    private MenuItem menuItem;
    private String imageUrl;
    private OnCompleteListener onCompleteListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setSupportActionBar(toolbar);
        imageUrl = getString(R.string.image_url);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null) {
                    downloadedImage.setImageBitmap(bitmap);
                    fab.setVisibility(View.INVISIBLE);
                    menuItem.setVisible(false);
                }
            }
        });

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        downloadedImage = (ImageView) findViewById(R.id.downloaded_image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menuItem = menu.findItem(R.id.action_download);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {
            getImage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setOnCompleteListener(OnCompleteListener l) {
        if (l != null) {
            onCompleteListener = l;
        }
    }

    private void getImage() {
        new ImageDownloader(onCompleteListener).execute(imageUrl);
        Snackbar.make(toolbar, R.string.download_started, Snackbar.LENGTH_LONG).setAction(R.string.download_started, null).show();
    }

}
