package com.ibeis.wildbook.wildbook;

/**
 *
 * Used to display the contributions
 * Created by Arjan on 9/25/2017.
 */

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;


       import android.content.Context;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context mContext;
    ArrayList<Uri> mMainImageUploadInfoList;
    JSONArray mJsonArray;
    private static final String  TAG="RecyclerViewAdapter";
    public RecyclerViewAdapter(Context context, ArrayList<Uri> TempList) {

        this.mMainImageUploadInfoList = TempList;

        this.mContext = context;
        Log.i("RecyclerViewAdapter","constructor!!!"+"TempList.size()="+TempList.size());
    }
    public RecyclerViewAdapter(Context context, JSONArray jsonArray){
        this.mContext =context;
        this.mJsonArray=jsonArray;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view,mContext);
        viewHolder.imageView3_identified.setVisibility(View.INVISIBLE);
        Log.i("RecyclerViewAdapter","ON_create_VIEW_HOLDER!!!");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView3_identified.setVisibility(View.INVISIBLE);
        if(mMainImageUploadInfoList!=null) {
            Uri UploadInfo = mMainImageUploadInfoList.get(position);
            // holder.imageNameTextView.setText(UploadInfo.getImageName());
            Log.i("RecyclerViewAdapter", "ON_BIND_VIEW_HOLDER!!!");
            //Loading image from Glide library.
            Glide.with(mContext)
                    .load(UploadInfo)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imageView);
        }
        if(mJsonArray!=null){

            try {
                Uri uploadInfo = Uri.parse(mJsonArray.getJSONObject(position).getString("thumbnailUrl"));
                if(mJsonArray.getJSONObject(position).has("individualId")) {
                    Log.i(TAG,"identified:"+mJsonArray.getJSONObject(position).getString("individualId"));
                    holder.imageView3_identified.setVisibility(View.VISIBLE);
                    //holder.imageView3_identified.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_tag,null));
                    Glide.with(mContext)
                            .load(R.mipmap.ic_launcher_foreground)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.imageView3_identified);
                }
                Glide.with(mContext)
                        .load(uploadInfo)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
            if(mMainImageUploadInfoList!=null) {
                return mMainImageUploadInfoList.size();
            }
            else{
                return mJsonArray.length();
            }
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView,imageView2,imageView3_identified;
        public TextView imageNameTextView;
        public Context mContext;

        public ViewHolder(View itemView,Context context) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView2 = (ImageView)itemView.findViewById(R.id.imageView2);
            imageView3_identified = (ImageView)itemView.findViewById(R.id.imageView3_positive_ID);
            imageView2.setVisibility(View.INVISIBLE);
            imageView3_identified.setVisibility(View.INVISIBLE);
            mContext = context;
            imageView.setOnClickListener(this);
           // imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
        }
        @Override
        public void onClick(View view){
            Intent intent = new Intent(mContext,ImageViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(mJsonArray==null) {
                Uri uri = mMainImageUploadInfoList.get(getAdapterPosition());
                intent.putExtra("POS", uri.toString()); // uri of the selected image.
                //identifier to distinguish the request for preview is related to User Contribution.
                intent.putExtra("Adapter", "RecyclerView");
                ArrayList<String> uriToString = new ArrayList<>();
                for(Uri aUri:mMainImageUploadInfoList)
                    uriToString.add(aUri.toString());
                intent.putExtra("SelectedFilePosition",getAdapterPosition());
                intent.putStringArrayListExtra("OtherImageUris",uriToString);
                mContext.startActivity(intent);
            }
            else{

                try {

                    JSONObject jsonObject = (mJsonArray.getJSONObject(getAdapterPosition()));
                    //JSONArray encounterImages = jsonObject.getJSONArray("assets");
                    ArrayList<String> imageUris = new ArrayList<String>();
                    for(int i=0;i<jsonObject.getJSONArray("assets").length();i++){
                        String aUri = jsonObject.getJSONArray("assets").getJSONObject(i).getString("url");
                        imageUris.add(aUri);
                    }
                    intent.putStringArrayListExtra("assets",imageUris);
                    String date=null,longitude=null,lat=null,encounterId = null,individualId=null;

                    if (jsonObject.has("date"))
                        date = jsonObject.get("date").toString();
                    if (jsonObject.has("decimalLongitude"))
                        longitude=jsonObject.get("decimalLongitude").toString();
                    if (jsonObject.has("decimalLatitude"))
                        lat=jsonObject.get("decimalLatitude").toString();
                    if (jsonObject.has("catalogNumber"))
                        encounterId=jsonObject.get("catalogNumber").toString();
                    if(jsonObject.has("individualId")) {
                        individualId = jsonObject.get("individualId").toString();
                        intent.putExtra("individualId",individualId);
                    }
                    intent.putExtra("encounter_date",date);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("latitude",lat);
                    intent.putExtra("encounterId",encounterId);
                    mContext.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
           /* Glide
                    .with(mContext)
                    .load(uri) // the uri you got from Firebase
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .crossFade()
                    .into(imageView);*/
        }

    }
}