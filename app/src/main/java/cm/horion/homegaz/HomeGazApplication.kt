package cm.horion.homegaz


import android.app.Application
import androidx.work.Configuration
import cm.horion.homegaz.di.initKoin
import cm.horion.homegaz.util.initSettings
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.DirectionsFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.android.ext.android.get
import org.koin.androidx.workmanager.factory.KoinWorkerFactory

class HomeGazApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        initSettings(this)
        MapKitFactory.setApiKey("34ebdf3d-6f74-49ca-9560-498aa45dd69f")
        MapKitFactory.initialize(this)
        //DirectionsFactory.initialize(this)
        initKoin {
            androidContext(this@HomeGazApplication)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            // On extrait manuellement la KoinWorkerFactory depuis le conteneur Koin maintenant qu'il est prêt
            .setWorkerFactory(get<KoinWorkerFactory>())
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
