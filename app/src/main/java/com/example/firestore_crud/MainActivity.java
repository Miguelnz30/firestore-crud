package com.example.firestore_crud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.widget.DatePicker;



public class MainActivity extends AppCompatActivity {
    private EditText mTitle , mDesc;
    private Button mSaveBtn, mShowBtn;
    private FirebaseFirestore db;
    private String uTitle, uDesc , uId;

    private Button mDatebtn , mTimebtn;
    private TextView mDateText , mTimeText;
    private String uDate, uTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = findViewById(R.id.edit_title);
        mDesc = findViewById(R.id.edit_desc);
        mSaveBtn = findViewById(R.id.save_btn);
        mShowBtn = findViewById(R.id.showall_btn);


        mDatebtn = findViewById(R.id.datebtn);
        mTimebtn = findViewById(R.id.timebtn);
        mDateText = findViewById(R.id.datetext);
        mTimeText = findViewById(R.id.textView2);

        db= FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            mSaveBtn.setText("Update");
            uTitle = bundle.getString("uTitle");
            uId = bundle.getString("uDesc");
            uDesc = bundle.getString("uDesc");
            uDate = bundle.getString("uDate");
            uTime = bundle.getString("uTime");
        } else {
            mSaveBtn.setText("Save");
        }

        if (bundle != null){
            mSaveBtn.setText("Update");
            uTitle = bundle.getString("uTitle");
            uId = bundle.getString("uId");
            uDesc = bundle.getString("uDesc");

            mTitle.setText(uTitle);
            mDesc.setText(uDesc);

            uDate = bundle.getString("uDate");
            uTime = bundle.getString("uTime");
            mDateText.setText(uDate);
            mTimeText.setText(uDesc);

          /*  mDatebtn.setText((CharSequence) mDateText);
            mTimebtn.setText((CharSequence) mTimeText);
            mDateText.setText((CharSequence) mDateText);
            mTimeText.setText((CharSequence) mTimeText);
           */
        }else{
            mSaveBtn.setText("Save");
        }

        mShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShowActivity.class));
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = mTitle.getText().toString();
                String desc = mDesc.getText().toString();
                String date = mDateText.getText().toString();
                String time = mTimeText.getText().toString();


                Bundle bundle1 = getIntent().getExtras();
                if(bundle1 != null){
                    String id = uId;
                    updateToFireStore(id, title, desc, date, time);
                } else {
                    String id = UUID.randomUUID().toString();
                    saveToFirestore(id, title, desc, date, time);
                }
            }
        });



        mDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });
        mTimebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();
            }
        });
    }
    private void updateToFireStore(String id, String title, String desc, String date, String time ) {
        db.collection("Documents").document(id).update(
                "title", title,
                "desc", desc,
                "date", date,
                "time", time
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Data update", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveToFirestore(String id, String title, String desc, String date, String time){
        if (!title.isEmpty() && !desc.isEmpty()){
            HashMap<String , Object> map = new HashMap<>();
            map.put("id" , id);
            map.put("title" , title);
            map.put("desc" , desc);
            map.put("date" , date);
            map.put("time" , time);

            db.collection("Documents").document(id).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Data Saved !!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Failed !!", Toast.LENGTH_SHORT).show();
                }
            });

        }else
            Toast.makeText(this, "Empty Fields not Allowed", Toast.LENGTH_SHORT).show();
    }

    private void openTimePicker() {

        Calendar calendar = Calendar.getInstance();

        int HOUR = calendar.get(Calendar.HOUR);
        int MINUTE = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mTimeText.setText(hourOfDay + ":" + minute);
            }
        } ,  HOUR , MINUTE , false);
        timePickerDialog.show();
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH);
        int DATE = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                mDateText.setText(dayOfMonth + "/" + month + "/" + year);
            }
        } , YEAR , MONTH , DATE);
        datePickerDialog.show();
    }
}