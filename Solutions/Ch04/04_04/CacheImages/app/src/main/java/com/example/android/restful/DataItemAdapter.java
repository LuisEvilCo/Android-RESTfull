package com.example.android.restful;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.restful.model.DataItem;
import com.example.android.restful.utils.ImageCacheManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataItemAdapter extends RecyclerView.Adapter<DataItemAdapter.ViewHolder> {

    private static final String TAG = "DataItemAdapter";
    public static final String ITEM_ID_KEY = "item_id_key";
    public static final String ITEM_KEY = "item_key";
    private List<DataItem> mItems;
//    private Map<String, Bitmap> mBitmaps = new HashMap<>();
    private Context mContext;
    private SharedPreferences.OnSharedPreferenceChangeListener prefsListener;

    public DataItemAdapter(Context context, List<DataItem> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public DataItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                Log.i("preferences", "onSharedPreferenceChanged: " + key);
            }
        };
        settings.registerOnSharedPreferenceChangeListener(prefsListener);

        boolean grid = settings.getBoolean(
                mContext.getString(R.string.pref_display_grid), false);
        int layoutId = grid ? R.layout.grid_item : R.layout.list_item;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DataItemAdapter.ViewHolder holder, int position) {
        final DataItem item = mItems.get(position);

        try {
            holder.tvName.setText(item.getItemName());
            //display image
            Bitmap bitmap = ImageCacheManager.getBitmap(mContext, item);
            if (bitmap == null) {
                ImageDownloadTask task = new ImageDownloadTask();
                task.setViewHolder(holder);
                task.execute(item);
            } else {
                holder.imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra(ITEM_KEY, item);
                mContext.startActivity(intent);
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "You long clicked " + item.getItemName(),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public ImageView imageView;
        public View mView;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.itemNameText);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            mView = itemView;
        }
    }

    private class ImageDownloadTask extends AsyncTask<DataItem, Void, Bitmap> {
        private static final String PHOTOS_BASE_URL =
                "http://560057.youcanlearnit.net/services/images/";
        private DataItem mDataItem;
        private ViewHolder mHolder;

        public void setViewHolder(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        protected Bitmap doInBackground(DataItem... dataItems) {

            mDataItem = dataItems[0];
            String imageUrl = PHOTOS_BASE_URL + mDataItem.getImage();
            InputStream in = null;

            try {
                in = (InputStream) new URL(imageUrl).getContent();
                return BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mHolder.imageView.setImageBitmap(bitmap);
            ImageCacheManager.putBitmap(mContext, mDataItem, bitmap);
        }
    }
}