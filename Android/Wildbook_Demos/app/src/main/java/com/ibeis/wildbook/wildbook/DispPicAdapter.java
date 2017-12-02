package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.List;

/**
 * Created by Arjan on 10/10/2017.
 */


public class DispPicAdapter extends RecyclerView.Adapter<DispPicAdapter.ViewHolder> {

    Context context;
    List<Uri> MainImageUploadInfoList;
    List<String> mImagePaths;

    public DispPicAdapter(Context context, List<Uri> TempList,List<String>imagePaths) {

        this.MainImageUploadInfoList = TempList;
        this.mImagePaths = imagePaths;
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

      //  String path = testImageNames.get(position);
        BitmapFactory.Options mBitOptions = new BitmapFactory.Options();
        mBitOptions.inScaled=true;
        mBitOptions.inSampleSize=2;
        // holder.imageNameTextView.setText(UploadInfo.getImageName());
        Log.i("RecyclerViewAdapter","ON_BIND_VIEW_HOLDER!!!");
        //Loading image from Glide library.
        if(context instanceof UploadCamPics) {
        Glide.with(context).load(Uri.fromFile(new File(UploadInfo.getPath())))
                .override(100,100)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .crossFade()

                .into(holder.imageView);
        }
        else {
            Glide.with(context).load(UploadInfo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .crossFade()
                    .into(holder.imageView);
        }
        /*File imageFile = new File(path);
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),mBitOptions);
        holder.getImageView().setImageBitmap(imageBitmap);*/
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView;
        public TextView imageNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setOnClickListener(this);

            // imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
        }
        @Override
        public void onClick(View view){
            Intent intent = new Intent(context,ImageViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri =MainImageUploadInfoList.get(getAdapterPosition());
            //Uri uri = Uri.parse(new File(testImageNames.get(getAdapterPosition())).getPath());
            String filePath=null;
            if(mImagePaths.get(getAdapterPosition())!=null)
                filePath = new File(mImagePaths.get(getAdapterPosition())).getPath();
            if("content".equalsIgnoreCase(uri.getScheme()))
                intent.putExtra("POS",uri.toString());
            else
                intent.putExtra("filePath",filePath);
            intent.putExtra("Adapter","DispPic");
            context.startActivity(intent);
        }
        public ImageView getImageView() {
            return imageView;
        }
    }
}
