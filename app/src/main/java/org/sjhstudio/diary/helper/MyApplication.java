package org.sjhstudio.diary.helper;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyApplication extends MultiDexApplication {
    /* 상수 */
    private static final String LOG = "MyApplication";
    public static final int RESPONSE_OK = 200;
    public static final int RESPONSE_ERROR = 400;

    /* Helper */
    public static RequestQueue requestQueue;        // 웹으로부터 데이터를 요청하기위해 사용되는 RequestQueue

    public static interface OnResponseListener {    // Volley 응답 시 사용될 응답 리스너
        public void onResponse(int reqeustCode, int responseCode, String response);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack() {
                @Override
                protected HttpURLConnection createConnection(URL url) throws IOException {
                    HttpURLConnection connection = super.createConnection(url);
                    connection.setInstanceFollowRedirects(false);               // 리다이렉트를 따라갈지 여부 = false

                    return connection;
                }
            });
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void request(final int requestCode, final int method, final String url, final Map<String, String> params, final OnResponseListener listener) {
        StringRequest request = new StringRequest(
                method,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResponse(requestCode, RESPONSE_OK, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onResponse(requestCode, RESPONSE_ERROR, error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        request.setShouldCache(false);              // 캐시가 남아있어도 사용 x
        request.setRetryPolicy(new DefaultRetryPolicy(10 * 1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.requestQueue.add(request);    // requestQueue 에 해당 request 를 추가
    }
}

