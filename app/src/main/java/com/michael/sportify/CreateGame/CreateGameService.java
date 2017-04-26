package com.michael.sportify.CreateGame;

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
import java.util.Objects;

public class CreateGameService extends Service {
    public CreateGameService() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Notification notification = new Notification.Builder(this).build();

        startForeground(1, notification);

        //Получаем параметры из Intent
        Bundle arguments = intent.getExtras();
        final String creator = arguments.getString("CREATOR");
        final String sportKind = arguments.getString("SPORT_KIND");
        final String description = arguments.getString("DESCRIPTION");
        final String maxQuantity = arguments.getString("MAX_QUANTITY");
        final String equipmentComment = arguments.getString("EQUIPMENT_COMMENT");
        final String equipmentNeed = arguments.getString("EQUIPMENT_NEED");
        final String locationLatitude = arguments.getString("LOCATION_LATITUDE");
        final String locationLongitude = arguments.getString("LOCATION_LONGITUDE");
        final String time = arguments.getString("TIME");
        final Messenger messenger = (Messenger) arguments.get("MSG");

        //Запрос на добавление игры в БД к API
        new Thread(new Runnable() {
            private String response;

            @Override
            public void run() {
                try {
                    //Создаем запрос на сервер
                    DefaultHttpClient hc = new DefaultHttpClient();
                    ResponseHandler<String> res = new BasicResponseHandler();
                    HttpPost postMethod = new HttpPost("http://wasser7i.bget.ru/CreateGame.php");

                    //Будем передавать два параметра
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                    //Передаем параметры
                    nameValuePairs.add(new BasicNameValuePair("creator", creator));
                    nameValuePairs.add(new BasicNameValuePair("sport_kind", sportKind));
                    nameValuePairs.add(new BasicNameValuePair("description", description));
                    nameValuePairs.add(new BasicNameValuePair("max_quantity", maxQuantity));
                    nameValuePairs.add(new BasicNameValuePair("equipment_comment", equipmentComment));
                    nameValuePairs.add(new BasicNameValuePair("equipment_need", equipmentNeed));
                    nameValuePairs.add(new BasicNameValuePair("location_latitude", locationLatitude));
                    nameValuePairs.add(new BasicNameValuePair("location_longitude", locationLongitude));
                    nameValuePairs.add(new BasicNameValuePair("time", time));

                    //Собираем вместе и отправляем
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                    //Получаем ответ от сервера
                    response = hc.execute(postMethod, res);
                    Log.d("RESPONSE_IN_REG", String.valueOf(response));
                    Message message = Message.obtain();
                    if (Objects.equals(response, "Connection is done Added")) {
                        message.arg1 = 1;
                    } else {
                        message.arg1 = 2;
                    }
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
