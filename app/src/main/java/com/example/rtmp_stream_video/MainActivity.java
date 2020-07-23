package com.example.rtmp_stream_video;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.rtmp_stream_video.stream_display.DisplayActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    String[] permission = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.stream_camera, R.id.stream_screen, R.id.watch_live, R.id.options, R.id.about, R.id.exit})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.stream_camera:
                if (checkPermission().size() != 0)
                    for (int i = 0; i < checkPermission().size(); i++) {
                        Permission.requestPermissions(MainActivity.this, permission, Constant.intPermission[i]);
                    }
                else{
                    startActivity(new Intent(MainActivity.this, LiveStreamCamera.class));
                }

                break;
            case R.id.watch_live:
                startActivity(new Intent(MainActivity.this, WatchLive.class));
                break;
            case R.id.stream_screen:
                startActivity(new Intent(MainActivity.this, DisplayActivity.class));
                break;
            case R.id.options:
                break;
            case R.id.about:
                break;
            case R.id.exit:
                finish();
                break;
        }
    }

    private List<String> checkPermission() {
        List<String> list = new ArrayList<>();
        for (String item : permission) {
            if (PackageManager.PERMISSION_GRANTED != Permission.checkSelfPermission(MainActivity.this, item)) {
                list.add(item);
            }
        }
        return list;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.WRITE_STORAGE:
                Permission.handlePermission(grantResults, MainActivity.this, requestCode, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
            case Constant.CAMERA:
                Permission.handlePermission(grantResults, MainActivity.this, requestCode, Manifest.permission.CAMERA);
                break;
            case Constant.RECORS_AUDIO:
                Permission.handlePermission(grantResults, MainActivity.this, requestCode, Manifest.permission.RECORD_AUDIO);
                break;
        }
    }




}
