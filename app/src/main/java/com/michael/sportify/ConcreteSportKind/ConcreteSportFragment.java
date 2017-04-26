package com.michael.sportify.ConcreteSportKind;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michael.sportify.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * Created by Michael on 08.05.16.
 */
public class ConcreteSportFragment extends Fragment {
    private ConcreteSportRecyclerAdapter concreteSportRecyclerAdapter;
    private ArrayList<ConcreteSportItem> concreteSportItemArrayList = new ArrayList<>();
    private String userLogin;
    private GregorianCalendar gregorianCalendar = new GregorianCalendar();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.concrete_game_list, container, false);
        concreteSportItemArrayList.clear();
        Bundle bundle = getArguments();
        int position = bundle.getInt("POSITION");
        int requestKind = bundle.getInt("REQUEST_KIND");
        userLogin = bundle.getString("USER_LOGIN");
        MyConcreteSportHandler myConcreteSportHandler = new MyConcreteSportHandler();
        Intent intent = new Intent(getActivity(), ConcreteSportService.class);
        intent.putExtra("REQUEST_KIND", requestKind);
        intent.putExtra("USER_LOGIN", String.valueOf(userLogin));
        intent.putExtra("POSITION", String.valueOf(position));
        intent.putExtra("MSG", new Messenger(myConcreteSportHandler));
        this.getActivity().startService(intent);
        RecyclerView concreteSportRecyclerView = (RecyclerView) view.findViewById(R.id.concrete_sport_recycler_view);
        concreteSportRecyclerView.setHasFixedSize(true);
        concreteSportRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        concreteSportRecyclerAdapter = new ConcreteSportRecyclerAdapter(concreteSportItemArrayList, getActivity());
        concreteSportRecyclerView.setAdapter(concreteSportRecyclerAdapter);
        return view;
    }

    //Создаем Handler для обмена параметрами с сервисом
    public class MyConcreteSportHandler extends Handler {
        JSONObject dataJSONObject;
        JSONArray sportArray;
        JSONObject sportJSONItem;

        @Override
        public void handleMessage(Message msg) {
            try {
                dataJSONObject = new JSONObject(String.valueOf(msg.obj));
                sportArray = dataJSONObject.getJSONArray("sports");
                for (int i = 0; i < sportArray.length(); i++) {
                    sportJSONItem = sportArray.getJSONObject(i);
                    ConcreteSportItem concreteSportItem = new ConcreteSportItem();
                    concreteSportItem.setSportKind(sportJSONItem.getString("sport"));
                    concreteSportItem.setGameId(sportJSONItem.getInt("id"));
                    concreteSportItem.setQuantity(sportJSONItem.getString("current_quantity") + "/" + sportJSONItem.getString("max_quantity"));
                    concreteSportItem.setLocationLatitude(sportJSONItem.getString("location_latitude"));
                    concreteSportItem.setLocationLongitude(sportJSONItem.getString("location_longitude"));
                    if (Objects.equals(sportJSONItem.getString("creator"), userLogin)) {
                        concreteSportItem.setVisibleButton(1);
                    } else {
                        concreteSportItem.setVisibleButton(0);
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
                        concreteSportItem.setDate("Сегодня");
                    } else if ((String.valueOf(new SimpleDateFormat("yyyy").format(gameDate)).equals(String.valueOf(new SimpleDateFormat("yyyy").format(todayDate)))) &&
                            (String.valueOf(new SimpleDateFormat("MM").format(gameDate)).equals(String.valueOf(new SimpleDateFormat("MM").format(todayDate)))) &&
                            (String.valueOf(new SimpleDateFormat("dd").format(gameDate)).equals(String.valueOf(Integer.parseInt(new SimpleDateFormat("dd").format(todayDate)) + 1)))) {
                        concreteSportItem.setDate("Завтра");
                    } else {
                        concreteSportItem.setDate(String.valueOf(new SimpleDateFormat("dd.MM.yyyy").format(gameDate)));
                    }


                    Log.d("USER_LOGIN", userLogin);
                    Log.d("JSON_LOGIN", sportJSONItem.getString("creator"));
                    Log.d("creator", String.valueOf(concreteSportItem.getVisibleButton()));
                    concreteSportItem.setLogin(userLogin);
                    concreteSportItem.setTime(sportJSONItem.getString("time").substring(11, 16));
                    concreteSportItemArrayList.add(concreteSportItem);
                }
                concreteSportRecyclerAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                System.out.print("Ошибка в JSON-ответе");
            }
        }
    }

}
