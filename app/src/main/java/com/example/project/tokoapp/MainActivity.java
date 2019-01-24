package com.example.project.tokoapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appeaser.sublimenavigationviewlibrary.OnNavigationMenuEventListener;
import com.appeaser.sublimenavigationviewlibrary.SublimeBaseMenuItem;
import com.appeaser.sublimenavigationviewlibrary.SublimeMenu;
import com.appeaser.sublimenavigationviewlibrary.SublimeNavigationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import fragment.master.FrgMerchantUser;
import utilities.CircleTransform;
import utilities.PatternEditableBuilder;
import utilities.PrefUtil;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>{

    private PrefUtil pref;
    private SublimeNavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private CharSequence mTitle;
    private DrawerLayout mDrawerLayout;
    private static final int REQUEST_MERCHANT = 7;

    private Toolbar toolbar;
    private SharedPreferences shared;
    private String id, nama, login, email, gender, profile, telp;
    private Integer countToko;
    private ImageView imgProfile, imgBg;
    private TextView txtNama, txtEmail, txtToko;
    private GoogleApiClient mGoogleApiClient;
    private boolean mSignInClicked;
    private boolean shouldLoadHomeFragOnBackPress = true;
    private static int navItemIndex = 0;
    private boolean stat = false;
    private FirebaseAuth mAuth;

    private static final String urlNavHeaderBg = "https://softchrist.com/tokoapp/theme/nav-menu-header-bg.jpg";
    private static final String TAG_BERANDA = "Beranda";
    private static final String TAG_KATEGORI = "Kategori";
    private static final String TAG_WISHLIST = "Wishlist";
    private static final String TAG_NOTIFICATIONS = "Notifikasi";
    private static final String TAG_SETTING = "Setting";
    private static final String TAG_ABOUTUS = "About Us";
    private static final String TAG_PRIVACY = "Privacy Policy";
    public static String CURRENT_TAG = TAG_BERANDA;

    @Override
    protected void onResume() {
        super.onResume();
        pref = new PrefUtil(this);
        try{
            shared  = pref.getUserInfo();
            id      = shared.getString(PrefUtil.ID, null);
            nama    = shared.getString(PrefUtil.NAME, null);
            login   = shared.getString(PrefUtil.LOGIN, null);
            email   = shared.getString(PrefUtil.EMAIL, null);
            gender  = shared.getString(PrefUtil.GENDER, null);
            profile = shared.getString(PrefUtil.PROFILE, null);
            telp    = shared.getString(PrefUtil.TELP, null);
            countToko = shared.getInt(PrefUtil.COUNTTOKO,0);
        }catch (Exception e){e.getMessage();}

        if(login==null){
            loadNavHeader(false, login);
            setUpNavigationView();
        }else if(login.equals(PrefUtil.PHONE)){
            if(telp==null || telp.trim().equals("")){
                loadNavHeader(false, login);
                setUpNavigationView();
            }else{
                loadNavHeader(true, login);
                setUpNavigationViewLogin();
            }
        }else{
            if(email==null || email.trim().equals("")){
                loadNavHeader(false, login);
                setUpNavigationView();
            }else{
                loadNavHeader(true, login);
                setUpNavigationViewLogin();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        pref = new PrefUtil(this);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (SublimeNavigationView) findViewById(R.id.navigation_view);
        navHeader = navigationView.getHeaderView();

        imgProfile  = (ImageView) navHeader.findViewById(R.id.img_profile);
        imgBg       = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        txtNama     = (TextView) navHeader.findViewById(R.id.name);
        txtEmail    = (TextView) navHeader.findViewById(R.id.email);
        txtToko     = (TextView) navHeader.findViewById(R.id.txt_nama_toko);

        try{
            shared  = pref.getUserInfo();
            id      = shared.getString(PrefUtil.ID, null);
            nama    = shared.getString(PrefUtil.NAME, null);
            login   = shared.getString(PrefUtil.LOGIN, null);
            email   = shared.getString(PrefUtil.EMAIL, null);
            gender  = shared.getString(PrefUtil.GENDER, null);
            profile = shared.getString(PrefUtil.PROFILE, null);
            telp    = shared.getString(PrefUtil.TELP, null);
            countToko = shared.getInt(PrefUtil.COUNTTOKO,0);
        }catch (Exception e){e.getMessage();}

        if(login==null){
            loadNavHeader(false, login);
            setUpNavigationView();
            menu();
        }else if(login.equals(PrefUtil.PHONE)){
            if(telp==null || telp.trim().equals("")){
                loadNavHeader(false, login);
                setUpNavigationView();
                menu();
            }else{
                loadNavHeader(true, login);
                setUpNavigationViewLogin();
                menuLogin();
                Toast.makeText(getApplicationContext(), "Welcome, " + telp, Toast.LENGTH_LONG).show();
            }
        }else{
            if(email==null || email.trim().equals("")){
                loadNavHeader(false, login);
                setUpNavigationView();
                menu();
            }else{
                loadNavHeader(true, login);
                setUpNavigationViewLogin();
                menuLogin();
                Toast.makeText(getApplicationContext(), "Welcome, " + nama, Toast.LENGTH_LONG).show();
            }
        }

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_BERANDA;
            loadHomeFragment();
        }

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                .enableAutoManage(MainActivity.this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

    }

    private void loadNavHeader(boolean status, String loginWith) {
        if(status){
            if(loginWith.equals(PrefUtil.PHONE)){
                txtNama.setText(telp);
                txtEmail.setText("");
            }else{
                txtNama.setText(nama);
                txtEmail.setText(email);
            }
            if(countToko.intValue()==0 ){
                txtToko.setText("Anda belum memiliki toko\nBuat toko?");
                new PatternEditableBuilder().
                        addPattern(Pattern.compile("Buat toko?"), Color.CYAN,
                                new PatternEditableBuilder.SpannableClickedListener() {
                                    @Override
                                    public void onSpanClicked(String text) {
                                        Intent i = new Intent(getApplicationContext(), StoreActivity.class);
                                        i.putExtra("tipe","ADD");
                                        startActivityForResult(i, REQUEST_MERCHANT);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        drawer.closeDrawers();
                                    }
                                })
                        .into(txtToko);
            }else{
                txtToko.setText("Kelola toko anda?");
                new PatternEditableBuilder().
                        addPattern(Pattern.compile("Kelola toko anda?"), Color.CYAN,
                                new PatternEditableBuilder.SpannableClickedListener() {
                                    @Override
                                    public void onSpanClicked(String text) {
                                        Intent i = new Intent(getApplicationContext(), FrgMerchantUser.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        drawer.closeDrawers();
                                    }
                                })
                        .into(txtToko);
            }
            Glide.with(this).load(profile)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);
            //if(nama!=null) navigationView.getMenu().getMenuItem(2).setActionView(R.layout.menu_dot);
        }
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgBg);
    }

    private void setUpNavigationViewLogin() {
        navigationView.setNavigationMenuEventListener(new OnNavigationMenuEventListener() {
            @Override
            public boolean onNavigationMenuEvent(Event event, SublimeBaseMenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.beranda:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_BERANDA;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        drawer.closeDrawers();
                        break;
                    case R.id.kategori:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_KATEGORI;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        drawer.closeDrawers();
                        break;
                    case R.id.wishlist:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_WISHLIST;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        drawer.closeDrawers();
                        break;
                    case R.id.notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        drawer.closeDrawers();
                        break;
                    case R.id.signout:
                        if(login.equals(PrefUtil.FB)){
                            LoginManager.getInstance().logOut();
                            FirebaseAuth.getInstance().signOut();
                            stat=true;
                            pref.clear();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }else if(login.equals(PrefUtil.GL)){//google
                            mAuth.signOut();
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    if(status.isSuccess()){
                                        Toast.makeText(MainActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                                        pref.clear();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        finish();
                                    }else {
                                        stat=false;
                                        Toast.makeText(MainActivity.this, "Logout failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }else if(login.equals(PrefUtil.PHONE)){
                            mAuth.signOut();
                            Toast.makeText(MainActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                            pref.clear();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }else{//OTHER
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(MainActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                            pref.clear();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();
                        }
                        return stat;
                    case R.id.menu_profile:
                        startActivity(new Intent(getApplicationContext(), Profil.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        drawer.closeDrawers();
                        return true;
                    case R.id.about_us:
                        CURRENT_TAG = TAG_ABOUTUS;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        drawer.closeDrawers();
                        return true;
                    case R.id.privacy_policy:
                        CURRENT_TAG =TAG_PRIVACY;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        // launch new intent instead of loading fragment
                        //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }
                if (menuItem.isChecked()) {
                    menuItem.setChecked(true);
                } else {
                    menuItem.setChecked(false);
                }
                //menuItem.setChecked(true);
                loadHomeFragment();
                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void setUpNavigationView() {
        navigationView.setNavigationMenuEventListener(new OnNavigationMenuEventListener() {
            @Override
            public boolean onNavigationMenuEvent(Event event, SublimeBaseMenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.beranda:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_BERANDA;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        drawer.closeDrawers();
                        break;
                    case R.id.kategori:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_KATEGORI;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        drawer.closeDrawers();
                        break;
                    case R.id.daftar:
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                        return true;
                    case R.id.signin:
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                        return true;
                    case R.id.about_us:
                        CURRENT_TAG = TAG_ABOUTUS;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        drawer.closeDrawers();
                        return true;
                    case R.id.privacy_policy:
                        CURRENT_TAG = TAG_PRIVACY;
                        getSupportActionBar().setTitle(CURRENT_TAG);
                        // launch new intent instead of loading fragment
                        //startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }
                if (menuItem.isChecked()) {
                    menuItem.setChecked(true);
                } else {
                    menuItem.setChecked(false);
                }
                loadHomeFragment();
                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void loadHomeFragment() {
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        /*Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }*/
        invalidateOptionsMenu();
    }

    /*private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                PhotosFragment photosFragment = new PhotosFragment();
                return photosFragment;
            case 2:
                // movies fragment
                MoviesFragment moviesFragment = new MoviesFragment();
                return moviesFragment;
            case 3:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }*/

    @Override
    public void onConnected(Bundle bundle) {
        mSignInClicked = false;
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(
                this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {
    }

    @Override
    protected void onStart() {
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();*/
        super.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        /*if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }*/
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    }).create().show();

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        if (shouldLoadHomeFragOnBackPress) {
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_BERANDA;
                loadHomeFragment();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(email!=null){
            getMenuInflater().inflate(R.menu.notification, menu);
        }else{
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }
        if (id == R.id.action_login) {
            Toast.makeText(getApplicationContext(), "User Login", Toast.LENGTH_LONG).show();
        }
        if (id == R.id.action_profile) {
            startActivity(new Intent(getApplicationContext(), Profil.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    private void menuLogin(){
        navigationView = (SublimeNavigationView) findViewById(R.id.navigation_view);
        SublimeMenu nav_Menu = navigationView.getMenu();
        nav_Menu.getMenuItem(R.id.beranda).setVisible(true);
        nav_Menu.getMenuItem(R.id.kategori).setVisible(true);
        nav_Menu.getMenuItem(R.id.wishlist).setVisible(true);
        nav_Menu.getMenuItem(R.id.notifications).setVisible(true);
        nav_Menu.getMenuItem(R.id.signout).setVisible(true);

        nav_Menu.getMenuItem(R.id.signin).setVisible(false);
        nav_Menu.getMenuItem(R.id.daftar).setVisible(false);

        nav_Menu.getMenuItem(R.id.separator_item_21).setVisible(true);
        nav_Menu.getGroup(R.id.group_11).setVisible(true);
        nav_Menu.getMenuItem(R.id.setting).setVisible(true);
        nav_Menu.getMenuItem(R.id.menu_profile).setVisible(true);

        nav_Menu.getGroup(R.id.group_21).setVisible(true);
        nav_Menu.getMenuItem(R.id.about_us).setVisible(true);
        nav_Menu.getMenuItem(R.id.privacy_policy).setVisible(true);
    }

    private void menu(){
        navigationView = (SublimeNavigationView) findViewById(R.id.navigation_view);
        SublimeMenu nav_Menu = navigationView.getMenu();
        nav_Menu.getMenuItem(R.id.beranda).setVisible(true);
        nav_Menu.getMenuItem(R.id.kategori).setVisible(true);
        nav_Menu.getMenuItem(R.id.wishlist).setVisible(false);
        nav_Menu.getMenuItem(R.id.notifications).setVisible(false);
        nav_Menu.getMenuItem(R.id.signout).setVisible(false);

        nav_Menu.getMenuItem(R.id.signin).setVisible(true);
        nav_Menu.getMenuItem(R.id.daftar).setVisible(true);

        nav_Menu.getMenuItem(R.id.separator_item_21).setVisible(true);
        nav_Menu.getGroup(R.id.group_11).setVisible(true);
        nav_Menu.getMenuItem(R.id.setting).setVisible(true);
        nav_Menu.getMenuItem(R.id.menu_profile).setVisible(false);

        nav_Menu.getGroup(R.id.group_21).setVisible(true);
        nav_Menu.getMenuItem(R.id.about_us).setVisible(true);
        nav_Menu.getMenuItem(R.id.privacy_policy).setVisible(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MERCHANT && resultCode == Activity.RESULT_OK) {
            countToko++;
            if(countToko.intValue()==0 ){
                txtToko.setText("Anda belum memiliki toko\nBuat toko?");
                new PatternEditableBuilder().
                        addPattern(Pattern.compile("Buat toko?"), Color.CYAN,
                                new PatternEditableBuilder.SpannableClickedListener() {
                                    @Override
                                    public void onSpanClicked(String text) {
                                        Intent i = new Intent(getApplicationContext(), StoreActivity.class);
                                        i.putExtra("tipe","ADD");
                                        startActivityForResult(i, REQUEST_MERCHANT);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        drawer.closeDrawers();
                                    }
                                })
                        .into(txtToko);
            }else{
                txtToko.setText("Kelola toko anda?");
                new PatternEditableBuilder().
                        addPattern(Pattern.compile("Kelola toko anda??"), Color.CYAN,
                                new PatternEditableBuilder.SpannableClickedListener() {
                                    @Override
                                    public void onSpanClicked(String text) {
                                        Intent i = new Intent(getApplicationContext(), FrgMerchantUser.class);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        drawer.closeDrawers();
                                    }
                                })
                        .into(txtToko);
            }
        }
    }
}

/*https://www.androidhive.info/2016/06/android-getting-started-firebase-simple-login-registration-auth/
http://androidbash.com/firebase-classic-email-login-facebook-login-android/
https://github.com/oemilk/firebase/tree/master/app/src/main/java/com/sh/firebase/authentication
https://github.com/Ginowine/android-firebase-authentication/blob/master/app/src/main/java/com/tutorial/authentication/MainActivity.java
http://javasampleapproach.com/android/firebase-authentication-sign-up-sign-in-sign-out-verify-email-android
https://android.jlelse.eu/android-firebase-authentication-with-google-signin-3f878d9b7553
http://javasampleapproach.com/android/firebase-authentication-send-reset-password-email-forgot-password-android*/
