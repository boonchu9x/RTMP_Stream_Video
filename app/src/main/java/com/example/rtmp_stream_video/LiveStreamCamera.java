package com.example.rtmp_stream_video;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class LiveStreamCamera extends AppCompatActivity implements SurfaceHolder.Callback, ConnectCheckerRtmp, CompoundButton.OnCheckedChangeListener {


    @BindView(R.id.cameraView)
    SurfaceView cameraView;
    @BindView(R.id.switch_camera)
    ImageView imgSwitchCamera;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.record_video)
    CheckBox ckBoxRecordVideo;

    private RtmpCamera2 rtmpCamera2;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_live_stream_camera);
        ButterKnife.bind(this);

        init();
        event();
    }

    private void init() {
        handler = new Handler();
        loading.setVisibility(View.GONE);
        rtmpCamera2 = new RtmpCamera2(cameraView, this);
        rtmpCamera2.setReTries(Constant.RETRY_COUNT);
        cameraView.getHolder().addCallback(this);
    }

    private void event() {
        ckBoxRecordVideo.setOnCheckedChangeListener(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        rtmpCamera2.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (rtmpCamera2.isRecording()) {
            rtmpCamera2.stopRecord();
        }
        if (rtmpCamera2.isStreaming()) {
            rtmpCamera2.stopStream();
        }
        rtmpCamera2.stopPreview();
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "Connection success");
            }
        });

    }

    @Override
    public void onConnectionFailedRtmp(@NonNull String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rtmpCamera2.shouldRetry(reason)) {
                    Log.d("TAG", "Retry");
                    rtmpCamera2.reTry(5000); //reconnect after 5s
                } else {
                    Log.d("TAG", "Connection fail.\n" + reason);
                    rtmpCamera2.stopStream();
                }
            }
        });
    }

    @Override
    public void onNewBitrateRtmp(long bitrate) {

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "Disconnected");
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "Auth error");
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG", "Auth success");
            }
        });
    }


    @OnClick({R.id.start, R.id.stop, R.id.switch_camera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_camera:
                switchCamera();
                break;
            case R.id.start:
                loading.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        startStream();
                    }
                }, Constant.DELAY_START_LIVE);
                break;
            case R.id.stop:
                stopStream();
                break;
        }

    }

    @OnTouch(R.id.linear_parent)
    public void onTouch(View view, MotionEvent event){
        ckBoxRecordVideo.setOnCheckedChangeListener(this);
    }

    private void switchCamera() {
        try {
            rtmpCamera2.switchCamera();
        } catch (CameraOpenException e) {
            Log.e("TAG", "Camera Error!\n" + e.getMessage());
        }

    }

    private void startStream() {
        if (rtmpCamera2.isStreaming()) rtmpCamera2.stopStream();
        if (rtmpCamera2.isRecording() || rtmpCamera2.prepareAudio() && rtmpCamera2.prepareVideo()) {
            Log.d("TAG", "Starting stream");
            rtmpCamera2.startStream(Constant.URL_SERVER_LIVESTREAM + Constant.SERVER_NAME);
            //rtmpCamera2.setAuthorization(Constant.AUTH, Constant.PASS);
        } else {
            Log.d("TAG", "Error preparing stream. This device can't do it");
        }
    }

    private void stopStream() {
        loading.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (rtmpCamera2.isStreaming()) rtmpCamera2.stopStream();
                loading.setVisibility(View.GONE);
                Log.d("TAG", "Stop stream");
                onBackPressed();
            }
        }, Constant.DELAY);

    }


    private void recordVideo(CheckBox checkBox) {
        if (checkBox.isChecked())//start record video
        {
            if (rtmpCamera2.isRecording()) rtmpCamera2.stopRecord();
            try {
                File folder = new File(Constant.FOLDER_PATH);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String currentDateAndTime = simpleDateFormat.format(new Date());
                if (!rtmpCamera2.isStreaming()) {
                    if (rtmpCamera2.prepareAudio() && rtmpCamera2.prepareVideo()) {
                        rtmpCamera2.startRecord(folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                        Log.d("TAG", "Recording... ");
                    } else {
                        Log.d("TAG", "Error preparing stream. This device can't do it");
                    }
                } else {
                    rtmpCamera2.startRecord(folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                    Log.d("TAG", "Recording... ");
                }
            } catch (IOException e) {
                loading.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rtmpCamera2.stopRecord();
                        Toast.makeText(LiveStreamCamera.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "Error:\n" + e.getMessage());
                        loading.setVisibility(View.GONE);
                    }
                }, Constant.DELAY);

            }

        } else {
            loading.setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rtmpCamera2.stopRecord();
                    Toast.makeText(LiveStreamCamera.this, "Save video sucess", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "Saved video success");
                    loading.setVisibility(View.GONE);
                }
            }, Constant.DELAY);

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        View parent = (View) buttonView.getParent();
        parent.performClick();
        recordVideo(ckBoxRecordVideo);
    }
}
