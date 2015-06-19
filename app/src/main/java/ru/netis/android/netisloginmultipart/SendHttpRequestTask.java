package ru.netis.android.netisloginmultipart;

import android.os.AsyncTask;
import android.util.Log;


public class SendHttpRequestTask extends AsyncTask<Void, Void, String> {
    HttpHelper helper;

    public SendHttpRequestTask(HttpHelper helper) {
        this.helper = helper;
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
            Log.d(MainActivity.LOG_TAG, "\r\n" + helper.getHeaders());
            Log.d(MainActivity.LOG_TAG, "\r\n" + helper.getCookies());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            helper.disConnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(MainActivity.LOG_TAG, "\r\n" + s);
//        Spanned s1 = Html.fromHtml(s);
//        myTextView.setText(s1);
    }
}

