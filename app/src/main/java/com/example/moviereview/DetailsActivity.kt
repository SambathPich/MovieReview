package com.example.moviereview

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.moviereview.Retrofit.GetMoviesInterface
import com.example.moviereview.Retrofit.RetrofitInstance
import com.example.moviereview.Retrofit.Reviews
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class DetailsActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)


        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val imageurl = intent.getStringExtra("imageurl")
        val id = intent.getStringExtra("movieId")
        val title = intent.getStringExtra("title")
        val year = intent.getStringExtra("year")


        val movieThumbnail = findViewById<ImageView>(R.id.movieThumbnail)
        val movieTitle = findViewById<TextView>(R.id.textViewTitle)
        val movieYear = findViewById<TextView>(R.id.textViewYear)
        val movieReviewCaption = findViewById<TextView>(R.id.textViewReviewCaption)
        val movieReview = findViewById<TextView>(R.id.textViewReview)


        movieTitle.text = title
        movieYear.text = "Year: " + year
        Picasso.get()
            .load(imageurl)
            .into(movieThumbnail)


        val fileName = "$id.txt"
        var fileOutputStream: FileOutputStream

        var reviewOrder = 0


        if(isOnline()) {

            val moviesInterface = RetrofitInstance(this).myJSONInfo()!!.create(GetMoviesInterface::class.java)
            val getMovies = moviesInterface?.getMovieReviews(id)
            getMovies?.enqueue(object : Callback<ArrayList<Reviews>> {
                override fun onFailure(call: Call<ArrayList<Reviews>>, t: Throwable) {

                    movieReviewCaption.text = "Reviews (Data From Cache):"
                    movieReview.text = ""

                    var fileInputStream: FileInputStream? = openFileInput(fileName)
                    var inputStreamReader = InputStreamReader(fileInputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var text: String? = null

                    while ({ text = bufferedReader.readLine(); text }() != null) {
                        if(reviewOrder == 0) {
                            if (reviewOrder % 2 == 0) {
                                movieReview.append("+ $text: ")
                            } else {
                                movieReview.append("$text")
                            }
                        } else {
                            if (reviewOrder % 2 == 0) {
                                movieReview.append("\n+ $text: ")
                            } else {
                                movieReview.append("$text")
                            }
                        }

                        reviewOrder++
                    }

                }
                override fun onResponse(call: Call<ArrayList<Reviews>>, response: Response<ArrayList<Reviews>>) {

                    //existedFile("$id.txt")
                    if(baseContext.getFileStreamPath(fileName).exists()) {
                        movieReviewCaption.text = "Reviews (Data From Cache):"
                        movieReview.text = ""

                        var fileInputStream: FileInputStream? = openFileInput(fileName)
                        var inputStreamReader = InputStreamReader(fileInputStream)
                        val bufferedReader = BufferedReader(inputStreamReader)
                        var text: String? = null

                        while ({ text = bufferedReader.readLine(); text }() != null) {
                            if(reviewOrder == 0) {
                                if (reviewOrder % 2 == 0) {
                                    movieReview.append("+ $text: ")
                                } else {
                                    movieReview.append("$text")
                                }
                            } else {
                                if (reviewOrder % 2 == 0) {
                                    movieReview.append("\n+ $text: ")
                                } else {
                                    movieReview.append("$text")
                                }
                            }

                            reviewOrder++
                        }

                    } else {

                        val allMovies = response.body()!!
                        val bundle = Bundle()


                        movieReviewCaption.text = "Reviews (Data From Internet):"
                        movieReview.text = ""

                        fileOutputStream = openFileOutput(fileName, Context.MODE_APPEND)

                        allMovies.reverse()
                        if (allMovies.size > 0) {
                            while (reviewOrder < allMovies.size) {

                                //Google Analytics
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, allMovies[reviewOrder].reviewer)
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, allMovies[reviewOrder].review)
                                mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

                                if (reviewOrder > 0) {
                                    movieReview.append("\n")
                                }
                                movieReview.append("+ " + allMovies[reviewOrder].reviewer + ": " + allMovies[reviewOrder].review)

                                if(!existedReview(fileName, allMovies[allMovies.size-1].review)) {
                                    //Write to Cache
                                    try {
                                        fileOutputStream.write((allMovies[reviewOrder].reviewer + "\n").toByteArray())
                                        fileOutputStream.write((allMovies[reviewOrder].review + "\n").toByteArray())
                                    } catch (e: Exception){
                                        e.printStackTrace()
                                    }
                                }
                                reviewOrder++
                            }
                        }
                        fileOutputStream.close()
                    }
                }
            })


            floatingAddButton.setOnClickListener {
                //Toast.makeText(this, title, Toast.LENGTH_LONG).show()
                val builder = AlertDialog.Builder(this)
                val inflater = layoutInflater
                builder.setTitle("Enter Your Name and Review")
                val dialogLayout = inflater.inflate(R.layout.activity_dialog_add_review, null)
                builder.setView(dialogLayout)

                val reviewer  = dialogLayout.findViewById<EditText>(R.id.editTextName)
                val review  = dialogLayout.findViewById<EditText>(R.id.editTextReview)

                builder.setNegativeButton("Cancel"){_,_ ->

                }

                builder.setPositiveButton("Submit") { _, _ ->

                    //Write to Cache
                    try {
                        fileOutputStream = openFileOutput(fileName, Context.MODE_APPEND)
                        fileOutputStream.write((reviewer.text.toString() + "\n").toByteArray())
                        fileOutputStream.write((review.text.toString() + "\n").toByteArray())

                        movieReview.append("\n+ " + reviewer.text.toString() + ": " + review.text.toString())
                        movieReviewCaption.text = "Reviews (Data From Cache):"
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }

                builder.show()
            }


        } else {

            if(baseContext.getFileStreamPath(fileName).exists()) {
                movieReviewCaption.text = "Reviews (Data From Cache):"
                movieReview.text = ""

                var fileInputStream: FileInputStream? = openFileInput(fileName)
                var inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var text: String? = null

                while ({ text = bufferedReader.readLine(); text }() != null) {
                    if(reviewOrder == 0) {
                        if (reviewOrder % 2 == 0) {
                            movieReview.append("+ $text: ")
                        } else {
                            movieReview.append("$text")
                        }
                    } else {
                        if (reviewOrder % 2 == 0) {
                            movieReview.append("\n+ $text: ")
                        } else {
                            movieReview.append("$text")
                        }
                    }

                    reviewOrder++
                }
            } else {
                movieReviewCaption.text = "Internet Required:"
                movieReview.text = "Please enable Internet to cache reviews"
            }

        }
    }


    fun existedReview( fileName: String,
                       newLastReview: String): Boolean {

        var fileInputStream: FileInputStream? = openFileInput(fileName)
        var inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        var lastLine: String? = ""
        var text: String? = null
        while ({ text = bufferedReader.readLine(); text }() != null) {
            lastLine = text
        }

        if (lastLine == newLastReview) {
            return true
        }
        return false
    }


    fun isOnline(): Boolean {
        val connectivityManager = this.getSystemService((Context.CONNECTIVITY_SERVICE)) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }




}