package com.bsu.android.acd;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bsu.android.acd.pojo.ButtonAction;
import com.bsu.android.acd.pojo.LogData;
import com.bsu.android.acd.rpc.RpcClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.List;


import javax.inject.Inject;
import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogTableViewActivity extends AppCompatActivity {

    @Inject
    OkHttpClient okHttpClient;

    @Bind(R.id.loggeddata)
    TableLayout DataTableLayout;

    @Inject
    RpcClient mRpcClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_log_view);
        ButterKnife.bind(this);

        ((AcdApplication) getApplication()).getApiComponent()
                .inject(this);

        getLog();
    }

    private void getLog() {

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("http://10.254.9.78:8000")
                .build();

        LogData logData = retrofit.create(LogData.class);
        Call<List<ButtonAction>> call = logData.getLog();
        call.enqueue(new Callback<List<ButtonAction>>() {
            @Override
            public void onResponse(Call<List<ButtonAction>> call, Response<List<ButtonAction>> response) {
                Toast.makeText(LogTableViewActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                updateTable(response.body());
            }

            @Override
            public void onFailure(Call<List<ButtonAction>> call, Throwable t) {
                Toast.makeText(LogTableViewActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateTable(List<ButtonAction> body) {
        for (ButtonAction l : body) {
            TableRow row = new TableRow(LogTableViewActivity.this);
            TextView action = new TextView(LogTableViewActivity.this);
            TextView datetime = new TextView(LogTableViewActivity.this);

            action.setText(l.getAction());
            action.setWidth(330);
            action.setAllCaps(true);
            action.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            action.setTextSize(15);

            datetime.setText(l.getTime().toString());
            datetime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            datetime.setTextSize(15);

            row.addView(action);
            row.addView(datetime);
            DataTableLayout.addView(row);
        }
    }
}