package com.prosper.mezzomicrosavings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Contacts> listContacts;
    private ArrayList<Contacts> mArrayList;

    private SqliteDatabase mDatabase;

    public ContactAdapter(Context context, ArrayList<Contacts> listContacts) {
        this.context = context;
        this.listContacts = listContacts;
        this.mArrayList=listContacts;
        mDatabase = new SqliteDatabase(context);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_layout, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final Contacts contacts = listContacts.get(position);

        holder.name.setText(contacts.getName());
        holder.balance.setText(contacts.getBalance());
        holder.ph_no.setText(contacts.getPhno());

        holder.deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                depositTaskDialog(contacts);
            }
        });

        holder.withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                withdrawTaskDialog(contacts);
            }
        });

        holder.editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTaskDialog(contacts);
            }
        });

        holder.deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete row from database

                deleteTaskDialog(contacts);
            }
        });
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    listContacts = mArrayList;
                } else {

                    ArrayList<Contacts> filteredList = new ArrayList<>();

                    for (Contacts contacts : mArrayList) {

                        if (contacts.getName().toLowerCase().contains(charString)) {

                            filteredList.add(contacts);
                        }
                    }

                    listContacts = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listContacts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listContacts = (ArrayList<Contacts>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public int getItemCount() {
        return listContacts.size();
    }


    private void editTaskDialog(final Contacts contacts){
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.editcontact_layout, null);

        final EditText nameField = (EditText)subView.findViewById(R.id.etCustumernameedit);
        final EditText contactField = (EditText)subView.findViewById(R.id.etcustumerPhoneedit);

        if(contacts != null){
            nameField.setText(contacts.getName());
            contactField.setText(String.valueOf(contacts.getPhno()));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("EDIT CUSTOMER'S DETAILS");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String name = nameField.getText().toString();
                final String ph_no = contactField.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(context, "Oppss!! Something went wrong\nUnable to save new details", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(ph_no)){
                    Toast.makeText(context, "Oppss!! Something went wrong\nUnable to save new details", Toast.LENGTH_LONG).show();
                }
                else{
                    assert contacts != null;
                    mDatabase.updateContacts(new Contacts(contacts.getId(), name, contacts.getBalance(), ph_no));
                    //refresh the activity
                    ((Activity)context).finish();
                    context.startActivity(((Activity)context).getIntent());
                    Toast.makeText(context, "Customer details saved successfully", Toast.LENGTH_SHORT).show();
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

    private void depositTaskDialog(final Contacts contacts) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.withdraw_layout, null);

        final TextView nameField = (TextView) subView.findViewById(R.id.tvCustumernameShow);
        final EditText amountField = (EditText)subView.findViewById(R.id.etcustumerbal);

        if(contacts != null){
            nameField.setText(contacts.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("DEPOSIT");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("DEPOSIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                assert contacts != null;
                final String balance = contacts.getBalance();
                final String Amount = amountField.getText().toString();

                if(TextUtils.isEmpty(Amount)){
                    Toast.makeText(context, "Deposit not successful, Try again", Toast.LENGTH_LONG).show();
                }
                else{
                    long BALANCE = Long.parseLong(balance);
                    long AMount = Long.parseLong(Amount);

                    long NewBal = BALANCE + AMount;
                    String NEWBALANCE = Long.toString(NewBal);

                    mDatabase.updateBalance(new Contacts( contacts.getId(), contacts.getName(), NEWBALANCE, contacts.getPhno()));
                    //refresh the activity
                    ((Activity)context).finish();
                    context.startActivity(((Activity)context).getIntent());
                    Toast.makeText(context, "Deposit successful", Toast.LENGTH_SHORT).show();

                    try{
                        String Textmsg = "MEZZO MICRO SAVINGS\n<<<< CREDIT ALERT >>>>\nNAME : " + contacts.getName() + "\nAMOUNT DEPOSITED (NGN) : " + Amount + "\nBALANCE (NGN) : " + NEWBALANCE;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse("smsto:"));
                        i.setType("vnd.android-dir/mms-sms");
                        i.putExtra("address", new String(contacts.getPhno()));
                        i.putExtra("sms_body",Textmsg);
                        context.startActivity(Intent.createChooser(i, "Send sms via:"));
                    }
                    catch(Exception e){
                        Toast.makeText(context, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
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

    private void withdrawTaskDialog(final Contacts contacts) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.withdraw_layout, null);

        final TextView nameField = (TextView) subView.findViewById(R.id.tvCustumernameShow);
        final EditText amountField = (EditText)subView.findViewById(R.id.etcustumerbal);

        if(contacts != null){
            nameField.setText(contacts.getName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("WITHDRAWAL");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("WITHDRAW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                assert contacts != null;
                final String balance = contacts.getBalance();
                final String Amount = amountField.getText().toString();

                if(TextUtils.isEmpty(Amount)){
                    Toast.makeText(context, "Withdrawal not successful, Try again", Toast.LENGTH_LONG).show();
                }
                else{
                    long BALANCE = Long.parseLong(balance);
                    long AMount = Long.parseLong(Amount);
                    if (AMount > BALANCE) {
                        Toast.makeText(context, "Enter lesser amount", Toast.LENGTH_LONG).show();
                    }
                    else {
                        long NewBal = BALANCE - AMount;
                        String NEWBALANCE = Long.toString(NewBal);

                        mDatabase.updateBalance(new Contacts( contacts.getId(), contacts.getName(), NEWBALANCE, contacts.getPhno()));
                        //refresh the activity
                        ((Activity)context).finish();
                        context.startActivity(((Activity)context).getIntent());
                        Toast.makeText(context, "Withdrawal successful", Toast.LENGTH_SHORT).show();

                        try{
                            String Textmsg = "MEZZO MICRO SAVINGS\n<<<< DEBIT ALERT >>>>\nNAME : " + contacts.getName() + "\nAMOUNT WITHDRAWN (NGN) : " + Amount + "\nBALANCE (NGN) : " + NEWBALANCE;
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse("smsto:"));
                            i.setType("vnd.android-dir/mms-sms");
                            i.putExtra("address", new String(contacts.getPhno()));
                            i.putExtra("sms_body",Textmsg);
                            context.startActivity(Intent.createChooser(i, "Send sms via:"));
                        }
                        catch(Exception e){
                            Toast.makeText(context, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                        }
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

    private void deleteTaskDialog(final Contacts contacts) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View subView = inflater.inflate(R.layout.delete_contact_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("REMOVE CUSTOMER");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDatabase.deleteContact(contacts.getId());
                Toast.makeText(context, "Customer Removed Successfully", Toast.LENGTH_SHORT).show();
                //refresh the activity page.
                ((Activity)context).finish();
                context.startActivity(((Activity) context).getIntent());
                try{
                    String Textmsg = "Dear " + contacts.getName() + ", Thank you for patronizing MEZZO MICRO SAVINGS.\nWe wish you the best in everything you do";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("smsto:"));
                    i.setType("vnd.android-dir/mms-sms");
                    i.putExtra("address", new String(contacts.getPhno()));
                    i.putExtra("sms_body",Textmsg);
                    context.startActivity(Intent.createChooser(i, "Send sms via:"));
                }
                catch(Exception e){
                    Toast.makeText(context, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}