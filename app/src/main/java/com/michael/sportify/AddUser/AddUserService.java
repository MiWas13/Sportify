package com.michael.sportify.AddUser;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.NameValuePair;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class AddUserService extends Service {
    private String userLogin;
    private String gameId;

    public AddUserService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        final Notification notification = new Notification.Builder(this).build();

        startForeground(1, notification);

        Bundle bundle = intent.getExtras();
        userLogin = bundle.getString("USER_LOGIN");
        gameId = bundle.getString("GAME_ID");

        //Запрос на добавление игры в БД к API
        new Thread(new Runnable() {
            private String response;

            @Override
            public void run() {
                Log.d("MY_LOG_SERVICE", "LoadStart");
                try {
                    //Создаем запрос на сервер
                    DefaultHttpClient hc = new DefaultHttpClient();
                    ResponseHandler<String> res = new BasicResponseHandler();
                    HttpPost postMethod = new HttpPost("http://wasser7i.bget.ru/AddUserToGame.php");

                    //Будем передавать два параметра
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                    //Передаем параметры
                    nameValuePairs.add(new BasicNameValuePair("id_game", gameId));
                    nameValuePairs.add(new BasicNameValuePair("user_login", userLogin));

                    //Собираем вместе и отправляем
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //Получаем ответ от сервера
                    response = hc.execute(postMethod, res);
                } catch (Exception e) {
                    System.out.println("Ошибка соединения с сервером");
                }
                Log.d("MY_LOG_SERVICE", "LoadStop");
                //Убиваем сервис после получения результата
                stopForeground(true);
                stopSelf();
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
