package com.openxu.core.http;

import androidx.annotation.IntDef;
import com.openxu.core.base.XBaseViewModel;
import com.openxu.core.config.AppConfig;
import com.openxu.core.http.data.XResponse;
import com.openxu.core.http.error.NetError;
import com.openxu.core.http.rx.BaseOberver;
import com.openxu.core.http.rx.NetErrorFunction;
import com.openxu.core.http.rx.ParseDataFunction;
import com.openxu.core.http.rx.RetryWhenReset;
import com.openxu.core.utils.XLog;
import com.openxu.core.http.callback.BaseCallback;
import com.openxu.core.http.callback.DownloadCallback;
import com.openxu.core.http.callback.ResponseCallback;
import com.openxu.core.http.converter.FzyGsonConverterFactory;
import com.openxu.core.http.data.Atta;
import com.openxu.core.http.interceptor.BaseInterceptor;
import com.openxu.core.http.interceptor.LoggerInterceptor;
import com.openxu.core.http.util.TimeOutDns;
import com.openxu.core.utils.toasty.XToast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Author: openXu
 * Time: 2019/4/12 10:30
 * class: NetworkManager
 * Description: 网络请求管理器
 * <p>
 * 配置：
 * 1、如果特殊域名配置，请在模块中FpcApplication中设置：
 * //初始化app特有
 * if (getCurrentProcessName().equals(AppConfig.appId)) {
 * //（可选）主进程中初始化，将可选择的域名列表存储本地以供选择
 * ArrayList<HostEntity> hostEntities = new ArrayList<>();
 * hostEntities.add(new HostEntity("118.190.148.230", "华北"));
 * SharedData.getInstance().setAddressList(new Gson().toJson(hostEntities));
 * //（必选）设置端口
 * SharedData.getInstance().setPort(8085);
 * }
 * <p>
 * 2、动态修改网络框架地址，域名重新选择之后调用此方法
 * NetworkManager.loadAddress();
 * <p>
 * <p>
 * 使用：
 * 1、普通get请求
 * NetworkManager.getInstance().newBuilder()
 * .method(NetworkManager.Method.GET)
 * .viewModel(this)
 * .url(ServerApi.LOGIN)
 * .putParam("UserCode", userName)   //参数对
 * .putParam("ApplicationID", AppConfig.serverAppId)
 * .build(new ResponseCallback(){
 *
 * @Override public void onSuccess(String msg, FpcDataSource data) throws Exception{
 * //1、解析数据
 * }
 * @Override public void onComplete() {
 * super.onComplete();
 * //下一步请求
 * }
 * });
 * <p>
 * 2、文件下载
 * NetworkManager.getInstance().newBuilder()
 * .method(NetworkManager.Method.DOWNLOAD)
 * .viewModel(this)
 * .url(versionInfo.getAppFilePath())  //二进制地址
 * .putParam("fileName", versionInfo.getLocalFileName())
 * .putParam("isDelete", "0")
 * .downloadFilePath(versionInfo.getLocalFilePath())
 * .build(new DownloadCallback() {
 * @Override public void onStart() {
 * }
 * @Override public void onProgress(FpcDownloadData download) {
 * }
 * @Override public void onFail(String message) {
 * super.onFail(message);
 * }
 * @Override public void onComplete() {
 * }
 * });
 * <p>
 * 3、Post提交（可包含附件）
 * NetworkManager.getInstance().newBuilder()
 * .method(NetworkManager.Method.POST)
 * .viewModel(this)
 * .url(ServerApi.POST_FEED_BACK)   //表单接口
 * .putParam("key1", "value1")      //表单参数
 * .serialKey("serialKey")          //附件key
 * .fileList(data)                  //附件
 * .build(new ResponseCallback() {
 * @Override public void onSuccess(String msg, FpcDataSource data) throws Exception {
 * }
 * });
 * <p>
 * 4、纯附件提交（离线任务附件提交）
 * NetworkManager.getInstance().newBuilder()
 * .method(NetworkManager.Method.UPLOAD)
 * .viewModel(this)
 * .serialKey("serialKey")          //附件key
 * .fileList(data)                  //附件
 * .build(new ResponseCallback() {
 * @Override public void onSuccess(String msg, FpcDataSource data) throws Exception {
 * }
 * });
 * <p>
 * <p>
 * Update:
 */
public class NetworkManager {

