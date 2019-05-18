package com.example.moviereview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviereview.Retrofit.CustomAdapterRetrofit
import com.example.moviereview.Retrofit.GetMoviesInterface
import com.example.moviereview.Retrofit.Movies
import com.example.moviereview.Retrofit.RetrofitInstance
import com.example.moviereview.RoomDb.*
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var model: MoviesViewModel
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        // Get the view model
        model = ViewModelProviders.of(this).get(MoviesViewModel::class.java)

        // Specify layout for recycler view
        val linearLayoutManager = LinearLayoutManager(
            this, RecyclerView.VERTICAL,false)
        recyclerView.layoutManager = linearLayoutManager as RecyclerView.LayoutManager?


        val moviesInterface = RetrofitInstance(this).myJSONInfo()!!.create(GetMoviesInterface::class.java)

        val getMovies = moviesInterface?.getAllMovies()
        getMovies?.enqueue(object : Callback<ArrayList<Movies>> {
            override fun onFailure(call: Call<ArrayList<Movies>>, t: Throwable) {

                textViewStatus.text = "Data From Cache"

                //Observe the model
                model.showAllMovies.observe(this@MainActivity, Observer{ movies->
                    // Data bind the recycler view
                    recyclerView.adapter = CustomAdapterRoomDb(movies)
                })
            }

            override fun onResponse(call: Call<ArrayList<Movies>>, response: Response<ArrayList<Movies>>) {

                textViewStatus.text = "Data From Internet (Try turning off)"
                val bundle = Bundle()

                val allMovies = response.body()!!
                doAsync {
                    model.deleteAllRecords()
                    var i = 0
                    while (i < allMovies.size) {
                        //Google Analytic
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, allMovies[i].title)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, allMovies[i].year)
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, allMovies[i].imageurl)
                        mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

                        //model.insert(Student(null, allMovies[i].title))
                        model.insert(Movies(null, allMovies[i].title, allMovies[i].year, allMovies[i].imageurl))
                        i++
                    }
                }

                recyclerView.adapter = CustomAdapterRetrofit(allMovies)
            }
        })
    }
}