package com.michael.sportify;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


import com.michael.sportify.AddUser.AddUserService;

import com.michael.sportify.CreateGame.CreateGameFragment;
import com.michael.sportify.Games.GamesRecyclerView;
import com.michael.sportify.MyGames.MyGamesFragment;
import com.michael.sportify.Profile.ProfileFragment;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

/**
 * Created by Michael on 04.05.16.
 */
public class BottomMenu extends AppCompatActivity {
    private BottomBar mBottomBar;
    private FragmentManager fragmentManager = getFragmentManager();
    private GamesRecyclerView gamesRecyclerView = new GamesRecyclerView();
    private String userLogin;
    private String resultJSON;
    private ProfileFragment profileFragment = new ProfileFragment();
    private MyGamesFragment myGamesFragment = new MyGamesFragment();
    private DialogFragment createGame = new CreateGameFragment();
    private FloatingActionButton fab;
    private static final String APP_PREFERENCES = "MY_PREF";
    private SharedPreferences sPrefAuth;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_app);
        if (!loadState()) {
            loadResult();
        }
        //Получаем параметры из SharedPreference
        userLogin = loadLogin(userLogin);
        resultJSON = loadResultJSON(resultJSON);
        mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.myCoordinator),
                findViewById(R.id.myScrollingContent), savedInstanceState);
        mBottomBar.noTopOffset();

        fab = (FloatingActionButton) findViewById(R.id.fab_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("USER_LOGIN", userLogin);
                createGame.setArguments(args);
                createGame.show(getFragmentManager(), "CREATE_GAME");
            }
        });

        //Меняем содержимое контейнера fragment_container по нажатию на элементы меню
        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (menuItemId == R.id.bb_menu_games) {
                    //Передаем параметры, меняем фрагмент с анимацией
                    Bundle args = new Bundle();
                    args.putString("USER_LOGIN", userLogin);
                    gamesRecyclerView.setArguments(args);
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(0, R.anim.fade_out, 0, 0)
                            .replace(R.id.fragment_container, gamesRecyclerView)
                            .commit();
                    fab.setVisibility(View.VISIBLE);
                }

                if (menuItemId == R.id.bb_menu_my_games) {
                    //Передаем параметры, меняем фрагмент с анимацией
                    myGamesFragment = MyGamesFragment.newInstance(userLogin, resultJSON);
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, 0, 0)
                            .replace(R.id.fragment_container, myGamesFragment)
                            .commit();
                    fab.setVisibility(View.VISIBLE);
                }

                if (menuItemId == R.id.bb_menu_profile) {
                    //Передаем параметры, меняем фрагмент с анимацией
                    profileFragment = ProfileFragment.newInstance(userLogin, resultJSON);
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, 0, 0)
                            .replace(R.id.fragment_container, profileFragment)
                            .commit();
                    fab.setVisibility(View.GONE);
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
            }
        });

        //Выставляем цвета для вкладок
        mBottomBar.mapColorForTab(0, "#FFFFFF");
        mBottomBar.mapColorForTab(1, "#FFFFFF");
        mBottomBar.mapColorForTab(2, "#FFFFFF");
        mBottomBar.setActiveTabColor("#000000");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }


    //Метод для запуска сервиса добавления пользователя в игру
    public void StartServiceAddUser(String userLogin, String gameId) {
        Intent intent = new Intent(BottomMenu.this, AddUserService.class);
        intent.putExtra("USER_LOGIN", userLogin);
        intent.putExtra("GAME_ID", gameId);
        startService(intent);
    }

    //Метод, проверяющий входил ли пользователь в свой аккаунт
    public void loadResult() {
        sPrefAuth = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String savedText = sPrefAuth.getString("MY_PREF", "");
        String userLogin = sPrefAuth.getString("USER_LOGIN", "");
        if (!(savedText == null)) {
            Intent intent = new Intent(BottomMenu.this, MainActivity.class);
            intent.putExtra("RESULT", savedText);
            intent.putExtra("USER_LOGIN", userLogin);
            startActivity(intent);
            finish();
        }
    }

    //Загружаем логин из SharedPreference
    public String loadLogin(String userLogin) {
        sPrefAuth = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        userLogin = sPrefAuth.getString("USER_LOGIN", "");
        return userLogin;
    }

    //Загружаем JSON из SharedPreference
    public String loadResultJSON(String resultJSON) {
        sPrefAuth = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        resultJSON = sPrefAuth.getString("MY_PREF", "");
        return resultJSON;
    }

    public Boolean loadState() {
        sPrefAuth = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Boolean state = sPrefAuth.getBoolean("STATE", false);
        if (state) {
            return true;
        } else {
            return false;
        }
    }


    //Методы для вызова фрагментов(Используются в следующих фрагментов)
    public void CallFrag(Fragment fragment) {
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void CallDialogFrag(DialogFragment dialogFragment, String s) {
        dialogFragment.show(getFragmentManager(), s);
    }
}