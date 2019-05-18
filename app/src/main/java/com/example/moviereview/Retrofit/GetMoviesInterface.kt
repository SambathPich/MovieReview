package com.example.moviereview.Retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetMoviesInterface {

    @GET("movies/")
    fun getAllMovies(): Call<ArrayList<Movies>>


    @GET("reviews")
    fun getMovieReviews(@Query("movieId") movieId: String): Call<ArrayList<Reviews>>

}