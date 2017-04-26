package com.michael.sportify.MyGames;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.michael.sportify.ConcreteSportKind.ConcreteSportFragment;
import com.michael.sportify.R;


/**
 * Created by Michael on 12.05.16.
 */
public class MyGamesFragment extends Fragment {
    private ConcreteSportFragment concreteSportFragment;
    private String userLogin;

    public static MyGamesFragment newInstance(String userLogin, String resultJSON) {
        MyGamesFragment fragment = new MyGamesFragment();
        Bundle b = new Bundle();
        b.putString("USER_LOGIN", userLogin);
        b.putString("RESULT", resultJSON);
        fragment.setArguments(b);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mygames_fragment, container, false);
        final FragmentManager fragmentManager = getFragmentManager();

        userLogin = getArguments().getString("USER_LOGIN");

        Log.d("LOGIN_IN_MYGAMES", userLogin);

        TabHost tabHost = (TabHost) view.findViewById(android.R.id.tabhost);

        //Инициализация
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        //Создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec("1");
        //Название вкладки
        tabSpec.setIndicator("Участник");
        //Указываем id компонента из FrameLayout
        tabSpec.setContent(R.id.tvTab1);
        //Добавляем в корневой элемент
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("2");
        tabSpec.setIndicator("Создатель");
        tabSpec.setContent(R.id.tvTab2);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("3");
        tabSpec.setIndicator("Ожидание");
        tabSpec.setContent(R.id.tvTab3);
        tabHost.addTab(tabSpec);

        //Вкладка будет выбрана по умолчанию
        tabHost.setCurrentTabByTag("1");

        final Bundle args = new Bundle();
        args.putInt("POSITION", 0);
        args.putInt("REQUEST_KIND", 1);
        args.putString("USER_LOGIN", userLogin);
        concreteSportFragment = new ConcreteSportFragment();
        concreteSportFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(android.R.id.tabcontent, concreteSportFragment)
                .commit();

        //Обработчик переключения вкладок
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "1":
                        concreteSportFragment = new ConcreteSportFragment();
                        args.putInt("POSITION", 0);
                        args.putInt("REQUEST_KIND", 1);
                        args.putString("USER_LOGIN", userLogin);
                        concreteSportFragment.setArguments(args);
                        fragmentManager.beginTransaction()
                                .replace(android.R.id.tabcontent, concreteSportFragment)
                                .commit();
                        break;

                    case "2":
                        concreteSportFragment = new ConcreteSportFragment();
                        args.putInt("POSITION", 0);
                        args.putInt("REQUEST_KIND", 2);
                        args.putString("USER_LOGIN", userLogin);
                        concreteSportFragment.setArguments(args);
                        fragmentManager.beginTransaction()
                                .replace(android.R.id.tabcontent, concreteSportFragment)
                                .commit();
                        break;

                    case "3":
                        concreteSportFragment = new ConcreteSportFragment();
                        args.putInt("POSITION", 0);
                        args.putInt("REQUEST_KIND", 3);
                        args.putString("USER_LOGIN", userLogin);
                        concreteSportFragment.setArguments(args);
                        fragmentManager.beginTransaction()
                                .replace(android.R.id.tabcontent, concreteSportFragment)
                                .commit();
                        break;

                }
            }
        });

        return view;
    }


}

