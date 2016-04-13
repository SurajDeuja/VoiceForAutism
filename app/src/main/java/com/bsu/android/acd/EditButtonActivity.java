package com.bsu.android.acd;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bsu.android.acd.pojo.DeviceButton;
import com.bsu.android.acd.pojo.ImageUploadService;
import com.bsu.android.acd.rpc.RpcClient;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.File;

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

public class EditButtonActivity extends AppCompatActivity {
    final static String TAG = "EditButtonActivity";
    private DeviceButton mDeviceButton;
    @Inject
    OkHttpClient okHttpClient;
    @Bind(R.id.btn_image_view)
    ImageView mImageViewBtn;
    @Bind(R.id.audio_text_switcher)
    ViewSwitcher mViewSwitcher;
    @Bind(R.id.img_audio_textview)
    TextView mAudioTextView;
    @Bind(R.id.img_audio_edittext)
    EditText mAudioTextEdit;
    @Inject
    RpcClient mRpcClient;
    @Bind(R.id.btn_image_upload)
    Button mUploadBtn;

    private Bitmap mBitmapImage;
    private Uri mImageUri;

    private static final int REQUEST_PICK = 1;
    private static final int REQUEST_CROP = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_button);
        ButterKnife.bind(this);
        ((AcdApplication) getApplication()).getApiComponent()
                .inject(this);

        mDeviceButton = Parcels.unwrap(getIntent().getParcelableExtra("button"));

        mImageViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDialogBox();
            }
        });

        mAudioTextView.setText(mDeviceButton.getText());
        mAudioTextEdit.setText(mAudioTextView.getText());
        mViewSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcher.showNext();
                mAudioTextEdit.setText(mAudioTextView.getText());
            }
        });

        loadImage();
        Log.d(TAG, "Button with id: " + mDeviceButton.getId());
    }

    void loadImage() {
        Picasso.with(EditButtonActivity.this)
                .load(mRpcClient.getmUri() + "/" + mDeviceButton.getUri())
                .into(mImageViewBtn);
    }

    void loadDialogBox() {
        CharSequence colors[] = new CharSequence[] {"Take Picture", "Choose Picture"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose action!");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                switch (which) {
                    case 0:
                        break;
                    default:
                        intent.setType("image/jpg");
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_PICK);
                        break;
                }
            }
        });
        builder.show();
    }

    private void handlePick(Uri imageUri) {
        mImageUri = Uri.fromFile(new File(getCacheDir(),
                new File(imageUri.getPath()).getName()));
        Crop.of(imageUri, mImageUri)
                .withMaxSize(mDeviceButton.getWidth(), mDeviceButton.getHeight())
                .start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK:
                    handlePick(data.getData());
                    break;
                case Crop.REQUEST_CROP:
                    mImageViewBtn.setImageURI(Crop.getOutput(data));
                    enableUploadBtn();
                    break;
            }
        }
    }

    public void enableUploadBtn() {
        mUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(mImageUri);
            }
        });

        mUploadBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.indigo));
        mUploadBtn.setText("Upload!");
        mUploadBtn.setClickable(true);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_save:
//                if (mImageUri != null && mImageChanged) {
//                    uploadImage(mImageUri);
//                }
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.edit_btn_main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mBitmapImage != null)
            outState.putParcelable("image", mBitmapImage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBitmapImage = savedInstanceState.getParcelable("image");
        if (mBitmapImage != null) {
            mImageViewBtn.setImageBitmap(mBitmapImage);
        }
    }

    public void uploadImage(Uri uri) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mRpcClient.getmUri())
                .build();

        File file = new File(uri.getPath());
        RequestBody request = RequestBody.create(MediaType.parse("images/jpg"), file);

        ImageUploadService service = retrofit.create(ImageUploadService.class);

        Call<Void> call = service.upload(request, file.getName(),
                mDeviceButton.getId(),
                mAudioTextEdit.getText().toString());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(EditButtonActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EditButtonActivity.this, "Failed to upload image!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
