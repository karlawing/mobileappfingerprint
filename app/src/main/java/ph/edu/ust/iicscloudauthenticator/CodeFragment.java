package ph.edu.ust.iicscloudauthenticator;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class CodeFragment extends Fragment{
    TextView tvCode, codeTv;
    EditText etCode;
    int n = 0;
    Button btnG;
    String s = "",user="";
    String code="";
    private AccessServiceAPI m_ServiceAccess;

    public CodeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_code, container, false);

        codeTv =(TextView) rootView.findViewById(R.id.codeTv);
        // Inflate the layout for this fragment
        m_ServiceAccess = new AccessServiceAPI();
        SharedPreferences sp = getActivity().getSharedPreferences("temp_user", Context.MODE_PRIVATE);
        user = sp.getString("username","N/A");
        new TaskCode().execute(user);
        return rootView;
    }



    public class TaskCode extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            //Create data to pass in param
            Map<String, String> param = new HashMap<>();
            param.put("action", "get_code");
            param.put("username", params[0]);
            JSONObject jObjResult;
            try {
                jObjResult = m_ServiceAccess.convertJSONString2Obj(m_ServiceAccess.getJSONStringWithParam_POST(Common.SERVICE_API_URL5, param));
                return code = jObjResult.getString("randomCode");
            } catch (Exception e) {
                e.printStackTrace();
                return Common.SERVICE_API_URL;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            codeTv.setText(code);
        }
    }

}
