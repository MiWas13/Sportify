package com.michael.sportify;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Michael on 11.05.16.
 */
public class ExitFragment extends DialogFragment {
    TextView exitYes, exitNo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.exit_fragment, container, false);
        //Получаем действие пользователя: либо выход, либо остаемся в приложении
        final String APP_PREFERENCES = "MY_PREF";
        getDialog().setTitle("Выход");
        exitYes = (TextView) view.findViewById(R.id.exit_yes);
        exitNo = (TextView) view.findViewById(R.id.exit_no);
        exitYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sPrefAuth = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sPrefAuth.edit();
                ed.putBoolean("STATE", false);
                ed.commit();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        exitNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}
