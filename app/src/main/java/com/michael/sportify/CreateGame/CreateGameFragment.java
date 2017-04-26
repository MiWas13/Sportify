package com.michael.sportify.CreateGame;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.michael.sportify.R;
import com.michael.sportify.Registration.ResultFragment;

import java.util.Calendar;


/**
 * Created by Michael on 12.05.16.
 */
public class CreateGameFragment extends DialogFragment {
    private MyCreateGameHandler myCreateGameHandler;
    private DialogFragment createGameMap;
    private TextView placeView, dateAndTimeView;
    private EditText createDescriptionView, createQuantityView, createCommentView;
    private CheckBox equipmentCheck;
    private CreateGameItem createGameItem = new CreateGameItem();
    private ProgressDialog progressDialog;
    private String userLogin, time, date;
    private Calendar dateAndTime = Calendar.getInstance();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_game, container, false);
        getDialog().setTitle("Создание игры");

        //Получаем параметры при перезапуске(после выбора метки на карте)
        try {
            createGameItem.setLocationLatitude(getArguments().getString("LOCATION_LATITUDE"));
            createGameItem.setLocationLongitude(getArguments().getString("LOCATION_LONGITUDE"));
        } catch (Exception e) {
            createGameItem.setLocationLatitude("0");
            createGameItem.setLocationLongitude("0");
        }

        try {
            createGameItem.setTime(getArguments().getString("TIME"));
        } catch (Exception e) {
            createGameItem.setTime("0");
        }

        try {
            createGameItem.setDate(getArguments().getString("DATE"));
        } catch (Exception e) {
            createGameItem.setDate("0");
        }

        Bundle argument = getArguments();
        userLogin = argument.getString("USER_LOGIN");

        //Инициализируем переменные
        myCreateGameHandler = new MyCreateGameHandler();
        Button publicationBtn = (Button) view.findViewById(R.id.publication_btn);
        createDescriptionView = (EditText) view.findViewById(R.id.create_description);
        createQuantityView = (EditText) view.findViewById(R.id.create_quantity);
        createCommentView = (EditText) view.findViewById(R.id.create_comment);
        TextView mapCreateGame = (TextView) view.findViewById(R.id.map_create_game);
        placeView = (TextView) view.findViewById(R.id.place_view);
        dateAndTimeView = (TextView) view.findViewById(R.id.date_and_time_view);
        Spinner spinnerKind = (Spinner) view.findViewById(R.id.kind_of_sport_spinner);
        Button dateBtn = (Button) view.findViewById(R.id.date_btn);
        Button timeBtn = (Button) view.findViewById(R.id.time_btn);
        equipmentCheck = (CheckBox) view.findViewById(R.id.equipment_check);

        ArrayAdapter<?> sportKindAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.kind_of_sport, android.R.layout.simple_spinner_item);
        sportKindAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerKind.setAdapter(sportKindAdapter);
        spinnerKind.setSelection(0);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), d,
                        dateAndTime.get(Calendar.YEAR),
                        dateAndTime.get(Calendar.MONTH),
                        dateAndTime.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getActivity(), t,
                        dateAndTime.get(Calendar.HOUR_OF_DAY),
                        dateAndTime.get(Calendar.MINUTE), true)
                        .show();
            }
        });

        publicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });

        mapCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("USER_LOGIN", userLogin);
                args.putString("TIME", time);
                args.putString("DATE", date);
                createGameMap = new CreateGameMap();
                createGameMap.setArguments(args);
                createGameMap.show(getFragmentManager(), "CREATE_GAME_MAP");
                dismiss();
            }
        });

        spinnerKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                try {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                } catch (Exception e) {
                    System.out.print("Ошибка в установке цвета");
                }
                String[] choose = getResources().getStringArray(R.array.kind_of_sport);
                createGameItem.setSportKind(choose[selectedItemPosition]);
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    //Проверка полей
    public void checkFields() {

        boolean validation = true;

        // Ошибки по умолчанию
        createDescriptionView.setError(null);
        createQuantityView.setError(null);
        createCommentView.setError(null);

        createGameItem.setDescription(createDescriptionView.getText().toString());
        createGameItem.setQuantity(createQuantityView.getText().toString());
        createGameItem.setComment(createCommentView.getText().toString());

        if (equipmentCheck.isChecked()) {
            createGameItem.setEquipmentNeed("true");
        } else {
            createGameItem.setEquipmentNeed("false");
        }

        if (TextUtils.isEmpty(createGameItem.getDescription())) {
            createDescriptionView.setError(getString(R.string.error_field_required));
            validation = false;
        }

        if (TextUtils.isEmpty(createGameItem.getLocationLatitude())) {
            placeView.setError(getString(R.string.error_field_required));
            validation = false;
        }

        if ((TextUtils.isEmpty(createGameItem.getDate())) || (TextUtils.isEmpty(createGameItem.getTime()))) {
            dateAndTimeView.setError(getString(R.string.error_field_required));
            validation = false;
        }

        if (TextUtils.isEmpty(createGameItem.getQuantity())) {
            createQuantityView.setError(getString(R.string.error_field_required));
            validation = false;
        } else if (!isQuantityValid(Integer.parseInt(createGameItem.getQuantity()))) {
            createQuantityView.setError(getString(R.string.error_quantity));
            validation = false;
        }

        if (TextUtils.isEmpty(createGameItem.getComment())) {
            createCommentView.setError(getString(R.string.error_field_required));
            validation = false;
        }


        // Проверка валидности всех полей, для запуска серверной части
        if (hasConnection(getActivity())) {
            if (validation) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Загрузка...");
                progressDialog.show();
                Intent intent = new Intent(getActivity(), CreateGameService.class);
                intent.putExtra("CREATOR", userLogin);
                intent.putExtra("SPORT_KIND", createGameItem.getSportKind());
                intent.putExtra("DESCRIPTION", createGameItem.getDescription());
                intent.putExtra("MAX_QUANTITY", createGameItem.getQuantity());
                intent.putExtra("EQUIPMENT_COMMENT", createGameItem.getComment());
                intent.putExtra("EQUIPMENT_NEED", createGameItem.getEquipmentNeed());
                intent.putExtra("LOCATION_LATITUDE", createGameItem.getLocationLatitude());
                intent.putExtra("LOCATION_LONGITUDE", createGameItem.getLocationLongitude());
                intent.putExtra("TIME", createGameItem.getDate() + " " + createGameItem.getTime());
                intent.putExtra("MSG", new Messenger(myCreateGameHandler));
                this.getActivity().startService(intent);
            }
        } else {
            Toast.makeText(getActivity(), "Подключитесь к интернету!", Toast.LENGTH_LONG).show();
        }

    }

    private boolean isQuantityValid(int quantity) {
        return quantity >= 2 && quantity < 9;
    }


    //Отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(getActivity(), d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    //Отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(getActivity(), t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    //Установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            time = String.valueOf(hourOfDay) + ":" + String.valueOf(minute) + ":" + "00";
            createGameItem.setTime(time);
        }
    };

    //Установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            date = String.valueOf(dateAndTime.get(Calendar.YEAR)) + "-" + String.valueOf(dateAndTime.get(Calendar.MONTH) + 1) + "-" + String.valueOf(dateAndTime.get(Calendar.DAY_OF_MONTH));
            createGameItem.setDate(date);
        }
    };

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
    public class MyCreateGameHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            Bundle args = new Bundle();
            if (msg.arg1 == 1) {
                args.putBoolean("RESULT", true);
                dismiss();
            } else {
                args.putBoolean("RESULT", false);
            }
            DialogFragment resultFragment = new ResultFragment();
            resultFragment.setArguments(args);
            resultFragment.show(getFragmentManager(), "RESULT_FRAGMENT");
        }
    }

}
