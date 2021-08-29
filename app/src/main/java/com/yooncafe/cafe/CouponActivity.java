package com.yooncafe.cafe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wajahatkarim3.easyflipview.EasyFlipView;


public class CouponActivity extends AppCompatActivity {
    private String useNum;
    private Integer useNum2;
    private Integer cafeStampNum;
    private String title;
    private String cafeCode;
    private ImageView image_front;
    private ImageView image_back;
    final long ONE_MEGABYTE = 1024 * 1024;
    String backimagePath;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon); //전환된 xml

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://yooncafe-8c8b5.appspot.com");
        final StorageReference storageRef = storage.getReference();

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        cafeCode = intent.getStringExtra("cafeCode");

        TextView textView = findViewById(R.id.coupon_title);
        image_front = findViewById(R.id.coupon_image_front);
        image_back = findViewById(R.id.coupon_image_back);
        Button couponLogButton = findViewById(R.id.coupon_log_view);

        final StorageReference[] pathReferenceback = new StorageReference[1];
        StorageReference pathReferencefront;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();
        final FirebaseFirestore firestorage = FirebaseFirestore.getInstance();
        final DocumentReference userLogPath = firestorage.collection(uid).document(cafeCode);

        userLogPath.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        firestorage.collection("CafeInfo").document(cafeCode)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Object cafeStampNumtemp = task.getResult().get("cafeStampNum");
                                    assert cafeStampNumtemp != null;
                                    cafeStampNum = Integer.parseInt(cafeStampNumtemp.toString());
                                    Log.d("123456789-1","                         "+cafeStampNum);
                                    Object cafeCodetemp = document.get(cafeCode);
                                    useNum = cafeCodetemp.toString();
                                    Log.d("12345678-2", cafeStampNum+"           "+useNum+"                         " );
                                    if (!TextUtils.isEmpty(useNum) && TextUtils.isDigitsOnly(useNum)) {
                                        useNum2 = (Integer.parseInt(useNum))%cafeStampNum;
                                        if(useNum2 == 0 ){
                                            Toast.makeText(CouponActivity.this, "스탬프를 모두 적립하셨습니다. 새 쿠폰으로 교환 됩니다.", Toast.LENGTH_LONG).show();
                                        }
                                        Log.d("12345678-2", cafeStampNum+"           "+Integer.parseInt(useNum)+"                         " + useNum2);
                                        backimagePath = "/" + cafeCode + "_" + useNum2 + "back.jpg";
                                    }
                                    pathReferenceback[0] = storageRef.child(cafeCode + backimagePath);
                                    pathReferenceback[0].getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            image_back.setImageBitmap(bitmap);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });

        textView.setText(title);
        textView.append("\n");
        String frontimagePath = "/"+cafeCode+"_front.jpg";

        pathReferencefront = storageRef.child(cafeCode+frontimagePath);
        pathReferencefront.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                image_front.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        final EasyFlipView myEasyFlipView;
        myEasyFlipView = findViewById(R.id.coupon_image_view);
        image_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    myEasyFlipView.flipTheView();
            }
        });

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myEasyFlipView.flipTheView();
            }
        });

        couponLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CouponActivity.this, LogViewActivity.class);
                intent.putExtra("cafeName",title);
                intent.putExtra("cafeCode",cafeCode);
                startActivity(intent);
                finish();
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