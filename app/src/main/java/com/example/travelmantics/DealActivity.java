package com.example.travelmantics;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DealActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText mDealTitle;
    private EditText mDealPrice;
    private EditText mDealDescription;
    TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("travelDeals");

        mDealTitle = findViewById(R.id.editText_travel_deal_title);
        mDealPrice = findViewById(R.id.editText_travel_deal_price);
        mDealDescription = findViewById(R.id.editText_travel_deal_description);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }

        this.deal = deal;

        mDealTitle.setText(deal.getTitle());
        mDealDescription.setText(deal.getDescription());
        mDealPrice.setText(deal.getPrice());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_travel_deal, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.menu_save_travel_deal).setVisible(true);
            menu.findItem(R.id.menu_delete_travel_deal).setVisible(true);
            enableText(true);
        }
        else {
            menu.findItem(R.id.menu_save_travel_deal).setVisible(false);
            menu.findItem(R.id.menu_delete_travel_deal).setVisible(false);
            enableText(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_travel_deal:
                saveDeal();
                Toast.makeText(this, "Deal saved!", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            case R.id.menu_delete_travel_deal:
                deleteDeal();
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void clean() {
        mDealTitle.setText("");
        mDealPrice.setText("");
        mDealDescription.setText("");
    }

    private void saveDeal() {
        deal.setTitle(mDealTitle.getText().toString());
        deal.setDescription(mDealDescription.getText().toString());
        deal.setPrice(mDealPrice.getText().toString());
        String imageUrl = "";

        if (deal.getId() == null) {
            mDatabaseReference.push().setValue(deal);
        }

        else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }

    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Deal does not exist", Toast.LENGTH_LONG).show();
            return;
        }

        mDatabaseReference.child(deal.getId()).removeValue();
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void enableText(boolean isEnabled) {
        mDealTitle.setEnabled(isEnabled);
        mDealDescription.setEnabled(isEnabled);
        mDealPrice.setEnabled(isEnabled);
    }
}
