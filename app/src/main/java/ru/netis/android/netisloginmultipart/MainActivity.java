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

public class MainActivity extends Activity {
    private static final String LOG_TAG = "myLog";
    private MenuItem item;
//    private String url = "http://bah.yar.ru/test03";
    private String url = "http://stat.netis.ru/login.pl";
    private TextView myTextView;
    HttpHelper helper;

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

                item.setActionView(R.layout.progress);

                helper = new HttpHelper();
                SendHttpRequestTask t = new SendHttpRequestTask(url);

                String[] params = new String[]{param1, param2};
                t.execute(params);
            }
        });
    }


    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        String url;
        SendHttpRequestTask(String url){
            this.url = url;
        }
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
                helper.connectForMultipart(url);
                helper.addFormPart("user", param1);
                helper.addFormPart("password", param2);
                helper.finishMultipart();
                data = helper.getResponse();
                Log.d(LOG_TAG, "\r\n" + helper.getHeaders());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        item = menu.getItem(0);
        return true;
    }
}
