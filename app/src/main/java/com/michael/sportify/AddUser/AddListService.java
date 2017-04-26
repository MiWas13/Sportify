package com.michael.sportify.AddUser;

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

public class AddListService extends Service {
    public AddListService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        final Notification notification = new Notification.Builder(this).build();

        startForeground(1, notification);

        Bundle bundle = intent.getExtras();
        final String id = bundle.getString("GAME_ID");
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
                    HttpPost postMethod = new HttpPost("http://wasser7i.bget.ru/AddUserRequest.php");

                    //Будем передавать два параметра
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                    //Передаем параметры
                    nameValuePairs.add(new BasicNameValuePair("id", id));

                    //Собираем вместе и отправляем
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    //Получаем ответ от сервера
                    response = hc.execute(postMethod, res);
                    response = "{\"users\":" + response + "}";
                    Message message = Message.obtain();
                    message.obj = response;
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
