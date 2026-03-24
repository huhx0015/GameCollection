package com.huhx0015.gamecollection.logging

import timber.log.Timber

/** No-op Timber tree for release builds (drops all log output). */
class ReleaseTree : Timber.Tree() {

    /** LOGGING METHODS ________________________________________________________________________  */

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {}
}