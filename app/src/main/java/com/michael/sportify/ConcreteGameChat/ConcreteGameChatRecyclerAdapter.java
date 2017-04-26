package com.michael.sportify.ConcreteGameChat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.michael.sportify.BottomMenu;
import com.michael.sportify.R;

import java.util.ArrayList;

/**
 * Created by Michael on 04.06.16.
 */
public class ConcreteGameChatRecyclerAdapter extends RecyclerView.Adapter<ConcreteGameChatRecyclerAdapter.MyViewHolder> {

    private ArrayList<ConcreteGameChatItem> items;

    //Конструктор адаптера
    public ConcreteGameChatRecyclerAdapter(ArrayList<ConcreteGameChatItem> concreteGameChatItems, Context context) {
        items = concreteGameChatItems;
        items.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //Подключаем разметку
    @Override
    public ConcreteGameChatRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.concrete_chat_item, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ConcreteGameChatItem concreteGameChatItem = items.get(position);
        //Действия с элементами View
        if (concreteGameChatItem.getState() == 0) {
            holder.nameView.setGravity(Gravity.START);
            holder.msgTextView.setGravity(Gravity.START);
            holder.dateView.setGravity(Gravity.START);
        } else {
            holder.nameView.setGravity(Gravity.END);
            holder.msgTextView.setGravity(Gravity.END);
            holder.dateView.setGravity(Gravity.END);
        }
        holder.nameView.setText(concreteGameChatItem.getUserName());
        holder.msgTextView.setText(concreteGameChatItem.getMsgText());
        holder.dateView.setText(concreteGameChatItem.getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //Объявляем и инициализируем элементы View
        private View view;
        private TextView nameView, msgTextView, dateView;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            nameView = (TextView) itemView.findViewById(R.id.user_name_chat_view);
            msgTextView = (TextView) itemView.findViewById(R.id.msg_text_chat_view);
            dateView = (TextView) itemView.findViewById(R.id.date_chat_view);
        }
    }
}