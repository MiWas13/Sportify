package com.michael.sportify;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.michael.sportify.Authorization.AuthorizationFragment;

import com.michael.sportify.Registration.RegistrationFragment;
import com.michael.sportify.Registration.RegistrationItem;
import com.michael.sportify.Registration.RegistrationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Objects;



/**
 * Created by Michael on 16.04.16.
 */
public class MainActivity extends Activity {
    private RegistrationItem registrationItem = new RegistrationItem();
    private DialogFragment registration;
    private DialogFragment authorization;
    private static final String APP_PREFERENCES = "MY_PREF";
    private SharedPreferences sPrefAuth;
    private CallbackManager callbackManager;
    private Bitmap myBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (loadState()) {
            loadResult();
        }
        Log.d("STATE", String.valueOf(loadState()));
        registration = new RegistrationFragment();
        authorization = new AuthorizationFragment();
        myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_fb);
        //Авторизируемся через Facebook
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                final Bundle userData = new Bundle();
                //Запрашиваем JSON с данными

                userData.putString("fields", "id, name, gender");
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/v2.5/" + loginResult.getAccessToken().getUserId(),
                        userData,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                JSONObject json = response.getJSONObject();
                                Log.d("FB_JSON", String.valueOf(json));
                                //Парсим JSON
                                try {
                                    registrationItem.setEmail(json.getString("id"));
                                    registrationItem.setPassword(null);
                                    registrationItem.setAvatar(encodeToBase64(myBitmap, Bitmap.CompressFormat.PNG, 100));
                                    registrationItem.setFirsName(json.getString("name"));
                                    registrationItem.setSecondName("");
                                    if (Objects.equals(json.getString("gender"), "мужской")||Objects.equals(json.getString("gender"), "male")) {
                                        registrationItem.setSex(1);
                                    } else {
                                        registrationItem.setSex(0);
                                    }
                                    if (!(json.getString("birthday") == null)) {
                                        registrationItem.setBirthDate(json.getString("birthday"));
                                    } else {
                                        registrationItem.setBirthDate("0000-00-00");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //Вносим пользователя в нашу БД через сервис регистрации
                                Intent regService = new Intent(MainActivity.this, RegistrationService.class);
                                MyRegistrationHandler myRegistrationHandler = new MyRegistrationHandler();
                                regService.putExtra("EMAIL", registrationItem.getEmail());
                                regService.putExtra("PASSWORD", registrationItem.getPassword());
                                regService.putExtra("FIRST_NAME", registrationItem.getFirsName());
                                regService.putExtra("SECOND_NAME", registrationItem.getSecondName());
                                regService.putExtra("IMAGE", registrationItem.getAvatar());
                                regService.putExtra("BIRTH_DATE", registrationItem.getBirthDate());
                                regService.putExtra("SEX", String.valueOf(registrationItem.getSex()));
                                regService.putExtra("MSG", new Messenger(myRegistrationHandler));
                                startService(regService);
                            }
                        }
                ).executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

        //Выставляем шрифты
        Typeface robotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        Button registrationBtn = (Button) findViewById(R.id.btn_registration);
        Button authorizationBtn = (Button) findViewById(R.id.btn_authorization);
        registrationBtn.setTypeface(robotoMedium);
        authorizationBtn.setTypeface(robotoMedium);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_registration:
                registration.show(getFragmentManager(), "registration");
                break;
            case R.id.btn_authorization:
                authorization.show(getFragmentManager(), "authorization");
                break;
        }
    }

    public class MyRegistrationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if ((msg.arg1 == 1) || (msg.arg1 == 2)) {
                Intent intent = new Intent(MainActivity.this, BottomMenu.class);
                intent.putExtra("USER_LOGIN", registrationItem.getEmail());
                startActivity(intent);
                saveResult(registrationItem.getEmail());
                finish();
            }
        }
    }


    //Метод, проверяющий входил ли пользователь в свой аккаунт
    public void loadResult() {
        sPrefAuth = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String savedText = sPrefAuth.getString("MY_PREF", "");
        String userLogin = sPrefAuth.getString("USER_LOGIN", "");
        if (!(savedText == null)) {
            Intent intent = new Intent(MainActivity.this, BottomMenu.class);
            intent.putExtra("RESULT", savedText);
            intent.putExtra("USER_LOGIN", userLogin);
            startActivity(intent);
        }
    }

    public Boolean loadState() {
        sPrefAuth = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Boolean state = sPrefAuth.getBoolean("STATE", false);
        if (state) {
            return true;
        } else {
            return false;
        }
    }

    //Метод сохранение предыдущего результаты авторизации
    public void saveResult(String login) {
        SharedPreferences sPrefAuth = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPrefAuth.edit();
        ed.putBoolean("STATE", true);
        ed.putString("USER_LOGIN", login);
        ed.commit();
    }

    //Метод для преобразования картинки в Base64
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}