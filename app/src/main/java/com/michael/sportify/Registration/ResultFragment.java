package com.michael.sportify.Registration;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.michael.sportify.R;



public class ResultFragment extends DialogFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.complited_fragment, null);
        Button finishAction = (Button) v.findViewById(R.id.finish_action);
        boolean result = getArguments().getBoolean("RESULT");
        Log.d("RESFRAG", String.valueOf(result));
        if (result) {
            getDialog().setTitle("Готово!");
        } else {
            getDialog().setTitle("Ошибочка...");
            finishAction.setText("Попробуем позже");
        }
        finishAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

}
