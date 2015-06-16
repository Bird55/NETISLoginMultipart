package ru.netis.android.netisloginmultipart;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
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

//                item.setActionView(R.layout.progress);
                SendHttpRequestTask t = new SendHttpRequestTask();

                String[] params = new String[]{url, param1, param2};
                t.execute(params);
            }
        });
    }


    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            item.setActionView(R.layout.progress);
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String param1 = params[1];
            String param2 = params[2];
            String data = null;
            HttpHelper helper = new HttpHelper();

            try {
                helper.connectForMultipart(url);
                helper.addFormPart("user", param1);
                helper.addFormPart("password", param2);
//                helper.addFormPart("submit", "%D0%92%D0%BE%D0%B9%D1%82%D0%B8");
//                helper.addFormPart("submit", "Submit");
                helper.finishMultipart();
                data = helper.getResponse();
                Log.d(LOG_TAG, "\r\n" + helper.getHeaders());
                Log.d(LOG_TAG, "\r\n" + helper.getCookies());
                helper.disConnect();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            item.setActionView(null);
            myTextView.setText(s);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        item = menu.getItem(0);
        return true;
    }
}
