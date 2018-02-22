package com.example.jack.sensors;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.example.jack.sensors.R.id.b3;
import static com.example.jack.sensors.R.id.bm;
import static com.example.jack.sensors.R.id.c1;
import static com.example.jack.sensors.R.id.e1;
import static com.example.jack.sensors.R.id.e2;

public class hcActivity extends AppCompatActivity {

    private Button b;
    private EditText e1;
    private EditText e2;
    Boolean flag=false;
    Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hc);

        b = (Button) findViewById(R.id.login);

        e1 = (EditText) findViewById(R.id.user);
        e2 = (EditText) findViewById(R.id.pass);



        b.setOnClickListener(new View.OnClickListener()   {

            @Override
            public void onClick(View v)
            {
                String user= e1.getText().toString();
                String pass =e2.getText().toString();

                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (mWifi.isConnected()) {
                    flag=true;
                    new HttpAsyncTask().execute("http://192.168.1.3:3000/login");
                }
                else
                {
                    flag=false;
                    Toast.makeText(hcActivity.this, "You are not connect to internet!",
                            Toast.LENGTH_SHORT).show();

                }

            }


        });

    }

    public static String POST(String url, Person person){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("username", person.getUser());
            jsonObject.accumulate("password", person.getPass());

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            person = new Person();
            person.setUser(e1.getText().toString());
            person.setPass(e2.getText().toString());

            return POST(urls[0],person);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
            try {

                JSONObject obj = new JSONObject(result);

                Log.d("My App", obj.toString());
                String login_status=obj.getString("status");
                Toast.makeText(getBaseContext(), login_status, Toast.LENGTH_LONG).show();
                if (login_status.equals("true"))
                {
                    startActivity(new Intent(hcActivity.this, hcActivityMain.class));
                }

            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
            }
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
