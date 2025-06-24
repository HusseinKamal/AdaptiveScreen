package com.hussein.adaptivescreenapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModel : ViewModel() {

    private val fruitList = listOf(
        "Apple", "Banana", "Orange", "Grape", "Strawberry",
        "Watermelon", "Mango", "Pineapple", "Peach", "Cherry"
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<String>>(fruitList)
    val searchResults: StateFlow<List<String>> = _searchResults.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(600)
                .distinctUntilChanged()
                .mapLatest { query ->
                    filterFruits(query)
                }
                .collect { filtered ->
                    _searchResults.value = filtered
                }
        }
    }

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun filterFruits(query: String): List<String> {
        return fruitList.filter {
            it.contains(query, ignoreCase = true)
        }
    }
}
