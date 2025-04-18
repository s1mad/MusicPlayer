package com.simad.musicplayer.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simad.musicplayer.presentation.localtracks.LocalTracksViewModel
import com.simad.musicplayer.presentation.remotetracks.RemoteTracksViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(RemoteTracksViewModel::class)
    abstract fun bindRemoteTracksViewModel(viewModel: RemoteTracksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LocalTracksViewModel::class)
    abstract fun bindLocalTracksViewModel(viewModel: LocalTracksViewModel): ViewModel
}

class ViewModelFactory @Inject constructor(
    private val viewModels: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = viewModels[modelClass]?.get()
            ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        return viewModel as T
    }
}

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)