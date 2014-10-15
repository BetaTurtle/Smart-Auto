package smartauto.betaturtle.in.smartauto;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by jun on 14/10/14.
 */
public class AsyncPost extends AsyncTask<String, String, String> {

    String url;
    JSONObject jsonObj;

    public AsyncPost(String url, JSONObject json) {
        this.url = url;
        this.jsonObj = json;
    }

    @Override
    protected String doInBackground(String... strings) {

        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        try {
            StringEntity entity = new StringEntity(jsonObj.toString());
            post.setHeader("Content-Type", "application/json");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            String str = new String(EntityUtils.toByteArray(response.getEntity()), "UTF-8");
            Log.w("Response", str);
            StatusLine statusLine = response.getStatusLine();
            Log.i("Status code", Integer.toString(statusLine.getStatusCode()));

        }catch (Exception e){
            e.printStackTrace();
            // Toast.makeText(context, "Oops. Something went wrong.", Toast.LENGTH_SHORT).show();

        }
        return null;
    }
}
