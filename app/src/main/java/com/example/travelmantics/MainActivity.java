package com.example.travelmantics;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText mDealTitle;
    private EditText mDealPrice;
    private EditText mDealDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("travelDeals");

        mDealTitle = findViewById(R.id.editText_travel_deal_title);
        mDealPrice = findViewById(R.id.editText_travel_deal_price);
        mDealDescription = findViewById(R.id.editText_travel_deal_description);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_travel_deal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_travel_deal:
                saveDeal();
                Toast.makeText(this, "Deal saved!", Toast.LENGTH_LONG).show();
                clean();
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
        String title = mDealTitle.getText().toString();
        String price = mDealPrice.getText().toString();
        String imageUrl = "";

        TravelDeal deal = new TravelDeal(title, price, imageUrl);

        mDatabaseReference.push().setValue(deal);
    }
}
