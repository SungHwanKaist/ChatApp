package com.hems.socketio.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hems.socketio.client.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends Activity {
    private static final String REQUEST_URL="http://52.231.66.86:3000/api/user";
    private String TAG = "Contacts log";
    private TextInputEditText etName, etUserName, etPassWord, etContact, etAge, etEmail;
    private Button button;
    private String Name, UserName, PassWord, Contact, Age, Email;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etName = (TextInputEditText)  findViewById(R.id.name);
        etUserName= (TextInputEditText)findViewById(R.id.username);
        etPassWord= (TextInputEditText)findViewById(R.id.password);
        etContact=(TextInputEditText)findViewById(R.id.contact);
        etAge=(TextInputEditText)findViewById(R.id.age);
        etEmail=(TextInputEditText)findViewById(R.id.email);

        button = (Button) findViewById(R.id.enrollment);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Name = etName.getText().toString();
                UserName = etUserName.getText().toString();
                PassWord = etPassWord.getText().toString();
                Contact = etContact.getText().toString();
                Age = etAge.getText().toString();
                Email = etEmail.getText().toString();
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
                    URL object=new URL(REQUEST_URL);
                    HttpURLConnection con = (HttpURLConnection) object.openConnection();
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    //con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "application/json");
                    //con.setRequestProperty("Accept", "*/*");
                    //con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                    con.setRequestMethod("POST");
                    //con.connect();

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("name", Name);
                    jsonObject.put("username", UserName);
                    jsonObject.put("password", PassWord);
                    jsonObject.put("age", Age);
                    jsonObject.put("email", Email);
                    jsonObject.put("contact", Contact);

                    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.write(jsonObject.toString().getBytes());

                    Log.d(TAG, jsonObject.toString());

                    //display what returns the POST request
                    StringBuilder sb = new StringBuilder();
                    int HttpResult =con.getResponseCode();
                    if(HttpResult ==HttpURLConnection.HTTP_OK){
                        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        System.out.println(""+sb.toString());
                    }else{
                        System.out.println(con.getResponseMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finally { }
            }
        });

        thread.start();
    }


}
