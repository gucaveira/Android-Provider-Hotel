package dominando.android.hotel.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.ShareActionProvider
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import dominando.android.hotel.R
import dominando.android.hotel.databinding.FragmentHotelDetailsBinding
import dominando.android.hotel.details.presenter.HotelDetailsPresenter
import dominando.android.hotel.details.presenter.HotelDetailsView
import dominando.android.hotel.form.HotelFormFragment
import dominando.android.hotel.model.Hotel
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class HotelDetailsFragment : Fragment(), HotelDetailsView {
    private val presenter: HotelDetailsPresenter by inject { parametersOf(this) }
    private var hotel: Hotel? = null
    private var shareActionProvider: ShareActionProvider? = null

    private var _binding: FragmentHotelDetailsBinding? = null
    private val binding: FragmentHotelDetailsBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHotelDetailsBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadHotelDetails(arguments?.getLong(EXTRA_HOTEL_ID, -1) ?: -1)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.hotel_details, menu)
        val shareItem = menu.findItem(R.id.action_share)
        shareActionProvider = MenuItemCompat.getActionProvider(shareItem) as? ShareActionProvider
        setShareIntent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            HotelFormFragment.newInstance(hotel?.id ?: 0).open(parentFragmentManager)
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setShareIntent() {
        val text = getString(
            R.string.share_text,
            hotel?.name,
            hotel?.rating
        )
        shareActionProvider?.setShareIntent(Intent(Intent.ACTION_SEND).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        })
    }


    override fun showHotelDetails(hotel: Hotel) {
        this.hotel = hotel
        binding.txtName.text = hotel.name
        binding.txtAddress.text = hotel.address
        binding.rtbRating.rating = hotel.rating
    }

    override fun errorHotelNotFound() {
        binding.txtName.text = getString(R.string.error_hotel_not_found)
        binding.txtAddress.visibility = View.GONE
        binding.rtbRating.visibility = View.GONE
    }

    companion object {
        const val TAG_DETAILS = "tagDetalhe"
        private const val EXTRA_HOTEL_ID = "hotelId"
        fun newInstance(id: Long) = HotelDetailsFragment().apply {
            arguments = Bundle().apply { putLong(EXTRA_HOTEL_ID, id) }
        }
    }
}


