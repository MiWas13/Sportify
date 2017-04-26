package com.michael.sportify.ConcreteSportKind;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michael.sportify.AddUser.AddUser;
import com.michael.sportify.BottomMenu;
import com.michael.sportify.ConcreteGame.ConcreteGameFragment;
import com.michael.sportify.ConcreteGameChat.ConcreteGameChatFragment;
import com.michael.sportify.R;

import java.util.ArrayList;


/**
 * Created by Michael on 09.05.16.
 */
public class ConcreteSportRecyclerAdapter extends RecyclerView.Adapter<ConcreteSportRecyclerAdapter.MyViewHolder> {
    private DialogFragment concreteGameFragment = new ConcreteGameFragment();
    private DialogFragment concreteSportMap = new ConcreteSportMap();
    private DialogFragment addUser = new AddUser();
    private DialogFragment concreteGameChatFragment = new ConcreteGameChatFragment();
    private Context context;


    private ArrayList<ConcreteSportItem> items;
    BottomMenu bottomMenu;

    //Конструктор адаптера
    public ConcreteSportRecyclerAdapter(ArrayList<ConcreteSportItem> concreteSportItems, Context context) {
        this.context = context;
        if (context instanceof BottomMenu) {
            bottomMenu = (BottomMenu) context;
        }
        items = concreteSportItems;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //Подключаем разметку
    @Override
    public ConcreteSportRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.concrete_game_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ConcreteSportItem concreteSportItem = items.get(position);
        holder.sportKindView.setText(concreteSportItem.getSportKind());
        holder.quantityView.setText(concreteSportItem.getQuantity());
        holder.dateView.setText(concreteSportItem.getDate() + " " + concreteSportItem.getTime());
        final double locationLatitude = Double.parseDouble(concreteSportItem.getLocationLatitude());
        final double locationLongitude = Double.parseDouble(concreteSportItem.getLocationLongitude());
        final int id = concreteSportItem.getGameId();
        final String userLogin = concreteSportItem.getLogin();
        //Действия с элементами View
        holder.locationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Кладем параметры и запускаем Dialog
                Bundle argsForMap = new Bundle();
                argsForMap.putDouble("LOCATION_LATITUDE", locationLatitude);
                argsForMap.putDouble("LOCATION_LONGITUDE", locationLongitude);
                concreteSportMap.setArguments(argsForMap);
                bottomMenu.CallDialogFrag(concreteSportMap, "concreteSportMap");
            }
        });
        if (concreteSportItem.getVisibleButton() == 0) {
            holder.requestView.setVisibility(View.GONE);
        } else {
            holder.requestView.setVisibility(View.VISIBLE);
        }

        holder.chatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Кладем параметры и запускаем Dialog
                Bundle argsForChat = new Bundle();
                argsForChat.putString("USER_LOGIN", userLogin);
                argsForChat.putString("GAME_ID", String.valueOf(id));
                concreteGameChatFragment.setArguments(argsForChat);
                bottomMenu.CallDialogFrag(concreteGameChatFragment,"CHAT");
            }
        });

        holder.requestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Кладем параметры и запускаем Dialog
                Bundle argsForRequest = new Bundle();
                argsForRequest.putString("GAME_ID", String.valueOf(concreteSportItem.getGameId()));
                addUser.setArguments(argsForRequest);
                bottomMenu.CallDialogFrag(addUser, "ADD_USER");

            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Кладем параметры и запускаем Dialog
                Bundle args = new Bundle();
                args.putInt("ID", id);
                args.putString("USER_LOGIN", userLogin);
                concreteGameFragment.setArguments(args);
                bottomMenu.CallDialogFrag(concreteGameFragment, "CONCRETE_GAME_FRAGMENT");
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //Объявляем и инициализируем элементы View
        private View view;
        private TextView sportKindView, dateView, locationView, chatView, quantityView, requestView;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            sportKindView = (TextView) itemView.findViewById(R.id.sport_kind_view);
            dateView = (TextView) itemView.findViewById(R.id.date_view);
            locationView = (TextView) itemView.findViewById(R.id.location_view);
            chatView = (TextView) itemView.findViewById(R.id.chat_view);
            quantityView = (TextView) itemView.findViewById(R.id.quantity_view);
            requestView = (TextView) itemView.findViewById(R.id.request_view);
        }
    }

}