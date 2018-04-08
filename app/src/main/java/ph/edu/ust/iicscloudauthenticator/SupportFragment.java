package ph.edu.ust.iicscloudauthenticator;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends Fragment implements View.OnClickListener {
    String email="";
    String recoveryCode="";
    String temp_user;
    private AccessServiceAPI m_ServiceAccess;
    private ProgressDialog m_ProgressDialog;
    Button recoveryBtn;
    public SupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_support, container, false);
        recoveryBtn=(Button)v.findViewById(R.id.recoveryBtn);
        recoveryBtn.setOnClickListener(this);
        m_ServiceAccess = new AccessServiceAPI();
        return v;

    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recoveryBtn:
                sendRecovery();
                break;
        }
    }

    private void sendRecovery() {
        SharedPreferences sp = this.getActivity().getSharedPreferences("temp_user", Context.MODE_PRIVATE);
        temp_user = sp.getString("username","N/A");
        new TaskEmail().execute(temp_user);
        new TaskRecovery().execute(temp_user);
        new TaskSend().execute();
    }

    public class TaskSend extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Open progress dialog during login
            // m_ProgressDialog = ProgressDialog.show(getActivity(), "Sending email", "Processing...", true);
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
            Toast.makeText(getActivity(), "Email Sent", Toast.LENGTH_LONG).show();
        }
    }

    public class TaskRecovery extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
            System.out.println("THIS IS THE recovery code:"+recoveryCode);
        }
    }

    public class TaskEmail extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
