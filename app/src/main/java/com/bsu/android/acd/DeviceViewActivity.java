package com.bsu.android.acd;

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

import com.bsu.android.acd.rpc.Button;
import com.bsu.android.acd.rpc.RpcCallback;
import com.bsu.android.acd.rpc.RpcClient;
import com.bsu.android.acd.rpc.RpcRequest;
import com.bsu.android.acd.rpc.RpcResponse;
import com.bsu.android.acd.rpc.RpcResults;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;
import org.w3c.dom.Text;

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

        mRpcClient.setUri("http://" + mCurrentDevice.getDeviceIp()+":8000");
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

    public class BtnAdapter extends RecyclerView.Adapter<BtnAdapter.ViewHolder> {
        private ArrayList<Button> mDataSet;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView btnName;
            TextView btnId;
            ImageView btnImageView;

            public ViewHolder(View itemView) {
                super(itemView);
                btnName = (TextView) itemView.findViewById(R.id.btn_name);
                btnId = (TextView) itemView.findViewById(R.id.btn_id);
                btnImageView = (ImageView) itemView.findViewWithTag("fuck");
            }

            public void setText(String s) {
                btnName.setText(s);
            }

            public void setBtnId(int id) {
                btnId.setText(Integer.toString(id));
            }

            public void setBtnImage(String uri) {
                Picasso.with(DeviceViewActivity.this)
                        .load(DeviceViewActivity.this.mRpcClient.getmUri()+"/images?"+uri)
                        .into(btnImageView);
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
            Button b = mDataSet.get(position);
            holder.setText(b.getText());
            holder.setBtnId(b.getId());
            holder.setBtnImage(b.getUri());
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
