package com.michael.sportify.AddUser;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.michael.sportify.Profile.ProfileInfoService;
import com.michael.sportify.Profile.ProfileItem;
import com.michael.sportify.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

import java.util.Objects;

/**
 * Created by Michael on 18.05.16.
 */
public class ProfileAddUserFragment extends DialogFragment {
    private TextView profileGamesPlayed, profileGamesMade, profileName, profileMainInfo, profileAboutUser, profileFavouriteGames;
    private ImageView profileAvatar;
    private ProfileItem profileItem = new ProfileItem();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, container, false);
        String userLogin = getArguments().getString("USER_LOGIN");
        Log.d("USER_LOGIN", userLogin);
        getDialog().setTitle("Профиль пользователя");
        MyProfileAddUserHandler myProfileAddUserHandler = new MyProfileAddUserHandler();
        profileGamesPlayed = (TextView) v.findViewById(R.id.games_played);
        profileGamesMade = (TextView) v.findViewById(R.id.games_made);
        profileName = (TextView) v.findViewById(R.id.name);
        profileMainInfo = (TextView) v.findViewById(R.id.mainInfo);
        profileAvatar = (ImageView) v.findViewById(R.id.avatar);
        TextView profileEdit = (TextView) v.findViewById(R.id.editProfile);
        profileAboutUser = (TextView) v.findViewById(R.id.about_user);
        profileFavouriteGames = (TextView) v.findViewById(R.id.favourite_games);
        Intent intent = new Intent(getActivity(), ProfileInfoService.class);
        intent.putExtra("USER_LOGIN", userLogin);
        intent.putExtra("MSG", new Messenger(myProfileAddUserHandler));
        this.getActivity().startService(intent);
        profileEdit.setVisibility(View.INVISIBLE);

        if (!hasConnection(inflater.getContext())) {
            Toast.makeText(inflater.getContext(), "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show();
        }
        return v;
    }

    public static Integer calculateAge(String year, String month, String day) {
        Calendar today = Calendar.getInstance();
        Calendar birthdate = GregorianCalendar.getInstance();
        birthdate.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

        // Включаем дату рождения
        birthdate.add(Calendar.DAY_OF_MONTH, -1);
        int age = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= birthdate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    //Метод проверки подключения пользователя к интернету
    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public class MyProfileAddUserHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //progressDialog.dismiss();
            JSONObject dataJsonObj;
            JSONArray profileInfo;
            String sex;
            try {
                dataJsonObj = new JSONObject(String.valueOf(msg.obj));
                profileInfo = dataJsonObj.getJSONArray("profile");
                JSONObject profileInfoJSONObject = profileInfo.getJSONObject(0);
                profileItem.setFirsName(profileInfoJSONObject.getString("firstname"));
                profileItem.setSecondName(profileInfoJSONObject.getString("secondname"));
                profileItem.setAvatar(profileInfoJSONObject.getString("image"));
                profileItem.setBirthDate(profileInfoJSONObject.getString("birthdate"));
                profileItem.setYear(profileItem.getBirthDate().substring(0, 4));
                profileItem.setMonth(profileItem.getBirthDate().substring(5, 7));
                profileItem.setDay(profileItem.getBirthDate().substring(8, 10));
                profileItem.setGameMade(profileInfoJSONObject.getInt("game_made"));
                profileItem.setGamePlayed(profileInfoJSONObject.getInt("game_played"));
                profileItem.setSex(profileInfoJSONObject.getString("sex"));
                profileItem.setAbout(profileInfoJSONObject.getString("about_user"));
                profileItem.setFavouriteGames(profileInfoJSONObject.getString("favourite_games"));
                profileItem.setAge((calculateAge(profileItem.getYear(), profileItem.getMonth(), profileItem.getDay())));
            } catch (Exception e) {
                System.out.print("Ошибка в JSON-ответе от сервера");
            }

            profileGamesPlayed.setText(String.valueOf(profileItem.getGamePlayed()));
            profileGamesMade.setText(String.valueOf(profileItem.getGameMade()));
            profileName.setText(profileItem.getFirsName() + " " + profileItem.getSecondName());
            profileAvatar.setImageBitmap(decodeBase64(profileItem.getAvatar()));
            profileAboutUser.setText(profileItem.getAbout());
            profileFavouriteGames.setText(profileItem.getFavouriteGames());

            if (Objects.equals(profileItem.getSex(), "1")) {
                sex = "Мужчина";
            } else {
                sex = "Женщина";
            }
            profileMainInfo.setText(sex + ", " + String.valueOf(profileItem.getAge()));

        }
    }

}
