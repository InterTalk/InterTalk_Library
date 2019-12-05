package com.intertalk.library;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.intertalk.http.OkGo;
import com.intertalk.http.callback.StringCallback;
import com.intertalk.http.cookie.CookieJarImpl;
import com.intertalk.http.cookie.store.SPCookieStore;
import com.intertalk.http.interceptor.HttpLoggingInterceptor;
import com.intertalk.http.model.Response;
import com.intertalk.ui.widget.dialog.QMUIDialog;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private static final int CONNECT_TIMEOUT_TIME = 10 * 1000;
    private static final int READ_TIMEOUT_TIME = 10 * 1000;
    private static final int WRITE_TIMEOUT_TIME = 10 * 1000;
    private static final int RETRY_COUNT = 2;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        initOkGo();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkGo.<String>get("http://idc.intertalk.wang/api/v4/time")
                        .tag(this)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {

                            }
                        });
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QMUIDialog.MessageDialogBuilder(mContext)
                        .setTitle("dialog")
                        .setMessage("dialog测试")
                        .show();
            }
        });
    }

    private void initOkGo(){
        try {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
            //log打印级别，决定了log显示的详细程度
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            //log级别
            loggingInterceptor.setColorLevel(Level.INFO);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(loggingInterceptor);
            //全局的读取超时时间
            builder.readTimeout(READ_TIMEOUT_TIME, TimeUnit.MILLISECONDS);
            //使用sp保持cookie，如果cookie不过期，则一直有效
            builder.cookieJar(new CookieJarImpl(new SPCookieStore(mContext)));
            //全局的写入超时时间
            builder.writeTimeout(WRITE_TIMEOUT_TIME, TimeUnit.MILLISECONDS);
            //全局的连接超时时间
            builder.connectTimeout(CONNECT_TIMEOUT_TIME, TimeUnit.MILLISECONDS);

            OkGo.getInstance().init(getApplication())
                    .setOkHttpClient(builder.build())
                    .setRetryCount(RETRY_COUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
