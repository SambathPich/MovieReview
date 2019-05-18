package com.example.moviereview.RoomDb


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereview.DetailsActivity
import com.example.moviereview.R
import com.squareup.picasso.Picasso

class CustomAdapterRoomDb(val moviesList: List<Movies>) : RecyclerView.Adapter<CustomAdapterRoomDb.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movies: Movies = moviesList[position]
        holder.textViewTitle.text = movies.title
        holder.textViewYear.text = "Year: " + movies.year
        holder.loadImageFromUrl(movies.imageurl)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsActivity::class.java)

            intent.putExtra("movieId", movies.id)
            intent.putExtra("title", movies.title)
            intent.putExtra("year", movies.year)
            intent.putExtra("imageurl", movies.imageurl)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return moviesList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val myImageView = itemView.findViewById(R.id.movieThumbnail) as ImageView
        val textViewTitle = itemView.findViewById(R.id.textViewTitle) as TextView
        val textViewYear = itemView.findViewById(R.id.textViewYear) as TextView

        fun loadImageFromUrl(imageurl: String) {
            Picasso.get()
                .load(imageurl)
                .into(myImageView)
        }
    }
}