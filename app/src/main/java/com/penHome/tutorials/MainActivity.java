package com.penHome.tutorials;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import android.widget.Toast;


import com.penHome.tutorials.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);
    }

    Fragment fragment = null;

    DrawerLayout drawerLayout;
    WebView mWebView;
    private Menu optionsMenu;
    Toolbar toolbar;
    NavigationView navigationView;



    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;

    final String wifi="file:///android_asset/wifi.html";
    final String help="https://huntmix.ru/help.html";
    final String hid="file:///android_asset/hid.html";
    final String fish="file:///android_asset/fish.html";
    final String mitm="file:///android_asset/mitm.html";
    final String brute="file:///android_asset/brute.html";
    final String nh="file:///android_asset/nethunter.html";
    final String msf="file:///android_asset/msf.html";
    final String shodan="file:///android_asset/shodan.html";
    final String sites="file:///android_asset/sites.html";
    final String anon="file:///android_asset/anon.html";
    final String nice="https://huntmix.ru";
    final String nice2="https://huntmix.ru/rekv.html";
    final String stat="https://huntmix.ru/stat.html";
    final String apps="https://huntmix.ru/apps2.html";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstRun", true);

        if (isFirstRun) {
            new AlertDialog.Builder(this)
                    .setTitle("Важно")
                    .setMessage("Внимание, приложение использует память для скачивания chroot. Если вы не будете пользоваться этой функцией, вы можете отклонить это разрешение, пользуясь этим приложением вы соглашаетесь с 'Политикой конфиденциальности'! Если вам понравилось приложение не забудьте оценить его или поддержать автора! Удачного пентеста!")
                    .setPositiveButton("Хорошо", null)
                    .setNegativeButton("Выйти", new DialogInterface.OnClickListener() {
                        final public void onClick(DialogInterface arg0, int arg1) {
                            MainActivity.super.onBackPressed();
                        }
                    }).create().show();
        }


        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isFirstRun", false).commit();

        viewInit();

        setTitle("WiFi - Главная");

        mWebView.loadUrl(wifi);

        setMySwipeRefreshLayout();

        setSupportActionBar(toolbar);





        setActionBarToogle();

        //setLocationPermission();





        webSettings();


    }

    void setRTL(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }



    final void setMySwipeRefreshLayout(){
        mySwipeRefreshLayout = findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    final public void onRefresh() {
                        mWebView.reload();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }





    final void setActionBarToogle(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }






    @Override
    final public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebViewFragment inside Fragment
            // Use RESULT_OK only if you're implementing WebViewFragment inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        else
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }


    final void viewInit(){
        drawerLayout = findViewById(R.id.drawer_layout);
        mWebView = findViewById(R.id.mWebView);
        navigationView =  findViewById(R.id.nav_view);
        toolbar =  findViewById(R.id.toolbar);



    }

     void webSettings(){
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDescription,
                                        String mimetype, long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(
                        DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                String fileName = URLUtil.guessFileName(url,contentDescription,".apk");

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName);

                DownloadManager dManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                dManager.enqueue(request);

                Toasty.info(getApplicationContext(), R.string.download_info, Toast.LENGTH_SHORT, true).show();
            }
        });

        mWebView.setWebViewClient(new WebViewClient()
        {

            public void onReceivedError(WebView mWebView, int i, String s, String d1)
            {
                Toasty.error(getApplicationContext(),"Упс, это онлайн страница, а ты без интернета :)").show();
                mWebView.loadUrl("file:///android_asset/error.html");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);
                boolean d = false;

            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                //opening link external browser
                /*if(!url.contains("android_asset")){
                    view.setWebViewClient(null);
                } else {
                    view.setWebViewClient(new WebViewClient());
                }*/

                if(url.contains("tester.huntmix.ru") || url.contains("play.google.com") || url.contains("donationalerts.com") || url.contains("4pda.ru") || url.contains("t.me") || url.contains("qiwi.com")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    return true;
                }
                else if(url.startsWith("mailto")){
                    handleMailToLink(url);
                    return true;
                }


                view.loadUrl(url);
                return true;
            }

        });


        mWebView.setWebChromeClient(new WebChromeClient(){

            public Bitmap getDefaultVideoPoster()
            {
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
            }

            public void onHideCustomView()
            {
                ((FrameLayout)getWindow().getDecorView()).removeView(mCustomView);
                mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
                setRequestedOrientation(mOriginalOrientation);
                mCustomViewCallback.onCustomViewHidden();
                mCustomViewCallback = null;
            }

            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
            {
                if (mCustomView != null)
                {
                    onHideCustomView();
                    return;
                }
                mCustomView = paramView;
                mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                mOriginalOrientation = getRequestedOrientation();
                mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)getWindow().getDecorView()).addView(mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846);
            }


            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            final protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    Toast.makeText(getApplicationContext() , "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    //eski---- >   Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }



            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        WebSettings webSettings = mWebView.getSettings();

        webSettings.setDomStorageEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.getSaveFormData();
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(true);
       // webSettings.setSupportMultipleWindows(true); //?a href problem
        webSettings.getJavaScriptEnabled();
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setGeolocationEnabled(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setLoadsImagesAutomatically(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
       // mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
      //  webSettings.setJavaScriptCanOpenWindowsAutomatically(false); //(popup)
    }





    // Custom method to handle web view mailto link
    protected void handleMailToLink(String url){
        // Initialize a new intent which action is send
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        // For only email app handle this intent
        intent.setData(Uri.parse("mailto:"));

        String mString="";
        // Extract the email address from mailto url
        String to = url.split("[:?]")[1];
        if(!TextUtils.isEmpty(to)){
            String[] toArray = to.split(";");
            // Put the primary email addresses array into intent
            intent.putExtra(Intent.EXTRA_EMAIL,toArray);
            mString += ("TO : " + to);
        }

        // Extract the cc
        if(url.contains("cc=")){
            String cc = url.split("cc=")[1];
            if(!TextUtils.isEmpty(cc)){
                //cc = cc.split("&")[0];
                cc = cc.split("&")[0];
                String[] ccArray = cc.split(";");
                // Put the cc email addresses array into intent
                intent.putExtra(Intent.EXTRA_CC,ccArray);
                mString += ("\nCC : " + cc );
            }
        }else {
            mString += ("\n" + "No CC");
        }

        // Extract the bcc
        if(url.contains("bcc=")){
            String bcc = url.split("bcc=")[1];
            if(!TextUtils.isEmpty(bcc)){
                //cc = cc.split("&")[0];
                bcc = bcc.split("&")[0];
                String[] bccArray = bcc.split(";");
                // Put the bcc email addresses array into intent
                intent.putExtra(Intent.EXTRA_BCC,bccArray);
                mString += ("\nBCC : " + bcc );
            }
        }else {
            mString+=("\n" + "No BCC");
        }

        // Extract the subject
        if(url.contains("subject=")){
            String subject = url.split("subject=")[1];
            if(!TextUtils.isEmpty(subject)){
                subject = subject.split("&")[0];
                // Encode the subject
                try{
                    subject = URLDecoder.decode(subject,"UTF-8");
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                // Put the mail subject into intent
                intent.putExtra(Intent.EXTRA_SUBJECT,subject);
                mString+=("\nSUBJECT : " + subject );
            }
        }else {
            mString+=("\n" + "No SUBJECT");
        }

        // Extract the body
        if(url.contains("body=")){
            String body = url.split("body=")[1];
            if(!TextUtils.isEmpty(body)){
                body = body.split("&")[0];
                // Encode the body text
                try{
                    body = URLDecoder.decode(body,"UTF-8");
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                // Put the mail body into intent
                intent.putExtra(Intent.EXTRA_TEXT,body);
                mString+=("\nBODY : " + body );
            }
        }else {
            mString+=("\n" + "No BODY");
        }

        // Email address not null or empty
        if(!TextUtils.isEmpty(to)){
            if(intent.resolveActivity(getPackageManager())!=null){
                // Finally, open the mail client activity
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(),"Нет почтового клиента..",Toast.LENGTH_SHORT).show();
            }
        }

    }


    protected void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    123
                            );

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            123
                    );
                }
            }else {
                // Permission already granted
            }
        }
    }

    @Override
    public void onBackPressed(){
        if(getSupportActionBar().getTitle().equals("Wifi - Главная")){
            setTitle("Wifi - Главная");


            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(wifi);
        }
        else if(mWebView.canGoBack())
            mWebView.goBack();
        else{
            new AlertDialog.Builder(this)
                    .setTitle("Выйти")
                    .setMessage("Ты действительно хочешь выйти из приложения?")
                    .setNegativeButton("Нет!", null)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        final public void onClick(DialogInterface arg0, int arg1) {
                            MainActivity.super.onBackPressed();
                        }
                    }).create().show();
        }
    }




    final public void copyToPanel(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Скопировано", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    final void share(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.share));
        startActivity(Intent.createChooser(sharingIntent, "Отправить ссылку"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.homepage_actions, menu);
        getMenuInflater().inflate(R.menu.main, optionsMenu);
        return super.onCreateOptionsMenu(menu);
    }

    final void setFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.sites) {
            fragment = null;
            setTitle("Сайты");
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(sites);
        } else if (id == R.id.wifi) {
            fragment = null;
            setTitle("WiFi - Главная");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(wifi);

        } else if (id == R.id.anon) {
            fragment = null;
            setTitle("Анонимность");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(anon);


        } else if (id == R.id.nh) {
            fragment = null;
            setTitle("NetHunter");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(nh);

        } else if (id == R.id.hid) {
            fragment = null;
            setTitle("HID атаки");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(hid);

        } else if (id == R.id.mitm) {
            fragment = null;
            setTitle("MITM атаки");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(mitm);
        } else if (id == R.id.brute) {
            fragment = null;
            setTitle("Брутфорс тестирование");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(brute);
        } else if (id == R.id.msf) {
            fragment = null;
            setTitle("Metasploit - основы");mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(msf);
        } else if (id == R.id.shodan) {
            fragment = null;
            setTitle("Shodan");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(shodan);
        } else if (id == R.id.nice) {
            fragment = null;
            setTitle("Полезное");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(nice);
        } else if (id == R.id.nice2) {
            fragment = null;
            setTitle("Поддержать проект");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(nice2);
        } else if (id == R.id.help) {
            fragment = null;
            setTitle("Обратная связь");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(help);
        } else if (id == R.id.stat) {
            fragment = null;
            setTitle("Статьи");
            mWebView.setVisibility(View.VISIBLE);

            mWebView.loadUrl(stat);
        } else if (id == R.id.apps) {
            fragment = null;
            setTitle("Каталог");
            mWebView.setVisibility(View.VISIBLE);
            checkPermission();

            mWebView.loadUrl(apps);
        } else if (id == R.id.fish) {
            fragment = null;
            setTitle("Социальная инженерия");
            mWebView.setVisibility(View.VISIBLE);
            checkPermission();

            mWebView.loadUrl(fish);
        }


        if (fragment != null) {
           setFragment();
           mWebView.setVisibility(View.GONE);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
