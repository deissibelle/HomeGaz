package cm.horion.homegaz


import android.app.Application
import cm.horion.homegaz.di.initKoin
import cm.horion.homegaz.util.initSettings
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import org.koin.android.ext.koin.androidContext

class HomeGazApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initSettings(this)
        MapKitFactory.setApiKey("34ebdf3d-6f74-49ca-9560-498aa45dd69f")
        MapKitFactory.initialize(this)
        initKoin {
            androidContext(this@HomeGazApplication)
        }

    }
}
