package com.michael.sportify.Profile;

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
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

public class ProfileInfoService extends Service {
    public ProfileInfoService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Notification notification = new Notification.Builder(this).build();

        startForeground(1, notification);

        //Получаем параметры из Intent
        Bundle arguments = intent.getExtras();
        final String userLogin = arguments.getString("USER_LOGIN");
        final Messenger messenger = (Messenger) arguments.get("MSG");

        //Thread для выполнения запроса к серверу
        new Thread(new Runnable() {
            private String response;

            @Override
            public void run() {
                try {
                    //Создаем запрос на сервер
                    DefaultHttpClient hc = new DefaultHttpClient();
                    ResponseHandler<String> res = new BasicResponseHandler();
                    HttpPost postMethod = new HttpPost("http://wasser7i.bget.ru/ProfilePost.php");
                    //Будем передавать два параметра
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                    //Передаем параметры
                    nameValuePairs.add(new BasicNameValuePair("login", userLogin));

                    //Собираем вместе и отправляем
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                    //Получаем ответ от сервера
                    response = hc.execute(postMethod, res);
                    response = "{\"profile\":" + response + "}";
                    Log.d("RESPONSE_IN_REG", String.valueOf(response));
                    Message message = Message.obtain();
                    message.obj = response;
                    Log.d("MESSAGE", String.valueOf(message.obj));
                    try {
                        assert messenger != null;
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    //Убиваем сервис после получения результата
                    stopForeground(true);
                    stopSelf();
                } catch (Exception e) {

                }
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
