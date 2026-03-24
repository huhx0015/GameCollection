package com.huhx0015.gamecollection.application

/** Test [AndroidApplication] subclass used in instrumented or Robolectric-style setups. */
class AndroidTestApplication : AndroidApplication() {

    /** APPLICATION LIFECYCLE METHODS __________________________________________________________  */

    override fun onCreate() {
        super.onCreate()
    }
}