package com.ibeis.wildbook.wildbook;

/**
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

import java.util.List;

/**
 * Created by AndroidJSon.com on 6/18/2017.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<Uri> MainImageUploadInfoList;


    public RecyclerViewAdapter(Context context, List<Uri> TempList) {

        this.MainImageUploadInfoList = TempList;

        this.context = context;
        Log.i("RecyclerViewAdapter","constructor!!!"+"TempList.size()="+TempList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(view,context);
        Log.i("RecyclerViewAdapter","ON_create_VIEW_HOLDER!!!");
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Uri UploadInfo = MainImageUploadInfoList.get(position);

       // holder.imageNameTextView.setText(UploadInfo.getImageName());
            Log.i("RecyclerViewAdapter","ON_BIND_VIEW_HOLDER!!!");
        //Loading image from Glide library.
        Glide.with(context).load(UploadInfo).into(holder.imageView);
    }

    @Override
    public int getItemCount() {

        return MainImageUploadInfoList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView;
        public TextView imageNameTextView;
        public Context mContext;

        public ViewHolder(View itemView,Context context) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            mContext = context;
            imageView.setOnClickListener(this);
           // imageNameTextView = (TextView) itemView.findViewById(R.id.ImageNameTextView);
        }
        @Override
        public void onClick(View view){
            Intent intent = new Intent(context,ImageViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri =MainImageUploadInfoList.get(getAdapterPosition());
            intent.putExtra("POS",uri.toString());
            context.startActivity(intent);
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