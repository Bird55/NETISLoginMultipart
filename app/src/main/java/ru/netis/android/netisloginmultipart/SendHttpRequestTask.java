package ru.netis.android.netisloginmultipart;

import android.os.AsyncTask;
import android.util.Log;


public class SendHttpRequestTask extends AsyncTask<Void, Void, String> {
    HttpHelper helper;
    AsyncTaskListener listener;

    public SendHttpRequestTask(HttpHelper helper, AsyncTaskListener listener) {
        this.helper = helper;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Void... params) {
        String data = null;

        try {
            helper.connectForMultipart();
            helper.finishMultipart();
            data = helper.getResponse();
//            Log.d(Constants.LOG_TAG, "\r\n" + helper.getHeaders());
//            Log.d(Constants.LOG_TAG, "\r\n" + helper.getCookies());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String data) {
        try {
            helper.disConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d(MainActivity.LOG_TAG, "\r\n" + data);
        listener.onAsyncTaskFinished(data);
    }
}

