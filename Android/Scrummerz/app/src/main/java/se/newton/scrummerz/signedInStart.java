package se.newton.scrummerz;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import se.newton.scrummerz.model.Student;

public class signedInStart extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference mRoot;

    TextView nameTextView, myGradesTextView;
    String uid;
    Student student = new Student();
    SharedPreferences studentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in_start);

        mRoot = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        uid = currentUser.getUid();
        studentInfo = PreferenceManager.getDefaultSharedPreferences(this);

        nameTextView = (TextView) findViewById(R.id.welcome);
        myGradesTextView = (TextView) findViewById(R.id.myGradesTextView);
        TextView coursesTextView = (TextView) findViewById(R.id.myCoursesTextView);
        TextView startScanner = (TextView) findViewById(R.id.scanner);


        startScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signedInStart.this, scanner.class);
                startActivity(intent);
            }
        });


        //Starts the course activity
        coursesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signedInStart.this, activity_courses.class);
                intent.putExtra("class", student.myClass);
                startActivity(intent);
            }
        });

        //Starts the grades activity
        myGradesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signedInStart.this, grades_activity.class);
                startActivity(intent);
            }
        });

        final ImageButton minus = (ImageButton) findViewById(R.id.negative);
        final ImageButton neutral = (ImageButton) findViewById(R.id.neutral);
        final ImageButton plus = (ImageButton) findViewById(R.id.positive);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        String classRef = studentInfo.getString("studentClass", "");
        final DatabaseReference dateData = mRoot.child("feelings").child(classRef).child(currentDate).child(uid);
        dateData.keepSynced(true);

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateData.setValue(-1);
                minus.setScaleX(1.15f);
                minus.setScaleY(1.15f);

                neutral.setScaleX(0.70f);
                neutral.setScaleY(0.70f);

                plus.setScaleX(0.70f);
                plus.setScaleY(0.70f);
            }
        });

        neutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateData.setValue(0);
                minus.setScaleX(0.70f);
                minus.setScaleY(0.70f);

                neutral.setScaleX(1.15f);
                neutral.setScaleY(1.15f);

                plus.setScaleX(0.70f);
                plus.setScaleY(0.70f);
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateData.setValue(1);
                minus.setScaleX(0.70f);
                minus.setScaleY(0.70f);

                neutral.setScaleX(0.70f);
                neutral.setScaleY(0.70f);

                plus.setScaleX(1.15f);
                plus.setScaleY(1.15f);
            }
        });

        setNotifications();

    }

    @Override
    protected void onStart() {
        super.onStart();
        String welcomeText = getString(R.string.welcome) + " " + studentInfo.getString("studentName", "");
        nameTextView.setText(welcomeText);
    }

    public void setNotifications () {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 59);

        Intent notificationIntent = new Intent(getApplicationContext(), Notification_reciever.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
