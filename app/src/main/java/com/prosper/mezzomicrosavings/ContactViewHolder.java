package com.prosper.mezzomicrosavings;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public TextView name, balance, ph_no;
    public ImageView deposit;
    public ImageView withdraw;
    public ImageView deleteContact;
    public  ImageView editContact;

    public ContactViewHolder(View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.custumername);
        balance = (TextView)itemView.findViewById(R.id.custumerbalance);
        ph_no = (TextView)itemView.findViewById(R.id.custumerphone);
        deposit = (ImageView)itemView.findViewById(R.id.deposit);
        withdraw = (ImageView)itemView.findViewById(R.id.withdraw);
        deleteContact = (ImageView)itemView.findViewById(R.id.delete_contact);
        editContact = (ImageView)itemView.findViewById(R.id.edit);
    }
}