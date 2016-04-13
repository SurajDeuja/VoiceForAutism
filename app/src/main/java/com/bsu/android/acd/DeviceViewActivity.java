package com.bsu.android.acd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsu.android.acd.pojo.DeviceButton;
import com.bsu.android.acd.rpc.RpcCallback;
import com.bsu.android.acd.rpc.RpcClient;
import com.bsu.android.acd.rpc.RpcRequest;
import com.bsu.android.acd.rpc.RpcResponse;
import com.bsu.android.acd.rpc.RpcResults;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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

        mRpcClient.setUri("http://" + mCurrentDevice.getDeviceIp() + ":8000");
        getButtonList();
        Log.d(TAG, "Selected device: " + mCurrentDevice + "/" + mCurrentDevice.getDeviceIp());
    }

    public void getButtonList() {
        mRpcClient.sendRequest(RpcRequest.builder()
                .method("get_btns")
                .build(), this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mBtnAdapter.deleteAll();
        mBtnAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getButtonList();
    }

    @Override
    public void onResponse(RpcResponse response) {
        List<DeviceButton> btnList = RpcResults.buttonListFromJson(response.getResult(), gson);
        mBtnAdapter.addButtons(btnList);
        mBtnAdapter.notifyItemInserted(mBtnAdapter.getItemCount() - 1);
        Log.d(TAG, "Recieved button list");
    }

    public class BtnAdapter extends RecyclerView.Adapter<BtnAdapter.ViewHolder> {
        private ArrayList<DeviceButton> mDataSet;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView btnName;
            TextView btnId;
            ImageView btnImageView;
            ImageView btnEditCard;

            public ViewHolder(View itemView) {
                super(itemView);
                btnName = (TextView) itemView.findViewById(R.id.btn_name);
                btnId = (TextView) itemView.findViewById(R.id.btn_id);
                btnImageView = (ImageView) itemView.findViewWithTag("fuck");
                btnEditCard = (ImageButton) itemView.findViewById(R.id.btn_edit);
            }

            public void setText(String s) {
                btnName.setText(s);
            }

            public void setBtnId(int id) {
                btnId.setText(Integer.toString(id));
            }

            public void setBtnImage(String uri) {
                Picasso.with(DeviceViewActivity.this)
                        .load(DeviceViewActivity.this.mRpcClient.getmUri() + "/" + uri)
                        .into(btnImageView);
            }
        }

        public BtnAdapter() {
            mDataSet = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.cards_layout, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final DeviceButton b = mDataSet.get(position);
            holder.setText(b.getText());
            holder.setBtnId(b.getId());
            holder.setBtnImage(b.getUri());
            holder.btnEditCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DeviceViewActivity.this, EditButtonActivity.class);
                    intent.putExtra("button", Parcels.wrap(b));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mDataSet.size();
        }

        public void addButtons(List<DeviceButton> deviceButtons) {
            for (DeviceButton btn : deviceButtons) {
                mDataSet.add(btn);
            }
        }

        public void deleteAll() {
            mDataSet = new ArrayList<>();
        }

    }

}
