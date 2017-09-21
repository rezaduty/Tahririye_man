package com.rezaduty.chdev.ks.tahririye_man.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rezaduty.chdev.ks.tahririye_man.R;
import com.rezaduty.chdev.ks.tahririye_man.models.Categories;
import com.rezaduty.chdev.ks.tahririye_man.models.CategoryItem;
import com.rezaduty.chdev.ks.tahririye_man.models.SettingsPreferences;
import com.rezaduty.chdev.ks.tahririye_man.models.SourceItem;
import com.rezaduty.chdev.ks.tahririye_man.sources.ISourceView;
import com.rezaduty.chdev.ks.tahririye_man.sources.SourcesPresenter;
import com.rezaduty.chdev.ks.tahririye_man.ui.adapters.CategoryListAdapter;
import com.rezaduty.chdev.ks.tahririye_man.ui.fragments.ArchiveFragment;
import com.rezaduty.chdev.ks.tahririye_man.ui.fragments.FeedsFragment;
import com.rezaduty.chdev.ks.tahririye_man.ui.fragments.ManageSourcesFragment;
import com.rezaduty.chdev.ks.tahririye_man.utils.AnimationUtil;
import com.rezaduty.chdev.ks.tahririye_man.utils.DateUtil;
import com.rezaduty.chdev.ks.tahririye_man.utils.FadeAnimationUtil;
import com.github.clans.fab.FloatingActionMenu;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements ISourceView, FloatingActionMenu.OnMenuToggleListener, AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.text_view_toolbar_title)
    TextView txtToolbarTitle;
    @Bind(R.id.recycler_view_feeds)
    RecyclerView recyclerViewFeeds;
    @Bind(R.id.fab)
    FloatingActionMenu fab;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.main_layout)
    RelativeLayout mainLayout;
    @Bind(R.id.secondary_layout)
    RelativeLayout secondaryLayout;
    @Bind(R.id.material_edit_text_source_name)
    MaterialEditText eTxtSourceName;
    @Bind(R.id.material_edit_text_source_url)
    MaterialEditText eTxtSourceUrl;
    @Bind(R.id.button_category)
    Button btnCategory;
    @Bind(R.id.button_save)
    Button btnSave;
    @Bind(R.id.image_view_category)
    ImageView imgCategory;
    @Bind(R.id.text_view_category)
    TextView txtCategory;
    @Bind(R.id.category_layout)
    LinearLayout categoryLayout;
    @Bind(R.id.spinner_sources)
    Spinner spinnerSources;
    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    int mNavPosition = 0;
    private SourcesPresenter mSourcesPresenter;
    private AnimationUtil mAnimationUtil;
    private DateUtil mDateUtil;
    private SourceItem mSourceItem;
    private boolean mAddFeedStatus = false;
    private ProgressDialog dialog;
    String urlsettextbox;
    // for cut baseurle
    String uuu="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = (View) LayoutInflater.from(this).inflate(R.layout.content_home, null);

        isStoragePermissionGranted();


        dialog = new ProgressDialog(this);
        //initialize settings
        SettingsPreferences.init(HomeActivity.this);

        //set theme
        setActivityTheme();

        setContentView(R.layout.activity_home);

        ButterKnife.bind(HomeActivity.this);

        //set theme of other ui elements
        setUiElementsTheme();

        if (mSourcesPresenter == null) {
            mSourcesPresenter = new SourcesPresenter(HomeActivity.this, HomeActivity.this);
        }

        fab.setOnMenuToggleListener(this);

        setSourcesSpinner();

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        //set the first item enabled true
        navigationView.getMenu().getItem(0).setChecked(true);

        //show changelog if user opens the app for first time
        if (SettingsPreferences.CHANGE_LOG_DIALOG_SHOW) {
            SettingsPreferences.showChangeLog(HomeActivity.this);
        }
    }




    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission","Permission is granted");
                return true;
            } else {

                Log.v("Permission","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission","Permission is granted");
            return true;
        }
    }





    private void setActivityTheme() {
        if (!SettingsPreferences.THEME) {
            setTheme(R.style.DarkAppTheme_NoActionBar);
            getWindow().setBackgroundDrawableResource(R.color.darkColorBackground);
        }
    }

    private void setUiElementsTheme() {
        if (!SettingsPreferences.THEME) {
            fab.setMenuButtonColorNormal(ContextCompat.getColor(HomeActivity.this, R.color.darkColorAccent));
            fab.setMenuButtonColorPressed(ContextCompat.getColor(HomeActivity.this, R.color.darkColorAccent));
            fab.setMenuButtonColorRipple(ContextCompat.getColor(HomeActivity.this, R.color.darkColorAccentDark));
            secondaryLayout.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.darkColorAccent));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;

        if (id == R.id.nav_feeds) {
            if (secondaryLayout.getVisibility() == View.INVISIBLE && mNavPosition != 0) {
                mSourcesPresenter.getSources();
                //new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(spinnerSources, 500);
                spinnerSources.setVisibility(View.VISIBLE);
                fragment = new FeedsFragment().setInstance("all_sources");
                fab.showMenuButton(true);
                txtToolbarTitle.setText("منبع جدید");
                txtToolbarTitle.setVisibility(View.INVISIBLE);
            }
            mNavPosition = 0;
            //new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(txtToolbarTitle, 500);
        } else if (id == R.id.nav_manage_sources) {
            hideSpinnerAndFab();
            fragment = new ManageSourcesFragment();
            showTitle("مدیریت منابع");
            mNavPosition = 1;
        } else if (id == R.id.nav_archive) {
            hideSpinnerAndFab();
            fragment = new ArchiveFragment();
            showTitle("ذخیره شده");
            mNavPosition = 2;
        } else if (id == R.id.nav_settings) {
            runIntent(SettingsActivity.class);
            mNavPosition = 3;
        } else if (id == R.id.nav_about) {
            runIntent(AboutActivity.class);
            mNavPosition = 4;
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void runIntent(final Class resultActivityClass) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HomeActivity.this, resultActivityClass);
                startActivity(intent);
            }
        }, 200);
    }

    private void showTitle(String title) {
        txtToolbarTitle.setText(title);
        if (txtToolbarTitle.getVisibility() != View.VISIBLE) {
            new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(txtToolbarTitle, 500);
        }
    }

    private void hideSpinnerAndFab() {
        if (spinnerSources.getVisibility() != View.INVISIBLE || secondaryLayout.getVisibility() != View.INVISIBLE) {
            new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(spinnerSources, 500);
        }
        //spinnerSources.setVisibility(View.INVISIBLE);
        fab.hideMenuButton(true);
    }

    //set the selected fragment onto the screen(activity)
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.main_layout, fragment);
        fragmentTransaction.commit();
    }

    private void setSourcesSpinner() {
        mSourcesPresenter.getSources();
        spinnerSources.setOnItemSelectedListener(this);
    }

    @Override
    public void onMenuToggle(boolean opened) {
        mAnimationUtil = new AnimationUtil(HomeActivity.this);
        fab.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.setEnabled(true);
            }
        }, 500);
        if (opened) {
            showAddSourceScreen(true);
        } else {
            showAddSourceScreen(false);
        }
    }

    private void showAddSourceScreen(boolean status) {
        enableSecondaryLayout(status);
        if (status) {
            new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(txtToolbarTitle, 500);
            new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(spinnerSources, 500);
            //new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(toolbar, 500);
            if (SettingsPreferences.CIRCULAR_REVEAL) {
                mAnimationUtil.revealAnimation(secondaryLayout, mainLayout);
            } else {
                new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(mainLayout, 500);
                new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(secondaryLayout, 500);
            }
        } else {
            if (mNavPosition == 0) {
                new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(txtToolbarTitle, 500);
                new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(spinnerSources, 500);
            }
            //new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(toolbar, 500);
            if (SettingsPreferences.CIRCULAR_REVEAL) {
                mAnimationUtil.revealAnimationHide(secondaryLayout, mainLayout);
            } else {
                new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(secondaryLayout, 500);
                new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(mainLayout, 500);
            }
        }
        mAddFeedStatus = status;
    }

    @OnClick(R.id.button_save)
    public void saveSource() {
        String sourceName = eTxtSourceName.getText().toString();
        String sourceUrl = eTxtSourceUrl.getText().toString();
        String sourceCategory = txtCategory.getText().toString();
        //Toast.makeText(HomeActivity.this, "name: "+sourceName+", url: "+sourceUrl, Toast.LENGTH_SHORT).show();
        mDateUtil = new DateUtil();
        String date = mDateUtil.getCurrDate();

        mSourceItem = new SourceItem();
        mSourceItem.setSourceName(sourceName);
        mSourceItem.setSourceUrl(sourceUrl);
        mSourceItem.setSourceCategoryName(sourceCategory);
        mSourceItem.setSourceCategoryImgId(new Categories(HomeActivity.this).getDrawableId(sourceCategory));
        mSourceItem.setSourceDateAdded(date);

        mSourcesPresenter.addSource(mSourceItem);
    }

    @OnClick(R.id.button_category)
    public void showCategory() {
        final List<CategoryItem> categoryItems = new Categories(HomeActivity.this).getCategoryItems();

        final MaterialDialog categoryDialog = new MaterialDialog.Builder(HomeActivity.this)
                .titleGravity(GravityEnum.END)
                .contentGravity(GravityEnum.END)
                .title(R.string.add_category)
                .adapter(new CategoryListAdapter(HomeActivity.this, categoryItems),
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                //Toast.makeText(HomeActivity.this, "Clicked item " + which, Toast.LENGTH_SHORT).show();
                                imgCategory.setImageDrawable(categoryItems.get(which).getCategoryImg());
                                txtCategory.setText(categoryItems.get(which).getCategoryName());
                                categoryLayout.setVisibility(View.VISIBLE);
                                dialog.dismiss();
                            }
                        })
                .build();
        categoryDialog.show();
    }

    // spinner Items (select from different sources)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(HomeActivity.this, "Item selected: " + adapterView.getItemAtPosition(i), Toast.LENGTH_SHORT).show();
        if (i == 0) {
            loadFragment(new FeedsFragment().setInstance("all_sources"));
        } else {
            loadFragment(new FeedsFragment().setInstance(adapterView.getItemAtPosition(i).toString()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void enableSecondaryLayout(boolean status) {
        eTxtSourceName.setEnabled(status);
        eTxtSourceUrl.setEnabled(status);
        btnCategory.setEnabled(status);
        btnSave.setEnabled(status);
        //categoryLayout.setVisibility(View.INVISIBLE);
        /*if(status){
            categoryLayout.setVisibility(View.VISIBLE);
        }else {
            categoryLayout.setVisibility(View.INVISIBLE);
        }*/
    }

    @Override
    public void dataSourceSaved(String message) {
        //add newly added items to spinner. Not working currently
        mSourcesPresenter.getSources();

        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
        fab.close(true);
        enableSecondaryLayout(false);
        //If called instantly after save button is clicked, will make the reveal(hide) animation lag a little bit,
        //so run the animation after a certain period of time.
        //mAnimationUtil.revealAnimationHide(secondaryLayout, mainLayout);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(toolbar, 500);
                drawer.setEnabled(true);
                if (SettingsPreferences.CIRCULAR_REVEAL) {
                    mAnimationUtil.revealAnimationHide(secondaryLayout, mainLayout);
                } else {
                    new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(secondaryLayout, 500);
                    new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(mainLayout, 500);
                }
            }
        }, 500);
        mAddFeedStatus = false;
        categoryLayout.setVisibility(View.INVISIBLE);
        clearSourceValues();
    }



    // check url is feed
    public boolean getHtml(String urll,String param) {
        Boolean status;
        status=false;
        try {
            // Build and set timeout values for the request.

            URL url = new URL(urll.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();

            int code = connection.getResponseCode();

            if(code == 200) {
                // Read and store the result line by line then return the entire string.
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder html = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    html.append(line);
                    if (html.toString().contains(param)) {
                        status = true;
                        break;

                    } else {
                        status = false;


                    }
                }
                in.close();
            }else{
                status=false;
            }

        } catch (Exception e) {
            Log.d("not found feed url", e.toString());
        }

        return status;


    }




    String sourcefeedurl;
    public boolean checkfeedbysource(String url){
        Boolean status;
        status=false;
        try {
            Document doc = Jsoup.connect(url).get();
            for (Element head : doc.select("link")) {
                if(head.toString().contains("rss")){
                    sourcefeedurl = head.attr("href").toString();
                    if(!android.util.Patterns.WEB_URL.matcher(sourcefeedurl).matches())
                        status=true;
                    else
                        status=false;
                    break;
                }
                Log.d("OK",head.toString());

            }
        }catch (Exception e){
            Log.e("Error checkfeed",e.toString());
        }
        return status;
    }




        public void MoreFindRss() {
            // extract feed url if is website source
            if(checkfeedbysource(originalText)){
                urlsettextbox=originalText+sourcefeedurl;
            }

        }





    private class CustomTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("لطفا صبر کنید در حال شناسایی فید");
            dialog.show();

        }

        @Override
        protected void onPostExecute(Void param) {
            //Print Toast or open dialog
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            eTxtSourceUrl.setText(urlsettextbox);

        }
        @Override
        protected Void doInBackground(Void... param) {
            //Do some work




            





            // wordpress feed url
            if(getHtml(urlsettextbox+"/feed","<rss") || getHtml(urlsettextbox+"/feed","<?xml")){
                urlsettextbox=urlsettextbox + "/feed";
            }



            // iransamaneh feed url
            else if(getHtml(urlsettextbox+"/fa/rss/allnews","<rss") || getHtml(urlsettextbox+"/fa/rss/allnews","<?xml")){
                urlsettextbox=urlsettextbox + "/fa/rss/allnews";
            }
            //varzesh3
            else if(getHtml(urlsettextbox+"/rss/all","<rss") || getHtml(urlsettextbox+"/rss/all","<?xml")){
                urlsettextbox=urlsettextbox + "/rss/all";
            }
            // varzesh11 feed url
            else if(getHtml(urlsettextbox+"/fa/rss/","<?xml") || getHtml(urlsettextbox+"/fa/rss/","<rss")){
                urlsettextbox=urlsettextbox + "/fa/rss/";
            }
            else if(getHtml(urlsettextbox+"/rss","<rss") || getHtml(urlsettextbox+"/rss","<?xml")){
                urlsettextbox=urlsettextbox + "/rss";
            }



            // presstv
            else if(getHtml(urlsettextbox+"/RSS/MRSS/1","<rss")){
                urlsettextbox=urlsettextbox + "/RSS/MRSS/1";
                Log.d("ok","presstv.ir");
            }

            // cnn
            else if(getHtml(urlsettextbox+"/rss/edition.rss","<rss")){
                urlsettextbox=urlsettextbox + "/rss/edition.rss";
                Log.d("ok","cnn.com");
            }
            // yahoo
            else if(getHtml(urlsettextbox+"/news/rss/","<rss")){
                urlsettextbox=urlsettextbox + "/news/rss/";
                Log.d("ok","yahoo.com");
            }
            // google news
            else if(getHtml(urlsettextbox+"/news?pz=1&cf=all&hl=en&output=rss","<rss")){
                urlsettextbox=urlsettextbox + "/news?pz=1&cf=all&hl=en&output=rss";
                Log.d("ok","google.com");
            }



            // akharinnews
            else if(getHtml(urlsettextbox+"/?option=com_k2&view=itemlist&format=feed&type=rss","<rss")){
                urlsettextbox=urlsettextbox + "/?option=com_k2&view=itemlist&format=feed&type=rss";
                Log.d("ok","akharinnews");
            }
            // http://bbcpersian.net/
            else if(getHtml(urlsettextbox+"/rss.aspx?type=all","<rss")){
                urlsettextbox=urlsettextbox + "/rss.aspx?type=all";
                Log.d("ok","bbcpersian");
            }
            // khabarfarsi
            else if(getHtml(urlsettextbox+"/rss/top","<rss")){
                urlsettextbox=urlsettextbox + "/rss/top";
                Log.d("ok","khabarfarsi");
            }

            // jamejamonline
            else if(getHtml(urlsettextbox+"/RssFeed/01-01-01-118","<rss")){
                urlsettextbox=urlsettextbox + "/RssFeed/01-01-01-118";
                Log.d("ok","jamejamonline");
            }

            // alef
            else if(getHtml(urlsettextbox+"/rss/all.xml","<rss")){
                urlsettextbox=urlsettextbox + "/rss/all.xml";
                Log.d("ok","alef");
            }

            // tasnimnews
            else if(getHtml(urlsettextbox+"/fa/rss/feed/0/7/0/پربازدیدترین?hit=1","<?xml")){
                urlsettextbox=urlsettextbox + "/fa/rss/feed/0/7/0/پربازدیدترین?hit=1";
                Log.d("ok","tasnimnews");
            }

            else if(getHtml(urlsettextbox+"/rss-homepage","<rss") || getHtml(urlsettextbox+"/rss-homepage","<?xml")){
                urlsettextbox=urlsettextbox + "/rss-homepage";
            }

            // wordpress feed url
            else if(getHtml(urlsettextbox+"/rss.xml","<rss") || getHtml(urlsettextbox+"/rss.xml","<?xml")){
                urlsettextbox=urlsettextbox + "/rss.xml";
            }

            // gohugo feed url
            else if(getHtml(urlsettextbox+"/index.xml","<rss") || getHtml(urlsettextbox+"/index.xml","<?xml")){
                urlsettextbox=urlsettextbox + "/rss-homepage";
            }
            else{
                MoreFindRss();
            }
            return null;
        }

    }
    // originalText for search feed by subdirectory website
    String regexp,originalText;
    @Override
    public void dataSourceSaveFailed(String message) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        // for generate correct url
        urlsettextbox=eTxtSourceUrl.getText().toString();
        if (message.equals("name_url_category_empty")) {
            message = "لظفا نام منبع و آدرس آن را به دقت وارد کنید";
            eTxtSourceName.setError("لطفا نام منبع را با دقت وارد کنید");
            eTxtSourceUrl.setError("لطفا آدرس منبع را با دقت وارد کنید");
        } else if (message.equals("name_empty")) {
            message = "نام منبع به صورت خودکار پر شد";
            eTxtSourceName.setText(eTxtSourceUrl.getText());
            eTxtSourceName.setError("لطفا نام منبع را وارد کنید");
        } else if (message.equals("url_empty")) {
            message = "آدرس مبنع نمیتواند خالی باشد";
            eTxtSourceUrl.setError("لطفا آدرس منبع را وارد کنید");
        } else if (message.equals("category_empty")) {
            message = "لطفا دسته بندی را انتخاب کنید";
        } else if (message.equals("در حال بررسی")) {
            // for incorrect_url
            // message = " متاسفانه آدرس منبع معتبر نیست";
            if(eTxtSourceName.getText().toString()==""){
                eTxtSourceName.setText(eTxtSourceUrl.getText());
            }
            regexp = "^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/\\n]+)"; //cut baseurle
            if(eTxtSourceUrl.getText().toString()!="") {
                originalText=eTxtSourceUrl.getText().toString();
                Pattern pattern = Pattern.compile(regexp);
                Matcher matcher = pattern.matcher(eTxtSourceUrl.getText().toString());
                if (matcher.find()) {// cutting
                   uuu =matcher.group(1);
                   eTxtSourceUrl.setText(uuu);
                    if(uuu.contains("cnn.com")){
                        uuu = "rss."+uuu;
                    }else if(uuu.contains("google.com")){
                        uuu = "news."+uuu;
                    }else if(uuu.contains("presstv.ir")){

                    }
                }
                if(!uuu.contains("http")) {
                    eTxtSourceUrl.setText("http://" + uuu);
                }
                // if domain is not complete
                if(!android.util.Patterns.WEB_URL.matcher(eTxtSourceUrl.getText().toString()).matches()){
                    Log.d("URL","ok");
                    if(getHtml(eTxtSourceUrl.getText()+".ir","<html")){
                        eTxtSourceUrl.setText(eTxtSourceUrl.getText()+".ir");
                    }else if(getHtml(eTxtSourceUrl.getText()+".com","<html")){
                        eTxtSourceUrl.setText(eTxtSourceUrl.getText()+".com");
                    }/*else if(getHtml(eTxtSourceUrl.getText()+".co.ir","<html")){
                        eTxtSourceUrl.setText(eTxtSourceUrl.getText()+".co.ir");
                    }else if(getHtml(eTxtSourceUrl.getText()+".io","<html")){
                        eTxtSourceUrl.setText(eTxtSourceUrl.getText()+".io");
                    }else if(getHtml(eTxtSourceUrl.getText()+".us","<html")){
                        eTxtSourceUrl.setText(eTxtSourceUrl.getText()+".us");
                    }else if(getHtml(eTxtSourceUrl.getText()+".tv","<html")){
                        eTxtSourceUrl.setText(eTxtSourceUrl.getText()+".tv");
                    }*/else{
                        eTxtSourceUrl.setText(eTxtSourceUrl.getText()+".خطا");
                    }

                }









                urlsettextbox=eTxtSourceUrl.getText().toString();
                // check feed url
                // Async for load progress dialog with check feed url
                new CustomTask().execute((Void[])null);
            }

        }

        Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dataSourceLoaded(List<String> sourceNames) {
        //Toast.makeText(HomeActivity.this, "data source loaded", Toast.LENGTH_SHORT).show();
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(HomeActivity.this, R.layout.spinner_text, sourceNames);
        spinnerSources.setAdapter(adapter);
    }

    //do not use it
    @Override
    public void dataSourceItemsLoaded(List<SourceItem> sourceItems) {

    }

    @Override
    public void dataSourceLoadingFailed(String message) {
        Toast.makeText(HomeActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
    }

    //No use
    @Override
    public void sourceItemModified(SourceItem sourceItem, String oldName) {

    }

    //No use
    @Override
    public void sourceItemModificationFailed(String message) {

    }

    //No use
    @Override
    public void sourceItemDeleted(SourceItem sourceItem) {

    }

    //No use
    @Override
    public void sourceItemDeletionFailed(String message) {

    }

    private void clearSourceValues() {
        eTxtSourceName.setText("");
        eTxtSourceUrl.setText("");
        imgCategory.setImageDrawable(null);
        txtCategory.setText(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mAddFeedStatus) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(toolbar, 500);
                    if (SettingsPreferences.CIRCULAR_REVEAL) {
                        mAnimationUtil.revealAnimationHide(secondaryLayout, mainLayout);
                    } else {
                        new FadeAnimationUtil(HomeActivity.this).fadeOutAlpha(secondaryLayout, 500);
                        new FadeAnimationUtil(HomeActivity.this).fadeInAlpha(mainLayout, 500);
                    }
                }
            }, 500);
            mAddFeedStatus = false;
            fab.close(true);
        } else {
            super.onBackPressed();
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feeds, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_clear_feeds) {
            //mFeedsPresenter.deleteFeeds();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}