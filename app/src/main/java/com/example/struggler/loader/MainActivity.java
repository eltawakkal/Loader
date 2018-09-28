package com.example.struggler.loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener{

    private static final String TAG = "contactApp";

    ListView lvContact;
    ProgressBar pgbContact;
    ContactAdapter adapter;

    private static final int CONTACT_LOAD_ID = 110;
    private static final int CONTACT_PHONE_ID = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvContact = findViewById(R.id.lv_contact);
        pgbContact = findViewById(R.id.pgb_contact);

        setListItems(null);

        pgbContact.setVisibility(View.VISIBLE);
        lvContact.setVisibility(View.GONE);
        lvContact.setAdapter(adapter);
        lvContact.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(CONTACT_LOAD_ID, null, this);
    }

    public void setListItems(Cursor cursor) {
        adapter = new ContactAdapter(MainActivity.this, cursor, true);
        lvContact.setAdapter(adapter);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        CursorLoader mCursorLoader = null;
        if (id == CONTACT_LOAD_ID) {
            String[] projectionFields = new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_URI};

            mCursorLoader = new CursorLoader(MainActivity.this,
                    ContactsContract.Contacts.CONTENT_URI,
                    projectionFields,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1",
                    null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        }

        if (id == CONTACT_PHONE_ID) {
            String[] phoneProjectionFields = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER};
            mCursorLoader = new CursorLoader(MainActivity.this,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    phoneProjectionFields,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                    ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + " AND " +
                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1",
                    new String[]{bundle.getString("id")},
                    null);
        }
        return mCursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "Load Finished");

        if (loader.getId() == CONTACT_LOAD_ID) {
            if (cursor.getCount() > 0) {
                setListItems(cursor);
                lvContact.setVisibility(View.VISIBLE);
                pgbContact.setVisibility(View.GONE);
            }
        }
        if (loader.getId() == CONTACT_PHONE_ID) {
            String contactNumber = null;
            if (cursor.moveToFirst()) {
                contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }

//            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactNumber));
//            startActivity(dialIntent);

            Toast.makeText(this, "His Phone Number Is : " + contactNumber, Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == CONTACT_LOAD_ID) {
            lvContact.setVisibility(View.INVISIBLE);
            pgbContact.setVisibility(View.VISIBLE);
            adapter.swapCursor(null);
            Log.d(TAG, "Load Reset");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(position);

        long contactID = cursor.getLong(0);
        Log.d(TAG, "Positoin : " + position + ", " + contactID);
        getPhoneNumber(String.valueOf(contactID));
    }

    private void getPhoneNumber(String contactID) {
        Bundle bundle = new Bundle();
        bundle.putString("id", contactID);
        getSupportLoaderManager().restartLoader(CONTACT_PHONE_ID, bundle, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
