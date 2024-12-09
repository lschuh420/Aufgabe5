package com.example.aufgabe3.ui.add

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aufgabe3.MainActivity
import com.example.aufgabe3.R
import com.example.aufgabe3.model.BookingEntry
import com.example.aufgabe3.viewmodel.SharedViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    var name by remember { mutableStateOf("") }
    var arrivalDate by remember { mutableStateOf<LocalDate?>(null) }
    var departureDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Booking Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = if (arrivalDate != null && departureDate != null) {
                    "${arrivalDate!!.format(dateFormatter)} - ${departureDate!!.format(dateFormatter)}"
                } else {
                    ""
                },
                onValueChange = {},
                label = { Text("Select Date Range") },
                enabled = false,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDateRangePicker = true },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (isBookEntryValid(arrivalDate, departureDate, name)) {
                        sharedViewModel.addBookingEntry(arrivalDate!!, departureDate!!, name.trim())
                        navController.popBackStack()
                    } else {
                        Toast.makeText(
                            navController.context,
                            "Invalid booking entry",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
    if (showDateRangePicker) {
        DateRangePickerModal(
            onDismiss = { showDateRangePicker = false },
            onDateRangeSelected = { dateRange ->
                val startDate = dateRange.first
                val endDate = dateRange.second
                arrivalDate = startDate?.let { convertToLocalDate(it) }
                departureDate = endDate?.let { convertToLocalDate(it) }
                showDateRangePicker = false
            },
            validateDate = { dateToValidate ->
                !convertToLocalDate(dateToValidate).isBefore(LocalDate.now())
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit,
    validateDate: (Long) -> Boolean
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select Date Range"
                )
            },
            showModeToggle = false, dateValidator = { validateDate(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}


/**
 * Converts a timestamp (in milliseconds) to a `LocalDate`.
 *
 * This utility function converts an epoch timestamp to a `LocalDate` object using the
 * system's default time zone.
 *
 * @param timestamp The timestamp in milliseconds to be converted.
 * @return The corresponding `LocalDate` instance.
 */
fun convertToLocalDate(timestamp: Long): LocalDate {
    return timestamp.let {
        LocalDateTime.ofEpochSecond(
            it / 1000, 0, ZoneId.systemDefault().rules.getOffset(
                LocalDateTime.now()
            )
        ).toLocalDate()
    }
}

/**
 * Validates the input data for a booking entry.
 *
 * This utility function checks if the arrival date, departure date, and name are
 * valid. The name must not be null or blank, and both dates must be non-null.
 *
 * @param arrivalDate The arrival date of the booking.
 * @param departureDate The departure date of the booking.
 * @param name The name associated with the booking.
 * @return True if the input data is valid; false otherwise.
 */
fun isBookEntryValid(arrivalDate: LocalDate?, departureDate: LocalDate?, name: String?): Boolean {
    return !(null == arrivalDate || null == departureDate || name.isNullOrBlank())
}