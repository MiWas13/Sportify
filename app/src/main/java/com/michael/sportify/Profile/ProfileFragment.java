package com.michael.sportify.Profile;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.michael.sportify.ExitFragment;
import com.michael.sportify.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

/**
 * Created by Michael on 04.05.16.
 */
public class ProfileFragment extends Fragment {
    private DialogFragment fragmentProfileEdit;
    private String userLogin;
    private TextView profileGamesPlayed, profileGamesMade, profileName, profileMainInfo, profileAboutUser, profileFavouriteGames;
    private ImageView profileAvatar;
    private ProgressDialog progressDialog;
    public ProfileItem profileItem = new ProfileItem();
    private MyUpdateAvatarHandler myUpdateAvatarHandler;
    static final int GALLERY_REQUEST = 1;

    public static ProfileFragment newInstance(String userLogin, String resultJSON) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle b = new Bundle();
        b.putString("USER_LOGIN", userLogin);
        b.putString("RESULT", resultJSON);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile, container, false);
        myUpdateAvatarHandler = new MyUpdateAvatarHandler();
        MyProfileInfoHandler myProfileInfoHandler = new MyProfileInfoHandler();
        userLogin = getArguments().getString("USER_LOGIN");
        profileGamesPlayed = (TextView) v.findViewById(R.id.games_played);
        profileGamesMade = (TextView) v.findViewById(R.id.games_made);
        profileName = (TextView) v.findViewById(R.id.name);
        profileMainInfo = (TextView) v.findViewById(R.id.mainInfo);
        profileAvatar = (ImageView) v.findViewById(R.id.avatar);
        TextView profileEdit = (TextView) v.findViewById(R.id.editProfile);
        Button exitBtn = (Button) v.findViewById(R.id.exit_btn);
        profileAboutUser = (TextView) v.findViewById(R.id.about_user);
        profileFavouriteGames = (TextView) v.findViewById(R.id.favourite_games);
        fragmentProfileEdit = new ProfileEdit();
        Intent intent = new Intent(getActivity(), ProfileInfoService.class);
        intent.putExtra("USER_LOGIN", userLogin);
        intent.putExtra("MSG", new Messenger(myProfileInfoHandler));
        this.getActivity().startService(intent);

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment exitDialog = new ExitFragment();
                exitDialog.show(getFragmentManager(), "EXIT_DIALOG");
            }
        });
        profileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentProfileEdit.show(getFragmentManager(), "registrationEdit");
                Bundle args = new Bundle();
                args.putString("LOGIN", userLogin);
                fragmentProfileEdit.setArguments(args);
            }
        });

        if (!hasConnection(inflater.getContext())) {
            Toast.makeText(inflater.getContext(), "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show();
        }
        return v;
    }

    //Метод загрузки изображения из галлереи и подгрузка его на сервер
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap bitmap = null;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == -1) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (byteSizeOf(bitmap) >= 10000000) {
                        Toast.makeText(getActivity(), "Большой размер фотографии", Toast.LENGTH_LONG).show();
                    } else {
                        profileAvatar.setImageBitmap(bitmap);
                        profileItem.setAvatar(encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100));
                        Log.d("AVATAR", profileItem.getAvatar());
                        Intent intent = new Intent(getActivity(), UpdateImageService.class);
                        intent.putExtra("USER_LOGIN", userLogin);
                        intent.putExtra("IMAGE", profileItem.getAvatar());
                        intent.putExtra("MSG", new Messenger(myUpdateAvatarHandler));
                        this.getActivity().startService(intent);
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Загрузка");
                        progressDialog.show();
                    }
                }
        }
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

    //Кодирование и декодирование изображений в Base64
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
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

    //Проверяем размер Битмапа
    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public class MyUpdateAvatarHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            if (msg.arg1 == 1) {
                Toast.makeText(getActivity(), "Фотография обновлена", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Ошибка на сервере!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class MyProfileInfoHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
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
                if (!(Objects.equals(profileInfoJSONObject.getString("birthdate"), "0000-00-00"))) {
                    profileItem.setBirthDate(profileInfoJSONObject.getString("birthdate"));
                    profileItem.setYear(profileItem.getBirthDate().substring(0, 4));
                    profileItem.setMonth(profileItem.getBirthDate().substring(5, 7));
                    profileItem.setDay(profileItem.getBirthDate().substring(8, 10));
                    profileItem.setAge((calculateAge(profileItem.getYear(), profileItem.getMonth(), profileItem.getDay())));
                } else {
                    profileItem.setAge(0);
                }
                profileItem.setGameMade(profileInfoJSONObject.getInt("game_made"));
                profileItem.setGamePlayed(profileInfoJSONObject.getInt("game_played"));
                profileItem.setSex(profileInfoJSONObject.getString("sex"));
                profileItem.setAbout(profileInfoJSONObject.getString("about_user"));
                profileItem.setFavouriteGames(profileInfoJSONObject.getString("favourite_games"));
            } catch (Exception e) {
                System.out.print("Ошибка в JSON-ответе");
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
            if (!((profileItem.getAge()) == 0)) {
                profileMainInfo.setText(sex + ", " + String.valueOf(profileItem.getAge()));
            } else {
                profileMainInfo.setText(sex);
            }

        }
    }

}
