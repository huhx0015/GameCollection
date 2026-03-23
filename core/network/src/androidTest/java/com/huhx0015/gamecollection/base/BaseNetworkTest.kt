package com.huhx0015.gamecollection.base

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.huhx0015.gamecollection.network.RetrofitInterfaceTest
import com.huhx0015.gamecollection.network.interfaces.RetrofitInterface
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@RunWith(AndroidJUnit4::class)
class BaseNetworkTest {

    @get:Rule
    val server = MockWebServer()

    private lateinit var retrofitInterface: RetrofitInterface

    @Before
    fun setUp() {
        InstrumentationRegistry.getInstrumentation().targetContext

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(server.url("/").toString())
            .client(client)
            .build()
        retrofitInterface = retrofit.create(RetrofitInterfaceTest::class.java)
    }

    @Test
    fun retrofitInterface_isConfigured() {
        assertNotNull(retrofitInterface)
    }
}
