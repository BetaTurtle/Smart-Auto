package smartauto.betaturtle.in.smartauto;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by jun on 14/10/14.
 */
public class AsyncPost extends AsyncTask<String, String, String> {

    private static final int CONNECTION_TIMEOUT_MS = 10000;
    private static final int SOCKET_TIMEOUT_MS = 15000;

    public interface OnPostCompleteListener {
        void onPostComplete(boolean success, int statusCode, String response);
    }

    String url;
    JSONObject jsonObj;
    private final OnPostCompleteListener listener;
    private int statusCode = -1;
    private boolean success = false;

    public AsyncPost(String url, JSONObject json) {
        this(url, json, null);
    }

    public AsyncPost(String url, JSONObject json, OnPostCompleteListener listener) {
        this.url = url;
        this.jsonObj = json;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(httpParams, SOCKET_TIMEOUT_MS);
        HttpClient client = new DefaultHttpClient(httpParams);
        HttpPost post = new HttpPost(url);
        try {
            StringEntity entity = new StringEntity(jsonObj.toString(), "UTF-8");
            post.setHeader("Content-Type", "application/json");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if (statusLine != null) {
                statusCode = statusLine.getStatusCode();
                success = statusCode >= 200 && statusCode < 300;
                Log.i("Status code", Integer.toString(statusCode));
            }
            HttpEntity responseEntity = response.getEntity();
            String str = responseEntity != null
                    ? new String(EntityUtils.toByteArray(responseEntity), "UTF-8")
                    : "";
            Log.w("Response", str);
            return str;
        } catch (Exception e) {
            Log.e("AsyncPost", "Failed to POST to " + url, e);
            return null;
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onPostComplete(success, statusCode, result);
        }
    }
}
