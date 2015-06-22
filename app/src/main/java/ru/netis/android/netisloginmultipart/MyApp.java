package ru.netis.android.netisloginmultipart;

import android.app.Application;

import java.net.CookieManager;
import java.net.CookiePolicy;

public class MyApp extends Application {

    public static final String TAG = MyApp.class.getSimpleName();

    private CookieManager mCookieManager;

    private static MyApp mInstance;

    public static synchronized MyApp getInstance() {
        return MyApp.mInstance;
    }

    public CookieManager getCookieManager() {
        if (mCookieManager == null) {
            mCookieManager = (CookieManager) CookieManager.getDefault();
            if (mCookieManager == null) {
                mCookieManager = new CookieManager();
                mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
                CookieManager.setDefault(mCookieManager);
            }
        }
        return mCookieManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApp.mInstance = this;
    }
}