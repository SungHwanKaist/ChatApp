package com.hems.socketio.client.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

public class MyContentProvider extends ContentProvider {
    SQLiteHelper sqLiteHelper;
    // used for the UriMacher
    private static final int CHAT = 1;
    private static final int CHAT_ID = 2;
    private static final int CONTACT = 3;
    private static final int CONTACT_ID = 4;
    private static final int MESSAGE = 5;
    private static final int CHAT_MESSAGES = 6;
    private static final int CHAT_MESSAGES_ID = 7;
    private static final int MESSAGE_ID = 8;
    private static final int CHAT_WITH_MESSAGE = 9;

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableChat.BASE_PATH, CHAT);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableChat.BASE_PATH + "/*", CHAT_ID);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableContact.BASE_PATH, CONTACT);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableContact.BASE_PATH + "/*", CONTACT_ID);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableMessage.BASE_PATH, MESSAGE);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableMessage.PATH_CHAT_MESSAGES, CHAT_MESSAGES);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableMessage.PATH_CHAT_MESSAGES + "/*", CHAT_MESSAGES_ID);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableMessage.BASE_PATH + "/*", MESSAGE_ID);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TableChat.PATH_CHAT_WITH_LAST_MESSAGE, CHAT_WITH_MESSAGE);
    }

    public MyContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = sqLiteHelper.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case CHAT:
                rowsDeleted = sqlDB.delete(DatabaseContract.TableChat.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case CHAT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            DatabaseContract.TableChat.TABLE_NAME,
                            DatabaseContract.TableChat.COLUMN_ID + "='" + id + "'",
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            DatabaseContract.TableChat.TABLE_NAME,
                            DatabaseContract.TableChat.COLUMN_ID + "='" + id + "'"
                                    + " AND " + selection,
                            selectionArgs);
                }
                break;
            case CONTACT:
                rowsDeleted = sqlDB.delete(DatabaseContract.TableContact.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case CONTACT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            DatabaseContract.TableContact.TABLE_NAME,
                            DatabaseContract.TableContact.COLUMN_ID + "='" + id + "'",
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            DatabaseContract.TableContact.TABLE_NAME,
                            DatabaseContract.TableContact.COLUMN_ID + "='" + id + "'"
                                    + " AND " + selection,
                            selectionArgs);
                }
                break;
            case MESSAGE:
                rowsDeleted = sqlDB.delete(DatabaseContract.TableMessage.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case MESSAGE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            DatabaseContract.TableMessage.TABLE_NAME,
                            DatabaseContract.TableMessage.COLUMN_ID + "='" + id + "'",
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            DatabaseContract.TableMessage.TABLE_NAME,
                            DatabaseContract.TableMessage.COLUMN_ID + "='" + id + "'"
                                    + " AND " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = sqLiteHelper.getWritableDatabase();
        Uri returnUri = null;
        switch (uriType) {
            case CHAT:
                long id = sqlDB.insert(DatabaseContract.TableChat.TABLE_NAME, null, values);
                returnUri = Uri.parse(DatabaseContract.TableChat.BASE_PATH + "/" + id);
                break;
            case CONTACT:
                id = sqlDB.insert(DatabaseContract.TableContact.TABLE_NAME, null, values);
                returnUri = Uri.parse(DatabaseContract.TableContact.BASE_PATH + "/" + id);
                break;
            case MESSAGE:
                id = sqlDB.insert(DatabaseContract.TableMessage.TABLE_NAME, null, values);
                returnUri = Uri.parse(DatabaseContract.TableMessage.BASE_PATH + "/" + id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        sqLiteHelper = new SQLiteHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String mQuery = null;
        // check if the caller has requested a column which does not exists
        // Set the table
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CHAT:
                queryBuilder.setTables(DatabaseContract.TableChat.TABLE_NAME);
                break;
            case CHAT_ID:
                // adding the ID to the original query
                queryBuilder.setTables(DatabaseContract.TableChat.TABLE_NAME);
                queryBuilder.appendWhere(DatabaseContract.TableChat.COLUMN_ID + "='"
                        + uri.getLastPathSegment() + "'");
                break;
            case CONTACT:
                queryBuilder.setTables(DatabaseContract.TableContact.TABLE_NAME);
                break;
            case CONTACT_ID:
                // adding the ID to the original query
                queryBuilder.setTables(DatabaseContract.TableContact.TABLE_NAME);
                queryBuilder.appendWhere(DatabaseContract.TableContact.COLUMN_ID + "='"
                        + uri.getLastPathSegment() + "'");
                break;
            case MESSAGE:
            case MESSAGE_ID:
                queryBuilder.setTables(DatabaseContract.TableMessage.TABLE_NAME);
                if (uriType == MESSAGE_ID) {
                    queryBuilder.appendWhere(DatabaseContract.TableMessage.COLUMN_ID + "='"
                            + uri.getLastPathSegment() + "'");
                }
                break;
            case CHAT_MESSAGES:
            case CHAT_MESSAGES_ID:
                mQuery = "SELECT C." + DatabaseContract.TableContact.COLUMN_NAME + " AS contact_name " +
                        ",M." + DatabaseContract.TableMessage.COLUMN_SENDER_NAME + " AS sender_name " +
                        ",M." + DatabaseContract.TableMessage.COLUMN_SENDER_ID +
                        ",M." + DatabaseContract.TableMessage.COLUMN_MESSAGE +
                        ",M." + DatabaseContract.TableMessage.COLUMN_TYPE +
                        ",M." + DatabaseContract.TableMessage.COLUMN_IMAGE_URL +
                        ",M." + DatabaseContract.TableMessage.COLUMN_CREATE_DATE + " " +
                        "FROM " + DatabaseContract.TableMessage.TABLE_NAME + " M " +
                        "LEFT JOIN " + DatabaseContract.TableContact.TABLE_NAME + " C " +
                        "ON M." + DatabaseContract.TableMessage.COLUMN_SENDER_ID + "=C." + DatabaseContract.TableContact.COLUMN_ID + " " +
                        "WHERE M." + DatabaseContract.TableMessage.COLUMN_CHAT_ID + "='" + ((uriType == CHAT_MESSAGES) ? "?" : uri.getLastPathSegment()) + "' " +
                        "ORDER BY M." + DatabaseContract.TableMessage.COLUMN_CREATE_DATE + " ASC " +
                        "LIMIT 20 OFFSET (SELECT COUNT(*) FROM " + DatabaseContract.TableMessage.TABLE_NAME + " " +
                        "WHERE " + DatabaseContract.TableMessage.COLUMN_CHAT_ID + "='" + ((uriType == CHAT_MESSAGES) ? "?" : uri.getLastPathSegment()) + "')-20";
                break;
            case CHAT_WITH_MESSAGE:
                mQuery = "SELECT C." + DatabaseContract.TableChat.COLUMN_ID +
                        ",C." + DatabaseContract.TableChat.COLUMN_NAME +
                        ",C." + DatabaseContract.TableChat.COLUMN_TYPE +
                        ",C." + DatabaseContract.TableChat.COLUMN_USERS +
                        ",C." + DatabaseContract.TableChat.COLUMN_ADMIN_IDS +
                        ",C." + DatabaseContract.TableChat.COLUMN_LAST_MESSAGE_ID +
                        ",C." + DatabaseContract.TableChat.COLUMN_UPDATE_DATE +
                        ",M." + DatabaseContract.TableMessage.COLUMN_MESSAGE +
                        ",M." + DatabaseContract.TableMessage.COLUMN_IMAGE_URL + " " +
                        "FROM " + DatabaseContract.TableChat.TABLE_NAME + " C " +
                        "LEFT JOIN " + DatabaseContract.TableMessage.TABLE_NAME + " M " +
                        "ON M." + DatabaseContract.TableMessage.COLUMN_ID + "=C." + DatabaseContract.TableChat.COLUMN_LAST_MESSAGE_ID + " " +
                        "ORDER BY C." + DatabaseContract.TableChat.COLUMN_UPDATE_DATE + " DESC ";
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor cursor;
        SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
        if (!TextUtils.isEmpty(mQuery)) {
            cursor = db.rawQuery(mQuery, selectionArgs);
        } else {
            cursor = queryBuilder.query(db, projection, selection,
                    selectionArgs, null, null, sortOrder);
        }
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = sqLiteHelper.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case CHAT:
                rowsUpdated = sqlDB.update(DatabaseContract.TableChat.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CHAT_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DatabaseContract.TableChat.TABLE_NAME,
                            values,
                            DatabaseContract.TableChat.COLUMN_ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = sqlDB.update(DatabaseContract.TableChat.TABLE_NAME,
                            values,
                            DatabaseContract.TableChat.COLUMN_ID + "='" + id + "'"
                                    + " AND "
                                    + selection,
                            selectionArgs);
                }
                break;
            case CONTACT:
                rowsUpdated = sqlDB.update(DatabaseContract.TableContact.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CONTACT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DatabaseContract.TableContact.TABLE_NAME,
                            values,
                            DatabaseContract.TableContact.COLUMN_ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = sqlDB.update(DatabaseContract.TableContact.TABLE_NAME,
                            values,
                            DatabaseContract.TableContact.COLUMN_ID + "='" + id + "'"
                                    + " AND "
                                    + selection,
                            selectionArgs);
                }
                break;
            case MESSAGE:
                rowsUpdated = sqlDB.update(DatabaseContract.TableMessage.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case MESSAGE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DatabaseContract.TableMessage.TABLE_NAME,
                            values,
                            DatabaseContract.TableMessage.COLUMN_ID + "=?",
                            new String[]{id});
                } else {
                    rowsUpdated = sqlDB.update(DatabaseContract.TableMessage.TABLE_NAME,
                            values,
                            DatabaseContract.TableMessage.COLUMN_ID + "='" + id + "'"
                                    + " AND "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
