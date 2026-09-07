package pl.dev.bkwiatkowski.orienteeringapp.presentation.developer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeveloperFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View = ComposeView(requireContext()).apply {
    setContent {
      OrienteeringAppTheme {
        DeveloperScreen(
          onBack = {
            parentFragmentManager.popBackStack()
          }
        )
      }
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
      parentFragmentManager.popBackStack()
    }
  }
}
