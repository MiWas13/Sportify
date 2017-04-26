package com.michael.sportify.ConcreteGame;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.michael.sportify.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * Created by Michael on 09.05.16.
 */
public class ConcreteGameFragment extends DialogFragment {
    private GoogleMap map;
    private int gameId;
    private ConcreteGameItem concreteGameItem = new ConcreteGameItem();
    private GregorianCalendar gregorianCalendar = new GregorianCalendar();
    private Button takePartBtn, deleteGameBtn;
    private TextView quantityView, dateView, equipmentView, descriptionView;
    private ProgressDialog progressDialog;
    private String userLogin;
    private static View view;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Чтобы карта не прогружалась заново и не вызывала NullPointerException, проверяем создан ли View
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.concrete_game_fragment, container, false);
        } catch (InflateException e) {
            System.out.print("Не удалось создать View");
        }
        Bundle argument = getArguments();
        userLogin = argument.getString("USER_LOGIN");
        quantityView = (TextView) view.findViewById(R.id.quantity_concrete_view);
        dateView = (TextView) view.findViewById(R.id.date_concrete_view);
        equipmentView = (TextView) view.findViewById(R.id.game_equipment);
        descriptionView = (TextView) view.findViewById(R.id.game_description);
        takePartBtn = (Button) view.findViewById(R.id.take_part);
        takePartBtn.setVisibility(View.VISIBLE);
        deleteGameBtn = (Button) view.findViewById(R.id.delete_game);
        deleteGameBtn.setVisibility(View.GONE);
        Bundle arguments = getArguments();
        gameId = arguments.getInt("ID");
        MyConcreteGameHandler myConcreteGameHandler = new MyConcreteGameHandler();
        final Intent intent = new Intent(getActivity(), ConcreteGameService.class);
        intent.putExtra("GAME_ID", String.valueOf(gameId));
        intent.putExtra("MSG", new Messenger(myConcreteGameHandler));
        this.getActivity().startService(intent);

        takePartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Загрузка...");
                progressDialog.show();
                MyTakePartHandler myTakePartHandler = new MyTakePartHandler();
                Intent intentTakePart = new Intent(getActivity(), TakePartService.class);
                intentTakePart.putExtra("GAME_ID", String.valueOf(gameId));
                intentTakePart.putExtra("USER_LOGIN", String.valueOf(userLogin));
                intentTakePart.putExtra("MSG", new Messenger(myTakePartHandler));
                getActivity().startService(intentTakePart);
            }
        });

        deleteGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Удаление...");
                progressDialog.show();
                MyDeleteGameHandler myDeleteGameHandler = new MyDeleteGameHandler();
                Intent intentDeleteGame = new Intent(getActivity(), DeleteGameService.class);
                intentDeleteGame.putExtra("GAME_ID", String.valueOf(gameId));
                intentDeleteGame.putExtra("USER_LOGIN", String.valueOf(userLogin));
                intentDeleteGame.putExtra("MSG", new Messenger(myDeleteGameHandler));
                getActivity().startService(intentDeleteGame);
            }
        });

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map_concrete_game_view);
        map = mapFragment.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        return view;
    }

    //Метод для определения окончания и дальнейшего склонения слов "альбом" и "трек"
    public static String CheckForEnding(int n, String s) {
        if ((n % 100 >= 11) && (n % 100 <= 14)) {
            s = n + " " + s;
        } else switch (n % 10) {
            case 0:
                s = n + " " + s;
                break;
            case 1:
                s = n + " " + s;
                break;
            case 2:
                s = n + " " + s + "а";
                break;
            case 3:
                s = n + " " + s + "а";
                break;
            case 4:
                s = n + " " + s + "а";
                break;
            case 5:
                s = n + " " + s;
                break;
            case 6:
                s = n + " " + s;
                break;
            case 7:
                s = n + " " + s;
                break;
            case 8:
                s = n + " " + s;
                break;
            case 9:
                s = n + " " + s;
                break;
        }
        return s;
    }


    //Создаем Handler для обмена параметрами с сервисом
    public class MyConcreteGameHandler extends Handler {
        private JSONObject dataJSONObject;
        private JSONArray sportArray;
        private JSONObject sportJSONItem;

        @Override
        public void handleMessage(Message msg) {
            try {
                dataJSONObject = new JSONObject(String.valueOf(msg.obj));
                sportArray = dataJSONObject.getJSONArray("sports");
                sportJSONItem = sportArray.getJSONObject(0);
                concreteGameItem.setSportKind(sportJSONItem.getString("sport"));
                concreteGameItem.setDescription(String.valueOf(sportJSONItem.getString("description")));
                concreteGameItem.setEquipment(String.valueOf(sportJSONItem.getString("equipment_comment")));
                concreteGameItem.setGameId(sportJSONItem.getInt("id"));
                concreteGameItem.setQuantity(sportJSONItem.getInt("max_quantity"));
                concreteGameItem.setLocationLatitude(sportJSONItem.getString("location_latitude"));
                concreteGameItem.setLocationLongitude(sportJSONItem.getString("location_longitude"));
                try {
                    if (Objects.equals(sportJSONItem.getString("creator"), userLogin)) {
                        takePartBtn.setVisibility(View.GONE);
                        deleteGameBtn.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String today = String.valueOf(gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH)) + "-" + String.valueOf(String.valueOf(gregorianCalendar.get(GregorianCalendar.MONTH) + 1)) + "-" + String.valueOf(String.valueOf(gregorianCalendar.get(GregorianCalendar.YEAR)));
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                SimpleDateFormat formatToday = new SimpleDateFormat("dd-MM-yyyy");
                Date gameDate = null;
                Date todayDate = null;
                try {
                    gameDate = format.parse(sportJSONItem.getString("time"));
                    todayDate = formatToday.parse(today);
                } catch (Exception e) {

                }

                //Проверяем на какой день назначена игра
                if ((String.valueOf(new SimpleDateFormat("yyyy").format(gameDate)).equals(String.valueOf(new SimpleDateFormat("yyyy").format(todayDate)))) &&
                        (String.valueOf(new SimpleDateFormat("MM").format(gameDate)).equals(String.valueOf(new SimpleDateFormat("MM").format(todayDate)))) &&
                        (String.valueOf(new SimpleDateFormat("dd").format(gameDate)).equals(String.valueOf(new SimpleDateFormat("dd").format(todayDate))))) {
                    concreteGameItem.setDate("Сегодня");
                } else if ((String.valueOf(new SimpleDateFormat("yyyy").format(gameDate)).equals(String.valueOf(new SimpleDateFormat("yyyy").format(todayDate)))) &&
                        (String.valueOf(new SimpleDateFormat("MM").format(gameDate)).equals(String.valueOf(new SimpleDateFormat("MM").format(todayDate)))) &&
                        (String.valueOf(new SimpleDateFormat("dd").format(gameDate)).equals(String.valueOf(Integer.parseInt(new SimpleDateFormat("dd").format(todayDate)) + 1)))) {
                    concreteGameItem.setDate("Завтра");
                } else {
                    concreteGameItem.setDate(String.valueOf(new SimpleDateFormat("dd.MM.yyyy").format(gameDate)));
                }

                concreteGameItem.setTime(sportJSONItem.getString("time").substring(11, 16));


            } catch (Exception e) {

            }


            quantityView.setText(String.valueOf(CheckForEnding(concreteGameItem.getQuantity(), "Человек")));
            dateView.setText(concreteGameItem.getDate() + " " + concreteGameItem.getTime());
            equipmentView.setText(concreteGameItem.getEquipment());
            descriptionView.setText(concreteGameItem.getDescription());
            double locationLatitude = Double.parseDouble(concreteGameItem.getLocationLatitude());
            double locationLongitude = Double.parseDouble(concreteGameItem.getLocationLongitude());
            try {
                getDialog().setTitle(concreteGameItem.getSportKind());
            } catch (Exception e) {

            }

            LatLng game_location = new LatLng(locationLatitude, locationLongitude);

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(game_location, 13));
            map.addMarker(new MarkerOptions()
                    .position(game_location)
                    .title(concreteGameItem.getSportKind()));

        }

    }

    //Создаем Handler для обмена параметрами с сервисом
    public class MyTakePartHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                progressDialog.dismiss();
                dismiss();
            }

        }
    }

    public class MyDeleteGameHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                progressDialog.dismiss();
                dismiss();
            }
        }
    }

}

