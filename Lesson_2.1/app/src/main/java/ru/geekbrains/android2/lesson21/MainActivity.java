package ru.geekbrains.android2.lesson21;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setSupportActionBar(toolbar);
        initViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle); // DrawerLayout.setDrawerListener() is depreciated
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        Для сравнения сделал popup-меню и контекстное.
//        Popup не очень для смотрится для моего приложения на моём устройстве (API 19). Проверьте сами.
//        Таким образом, его преимущества над контекстным для меня неочевидны.
//
//        textView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                popupMenu.show();
//                return true;
//            }
//        });

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
    }

    private void initViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TabFragment.newInstance(getString(R.string.tab_one_content)), getString(R.string.tab_one_title));
        adapter.addFragment(TabFragment.newInstance(getString(R.string.tab_two_content)), getString(R.string.tab_two_title));
        adapter.addFragment(TabFragment.newInstance(getString(R.string.tab_three_content)), getString(R.string.tab_three_title));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menuClickHandler(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        menuClickHandler(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.text_view) {
            getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        menuClickHandler(item.getItemId());
        return super.onContextItemSelected(item);
    }

    void menuClickHandler(int id) {
        String textToCopy;
        String label;
        switch (id) {
            case R.id.nav_copy_all:
                textToCopy = getString(R.string.about_text);
                label = getString(R.string.copying);
                copyToClipboard(label, textToCopy);
                break;
            case R.id.nav_copy_email:
                textToCopy = getString(R.string.email);
                label = getString(R.string.copying);
                copyToClipboard(label, textToCopy);
                break;
            case R.id.nav_copy_github:
                textToCopy = getString(R.string.github);
                label = getString(R.string.copying);
                copyToClipboard(label, textToCopy);
                break;
            case R.id.nav_sms_email:
                textToCopy = getString(R.string.email);
                sendSMS(textToCopy);
                break;
            case R.id.nav_sms_github:
                textToCopy = getString(R.string.github);
                sendSMS(textToCopy);
                break;
            case R.id.nav_open_second_activity:
                Intent activityIntent = new Intent(this, SecondActivity.class);
                startActivity(activityIntent);
                break;
        }
    }

    private void copyToClipboard(String label, String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, textToCopy);
        clipboard.setPrimaryClip(clip);
        Snackbar.make(drawer, R.string.copying_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void sendSMS(String textToCopy) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("sms_body", textToCopy);
        startActivity(smsIntent);
        Snackbar.make(drawer, R.string.sms_sent, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

}
