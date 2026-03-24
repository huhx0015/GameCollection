package com.huhx0015.gamecollection.architecture.mvi

/** Immutable snapshot of UI data for an MVI screen. */
interface BaseState {

    /** Default no-field state for screens that do not model dedicated state properties. */
    class State : BaseState
}
