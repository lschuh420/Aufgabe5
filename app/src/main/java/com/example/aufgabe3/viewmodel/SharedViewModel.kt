package com.example.aufgabe3.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aufgabe3.model.BookingEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

class SharedViewModel : ViewModel() {
    private val _bookingsEntries = MutableStateFlow<List<BookingEntry>>(emptyList())
    val bookingsEntries: StateFlow<List<BookingEntry>> = _bookingsEntries

    fun addBookingEntry(arrivalDate: LocalDate, departureDate: LocalDate, name: String) {
        val newBookingEntry = BookingEntry(name, departureDate, arrivalDate)
        val updatedList = _bookingsEntries.value + newBookingEntry
        _bookingsEntries.value = updatedList
    }
    fun deleteBookingEntry(bookingEntry: BookingEntry) {
        val updatedList = _bookingsEntries.value.minus(bookingEntry)
        _bookingsEntries.value = updatedList
    }
}
