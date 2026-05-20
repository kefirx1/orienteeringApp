package pl.dev.bkwiatkowski.common.core.logger

import android.util.Log

typealias Log = StaticLogger

object StaticLogger {
  fun e(tag: String, message: String) {
    Log.e(tag, message)
  }
  fun i(tag: String, message: String) {
    Log.i(tag, message)
  }
}