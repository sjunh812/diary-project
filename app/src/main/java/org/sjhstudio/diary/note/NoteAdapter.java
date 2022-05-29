package org.sjhstudio.diary.note;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.sjhstudio.diary.R;
import org.sjhstudio.diary.helper.OnNoteItemClickListener;
import org.sjhstudio.diary.helper.OnNoteItemLongClickListener;
import org.sjhstudio.diary.helper.OnNoteItemTouchListener;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder>
        implements OnNoteItemClickListener, OnNoteItemTouchListener, OnNoteItemLongClickListener {

    private final Context mContext;

    private ArrayList<Note> items = new ArrayList<>();
    private OnNoteItemClickListener clickListener;
    private OnNoteItemTouchListener touchListener;
    private OnNoteItemLongClickListener longClickListener;

    private int layoutType = 0;

    public NoteAdapter(Context context) {
        mContext = context;
    }

    public void setItems(ArrayList<Note> items) {
        this.items = items;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    // 즐겨찾기 일기목록
    public void setStar() {
        for(int i = 0; i < items.size(); i++) {
            Note item = items.get(i);

            if(item.getStarIndex() == 0) {
                items.remove(i);
                i--;
            }
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.list_item, parent, false);

        return new NoteViewHolder(itemView, layoutType, mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note item = items.get(position);
        holder.setItem(item);
        holder.setOnItemClickListener(clickListener);
        holder.setOnItemTouchListener(touchListener);
        holder.setOnItemLongClickListener(longClickListener);
        holder.setLayoutType(layoutType);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).get_id();
    }

    public Note getItem(int position) {
        return items.get(position);
    }

    public void setOnItemClickListener(OnNoteItemClickListener listener) {
        clickListener = listener;
    }

    public void setOnItemTouchListener(OnNoteItemTouchListener listener) {
        touchListener = listener;
    }

    public void setOnItemLongClickListener(OnNoteItemLongClickListener listener) {
        longClickListener = listener;
    }

    @Override
    public void onItemClick(NoteViewHolder holder, View view, int position) {
        if(clickListener != null) {
            clickListener.onItemClick(holder, view, position);
        }
    }

    @Override
    public void onItemTouch(NoteViewHolder holder, View view, int position, MotionEvent event) {
        if(touchListener != null) {
            touchListener.onItemTouch(holder, view, position, event);
        }
    }

    @Override
    public void onLongClick(NoteViewHolder holder, View view, int position) {
        if(longClickListener != null) {
            longClickListener.onLongClick(holder, view, position);
        }
    }

}
