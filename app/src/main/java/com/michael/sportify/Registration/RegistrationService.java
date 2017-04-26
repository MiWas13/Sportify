package com.michael.sportify.Registration;

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

public class RegistrationService extends Service {
    public RegistrationService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final Notification notification = new Notification.Builder(this).build();

        startForeground(1, notification);

        //Получаем параметры из Intent
        Bundle arguments = intent.getExtras();
        final String email = arguments.getString("EMAIL");
        final String password = arguments.getString("PASSWORD");
        final String firstName = arguments.getString("FIRST_NAME");
        final String secondName = arguments.getString("SECOND_NAME");
        final String image = arguments.getString("IMAGE");
        final String birthDate = arguments.getString("BIRTH_DATE");
        final String sex = arguments.getString("SEX");
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
                    HttpPost postMethod = new HttpPost("http://wasser7i.bget.ru/RegistrationPost.php");
                    //Будем передавать два параметра
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

                    //Передаем параметры
                    nameValuePairs.add(new BasicNameValuePair("email", email));
                    nameValuePairs.add(new BasicNameValuePair("password", password));
                    nameValuePairs.add(new BasicNameValuePair("firstname", firstName));
                    nameValuePairs.add(new BasicNameValuePair("secondname", secondName));
                    nameValuePairs.add(new BasicNameValuePair("image", image));
                    nameValuePairs.add(new BasicNameValuePair("birthdate", birthDate));
                    nameValuePairs.add(new BasicNameValuePair("sex", sex));

                    //Собираем вместе и отправляем
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                    //Получаем ответ от сервера
                    response = hc.execute(postMethod, res);
                    Log.d("RESPONSE_IN_REG", String.valueOf(response));
                    Message message = Message.obtain();
                    if (Objects.equals(response, "Connection is done Good Added")) {
                        message.arg1 = 1;
                    } else if (Objects.equals(response, "Connection is done Bad Problem")) {
                        message.arg1 = 2;
                    } else {
                        message.arg1 = 3;
                    }
                    try {
                        assert messenger != null;
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
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
