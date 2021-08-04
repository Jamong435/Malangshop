package com.auto.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wv = findViewById(R.id.wv);
        wv.loadUrl("https://smartstore.naver.com/malangin");
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true); // 웹페이지 자바스클비트 허용 여부
        webSettings.setSupportMultipleWindows(false); // 새창 띄우기 허용 여부
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        webSettings.setLoadWithOverviewMode(true); // 메타태그 허용 여부
        webSettings.setUseWideViewPort(true); // 화면 사이즈 맞추기 허용 여부
        webSettings.setSupportZoom(false); // 화면 줌 허용 여부
        webSettings.setBuiltInZoomControls(false); // 화면 확대 축소 허용 여부
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); // 컨텐츠 사이즈 맞추기
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // 브라우저 캐시 허용 여부
        webSettings.setDomStorageEnabled(true); // 로컬저장소 허용 여부

//        wv.setWebViewClient(new WebViewClient() {
//
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return false;
//            }
//
//
//        });
        wv.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //http나 https 로 연결하는거면 자연스럽게 되는데
                //결제페이지는 그렇지않기에 에러가 나는것입니다. 그래서 그 안되는것만 잡아서 되게 변형시키면됩니다
                //안드로이드가 앱 - 앱 간 인텐트 로딩할때 uri 방식으로 로드하기 때문에 오버라이드 단계에서 uri로 판단해서, 정상 uri가 아닐 경우 scheme로 parse 하여 인텐트 요청
                if (!request.getUrl().toString().startsWith("http://") && !request.getUrl().toString().startsWith("https://") && !request.getUrl().toString().startsWith("javascript")) {
                    Intent intent = null;

                    try {
                        intent = Intent.parseUri(request.getUrl().toString(), Intent.URI_INTENT_SCHEME);
                        Uri uri = Uri.parse(intent.getDataString());

                        wv.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        return true;

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        return false;
                    } catch (ActivityNotFoundException e) {
                        if (intent == null) {
                            return false;
                        }

                        String packageName = intent.getPackage();
                        if (packageName != null) {
                            wv.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                            return true;
                        }

                        return false;
                    }
                }

                return super.shouldOverrideUrlLoading(view, request);

            }

        });


    }
    public void onBackPressed() {
        if (wv.canGoBack()) {
            wv.goBack();
        } else {
            super.onBackPressed();
        }

    }

}