package ru.netis.android.netisloginmultipart;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.CookieManager;
import java.net.CookieStore;

public class MainActivity extends Activity  implements AsyncTaskListener {

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
        final AsyncTaskListener listener = this;

        btnUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param1 = edtText1.getText().toString();
                String param2 = edtText2.getText().toString();
                helper.addFormPart(new MultipartParameter("user", Constants.CONTENT_TYPE, param1));
//                helper.addFormPart("user", param1);
                helper.addFormPart("password", param2);

                item.setActionView(R.layout.progress);

                SendHttpRequestTask t = new SendHttpRequestTask(helper, listener);
                t.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        item = menu.getItem(0);
        return true;
    }

    @Override
    public void onAsyncTaskFinished(String data) {
        myTextView.setText(Html.fromHtml(data));
    }
}
