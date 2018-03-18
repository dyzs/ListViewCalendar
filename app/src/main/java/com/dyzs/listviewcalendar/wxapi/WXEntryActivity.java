package com.dyzs.listviewcalendar.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dyzs.listviewcalendar.AppApplication;
import com.dyzs.listviewcalendar.R;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.ShowMessageFromWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * tips:这个类一定要放在 app packageName 下的 wxapi 包里, 否则将获取不等授权登录的 code
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	private IWXAPI api;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			api = AppApplication.getIWXAPI();
			api.handleIntent(getIntent(), this);
	}
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				goToGetMsg();
			break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				goToShowMsg((ShowMessageFromWX.Req) req);
				break;
			default:
				break;
		}
	}

	private void goToGetMsg() {
		Bundle bundle = getIntent().getExtras();
		finish();
	}

	private void goToShowMsg(ShowMessageFromWX.Req showReq) {
		WXMediaMessage wxMsg = showReq.message;
		WXAppExtendObject obj = (WXAppExtendObject) wxMsg.mediaObject;

		StringBuffer msg = new StringBuffer(); // 组织一个待显示的消息内容
		msg.append("description: ");
		msg.append(wxMsg.description);
		msg.append("\n");
		msg.append("extInfo: ");
		msg.append(obj.extInfo);
		msg.append("\n");
		msg.append("filePath: ");
		msg.append(obj.filePath);

		String s = msg.toString();
		String title = wxMsg.title;

		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		if(resp instanceof SendAuth.Resp){
			SendAuth.Resp newResp = (SendAuth.Resp) resp;
			//获取微信传回的code
			String code = newResp.code;
			Log.i("code--->", "   code    :" + code);
			AppApplication.wxAuthCode = code;
		} else {
			//获取code后需要去获取access_token
			int result = 0;
			switch (resp.errCode) {
				case BaseResp.ErrCode.ERR_OK:
					result = R.string.errcode_success;
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					result = R.string.errcode_cancel;
					break;
				case BaseResp.ErrCode.ERR_AUTH_DENIED:
					result = R.string.errcode_deny;
					break;
				default:
					result = R.string.errcode_unknown;
					break;
			}
			Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		}
		this.finish();
		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
		finish();
	}
}
