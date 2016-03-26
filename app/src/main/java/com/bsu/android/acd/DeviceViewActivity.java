package com.bsu.android.acd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsu.android.acd.rpc.Button;
import com.bsu.android.acd.rpc.RpcCallback;
import com.bsu.android.acd.rpc.RpcClient;
import com.bsu.android.acd.rpc.RpcRequest;
import com.bsu.android.acd.rpc.RpcResponse;
import com.bsu.android.acd.rpc.RpcResults;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceViewActivity extends AppCompatActivity implements RpcCallback {
    private final static String TAG = "DeviceViewActivity";
    private Device mCurrentDevice;

    @Inject
    RpcClient mRpcClient;
    @Bind(R.id.btn_list_viewer)
    RecyclerView mRecyclerView;
    @Inject
    Gson gson;

    private BtnAdapter mBtnAdapter;

    private String[] mData = {"a", "b", "c"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_view);
        ButterKnife.bind(this);

        ((AcdApplication) getApplication()).getApiComponent()
                .inject(this);

        mCurrentDevice = Parcels.unwrap(getIntent()
                .getParcelableExtra("device"));

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mBtnAdapter = new BtnAdapter();
        mRecyclerView.setAdapter(mBtnAdapter);

        setTitle(mCurrentDevice.getDeviceName());

        mRpcClient.setUri("http://" + mCurrentDevice.getDeviceIp()+":8000/jsonrpc");
        getButtonList();
        Log.d(TAG, "Selected device: " + mCurrentDevice + "/" + mCurrentDevice.getDeviceIp());
    }

    public void getButtonList() {
        mRpcClient.sendRequest(RpcRequest.builder()
                .method("get_btns")
                .build(),this);
    }

    @Override
    public void onResponse(RpcResponse response) {
        List<Button> btnList = RpcResults.buttonListFromJson(response.getResult(),gson);
        mBtnAdapter.addButtons(btnList);
        mBtnAdapter.notifyItemInserted(mBtnAdapter.getItemCount()-1);
        Log.d(TAG, "Recieved button list");
    }

    public static class BtnAdapter extends RecyclerView.Adapter<BtnAdapter.ViewHolder> {
        private ArrayList<Button> mDataSet;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                mTextView = (TextView) itemView.findViewById(R.id.btn_name);
            }

            public void setText(String s) {
                mTextView.setText(s);
            }
        }

        public BtnAdapter() {
            mDataSet = new ArrayList<>();
        }
//        public BtnAdapter(Object[] array) {
//            this.mDataSet = new ArrayList<>();
//            for (Object o: array) {
//                mDataSet.add((String)o);
//            }
//        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cards_layout, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setText(mDataSet.get(position).getText());
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public void addButtons(List<Button> buttons) {
            for (Button btn : buttons) {
                mDataSet.add(btn);
            }
        }

    }

}
