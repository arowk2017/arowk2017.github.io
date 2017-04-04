package com.YOUR.PACKAGE.NAME;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import java.io.IOException;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created on 2/25/2017.
 */

public class FeedVideoPlayer extends TextureView implements TextureView.SurfaceTextureListener {

    private static String TAG = "VideoPlayer";
    private MediaPlayer mMediaPlayer;
    private Uri mSource;
    private MediaPlayer.OnCompletionListener mCompletionListener;
    private boolean isLooping, isMuted;
    static StringBuilder mFormatBuilder;
    static Formatter mFormatter;

    public FeedVideoPlayer(Context context, Uri mSource) {
        super(context);
        this.mSource = mSource;
        setSurfaceTextureListener(this);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        isLooping = true;
        isMuted = true;
    }



    public FeedVideoPlayer(Context context, Uri mSource, boolean freeze) {
        super(context);
        this.mSource = mSource;
        setSurfaceTextureListener(this);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        isMuted = false;
       isLooping = freeze;
    }

    public FeedVideoPlayer(Context context, Uri mSource, boolean freeze, boolean muted) {
        super(context);
        this.mSource = mSource;
        setSurfaceTextureListener(this);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        isMuted = muted;
        isLooping = freeze;
    }

    public FeedVideoPlayer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FeedVideoPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSurfaceTextureListener(this);
    }

    public void setSource(Uri source) {
        mSource = source;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        mCompletionListener = listener;
    }

    public void setLooping(boolean looping) {
        isLooping = looping;
    }

    @Override
    protected void onDetachedFromWindow() {
        
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        super.onDetachedFromWindow();
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setLooping(isLooping);
            mMediaPlayer.setDataSource(getContext(), mSource);
            mMediaPlayer.setSurface(surface);
			//Loads video asynchroniously
            mMediaPlayer.prepareAsync();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mPlayer) {
                    if(isMuted)
                    {
                        mMediaPlayer.setVolume(0f, 0f);
                    }
                    else
                    {
                        mMediaPlayer.setVolume(1f, 1f);
                    }

                    mMediaPlayer.start();
                    if(!isLooping)
                    {
                        mMediaPlayer.pause();
                    }
                }
            });



        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            mMediaPlayer.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        surface.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public boolean startPlay()
    {
        if(mMediaPlayer!=null)
            if(!mMediaPlayer.isPlaying())
            {
                mMediaPlayer.start();
                return true;
            }

        return false;
    }

    public void pausePlay()
    {
        if(mMediaPlayer!=null)
            mMediaPlayer.pause();
    }

    public void stopPlay()
    {
        if(mMediaPlayer!=null)
            mMediaPlayer.stop();
    }

    public void changePlayState()
    {
        if(mMediaPlayer!=null)
        {
            if(mMediaPlayer.isPlaying())
                mMediaPlayer.pause();
            else
                mMediaPlayer.start();
        }

    }


    public void mute(Boolean muted)
    {
        if(mMediaPlayer!=null) {

            if(!muted)
            {
                mMediaPlayer.setVolume(0f, 0f);
            }
            else
            {
                mMediaPlayer.setVolume(1f, 1f);

            }

        }
    }

    public boolean checkPlayState()
    {
        if(mMediaPlayer!=null)
        {
            return mMediaPlayer.isPlaying();
        }
        else
        {
            return false;
        }

    }

    public int getProgress() {
        if (mMediaPlayer == null) {
            return 0;
        }

        int position = mMediaPlayer.getCurrentPosition();


        return position;
    }

    public int getDuration() {
        if (mMediaPlayer == null) {
            return 0;
        }

        int duration = mMediaPlayer.getDuration();


        return duration;
    }

   //Converts millisecond time lapse to seconds, minutes, hours
    public static String stringForTime(int millis) {
 
        final int HOUR = 60*60*1000;
        final int MINUTE = 60*1000;
        final int SECOND = 1000;

        int curVolume  = millis;

        int hours = curVolume/HOUR;
        int minutes = (curVolume%HOUR)/MINUTE;
        int seconds = (curVolume%MINUTE)/SECOND;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

}