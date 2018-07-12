package com.hems.socketio.client;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

import android.Manifest;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Fragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<>();

    private ArrayList<String> imgString = new ArrayList<>();

    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static final String REQUEST_URL="http://52.231.66.86:3000/api/image";
    private String result=null;

    public Fragment2(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_grid_view, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return view;
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout_gridview);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        gridView = (GridView) view.findViewById(R.id.grid_view);

        utils = new Utils(getContext());

        getJSON();
        jsonParser(result);

        // Initilizing Grid View
        InitilizeGridLayout();

        // Gridview adapter
        adapter = new GridViewImageAdapter(getActivity(), imgString, columnWidth);

        // setting grid view adapter
        gridView.setAdapter(adapter);

        return view;
    }
    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

        gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){

                utils = new Utils(getContext());

                getJSON();
                jsonParser(result);

                // Initilizing Grid View
                InitilizeGridLayout();

                // Gridview adapter
                adapter = new GridViewImageAdapter(getActivity(), imgString, columnWidth);

                // setting grid view adapter
                gridView.setAdapter(adapter);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        },500);
    }

//    public static String img2str(Bitmap image)
//    {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] b = baos.toByteArray();
//        String result = Base64.encodeToString(b,Base64.URL_SAFE);
//        return result;
//    }

//    public static Bitmap str2img(String input)
//    {
//        byte [] encodeByte= Base64.decode(input, Base64.URL_SAFE);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//        return bitmap;
//    }



    public String getJSON() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException { }

                        @Override
                        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException { }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }};
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null,trustAllCerts,new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

                    //Log.d(TAG, REQUEST_URL);
                    URL url = new URL(REQUEST_URL);
                    HttpsURLConnection HttpsURLConnection = (HttpsURLConnection) url.openConnection();

                    HttpsURLConnection.setReadTimeout(3000);
                    HttpsURLConnection.setConnectTimeout(3000);
//                    HttpsURLConnection.setDoOutput(true);
//                    HttpsURLConnection.setDoInput(true);
                    HttpsURLConnection.setRequestMethod("GET");
                    HttpsURLConnection.setUseCaches(false);
                    HttpsURLConnection.connect();

                    int responseStatusCode = HttpsURLConnection.getResponseCode();

                    InputStream inputStream;
                    if (responseStatusCode == HttpsURLConnection.HTTP_OK) {

                        inputStream = HttpsURLConnection.getInputStream();
                    } else {
                        inputStream = HttpsURLConnection.getErrorStream();
                        //Log.d(TAG,"Response Status Code : " + responseStatusCode);
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    bufferedReader.close();
                    inputStreamReader.close();
                    HttpsURLConnection.disconnect();

                    result = sb.toString().trim();
//                    Log.d(TAG,result);


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();

        return result;
    }

    public boolean jsonParser(String jsonString){

        if(jsonString == null) return false;
//        exchangeRateList = new ArrayList<ExchangeRate>();
//        onCreate에서 미리 선언해야함.
        try {
            JSONArray jsonArray = new JSONArray(jsonString);

            imgString.clear();

            //String p = "(USD)+/[A-Z]*";
//            Pattern pk = Pattern.compile("(USD)+/(KRW)+");

            for(int i=0; i<jsonArray.length();i++){
                JSONObject imgInfo = jsonArray.getJSONObject(i);
                //String currencyPair = exchangeInfo.getString("user");

                String bitImg = imgInfo.getString("img");
                imgString.add(bitImg);

                }
            } catch (JSONException e1) {
            e1.printStackTrace();
        }

        return true;
    }
}
