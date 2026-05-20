package pl.dev.bkwiatkowski.common.core.loader

interface RunWithLoaderUC {
  suspend operator fun <RESULT> invoke(action: suspend () -> RESULT): RESULT
}