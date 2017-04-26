package com.michael.sportify.Games;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michael.sportify.R;

import java.util.ArrayList;

/**
 * Created by Michael on 09.05.16.
 */
public class GamesRecyclerView extends Fragment {

    private ArrayList<GamesRecyclerItem> gamesRecyclerItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.games_recycler, container, false);
        Bundle arguments = getArguments();
        String userLogin = arguments.getString("USER_LOGIN");
        //Чистим ArrayList(чтобы не было повторного отображения)
        gamesRecyclerItems.clear();

        //Устанавливаем адаптер, список и тд
        GamesRecyclerAdapter gamesRecyclerAdapter = new GamesRecyclerAdapter(gamesRecyclerItems, getActivity());
        RecyclerView gamesRecyclerView = (RecyclerView) view.findViewById(R.id.gridRecyclerView);
        gamesRecyclerView.setHasFixedSize(true);
        gamesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        gamesRecyclerView.setAdapter(gamesRecyclerAdapter);

        //Массив string для объектов RecyclerView
        String[] sportKind = {"Теннис", "Баскетбол", "Футбол", "Хоккей", "Бокс", "Легкая атлетика", "Велоспорт", "Плавание"};
        int[] imagesKind = {R.drawable.tennis, R.drawable.basketball, R.drawable.football, R.drawable.hockey, R.drawable.boxing, R.drawable.run, R.drawable.cycling, R.drawable.swimming};
        for (int i = 0; i < 8; i++) {
            GamesRecyclerItem gamesRecyclerItem = new GamesRecyclerItem();
            gamesRecyclerItem.setSportKind(sportKind[i]);
            gamesRecyclerItem.setImage(imagesKind[i]);
            gamesRecyclerItem.setLogin(userLogin);
            gamesRecyclerItems.add(gamesRecyclerItem);
        }

        gamesRecyclerAdapter.notifyDataSetChanged();

        return view;
    }

}
