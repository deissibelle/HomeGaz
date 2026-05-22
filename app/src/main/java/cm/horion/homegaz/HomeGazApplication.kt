package cm.horion.homegaz


import android.app.Application
import cm.horion.homegaz.di.initKoin
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import org.koin.android.ext.koin.androidContext

class HomeGazApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        MapKitFactory.initialize(this)
        initKoin {
            androidContext(this@HomeGazApplication)
        }

    }
}
