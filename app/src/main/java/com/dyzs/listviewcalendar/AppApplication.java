package com.dyzs.listviewcalendar;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dyzs.tencent_test.util.TencentConsts;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by maidou on 2016/9/2.
 */
public class AppApplication extends Application{
    public static Context mAppContext;

    private static AppApplication appInstance = null;
    public static String wxAuthCode = "";

    private AppApplication() {}
    public static AppApplication getInstance(){
        if(appInstance == null)
            appInstance = new AppApplication();
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        initImageLoader();
        mAppContext = this;
    }


    private static IWXAPI mIWXAPI;
    public static IWXAPI getIWXAPI() {
        if (mIWXAPI == null) {
            initWXAPI();
        }
        return mIWXAPI;
    }

    private static void initWXAPI() {
//        if (checkNet()) {
            // 通过WXAPIFactory工厂，获取IWXAPI的实例
            mIWXAPI = WXAPIFactory.createWXAPI(mAppContext, TencentConsts.APP_ID, true);
            mIWXAPI.registerApp(TencentConsts.APP_ID);
//        }
    }

    private static boolean checkNet() {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {

                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {

                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

}
