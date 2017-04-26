package com.michael.sportify.Games;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import com.michael.sportify.BottomMenu;

import com.michael.sportify.ConcreteSportKind.ConcreteSportFragment;

import com.michael.sportify.R;

import java.util.ArrayList;


/**
 * Created by Michael on 08.05.16.
 */
public class GamesRecyclerAdapter extends RecyclerView.Adapter<GamesRecyclerAdapter.MyViewHolder> {
    private ConcreteSportFragment concreteSportFragment = new ConcreteSportFragment();
    private Context context;
    private ArrayList<GamesRecyclerItem> items;
    private BottomMenu bottomMenu;

    //Конструктор адаптера
    public GamesRecyclerAdapter(ArrayList<GamesRecyclerItem> gamesRecyclerItems, Context context) {
        this.context = context;
        if (context instanceof BottomMenu) {
            bottomMenu = (BottomMenu) context;
        }
        items = gamesRecyclerItems;
        items.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public GamesRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.games_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final GamesRecyclerItem gamesRecyclerItem = items.get(position);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("POSITION", position);
                args.putInt("REQUEST_KIND", 0);
                args.putString("USER_LOGIN", gamesRecyclerItem.getLogin());
                concreteSportFragment.setArguments(args);
                bottomMenu.CallFrag(concreteSportFragment);
            }
        });
        holder.sportKindView.setText(gamesRecyclerItem.getSportKind());
        holder.sportKindImage.setImageResource(gamesRecyclerItem.getImage());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //Объявляем и инициализируем элементы View
        private View view;
        private TextView sportKindView;
        private ImageView sportKindImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            sportKindView = (TextView)itemView.findViewById(R.id.games_sport_kind);
            sportKindImage = (ImageView)itemView.findViewById(R.id.sport_kind_image);

        }
    }

}
