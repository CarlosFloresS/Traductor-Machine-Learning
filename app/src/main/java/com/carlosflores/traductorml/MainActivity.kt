package com.carlosflores.traductorml

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var et_idioma_origen : EditText
    private lateinit var tv_idioma_destino : TextView
    private lateinit var btn_elegir_idioma : MaterialButton
    private lateinit var btn_idioma_elegido : MaterialButton
    private lateinit var btn_traducir : MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inicializarVistas()

        btn_elegir_idioma.setOnClickListener{
            Toast.makeText(applicationContext, "Elegir idioma", Toast.LENGTH_SHORT).show()
        }

        btn_idioma_elegido.setOnClickListener{
            Toast.makeText(applicationContext, "Idioma elegido", Toast.LENGTH_SHORT).show()
        }

        btn_traducir.setOnClickListener{
            Toast.makeText(applicationContext, "Traducir", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inicializarVistas(){
        et_idioma_origen = findViewById(R.id.et_idioma_origen)
        tv_idioma_destino = findViewById(R.id.tv_idioma_destino)
        btn_elegir_idioma = findViewById(R.id.btn_elegir_idioma)
        btn_idioma_elegido = findViewById(R.id.btn_idioma_elegido)
        btn_traducir = findViewById(R.id.btn_traducir)
    }
}