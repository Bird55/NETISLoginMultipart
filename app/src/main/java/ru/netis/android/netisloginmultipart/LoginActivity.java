package ru.netis.android.netisloginmultipart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends AppCompatActivity implements AsyncTaskListener {

    private static final String URL = "http://stat.netis.ru/login.pl";

    private MenuItem item;
    private HttpHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        final EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        final Button buttonSubmit = (Button) findViewById(R.id.buttonSubmit);

        final AsyncTaskListener listener = this;
        helper = new HttpHelper(URL, MainActivity.msCookieManager);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = nameEditText.getText().toString();
                helper.addFormPart(new MultipartParameter("name", Constants.CONTENT_TYPE, param));
                param = passwordEditText.getText().toString();
                helper.addFormPart(new MultipartParameter("password", Constants.CONTENT_TYPE, param));

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
        Intent intent = new Intent();
        intent.putExtra("html", data);
        setResult(RESULT_OK, intent);
        finish();    }
}
