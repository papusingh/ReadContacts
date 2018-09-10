package androidbuffer.com.readcontactdemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private List<ContactModel> contactModelList = new ArrayList<>();
    ContactAdapter contactAdapter;
    private static String[] PERMISSION_CONTACT = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private static final int REQUEST_CONTACT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv);
        setDataToAdapter();
        requestContactsPermissions();
    }

    private void setDataToAdapter(){
        contactAdapter = new ContactAdapter(contactModelList);
        initRecyclerView();
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contactAdapter);
    }

    private void getContactInfo(){
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PHONE_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String PHONE_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI,null,null,null,DISPLAY_NAME);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String CONTACT_ID = cursor.getString(cursor.getColumnIndex(ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(HAS_PHONE_NUMBER));
                ContactModel contactModel = new ContactModel();
                if (hasPhoneNumber > 0) {
                    contactModel.setName(name);

                    Cursor phoneCursor = contentResolver.query(PHONE_URI, new String[]{NUMBER}, PHONE_ID + " = ?", new String[]{CONTACT_ID}, null);
                    List<String> contactList = new ArrayList<>();
                    assert phoneCursor != null;
                    phoneCursor.moveToFirst();
                    while (!phoneCursor.isAfterLast()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).replace(" ", "");
                        contactList.add(phoneNumber);
                        phoneCursor.moveToNext();
                    }
                    contactModel.setNumber(contactList);
                    contactModelList.add(contactModel);
                    phoneCursor.close();
                }
            }
            contactAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    public void requestContactsPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_CONTACTS) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)){

                Snackbar.make(recyclerView, "permission Contact", Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(MainActivity.this,PERMISSION_CONTACT,REQUEST_CONTACT);
                            }
                        }).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,PERMISSION_CONTACT,REQUEST_CONTACT);
            }
        } else {
            getContactInfo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults){
            if (result == PackageManager.PERMISSION_GRANTED){
                getContactInfo();
            }
        }
    }
}
