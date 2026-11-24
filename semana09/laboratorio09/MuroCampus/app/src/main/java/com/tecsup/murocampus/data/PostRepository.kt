package com.tecsup.murocampus.data

import com.tecsup.murocampus.data.remote.PostApi
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.EditarTitulo
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException

interface PostRepository {
    suspend fun listar(): Result<List<Post>>
    suspend fun detalle(id: Int): Result<Post>
    suspend fun comentarios(id: Int): Result<List<Comentario>>
    suspend fun porUsuario(userId: Int): Result<List<Post>>
    suspend fun crear(nuevo: NuevoPost): Result<Post>
    suspend fun eliminar(id: Int): Result<Unit>
    suspend fun editarTitulo(id: Int, nuevoTitulo: String): Result<Post>
    suspend fun listarPaginado(start: Int, limit: Int = 20): Result<List<Post>>
}

class PostRepositoryRemote(private val api: PostApi) : PostRepository {
    private suspend fun <T> llamar(bloque: suspend () -> T): Result<T> =
        try {
            Result.success(bloque())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun listar(): Result<List<Post>> = llamar {
        api.listar()
    }

    override suspend fun detalle(id: Int): Result<Post> = llamar {
        api.detalle(id)
    }

    override suspend fun comentarios(id: Int): Result<List<Comentario>> = llamar {
        api.comentarios(id)
    }

    override suspend fun porUsuario(userId: Int): Result<List<Post>> = llamar {
        api.porUsuario(userId)
    }

    override suspend fun crear(nuevo: NuevoPost): Result<Post> = llamar {
        api.crear(nuevo)
    }

    override suspend fun eliminar(id: Int): Result<Unit> = llamar {
        val resp = api.eliminar(id)
        if (resp.isSuccessful) Unit else throw HttpException(resp)
    }

    override suspend fun editarTitulo(id: Int, nuevoTitulo: String): Result<Post> = llamar {
        api.editarTitulo(id, EditarTitulo(nuevoTitulo))
    }

    override suspend fun listarPaginado(start: Int, limit: Int): Result<List<Post>> = llamar {
        api.listarPaginado(start, limit)
    }
}
