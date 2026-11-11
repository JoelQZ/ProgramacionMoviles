package com.tecsup.catalogo.data.remote

import com.tecsup.catalogo.data.remote.dto.ItemDto
import com.tecsup.catalogo.data.remote.dto.ProductosRespuesta
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("products/category/smartphones")
    suspend fun listar(): ProductosRespuesta

    //@GET("products/category/smartphones")
    //suspend fun listar(@Query("limit") limit: Int = 10): ProductosRespuesta

    @GET("products/{id}")
    suspend fun detalle(@Path("id") id: Int): ItemDto
}
