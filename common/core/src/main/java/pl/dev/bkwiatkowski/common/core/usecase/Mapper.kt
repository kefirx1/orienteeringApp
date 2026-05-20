package pl.dev.bkwiatkowski.common.core.usecase

interface Mapper<FROM, TO> {
  operator fun invoke(params: FROM): TO
}