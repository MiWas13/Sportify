package com.michael.sportify.AddUser;

import android.app.DialogFragment;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.michael.sportify.BottomMenu;
import com.michael.sportify.R;

import java.util.ArrayList;

/**
 * Created by Michael on 18.05.16.
 */
public class AddUserRecyclerAdapter extends RecyclerView.Adapter<AddUserRecyclerAdapter.MyViewHolderAdd> {

    private Context context;
    private DialogFragment profileAddUserFragment = new ProfileAddUserFragment();
    private ArrayList<AddUserItem> items;
    private BottomMenu bottomMenu;

    //Конструктор адаптера
    public AddUserRecyclerAdapter(ArrayList<AddUserItem> addUserItems, Context context) {
        if (context instanceof BottomMenu) {
            bottomMenu = (BottomMenu) context;
        }

        this.context = context;
        items = addUserItems;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    //Подключаем разметку
    @Override
    public AddUserRecyclerAdapter.MyViewHolderAdd onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_user_to_game, parent, false);

        return new MyViewHolderAdd(v);
    }

    @Override
    public void onBindViewHolder(final AddUserRecyclerAdapter.MyViewHolderAdd holder, final int position) {
        final AddUserItem addUserItem = items.get(position);
        holder.userNameView.setText(addUserItem.getUserName());
        Log.d("USER_LOGIN", String.valueOf(addUserItem.getLogin()));
        Bitmap bitmapAvatar = decodeBase64(addUserItem.getAvatar());
        //Действия с элементами View
        holder.avatar.setImageBitmap(bitmapAvatar);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //В DialogFragment кладем параметры
                Bundle argsForProfile = new Bundle();
                argsForProfile.putString("USER_LOGIN", addUserItem.getLogin());
                profileAddUserFragment.setArguments(argsForProfile);
                bottomMenu.CallDialogFrag(profileAddUserFragment, "PROFILE_ADD");
            }
        });

        holder.addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.remove(position);
                notifyDataSetChanged();
                bottomMenu.StartServiceAddUser(addUserItem.getLogin(), addUserItem.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class MyViewHolderAdd extends RecyclerView.ViewHolder {

        //Объявляем и инициализируем элементы View
        private View view;
        private TextView userNameView;
        private ImageView avatar;
        private FloatingActionButton addUserButton;

        public MyViewHolderAdd(View itemView) {
            super(itemView);
            view = itemView;
            userNameView = (TextView) itemView.findViewById(R.id.user_name_add);
            avatar = (ImageView) itemView.findViewById(R.id.avatar_add);
            addUserButton = (FloatingActionButton) view.findViewById(R.id.fab_add);

        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
