package corpex.shureader.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import corpex.shureader.R;
import corpex.shureader.utils.Constants;


public class LoginFragment extends Fragment {
    EditText etUsername;
    EditText etPassword;
    Button btnConect;
    ProgressBar pbLoading;
    TextView tvMensaje;

    private OnLoginFragmentListener mLoginFragmentListener;

    public LoginFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        configureButton();
    }

    private void initViews() {
        if(getView() != null) {
            etUsername = (EditText) getView().findViewById(R.id.etUsername);
            etPassword = (EditText) getView().findViewById(R.id.etPassword);
            btnConect = (Button) getView().findViewById(R.id.btnConect);
            pbLoading = (ProgressBar) getView().findViewById(R.id.pbLoading);
            tvMensaje = (TextView) getView().findViewById(R.id.tvMensaje);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    private void configureButton() {
        btnConect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEditTexts()) {
                    //activate loading ring
                    btnConect.setVisibility(View.GONE);
                    pbLoading.setVisibility(View.VISIBLE);
                    tvMensaje.setTextColor(Color.parseColor("#FF78B6A2"));
                    tvMensaje.setText(R.string.connecting);
                    tvMensaje.setVisibility(View.VISIBLE);

                    //if checking edittext was ok, do the request
                    final String userName = etUsername.getText().toString();
                    final String password = etPassword.getText().toString();

                    new AsyncTask<String,Void,Integer>(){
                        @Override
                        protected Integer doInBackground(String... params) {
                            return mLoginFragmentListener.loginRequest(userName, password);
                        }

                        @Override
                        protected void onPostExecute(Integer result) {

                            switch (result){
                                case Constants.RESULT_LOGGED:
                                    tvMensaje.setTextColor(Color.parseColor("#FF78B6A2"));
                                    tvMensaje.setText(R.string.correctData);
                                    mLoginFragmentListener.loadContentFragment();
                                    break;
                                case Constants.RESULT_NOT_LOGGED:
                                    pbLoading.setVisibility(View.GONE);
                                    tvMensaje.setTextColor(Color.parseColor("#FFEA3235"));
                                    tvMensaje.setText(R.string.incorrectUserPassword);
                                    btnConect.setVisibility(View.VISIBLE);
                                    break;
                                case Constants.RESULT_ERROR:
                                    pbLoading.setVisibility(View.GONE);
                                    tvMensaje.setTextColor(Color.parseColor("#FFEA3235"));
                                    tvMensaje.setText(R.string.connectionError);
                                    btnConect.setVisibility(View.VISIBLE);
                                    break;
                            }

                            super.onPostExecute(result);
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }else{
                    tvMensaje.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private boolean checkEditTexts() {
        boolean result = true;

        if(etUsername.getText().toString().isEmpty() && etPassword.getText().toString().isEmpty()) {
            tvMensaje.setTextColor(Color.parseColor("#FFEA3235"));
            tvMensaje.setText(R.string.emptyUserPassword);
            result = false;
        }else if(etUsername.getText().toString().length() < 4 && etPassword.getText().toString().length() < 4){
            tvMensaje.setTextColor(Color.parseColor("#FFEA3235"));
            tvMensaje.setText(R.string.invalidUserPassword);
            result = false;
        }else {
            if (etUsername.getText().toString().isEmpty()) {
                tvMensaje.setTextColor(Color.parseColor("#FFEA3235"));
                tvMensaje.setText(R.string.emptyUser);
                result = false;
            } else if (etUsername.getText().toString().length() < 4) {
                tvMensaje.setTextColor(Color.parseColor("#FFEA3235"));
                tvMensaje.setText(R.string.invalidUser);
                result = false;
            }

            if (etPassword.getText().toString().isEmpty()) {
                tvMensaje.setTextColor(Color.parseColor("#FFEA3235"));
                tvMensaje.setText(R.string.emptyPassword);
                result = false;
            } else if (etPassword.getText().toString().length() < 4) {
                tvMensaje.setTextColor(Color.parseColor("#FFEA3235"));
                tvMensaje.setText(R.string.invalidPassword);
                result = false;
            }
        }

        return result;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentListener) {
            mLoginFragmentListener = (OnLoginFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()+ " must implement OnLoginFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mLoginFragmentListener = null;
    }


    public interface OnLoginFragmentListener {
        int loginRequest(String username, String password);
        void loadContentFragment();
    }
}
