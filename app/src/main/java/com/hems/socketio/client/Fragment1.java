package com.hems.socketio.client;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
//import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hems.socketio.client.adapter.ContactListRecyclerAdapter;
import com.hems.socketio.client.adapter.ContactRecyclerAdapter;
import com.hems.socketio.client.api.ChatService;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.RetrofitCallback;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.api.UserService;
import com.hems.socketio.client.enums.ChatType;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.provider.DatabaseContract;
import com.hems.socketio.client.provider.QueryUtils;
import com.hems.socketio.client.sync.ChatSyncAdapter;
import com.hems.socketio.client.utils.SessionManager;

import java.util.ArrayList;
import java.util.Comparator;

public class Fragment1 extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public Fragment1(){}

    private RecyclerView recyclerView;
    private ContactListRecyclerAdapter adapter;
    private ArrayList<Contact> list = new ArrayList<>();
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment1, container, false);

        sessionManager = SessionManager.newInstance(getContext());

//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView_fragment1);
        setRecyclerView(recyclerView);

        return view;
    }

    public void setRecyclerView(RecyclerView recyclerView){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        adapter = new ContactListRecyclerAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation()));
        getLoaderManager().initLoader(101, null, this);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_contact, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_create_chat) {
//            ArrayList<String> contacts = new ArrayList<>();
//            for (Contact contact : adapter.getDatas()) {
//                if (contact.isSelected()) {
//                    contacts.add(contact.getId());
//                }
//            }
//            createChat(contacts.size() == 1 ? ChatType.PERSONAL : ChatType.GROUP,
//                    contacts);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public void onItemClick(View view, int position) {
//        adapter.getItem(position).setSelected(!adapter.getItem(position).isSelected());
//        adapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onCheckedChange(CompoundButton buttonView, boolean isChecked, int position) {
//        adapter.getItem(position).setSelected(isChecked);
//        adapter.notifyDataSetChanged();
//    }

//    private void createChat(ChatType chatType, ArrayList<String> users) {
//        progressDialog = ProgressDialog.show(getContext(), getString(R.string.app_name),
//                "Creating " + (chatType == ChatType.PERSONAL ? "private" : "group") + " chat", false);
//        ChatService request = (ChatService) RetrofitCall.createRequest(ChatService.class);
//        request.createChat(sessionManager.getUserId(), chatType.getValue(), users).enqueue(new RetrofitCallback<Chat>() {
//            @Override
//            public void onResponse(Chat response) {
//                if (progressDialog != null) {
//                    progressDialog.dismiss();
//                    progressDialog = null;
//                }
//                Toast.makeText(CreateChatActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
//                if (response.getStatus() == Service.SUCCESS) {
//                    QueryUtils.addChat(getApplicationContext(), response.getData().get(0));
//                    finish();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Toast.makeText(CreateChatActivity.this, "Failed to create chat", Toast.LENGTH_SHORT).show();
//                if (progressDialog != null) {
//                    progressDialog.dismiss();
//                    progressDialog = null;
//                }
//            }
//        });
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), DatabaseContract.TableContact.CONTENT_URI,
                DatabaseContract.TableContact.PROJECTION,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Contact> contacts= new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Contact chat = new Contact(cursor);
                    contacts.add(chat);
                } while (cursor.moveToNext());

//                Fragment1 fragment1 = new Fragment1();
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("contactList", list);
//                fragment1.setArguments(bundle);
            }
        }
        adapter.setDatas(contacts);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

//    private void getContacts() {
//        UserService request = (UserService) RetrofitCall.createRequest(UserService.class);
//        request.getUserList(sessionManager.getUserId()).enqueue(new RetrofitCallback<Contact>() {
//            @Override
//            public void onResponse(Contact response) {
//
//                if (response.getStatus() == Service.SUCCESS) {
//                    list.addAll(response.getData());
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Throwable t) {            }
//
//        });
//    }
}