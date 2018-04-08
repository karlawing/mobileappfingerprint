package ph.edu.ust.iicscloudauthenticator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class BindActivity extends AppCompatActivity implements View.OnClickListener {
    Button bindBtn;
    Boolean isBinded = false;
    TextView imeiTv;
    String imei, temp_user;
    private AccessServiceAPI m_ServiceAccess;
    private ProgressDialog m_ProgressDialog;
    String email="";
    String recoveryCode="";
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
   // @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);

        bindBtn = (Button) findViewById(R.id.bindBtn);
        imeiTv = (TextView) findViewById(R.id.imeiTv);
        bindBtn.setOnClickListener(this);
        m_ServiceAccess = new AccessServiceAPI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {
                TelephonyManager mngr = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
                imei = mngr.getDeviceId();
            }
        }else{
            TelephonyManager mngr = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
            imei = mngr.getDeviceId();
        }
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDeviceImei();
        }
    }

    private void getDeviceImei() {

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = mTelephonyManager.getDeviceId();
        Log.d("msg", "DeviceImei " + deviceid);
    }*/

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Clicking the BACK button again will exit the app", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bindBtn:
                bind_account();
                break;
        }
    }
    /*
   System.out.println("email sent to: "+email+"Message is: "+recoveryCode);
        try {
            GMailSender sender = new GMailSender("iicsowncloud@gmail.com", " S!D@3f4g");
            sender.sendMail("IICS OWNCLOUD RECOVERY PASSWORD",
                    "RECOVERY CODE:"+recoveryCode,
                    "iicsowncloud@gmail.com",
                    email);
            System.out.print("email sent to:"+email+"Message is: "+recoveryCode);
        } catch (Exception e) {
            Log.e("SendMail", e.getMessage(), e);
        }
    */

    private void bind_account() {
        SharedPreferences sp = getSharedPreferences("temp_user", Context.MODE_PRIVATE);
        temp_user = sp.getString("username","N/A");
        new TaskBind().execute(temp_user, imei);
        new TaskEmail().execute(temp_user);
        new TaskRecovery().execute(temp_user);
        new TaskSend().execute();
       // new TaskSendMail().execute();
    }

    public class TaskSend extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Open progress dialog during login
            //m_ProgressDialog = ProgressDialog.show(BindActivity.this, "Please wait while we bind your account", "Processing...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                GMailSender sender = new GMailSender("iicsowncloud@gmail.com", "S!D@3f4g");
                sender.sendMail("IICS Mobile Authenticator Recovery Code",
                        "Greetings,\n" +
                                "\n" +
                                "Your account has been successfully binded to the IICS Cloud Mobile Authenticator. " +
                                "If ever your device is lost, please use this recovery code "+recoveryCode+
                                " and input this on the OwnCloud after logging in.\n\n Best Regards, \nOwnCloud IICS Authentication Team",
                        "iicsowncloud@gmail.com",
                        temp_user);
                System.out.print("email sent to:"+email+"Message is: "+recoveryCode);
                return Common.SERVICE_API_URL;
            } catch (Exception e) {
                Log.e("SendMail", e.getMessage(), e);
                return Common.SERVICE_API_URL;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //m_ProgressDialog.dismiss();

        }
    }

    public class TaskBind extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Open progress dialog during login
            //m_ProgressDialog = ProgressDialog.show(BindActivity.this, "Please wait while we bind your account", "Processing...", true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("action", "put_imei");
            param.put("username", params[0]);
            param.put("imei", params[1]);

            JSONObject jObjResult;
            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Common.SERVICE_API_URL2, param));

                return jObjResult.getInt("result");
            } catch (Exception e) {
                return Common.RESULT_ERROR;
            }
        }


        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            //m_ProgressDialog.dismiss();
            if (Common.RESULT_SUCCESS == result) {
                isBinded = true;
                Toast.makeText(getApplicationContext(), "Bind success", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Bind fail", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class TaskRecovery extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Open progress dialog during login
            //m_ProgressDialog = ProgressDialog.show(BindActivity.this, "Please wait while we bind your account", "Processing...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("action", "getrecovery");
            param.put("username", params[0]);

            JSONObject jObjResult;
            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Common.SERVICE_API_URL7, param));
                return recoveryCode = jObjResult.getString("recoveryCode");
            } catch (Exception e) {
                return Common.SERVICE_API_URL;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //m_ProgressDialog.dismiss();
            System.out.println("THIS IS THE recovery code:"+recoveryCode);
        }
    }

    public class TaskEmail extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Open progress dialog during login
            //m_ProgressDialog = ProgressDialog.show(BindActivity.this, "Please wait while we bind your account", "Processing...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("action", "getemail");
            param.put("username", params[0]);

            JSONObject jObjResult;
            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Common.SERVICE_API_URL6, param));
                return email = jObjResult.getString("email");
            } catch (Exception e) {
                return Common.SERVICE_API_URL;
            }
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }
    }
}
