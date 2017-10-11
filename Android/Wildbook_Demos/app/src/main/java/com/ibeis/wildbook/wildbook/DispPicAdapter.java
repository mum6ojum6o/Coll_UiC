package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by Arjan on 10/10/2017.
 */


public class DispPicAdapter extends RecyclerView.Adapter<DispPicAdapter.ViewHolder> {

    Context context;
    List<Uri> MainImageUploadInfoList;
    List<String> testImageNames;

    public DispPicAdapter(Context context, List<Uri> TempList,List<String>imagePaths) {

        this.MainImageUploadInfoList = TempList;
        this.testImageNames = imagePaths;
        this.context = context;
        Log.i("RecyclerViewAdapter","constructor!!!"+"TempList.size()="+TempList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        Log.i("RecyclerViewAdapter","ON_create_VIEW_HOLDER!!!");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri UploadInfo = MainImageUploadInfoList.get(position);
        String path = testImageNames.get(position);
        BitmapFactory.Options mBitOptions = new BitmapFactory.Options();
        mBitOptions.inScaled=true;
        mBitOptions.inSampleSize=2;
        // holder.imageNameTextView.setText(UploadInfo.getImageName());
        Log.i("RecyclerViewAdapter","ON_BIND_VIEW_HOLDER!!!");
        //Loading image from Glide library.
        //Glide.with(context).load(UploadInfo).into(holder.imageView);
        File imageFile = new File(path);
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),mBitOptions);
        holder.getImageView().setImageBitmap(imageBitmap);
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView imageNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            // imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
        }
        public ImageView getImageView() {
            return imageView;
        }
    }
}
