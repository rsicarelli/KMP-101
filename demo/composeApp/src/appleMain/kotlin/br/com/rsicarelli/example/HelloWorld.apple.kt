package br.com.rsicarelli.example

import kotlin.experimental.ExperimentalObjCRefinement

object HelloWorld

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
actual fun helloWorld(): String = "Olá mundo Apple Main"

fun HelloWorld.get(): String = helloWorld()
