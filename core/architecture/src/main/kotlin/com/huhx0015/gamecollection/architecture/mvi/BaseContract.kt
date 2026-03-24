package com.huhx0015.gamecollection.architecture.mvi

/** Groups the view contract for an MVI feature; typically implemented by the UI layer. */
interface BaseContract {

    /** Screen view responsibilities for this contract (initialization and observation hooks). */
    interface View : BaseView
}
