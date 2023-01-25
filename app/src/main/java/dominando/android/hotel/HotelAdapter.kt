package dominando.android.hotel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RatingBar
import android.widget.TextView
import dominando.android.hotel.databinding.ItemHotelBinding
import dominando.android.hotel.model.Hotel

class HotelAdapter(context: Context, hotels: List<Hotel>) :
    ArrayAdapter<Hotel>(context, 0, hotels) {

    lateinit var binding: ItemHotelBinding


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val hotel = getItem(position)

        binding = ItemHotelBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )

        val viewHolder =  ViewHolder(binding).apply {
               // binding.root.tag = this
            }

        viewHolder.txtName.text = hotel?.name
        viewHolder.rtbRating.rating = hotel?.rating ?: 0f

        return binding.root
    }

    class ViewHolder(binding: ItemHotelBinding) {
        val txtName: TextView = binding.txtName
        val rtbRating: RatingBar = binding.rtbRating
    }
}