package ph.edu.ust.iicscloudauthenticator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    private boolean isBinded = true;
    private EditText txtUsername;
    private EditText txtPassword;
    private AccessServiceAPI m_ServiceAccess;
    private ProgressDialog m_ProgressDialog;
    String imei;
    Button buttonLogin;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        txtUsername = (EditText) findViewById(R.id.txt_username_login);
        txtPassword = (EditText) findViewById(R.id.txt_pwd_login);
        m_ServiceAccess = new AccessServiceAPI();
        buttonLogin.setOnClickListener(this);

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

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {
            txtUsername.setText(data.getStringExtra("username"));
            txtPassword.setText(data.getStringExtra("password"));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonLogin:
                btnLogin_Click();
                break;
        }
    }

    public void btnLogin_Click() {
        //Validate input
        if ("".equals(txtUsername.getText().toString())) {
            txtUsername.setError("Username is required!");
            return;
        }
        if ("".equals(txtPassword.getText().toString())) {
            txtPassword.setError("Password is required!");
            return;
        }
        //Call async task to login
        new TaskLogin().execute(txtUsername.getText().toString(), txtPassword.getText().toString(), imei);
    }

    public class TaskLogin extends AsyncTask<String, Void, Integer> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Open progress dialog during login
            m_ProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait...", "Processing...", true);
        }


        @Override
        protected Integer doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("action", "login");
            param.put("username", params[0]);
            param.put("password", params[1]);
            param.put("imei",params[2]);

            JSONObject jObjResult;
            try {

                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Common.SERVICE_API_URL, param));
                return jObjResult.getInt("result");
            } catch (Exception e) {
                return Common.RESULT_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            m_ProgressDialog.dismiss();
            if(Common.RESULT_SUCCESS == result) {
                SharedPreferences sp = getSharedPreferences("temp_user", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("username", txtUsername.getText().toString());
                edit.commit();

                Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                i.putExtra("username", txtUsername.getText().toString());
                startActivity(i);
                finish();
            }else if(Common.RESULT_SUCCESS_NOT_BINDED == result){
                SharedPreferences sp = getSharedPreferences("temp_user", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("username", txtUsername.getText().toString());
                edit.commit();

                Toast.makeText(getApplicationContext(), "Account not binded, please bind your account", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), BindActivity.class);
                i.putExtra("username", txtUsername.getText().toString());
                startActivity(i);
                finish();
            }else if(Common.RESULT_BINDED == result){
                SharedPreferences sp = getSharedPreferences("temp_user", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("username", txtUsername.getText().toString());
                edit.commit();


                Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                i.putExtra("username", txtUsername.getText().toString());
                startActivity(i);
                finish();
            }

            else if(Common.RESULT_BINDED_PHONE == result){
                Toast.makeText(getApplicationContext(), "Phone is already binded to another account.", Toast.LENGTH_SHORT).show();
            }
            else if(Common.RESULT_BINDED_ACCOUNT == result){
                Toast.makeText(getApplicationContext(), "Account is already binded to another phone.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_LONG).show();
            }
        }


    }
}