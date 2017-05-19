package group2.ictk59.moviedatabase.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import group2.ictk59.moviedatabase.AlertDialogWrapper;
import group2.ictk59.moviedatabase.R;

/**
 * Created by ZinZin on 3/27/2017.
 */

public class HomeFragment extends BaseFragment {

    private WebView mWebView;
    private ProgressBar progressBar;
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
        progressBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
        mWebView = (WebView)rootView.findViewById(R.id.web_view);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new HomepageTask().execute("http://www.darkhorizons.com/section/movie-news/");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
    }

    private void webViewGoBack(){
        mWebView.goBack();
    }

    private class HomepageTask extends AsyncTask<String, Void, Document>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
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
            document.getElementsByClass("td-mobile-nav").remove();
            document.getElementsByClass("td-search-wrap-mob").remove();
            document.getElementsByClass("td-header-top-menu-full td-container-wrap ").remove();
            document.getElementsByClass("td-banner-wrap-full td-container-wrap ").remove();
            document.getElementsByClass("td-header-menu-wrap").remove();
            document.getElementsByClass("td-search-wrapper").remove();
            document.getElementsByClass("header-search-wrap").remove();

            document.getElementsByClass("td-g-rec td-g-rec-id-sidebar ").remove();
            document.getElementsByClass("td-block-title-wrap").remove();
            document.getElementsByClass("td_block_inner").remove();
            document.getElementsByClass("td-next-prev-wrap").remove();
            document.getElementsByClass("td-sub-footer-container td-container-wrap ").remove();
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(1000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();
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
