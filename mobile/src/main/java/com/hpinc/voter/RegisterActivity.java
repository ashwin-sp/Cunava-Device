package com.hpinc.voter;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.view.Menu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterActivity extends Activity {

    String gender= "";
    String crime= "";
    String regex = "[0-9]";
    EditText first_name;
    EditText middle_name;
    EditText last_name;
    EditText age;
    EditText address;
    EditText constitute;
    EditText occupation;
    EditText password;
    EditText confirm;
    EditText phone;
    TextView t2,t4,t6,t8,t12,t14,t17,t19;
    int f1=0,f2=0,f3=0,f4=0,f5=0,f6=0,f7=0,f8=0;
    DatabaseHelper db;
    SQLiteDatabase sb;
   // static int regime= 1000;
    int code = -1;
    static int counterpa;
    private static String SENT = "SMS_SENT";
    private static String DELIVERED = "SMS_DELIVERED";
    private static int MAX_SMS_MESSAGE_LENGTH = 160;
   // static int[] count= new int[9999];
    public static final String CHANNEL_ONE_ID = "General";
    public static final String CHANNEL_ONE_NAME = "General";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
        first_name = (EditText) findViewById(R.id.editText);
        middle_name = (EditText) findViewById(R.id.editText2);
        last_name = (EditText) findViewById(R.id.editText3);
        age = (EditText) findViewById(R.id.editText4);
        address = (EditText) findViewById(R.id.editText5);
        constitute= (EditText) findViewById(R.id.editText6);
        occupation= (EditText) findViewById(R.id.editText7);
        password= (EditText) findViewById(R.id.editText8);
        confirm= (EditText) findViewById(R.id.editText9);
        phone= (EditText) findViewById(R.id.editText10);
        t2= (TextView) findViewById(R.id.textView2);
        t4= (TextView) findViewById(R.id.textView4);
        t6= (TextView) findViewById(R.id.textView6);
        t8= (TextView) findViewById(R.id.textView8);
        t12= (TextView) findViewById(R.id.textView12);
        t14= (TextView) findViewById(R.id.textView14);
        t17= (TextView) findViewById(R.id.textView17);
        t19= (TextView) findViewById(R.id.textView19);
        t2.setVisibility(View.GONE);
        t4.setVisibility(View.GONE);
        t6.setVisibility(View.GONE);
        t8.setVisibility(View.GONE);
        t12.setVisibility(View.GONE);
        t14.setVisibility(View.GONE);
        t17.setVisibility(View.GONE);
        t19.setVisibility(View.GONE);
       db = new DatabaseHelper(RegisterActivity.this);
       sb = db.getReadableDatabase();
      // Log.d("Reg Check ", "9789859912".hashCode()+"");
	}
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton:
                if (checked) {
                    gender="male";
                    break;
                }
            case R.id.radioButton2:
                if (checked) {
                    gender="female";
                    break;
                }

        }
    }
    public void onRadioButtonClicked2(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButton3:
                if (checked) {
                    crime="true";
                    break;
                }
            case R.id.radioButton4:
                if (checked) {
                    crime="false";
                    break;
                }

        }
    }
	public void submit(View v){




        check();
    }



    /*
     * Validation
     */
    public boolean validate() {

        if (first_name.getText().toString().equalsIgnoreCase("")) {
            first_name.requestFocus();
            return false;
        }
        else if (middle_name.getText().toString().equalsIgnoreCase("")) {
            middle_name.requestFocus();
            return false; }
        else if (last_name.getText().toString().equalsIgnoreCase("")) {
            last_name.requestFocus();
            return false; }
        else if (age.getText().toString().equalsIgnoreCase("")) {
            age.requestFocus();
            return false; }
        else if (gender.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please select a gender", Toast.LENGTH_SHORT).show();
            return false; }
        else if (crime.equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please select whether you did a crime or not", Toast.LENGTH_SHORT).show();
            return false; }
        else if (address.getText().toString().equalsIgnoreCase("")) {
            address.requestFocus();
            return false; }
        else if (constitute.getText().toString().equalsIgnoreCase("")) {
            constitute.requestFocus();
            return false;
        }
        else if (occupation.getText().toString().equalsIgnoreCase("")) {
            constitute.requestFocus();
            return false;
        }else if (password.getText().toString().equalsIgnoreCase("")) {
            password.requestFocus();
            return false;
        }
        else if (confirm.getText().toString().equalsIgnoreCase("")) {
            confirm.requestFocus();
            return false;
        }else if (phone.getText().toString().equalsIgnoreCase("")) {
            phone.requestFocus();
            return false;
        }

        return true;
    }
    public void check() {

        if (validate()) {
            // we assume special characters as @, ., *, & etc., as they are frequently //
            if((first_name.getText().toString().indexOf('@')>=0)||(first_name.getText().toString().indexOf('.')>=0)||(first_name.getText().toString().indexOf('*')>=0)||(first_name.getText().toString().indexOf('&')>=0)||(first_name.getText().toString().indexOf('(')>=0)||(first_name.getText().toString().indexOf(')')>=0)||(first_name.getText().toString().indexOf('!')>=0)||(first_name.getText().toString().indexOf('#')>=0)||(first_name.getText().toString().indexOf('%')>=0))
            {
                t2.setVisibility(View.VISIBLE);
                f1=1;
            }
            else
            {
                t2.setVisibility(View.GONE);
                f1=0;
            }
            if((middle_name.getText().toString().indexOf('@')>=0)||(middle_name.getText().toString().indexOf('.')>=0)||(middle_name.getText().toString().indexOf('*')>=0)||(middle_name.getText().toString().indexOf('&')>=0)||(middle_name.getText().toString().indexOf('(')>=0)||(middle_name.getText().toString().indexOf(')')>=0)||(middle_name.getText().toString().indexOf('!')>=0)||(middle_name.getText().toString().indexOf('#')>=0)||(middle_name.getText().toString().indexOf('%')>=0))
            {
                t4.setVisibility(View.VISIBLE);
                f2=1;
            }
            else
            {
                t4.setVisibility(View.GONE);
                f2=0;
            }
            if((last_name.getText().toString().indexOf('@')>=0)||(last_name.getText().toString().indexOf('.')>=0)||(last_name.getText().toString().indexOf('*')>=0)||(last_name.getText().toString().indexOf('&')>=0)||(last_name.getText().toString().indexOf('(')>=0)||(last_name.getText().toString().indexOf(')')>=0)||(last_name.getText().toString().indexOf('!')>=0)||(last_name.getText().toString().indexOf('#')>=0)||(last_name.getText().toString().indexOf('%')>=0))
            {
                t6.setVisibility(View.VISIBLE);
                f3=1;
            }
            else
            {
                t6.setVisibility(View.GONE);
                f3=0;
            }
            if(Integer.valueOf(age.getText().toString())<18)
            {
                t8.setVisibility(View.VISIBLE);
                f4=1;
            }
            else
            {
                t8.setVisibility(View.GONE);
                f4=0;
            }
            if((constitute.getText().toString().indexOf('@')>=0)||(constitute.getText().toString().indexOf('.')>=0)||(constitute.getText().toString().indexOf('*')>=0)||(constitute.getText().toString().indexOf('&')>=0)||(constitute.getText().toString().indexOf('(')>=0)||(constitute.getText().toString().indexOf(')')>=0)||(constitute.getText().toString().indexOf('!')>=0)||(constitute.getText().toString().indexOf('#')>=0)||(constitute.getText().toString().indexOf('%')>=0))
            {
                t12.setVisibility(View.VISIBLE);
                f5=1;
            }
            else
            {
                t12.setVisibility(View.GONE);
                f5=0;
            }
            if((occupation.getText().toString().indexOf('@')>=0)||(occupation.getText().toString().indexOf('.')>=0)||(occupation.getText().toString().indexOf('*')>=0)||(occupation.getText().toString().indexOf('&')>=0)||(occupation.getText().toString().indexOf('(')>=0)||(occupation.getText().toString().indexOf(')')>=0)||(occupation.getText().toString().indexOf('!')>=0)||(occupation.getText().toString().indexOf('#')>=0)||(occupation.getText().toString().indexOf('%')>=0))
            {
                t14.setVisibility(View.VISIBLE);
                f6=1;
            }
            else
            {
                t14.setVisibility(View.GONE);
                f6=0;
            }
            if((password.getText().toString().matches("[a-zA-Z]+"))||(password.getText().toString().matches(regex)))
            {
                t17.setVisibility(View.VISIBLE);
                f7=1;
            }
            else
            {
                t17.setVisibility(View.GONE);
                f7=0;
            }
            if((confirm.getText().toString().matches("[a-zA-Z]+"))||(confirm.getText().toString().matches(regex)))
            {
                t19.setVisibility(View.VISIBLE);
                f8=1;
            }
            else
            {
                t19.setVisibility(View.GONE);
                f8=0;
            }
            if((f1==1)||(f2==1)||(f3==1)||(f4==1)||(f5==1)||(f6==1)||(f7==1)||(f8==1))
            {
                Toast.makeText(getApplicationContext(),"Correct the errors in red",Toast.LENGTH_SHORT).show();
            }
            else if(!password.getText().toString().equals(confirm.getText().toString()))
            {
                Toast.makeText(getApplicationContext(),"Passwords not matching",Toast.LENGTH_SHORT).show();
            }
            else if(crime.equals("true"))
            {
                Toast.makeText(getApplicationContext(),"You are not eligible to vote as you have a previous crime",Toast.LENGTH_SHORT).show();
            }
            else {


                counterpa = db.getyourdata3(phone.getText().toString());
                if (counterpa == 1) {/*
			 * Insert the Values to the Database
			 */
                    //db.execSQL("create table if not exists LOGIN (Name TEXT,Department TEXT,Year NUMBER,Password TEXT,Confirmpassword TEXT)");
                    //regime++;
                    code = phone.hashCode();
                    if(code < 0)
                    {
                        code *= -1;
                    }
                    sb.execSQL("insert into LOGGER "
                            + "values('"
                            + first_name.getText().toString()
                            + "',"
                            + "'"
                            + middle_name.getText().toString()
                            + "',"
                            + "'"
                            + last_name.getText().toString()
                            + "',"
                            + "'"
                            + age.getText().toString()
                            + "',"
                            + "'"
                            + address.getText().toString()
                            + "',"
                            + "'"
                            + constitute.getText().toString()
                            + "',"
                            + "'"
                            + occupation.getText().toString()
                            + "',"
                            + "'"
                            + password.getText().toString()
                            + "',"
                            + "'"
                            + confirm.getText().toString()
                            + "',"
                            + "'"
                            + phone.getText().toString()
                            + "',"
                            + "'"
                            + code
                            + "',"
                            + "'"
                            + "false"
                            + "',"
                            + "'"
                            + "NOTA"
                            + "')");
                    // Call the Methods
                //    count[regime] = 0;

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c.getTime());


                    //sendSMS("9841501621", "Registered for voting on " + formattedDate + " by " + first_name.getText().toString() + ". The confirmation code is " + code, RegisterActivity.this);
                    Toast.makeText(getApplicationContext(),
                            "Registered Successfully...", Toast.LENGTH_LONG)
                            .show();
                    navigate();
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Duplicate entry...", Toast.LENGTH_LONG)
                            .show();
                }
            }

        }
        else {
            // USER Reference Display Message
            Toast.makeText(getApplicationContext(),
                    "Please Fill All the Mandatory Fields", Toast.LENGTH_LONG)
                    .show();
        }

    }

