package com.example.callproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Variable Declaration

    private EditText number_input;
    private Button call_btn;

    ListView usersList;
    ArrayAdapter<String> adapter;

    ArrayList<String> usersData;


    //Request Code
    private int request_Code = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variable Initialization


        usersList          = findViewById(R.id.users_list);
        number_input = (EditText) findViewById(R.id.number_input);
        call_btn = (Button) findViewById(R.id.call_btn);

        PrintUsers();

        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int check_permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE);

                if(check_permission == PackageManager.PERMISSION_GRANTED){
                    makePhoneCall();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CALL_PHONE},request_Code );
                }
            }
        });

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                number_input.setText(usersData.get(position));
            }
        });
    }

    @SuppressLint("Range")
    private void PrintUsers()
    {
        int check_permission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS);

        if(check_permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_CONTACTS},request_Code+1 );
        }

        //Uri uri = ContactsContract.Contacts.CONTENT_URI;
        //Cursor contacts = getContentResolver().query(uri, null, null, null, null);

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor contacts = getContentResolver().query(uri, null, null, null, null);

        usersData = new ArrayList<>();

        if (contacts.moveToFirst())
        {
            do
            {
                //usersData.add(contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                usersData.add(contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            } while (contacts.moveToNext());
        }

        adapter = new ArrayAdapter<>
                (
                        this,
                        android.R.layout.simple_list_item_1,
                        usersData
                );

        usersList.setAdapter(adapter);
    }

    private void makePhoneCall() {

        String phone_Number = number_input.getText().toString();

        if(phone_Number.trim().length()>0 ){            // it will trim or dlt empty spaces

            String dial = "tel:" + phone_Number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }else {
            Toast.makeText(this,"Enter Phone Number",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 77:
                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makePhoneCall();
                } else {
                    Toast.makeText(this, "You Don't have permission", Toast.LENGTH_LONG).show();
                }
        }
    }
}