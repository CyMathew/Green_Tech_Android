package app.greentech.Fragments_Login;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;

import app.greentech.Utils.Constants;
import app.greentech.Models.ServerRequest;
import app.greentech.Models.ServerResponse;
import app.greentech.Models.User;
import app.greentech.R;
import app.greentech.Utils.RequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment_SignIn extends Fragment implements View.OnClickListener {

    private AppCompatButton btn_login;
    private EditText et_email, et_password;
    private TextView tv_register, tv_reset_password;
    private ProgressBar progress;
    private SharedPreferences preferences;

    private LoginButton fbLoginButton;
    private SignInButton gLoginButton;
    private CallbackManager callbackManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        callbackManager = CallbackManager.Factory.create();

        fbLoginButton = (LoginButton) view.findViewById(R.id.btn_fb_login);
        fbLoginButton.setReadPermissions("user_friends");

        // If using in a fragment
        fbLoginButton.setFragment(this);

        // Other app specific specialization
        gLoginButton = (SignInButton) view.findViewById(R.id.btn_google_login);
        gLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pickUserAccount();
            }
        });
        initViews(view);

        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                saveInfo();
                getActivity().finish();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        return view;
    }

    private void saveInfo()
    {
        Profile userProfile = Profile.getCurrentProfile();
        SharedPreferences.Editor prefEdit = preferences.edit();
        prefEdit.putString("Username", userProfile.getName());
        Log.i("Info", userProfile.toString());
        prefEdit.putString("Email", userProfile.toString());
        prefEdit.commit();

    }

    private void initViews(View view) {

        btn_login = (AppCompatButton) view.findViewById(R.id.btn_login);
        tv_register = (TextView) view.findViewById(R.id.tv_register);
        tv_reset_password = (TextView) view.findViewById(R.id.tv_reset_password);
        et_email = (EditText) view.findViewById(R.id.tv_email);
        et_password = (EditText) view.findViewById(R.id.tv_password);

        progress = (ProgressBar) view.findViewById(R.id.progress);

        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        tv_reset_password.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        preferences = this.getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_register:
                goToRegister();
                break;

            case R.id.btn_login:
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {

                    progress.setVisibility(View.VISIBLE);
                    loginProcess(email, password);

                } else {

                    Snackbar.make(getView(), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_reset_password:
                goToResetPassword();
                break;
        }
    }

    private void loginProcess(String email, String password) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.putString(Constants.EMAIL,resp.getUser().getEmail());
                    editor.putString(Constants.NAME,resp.getUser().getName());
                    editor.putString(Constants.UNIQUE_ID,resp.getUser().getUnique_id());
                    editor.apply();
                    getActivity().finish();

                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

            }
        });
    }

    private void goToResetPassword() {

        Fragment reset = new Fragment_ResetPass();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.activity_login, reset);
        ft.commit();
    }

    private void goToRegister() {

        Fragment register = new Fragment_Register();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.activity_login, register);
        ft.commit();
    }
}
