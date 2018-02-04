package corpex.shureader.utils;

/**
 * Created with love by Corpex on 18/12/2016.
 */

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;


import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.Locale;

public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        enableCookies();
        loadLastLanguage();
    }

    private void enableCookies() {
        java.net.CookieManager cookieManager = new java.net.CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);
    }

    private void loadLastLanguage(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //If the preference doesnt exist, it will take mobilr language by default.
        String lang = preferences.getString("lang", Locale.getDefault().getLanguage());
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,  getBaseContext().getResources().getDisplayMetrics());
    }

}