package com.example.moviereview.RoomDb

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import android.app.Application


class MoviesViewModel(application:Application): AndroidViewModel(application){
    private val db:RoomSingleton = RoomSingleton.getInstance(application)
    internal val showAllMovies : LiveData<List<Movies>> = db.moviesDao().showAllMovies()

    fun insert(movie:Movies){
        db.moviesDao().insert(movie)
    }

    fun deleteAllRecords() {
        db.moviesDao().deleteAll()
    }
}