package com.carlosflores.traductorml

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.carlosflores.traductorml.Modelo.Idioma
import com.google.android.material.button.MaterialButton
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var et_idioma_origen: EditText
    private lateinit var tv_idioma_destino: TextView
    private lateinit var btn_elegir_idioma: MaterialButton
    private lateinit var btn_idioma_elegido: MaterialButton
    private lateinit var btn_traducir: MaterialButton

    private var idiomaArrayList: ArrayList<Idioma>? = null

    companion object {
        private const val REGISTRO = "Mis_registros"
    }

    private var codigo_idioma_origen = "es"
    private var titulo_idioma_origen = "Español"

    private var codigo_idioma_destino = "en"
    private var titulo_idioma_destino = "Inglés"

    private lateinit var translateOptions: TranslatorOptions
    private lateinit var translator: Translator
    private lateinit var progressDialog: ProgressDialog

    private var texto_idioma_origen = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializarVistas()
        idiomasDisponibles()

        btn_elegir_idioma.setOnClickListener {
            //Toast.makeText(applicationContext, "Elegir idioma", Toast.LENGTH_SHORT).show()
            elegirIdiomaOrigen()
        }

        btn_idioma_elegido.setOnClickListener {
            //Toast.makeText(applicationContext, "Idioma elegido", Toast.LENGTH_SHORT).show()
            elegirIdiomaDestino()
        }

        btn_traducir.setOnClickListener {
            //Toast.makeText(applicationContext, "Traducir", Toast.LENGTH_SHORT).show()
            validarDatos()

        }
    }

    private fun inicializarVistas() {
        et_idioma_origen = findViewById(R.id.et_idioma_origen)
        tv_idioma_destino = findViewById(R.id.tv_idioma_destino)
        btn_elegir_idioma = findViewById(R.id.btn_elegir_idioma)
        btn_idioma_elegido = findViewById(R.id.btn_idioma_elegido)
        btn_traducir = findViewById(R.id.btn_traducir)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Por favor espere")
        progressDialog.setCanceledOnTouchOutside(false)
    }

    private fun idiomasDisponibles() {
        idiomaArrayList = ArrayList()

        val listaCodigoIdiomas = TranslateLanguage.getAllLanguages()

        for (codigo_lenguaje in listaCodigoIdiomas) {
            val titulo_lenguaje = Locale(codigo_lenguaje).displayLanguage
            val modeloIdioma = Idioma(codigo_lenguaje, titulo_lenguaje)

//            Log.d(REGISTRO, "idiomasDisponibles: codigo_lenguaje = $codigo_lenguaje")
//            Log.d(REGISTRO, "idiomasDisponibles: titulo_lenguaje = $titulo_lenguaje")

            idiomaArrayList!!.add(modeloIdioma)
        }
    }

    private fun elegirIdiomaOrigen() {
        val popUpMenu = PopupMenu(this, btn_elegir_idioma)

        for (i in idiomaArrayList!!.indices) {
            popUpMenu.menu.add(Menu.NONE, i, i, idiomaArrayList!![i].titulo_idioma)
        }

        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener { menuItem ->
            val posicion = menuItem.itemId
            codigo_idioma_origen = idiomaArrayList!![posicion].codigo_idioma
            titulo_idioma_origen = idiomaArrayList!![posicion].titulo_idioma

            btn_elegir_idioma.text = titulo_idioma_origen
            et_idioma_origen.hint = "Ingrese texto en $titulo_idioma_origen"

            Log.d(REGISTRO, "elegirIdiomaOrigen: codigo_idioma_origen = $codigo_idioma_origen")
            Log.d(REGISTRO, "elegirIdiomaOrigen: titulo_idioma_origen = $titulo_idioma_origen")

            false
        }
    }

    private fun elegirIdiomaDestino() {
        val popUpMenu = PopupMenu(this, btn_idioma_elegido)

        for (i in idiomaArrayList!!.indices) {
            popUpMenu.menu.add(Menu.NONE, i, i, idiomaArrayList!![i].titulo_idioma)
        }

        popUpMenu.show()

        popUpMenu.setOnMenuItemClickListener { menuItem ->
            val posicion = menuItem.itemId
            codigo_idioma_destino = idiomaArrayList!![posicion].codigo_idioma
            titulo_idioma_destino = idiomaArrayList!![posicion].titulo_idioma

            btn_idioma_elegido.text = titulo_idioma_destino

            Log.d(REGISTRO, "elegirIdiomaDestino: codigo_idioma_destino = $codigo_idioma_destino")
            Log.d(REGISTRO, "elegirIdiomaDestino: titulo_idioma_destino = $titulo_idioma_destino")

            false
        }
    }

    private fun validarDatos() {
        texto_idioma_origen = et_idioma_origen.text.toString().trim()
        if (texto_idioma_origen.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese texto", Toast.LENGTH_SHORT).show()
        } else {
            traducirTexto()
        }

    }

    private fun traducirTexto() {
        progressDialog.setMessage("Traduciendo texto...")
        progressDialog.show()

        translateOptions = TranslatorOptions.Builder()
            .setSourceLanguage(codigo_idioma_origen)
            .setTargetLanguage(codigo_idioma_destino)
            .build()
        translator = Translation.getClient(translateOptions)

        val downloadConditions = DownloadConditions.Builder().requireWifi().build()

        translator.downloadModelIfNeeded(downloadConditions)
            .addOnSuccessListener {
                Log.d(REGISTRO, "El paquete de traducción esta listo")
                progressDialog.setMessage("Traduciendo texto...")
                translator.translate(texto_idioma_origen)
                    .addOnSuccessListener { texto_traducido ->
                        Log.d(REGISTRO, "texto_traducido: $texto_traducido")
                        progressDialog.dismiss()
                        tv_idioma_destino.text = texto_traducido
                    }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "${e}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "${e}", Toast.LENGTH_SHORT).show()

            }
    }

    // Creamos el menú
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.mi_menu, menu)
        return true
    }

    // Seleccionamos la opción del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.limpiar_texto -> {
                val s_traduccion = "Traducción"
                et_idioma_origen.setText("")
                et_idioma_origen.hint = "Ingrese texto en $titulo_idioma_origen"
                tv_idioma_destino.text = s_traduccion
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}