package app.greentech.Utils;

import app.greentech.Models.ServerRequest;
import app.greentech.Models.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Cyril on 4/16/2016.
 */

public interface RequestInterface {

    @POST("server/")
    Call<ServerResponse> operation(@Body ServerRequest request);

}
