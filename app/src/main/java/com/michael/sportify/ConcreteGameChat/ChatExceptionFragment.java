package com.michael.sportify.ConcreteGameChat;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.michael.sportify.R;

/**
 * Created by Michael on 05.06.16.
 */
public class ChatExceptionFragment extends DialogFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_exception_fragment, null);
        getDialog().setTitle("Ошибка доступа!");
        return v;
    }
}
