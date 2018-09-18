package com.linxiao.webviewforflashdemo;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

  private WebView webView;
//    String defaultUrl = "https://www.jiyoushe.cn/";
//  String defaultUrl = "http://www.baidu.com";
  String defaultUrl = "http://192.168.0.100:801/test/";
//  String defaultUrl = "http://kaq.io/ujQK";
//  String defaultUrl = "http://m.iqiyi.com/v_19rqy8zme0.html";
  private FrameLayout mFrameLayout;
  private View mTitle;
  //  String url = "http://www.1kejian.com/flash/";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initView();
  }


  private void initView() {
    webView = findViewById(R.id.id_webview);
    mTitle = findViewById(R.id.title);
    EditText editText = findViewById(R.id.edit);
    editText.setHint(defaultUrl);

    mFrameLayout = (FrameLayout) findViewById(R.id.frame_layout);
    //支持javascript
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);
    // 设置可以支持缩放
    settings.setSupportZoom(true);
    // 设置出现缩放工具
    settings.setBuiltInZoomControls(true);
    //扩大比例的缩放
    settings.setUseWideViewPort(true);
    //自适应屏幕
    settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    settings.setLoadWithOverviewMode(true);
    settings.setPluginState(WebSettings.PluginState.ON);

    settings.setDomStorageEnabled(true);
    //如果不设置WebViewClient，请求会跳转系统浏览器
    webView.setWebViewClient(new WebViewClient() {

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //该方法在Build.VERSION_CODES.LOLLIPOP以前有效，从Build.VERSION_CODES.LOLLIPOP起，建议使用shouldOverrideUrlLoading(WebView, WebResourceRequest)} instead
        //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
        //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242

        if (url.toString().contains("sina.cn")) {
          view.loadUrl("http://ask.csdn.net/questions/178242");
          return true;
        }
        return false;
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        //返回false，意味着请求过程里，不管有多少次的跳转请求（即新的请求地址），均交给webView自己处理，这也是此方法的默认处理
        //返回true，说明你自己想根据url，做新的跳转，比如在判断url符合条件的情况下，我想让webView加载http://ask.csdn.net/questions/178242
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          if (request.getUrl().toString().contains("sina.cn")) {
            view.loadUrl("http://ask.csdn.net/questions/178242");
            return true;
          }
        }
        return false;
      }

    });

    webView.setWebChromeClient(new InsideWebChromeClient());

    webView.loadUrl(defaultUrl);
  }

  private View mCustomView;
  private WebChromeClient.CustomViewCallback mCustomViewCallback;

  public void go(View view) {
    EditText viewById = findViewById(R.id.edit);
    String url = viewById.getText().toString();
    if (TextUtils.isEmpty(url)) {
      url = defaultUrl;
//      Toast.makeText(this, "url is null", Toast.LENGTH_SHORT).show();
    }
    webView.loadUrl(url);
  }

  public void url_list(View view) {


  }

  private class InsideWebChromeClient extends WebChromeClient {

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
      super.onShowCustomView(view, callback);
      if (mCustomView != null) {
        callback.onCustomViewHidden();
        return;
      }
      mCustomView = view;
      mFrameLayout.addView(mCustomView);
      mCustomViewCallback = callback;
//      webView.setVisibility(View.GONE);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      setStatusBarVisibility(false);
    }

    @Override
    public void onHideCustomView() {
//      webView.setVisibility(View.VISIBLE);
      if (hideCustomeView()) {
        return;
      }
      super.onHideCustomView();
    }


  }

  private boolean hideCustomeView() {
    if (mCustomView == null) {
      return true;
    }
    mCustomView.setVisibility(View.GONE);
    mFrameLayout.removeView(mCustomView);
    mCustomViewCallback.onCustomViewHidden();
    mCustomView = null;
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setStatusBarVisibility(true);
    return false;
  }

  private void setStatusBarVisibility(boolean visible) {
    int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
    getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);
  }


  @Override
  public void onBackPressed() {
    if (mCustomView != null) {
      hideCustomeView();
      return;
    } else if (webView.canGoBack()) {
      webView.goBack();
      return;
    }
    super.onBackPressed();
  }

  private boolean check() {
    PackageManager pm = getPackageManager();
    List<PackageInfo> infoList = pm
        .getInstalledPackages(PackageManager.GET_SERVICES);
    for (PackageInfo info : infoList) {
      if ("com.adobe.flashplayer".equals(info.packageName)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    CookieSyncManager.createInstance(this);  //Create a singleton CookieSyncManager within a context
    CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
    cookieManager.removeAllCookie();// Removes all cookies.
    CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

    webView.setWebChromeClient(null);
    webView.setWebViewClient(null);
    webView.getSettings().setJavaScriptEnabled(false);
    webView.clearCache(true);
  }
}
