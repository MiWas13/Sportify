package com.michael.sportify.Registration;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.michael.sportify.R;

import java.io.ByteArrayOutputStream;


/**
 * Created by Michael on 16.04.16.
 */
public class RegistrationFragment extends DialogFragment implements OnClickListener {
    private MyRegistrationHandler myRegistrationHandler = new MyRegistrationHandler();
    private DialogFragment registrationResult;
    private final String LOG_TAG = "myLogs";
    private EditText emailView, passwordView, firstNameView, secondNameView, dayView, monthView, yearView;
    private Bitmap myBitmap;
    private int sex = 1;
    private Button btnMen, btnWomen;
    private RegistrationItem registrationItem;
    private ProgressDialog progressDialog;
    private Bundle args = new Bundle();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Получаем dialog с помощью getDialog и устанавливаем заголовок диалога
        getDialog().setTitle("Добро пожаловать!");
        //Инициализируем переменные
        View v = inflater.inflate(R.layout.registration_fragment, null);
        v.findViewById(R.id.btnMen).setOnClickListener(this);
        v.findViewById(R.id.btnWomen).setOnClickListener(this);
        v.findViewById(R.id.back_to_main).setOnClickListener(this);
        btnMen = (Button) v.findViewById(R.id.btnMen);
        btnWomen = (Button) v.findViewById(R.id.btnWomen);
        Button confirmBtn = (Button) v.findViewById(R.id.confirm_btn);
        emailView = (EditText) v.findViewById(R.id.userEmail);
        passwordView = (EditText) v.findViewById(R.id.userPassword);
        firstNameView = (EditText) v.findViewById(R.id.userFirstName);
        secondNameView = (EditText) v.findViewById(R.id.userSecondName);
        myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        dayView = (EditText) v.findViewById(R.id.day);
        monthView = (EditText) v.findViewById(R.id.month);
        yearView = (EditText) v.findViewById(R.id.year);
        registrationResult = new ResultFragment();
        registrationItem = new RegistrationItem();
        //Проверка интернет соединения при входе
        if (!hasConnection(inflater.getContext())) {
            Toast.makeText(inflater.getContext(), "Подключитесь к интернету!", Toast.LENGTH_LONG).show();
        }

        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });

        return v;
    }

    //Метод для проверки валидности полей

    private void checkFields() {

        boolean validation = true;

        // Ошибки по умолчанию
        emailView.setError(null);
        passwordView.setError(null);
        firstNameView.setError(null);
        secondNameView.setError(null);

        // Получаем значения EditText
        registrationItem.setEmail(emailView.getText().toString());
        registrationItem.setPassword(passwordView.getText().toString());
        registrationItem.setFirsName(firstNameView.getText().toString());
        registrationItem.setSecondName(secondNameView.getText().toString());
        registrationItem.setAvatar(encodeToBase64(myBitmap, Bitmap.CompressFormat.PNG, 100));
        registrationItem.setBirthDate(yearView.getText().toString() + "-" + monthView.getText().toString() + "-" + dayView.getText().toString());
        registrationItem.setSex(sex);

        // Проверка валидности имени
        if (TextUtils.isEmpty(registrationItem.getFirsName())) {
            firstNameView.setError(getString(R.string.error_field_required));
            validation = false;
        }

        // Проверка валидности фамилии
        if (TextUtils.isEmpty(registrationItem.getSecondName())) {
            secondNameView.setError(getString(R.string.error_field_required));
            validation = false;
        }

        // Проверка валидности даты рождения
        if (TextUtils.isEmpty(yearView.getText()) | TextUtils.isEmpty(monthView.getText()) | TextUtils.isEmpty(dayView.getText())) {
            yearView.setError(getString(R.string.error_field_required));
            validation = false;
        }

        // Проверка валидности E-mail
        if (TextUtils.isEmpty(registrationItem.getEmail())) {
            emailView.setError(getString(R.string.error_field_required));
            validation = false;
        } else if (!isEmailValid(registrationItem.getEmail())) {
            emailView.setError(getString(R.string.error_invalid_email));
            validation = false;
        }

        // Проверка валидности пароля
        if (TextUtils.isEmpty(registrationItem.getPassword())) {
            passwordView.setError(getString(R.string.error_field_required));
            validation = false;
        } else if (!isPasswordValid(registrationItem.getPassword())) {
            passwordView.setError(getString(R.string.error_invalid_password));
            validation = false;
        }

        // Проверка валидности всех полей, для запуска серверной части(сервиса)
        if (hasConnection(getActivity())) {
            if (validation) {
                Log.d(LOG_TAG, "Запускаем AsyncTask");
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Загрузка...");
                progressDialog.show();
                Intent intent = new Intent(getActivity(), RegistrationService.class);
                intent.putExtra("EMAIL", registrationItem.getEmail());
                intent.putExtra("PASSWORD", registrationItem.getPassword());
                intent.putExtra("FIRST_NAME", registrationItem.getFirsName());
                intent.putExtra("SECOND_NAME", registrationItem.getSecondName());
                intent.putExtra("IMAGE", registrationItem.getAvatar());
                intent.putExtra("BIRTH_DATE", registrationItem.getBirthDate());
                intent.putExtra("SEX", String.valueOf(registrationItem.getSex()));
                intent.putExtra("MSG", new Messenger(myRegistrationHandler));
                this.getActivity().startService(intent);
            }
        } else {
            Toast.makeText(getActivity(), "Подключитесь к интернету!", Toast.LENGTH_LONG).show();
        }

    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnWomen:
                btnWomen.setBackgroundColor(getResources().getColor(R.color.activeSex));
                btnWomen.setTextColor(getResources().getColor(R.color.white));
                btnMen.setBackgroundColor(getResources().getColor(R.color.passiveSex));
                btnMen.setTextColor(getResources().getColor(R.color.grey));
                sex = 0;
                break;
            case R.id.btnMen:
                btnMen.setBackgroundColor(getResources().getColor(R.color.activeSex));
                btnMen.setTextColor(getResources().getColor(R.color.white));
                btnWomen.setBackgroundColor(getResources().getColor(R.color.passiveSex));
                btnWomen.setTextColor(getResources().getColor(R.color.grey));
                sex = 1;
                break;
            case R.id.back_to_main:
                dismiss();
                break;
        }
    }

    public class MyRegistrationHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (msg.arg1 == 1) {
                args.putBoolean("RESULT", true);
                dismiss();
                Log.d(LOG_TAG, "Все верно, добавляем в БД");
            } else if (msg.arg1 == 2) {
                emailView.setError(getString(R.string.error_user));
                Log.d(LOG_TAG, "Такой пользователь уже есть");
            } else if (msg.arg1 == 3) {
                args.putBoolean("RESULT", false);
            }
            registrationResult.setArguments(args);
            registrationResult.show(getFragmentManager(), "registrationResult");
        }
    }


    //Метод onDismiss срабатывает когда диалог закрывается
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Закрываем фрагмент");
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

    //Метод для преобразования картинки в Base64
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
}