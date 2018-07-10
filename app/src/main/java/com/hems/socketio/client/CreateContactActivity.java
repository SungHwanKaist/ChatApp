package com.hems.socketio.client;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hems.socketio.client.adapter.ContactRecyclerAdapter;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.RetrofitCallback;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.api.UserService;
import com.hems.socketio.client.model.Contact;
import com.hems.socketio.client.model.Response;
import com.hems.socketio.client.provider.QueryUtils;
import com.hems.socketio.client.utils.SessionManager;

import java.util.ArrayList;

public class CreateContactActivity extends AppCompatActivity implements ContactRecyclerAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ContactRecyclerAdapter adapter;
    private ArrayList<Contact> list;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = SessionManager.newInstance(this);

        setContentView(R.layout.activity_contact_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation()));
        list = new ArrayList<>();
        adapter = new ContactRecyclerAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        getContacts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_create_chat) {
            ArrayList<String> contacts = new ArrayList<>();
            ArrayList<Contact> contactList = new ArrayList<>();
            for (Contact contact : adapter.getDatas()) {
                if (contact.isSelected()) {
                    contacts.add(contact.getId());
                    contactList.add(contact);
                }
            }
            createContact(contacts, contactList);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        adapter.getItem(position).setSelected(!adapter.getItem(position).isSelected());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCheckedChange(CompoundButton buttonView, boolean isChecked, int position) {
        adapter.getItem(position).setSelected(isChecked);
        adapter.notifyDataSetChanged();
    }

    private void createContact(ArrayList<String> users, final ArrayList<Contact> contactList) {
        progressDialog = ProgressDialog.show(this, getString(R.string.app_name),
                "Creating contact", false);
        UserService request = (UserService) RetrofitCall.createRequest(UserService.class);
        request.createContact(sessionManager.getUserId(), users).enqueue(new RetrofitCallback<Response<String>>() {
            @Override
            public void onResponse(Response<String> response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                Toast.makeText(CreateContactActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                if (response.getStatus() == Service.SUCCESS) {
                    QueryUtils.addContact(getApplicationContext(), contactList);
                    finish();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(CreateContactActivity.this, "Failed to create contact", Toast.LENGTH_SHORT).show();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        });
    }

    private void getContacts() {
        UserService request = (UserService) RetrofitCall.createRequest(UserService.class);
        request.getUserList(sessionManager.getUserId()).enqueue(new RetrofitCallback<Contact>() {
            @Override
            public void onResponse(Contact response) {
                if (response.getStatus() == Service.SUCCESS) {
                    list.addAll(response.getData());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CreateContactActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

}
