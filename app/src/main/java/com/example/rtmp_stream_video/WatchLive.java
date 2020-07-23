package com.example.rtmp_stream_video;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.Util;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WatchLive extends AppCompatActivity implements PlaybackPreparer, PlayerControlView.VisibilityListener, CheckBox.OnCheckedChangeListener{

    @BindView(R.id.control_video)
    CheckBox checkControlVideo;
    @BindView(R.id.video_view)
    PlayerView videoView;

    private Handler handler;
    private Runnable runnable;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_watch_live);
        ButterKnife.bind(this);

        init();

    }


    private void init(){
        handler = new Handler();
        runnable = () -> {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
            valueAnimator.setDuration(1000);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float alpha = (float) animation.getAnimatedValue();
                    if (alpha == 0f) {
                        player.setPlayWhenReady(true);
                    }
                }
            });
            valueAnimator.start();
        };
        playVideo(Constant.URL_WATCH_SERVER_LIVE);
        setupPlayer();
    }

    @SuppressLint({"SourceLockedOrientationActivity", "WrongConstant"})
    private void changeOrientation() {
        int orientation = WatchLive.this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
        if (Settings.System.getInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }, 4000);
        }
    }


    @OnClick(R.id.rotation_screen)
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rotation_screen:
                changeOrientation();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }



    private void setupPlayer() {
        player =
                new SimpleExoPlayer.Builder(/* context= */ WatchLive.this)
                        .build();
        player.setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true);
        player.setPlayWhenReady(false);
        videoView.setPlayer(player);
        videoView.onResume();
        videoView.setPlaybackPreparer(this);
        videoView.setControllerHideOnTouch(true);

        videoView.setControllerVisibilityListener(this);
        videoView.requestFocus();
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                error.printStackTrace();
                Toast.makeText(WatchLive.this, "Không thể phát video", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
        if (mediaSource != null) {
            player.prepare(mediaSource);
        }
    }

    private void playVideo(String videoUrl) {
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(WatchLive.this,
                Util.getUserAgent(WatchLive.this, WatchLive.this.getApplicationInfo().loadLabel(WatchLive.this.getPackageManager()).toString()));
        switch (Util.inferContentType(Uri.parse(videoUrl))) {
            case C.TYPE_DASH:
                mediaSource = new DashMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(videoUrl));
                break;
            case C.TYPE_SS:
                mediaSource = new SsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(videoUrl));
                break;
            case C.TYPE_HLS:
                mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(videoUrl));
                break;
            case C.TYPE_OTHER:
                Uri uri = Uri.parse(videoUrl);
                if (Objects.equals(uri.getScheme(), "rtmp")) {
                    RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();
                    // This is the MediaSource representing the media to be played.
                    mediaSource = new ProgressiveMediaSource.Factory(rtmpDataSourceFactory)
                            .createMediaSource(uri);
                } else {
                    mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(uri);
                }
                break;
            default:
                break;
        }
    }

    private void pauseVideo() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    private void resumeVideo() {
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    public void onPause() {
        pauseVideo();
        super.onPause();
    }

    @Override
    public void onResume() {
        resumeVideo();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.onPause();
        }
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            mediaSource = null;
        }
    }

    @Override
    public void preparePlayback() {
        player.retry();
    }

    @Override
    public void onVisibilityChange(int visibility) {

    }

    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {

        @NotNull
        @Override
        public Pair<Integer, String> getErrorMessage(ExoPlaybackException e) {
            String errorString = WatchLive.this.getString(R.string.error_generic);
            if (e.type == ExoPlaybackException.TYPE_RENDERER) {
                Exception cause = e.getRendererException();
                if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                    // Special case for decoder initialization failures.
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                            (MediaCodecRenderer.DecoderInitializationException) cause;
                    if (decoderInitializationException.codecInfo == null) {
                        if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                            errorString = WatchLive.this.getString(R.string.error_querying_decoders);
                        } else if (decoderInitializationException.secureDecoderRequired) {
                            errorString =
                                    WatchLive.this.getString(
                                            R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                        } else {
                            errorString =
                                    WatchLive.this.getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                        }
                    } else {
                        errorString =
                                WatchLive.this.getString(
                                        R.string.error_instantiating_decoder,
                                        decoderInitializationException.codecInfo.name);
                    }
                }
            }
            return Pair.create(0, errorString);
        }
    }
}
