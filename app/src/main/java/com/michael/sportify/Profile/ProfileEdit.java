package com.michael.sportify.Profile;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.michael.sportify.R;


/**
 * Created by Michael on 05.05.16.
 */
public class ProfileEdit extends DialogFragment {
    private String userLogin;
    private EditText personAbout, personFavourite;
    private ProfileItem profileItem;
    private ProgressDialog progressDialog;
    private MyProfileEditHandler myProfileEditHandler;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Получаем dialog с помощью getDialog и устанавливаем заголовок диалога
        getDialog().setTitle("Редактирование профиля");
        View v = inflater.inflate(R.layout.editprofile_fragment, null);
        myProfileEditHandler = new MyProfileEditHandler();
        userLogin = getArguments().getString("LOGIN");
        profileItem = new ProfileItem();
        personAbout = (EditText) v.findViewById(R.id.about_user_edit);
        personFavourite = (EditText) v.findViewById(R.id.favourite_games_edit);
        Button confirmButton = (Button) v.findViewById(R.id.confirm_edit);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });
        return v;
    }

    //Метод проверки полей
    private void checkFields() {

        boolean validation = true;
        personAbout.setError(null);
        personFavourite.setError(null);

        profileItem.setAbout(personAbout.getText().toString());
        profileItem.setFavouriteGames(personFavourite.getText().toString());

        // Проверка валидности "О себе"
        if (TextUtils.isEmpty(profileItem.getAbout())) {
            personAbout.setError(getString(R.string.error_field_required));
            validation = false;
        }

        // Проверка валидности имени
        if (TextUtils.isEmpty(profileItem.getFavouriteGames())) {
            personAbout.setError(getString(R.string.error_field_required));
            validation = false;
        }
        // Проверка валидности всех полей, для запуска серверной части
        if (hasConnection(getActivity())) {
            if (validation) {
                Intent intent = new Intent(getActivity(), ProfileEditService.class);
                intent.putExtra("USER_LOGIN", userLogin);
                intent.putExtra("ABOUT_USER", profileItem.getAbout());
                intent.putExtra("FAVOURITE_GAMES", profileItem.getFavouriteGames());
                intent.putExtra("MSG", new Messenger(myProfileEditHandler));
                this.getActivity().startService(intent);
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Загрузка...");
                progressDialog.show();
            }
        } else {
            Toast.makeText(getActivity(), "Подключитесь к интернету!", Toast.LENGTH_LONG).show();
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

    //Создаем Handler для обмена параметрами с сервисом
    public class MyProfileEditHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            dismiss();
        }
    }

}
