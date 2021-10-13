package com.example.tata_consultor.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tata_consultor.Clases.ProductoDemo;


import java.util.ArrayList;

public class ProductoDB {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public ProductoDB(Context context) {
        dbHelper = new DBHelper(context);
    }


    private void openReadableDB() {
        db = dbHelper.getReadableDatabase();
    }

    private void openWriteableDB() {
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        if(db!=null){
            db.close();
        }
    }

    private ContentValues clienteMapperContentValues(ProductoDemo producto) {
        ContentValues cv = new ContentValues();

        cv.put(ConstantsDB.PRO_IDPRODUCTO, producto.getIdProducto());
        cv.put(ConstantsDB.PRO_CODIGOPRODUCTO, producto.getCodigoProducto());
        cv.put(ConstantsDB.PRO_CODIGOEAN, producto.getCodigoEan());
        cv.put(ConstantsDB.PRO_DESCRIPCION, producto.getDescripcion());
        cv.put(ConstantsDB.PRO_PRECIO, producto.getPrecio());



        return cv;
    }


    public ProductoDemo Buscar_tabla_producto(String codigoetiqueta) {

        boolean error ;

        ProductoDemo producto = new ProductoDemo();
        this.openReadableDB();
        String where = ConstantsDB.PRO_CODIGOPRODUCTO+ "= ?" + " OR " + ConstantsDB.PRO_CODIGOEAN + "= ?" ;
        String[] campos = new String[]{ConstantsDB.PRO_IDPRODUCTO,ConstantsDB.PRO_CODIGOPRODUCTO,ConstantsDB.PRO_CODIGOEAN, ConstantsDB.PRO_DESCRIPCION, ConstantsDB.PRO_PRECIO};
        String[] whereArgs = {codigoetiqueta,codigoetiqueta};
        Cursor c = db.query(ConstantsDB.TABLA_PRODUCTO, campos, where, whereArgs, null, null, null, null);

        try{
            if( c != null ) {
                c.moveToFirst();
                producto.setIdProducto(c.getInt(0));
                producto.setCodigoProducto(c.getString(1));
                producto.setCodigoEan(c.getString(2));
                producto.setDescripcion(c.getString(3));
                producto.setPrecio(c.getString(4));

                c.close();
                error = false;
            }else{
                error = true;
            }

        }catch (Exception ex){
            Log.e("ProductoDB," +
                    "", "error al leer");
            error = true;
        }

        if (error){
            return null;
        }else {
            return producto;
        }
    }



    public ArrayList loadProducto() {

        ArrayList<ProductoDemo> list = new ArrayList<>();
        this.openReadableDB();
        String[] campos = new String[]{ConstantsDB.PRO_IDPRODUCTO,ConstantsDB.PRO_CODIGOPRODUCTO,ConstantsDB.PRO_CODIGOEAN, ConstantsDB.PRO_DESCRIPCION, ConstantsDB.PRO_PRECIO};
        Cursor c = db.query(ConstantsDB.TABLA_PRODUCTO, campos, null, null, null, null, null);

        try {
            while (c.moveToNext()) {
                ProductoDemo producto = new ProductoDemo();
                producto.setIdProducto(c.getInt(0));
                producto.setCodigoProducto(c.getString(1));
                producto.setCodigoEan(c.getString(2));
                producto.setDescripcion(c.getString(3));
                producto.setPrecio(c.getString(4));
                list.add(producto);
            }
        } finally {
            c.close();
        }
        this.closeDB();
        return list;
    }

    public long insertarProducto(ProductoDemo producto) {
        this.openWriteableDB();
        long rowID = db.insert(ConstantsDB.TABLA_PRODUCTO, null, clienteMapperContentValues(producto));
        this.closeDB();
        return rowID;
    }

    public Boolean validar(String codigo) {

        boolean error= false;
        this.openReadableDB();
        String where = ConstantsDB.PRO_CODIGOPRODUCTO+ "= ?";

        Cursor c = db.query(ConstantsDB.TABLA_PRODUCTO, null, where, new String[]{codigo}, null, null, null, null);
        try{
            if( c.getCount()>0) {
                c.close();
                error = true;
            }

        }catch (Exception ex){
            Log.e("valiando" + "", "error al leer");
            error = true;
        }

        return error;
    }




    public void eliminarAll() {
        this.openWriteableDB();
        db.delete(ConstantsDB.TABLA_PRODUCTO, null, null);
        this.closeDB();

    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, ConstantsDB.DB_NAME, null, ConstantsDB.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ConstantsDB.TABLA_PRODUCTO_SQL);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


}
