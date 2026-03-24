package com.huhx0015.gamecollection.architecture.mvi

/** View-side contract for binding an MVI screen (setup and reactive observation). */
interface BaseView {

    fun initView()

    fun observe()
}
