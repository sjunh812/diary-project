package org.sjhstudio.diary.adapters

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.sjhstudio.diary.PhotoActivity
import org.sjhstudio.diary.R
import org.sjhstudio.diary.helper.WriteFragmentListener
import java.io.File

class PhotoAdapter(val context: Context, val fragment: Fragment?) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    var items: ArrayList<String> = arrayListOf()
    private var listener: WriteFragmentListener? = null

    init {
        if (fragment is WriteFragmentListener) listener = fragment
    }

    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivPhoto: ImageView = itemView.findViewById(R.id.photo)

        init {
            ivPhoto.setOnClickListener {
                if (fragment != null) {
                    listener?.showAddPhotoDialog()
                } else {
                    val intent = Intent(context, PhotoActivity::class.java)
                    intent.putExtra("position", adapterPosition)
                    intent.putExtra("picturePaths", items)
                    context.startActivity(intent)
                }
            }

            ivPhoto.setOnLongClickListener {
                if (fragment != null) {
                    listener?.showDeletePictureDialog(adapterPosition)
                    true
                } else {
                    false
                }
            }
        }

        fun setPhoto(filePath: String) {
            val file = File(filePath)
            val bitmap =
                getRoundedCornerBitmap(BitmapFactory.decodeFile(file.absolutePath) ?: return)

            bitmap?.let { ivPhoto.setImageBitmap(it) }
        }

        private fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap? {
            val output = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = 30f

            canvas.drawARGB(0, 0, 0, 0)
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.isAntiAlias = true
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)

            return output
        }
    }

    fun addItem(item: String) {
        items.add(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val itemView =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.item_photo, parent, false
            )

        return PhotoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.setPhoto(items[position])
    }

    override fun getItemCount() = items.size
}