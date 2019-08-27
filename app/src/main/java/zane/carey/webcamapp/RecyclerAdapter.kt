package zane.carey.webcamapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_listitem.view.*

class RecyclerAdapter(val cams: ArrayList<WebCam>, val context: Context) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_listitem, p0, false))
    }

    override fun getItemCount(): Int {
        return cams.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.camTitle.text = cams[p1].title
        Glide.with(context)
            .asBitmap()
            .load(cams[p1].thumbPic)
            .into(p0.camThumb)

        p0.cardView.setOnClickListener{
            val intent = Intent(context, CamDisplayActivity::class.java)
            intent.putExtra("camID", cams[p1].id)
            context.startActivity(intent)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val camThumb = view.thumbPic
        val camTitle = view.camTitle
        val cardView = view.listItemCardView
    }
}