package ph.edu.ust.iicscloudauthenticator;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignOutFragment extends Fragment {

    TextView tvKey;
    SharedPreferences sp;


    public SignOutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_out, container, false);

        //SharedPrefManager.getInstance(getActivity()).logout();

        sp = this.getActivity().getSharedPreferences("myAcc", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.clear();
        edit.commit();

        Intent in = new Intent(getActivity(), MainActivity.class);
        startActivity(in);

        getActivity().finish();

        return v;

    }

}
