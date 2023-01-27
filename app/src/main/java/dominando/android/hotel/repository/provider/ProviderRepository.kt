package dominando.android.hotel.repository.provider

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import dominando.android.hotel.HotelProvider
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository
import dominando.android.hotel.repository.sqlite.COLUMN_ADDRESS
import dominando.android.hotel.repository.sqlite.COLUMN_ID
import dominando.android.hotel.repository.sqlite.COLUMN_NAME
import dominando.android.hotel.repository.sqlite.COLUMN_RATING

class ProviderRepository(private val ctx: Context) : HotelRepository {

    override fun save(hotel: Hotel) {
        val uri = ctx.contentResolver
            .insert(HotelProvider.CONTENT_URI, getValues(hotel))
        val id = uri?.lastPathSegment?.toLong() ?: -1L
        if (id != -1L) {
            hotel.id = id
        }
    }

    private fun getValues(hotel: Hotel): ContentValues {
        val contentValues = ContentValues()

        if (hotel.id > 0) {
            contentValues.put(COLUMN_ID, hotel.id)
        }

        return contentValues.apply {
            put(COLUMN_NAME, hotel.name)
            put(COLUMN_ADDRESS, hotel.address)
            put(COLUMN_RATING, hotel.rating)
        }
    }

    override fun remove(vararg hotels: Hotel) {
        hotels.forEach { hotel ->
            val uri = Uri.withAppendedPath(
                HotelProvider.CONTENT_URI,
                hotel.id.toString()
            )
            ctx.contentResolver.delete(uri, null, null)
        }
    }

    override fun hotelById(id: Long, action: (Hotel?) -> Unit) {
        val cursor = ctx.contentResolver.query(
            Uri.withAppendedPath(
                HotelProvider.CONTENT_URI,
                id.toString()
            ), null, null, null, null
        )
        var hotel: Hotel? = null
        if (cursor?.moveToNext() == true) {
            hotel = hotelFromCursor(cursor)
        }
        cursor?.close()
        action(hotel)
    }

    override fun search(term: String, action: (List<Hotel>) -> Unit) {
        var where: String? = null
        var whereArgs: Array<String>? = null
        if (term.isNotEmpty()) {
            where = "$COLUMN_NAME LIKE ?"
            whereArgs = arrayOf("%$term%")
        }
        val cursor = ctx.contentResolver.query(
            HotelProvider.CONTENT_URI,
            null,
            where,
            whereArgs,
            COLUMN_NAME
        )
        val hotels = mutableListOf<Hotel>()
        while (cursor?.moveToNext() == true) {
            hotels.add(hotelFromCursor(cursor))
        }
        cursor?.close()
        action(hotels)
    }

    @SuppressLint("Range")
    private fun hotelFromCursor(cursor: Cursor): Hotel {
        val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
        val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
        val address = cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS))
        val rating = cursor.getFloat(cursor.getColumnIndex(COLUMN_RATING))
        return Hotel(id, name, address, rating)
    }
}
