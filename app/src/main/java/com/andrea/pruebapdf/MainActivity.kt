package com.andrea.pruebapdf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.andrea.pruebapdf.databinding.ActivityMainBinding
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGenerarPDF.setOnClickListener {
            generarPDF()
        }

    }

    private fun generarPDF() {
        val filePath = getPDFfilePath()

        val url = "http://localfavas.online/Categoria/ReadCategoria.php"

        // Crear la solicitud JSON utilizando Volley
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Procesar la respuesta JSON
                val categoria = response.getJSONArray("data")

                // Crear el documento PDF
                val document = Document()
                PdfWriter.getInstance(document, FileOutputStream(filePath))
                document.open()

                // Recorrer los resultados y agregarlos al PDF
                for (i in 0 until categoria.length()) {
                    val categoria = categoria.getJSONObject(i)
                    val idCategoria = categoria.getInt("idCategoria")
                    val nombre= categoria.getString("nombre")

                    // Agregar los datos al PDF
                    document.add(Paragraph("ID: $idCategoria"))
                    document.add(Paragraph("Nombre: $nombre"))
                    document.add(Paragraph("--------------------------------------"))
                }

                // Cerrar el documento PDF
                document.close()

                // Notificar al usuario que el PDF se generó correctamente
                Toast.makeText(this, "PDF generado exitosamente en descargas", Toast.LENGTH_SHORT).show()
            },
            { error ->
                // Manejar el error de la solicitud
                error.printStackTrace()
                // Manejar cualquier error de conexión o generación de PDF
            }
        )

        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(request)
    }

    private fun getPDFfilePath():String{
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "archivo.pdf"
        val filePath = File(directory, fileName)
        return filePath.absolutePath
    }
}