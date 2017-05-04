package group2.ictk59.moviedatabase.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import group2.ictk59.moviedatabase.AlertDialogWrapper;
import group2.ictk59.moviedatabase.R;

/**
 * Created by ZinZin on 3/27/2017.
 */

public class HomeFragment extends BaseFragment {

    private ProgressDialog progressDialog;
    private WebView mWebView;
    String url;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        if (!isNetworkConnected()){
            AlertDialogWrapper.showAlertDialog(getActivity());
        }
        mWebView = (WebView)rootView.findViewById(R.id.web_view);
        mWebView.setWebViewClient(new MyWebViewClient());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new HomepageTask().execute("http://www.hollywoodreporter.com/topic/movies");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
    }

    private void webViewGoBack(){
        mWebView.goBack();
    }

    private class HomepageTask extends AsyncTask<String, Void, Document>{

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Document doInBackground(String... params) {
            Document document = null;
            url = params[0];
            try {
                document = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            document.getElementsByTag("header").remove();
            document.getElementsByClass("site-header").remove();
            document.getElementsByClass("site-header__placeholder").remove();
            document.getElementsByClass("site-header-links").remove();
            document.getElementsByTag("footer").remove();
            return document;
        }

        @Override
        protected void onPostExecute(Document document) {
            WebSettings ws = mWebView.getSettings();
            ws.setJavaScriptEnabled(true);
            mWebView.loadDataWithBaseURL(url,document.toString(),"text/html","utf-8","");
            mWebView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == MotionEvent.ACTION_UP
                            && mWebView.canGoBack()) {
                        handler.sendEmptyMessage(1);
                        return true;
                    }

                    return false;
                }
            });
        }
    }

    private class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            new HomepageTask().execute(url);
            return true;
        }
    }
}
