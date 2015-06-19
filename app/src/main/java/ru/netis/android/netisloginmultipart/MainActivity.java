package ru.netis.android.netisloginmultipart;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.CookieManager;
import java.net.CookieStore;

public class MainActivity extends Activity  implements AsyncTaskListener {
    public static final String LOG_TAG = "myLog";
    private static final String URL = "http://stat.netis.ru/login.pl";
    private TextView myTextView;

    public static CookieStore cookies;
    static {
        CookieManager m = new CookieManager();
        cookies = m.getCookieStore();
    }

    private HttpHelper helper;
    {
        try {
            helper = new HttpHelper(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTextView = (TextView) findViewById(R.id.textView);
        final EditText edtText1 = (EditText) findViewById(R.id.nameEditText);
        final EditText edtText2 = (EditText) findViewById(R.id.passwordEditText);
        Button btnUp1 = (Button) findViewById(R.id.button);

        btnUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param1 = edtText1.getText().toString();
                String param2 = edtText2.getText().toString();
                helper.addFormPart("user", param1);
                helper.addFormPart("password", param2);

                item.setActionView(R.layout.progress);

                SendHttpRequestTask t = new SendHttpRequestTask(helper);
                t.execute();
            }
        });
    }

/*
    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            item.setActionView(R.layout.progress);
        }

        @Override
        protected String doInBackground(String... params) {
            String param1 = params[0];
            String param2 = params[1];
            String data = null;

            try {
                helper.connectForMultipart();
                helper.addFormPart("user", param1);
                helper.addFormPart("password", param2);
                helper.finishMultipart();
                data = helper.getResponse();
//                Log.d(LOG_TAG, "\r\n" + helper.getHeaders());
                Log.d(LOG_TAG, "\r\n" + helper.getCookies());
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
            item.setActionView(null);
            Spanned s1 = Html.fromHtml(s);
            myTextView.setText(s1);
        }
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        item = menu.getItem(0);
        return true;
    }

    @Override
    public void onAsyncTaskFinished() {

    }
}
