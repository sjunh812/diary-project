package org.sjhstudio.diary.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.sjhstudio.diary.PhotoActivity
import org.sjhstudio.diary.R
import org.sjhstudio.diary.helper.WriteFragmentListener

class PhotoAdapter(val context: Context, val fragment: Fragment?): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>(){
    var items: ArrayList<String> = arrayListOf()
    private var listener: WriteFragmentListener? = null

    init {
        if(fragment is WriteFragmentListener) {
            listener = fragment
        }
    }

    inner class PhotoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var container: FrameLayout
        private var photo: ImageView

        init {
            container = itemView.findViewById(R.id.container)
            photo = itemView.findViewById(R.id.photo)
        }

        fun setPhoto(filePath: String) {
            Glide.with(context).load(Uri.parse("file://$filePath")).transform(FitCenter(), RoundedCorners(20)).into(photo)

            photo.setOnClickListener {
                println("click!!")

                if(fragment != null) {
                    listener?.setDialog()
                } else {
                    val intent  = Intent(context, PhotoActivity::class.java);
                    intent.putExtra("picturePath", filePath);
                    context.startActivity(intent);
                }
            }

            photo.setOnLongClickListener(object: View.OnLongClickListener {
                override fun onLongClick(v: View?): Boolean {
                    if(fragment != null) {
                        listener?.setDeletePictureDialog(adapterPosition)
                        return true
                    }
                    return false
                }
            })
        }
    }

    fun addItem(item: String) {
        items.add(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
            R.layout.item_photo, parent, false)

        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.setPhoto(items.get(position))
    }

    override fun getItemCount(): Int {
        return items.size
    }
}