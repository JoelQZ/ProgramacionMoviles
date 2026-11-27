package com.tecsup.avisoscampus.data

import android.content.Context
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AvisosRepositoryPersistente(context: Context) : AvisosRepository {
    private val prefs = context.getSharedPreferences("avisos_prefs", Context.MODE_PRIVATE)
    private val _avisos = MutableStateFlow<List<Aviso>>(emptyList())
    override val avisos: StateFlow<List<Aviso>> = _avisos.asStateFlow()

    private var contadorId = 1
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        cargarAvisos()
    }

    private fun cargarAvisos() {
        val jsonString = prefs.getString("clave_avisos", null) ?: return
        try {
            val jsonArray = JSONArray(jsonString)
            val lista = mutableListOf<Aviso>()
            var maxId = 0
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getInt("id")
                val titulo = obj.getString("titulo")
                val texto = obj.getString("texto")
                val origenStr = obj.getString("origen")
                val origen = try { Origen.valueOf(origenStr) } catch (e: Exception) { Origen.BOTON }
                val hora = obj.getString("hora")
                val esUrgente = obj.optBoolean("esUrgente", false)

                if (id > maxId) {
                    maxId = id
                }
                lista.add(Aviso(id, titulo, texto, origen, hora, esUrgente))
            }
            contadorId = maxId + 1
            _avisos.value = lista
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun guardarAvisos(lista: List<Aviso>) {
        try {
            val jsonArray = JSONArray()
            for (aviso in lista) {
                val obj = JSONObject().apply {
                    put("id", aviso.id)
                    put("titulo", aviso.titulo)
                    put("texto", aviso.texto)
                    put("origen", aviso.origen.name)
                    put("hora", aviso.hora)
                    put("esUrgente", aviso.esUrgente)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString("clave_avisos", jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun registrar(titulo: String, texto: String, origen: Origen, esUrgente: Boolean): Aviso {
        val horaActual = dateFormat.format(Date())
        val nuevoAviso = Aviso(
            id = contadorId++,
            titulo = titulo,
            texto = texto,
            origen = origen,
            hora = horaActual,
            esUrgente = esUrgente
        )
        val nuevaLista = listOf(nuevoAviso) + _avisos.value
        _avisos.value = nuevaLista
        guardarAvisos(nuevaLista)
        return nuevoAviso
    }
}
