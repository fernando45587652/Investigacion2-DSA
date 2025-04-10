package com.example.tuapp_eletrodomesticos

import android.app.AlertDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.tuapp.electrodomesticos.DBHelper
import com.tuapp.electrodomesticos.Producto

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var listView: ListView
    private lateinit var productos: List<Producto>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)
        listView = findViewById(R.id.listaProductos)

        findViewById<Button>(R.id.btnAgregar).setOnClickListener {
            dbHelper.agregarProducto("Lavadora", "Electrodoméstico", 350.0, 2)
            dbHelper.agregarProducto("Televisor LG", "Electrodoméstico", 500.0, 4)
            dbHelper.agregarProducto("Secadora", "Electrodoméstico", 350.0, 1)
            dbHelper.agregarProducto("Microondas", "Electrodoméstico", 300.0, 3)
            dbHelper.agregarProducto("Equipo de Sonido Panasonic", "Electrodoméstico", 100.0, 1)
            dbHelper.agregarProducto("Cocina Mabe", "Electrodoméstico", 150.00,1)
            dbHelper.agregarProducto("Refrigeradora Samsung", "Electrodoméstico", 900.00,1)

            actualizarLista()
        }

        findViewById<Button>(R.id.btnCarrito).setOnClickListener {
            val carrito = dbHelper.obtenerCarrito()
            val total = dbHelper.totalCarrito()
            val detalles = carrito.joinToString("\n") {
                "${it.nombre}: $${it.precio} x${it.cantidad}"
            }
            AlertDialog.Builder(this)
                .setTitle("Carrito - Total: $${total}")
                .setMessage(detalles)
                .setPositiveButton("OK", null)
                .show()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val producto = productos[position]
            AlertDialog.Builder(this)
                .setTitle("Agregar al carrito")
                .setMessage("¿Agregar ${producto.nombre} al carrito?")
                .setPositiveButton("Sí") { _, _ ->
                    dbHelper.agregarAlCarrito(producto, 1)
                    Toast.makeText(this, "Agregado al carrito", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        }

        actualizarLista()
    }

    private fun actualizarLista() {
        productos = dbHelper.obtenerProductos()
        val nombres = productos.map { "${it.nombre} - ${it.categoria} - $${it.precio} x${it.cantidad}" }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)
        listView.adapter = adapter
    }
}
