package com.openxu.core.http;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {


    /*
     * retrofit不支持二次泛型 https://github.com/square/retrofit/issues/2012
     * 报错Method return type must not include a type variable or wildcard: retrofit2.Call<com.fpc.net.NetResponse<T>>
     */
//    @GET
//    Call<NetResponse<T>> doGet(@Url String url, @QueryMap Map<String, String> map);


    /*
     * http://open.iciba.com/dsapi/?date=2019-02-15
     * @Query 用于@GET方法的查询参数 key-value
     */
    @GET
    Call<ResponseBody> doGet(@Url String url, @QueryMap Map<String, String> map);

    @GET
    Observable<ResponseBody> rxGet(@Url String url, @QueryMap Map<String, String> map);

    @GET
    Observable<ResponseBody> rxGetAllPath(@Url String url, @QueryMap Map<String, String> map);

    /**
     *          MediaType textType = MediaType.parse("text/plain");
     *         RequestBody name = RequestBody.create(textType, "Carson");
     *         RequestBody age = RequestBody.create(textType, "24");
     *         RequestBody file = RequestBody.create(MediaType.parse("application/octet-stream"), "这里是模拟文件的内容");
     *         MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "test.txt", file);
     *
     *         Map<String, RequestBody> map = new HashMap<>();
     *         map.put("name", name);
     *         map.put("age", age);
     *         rxPost("", map, filePart);
     */
//    @FormUrlEncoded   //表示发送form-encoded的数据，每个键值对需要用@Filed来注解键名，随后的对象需要提供值
    @Multipart        //告诉Retrofit这个Service采用了multipart/form-data的请求方式，请求头中会添加Content-Type:multipart/form-data;
    @POST
    Observable<ResponseBody> rxPost(@Url String url, @PartMap Map<String, RequestBody> params/*, @Part MultipartBody.Part[] file*/);
//    Observable<ResponseBody> rxPost(@Url String url, @FieldMap Map<String, String> map, @PartMap Map<String, RequestBody> files);
//    @FormUrlEncoded   //表示发送form-encoded的数据，每个键值对需要用@Filed来注解键名，随后的对象需要提供值

//    @Headers({"Content-Type:application/json", "Accept:application/json"})
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST
    Observable<ResponseBody> rxPost(@Url String url, @Body Object obj);
//    Observable<ResponseBody> rxPost(@Url String url, @Body RequestBody jsonParam, @Body Object... objects);

    /*文件上传*/
    @Multipart
    @POST
    Observable<ResponseBody> rxFileUpload(@Url String url, @Part MultipartBody.Part... file);

    /*文件下载*/
    @Streaming
    @GET
    Observable<ResponseBody> rxDownload(@Url String url, @QueryMap Map<String, String> map);
}
