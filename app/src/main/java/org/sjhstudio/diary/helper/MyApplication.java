package org.sjhstudio.diary.helper;

import androidx.multidex.MultiDexApplication;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.sjhstudio.diary.utils.Constants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyApplication extends MultiDexApplication {

    public static RequestQueue requestQueue;    // 웹으로부터 데이터를 요청하기위해 사용되는 RequestQueue

    public interface OnResponseListener {   // Volley 응답 시 사용될 응답 리스너
        void onResponse(int requestCode, int responseCode, String response);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false);   // 리다이렉트를 따라갈지 여부 = false

                    return connection;
                }
            });
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void request(
            final int requestCode,
            final int method,
            final String url,
            final Map<String, String> params,
            final OnResponseListener listener
    ) {
        StringRequest request = new StringRequest(
                method,
                url,
                response -> listener.onResponse(requestCode, Constants.VOLLEY_RESPONSE_OK, response),
                error -> listener.onResponse(requestCode, Constants.VOLLEY_RESPONSE_ERROR, error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        request.setShouldCache(false);  // 캐시x
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.requestQueue.add(request);    // requestQueue 에 해당 request 추가
    }

    public static void requestWithHeader(
            final int requestCode,
            final int method,
            final String url,
            final Map<String, String> headers,
            final OnResponseListener listener
    ) {
        StringRequest request = new StringRequest(
                method,
                url,
                response -> listener.onResponse(requestCode, Constants.VOLLEY_RESPONSE_OK, response),
                error -> listener.onResponse(requestCode, Constants.VOLLEY_RESPONSE_ERROR, error.getMessage())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };

        request.setShouldCache(false);  // 캐시x
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.requestQueue.add(request);    // requestQueue 에 해당 request 추가
    }
}

