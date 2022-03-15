package org.sjhstudio.diary.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.PhotoView;

import org.sjhstudio.diary.R;

import java.util.ArrayList;

public class PhotoDetailAdapter extends RecyclerView.Adapter<PhotoDetailAdapter.PhotoDetailViewHolder> {

    private Context mContext;
    private ArrayList<String> items = new ArrayList<>();

    public PhotoDetailAdapter(Context context) {
        mContext = context;
    }

    class PhotoDetailViewHolder extends RecyclerView.ViewHolder {
        private final SubsamplingScaleImageView scaleImageView = itemView.findViewById(R.id.scale_image_view);

        public PhotoDetailViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(String data) {
            scaleImageView.setImage(ImageSource.uri(Uri.parse("file://" + data)));
//            photoView.setImageURI(Uri.parse("file://" + data));
        }
    }

    public void setItems(ArrayList<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PhotoDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.item_photo_detail, parent, false);
        return new PhotoDetailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoDetailViewHolder holder, int position) {
        String item = items.get(position);
        holder.setData(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
