package com.example.asus.tastenews.main.widget;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.asus.tastenews.R;
import com.example.asus.tastenews.about.widget.AboutFragment;
import com.example.asus.tastenews.floatingwindow.widget.FloatingFragment;
import com.example.asus.tastenews.guide.widget.GuideFragment;
import com.example.asus.tastenews.images.widget.ImageFragment;
import com.example.asus.tastenews.main.helper.ThemeSwitchHelper;
import com.example.asus.tastenews.main.presenter.MainPresenter;
import com.example.asus.tastenews.main.presenter.MainPresenterImpl;
import com.example.asus.tastenews.main.view.MainView;
import com.example.asus.tastenews.news.widget.NewsFragment;
import com.example.asus.tastenews.utils.LogUtils;
import com.example.asus.tastenews.utils.SpeechRecognitionUtils.SpeechRecognitionUtils;
import com.example.asus.tastenews.weather.widget.WeatherFragment;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.sunflower.FlowerCollector;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements MainView,SpeechRecognitionUtils.SpeechRecognitionCallback {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;
    private MainPresenter mMainPresenter;
    private SpeechRecognitionUtils mSpeechRecognition;
    private boolean isListening = false;
    private ThemeSwitchHelper mThemeSwitchHelper;
    private OnThemeSwitchListener mOnThemeSwitchListener;
    private final String TAG = MainActivity.class.getSimpleName();

    public static final String THEME_NAVIGATION = "navigation_theme";
    public static final String THEME_BACKGROUND = "background_theme";
    public static final String THEME_TEXT = "text_theme";
    public static final String THEME_TEXT_SECOND = "text_second_theme";
    public static final String THEME_TAB = "tab_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.d("dshfau","MainActivity created");
        super.onCreate(savedInstanceState);
//        initTheme();
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        init();
        switch2News();
    }

    public void setOnThemeSwitchListener(OnThemeSwitchListener listener){
        mOnThemeSwitchListener = listener;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        /**
         * 重写这个函数的目的是因为fragment在activity被回收后不会被回收，当activity再次创建时，
         * 因为activity之前已经保存fragment在Bundle中了，所以会恢复fragment，但是activity重启
         * 也会再次生成fragment，两层fragment重叠，恢复的fragment使用getActivity()为null，于是
         * 报错。
         * 传输门：http://blog.csdn.net/goodlixueyong/article/details/48715661
         */

        //super.onSaveInstanceState(outState);
    }

    private void switchAccordingSpeech(String result){
        if(result.contains("新闻")||result.contains("新")||result.contains("闻")){
            switch2News();
            return;
        }
        if(result.contains("头条")||result.contains("头")||result.contains("条")){
            switch2News();
            return;
        }
        if(result.contains("NBA")||result.contains("n")||result.contains("b")||result.contains("a")){
            switch2NBA();
            return;
        }
        if(result.contains("笑话")||result.contains("笑")||result.contains("话")){
            switch2Joke();
            return;
        }
        if(result.contains("汽车")||result.contains("汽")||result.contains("车")){
            switch2Cars();
            return;
        }
        if(result.contains("天气")||result.contains("天")||result.contains("气")){
            switch2Weather();
            return;
        }
        if(result.contains("图片")||result.contains("图")||result.contains("片")){
            switch2Images();
            return;
        }
        if(result.contains("悬浮窗")||result.contains("悬")||result.contains("浮")||result.contains("窗")){
            switch2FloatingWindow();
            return;
        }
    }

    @Override
    public void onSuccess(String result) {
        LogUtils.d("SPEECHT","result is " + result);
        switchAccordingSpeech(result);
        mSpeechRecognition.stopSpeechRecognition();
    }

    @Override
    public void onFailure(String errorMessage) {
        LogUtils.d("SPEECHT","error is " + errorMessage);
        mSpeechRecognition.stopSpeechRecognition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        setIconEnable(menu,true);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    private void setIconEnable(Menu menu, boolean enable)
    {
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            //下面传入参数
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.action_setting){
            Toast.makeText(this,"设置",Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.voice_open) {
          Toast.makeText(this, "开启语音控制", Toast.LENGTH_SHORT).show();
          mSpeechRecognition.startSpeechRecognition(this, this);
          return true;
        }
//        }else if(id == R.id.day_theme){
//            setTheme(R.style.DayStyle);
//            ((NewsApplication)(getApplication())).setMode(R.style.DayStyle);
//            mThemeSwitchHelper.setThemeMode(ThemeSwitchHelper.THEME_MODE_DAY);
//            refreshUI();
//            return true;
//        }else if(id == R.id.night_theme){
//            setTheme(R.style.BlueStyle);
//            ((NewsApplication)(getApplication())).setMode(R.style.BlueStyle);
//            mThemeSwitchHelper.setThemeMode(ThemeSwitchHelper.THEME_MODE_NIGTH);
//            refreshUI();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

  /**
   * 由于对UI设计不熟，因此只是当作练技术，未将换肤功能加入实际的app中。
   */
//  private void initTheme(){
//        mThemeSwitchHelper = new ThemeSwitchHelper(this);
//        if(mThemeSwitchHelper.isDay()){
//            setTheme(R.style.DayStyle);
//            ((NewsApplication)(getApplication())).setMode(R.style.DayStyle);
//        }else{
//            setTheme(R.style.BlueStyle);
//            ((NewsApplication)(getApplication())).setMode(R.style.BlueStyle);
//        }
//    }

    private void init(){
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,
                R.string.drawer_close);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mNavigationView = (NavigationView)findViewById(R.id.navigation_view);

        setupDrawerLayout(mNavigationView);

        mMainPresenter = new MainPresenterImpl(this);

        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=579db503");
        mSpeechRecognition = new SpeechRecognitionUtils();
    }

    private void refreshUI(){
        TypedValue backgroundColor = new TypedValue();
        TypedValue textColor= new TypedValue();
        TypedValue textPrimaryColor = new TypedValue();
        TypedValue headerColor = new TypedValue();
        TypedValue footerColor = new TypedValue();
        TypedValue titleBarColor = new TypedValue();
        TypedValue tabColor = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(R.attr.colorBackground,backgroundColor,true);
        theme.resolveAttribute(R.attr.colorText,textColor,true);
        theme.resolveAttribute(R.attr.colorTextPrimary,textPrimaryColor,true);
        theme.resolveAttribute(R.attr.colorHeader,headerColor,true);
        theme.resolveAttribute(R.attr.colorFooter,footerColor,true);
        theme.resolveAttribute(R.attr.colorTitleBar,titleBarColor,true);
        theme.resolveAttribute(R.attr.colorTab,tabColor,true);

        mNavigationView.getHeaderView(0).setBackgroundResource(headerColor.resourceId);
        mToolbar.setBackgroundResource(titleBarColor.resourceId);
        mToolbar.setBackgroundColor(this.getResources().getColor(titleBarColor.resourceId));
        if(mOnThemeSwitchListener != null){
            HashMap<String,TypedValue> themes = new HashMap<>();
            themes.put(THEME_BACKGROUND,backgroundColor);
            themes.put(THEME_TEXT,textColor);
            themes.put(THEME_TEXT_SECOND,textPrimaryColor);
            themes.put(THEME_TAB,tabColor);
            mOnThemeSwitchListener.switch2Theme(themes);
        }
    }

    private void setupDrawerLayout(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem item) {
                        mMainPresenter.switchNavigation(item.getItemId());
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    @Override
    public void switch2Guide(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content,new GuideFragment()).commit();
    }

    @Override
    public void switch2News(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content,new NewsFragment()).commit();
        mToolbar.setTitle(R.string.navigation_news);
    }

    private void switch2NBA(){
        NewsFragment fragment = new NewsFragment();
        fragment.setFirstShowFragment(NewsFragment.NEWS_TYPE_NBA);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,fragment).commit();
        mToolbar.setTitle(R.string.navigation_news);
    }

    private void switch2Joke(){
        NewsFragment fragment = new NewsFragment();
        fragment.setFirstShowFragment(NewsFragment.NEWS_TYPE_JOKES);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,fragment).commit();
        mToolbar.setTitle(R.string.navigation_news);
    }

    private void switch2Top(){
        NewsFragment fragment = new NewsFragment();
        fragment.setFirstShowFragment(NewsFragment.NEWS_TYPE_TOP);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,fragment).commit();
        mToolbar.setTitle(R.string.navigation_news);
    }

    private void switch2Cars(){
        NewsFragment fragment = new NewsFragment();
        fragment.setFirstShowFragment(NewsFragment.NEWS_TYPE_CARS);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content,fragment).commit();
        mToolbar.setTitle(R.string.navigation_news);
    }

    @Override
    public void switch2Images(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content,new ImageFragment()).commit();
        mToolbar.setTitle(getString(R.string.navigation_images));
    }

    @Override
    public void switch2Weather(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content,new WeatherFragment()).commit();
        mToolbar.setTitle(getString(R.string.navigation_weather));
    }

    @Override
    public void switch2About(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content,new AboutFragment()).commit();
        mToolbar.setTitle(getString(R.string.navigation_about));
    }

    @Override
    public void switch2FloatingWindow() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_content,new FloatingFragment()).commit();
        mToolbar.setTitle(getString(R.string.floating_switch));
    }

    @Override
    protected void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(this);
        super.onPause();
    }

    public interface OnThemeSwitchListener{
        void switch2Theme(HashMap<String,TypedValue> themes);
    }
}
