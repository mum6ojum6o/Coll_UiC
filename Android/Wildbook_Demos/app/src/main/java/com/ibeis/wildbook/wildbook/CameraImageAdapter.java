package com.ibeis.wildbook.wildbook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by Arjan on 10/10/2017.
 */

public class CameraImageAdapter extends RecyclerView.Adapter<CameraImageAdapter.ViewHolder>{  private static final String TAG="CameraImageAdapter";
    private File imagesFile;

    public CameraImageAdapter(File folderFile) {
        Log.i(TAG,folderFile.toString()+"printing Folder File FROM IMAGE_ADAPTER!!!!");
        imagesFile = folderFile;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG,"onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.camera_previews_gallery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i(TAG,"onBindViewHolder");
        BitmapFactory.Options mBitOptions = new BitmapFactory.Options();
        mBitOptions.inScaled=true;
        mBitOptions.inSampleSize=2;

        int size = imagesFile.listFiles().length;
        Log.i(TAG,imagesFile.toString()+" its SIZE="+size+" position="+position);
        Log.i(TAG,"CONSIDERING FILE:"+imagesFile.listFiles()[position].getPath());

        File imageFile = imagesFile.listFiles()[position];
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),mBitOptions);
        holder.getImageView().setImageBitmap(imageBitmap);
    }

    @Override
    public int getItemCount() {
        Log.i(TAG,"getItemCount");
        return imagesFile.listFiles().length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            imageView = (ImageView) view.findViewById(R.id.imageGalleryView);
        }

        public ImageView getImageView() {
            return imageView;
        }
    }
}