package com.michael.sportify.Authorization;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.michael.sportify.BottomMenu;
import com.michael.sportify.MainActivity;
import com.michael.sportify.R;

import java.util.Objects;

/**
 * Created by Michael on 17.04.16.
 */
public class AuthorizationFragment extends DialogFragment implements View.OnClickListener {
    private EditText loginView, passwordView;
    private LayoutInflater inflaterView;
    private ProgressDialog progressDialog;
    private MyAuthorizationHandler myAuthorizationHandler;
    private AuthorizationItem authorisationItem = new AuthorizationItem();
    public static final String APP_PREFERENCES = "MY_PREF";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Получаем dialog с помощью getDialog и устанавливаем заголовок диалога
        getDialog().setTitle("Добро пожаловать!");
        View v = inflater.inflate(R.layout.authorization_fragment, null);
        inflaterView = inflater;
        TextView backToMain = (TextView) v.findViewById(R.id.back_to_main);
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        loginView = (EditText) v.findViewById(R.id.login);
        passwordView = (EditText) v.findViewById(R.id.password);
        myAuthorizationHandler = new MyAuthorizationHandler();
        Button confirmAuth = (Button) v.findViewById(R.id.confirm_auth);
        confirmAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });
        if (!hasConnection(inflater.getContext())) {
            Toast.makeText(inflater.getContext(), "Подключитесь к интернету!", Toast.LENGTH_LONG).show();
        }
        return v;
    }

    private void checkFields() {
        boolean validation = true;
        loginView.setError(null);
        passwordView.setError(null);
        authorisationItem.setLogin(loginView.getText().toString());
        authorisationItem.setPassword(passwordView.getText().toString());

        //Проверка валидности логина
        if (TextUtils.isEmpty(authorisationItem.getLogin())) {
            loginView.setError(getString(R.string.error_field_required));
            validation = false;
        }

        //Проверка валидности пароля
        if (TextUtils.isEmpty(authorisationItem.getPassword())) {
            passwordView.setError(getString(R.string.error_field_required));
            validation = false;
        }

        // Проверка валидности всех полей, для запуска серверной части
        if (hasConnection(getActivity())) {
            if (validation) {
                Intent intent = new Intent(getActivity(), AuthorizationService.class);
                intent.putExtra("LOGIN", authorisationItem.getLogin());
                intent.putExtra("PASSWORD", authorisationItem.getPassword());
                intent.putExtra("MSG", new Messenger(myAuthorizationHandler));
                this.getActivity().startService(intent);
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Загрузка...");
                progressDialog.show();
            }
        } else {
            Toast.makeText(getActivity(), "Подключитесь к интернету!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_to_main:
                dismiss();
                break;
        }

    }

    //Метод проверки подключения пользователя к интернету
    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    //Метод сохранение предыдущего результаты авторизации
    public void saveResult(String result, String login) {
        SharedPreferences sPrefAuth = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPrefAuth.edit();
        ed.putString("MY_PREF", result);
        ed.putBoolean("STATE", true);
        ed.putString("USER_LOGIN", login);
        ed.commit();
    }

    public class MyAuthorizationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (Objects.equals(String.valueOf(msg.obj), "Problem")) {
                loginView.setError(getString(R.string.error_authorization));
            } else {
                Intent intent = new Intent(inflaterView.getContext(), BottomMenu.class);
                intent.putExtra("USER_LOGIN", authorisationItem.getLogin());
                intent.putExtra("RESULT", String.valueOf(msg.obj));
                saveResult(String.valueOf(msg.obj), authorisationItem.getLogin());
                startActivity(intent);
                dismiss();
                getActivity().finish();
            }
        }
    }


}

