package com.example.lockin_prototype;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseActivity extends AppCompatActivity {
    public EditText eTKey, eTcontent;
    public ListView lVData;

    // firebase database reference
    public DatabaseReference refDatabase;

    public List<String> dataList  = new ArrayList<>();
    public ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        eTKey = findViewById(R.id.eTKey);
        eTcontent = findViewById(R.id.eTcontent);
        lVData = findViewById(R.id.lVData);

        // get the firebase database root reference and set the main node name ("records")
        refDatabase = FirebaseDatabase.getInstance().getReference("records");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        lVData.setAdapter(adapter);

        // start listening for data changes immediately
        loadDataFromFirebase();
    }

    private void saveData() {
        String recordId = eTKey.getText().toString().trim();
        String content = eTcontent.getText().toString().trim();

        if (recordId.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "please enter both key and content.", Toast.LENGTH_SHORT).show();
            return;
        }

        // get a reference to the specific record node using the user-provided ID
        DatabaseReference recordRef = refDatabase.child(recordId);

        // set the key of that node
        recordRef.child("id").setValue(recordId);

        // set the value of that node
        recordRef.child("content").setValue(content)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "record saved successfully!", Toast.LENGTH_SHORT).show();
                        // clear fields
                        eTKey.setText("");
                        eTcontent.setText("");
                    }
                    else {
                        Toast.makeText(this, "failed to save record: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void loadDataFromFirebase() {
        refDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();

                // iterate over all records
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // we use getValue(String.class) for direct string extraction
                    String id = snapshot.child("id").getValue(String.class);
                    String content = snapshot.child("content").getValue(String.class);

                    // create a string from data
                    if (id != null && content != null) {
                        dataList.add(id + " : " + content);
                    }
                }

                // tell the adapter that the underlying dataList has changed
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // handle errors
                Toast.makeText(getApplicationContext(), "failed to load data: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onEnter(View view) {
        saveData();
    }
}