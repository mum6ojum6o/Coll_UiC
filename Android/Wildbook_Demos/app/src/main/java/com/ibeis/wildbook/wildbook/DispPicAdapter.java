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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arjan on 10/10/2017.
 */


public class DispPicAdapter extends RecyclerView.Adapter<DispPicAdapter.ViewHolder> {

    Context context;
    List<Uri> MainImageUploadInfoList;
    List<String> mImagePaths;
    List<Integer> mSelectedImages;//holds positions of the images selected.
    boolean longClicked;

    public DispPicAdapter(Context context, List<Uri> TempList,List<String>imagePaths) {

        this.MainImageUploadInfoList = TempList;
        this.mImagePaths = imagePaths;
        this.context = context;
        longClicked = false;
        Log.i("RecyclerViewAdapter","constructor!!!"+"TempList.size()="+TempList.size());
        this.mSelectedImages = new ArrayList<Integer>();
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
        Glide.with(context).load(R.drawable.notification_sync_complete)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .crossFade()
                .into(holder.imageView2);
        /*File imageFile = new File(path);
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),mBitOptions);
        holder.getImageView().setImageBitmap(imageBitmap);*/
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }

    public ArrayList<Integer> getSelectedImages(){
        return (ArrayList)mSelectedImages;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        public ImageView imageView,imageView2;
        public TextView imageNameTextView;
        public boolean imageViewisSelected;
        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            imageView2.setVisibility(View.VISIBLE);
            imageView2.setBackgroundColor(itemView.getResources().getColor(R.color.red, null));
            imageView.setOnClickListener(this);
            imageView.setOnLongClickListener(this);
            imageViewisSelected=true;
            // imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
        }
        @Override
        public void onClick(View view){
            if(imageViewisSelected==false) {
                Intent intent = new Intent(context, ImageViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = MainImageUploadInfoList.get(getAdapterPosition());
                //imageView2.setVisibility(View.VISIBLE);

                //Uri uri = Uri.parse(new File(testImageNames.get(getAdapterPosition())).getPath());
                String filePath = null;
                if (mImagePaths.get(getAdapterPosition()) != null)
                    filePath = new File(mImagePaths.get(getAdapterPosition())).getPath();
                if ("content".equalsIgnoreCase(uri.getScheme()))
                    intent.putExtra("POS", uri.toString());
                else
                    intent.putExtra("filePath", filePath);
                intent.putExtra("Adapter", "DispPic");
                context.startActivity(intent);
            }
            else{
                imageView2.setVisibility(View.VISIBLE);
                imageViewisSelected=false;
            }
        }
        public ImageView getImageView() {
            return imageView;
        }

        @Override
        public boolean onLongClick(View view) {
            if(imageViewisSelected==false ) {
                imageView2.setVisibility(View.VISIBLE);
                imageViewisSelected=true;
                /*mSelectedImages.add(getAdapterPosition());
                Log.i("DispPicAdapter","Selecting "+mImagePaths.get(getAdapterPosition()));
                imageViewisSelected = false;*/



            }
            else if(imageViewisSelected==true ){
                imageViewisSelected=false;
                Log.i("DispPicAdapter","unselecting "+mImagePaths.get(getAdapterPosition()));
               // mSelectedImages.remove(mSelectedImages.indexOf(getAdapterPosition()));
                //imageView2.setVisibility(View.VISIBLE);
                imageView2.setVisibility(View.INVISIBLE);
                Log.i("DispPicAdapter", "You selected image " + getAdapterPosition());
            }
            return true;
        }


    }

}
