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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

import android.Manifest;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.TypedValue;
import android.widget.GridView;

public class Fragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Utils utils;
    private ArrayList<String> imagePaths = new ArrayList<>();
    private GridViewImageAdapter adapter;
    private GridView gridView;
    private int columnWidth;

    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        // Initilizing Grid View
        InitilizeGridLayout();

        // loading all image paths from SD card
        imagePaths = utils.getFilePaths();

        // Gridview adapter
        adapter = new GridViewImageAdapter(getActivity(), imagePaths,
                columnWidth);

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

                // Initilizing Grid View
                InitilizeGridLayout();

                // loading all image paths from SD card
                imagePaths = utils.getFilePaths();

                // Gridview adapter
                adapter = new GridViewImageAdapter(getActivity(), imagePaths,
                        columnWidth);

                // setting grid view adapter
                gridView.setAdapter(adapter);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        },500);
    }

    public static String img2str(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String result = Base64.encodeToString(b,Base64.URL_SAFE);
        return result;
    }

    public static Bitmap str2img(String input)
    {
        byte [] encodeByte= Base64.decode(input, Base64.URL_SAFE);
        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;
    }

//    public void sendPost() throws IOException {
//
//        // 이미지
//        Bitmap bitmap = myView.getBitmap();
//
//// 기타 필요한 내용
//        String attachmentName = "bitmap";
//        String attachmentFileName = "bitmap.bmp";
//        String crlf = "\r\n";
//        String twoHyphens = "--";
//        String boundary =  "*****";
//
//// request 준비
//        HttpURLConnection httpUrlConnection = null;
//        URL url = new URL("http://example.com/server.cgi");
//        httpUrlConnection = (HttpURLConnection) url.openConnection();
//        httpUrlConnection.setUseCaches(false);
//        httpUrlConnection.setDoOutput(true);
//
//        httpUrlConnection.setRequestMethod("POST");
//        httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
//        httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
//        httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.boundary);
//
//// content wrapper시작
//        DataOutputStream request = new DataOutputStream(
//                httpUrlConnection.getOutputStream());
//
//        request.writeBytes(this.twoHyphens + this.boundary + this.crlf);
//        request.writeBytes("Content-Disposition: form-data; name=\"" + this.attachmentName + "\";filename=\"" +
//                this.attachmentFileName + "\"" + this.crlf);
//        request.writeBytes(this.crlf);
//
//// Bitmap을 ByteBuffer로 전환
//        byte[] pixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
//        for (int i = 0; i < bitmap.getWidth(); ++i) {
//            for (int j = 0; j < bitmap.getHeight(); ++j) {
//                //we're interested only in the MSB of the first byte,
//                //since the other 3 bytes are identical for B&W images
//                pixels[i + j] = (byte) ((bitmap.getPixel(i, j) & 0x80) >> 7);
//            }
//        }
//        request.write(pixels);
//
//// content wrapper종료
//        request.writeBytes(this.crlf);
//        request.writeBytes(this.twoHyphens + this.boundary +
//                this.twoHyphens + this.crlf);
//
//// buffer flush
//        request.flush();
//        request.close();
//
//// Response받기
//        InputStream responseStream = new
//                BufferedInputStream(httpUrlConnection.getInputStream());
//        BufferedReader responseStreamReader =
//                new BufferedReader(new InputStreamReader(responseStream));
//        String line = "";
//        StringBuilder stringBuilder = new StringBuilder();
//        while ((line = responseStreamReader.readLine()) != null) {
//            stringBuilder.append(line).append("\n");
//        }
//        responseStreamReader.close();
//        String response = stringBuilder.toString();
//
//
////Response stream종료
//        responseStream.close();
//
//// connection종료
//        httpUrlConnection.disconnect();
//    }
}
