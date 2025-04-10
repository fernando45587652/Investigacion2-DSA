package com.example.app_eletrodomesticos

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "ElectroDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE productos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                categoria TEXT,
                precio REAL,
                cantidad INTEGER
            )
        """)
        db.execSQL("""
            CREATE TABLE carrito (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                producto_id INTEGER,
                nombre TEXT,
                precio REAL,
                cantidad INTEGER
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS productos")
        db.execSQL("DROP TABLE IF EXISTS carrito")
        onCreate(db)
    }

    fun agregarProducto(nombre: String, categoria: String, precio: Double, cantidad: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("categoria", categoria)
            put("precio", precio)
            put("cantidad", cantidad)
        }
        db.insert("productos", null, values)
        db.close()
    }

    fun obtenerProductos(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM productos", null)
        while (cursor.moveToNext()) {
            val producto = Producto(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                cursor.getString(cursor.getColumnIndexOrThrow("categoria")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
            )
            lista.add(producto)
        }
        cursor.close()
        db.close()
        return lista
    }

    fun eliminarProducto(id: Int) {
        val db = writableDatabase
        db.delete("productos", "id=?", arrayOf(id.toString()))
        db.close()
    }

    fun agregarAlCarrito(producto: Producto, cantidad: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("producto_id", producto.id)
            put("nombre", producto.nombre)
            put("precio", producto.precio)
            put("cantidad", cantidad)
        }
        db.insert("carrito", null, values)
        db.close()
    }

    fun obtenerCarrito(): List<Producto> {
        val lista = mutableListOf<Producto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM carrito", null)
        while (cursor.moveToNext()) {
            val producto = Producto(
                cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                "Carrito",
                cursor.getDouble(cursor.getColumnIndexOrThrow("precio")),
                cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"))
            )
            lista.add(producto)
        }
        cursor.close()
        db.close()
        return lista
    }

    fun totalCarrito(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM(precio * cantidad) AS total FROM carrito", null)
        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"))
        }
        cursor.close()
        db.close()
        return total
    }
}