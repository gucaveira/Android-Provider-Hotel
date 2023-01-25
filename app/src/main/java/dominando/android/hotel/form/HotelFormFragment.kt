package dominando.android.hotel.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import dominando.android.hotel.model.Hotel
import dominando.android.hotel.R
import dominando.android.hotel.databinding.FragmentHotelFormBinding
import dominando.android.hotel.form.presenter.HotelFormPresenter
import dominando.android.hotel.form.presenter.HotelFormView
import dominando.android.hotel.repository.memory.MemoryRepository

class HotelFormFragment : DialogFragment(), HotelFormView {

    private var _binding: FragmentHotelFormBinding? = null
    private val binding: FragmentHotelFormBinding get() = _binding!!

    private val presenter = HotelFormPresenter(this, MemoryRepository)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHotelFormBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hotelId = arguments?.getLong(EXTRA_HOTEL_ID, 0) ?: 0
        presenter.loadHotel(hotelId)

        binding.edtAddress.setOnEditorActionListener { _, i, _ ->
            handleKeyboardEvent(i)
        }

        dialog?.apply {
            setTitle(R.string.action_new_hotel)

            // Abre o teclado virtual ao exibir o Dialog
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
    }

    override fun showHotel(hotel: Hotel) {
        binding.edtName.setText(hotel.name)
        binding.edtAddress.setText(hotel.address)
        binding.rtbRating.rating = hotel.rating
    }

    override fun errorSaveHotel() {
        Toast.makeText(requireContext(), R.string.error_hotel_not_found, Toast.LENGTH_SHORT).show()
    }

    override fun errorInvalidHotel() {
        Toast.makeText(requireContext(), R.string.error_invalid_hotel, Toast.LENGTH_SHORT).show()
    }

    private fun handleKeyboardEvent(actionId: Int): Boolean {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            val hotel = saveHotel()
            if (hotel != null) {
                if (activity is OnHotelSavedListener) {
                    val listener = activity as OnHotelSavedListener
                    listener.onHotelSaved(hotel)
                }
                // Feche o dialog
                dialog?.dismiss()
                return true
            }
        }
        return false
    }

    private fun saveHotel(): Hotel? {
        val hotel = Hotel()
        val hotelId = arguments?.getLong(EXTRA_HOTEL_ID, 0) ?: 0

        hotel.apply {
            id = hotelId
            name = binding.edtName.text.toString()
            address = binding.edtAddress.text.toString()
            rating = binding.rtbRating.rating
        }

        return if (presenter.saveHotel(hotel)) {
            hotel
        } else {
            null
        }
    }

    fun open(fm: FragmentManager) {
        if (fm.findFragmentByTag(DIALOG_TAG) == null) {
            show(fm, DIALOG_TAG)
        }
    }

    interface OnHotelSavedListener {
        fun onHotelSaved(hotel: Hotel)
    }

    companion object {
        private const val DIALOG_TAG = "editDialog"
        private const val EXTRA_HOTEL_ID = "hotel_id"
        fun newInstance(hotelId: Long = 0) = HotelFormFragment().apply {
            arguments = Bundle().apply { putLong(EXTRA_HOTEL_ID, hotelId) }
        }
    }
}


