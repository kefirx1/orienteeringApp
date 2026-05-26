package pl.dev.bkwiatkowski.common.ui.component.picker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButton
import pl.dev.bkwiatkowski.common.ui.component.button.SmallButtonData
import pl.dev.bkwiatkowski.common.ui.component.text.CustomText
import pl.dev.bkwiatkowski.common.ui.theme.OrienteeringAppTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

sealed interface DateValidationMode {
  data object NoValidation : DateValidationMode
  data class PastDatesOnly(val excludeToday: Boolean = false) : DateValidationMode
  data class FutureDatesOnly(val excludeToday: Boolean = false) : DateValidationMode
  data class DateRange(val from: LocalDateTime, val to: LocalDateTime) : DateValidationMode
}

data class CustomDatePickerData(
  val pickerTitle: String,
  val pickedDate: LocalDateTime,
  val onNewDatePicked: (LocalDateTime) -> Unit,
  val onDismiss: () -> Unit,
  val validationMode: DateValidationMode = DateValidationMode.NoValidation,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(data: CustomDatePickerData) {
  val selectableDates = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
      val instant = Instant.ofEpochMilli(utcTimeMillis)
      val selectedLocalDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
      val now = LocalDateTime.now(ZoneId.systemDefault())

      return when (data.validationMode) {
        is DateValidationMode.NoValidation -> true
        is DateValidationMode.PastDatesOnly -> {
          val mode = data.validationMode
          if (mode.excludeToday) {
            selectedLocalDate.isBefore(now)
          } else {
            selectedLocalDate <= now
          }
        }
        is DateValidationMode.FutureDatesOnly -> {
          val mode = data.validationMode
          if (mode.excludeToday) {
            selectedLocalDate.isAfter(now)
          } else {
            selectedLocalDate >= now
          }
        }
        is DateValidationMode.DateRange -> {
          val mode = data.validationMode
          selectedLocalDate >= mode.from && selectedLocalDate <= mode.to
        }
      }
    }
  }

  val state = rememberDatePickerState(
    initialSelectedDateMillis = data.pickedDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    initialDisplayMode = DisplayMode.Picker,
    selectableDates = selectableDates
  )
  val dateFormatter = remember { DatePickerDefaults.dateFormatter() }
  val datePickerColors = DatePickerDefaults.colors(
    containerColor = MaterialTheme.colorScheme.background,
    titleContentColor = MaterialTheme.colorScheme.onPrimary,
    headlineContentColor = MaterialTheme.colorScheme.onPrimary,
    weekdayContentColor = MaterialTheme.colorScheme.onPrimary,
    subheadContentColor = MaterialTheme.colorScheme.onPrimary,
    navigationContentColor = MaterialTheme.colorScheme.onPrimary,
    yearContentColor = MaterialTheme.colorScheme.onPrimary,
    disabledYearContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
    currentYearContentColor = MaterialTheme.colorScheme.onPrimary,
    selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
    disabledSelectedYearContentColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
    dayContentColor = MaterialTheme.colorScheme.onPrimary,
    disabledDayContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
    selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
    disabledSelectedDayContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.4f),
    todayContentColor = MaterialTheme.colorScheme.onPrimary,
    selectedYearContainerColor = MaterialTheme.colorScheme.primary,
    disabledSelectedYearContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
    disabledSelectedDayContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
    todayDateBorderColor = MaterialTheme.colorScheme.primary,
    dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onPrimary,
    dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary,
    dividerColor = MaterialTheme.colorScheme.primary,
  )

  DatePickerDialog(
    onDismissRequest = data.onDismiss,
    dismissButton = {
      SmallButton(
        buttonData = SmallButtonData.Tertiary(
          text = "Anuluj",
          onClick = data.onDismiss,
        )
      )
    },
    confirmButton = {
      val instant = Instant.ofEpochMilli(state.selectedDateMillis ?: Instant.now().toEpochMilli())
      val selectedLocalDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

      SmallButton(
        buttonData = SmallButtonData.Tertiary(
          text = "Wybierz",
          onClick = {
            data.onNewDatePicked(selectedLocalDate)
          },
        )
      )
    },
    colors = datePickerColors,
    content = {
      DatePicker(
        state = state,
        dateFormatter = dateFormatter,
        title = {
          Column(
            modifier = Modifier
              .padding(horizontal = 20.dp)
              .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Spacer(modifier = Modifier.height(20.dp))
            CustomText(
              text = data.pickerTitle,
              color = MaterialTheme.colorScheme.onPrimary,
              style = MaterialTheme.typography.titleMedium,
            )
          }
        },
        headline = {
          DatePickerDefaults.DatePickerHeadline(
            modifier = Modifier
              .padding(horizontal = 10.dp),
            selectedDateMillis = state.selectedDateMillis,
            displayMode = state.displayMode,
            dateFormatter = dateFormatter,
          )
        },
        colors = datePickerColors,
      )
    },
  )
}

private class CustomDatePickerProvider : PreviewParameterProvider<CustomDatePickerData> {
  override val values: Sequence<CustomDatePickerData> = sequenceOf(
    CustomDatePickerData(
      pickedDate = LocalDateTime.now(ZoneId.systemDefault()),
      pickerTitle = "Wybierz date wylotu",
      onNewDatePicked = {},
      onDismiss = {},
      validationMode = DateValidationMode.PastDatesOnly(excludeToday = false),
    )
  )
}

@Preview(name = "CustomDatePicker preview")
@Composable
fun CustomDatePickerPreview(
  @PreviewParameter(CustomDatePickerProvider::class) data: CustomDatePickerData,
) {
  OrienteeringAppTheme {
    CustomDatePicker(data = data)
  }
}