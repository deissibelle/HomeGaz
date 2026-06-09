package cm.horion.homegaz.di


import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    //workManagerFactory()
    modules(
        dataModule(),
        viewModelModule()
       
    )
}
