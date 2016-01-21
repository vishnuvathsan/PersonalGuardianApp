package com.aslan.personalguardianapp.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aslan.personalguardianapp.R;
import com.aslan.personalguardianapp.util.UserConfiguration;
import com.aslan.personalguardianapp.util.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    boolean isSaved = false;
    private OnFragmentInteractionListener mListener;
    private UserConfiguration conf;
    private EditText etxtName, etxtPhone, etxtPassword;
    private Button btnSave;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conf = new UserConfiguration();
        UserConfiguration data = Utility.getUserConf(getContext());
        if (data != null) {
            conf = data;

            if (conf.getPassword() != null) {
                showPasswordDialog();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etxtName = (EditText) view.findViewById(R.id.etxtName);
        etxtPhone = (EditText) view.findViewById(R.id.etxtPhone);
        etxtPassword = (EditText) view.findViewById(R.id.etxtPassword);

        etxtName.setSelectAllOnFocus(true);
        etxtPhone.setSelectAllOnFocus(true);
        etxtPassword.setSelectAllOnFocus(true);

        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserInputs();
                // isSaved = deleteFile("conf");
                if (isSaved) {
                    Toast.makeText(getContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
                    Utility.saveUserConf(getContext(), conf);
                } else {
                    Toast.makeText(getContext(), "Unable to save.\nTry again later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showPasswordDialog() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View loginView = inflater.inflate(
                R.layout.alert_activity_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(loginView)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        EditText etxtPwd = (EditText) loginView
                                .findViewById(R.id.etxtAlertPassword);
                        String password = etxtPwd.getText().toString();
                        Log.i("Pwd", password);
                        if (password.equalsIgnoreCase(conf.getPassword())) {
                            updateFields();
                            Log.i("Pwd22", conf.getPassword());
                        } else {
                            showPasswordDialog();
                            Toast.makeText(getContext(), "Password Incorrect",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // closes the activity
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getUserInputs() {
        String name = etxtName.getText().toString();
        String phone = etxtPhone.getText().toString();
        String passwrd = etxtPassword.getText().toString();
        // get data from inputs and store it in UserConfiguration object

        Log.i("name", name);
        Log.i("phone", phone);
        Log.i("passwrd", passwrd);

        if (name.length() > 2) {
            conf.setGuardianName(name);
        } else {
            Toast.makeText(getContext(), "Name should be atleast 3 charactors", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.length() >= 9 && phone.length() <= 14) {
            conf.setGuardianPhone(phone);
        } else {
            Toast.makeText(getContext(), "Phone number invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passwrd.length() >= 3) {
            conf.setPassword(passwrd);
        } else {
            Toast.makeText(getContext(), "Password should be atleast 3 charactors", Toast.LENGTH_SHORT).show();
            return;
        }
        // save
        isSaved = Utility.saveUserConf(getContext(), conf);
    }

    private void updateFields() {
        // used to update the data fields after reading configuration setting
        // from file
        if (conf != null) {
            etxtName.setText(conf.getGuardianName());
            etxtPhone.setText(conf.getGuardianPhone());
            etxtPassword.setText(conf.getPassword());
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
