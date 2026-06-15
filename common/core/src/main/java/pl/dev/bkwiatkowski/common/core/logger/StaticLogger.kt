package pl.dev.bkwiatkowski.common.core.logger

import android.util.Log

typealias Log = StaticLogger

object StaticLogger {
  fun e(tag: Tag<*>, message: String) {
    Log.e(tag.javaClass.name, message)
  }
  fun i(tag: Tag<*>, message: String) {
    Log.i(tag.javaClass.name, message)
  }
}

data class Tag<T>(val obj: T) {
  val name: String
    get() = obj?.javaClass?.name ?: "TAG"
}