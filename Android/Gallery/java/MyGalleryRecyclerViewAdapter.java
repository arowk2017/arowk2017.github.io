package com.YOUR.PACKAGE.NAME;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;

/**
 * Created on 3/13/2017.
 */


public class MyGalleryRecyclerViewAdapter extends RecyclerView.Adapter<MyGalleryRecyclerViewAdapter.CustomViewHolder> {
    
    private Context mContext;
    private ArrayList<String> objects, objects1, objects2;
    private OnItemClickListener onItemClickListener;
    private boolean muted = true;
    private int feedHeight, laughHeight, laughWidth, feedMargin, feedMarginLabel,profileHeight;
    private Bundle b;
    private LinearLayout.LayoutParams params;
    public static String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA };
    public static String[] mediaColumns = { MediaStore.Video.Media._ID };
    private static final String SELECTION = MediaStore.MediaColumns.DATA + "=?";
    private static final String[] PROJECTION = { BaseColumns._ID };
    private CallbackInterface mCallback;

    public interface CallbackInterface{

        /**
         * Callback invoked when clicked
         * @param position - the position
         * @param text - the text to pass back
         */
        void onHandleSelection(int position, String text);
    }

    public MyGalleryRecyclerViewAdapter(Context context, ArrayList<String> objects, ArrayList<String> objects1, ArrayList<String> objects2) {
        this.objects = objects;
        this.objects1 = objects1;
        this.objects2 = objects2;
        this.mContext = context;
        feedHeight = (int)mContext.getResources().getDimension(R.dimen.video_feed_height);
        laughHeight = (int)mContext.getResources().getDimension(R.dimen.laugh_feed_height);
        laughWidth = (int)mContext.getResources().getDimension(R.dimen.laugh_feed_width);
        feedMargin = (int)mContext.getResources().getDimension(R.dimen.feed_margin);
        feedMarginLabel = (int)mContext.getResources().getDimension(R.dimen.feed_margin_label);

        profileHeight = (int)mContext.getResources().getDimension(R.dimen.profile_grid_height);
        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, feedHeight);
        b = new Bundle();

     
        try{
            mCallback = (CallbackInterface) context;
        }catch(ClassCastException ex){
            
            Log.e("MyAdapter","Must implement the CallbackInterface in the Activity", ex);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_gallery_recyclerview_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        
        final String mediaItem = objects.get(i);

        //IMAGE
        if(objects1.get(i).equals("1")) {
            customViewHolder.gallery_play.setVisibility(View.GONE);
            Uri uri = Uri.fromFile(new File(objects.get(i)));
            Picasso.with(mContext)
                    .load(uri)
                    .fit()
                    .centerCrop()
                    .into(customViewHolder.gallery_pic);
            final String picPath = objects.get(i);
            final int pos = i;
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(mCallback != null){
                        mCallback.onHandleSelection(pos, picPath);
                    }
                    
                }
            };
            customViewHolder.gallery_pic.setOnClickListener(listener);
        }
        //////////////VIDEO
        else if (objects1.get(i).equals("3"))
        {
            customViewHolder.gallery_play.setVisibility(View.VISIBLE);

            String[] proj1 = {

                    MediaStore.Video.Thumbnails.DATA
            };

            String sel1 = MediaStore.Video.Thumbnails.VIDEO_ID + " = ?";
            String [] sel2 = new String[]{ objects2.get(i) };
            Cursor thumbCursor = mContext.getContentResolver().query(
                    MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, proj1, sel1, sel2, null);

            final String vidPath = objects.get(i);
            if(thumbCursor.moveToFirst()) {
                int actual_video_thumb_column_index = thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
                final String thumbPath = thumbCursor.getString(actual_video_thumb_column_index);

                Uri videoUri = Uri.fromFile(new File(thumbPath));

                Picasso.with(mContext)
                        .load(videoUri)
                        .fit()
                        .centerCrop()
                        .into(customViewHolder.gallery_pic);

            }
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
					//Handle when item is clicked.  Send results to your next intent
                    Intent intent = new Intent(mContext, UploadMedia.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    b.putString("getMedia", vidPath);
                    b.putString("getType", "VIDEO");
                    String filename = vidPath.substring(vidPath.lastIndexOf("/") + 1);
                    int pos = filename.lastIndexOf(".");
                    if (pos > 0) {
                        filename = filename.substring(0, pos);
                    }
                    b.putString("getImageName", filename);

                    intent.putExtras(b);

                    mContext.startActivity(intent);
                    
                }
            };
            customViewHolder.gallery_pic.setOnClickListener(listener);
        }
    }

    @Override
    public int getItemCount() {
        return (null != objects ? objects.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView gallery_pic,gallery_play;
        protected FrameLayout layout;

        public CustomViewHolder(View view) {
            super(view);

            this.gallery_pic = (ImageView) view.findViewById(R.id.galleryGridPic);
            this.layout = (FrameLayout) view.findViewById(R.id.galleryGridContainer);
            this.gallery_play = (ImageView) view.findViewById(R.id.galleryPlay);

        }
    }



}

