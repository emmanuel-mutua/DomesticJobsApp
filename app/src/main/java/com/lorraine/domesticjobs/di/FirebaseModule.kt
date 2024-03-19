package com.lorraine.domesticjobs.di

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.lorraine.domesticjobs.connectivity.NetworkConnectivityObserver
import com.lorraine.domesticjobs.data.repository.AuthAuthRepositoryImpl
import com.lorraine.domesticjobs.data.repository.AuthRepository
import com.lorraine.domesticjobs.data.repository.StorageService
import com.lorraine.domesticjobs.data.repository.StorageServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//depedancy injection
@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {
    @Singleton
    @Provides
    fun provideAuthRepo(): AuthRepository = AuthAuthRepositoryImpl(auth = Firebase.auth)

    @Provides
    @Singleton
    fun provideFirebaseFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideStorageService(db: FirebaseFirestore): StorageService = StorageServiceImpl(db)

    @Singleton
    @Provides
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context
    ) = NetworkConnectivityObserver(context = context)

}
