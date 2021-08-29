package com.yooncafe.cafe;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class QRActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private IntentIntegrator qrScan;
    private AlertDialog dialog;
    public static Context context_main;
    public String cafeName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        context_main = this;
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.setPrompt("Sample Text!");
        qrScan.initiateScan();
    }

    //시간 get
    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String getTime = simpleDate.format(mDate);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Intent intent = new Intent(QRActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                String result_text = result.getContents();
                if(result_text.contains("yooncafeyooncafe3x3x3x3x3")) {//yooncafeyooncafe3x3x3x3x3-456456-cafename(코드 형식)
                    Toast.makeText(this, "스탬프가 적립되었습니다.", Toast.LENGTH_LONG).show();
                    cafeName = result_text.substring(33);
                    final String cafeCode = result_text.substring(26,32);
                    final String userLog = getTime;

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    final String uid = user.getUid();
                    final FirebaseFirestore storage = FirebaseFirestore.getInstance();

                    final HashMap<Object, Object> nestedmap = new HashMap<>();
                    nestedmap.put("useNum",1);
                    nestedmap.put("Log 1",userLog);

                    final HashMap<Object, Object> nestedmap2 = new HashMap<>();
                    nestedmap2.put(cafeCode,1);
                    nestedmap2.put("cafeCode",cafeCode);

                    final DocumentReference userLogPath = storage.collection(uid).document(uid)
                            .collection(cafeName).document(cafeCode);
                    final boolean[] defaultCol = {false};
                    storage.collection(uid).document(uid).collection(cafeName)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if(!task.getResult().isEmpty()) {
                                            defaultCol[0] = true;
                                        } else {
                                            defaultCol[0] = false;
                                            userLogPath.set(nestedmap);
                                            storage.collection(uid).document(cafeCode).set(nestedmap2,SetOptions.merge());
                                            storage.collection(uid).document(uid).update("useCafeNum",FieldValue.increment(1));
                                            Intent intent = new Intent(QRActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Log.d("d", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                    final int[] SaveduserNum = new int[1];
                    userLogPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    if(defaultCol[0]){
                                        String temp = document.get("useNum").toString();
                                        if (!TextUtils.isEmpty(temp) && TextUtils.isDigitsOnly(temp)) {
                                            SaveduserNum[0] = Integer.parseInt(temp);
                                            SaveduserNum[0] = SaveduserNum[0]+1;
                                        }
                                        final HashMap<Object, Object> map2 = new HashMap<>();
                                        map2.put("Log "+ SaveduserNum[0],userLog);
                                        userLogPath.update("useNum", FieldValue.increment(1));
                                        userLogPath.set(map2, SetOptions.merge());
                                        storage.collection(uid).document(cafeCode).update(cafeCode,FieldValue.increment(1));
                                        Intent intent = new Intent(QRActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(this, "유효하지 않은 QR 입니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(QRActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}