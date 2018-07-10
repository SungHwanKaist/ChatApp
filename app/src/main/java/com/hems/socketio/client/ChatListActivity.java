package com.hems.socketio.client;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.hems.socketio.client.adapter.ChatListRecyclerAdapter;
import com.hems.socketio.client.api.ChatService;
import com.hems.socketio.client.api.RetrofitCall;
import com.hems.socketio.client.api.RetrofitCallback;
import com.hems.socketio.client.api.Service;
import com.hems.socketio.client.enums.ChatType;
import com.hems.socketio.client.model.Chat;
import com.hems.socketio.client.provider.DatabaseContract;
import com.hems.socketio.client.provider.QueryUtils;
import com.hems.socketio.client.provider.SQLiteHelper;
import com.hems.socketio.client.service.SocketIOService;
import com.hems.socketio.client.sync.ChatSyncAdapter;
import com.hems.socketio.client.utils.FileUtils;
import com.hems.socketio.client.utils.PermissionUtils;
import com.hems.socketio.client.utils.SessionManager;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity
        implements ChatListRecyclerAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private RecyclerView recyclerView;
    private ChatListRecyclerAdapter adapter;
    private ArrayList<Chat> list;
    private SessionManager sessionManager;
    private Chat mChat;
    private FloatingActionMenu fabMenu;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = SessionManager.newInstance(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        Intent service = new Intent(this, SocketIOService.class);
        service.putExtra(SocketIOService.EXTRA_EVENT_TYPE, SocketIOService.EVENT_TYPE_JOIN);
        service.putExtra(SocketIOService.EXTRA_USER_NAME, sessionManager.getUserId());
        startService(service);

        setContentView(R.layout.activity_chat_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        parentLayout = findViewById(R.id.parentLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new ChatListRecyclerAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        findViewById(R.id.menu_add_chat).setOnClickListener(this);
        findViewById(R.id.menu_add_contact).setOnClickListener(this);
        // perform chat sync
        ChatSyncAdapter.performSync();
        getLoaderManager().initLoader(101, null, this);
        /*if (PermissionUtils.checkForPermission(this,
                PermissionUtils.PERMISSION_READ_STORAGE,
                PermissionUtils.PERMISSION_READ_STORAGE_REQ)) {
            FileUtils.backUpSqliteDb();
        }*/
        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (fabMenu.isOpened()) {
                    fabMenu.close(true);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_list_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.menu_title_logout);
            builder.setMessage("Are you sure want to logout?");
            builder.setPositiveButton(R.string.menu_title_logout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SQLiteHelper.deleteDatabase(getApplicationContext());
                    sessionManager.logoutUser(ChatListActivity.this);
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.menu_title_chat_options);
        menu.add(Menu.NONE, 101, 1, R.string.menu_title_delete_chat);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 101) {
            if (mChat.getType() == ChatType.PERSONAL) {
                Toast.makeText(this, "Personal chat can not be deleted!", Toast.LENGTH_SHORT).show();
            } else if (mChat != null) {
                deleteGroupChat(mChat.getId());
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        Chat chat = adapter.getItem(position);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_DATA, chat);
        startActivity(intent);
    }

    @Override
    public void onLongClick(View v, int position) {
        mChat = adapter.getItem(position);
        registerForContextMenu(v);
    }

    private void deleteGroupChat(final String chatId) {
        ChatService request = (ChatService) RetrofitCall.createRequest(ChatService.class);
        request.deleteChat(sessionManager.getUserId(), chatId).enqueue(new RetrofitCallback<Chat>() {
            @Override
            public void onResponse(Chat response) {
                if (response.getStatus() == Service.SUCCESS) {
                    Toast.makeText(ChatListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatListActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mChat = null;
                QueryUtils.deleteChat(ChatListActivity.this, chatId);
            }

            @Override
            public void onFailure(Throwable t) {
                mChat = null;
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, DatabaseContract.TableChat.CONTENT_URI_CHAT_WITH_LAST_MESSAGE,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        ArrayList<Chat> chats = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Chat chat = new Chat(cursor);
                    chats.add(chat);
                } while (cursor.moveToNext());
            }
        }
        adapter.setDatas(chats);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_add_chat:
                Intent intent = new Intent(ChatListActivity.this, CreateChatActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_add_contact:
                intent = new Intent(ChatListActivity.this, CreateContactActivity.class);
                startActivity(intent);
                break;

        }
    }
}
