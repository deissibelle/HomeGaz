package cm.horion.homegaz


import android.app.Application
import cm.horion.homegaz.di.initKoin
import org.koin.android.ext.koin.androidContext

class HomeGazApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@HomeGazApplication)
        }
    }
}
