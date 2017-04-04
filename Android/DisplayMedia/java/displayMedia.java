package com.YOUR.PACKAGE.NAME;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


/**
 * Created on 2/20/2017.
 */

public class displayMedia extends AppCompatActivity  {


    private ImageView feedPic;
    private ProgressBar feedSpinner;
    private SurfaceView videoSurface;
    private FrameLayout feedMedia, feedPicOptions;
    private String getMedia;
    Context mContext;
    private FeedVideoPlayer videoPlayer;
    private TextView timeUpload;
    private Handler myHandler = new Handler();
    private ImageView playUpload, button_mute;
    private boolean muted;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.display_media);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent in = getIntent();
        Bundle b = in.getExtras();

        String mediaType = null;

        mContext = this;

        feedSpinner = (ProgressBar) findViewById(R.id.progressBar);
        feedMedia = (FrameLayout) findViewById(R.id.feedMedia);
        feedPicOptions = (FrameLayout) findViewById(R.id.feedPicOptions);
        playUpload = (ImageView) findViewById(R.id.displayPlay);
        button_mute = (ImageView) findViewById(R.id.displayMute);
        timeUpload = (TextView) findViewById(R.id.time_current);

        if(b != null) {
			//Conditional for type of media
            mediaType = b.getString("getMediaType");
         
            if (mediaType.equals("IMAGE")) {
                //full string path 
                getMedia = b.getString("getMedia");
            } else if (mediaType.equals("VIDEO")) {
				//full string path 
                getMedia = b.getString("getMedia");
            }
        }
        
        feedPic = (ImageView) findViewById(R.id.feedPic);
        videoSurface = (SurfaceView) findViewById(R.id.feedVideo);


        if(mediaType.equals("VIDEO"))
        {

            feedPic.setVisibility(View.GONE);
            feedPicOptions.setVisibility(View.VISIBLE);
            videoSurface.setVisibility(View.VISIBLE);
            feedSpinner.setVisibility(View.VISIBLE);
            button_mute.setVisibility(View.VISIBLE);
            timeUpload.setVisibility(View.VISIBLE);
            muted = true;

            getSupportActionBar().hide();

            Uri vid = Uri.parse(getMedia);

            myHandler.postDelayed(UpdateVideoTime,100);
            videoPlayer = new FeedVideoPlayer(this, vid, true, false);

            feedMedia.addView(videoPlayer);

            View.OnClickListener videoListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(videoPlayer.checkPlayState())
                    {

                        playUpload.setVisibility(View.VISIBLE);
                        videoPlayer.changePlayState();
						
						/////Plug in your own animation if desired
                        Animation startFadeOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.feed_fade_out);
                        playUpload.startAnimation(startFadeOutAnimation);
						/////////////////////////////
						
                        playUpload.setVisibility(View.GONE);
                        playUpload.setImageResource(R.drawable.ic_media_pause);

                    }
                    else
                    {

                        playUpload.setVisibility(View.VISIBLE);
                        videoPlayer.changePlayState();
                        playUpload.setVisibility(View.GONE);
						
						/////Plug in your own animation if desired
                        Animation startFadeOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.feed_fade_out);
                        playUpload.startAnimation(startFadeOutAnimation);
						/////////////////////////////
						
                        playUpload.setImageResource(R.drawable.ic_media_play);

                    }
                }
            };

            videoSurface.setOnClickListener(videoListener);

            View.OnClickListener muteButtonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(muted)
                    {
                        videoPlayer.mute(muted);
                        button_mute.setImageResource(R.drawable.volume);
                        muted = false;
                    }
                    else
                    {
                        videoPlayer.mute(muted);
                        button_mute.setImageResource(R.drawable.mute);
                        muted = true;
                    }
                }
            };
            button_mute.setOnClickListener(muteButtonListener);

        }


        else if (mediaType.equals("IMAGE")) {

            feedPic.setVisibility(View.VISIBLE);
            videoSurface.setVisibility(View.GONE);

            Uri picUri = Uri.parse(getMedia);

            Picasso.with(mContext)
                    .load(picUri)
                    .resize(6000, 2000)
                    .centerInside()
                    .into(feedPic);

            feedPic.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    finish();
                }
            });
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
        
        if(!(videoPlayer == null)) {
            videoPlayer.onDetachedFromWindow();
        }
        
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (!(videoPlayer == null)) {
            videoPlayer.onDetachedFromWindow();
        }
    }



    private Runnable UpdateVideoTime = new Runnable() {
        public void run() {
            long duration = videoPlayer.getDuration();
            int progress = videoPlayer.getProgress();
            long newposition = (duration * progress) / 1000L;

            if (timeUpload != null) {
                timeUpload.setText(FeedVideoPlayer.stringForTime( progress));
            }
            myHandler.postDelayed(this, 100);
        }
    };

}

