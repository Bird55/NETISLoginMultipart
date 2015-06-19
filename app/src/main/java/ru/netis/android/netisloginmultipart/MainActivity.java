package ru.netis.android.netisloginmultipart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.CookieHandler;
import java.net.CookieManager;

public class MainActivity extends AppCompatActivity implements AsyncTaskListener {

    private static final String URL = "http://stat.netis.ru/login.pl";
    private TextView myTextView;

    static CookieManager msCookieManager;

    private HttpHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTextView = (TextView) findViewById(R.id.textView);
        final EditText edtText1 = (EditText) findViewById(R.id.nameEditText);
        final EditText edtText2 = (EditText) findViewById(R.id.passwordEditText);
        Button btnUp1 = (Button) findViewById(R.id.button);

        msCookieManager = (CookieManager) CookieHandler.getDefault();
        if (msCookieManager == null) {
            msCookieManager = new CookieManager();
        }

        final AsyncTaskListener listener = this;

        btnUp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param1 = edtText1.getText().toString();
                String param2 = edtText2.getText().toString();
                helper = new HttpHelper(URL, msCookieManager);
                helper.addFormPart(new MultipartParameter("user", Constants.CONTENT_TYPE, param1));
                helper.addFormPart(new MultipartParameter("password", Constants.CONTENT_TYPE, param2));

                SendHttpRequestTask t = new SendHttpRequestTask(helper, listener);
                t.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_login:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, Constants.LOGIN_REQUEST);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.LOGIN_REQUEST && resultCode == RESULT_OK) {
            String s = data.getStringExtra("html");
            myTextView.setText(Html.fromHtml(s));
        } else {
            myTextView.setText("Error!");
        }
    }

    @Override
    public void onAsyncTaskFinished(String data) {
        myTextView.setText(Html.fromHtml(data));
        if(msCookieManager.getCookieStore().getCookies().size() > 0) {
            Log.d(Constants.LOG_TAG, "onAsyncTaskFinished Cookie: " + TextUtils.join(",", msCookieManager.getCookieStore().getCookies()));
        } else {
            Log.d(Constants.LOG_TAG,  "No Cookies");
        }
    }
}
