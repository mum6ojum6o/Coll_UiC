package com.ibeis.wildbook.wildbook;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/*************************************
 * Created by Arjan on 10/10/2017.
 * Adapter to display previews of images that are to be uploaded.
 **********************************/

public class DispPicAdapter extends RecyclerView.Adapter<DispPicAdapter.ViewHolder> {
    Context context;
    List<Uri> MainImageUploadInfoListUris;
    ArrayList<String> mImagePaths;
    ArrayList<Integer> mSelectedImages;//holds positions of the images selected.
    List<ViewHolder> viewHoldersList;
    boolean mLongClicked;
    public static final String TAG="DispPicAdapter";
    public int mImagesSelectedCount;
    public DispPicAdapter(Context context, List<Uri> TempList,ArrayList<String>imagePaths) {
        this.MainImageUploadInfoListUris = TempList;
        this.mImagePaths = imagePaths;
        this.context = context;
        mLongClicked = false;
        Log.i("RecyclerViewAdapter","constructor!!!"+"TempList.size()="+TempList.size());
        this.mSelectedImages = new ArrayList<Integer>();
        this.mImagesSelectedCount=0;
        viewHoldersList=new ArrayList<>();
    }
    public void setmSelectedImages(ArrayList<Integer> selectedImages){
        mSelectedImages = selectedImages;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        Log.i("RecyclerViewAdapter","ON_create_VIEW_HOLDER!!!");
        viewHoldersList.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i(TAG,"Binding View position="+position);
        Uri UploadInfo = MainImageUploadInfoListUris.get(position);
        BitmapFactory.Options mBitOptions = new BitmapFactory.Options();
        mBitOptions.inScaled=true;
        mBitOptions.inSampleSize=2;
        Log.i("RecyclerViewAdapter","ON_BIND_VIEW_HOLDER!!!");
        //Loading image from Glide library.
        if(context instanceof UploadCamPicsActivity) {
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
        if(!isSelected(position))//is the position is not selected...
            holder.imageView2.setVisibility(View.INVISIBLE);
        else {
            holder.imageView2.setVisibility(View.VISIBLE);
            holder.setImageViewisSelected(true);
            mLongClicked=true;
            mImagesSelectedCount++;
        }
        Glide.with(context).load(R.drawable.notification_sync_complete)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .crossFade()
                .into(holder.imageView2);
        }

    @Override
    public int getItemCount() {
        return MainImageUploadInfoListUris.size();
    }
    public void setmLongClicked(boolean val){
        mLongClicked=val;
    }
    public void updateAllViewHolderVisibilityStatus(boolean val){
        for(ViewHolder vh:viewHoldersList)
            vh.setImageViewisSelected(val);
    }

    /**********************************************
     * mImagesSelectedCount variable helps in keeping track of the images selcted by the user
     * However, during the Select All and Deselect All button click event we need to explicitly
     * set the value of the variable to the total number of ViewHoldersCreated.
     * This method is used to set the mImagesSelectedCount = size of mViewHoldersList.
     * @param count
     *************************************************/
    public void setmImagesSelectedCount(int count){
        mImagesSelectedCount=count;
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        public ImageView imageView,imageView2;
        //public TextView imageNameTextView;
        public boolean imageViewisSelected;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            imageView2.setVisibility(View.INVISIBLE);
            imageView2.setBackgroundColor(itemView.getResources().getColor(R.color.red, null));
            imageView.setOnClickListener(this);
            imageView.setOnLongClickListener(this);
            imageViewisSelected=false;
        }
        public ViewHolder(View itemView, boolean updatedValue){
            super(itemView);
            imageViewisSelected=updatedValue;
        }
        @Override
        public void onClick(View view){
            if(mLongClicked==false) { // if the user has already long-clicked
                if (imageViewisSelected == false) { // the clicked image has not been selected
                    //sets up the image for preview
                    Intent intent = new Intent(context, ImagePreviewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = MainImageUploadInfoListUris.get(getAdapterPosition());
                    String filePath = null;
                    if (mImagePaths.get(getAdapterPosition()) != null)
                        //get the path of the selected image
                        filePath = new File(mImagePaths.get(getAdapterPosition())).getPath();
                    Log.i(TAG, "imagePaths count" + mImagePaths.size());
                    if ("content".equalsIgnoreCase(uri.getScheme())) {
                        intent.putExtra("POS", uri.toString()); // current image..
                        ArrayList<String> uriToStringPaths = new ArrayList<>();
                        for (Uri aUri : MainImageUploadInfoListUris)
                            uriToStringPaths.add(aUri.toString());
                        intent.putStringArrayListExtra("OtherImageUris", uriToStringPaths);
                    } else {
                        intent.putExtra("filePath", filePath); //
                        intent.putStringArrayListExtra("AllFiles", mImagePaths);

                    }
                    intent.putExtra("SelectedFilePosition", getAdapterPosition());
                    intent.putExtra("Adapter", "DispPic");
                    context.startActivity(intent);
                } else {
                    imageView2.setVisibility(View.VISIBLE);
                    imageViewisSelected = true;
                    mImagesSelectedCount++;
                    mSelectedImages.add(getAdapterPosition());
                }
            }
            else if(mLongClicked==true && imageViewisSelected == false){
                imageView2.setVisibility(View.VISIBLE);
                imageViewisSelected = true;
                mImagesSelectedCount++;
                mSelectedImages.add(getAdapterPosition());
            }
            else{
                imageView2.setVisibility(View.INVISIBLE);
                imageViewisSelected=false;
                mImagesSelectedCount--;
                removeFromSelectedPicsCache(getAdapterPosition());

            }
            if(mImagesSelectedCount==0)
                mLongClicked=false;
        }


        @Override
        public boolean onLongClick(View view) {
            if(imageView2.getVisibility()==View.INVISIBLE &&  imageViewisSelected==false ) {
                imageView2.setVisibility(View.VISIBLE);
                imageViewisSelected=true;
                mLongClicked=true;
                mImagesSelectedCount++;
                mSelectedImages.add(getAdapterPosition());

            }
            else if(imageView2.getVisibility()==View.VISIBLE  && imageViewisSelected==true ){
                imageViewisSelected=false;
                Log.i("DispPicAdapter","unselecting "+mImagePaths.get(getAdapterPosition()));
                imageView2.setVisibility(View.INVISIBLE);
                Log.i("DispPicAdapter", "You selected image " + getAdapterPosition());
                removeFromSelectedPicsCache(getAdapterPosition());
            }
            else if(imageView2.getVisibility()==View.VISIBLE && imageViewisSelected==false ){
                imageView2.setVisibility(View.INVISIBLE);
                removeFromSelectedPicsCache(getAdapterPosition());
            }
            else if(imageView2.getVisibility()==View.INVISIBLE && imageViewisSelected==true ){
                imageView2.setVisibility(View.VISIBLE);
                mSelectedImages.add(getAdapterPosition());
            }
            return true;
        }
        public void setImageViewisSelected(boolean val){
            imageViewisSelected=val;
        }

    }
    //puts the list of integers mSelectedImages to the bundle.
    public void onSaveState(Bundle b){
        b.putIntegerArrayList("SELECTED_IMAGES",mSelectedImages);
    }

    public void removeFromSelectedPicsCache(int position){
        for(int i=0;i<mSelectedImages.size();i++){
            if(mSelectedImages.get(i)==position)
                mSelectedImages.remove(i);
        }
    }
    public boolean isSelected(int position) {
        for (int i = 0; i < mSelectedImages.size(); i++)
            if (mSelectedImages.get(i) == position) return true;

        return false;
    }
}
