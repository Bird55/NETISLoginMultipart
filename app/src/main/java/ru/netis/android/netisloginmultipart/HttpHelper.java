package ru.netis.android.netisloginmultipart;

import android.text.TextUtils;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by bird on 02.06.2015
 */
public class HttpHelper {

    private static final String LOG_TAG = "myLog";

    private HttpURLConnection connection;
    private DataOutputStream outputStream;
    private StringBuilder stringBuilder;
    private List<String> cookies = null;

    private static final String delimiter = "--";
    private static final String boundary =  "SwA"+Long.toString(System.currentTimeMillis())+"SwA";

    private static final String COOKIES_HEADER = "Set-Cookie";
    private static CookieManager msCookieManager = new CookieManager();
    private static final String lineEnd = "\r\n";


    public HttpHelper() {
//        cookies = null;
        stringBuilder = new StringBuilder();
    }

    public void connectForMultipart(String url) throws Exception {
        connection = (HttpURLConnection) ( new URL(url)).openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
//        connection.setUseCaches(false); // Don't use a Cached Copy

        if(msCookieManager.getCookieStore().getCookies().size() > 0) {
            connection.setRequestProperty("Cookie:",
                    TextUtils.join(",", msCookieManager.getCookieStore().getCookies()));
        } else
        {
            connection.setRequestProperty("Cookie", "SID=-;path=/");
        }

        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    public void addFormPart(String paramName, String value) {
        stringBuilder.append(delimiter).append(boundary).append(lineEnd);
        stringBuilder.append("Content-Type: text/plain\r\n");
        stringBuilder.append("Content-Disposition: form-data; name=\"").append(paramName).append("\"").append(lineEnd);
        stringBuilder.append(lineEnd).append(value).append(lineEnd);
    }

    public void finishMultipart() throws Exception {
        stringBuilder.append(delimiter).append(boundary).append(delimiter).append(lineEnd);

//        int length = stringBuilder.length();
//        connection.setRequestProperty("Content-Length", Integer.toString(length));

        connection.connect();
        outputStream = new DataOutputStream(connection.getOutputStream());

        Log.d(LOG_TAG, "finishMultipart: " + stringBuilder.toString());

        outputStream.write((stringBuilder.toString()).getBytes());
//        outputStream.flush();
//        outputStream.close();
    }

    public String getHeaders(){
        StringBuilder ret = new StringBuilder();
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        Iterator<Map.Entry<String,List<String>>> entries = headerFields.entrySet().iterator();
        while (entries.hasNext()){
            Map.Entry<String,List<String>> entry = entries.next();
            if (entry.getKey() != null) {
                ret.append(entry.getKey()).append(":").append(lineEnd);
            } else {
                ret.append(lineEnd);
            }
            List<String> list = entry.getValue();
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                String l = iterator.next();
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
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

        if(cookiesHeader != null) {
            for (String cookie : cookiesHeader)
            {
                msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
            }
        }

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
