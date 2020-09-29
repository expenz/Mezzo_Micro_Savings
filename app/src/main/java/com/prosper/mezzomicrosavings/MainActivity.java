package com.prosper.mezzomicrosavings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private SqliteDatabase mDatabase;
    private ArrayList<Contacts> allContacts = new ArrayList<>();
    private ContactAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView contactView = (RecyclerView)findViewById(R.id.product_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        contactView.setLayoutManager(linearLayoutManager);
        contactView.setHasFixedSize(true);
        mDatabase = new SqliteDatabase(this);
        allContacts = mDatabase.listContacts();

        if(allContacts.size() > 0){
            contactView.setVisibility(View.VISIBLE);
            mAdapter = new ContactAdapter(this, allContacts);
            contactView.setAdapter(mAdapter);

        }else {
            contactView.setVisibility(View.GONE);
            TextView tvempty = findViewById(R.id.tvempty);
            tvempty.setVisibility(View.VISIBLE);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTaskDialog();
            }
        });
    }

    private void addTaskDialog(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View subView = inflater.inflate(R.layout.add_contact_layout, null);

        final EditText nameField = (EditText)subView.findViewById(R.id.etCustumername);
        final EditText balanceField = (EditText)subView.findViewById(R.id.etcustumerAmount);
        final EditText noField = (EditText)subView.findViewById(R.id.etcustumerPhone);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ADD NEW CUSTOMER");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();
                final String balance = balanceField.getText().toString();
                final String ph_no = noField.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(MainActivity.this, "Oppss!! Something went wrong\nUnable to add customer", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(balance)){
                    Toast.makeText(MainActivity.this, "Oppss!! Something went wrong\nUnable to add customer", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(ph_no)){
                    Toast.makeText(MainActivity.this, "Oppss!! Something went wrong\nUnable to add customer", Toast.LENGTH_LONG).show();
                }
                else{
                    Contacts newContact = new Contacts(name, balance, ph_no);
                    mDatabase.addContacts(newContact);
                    Toast.makeText(MainActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());

                    try{
                        String Textmsg = "MEZZO MICRO SAVINGS\n<<<< CREDIT ALERT >>>>\nNAME : " + newContact.getName() + "\nFIRST DEPOSIT AMOUNT (NGN) : " + balance + "\nBALANCE (NGN) : " + newContact.getBalance();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("smsto:"));
                        i.setType("vnd.android-dir/mms-sms");
                        i.putExtra("address", new String(newContact.getPhno()));
                        i.putExtra("sms_body",Textmsg);
                        startActivity(Intent.createChooser(i, "Send sms via:"));
                    }
                    catch(Exception e){
                        Toast.makeText(MainActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDatabase != null){
            mDatabase.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mAdapter!=null)
                    mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}