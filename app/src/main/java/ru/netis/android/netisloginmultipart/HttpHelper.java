package ru.netis.android.netisloginmultipart;

import android.text.TextUtils;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by bird on 02.06.2015
 */
public class HttpHelper {
    private static final String LOG_TAG = "myLog";

//    private static final String LOG_TAG = "myLog";

    private HttpURLConnection connection;
    private StringBuilder stringBuilder;
    private String url;
    private static final String delimiter = "--";
    private static final String boundary =  "SwA"+Long.toString(System.currentTimeMillis())+"SwA";

    private static final String COOKIES_HEADER = "Set-Cookie";
    private CookieManager msCookieManager;
    private static final String lineEnd = "\r\n";


    public HttpHelper(String url) {
        this.url = url;
        msCookieManager = MyApp.getInstance().getCookieManager();
        stringBuilder = new StringBuilder();
    }

    public void connectForMultipart() throws Exception {
        connection = (HttpURLConnection) ( new URL(url)).openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        if(msCookieManager.getCookieStore().getCookies().size() == 0) {
            Log.d(LOG_TAG, "connectForMultipart: Set cookie");
            connection.setRequestProperty("Cookie", "SID=-;path=/");
        }

        connection.setRequestProperty("Referer", "stat.netis.ru/");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    public HttpHelper addFormPart(String paramName, String value) {
        stringBuilder.append(delimiter).append(boundary).append(lineEnd);
        stringBuilder.append("Content-Type: text/plain\r\n");
        stringBuilder.append("Content-Disposition: form-data; name=\"").append(paramName).append("\"").append(lineEnd);
        stringBuilder.append(lineEnd).append(value).append(lineEnd);
        return this;
    }

    public HttpHelper addFormPart(MultipartParameter param) {
        stringBuilder.append(delimiter).append(boundary).append(lineEnd);
        stringBuilder.append("Content-Type: ").append(param.getContentType()).append("\r\n");
        stringBuilder.append("Content-Disposition: form-data; name=\"").append(param.getName()).append("\"").append(lineEnd);
        stringBuilder.append(lineEnd).append(param.getContent()).append(lineEnd);
        return this;
    }

    public void finishMultipart() throws Exception {
        stringBuilder.append(delimiter).append(boundary).append(delimiter).append(lineEnd);

        // Длинна запроса вставляется автоматически
        // Query length is automatically inserted
//        int length = stringBuilder.length();
//        connection.setRequestProperty("Content-Length", Integer.toString(length));

        connection.connect();
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

//        Log.d(LOG_TAG, "finishMultipart: " + stringBuilder.toString());

        outputStream.write((stringBuilder.toString()).getBytes());

/*
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
        if(cookiesHeader != null) {
            Log.d(LOG_TAG, "finishMultipart ");
            for (String cookie : cookiesHeader) {
                Log.d(LOG_TAG, "finishMultipart " + HttpCookie.parse(cookie).get(0));
                msCookieManager.getCookieStore().add(new URI("http://stat.netis.ru"), HttpCookie.parse(cookie).get(0));
            }
        }
*/
    }

    public String getHeaders(){
        StringBuilder ret = new StringBuilder();
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
            if (entry.getKey() != null) {
                ret.append(entry.getKey()).append(":").append(lineEnd);
            } else {
                ret.append(lineEnd);
            }
            List<String> list = entry.getValue();
            for (String l : list) {
                ret.append(l).append(lineEnd);
            }
        }
        return ret.toString();
    }

    public String getResponse() throws Exception {
        InputStream is = connection.getInputStream();
        byte[] b1 = new byte[1024];
        StringBuilder buffer = new StringBuilder();

        while ( is.read(b1) != -1)
            buffer.append(new String(b1));

        return buffer.toString();
    }

    public String getCookies() {

        if(msCookieManager.getCookieStore().getCookies().size() > 0) {
            return "Cookie: " + TextUtils.join(",", msCookieManager.getCookieStore().getCookies());
        } else {
            return "No Cookies";
        }
    }

    public void disConnect() throws Exception {
        connection.disconnect();
    }
}
