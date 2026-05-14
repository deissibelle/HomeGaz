package cm.horion.homegaz


import android.app.Application
import cm.horion.homegaz.di.initKoin
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext

class HomeGazApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        initKoin {
            androidContext(this@HomeGazApplication)
        }

    }
}
