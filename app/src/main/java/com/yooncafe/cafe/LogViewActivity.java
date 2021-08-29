package com.yooncafe.cafe;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class LogViewActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth =  FirebaseAuth.getInstance();
    FirebaseFirestore storage;
    RecyclerView recyclerView;
    FirebaseUser user = firebaseAuth.getCurrentUser();
    final String uid = user.getUid();
    String cafeCode;
    String cafeName;
    List<LogLog> logs = new ArrayList<>();
    LogRecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        logs = new ArrayList<>();
        recyclerAdapter = new LogRecyclerAdapter(getApplicationContext(), logs, R.layout.activity_log);
        recyclerView = (RecyclerView) findViewById(R.id.logrecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        Intent intent = getIntent();
        cafeName = intent.getStringExtra("cafeName");
        cafeCode = intent.getStringExtra("cafeCode");
        storage = FirebaseFirestore.getInstance();
        storage.collection(uid).document(uid).collection(cafeName).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e){
                if(e!=null){
                }
                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        Map<String, Object> map = doc.getDocument().getData();
                        SortedSet<String> keys = new TreeSet<>(map.keySet());
                        for (String key : keys) {
                            String value = map.get(key).toString();
                            if(!key.equals("useNum")) {
                                LogLog log = new LogLog(key, value);
                                logs.add(log);
                            }
                        }
                        recyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}