package com.hyun.firetube.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.preference.PreferenceManager
import com.hyun.firetube.R
import com.hyun.firetube.model.Playlist


class PlaylistDB(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "PlaylistDB.db"
        private const val TABLE_NAME = "Playlist"

        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_THUMBNAIL = "thumbnail"
        private const val KEY_COUNT = "itemCount"
    }

    override fun onCreate(db : SQLiteDatabase) {
        val createPlaylistQuery = (
                "CREATE TABLE " + TABLE_NAME + "(" +
                        KEY_ID + " TEXT," +
                        KEY_TITLE + " TEXT," +
                        KEY_THUMBNAIL + " TEXT," +
                        KEY_COUNT + " INTEGER" + ")"
                )
        db.execSQL(createPlaylistQuery)
    }

    override fun onUpgrade(db : SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getPlaylist(tag : String?) : ArrayList<Playlist> {

        var query = "SELECT * FROM $TABLE_NAME "
        if (tag != "") query += "WHERE $KEY_TITLE LIKE '%$tag%'"

        val playlist: ArrayList<Playlist> = ArrayList()
        val db = this.readableDatabase

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {

            while(!cursor.isAfterLast) {
                val id = cursor.getString(0)
                val title = cursor.getString(1)
                val thumbnail = cursor.getString(2)
                val count = cursor.getInt(3)

                playlist.add(Playlist(id, title, thumbnail, count))
                cursor.moveToNext()
            }
        }

        cursor.close()
        db.close()
        return playlist
    }

    fun setPlaylist(playlists : ArrayList<Playlist>) {

        val db = this.writableDatabase

        for (playlist in playlists) {

            val values = ContentValues()
            values.put(KEY_ID, playlist.id)
            values.put(KEY_TITLE, playlist.title)
            values.put(KEY_THUMBNAIL, playlist.thumbnail)
            values.put(KEY_COUNT, playlist.itemCount)

            db.insert(TABLE_NAME, null, values)
        }

        db.close()
    }

    fun clearTable() {
        val db = this.writableDatabase
        val clearQuery = "DELETE FROM $TABLE_NAME"
        db.execSQL(clearQuery)
        db.close()
    }

    fun clearDatabase(context: Context) {
        this.clearTable()
        context.deleteDatabase(DATABASE_NAME)
    }
}