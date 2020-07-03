package com.muzzlyworld.genimovie.network

import com.muzzlyworld.genimovie.network.model.DetailMovieResponse
import com.muzzlyworld.genimovie.network.model.MovieApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val IMAGE_URL_HEADER = "https://image.tmdb.org/t/p/w500/"

private const val API_MEDIA_TYPE = "movie"
private const val API_TIME_WINDOW = "week"
private const val DEFAULT_DETAIL_APPEND = "credits"

interface MovieApi {

    @GET("trending/$API_MEDIA_TYPE/$API_TIME_WINDOW")
    suspend fun getTrending(@Query("page") p : Int) : Response<MovieApiResponse>

    @GET("search/$API_MEDIA_TYPE/")
    suspend fun searchByQuery(@Query("query") q : String, @Query("page") p : Int) : Response<MovieApiResponse>

    @GET("movie/{movie_id}")
    suspend fun getDetailMovieById(
        @Path("movie_id") movieId: String,
        @Query("append_to_response") append: String = DEFAULT_DETAIL_APPEND
    ) : Response<DetailMovieResponse>
}