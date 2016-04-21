package com.bsu.android.acd;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import com.bsu.android.acd.pojo.ButtonAction;
import com.bsu.android.acd.pojo.ImageUploadService;
import com.bsu.android.acd.pojo.LogData;
import com.bsu.android.acd.rpc.RpcClient;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogViewActivity extends AppCompatActivity {

    @Inject
    OkHttpClient okHttpClient;

    @Bind(R.id.pie_chart)
    PieChart mPieChart;

    @Inject
    RpcClient mRpcClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_view);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((AcdApplication) getApplication()).getApiComponent()
                .inject(this);

        getLog();
    }

    private void getLog() {

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl("http://192.168.1.36:8000")
                .build();

        LogData logData = retrofit.create(LogData.class);
        Call<List<ButtonAction>> call = logData.getLog();
        call.enqueue(new Callback<List<ButtonAction>>() {
            @Override
            public void onResponse(Call<List<ButtonAction>> call, Response<List<ButtonAction>> response) {
                Toast.makeText(LogViewActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                updateGraph(response.body());
            }

            @Override
            public void onFailure(Call<List<ButtonAction>> call, Throwable t) {
                Toast.makeText(LogViewActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateGraph(List<ButtonAction> buttonActions) {
        Map<String, Integer> map = getCount(buttonActions);

        ArrayList<Entry> count = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int i = 0;
        for (String key: map.keySet()) {
            labels.add(key);
            count.add(new Entry(map.get(key), i++));
        }

        PieDataSet dataSet = new PieDataSet(count, "Logged Data");

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(labels, dataSet);
        mPieChart.setDescription("Activity since start!");
        mPieChart.setData(data);
        mPieChart.highlightValues(null);
        // notify changes in value
        mPieChart.invalidate();
    }

    public Map<String, Integer> getCount(List<ButtonAction> buttonActions) {
        Map<String, Integer> map = new HashMap<>();

        for (ButtonAction btnAction: buttonActions) {
            if (map.containsKey(btnAction.getAction())) {
                // increment count
                map.put(btnAction.getAction(),map.get(btnAction.getAction()) + 1);
            } else {
                // add new element
                map.put(btnAction.getAction(), 1);
            }
        }

        return map;
    }

}
