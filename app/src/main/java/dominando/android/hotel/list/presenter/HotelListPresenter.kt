package dominando.android.hotel.list.presenter

import dominando.android.hotel.model.Hotel
import dominando.android.hotel.repository.HotelRepository

class HotelListPresenter(
    private val view: HotelListView,
    private val repository: HotelRepository
) {

    private var lastTerm = ""
    private var inDeleteMode = false
    private val selectedItems = mutableListOf<Hotel>()
    private val deletedItems = mutableListOf<Hotel>()

    fun init() {
        if (inDeleteMode) {
            showDeleteMode()
            view.updateSelectionCountText(selectedItems.size)
            view.showSelectedHotels(selectedItems)
        } else {
            refresh()
        }
    }

    fun searchHotels(term: String) {
        lastTerm = term
        repository.search(term) { hotels -> view.showHotels(hotels) }
    }

    fun selectHotel(hotel: Hotel) {
        if (inDeleteMode) {
            toggleHotelSelected(hotel)
            if (selectedItems.size == 0) {
                view.hideDeleteMode()
            } else {
                view.updateSelectionCountText(selectedItems.size)
                view.showSelectedHotels(selectedItems)
            }
        } else {
            view.showHotelDetails(hotel)
        }
    }

    private fun toggleHotelSelected(hotel: Hotel) {
        val existing = selectedItems.find { it.id == hotel.id }

        existing?.let {
            selectedItems.removeAll { it.id == hotel.id }
        } ?: run { selectedItems.add(hotel) }
    }

    fun showDeleteMode() {
        inDeleteMode = true
        view.showDeleteMode()
    }

    fun hideDeleteMode() {
        inDeleteMode = false
        selectedItems.clear()
        view.hideDeleteMode()
    }

    private fun refresh() {
        searchHotels(lastTerm)
    }

    fun deleteSelected(action: (List<Hotel>) -> Unit) {
        //: É possível passar um array para um método que recebe um varargs simplesmente
        // adicionando um asterisco (*) à frente.
        repository.remove(*selectedItems.toTypedArray())
        deletedItems.clear()
        deletedItems.addAll(selectedItems)
        refresh()
        action(selectedItems)
        hideDeleteMode()
        view.showMessageHotelsDeleted(deletedItems.size)
    }

    fun undoDelete() {
        if (deletedItems.isNotEmpty()) {
            for (hotel in deletedItems) {
                repository.save(hotel)
            }
            searchHotels(lastTerm)
        }
    }
}