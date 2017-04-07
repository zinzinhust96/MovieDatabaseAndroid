package group2.ictk59.moviedatabase;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ZinZin on 3/24/2017.
 */

enum DownloadStatus{IDLE, NOT_INITIALIZED, FAILED_OR_EMPTY, PROCESSING, OK}

public class GetRawData {
    private String LOG_TAG = GetRawData.class.getSimpleName();
    private String mURL;
    private String mData;
    private DownloadStatus mDownloadStatus;

    public GetRawData(String mURL) {
        this.mURL = mURL;
        mDownloadStatus = DownloadStatus.IDLE;
    }

    public void setmURL(String mURL) {
        this.mURL = mURL;
    }

    public String getmData() {
        return mData;
    }

    public DownloadStatus getmDownloadStatus() {
        return mDownloadStatus;
    }

    public void execute(){
        mDownloadStatus = DownloadStatus.PROCESSING;
        DownloadRawData downloadRawData = new DownloadRawData();
        downloadRawData.execute(mURL);
    }

    protected class DownloadRawData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String webData) {
            mData = webData;
            Log.v(LOG_TAG, "The data was returned is: " + mData);
            if (mData == null){
                if (mURL == null){
                    mDownloadStatus = DownloadStatus.NOT_INITIALIZED;
                }else{
                    mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
                }
            }else{
                mDownloadStatus = DownloadStatus.OK;
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if (strings == null){
                return null;
            }

            try{
                URL url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null){
                    return null;
                }

                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            }catch (IOException e){
                Log.e(LOG_TAG, "Error downloading data");
                return null;
            }finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
                if (reader != null){
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error in closing stream reader");
                    }
                }
            }
        }
    }
}

