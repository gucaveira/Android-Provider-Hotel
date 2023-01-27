package dominando.android.hotel.di

import dominando.android.hotel.details.presenter.HotelDetailsPresenter
import dominando.android.hotel.details.presenter.HotelDetailsView
import dominando.android.hotel.form.presenter.HotelFormPresenter
import dominando.android.hotel.form.presenter.HotelFormView
import dominando.android.hotel.list.presenter.HotelListPresenter
import dominando.android.hotel.list.presenter.HotelListView
import dominando.android.hotel.repository.HotelRepository
import dominando.android.hotel.repository.provider.ProviderRepository
import dominando.android.hotel.repository.sqlite.SQLiteRepository
import org.koin.dsl.module

val androidModule = module {
    single { this }
    //single<HotelRepository> { SQLiteRepository(ctx = get()) }
    single<HotelRepository> { ProviderRepository(ctx = get()) }

    factory { (view: HotelListView) -> HotelListPresenter(view, repository = get()) }

    factory { (view: HotelDetailsView) -> HotelDetailsPresenter(view, repository = get()) }

    factory { (view: HotelFormView) -> HotelFormPresenter(view, repository = get()) }
}
