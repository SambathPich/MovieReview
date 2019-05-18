package com.example.moviereview.RoomDb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tblMovies")
data class Movies(

    @PrimaryKey var id:Long?,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "year") var year: String,
    @ColumnInfo(name = "imageurl") var imageurl: String
)