    private Retrofit retrofit;
    private ApiService apiService;
    private static NetworkManager INSTANCE;

    static {
        INSTANCE = new NetworkManager();
    }

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    private NetworkManager() {

        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type","application/json;charset=UTF-8");
//         header.put("Accept-Encoding","gzip,deflate");
        //可以把客户端请求改成短连接，修改http header:Connection=close（http1.1协议默认是Connection=Keep-Alive，也就是长连接）
        /**
         * 保持长连接，会产生一个问题，如果客户端保持时间比服务端长，服务端超时后主动断开连接了，而客户端认为这个连接还能使用，就会报connect reset
         * 解决办法：客户端保持连接时间比服务端短，断开连接不能让服务端执行，而是客户端断开
         *          .connectionPool(new ConnectionPool(5, 50, TimeUnit.SECONDS))
         */
        header.put("Connection", "keep-alive");
//        header.put("Connection", "close");
        header.put("Accept", "*/*");
//        httpGet.setProtocolVersion(HttpVersion.HTTP_1_0);
//        httpGetheader.put(HTTP.CONN_DIRECTIVE,HTTP.CONN_CLOSE);
        // 初始化okhttp
        OkHttpClient client = new OkHttpClient.Builder()
//                .retryOnConnectionFailure(true)//默认重试一次，若需要重试N次，则要实现拦截器。
                .retryOnConnectionFailure(false)
                //DNS解析超时，如果不设置可能回出现在网络不可用的情况下，DNS解析时间太长
                .connectTimeout(4000, TimeUnit.MILLISECONDS)       //IP连接超时
                .dns(new TimeOutDns(4000, TimeUnit.MILLISECONDS))
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .writeTimeout(30000, TimeUnit.MILLISECONDS)
                //连接线程池：最大空闲连接数、keep-Alive时间、时间单位
                .connectionPool(new ConnectionPool(5, 30, TimeUnit.SECONDS))
                .addInterceptor(new BaseInterceptor(header))
                .addInterceptor(new LoggerInterceptor())
//                .addInterceptor(new HttpLoggingInterceptor())
                // 模拟服务器返回数据（注释）
                //.addInterceptor(new MockInterceptor(Parrot.create(MockService.class)))
                //.addInterceptor(new RetryInterceptor(2))  //重试
                .build();
        client.dispatcher().setMaxRequests(60);         //最大并发请求数为60，多个主机最大请求数叠加不能超过该并发数（超过取最小值）
        client.dispatcher().setMaxRequestsPerHost(5);   //每个主机最大请求数(保持长连接的数量)

        // 初始化Retrofit
        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(AppConfig.baseUrl)//"http://www.baidu.com/";
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FzyGsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @IntDef({Method.GET, Method.POST, Method.DOWNLOAD, Method.UPLOAD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Methods {
    }

    public static class Method {
        public static final int GET = 1;
        public static final int POST = 2;
        public static final int DOWNLOAD = 3;
        public static final int UPLOAD = 4;
    }
    public RequstBuilder build() {
        return new RequstBuilder();
    }
    public class RequstBuilder {
        public XBaseViewModel viewModel;   //ViewModel用于同步请求生命周期
        public String url;    //url
        public Map<String, String> params = new HashMap<>();   //参数
        public BaseCallback callback;
        public boolean showDialog = true;          //是否显示进度条
        public boolean postSuccessToast = true;    //post提交成功后是否Toast默认提示（ParseDataFunction提示：提交成功）
        //post带实体对象
        public Object object;
        //文件下载
        public String downloadFilePath;   //文件缓存路径
        public List<Atta> fileList;
        private RequstBuilder(){
        }

        public RequstBuilder viewModel(XBaseViewModel viewModel) {
            this.viewModel = viewModel;
            return this;
        }
        public RequstBuilder url(String url) {
            this.url = url;
            return this;
        }
        public RequstBuilder showDialog(boolean showDialog) {
            this.showDialog = showDialog;
            return this;
        }
        public RequstBuilder postSuccessToast(boolean postSuccessToast) {
            this.postSuccessToast = postSuccessToast;
            return this;
        }
        public RequstBuilder downloadFilePath(String downloadFilePath) {
            this.downloadFilePath = downloadFilePath;
            return this;
        }
        public RequstBuilder putParam(String key, String value) {
            params.put(key, value);
            return this;
        }
        public RequstBuilder putParams(Map<String, String> params) {
            this.params.putAll(params);
            return this;
        }
        public RequstBuilder object(Object object) {
            this.object = object;
            return this;
        }
        public RequstBuilder fileList(List<Atta> fileList) {
            this.fileList = fileList;
            return this;
        }

        /**
         * get请求
         */
        public <T> void doGet(ResponseCallback<T> callback) {
            apiService.rxGet(url, params)
//                .compose(new XTransformer<FanyiResult>().baseTransformer(new ParseDataFunction(FanyiResult.class)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .retryWhen(new RetryWhenReset(4, 100))
                    .map(new ParseDataFunction<T>(callback.getDataClass()))
                    .onErrorResumeNext(new NetErrorFunction())
                    .subscribe(new BaseOberver<T>(viewModel, showDialog) {
                        @Override
                        public void onNext(T response) {
                            try {
                                callback.onSuccess(response);
                            } catch (Exception e) {
                                XLog.e("：解析数据错误" + e);
                                if (AppConfig.DEBUG) {
                                    XToast.error("  " + url + "---" + params.toString() + "---" + e.toString());
                                }
                                XToast.error(e.getMessage());
                            }
                        }
                        @Override
                        public void onError(Throwable e) {
                            XLog.e("请求数据错误：" + e);
                            if (AppConfig.DEBUG) {
                                callback.onFail("  " + url + "---" + params.toString() + "---" + e.toString());
                            }
                            callback.onFail(((NetError) e).getUserMsg());   //给用户提示的错误信息
                            super.onError(e);
                        }

                        @Override
                        public void onComplete() {
                            super.onComplete();
                        }
                    });
            }

   /*     public <T> void build(BaseCallback<T> callback) {
            this.callback = callback;
            if (method == Method.GET && callback instanceof ResponseCallback) {
                doGet(this);
            } else if (method == Method.DOWNLOAD && callback instanceof DownloadCallback) {
                downLoadFile(this);
            } else if (method == Method.POST && callback instanceof ResponseCallback) {
                if (AppConfig.DEBUG) {
                    for (String key : params.keySet()) {
                        XLog.e("post请求参数== " + key + " : " + params.get(key));
                    }
                }

                if (null != fileList && fileList.size() > 0) {
                    //先上传附件
//                    uploadFile(this, serialKey, operUrl);
                } else {
                    doPost(this);
                }
            } else if (method == Method.UPLOAD && callback instanceof ResponseCallback) {
//                uploadFile(this, serialKey, operUrl);
            } else {
                throw new RuntimeException("Please set the correct callback type");
            }
        }*/
    }



    /**
     * 下载文件（app升级下载）
     *
     * @param builder
     */
    private void downLoadFile(RequstBuilder builder) {
      /*  apiService.rxDownload(BaseInterceptor.fileDownloadUrl + builder.url, builder.params)
//        apiService.rxDownload("http://vjs.zencdn.net/v/oceans.mp4", new HashMap<>())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .concatMap(new DownloadFunction(builder.downloadFilePath))
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWhenReset(4, 100))   //请求失败重试次数
                .onErrorResumeNext(new DownLoadNetErrorFunction())
                .subscribe(new BaseOberver<FpcDownloadData>(builder.viewModel, builder.showDialog) {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
//                        XLog.w("下载订阅："+FTimeUtils.date2Str(new Date(), "HH:mm:ss"));
                        ((DownloadCallback) builder.callback).onStart();
                    }

                    @Override
                    public void onNext(FpcDownloadData response) {
//                        XLog.i("下载进度："+response);
                        ((DownloadCallback) builder.callback).onProgress(response);
                    }
                    @Override
                    public void onError(Throwable e) {
                        XLog.e("下载文件错误：" + e);
                        builder.callback.onFail(((NetError) e).getUserMsg());   //给用户提示的错误信息
                        super.onError(e);
                    }
                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });*/
    }

    /**
     * 附件上传
     *
     * @param builder
     */
  /*  private void uploadFile(RequstBuilder builder, String serialKey, String operUrl) {
        if (null == builder.fileList || builder.fileList.size() <= 0)
            return;
        MultipartBody.Part[] parts = new MultipartBody.Part[builder.fileList.size()];
        File file;
        Atta atta;
        String mediaType;
        String name;

        FpcFileInfoEntity fpcFileInfoEntity = new FpcFileInfoEntity();
        fpcFileInfoEntity.setFileSize(builder.fileList.size());
        ArrayList<FpcEachFileInfoEntity> fpcEachFileInfoEntities = new ArrayList<>();


        float filetatalSize = 0f;
        for (int i = 0; i < builder.fileList.size(); i++) {
            atta = builder.fileList.get(i);
            XLog.e("当前文件的路径==" + atta.getPath());
            file = new File(atta.getPath());
            mediaType = AttaType.IMAGE == atta.getType() ? "image/jpeg" :   //图片
                    (AttaType.AUDIO == atta.getType() ? "audio/x-m4a" :     //音频
                            (AttaType.VIDEO == atta.getType() ? "video/mp4" : AttaType.TEXT == atta.getType() ? "text/plain" : ""));  //视频
            name = atta.getName() + "や" + atta.getDescription();
            //不支持中文 や java.lang.IllegalArgumentException: Unexpected char 0x3084 at 82 in Content-Disposition value: form-data
//            URLEncoder.encode(file.getName(),"UTF-8")；//App传递给后台时候编码
//            URLDecoder.decode(ss,"UTF-8")；//后台接到时候进行转码
            name = encodeHeadInfo(name);
            // TODO 附件名称不支持中文，导致后台获取不到标题
            long timeStamp = System.currentTimeMillis();
            XLog.i("上传附件：name=" + name + " filename=" + (timeStamp + "_" + file.getName()) + " mediaType=" + mediaType);

            FpcEachFileInfoEntity fpcEachFileInfoEntity = new FpcEachFileInfoEntity();
            String fileSize = FileUtils.getFileSize(file);
            fpcEachFileInfoEntity.setName(timeStamp + "_" + file.getName());
            try {
                if ("KB".equals(fileSize.substring(fileSize.length() - 2))) {
                    filetatalSize += Float.parseFloat(fileSize.substring(0, fileSize.length() - 2));
                } else if ("MB".equals(fileSize.substring(fileSize.length() - 2))) {
                    filetatalSize += Float.parseFloat(fileSize.substring(0, fileSize.length() - 2)) * 1024;
                } else if ("GB".equals(fileSize.substring(fileSize.length() - 2))) {
                    filetatalSize += Float.parseFloat(fileSize.substring(0, fileSize.length() - 2)) * 1024 * 1024;
                }
            } catch (Exception e) {
            }

            fpcEachFileInfoEntity.setSize(fileSize);
            fpcEachFileInfoEntities.add(fpcEachFileInfoEntity);
            parts[i] = MultipartBody.Part.createFormData(name, timeStamp + "_" + file.getName(),
                    RequestBody.create(MediaType.parse(mediaType), file));
        }

        boolean iswifi = FNetworkUtils.isWifiConnected();
        fpcFileInfoEntity.setNetEnv(iswifi ? "wifi连接" : (FNetworkUtils.getNetworkType().toString()));
        fpcFileInfoEntity.setFileKey(serialKey);
        fpcFileInfoEntity.setList(fpcEachFileInfoEntities);
        long startMinites = System.currentTimeMillis() / 1000;
        fpcFileInfoEntity.setStartTime(FTimeUtils.date2Str(new Date(), FTimeUtils.DATE_TIME));
        //http://114.115.144.251:8001/WebApi/Upload/Post?SerialKey=xxxx&dataKey=00-00-00-00
        float finalFiletatalSize = filetatalSize;
        ProcessLogUtils.insertOne(ProcessLogUtils.NetType, "附件接口开始,key:" + builder.serialKey);
        apiService.rxFileUpload(BaseInterceptor.fileUploadUrl + builder.serialKey + (TextUtils.isEmpty(operUrl) ? "" : "&operUrl=" + operUrl), parts)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new UploadFunction())
                .retryWhen(new RetryWhenReset(4, 100))   //请求失败重试次数
                .onErrorResumeNext(new NetErrorFunction())
                .subscribe(new BaseOberver<XResponse>(builder.viewModel, builder.showDialog) {
                    @Override
                    public void onNext(XResponse response) {
                        XLog.i(response.getMessage());
                        success = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        XLog.e("上传文件错误：" + e);
                        builder.callback.onFail(((NetError) e).getUserMsg());
                        fpcFileInfoEntity.setStatus("fail");
                        fpcFileInfoEntity.setEndTime(FTimeUtils.date2Str(new Date(), FTimeUtils.DATE_TIME));

                        Date startDate = FTimeUtils.str2Date(fpcFileInfoEntity.getStartTime(), FTimeUtils.DATE_TIME);
                        Date endDate = FTimeUtils.str2Date(fpcFileInfoEntity.getEndTime(), FTimeUtils.DATE_TIME);
                        long timeDiff = endDate.getTime() - startDate.getTime();
                        String timeByMs = FTimeUtils.getTimeByMs((int) timeDiff);
                        fpcFileInfoEntity.setTimeDifference(timeByMs);

                        long endMinites = System.currentTimeMillis() / 1000;
                        fpcFileInfoEntity.setNetSpeed((finalFiletatalSize / (endMinites - startMinites)) + "kb/s");
                        builder.callback.onGetFileInfo(fpcFileInfoEntity);

                        ProcessLogUtils.insertOne(ProcessLogUtils.NetType, "附件上传失败：" + fpcFileInfoEntity.toString());
                        super.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        if (success) {
                            long endMinites = System.currentTimeMillis() / 1000;
                            fpcFileInfoEntity.setNetSpeed(finalFiletatalSize / (endMinites - startMinites) + "kb/s");
                            fpcFileInfoEntity.setStatus("success");
                            fpcFileInfoEntity.setEndTime(FTimeUtils.date2Str(new Date(), FTimeUtils.DATE_TIME));

                            Date startDate = FTimeUtils.str2Date(fpcFileInfoEntity.getStartTime(), FTimeUtils.DATE_TIME);
                            Date endDate = FTimeUtils.str2Date(fpcFileInfoEntity.getEndTime(), FTimeUtils.DATE_TIME);
                            long timeDiff = endDate.getTime() - startDate.getTime();
                            String timeByMs = FTimeUtils.getTimeByMs((int) timeDiff);
                            fpcFileInfoEntity.setTimeDifference(timeByMs);

                            builder.callback.onGetFileInfo(fpcFileInfoEntity);
                            ProcessLogUtils.insertOne(ProcessLogUtils.NetType, "附件上传成功：" + fpcFileInfoEntity.toString());
                            //附件上传成功之后，提交表单
                            if (builder.method == Method.POST)
                                doPost(builder);
                        }
                    }
                });

    }*/

    private void doPost(RequstBuilder builder) {
//       doPost(builder, RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(builder.params)), builder.objList.toArray());

        if (null != builder.object)
            doPost(builder, builder.object);
        else
            doPost(builder, builder.params);
//        List<RequestBody> bodies = new ArrayList<>();
//        bodies.add(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), new Gson().toJson(builder.params)));
//        for(Object obj : builder.objList){
//            bodies.add(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), obj);
//        }
//        doPost(builder, bodies.toArray());
    }


    /**
     * 提交表单
     * @param builder
     */
    private void doPost(RequstBuilder builder, Object object) {
     /*   apiService.rxPost(builder.url, object)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new ParseDataFunction(null, builder))
                .retryWhen(new RetryWhenReset(4, 100))   //请求失败重试次数
                .onErrorResumeNext(new NetErrorFunction())
                .subscribe(new BaseOberver<XResponse>(builder.viewModel, builder.showDialog) {
                    @Override
                    public void onNext(XResponse response) {
                        try {
                            ((ResponseCallback) builder.callback).onSuccess(response.getMessage(), null);
                        } catch (Exception e) {
                            XLog.e("解析数据错误：" + e);
                            if (AppConfig.DEBUG) {
                                XToast.error(builder.url + "---" + object.toString() + "---" + e.toString());
                            } else {
                                XToast.error(e.getMessage());
                            }

                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        XLog.e("请求数据错误：" + e);
                        if (AppConfig.DEBUG) {
                            XToast.error(builder.url + "---" + object.toString() + "---" + e.toString());
                        } else {
                            builder.callback.onFail(((NetError) e).getUserMsg());   //给用户提示的错误信息
                        }
                        super.onError(e);
                    }
                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });*/
    }

    private static String encodeHeadInfo(String headInfo) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0, length = headInfo.length(); i < length; i++) {
            char c = headInfo.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                stringBuffer.append(String.format("\\u%04x", (int) c));
            } else {
                stringBuffer.append(c);
            }
        }
        return stringBuffer.toString();
    }
}
