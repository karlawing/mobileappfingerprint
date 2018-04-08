package ph.edu.ust.iicscloudauthenticator;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class ProfileFragment extends Fragment implements View.OnClickListener {
    TextView tvUser, tvBday, tvAdd, tvEmail;
    SharedPreferences sp;
    Button btnEleap, btnLogout;
    private AccessServiceAPI m_ServiceAccess;
    private ProgressDialog m_ProgressDialog;

    private String userHolder = "", address,bday;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        btnEleap = (Button) v.findViewById(R.id.btnEleap);
        btnLogout = (Button) v.findViewById(R.id.btnLogout);
        tvUser = (TextView) v.findViewById(R.id.tvUser);
        tvBday = (TextView) v.findViewById(R.id.tvBday);
        tvAdd = (TextView) v.findViewById(R.id.tvAdd);
        tvEmail = (TextView) v.findViewById(R.id.tvEmail);

        btnEleap.setOnClickListener(this);
        btnLogout.setOnClickListener(this);


        m_ServiceAccess = new AccessServiceAPI();
        Intent in = getActivity().getIntent();
        Bundle b = in.getExtras();
        sp = getActivity().getSharedPreferences("temp_user", Context.MODE_PRIVATE);
        userHolder = sp.getString("username","N/A");
        tvUser.setText(userHolder);
        new TaskBday().execute(userHolder);
        new TaskAddr().execute(userHolder);
        return v;
    }

    public class TaskBday extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            }

        @Override
        protected String doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("action", "getprofile");
            param.put("username", params[0]);

            JSONObject jObjResult;
            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Common.SERVICE_API_URL3, param));
                return bday = jObjResult.getString("bday");
            } catch (Exception e) {
                e.printStackTrace();
                return Common.SERVICE_API_URL;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tvBday.setText(bday);
        }
    }

    public class TaskAddr extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("action", "getprofile");
            param.put("username", params[0]);

            JSONObject jObjResult;
            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Common.SERVICE_API_URL4, param));
                return address = jObjResult.getString("address");
            } catch (Exception e) {
                e.printStackTrace();
                return Common.SERVICE_API_URL;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            tvAdd.setText(address);
        }
    }



    public void logout(){
        sp = this.getActivity().getSharedPreferences("myAcc", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.commit();

        Intent in = new Intent(getActivity(), MainActivity.class);
        startActivity(in);

        getActivity().finish();
    }

    public void ust(){
        String url = "https://myuste.ust.edu.ph/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void eleap(){
        String url = "https://eleap.ust.edu.ph/";
        Intent i2 = new Intent(Intent.ACTION_VIEW);
        i2.setData(Uri.parse(url));
        startActivity(i2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case (R.id.btnLogout):
                logout();
                break;

            case (R.id.btnEleap):
                eleap();
                break;
        }
    }
}
