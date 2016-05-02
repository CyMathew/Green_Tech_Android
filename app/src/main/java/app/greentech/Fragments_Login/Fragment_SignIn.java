package app.greentech.Fragments_Login;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;

import org.json.JSONObject;

import java.util.Arrays;

import app.greentech.Utils.Constants;
import app.greentech.Models.ServerRequest;
import app.greentech.Models.ServerResponse;
import app.greentech.Models.User;
import app.greentech.R;
import app.greentech.Utils.GetGUsernameTask;
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

    final static int TYPE_FB = 0x1;
    final static int TYPE_GOOG = 0x2;
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    String mEmail; // Received from newChooseAccountIntent(); passed to getToken()
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        preferences = this.getActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        callbackManager = CallbackManager.Factory.create();
        fbLoginButton = (LoginButton) view.findViewById(R.id.btn_fb_login);
        fbLoginButton.setReadPermissions(Arrays.asList("email", "user_friends"));
        fbLoginButton.setFragment(this);

        // Callback registration
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("Info", "User succeeded in Facebook login");
                getFBUserInfo(loginResult);
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }

            @Override
            public void onCancel() {
                Log.i("Info", "Facebook login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("Error", "Facebook couldn't log in");
                exception.printStackTrace();
            }
        });

        // Other app specific specialization
        gLoginButton = (SignInButton) view.findViewById(R.id.btn_google_login);
        gLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickGUserAccount();
            }
        });

        initViews(view);

        return view;
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
                    serverLoginProcess(email, password);

                } else {

                    Snackbar.make(getView(), "Fields are empty!", Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.tv_reset_password:
                goToResetPassword();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_ACCOUNT)
        {
            // Receiving a result from the AccountPicker
            if (resultCode == Activity.RESULT_OK)
            {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                getGUsername();

                SharedPreferences.Editor prefEdit = preferences.edit();
                prefEdit.putString("GUsername", mEmail);
                //prefEdit.putInt("AccountType", TYPE_GOOG);
                prefEdit.putBoolean(getString(R.string.is_logged_in), true);
                prefEdit.commit();

                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
            else if (resultCode == Activity.RESULT_CANCELED)
            {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(getActivity(), R.string.pick_account, Toast.LENGTH_SHORT).show();
            }
        }
        // Handle the result from exceptions
        //TODO: Exception Handling for Not logging in
    }

    private void serverLoginProcess(String email, String password) {

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

    /*
        To get the facebook user's own profile information via  creating a new request.
        When the request is completed, a callback is called to handle the success condition.
    */
    protected void getFBUserInfo(LoginResult loginResult){

        GraphRequest data_request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {

                        //saveInfo(TYPE_FB, );
                        SharedPreferences.Editor prefEdit = preferences.edit();
                        prefEdit.putString("FB_jsondata", json_object.toString());
                        prefEdit.putInt("AccountType", TYPE_FB);
                        prefEdit.putBoolean(getString(R.string.is_logged_in), true);
                        prefEdit.commit();

                    }
                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    private void pickGUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    /**
     * Attempts to retrieve the username.
     * If the account is not yet known, invoke the picker. Once the account is known,
     * start an instance of the AsyncTask to get the auth token and do work with it.
     */
    private void getGUsername() {
        if (mEmail == null)
        {
            pickGUserAccount();
        }
        else
        {
            if (isDeviceOnline())
            {
                new GetGUsernameTask(getActivity(), mEmail, SCOPE).execute();
            }
            else
            {
                Toast.makeText(getActivity(), R.string.not_online, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isDeviceOnline()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method is a hook for background threads and async tasks that need to
     * provide the user a response UI when an exception occurs.
     */
    /*public void handleException(final Exception e)
    {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException)e).getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode, getActivity(), REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent, REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }*/


    /*private void saveInfo(int type)
    {
        switch(type)
        {
            case TYPE_FB:
                Profile userProfile = Profile.getCurrentProfile();

                //fos = new FileOutputStream(userProfile.getProfilePictureUri(15, 15).getPath());
                //image.compress(format, 100, fos);


                SharedPreferences.Editor prefEdit = preferences.edit();
                prefEdit.putString("Username", userProfile.getName());
                prefEdit.putString("Email", fb_email);
                prefEdit.putBoolean(getString(R.string.is_logged_in), true);
                prefEdit.commit();
                break;
        }


    }*/
}
