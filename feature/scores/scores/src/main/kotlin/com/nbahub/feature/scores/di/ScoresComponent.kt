package com.nbahub.feature.scores.di

import com.nbahub.feature.scores.ScoresFeatureDependencies
import dagger.Component

@Component(modules = [PlatformModule::class])
internal interface ScoresComponent {

    fun viewModelFactory(): ScoresViewModelFactory

    @Component.Factory
    interface Factory {
        fun create(platformModule: PlatformModule): ScoresComponent
    }

    companion object {
        fun create(dependencies: ScoresFeatureDependencies): ScoresComponent =
            DaggerScoresComponent.factory().create(
                platformModule = PlatformModule(dependencies),
            )
    }
}
