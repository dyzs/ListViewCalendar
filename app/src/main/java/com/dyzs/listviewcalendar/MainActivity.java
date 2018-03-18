package com.dyzs.listviewcalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dyzs.library.activity.CalendarActivity;
import com.dyzs.tencent_test.util.TencentConsts;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();


    private TextView goto_list_view_calendar, test_wx_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 微信初始化
//        wxApi = AppApplication.getIWXAPI();
        wxApi = WXAPIFactory.createWXAPI(this, TencentConsts.APP_ID, true);
        wxApi.registerApp(TencentConsts.APP_ID);

        initView();

        initEvent();
    }

    private void initView () {
        goto_list_view_calendar = (TextView) findViewById(R.id.goto_list_view_calendar);
        test_wx_auth = (TextView) findViewById(R.id.test_wx_auth);
    }




    private void initEvent () {
        goto_list_view_calendar.setOnClickListener(this);
        test_wx_auth.setOnClickListener(this);
    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.goto_list_view_calendar:
                Intent intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                break;
            case R.id.test_wx_auth:
                loginWxAuth();
                break;
        }
    }

    //----微信
    IWXAPI wxApi = null;
    private boolean isWxAuthLogin = false;
    private void loginWxAuth() {
        if (wxApi == null) {
            wxApi = WXAPIFactory.createWXAPI(this, TencentConsts.APP_ID, false);
            wxApi.registerApp(TencentConsts.APP_ID);
        }
        if (!wxApi.isWXAppInstalled()) {
            return;
        }
        Log.d(TAG, "login wx sendAuth");
        final SendAuth.Req req = new SendAuth.Req(new Bundle());
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_calendar_test";
        wxApi.sendReq(req);
        isWxAuthLogin = true;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (AppApplication.wxAuthCode != null && AppApplication.wxAuthCode.length() > 0 && isWxAuthLogin) {
            isWxAuthLogin = false;
            String info = "{\"code\":\"" + AppApplication.wxAuthCode + "\"}";
            Log.d(TAG, "info:" + info);
            // send login request
        }
    }
}
