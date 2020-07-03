package com.muzzlyworld.genimovie.model

data class Paged<out T>(
    val data: List<T>,

    val page: Int,
    val total: Int
)