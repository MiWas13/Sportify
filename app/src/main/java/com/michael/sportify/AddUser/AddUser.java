package com.michael.sportify.AddUser;

import android.app.DialogFragment;
import android.app.ProgressDialog;
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

import java.util.ArrayList;

/**
 * Created by Michael on 18.05.16.
 */
public class AddUser extends DialogFragment {
    private AddUserRecyclerAdapter addUserRecyclerAdapter;
    private ArrayList<AddUserItem> addUserItemsArray = new ArrayList<>();
    private ProgressDialog progressDialog;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.games_recycler, null);
        Bundle arguments = getArguments();
        String id = arguments.getString("GAME_ID");
        getDialog().setTitle("Заявки в игру");
        addUserItemsArray.clear();
        RecyclerView addUserToGameRecycler = (RecyclerView) view.findViewById(R.id.gridRecyclerView);
        addUserToGameRecycler.setHasFixedSize(true);
        addUserToGameRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        addUserRecyclerAdapter = new AddUserRecyclerAdapter(addUserItemsArray, getActivity());
        addUserToGameRecycler.setAdapter(addUserRecyclerAdapter);
        MyAddUserHandler myAddUserHandler = new MyAddUserHandler();
        Intent intent = new Intent(getActivity(), AddListService.class);
        intent.putExtra("GAME_ID", id);
        intent.putExtra("MSG", new Messenger(myAddUserHandler));
        this.getActivity().startService(intent);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Загрузка...");
        progressDialog.show();
        return view;

    }

    //Создаем Handler для обмена параметрами с сервисом
    public class MyAddUserHandler extends Handler {
        private JSONObject dataJSONObject;
        private JSONArray sportArray;
        private JSONObject sportJSONItem;

        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            //Парсим JSON
            try {
                dataJSONObject = new JSONObject(String.valueOf(msg.obj));
                Log.d("MSG_HANDLER", String.valueOf(msg.obj));
                sportArray = dataJSONObject.getJSONArray("users");
                for (int i = 0; i < sportArray.length(); i++) {
                    sportJSONItem = sportArray.getJSONObject(i);
                    AddUserItem addUserItem = new AddUserItem();
                    addUserItem.setId(sportJSONItem.getString("id_game"));
                    addUserItem.setAvatar(sportJSONItem.getString("image"));
                    addUserItem.setLogin(sportJSONItem.getString("email"));
                    addUserItem.setUserName(sportJSONItem.getString("firstname") + " " + sportJSONItem.getString("secondname"));
                    Log.d("IMAGE", addUserItem.getAvatar());
                    Log.d("NAME", addUserItem.getUserName());
                    addUserItemsArray.add(addUserItem);
                }

                addUserRecyclerAdapter.notifyDataSetChanged();

            } catch (Exception e) {

            }

        }
    }
}

