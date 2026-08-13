package pl.dev.bkwiatkowski.common.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import pl.dev.bkwiatkowski.common.core.usecase.Mapper

abstract class ViewModelPreviewProvider<VM, VM_DATA, MAPPER_PARAMS> : PreviewParameterProvider<VM> {
  abstract val mapper: Mapper<MAPPER_PARAMS, VM_DATA>

  val mock by lazy { MockProvider() }
}