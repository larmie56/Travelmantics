package com.example.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity {

    public static final int PICTURE_RESULT = 42;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText mDealTitle;
    private EditText mDealPrice;
    private EditText mDealDescription;
    private ImageView mImageView;
    TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("travelDeals");

        mDealTitle = findViewById(R.id.editText_travel_deal_title);
        mDealPrice = findViewById(R.id.editText_travel_deal_price);
        mDealDescription = findViewById(R.id.editText_travel_deal_description);
        mImageView = findViewById(R.id.image);

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }

        this.deal = deal;

        mDealTitle.setText(deal.getTitle());
        mDealDescription.setText(deal.getDescription());
        mDealPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());

        Button imageButton = findViewById(R.id.button_image);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent,
                        "Insert picture"), PICTURE_RESULT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final StorageReference ref = FirebaseUtil.mStorageReference.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String imageName = taskSnapshot.getStorage().getPath();
                    deal.setImageName(imageName);
                    Log.d("Image name", imageName);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("Url", uri.toString());
                            deal.setImageUrl(uri.toString());
                            showImage(uri.toString());
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_travel_deal, menu);
        if (FirebaseUtil.isAdmin) {
            menu.findItem(R.id.menu_save_travel_deal).setVisible(true);
            menu.findItem(R.id.menu_delete_travel_deal).setVisible(true);
            enableText(true);
            findViewById(R.id.button_image).setEnabled(true);
        }
        else {
            menu.findItem(R.id.menu_save_travel_deal).setVisible(false);
            menu.findItem(R.id.menu_delete_travel_deal).setVisible(false);
            enableText(false);
            findViewById(R.id.button_image).setEnabled(false);
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
        Log.d("Image name", deal.getImageName());
        if (deal.getImageName() != null && deal.getImageName().isEmpty() == false) {
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(deal.getImageName());
            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Deleted", "Deleted successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Deleted", e.getMessage());
                }
            });
        }
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

    private void showImage(String url) {
        if (url != null && url.isEmpty() == false) {
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this).load(url).resize(width, width*7/10).centerCrop().into(mImageView);
        }
    }
}
