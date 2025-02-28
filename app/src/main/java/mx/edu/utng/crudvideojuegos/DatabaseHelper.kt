package mx.edu.utng.crudvideojuegos
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper (context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "VideoGamesDB"
        private const val TABLE_CONTACTS = "Games"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_DESC = "desc"
        private const val KEY_ANIO = "anio"
        private const val KEY_PRICE = "price"
        private const val KEY_QUALIFI = "qualifi"
        private const val KEY_IMG = "imgUrl"
        private const val KEY_RECORD_AT = "record_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DESC + " TEXT," + KEY_ANIO + " TEXT," + KEY_PRICE + " FLOAT,"
                + KEY_QUALIFI + " TEXT," + KEY_IMG + " TEXT," + KEY_RECORD_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }

    fun addGame(emp: EmpModelClass): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_NAME, emp.name)
            put(KEY_DESC, emp.desc)
            put(KEY_ANIO, emp.anio)
            put(KEY_PRICE, emp.price.toFloat() ?: 0f)
            put(KEY_QUALIFI, emp.qualifi)
            put(KEY_IMG, emp.imgUrl)
        }
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        db.close()
        return success
    }

    fun viewGames(): List<EmpModelClass> {
        val empList = ArrayList<EmpModelClass>()
        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val userId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                    val userName = cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME))
                    val userDesc = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESC))
                    val userAnio = cursor.getString(cursor.getColumnIndexOrThrow(KEY_ANIO))
                    val userPrice = cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRICE))
                    val userQualifi = cursor.getString(cursor.getColumnIndexOrThrow(KEY_QUALIFI))
                    val userImg = cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMG))
                    val userRecord_at = cursor.getString(cursor.getColumnIndexOrThrow(KEY_RECORD_AT))

                    val emp = EmpModelClass(userId, userName, userDesc, userAnio, userPrice, userQualifi, userImg, userRecord_at)
                    empList.add(emp)
                } while (cursor.moveToNext())
            }
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
        } finally {
            cursor?.close()
        }
        return empList
    }

    fun updateGame(emp: EmpModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_NAME, emp.name)
            put(KEY_DESC, emp.desc)
            put(KEY_ANIO, emp.anio)
            put(KEY_PRICE, emp.price)
            put(KEY_QUALIFI, emp.qualifi)
            put(KEY_IMG, emp.imgUrl)
            put(KEY_RECORD_AT, emp.record_at)
        }
        val success = db.update(TABLE_CONTACTS, contentValues, "$KEY_ID=?", arrayOf(emp.id.toString()))
        db.close()
        return success
    }

    fun deleteGame(emp: EmpModelClass): Int {
        val db = this.writableDatabase
        val success = db.delete(TABLE_CONTACTS, "$KEY_ID=?", arrayOf(emp.id.toString()))
        db.close()
        return success
    }
}