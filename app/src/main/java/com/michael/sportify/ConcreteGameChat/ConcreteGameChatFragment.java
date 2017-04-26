package com.michael.sportify.ConcreteGameChat;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.widget.EditText;
import android.widget.TextView;

import com.michael.sportify.MainActivity;
import com.michael.sportify.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * Created by Michael on 04.06.16.
 */
public class ConcreteGameChatFragment extends DialogFragment {
    private ConcreteGameChatRecyclerAdapter concreteGameChatRecyclerAdapter;
    private ArrayList<ConcreteGameChatItem> concreteGameChatItemArrayList = new ArrayList<>();
    private String userLogin;
    private GregorianCalendar gregorianCalendar = new GregorianCalendar();

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.concrete_game_chat_list, container, false);
        concreteGameChatItemArrayList.clear();
        getDialog().setTitle("Чат");
        //Принимаем параметры
        final Bundle bundle = getArguments();
        final String gameId = bundle.getString("GAME_ID");
        userLogin = bundle.getString("USER_LOGIN");
        TextView sendBtn = (TextView) view.findViewById(R.id.send_btn);
        final EditText messageEdit = (EditText) view.findViewById(R.id.edit_message);
        final MyConcreteGameChatHandler myConcreteGameChatHandler = new MyConcreteGameChatHandler();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Запускаем сервисы для отправки сообщения на сервер и обновления RecyclerView
                    Intent intent = new Intent(getActivity(), SendMessageService.class);
                    intent.putExtra("USER_LOGIN", userLogin);
                    intent.putExtra("GAME_ID", String.valueOf(gameId));
                    intent.putExtra("DATE", String.valueOf(gregorianCalendar.get(GregorianCalendar.YEAR)) + "0" + String.valueOf(gregorianCalendar.get(GregorianCalendar.MONTH) + 1) + "0" + String.valueOf(gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH)));
                    intent.putExtra("TEXT", messageEdit.getText().toString());
                    getActivity().startService(intent);
                    Intent intentUpdate = new Intent(getActivity(), ConcreteGameChatService.class);
                    intentUpdate.putExtra("GAME_ID", String.valueOf(gameId));
                    intentUpdate.putExtra("USER_LOGIN", userLogin);
                    intentUpdate.putExtra("MSG", new Messenger(myConcreteGameChatHandler));
                    getActivity().startService(intentUpdate);
                } catch (Exception e) {
                    System.out.print("Сервисы не запустились");
                }
            }
        });

        //При открытии фрагмента заполняем RecyclerView, обратившись к серверу
        Intent intent = new Intent(getActivity(), ConcreteGameChatService.class);
        intent.putExtra("GAME_ID", String.valueOf(gameId));
        intent.putExtra("USER_LOGIN", userLogin);
        intent.putExtra("MSG", new Messenger(myConcreteGameChatHandler));
        this.getActivity().startService(intent);
        //Подключаем RecyclerView, Adapter
        RecyclerView concreteGameChatRecyclerView = (RecyclerView) view.findViewById(R.id.concrete_game_chat_recycler_view);
        concreteGameChatRecyclerView.setHasFixedSize(true);
        concreteGameChatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        concreteGameChatRecyclerAdapter = new ConcreteGameChatRecyclerAdapter(concreteGameChatItemArrayList, getActivity());
        concreteGameChatRecyclerView.setAdapter(concreteGameChatRecyclerAdapter);
        return view;
    }

    //Создаем Handler для обмена параметрами с сервисом
    public class MyConcreteGameChatHandler extends Handler {
        JSONObject dataJSONObject;
        JSONArray sportArray;
        JSONObject sportJSONItem;

        @Override
        public void handleMessage(Message msg) {
            Log.d("MSG", String.valueOf(msg.obj));
            if (String.valueOf(msg.obj).equals("{\"chat\":ProblemProblem}")) {
                dismiss();
                DialogFragment chatExceptionFragment = new ChatExceptionFragment();
                chatExceptionFragment.show(getFragmentManager(), "EXCEPTION");
            }
            try {
                concreteGameChatItemArrayList.clear();
                dataJSONObject = new JSONObject(String.valueOf(msg.obj));
                sportArray = dataJSONObject.getJSONArray("chat");
                for (int i = 0; i < sportArray.length(); i++) {
                    sportJSONItem = sportArray.getJSONObject(i);
                    ConcreteGameChatItem concreteGameChatItem = new ConcreteGameChatItem();
                    concreteGameChatItem.setUserName(sportJSONItem.getString("firstname") + " " + sportJSONItem.getString("secondname"));
                    concreteGameChatItem.setMsgText(sportJSONItem.getString("text"));
                    concreteGameChatItem.setDate(sportJSONItem.getString("date"));
                    if (Objects.equals(userLogin, sportJSONItem.getString("author_login"))) {
                        concreteGameChatItem.setState(1);
                    } else {
                        concreteGameChatItem.setState(0);
                    }

                    if ((Integer.parseInt(sportJSONItem.getString("date").substring(0, 4)) == gregorianCalendar.get(GregorianCalendar.YEAR)) &&
                            (Integer.parseInt(sportJSONItem.getString("date").substring(4, 6)) == gregorianCalendar.get(GregorianCalendar.MONTH) + 1) &&
                            (Integer.parseInt(sportJSONItem.getString("date").substring(6, 8)) == gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH))) {
                        concreteGameChatItem.setDate("Сегодня");

                    } else {
                        concreteGameChatItem.setDate("Несколько дней назад");
                    }
                    concreteGameChatItemArrayList.add(concreteGameChatItem);
                }

                concreteGameChatRecyclerAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                System.out.print("Ошибка в JSON-ответе");
            }
        }
    }
}
