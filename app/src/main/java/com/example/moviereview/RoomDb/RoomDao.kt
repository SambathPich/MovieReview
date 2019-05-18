package com.example.moviereview.RoomDb


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MoviesDao{
    @Query("SELECT * FROM tblMovies")
    fun showAllMovies(): LiveData<List<Movies>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movie: Movies)

    @Query("delete from tblMovies")
    fun deleteAll()
}