package com.example.struggler.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends CursorAdapter {

    public ContactAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_contact, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            TextView tvNamaContact = view.findViewById(R.id.tv_contact);
            CircleImageView imgContact = view.findViewById(R.id.img_contact);

            tvNamaContact.setText(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));

            if (cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)) != null) {
                imgContact.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI))));
            } else {
                imgContact.setImageResource(R.mipmap.ic_launcher_round);
            }
        }
    }
}
