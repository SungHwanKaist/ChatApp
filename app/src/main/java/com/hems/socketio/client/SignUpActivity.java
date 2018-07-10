package com.hems.socketio.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hems.socketio.client.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends Activity {
    private static final String REQUEST_URL="http://52.231.66.86:3000/api/user";
    private String TAG = "Contacts log";
    private EditText etName, etUserName, etPassWord, etContact, etAge, etEmail;
    private Button button;
    private String Name, UserName, PassWord, Contact, Age, Email;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = (EditText)  findViewById(R.id.name);
        etUserName= (EditText)findViewById(R.id.username);
        etPassWord= (EditText)findViewById(R.id.password);
        etContact=(EditText)findViewById(R.id.contact);
        etAge=(EditText)findViewById(R.id.age);
        etEmail=(EditText)findViewById(R.id.email);

        Name = etName.getText().toString();
        UserName = etUserName.getText().toString();
        PassWord = etPassWord.getText().toString();
        Contact = etContact.getText().toString();
        Age = etAge.getText().toString();
        Email = etEmail.getText().toString();

        button = (Button) findViewById(R.id.enrollment);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPost();
                Toast.makeText(getApplicationContext(), "Sign up Success!", Toast.LENGTH_LONG);
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

    }

    public void sendPost() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(REQUEST_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    //conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("nickname", Name);
                    jsonObject.put("userid", UserName);
                    jsonObject.put("password", PassWord);
                    jsonObject.put("age", Age);
                    jsonObject.put("email", Email);
                    jsonObject.put("contact", Contact);
                    jsonArray.put(jsonObject);

                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
//                    os.writeBytes(jArray.toString());
                    os.writeBytes(jsonArray.toString());
                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());


                    // convert JSONObject to JSON to String

                    //Log.i("JSON", jArray.toString());


                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


}
