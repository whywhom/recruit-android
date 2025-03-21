package nz.co.test.transactions.di

import android.app.Activity
import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import nz.co.test.transactions.data.TransactionRepository
import nz.co.test.transactions.data.services.TransactionsService
import nz.co.test.transactions.ui.MainActivity
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Singleton
import kotlin.reflect.KClass

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(OffsetDateTimeAdapter())  // Add custom OffsetDateTime adapter
            .add(BigDecimalAdapter())      // Add custom BigDecimal adapter
            .build()
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl("https://gist.githubusercontent.com/Josh-Ng/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(client)
        .build()

    @Provides
    @Singleton
    fun provideTransactionsApi(retrofit: Retrofit): TransactionsService =
        retrofit.create(TransactionsService::class.java)

    @Provides
    @Singleton
    fun provideTransactionRepository(api: TransactionsService): TransactionRepository {
        return TransactionRepository(api)
    }
}

@Module
@InstallIn(ActivityComponent::class)
class ActivitiesModule {

    @Provides
    @IntoMap
    @ActivityClassKey(MainActivity::class)
    fun providesMainActivity(): Activity = MainActivity()
}

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ActivityClassKey(val value: KClass<out Activity>)

class BigDecimalAdapter {
    @FromJson
    fun fromJson(value: String): BigDecimal {
        return BigDecimal(value)
    }

    @ToJson
    fun toJson(value: BigDecimal): String {
        return value.toString()
    }
}

class OffsetDateTimeAdapter {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    @FromJson
    fun fromJson(value: String): LocalDateTime {
        return LocalDateTime.parse(value, formatter)
    }

    @ToJson
    fun toJson(value: LocalDateTime): String {
        return formatter.format(value)
    }
}