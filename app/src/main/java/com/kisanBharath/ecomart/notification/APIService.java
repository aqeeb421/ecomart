package com.kisanBharath.ecomart.notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAWtMkz7M:APA91bHKrb2VkH3Kcl6tN8Krh5eTnhGBYPiwkBmiU8dtFOyRWR1DTCMaRJnl5TALhmw0KptD6OW6Cc8U6zbCzSnEnt20uwT7BMyQPxQ7iapOcfqJIsYBV4gjFBx7SWZg1aQRpw3oxhHf",
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender sender);


}
