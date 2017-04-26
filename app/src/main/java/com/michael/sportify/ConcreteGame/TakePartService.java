package com.michael.sportify.ConcreteGame;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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

public class TakePartService extends Service {
    public TakePartService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        final Notification notification = new Notification.Builder(this).build();

        startForeground(1, notification);

        Bundle bundle = intent.getExtras();
        final String gameId = bundle.getString("GAME_ID");
        final String userLogin = bundle.getString("USER_LOGIN");
        final Messenger messenger = (Messenger) bundle.get("MSG");

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
                    HttpPost postMethod = new HttpPost("http://wasser7i.bget.ru/NewRequestToGame.php");

                    //Будем передавать два параметра
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                    //Передаем параметры
                    nameValuePairs.add(new BasicNameValuePair("id_game", String.valueOf(gameId)));
                    nameValuePairs.add(new BasicNameValuePair("user_login", String.valueOf(userLogin)));

                    //Собираем вместе и отправляем
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //Получаем ответ от сервера
                    response = hc.execute(postMethod, res);
                    Message message = Message.obtain();
                    message.arg1 = 1;
                    try {
                        assert messenger != null;
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
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