public void navigate() {

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, mNotifyMgr.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        if (mNotifyMgr != null) {
            mNotifyMgr.createNotificationChannel(notificationChannel);
        }
        Notification.Builder noti = new Notification.Builder(this)
                .setContentTitle("CUNAVA VOTER")
                .setContentText("Your Confirmation code is: "+ code).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .setChannelId(CHANNEL_ONE_ID);

        noti.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        mNotifyMgr.notify(0, noti.build());
    }
    else
    {
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder noti = new Notification.Builder(this)
                .setContentTitle("CUNAVA VOTER")
                .setContentText("Your Confirmation code is: "+ code).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent);
        noti.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        mNotifyMgr.notify(0, noti.build());
    }
    Intent i =new Intent(RegisterActivity.this,LoginActivity.class);
    startActivity(i);
    finish();

    db.close();
    sb.close();
}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}
/*    public void sendSMS(String phoneNumber, String message, Context mContext) {


        if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(RegisterActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this,
                    Manifest.permission.SEND_SMS)) {

            } else {
                Toast.makeText(RegisterActivity.this, "Please grant permission for sending sms in the settings ", Toast.LENGTH_LONG).show();
            }
        }
        else {
            PendingIntent piSent = PendingIntent.getBroadcast(mContext, 0, new Intent(SENT), 0);
            PendingIntent piDelivered = PendingIntent.getBroadcast(mContext, 0, new Intent(DELIVERED), 0);
            SmsManager smsManager = SmsManager.getDefault();
            int length = message.length();
            if (length > MAX_SMS_MESSAGE_LENGTH) {
                ArrayList<String> messagelist = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(phoneNumber, null, messagelist, null, null);
            } else {
                smsManager.sendTextMessage(phoneNumber, null, message, piSent, piDelivered);

            }
        }
    }*/

}
