package dominando.android.hotel

import android.app.Application
import dominando.android.hotel.di.androidModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.stopKoin


class HotelApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@HotelApp)
            modules(listOf(androidModule))
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}