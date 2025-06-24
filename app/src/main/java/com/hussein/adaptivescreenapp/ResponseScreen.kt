package com.hussein.adaptivescreenapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hussein.adaptivescreenapp.ui.SearchViewModel
import kotlinx.coroutines.delay

@Composable
fun ResponsiveScreen(viewModel: SearchViewModel = viewModel()) {
    // Get screen width in DP
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    // Define adaptive dimensions
    val horizontalPadding = adaptivePadding(screenWidthDp)
    val fontSize = adaptiveFontSize(screenWidthDp)
    val buttonHeight = adaptiveButtonHeight(screenWidthDp)
    val query by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = horizontalPadding),
        verticalArrangement = Arrangement.spacedBy(horizontalPadding)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome to Adaptive UI",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )

        SearchBarWithDebounce { query ->
            // Trigger search API call or filtering
            viewModel.onQueryChanged(query)
        }
        if(query.isNotBlank()){
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(searchResults){item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE3F2FD) // light blue
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item,
                            fontSize = fontSize,
                            modifier = Modifier
                                .padding(16.dp),
                            color = Color.Black
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
        ) {
            Text(text = "Click Me", fontSize = fontSize)
        }
    }
}

// Helper functions to choose dimensions based on thresholds
fun adaptivePadding(screenWidth: Dp): Dp = when {
    screenWidth < 360.dp -> 8.dp   // small phones
    screenWidth < 600.dp -> 16.dp  // regular phones
    else -> 32.dp                   // tablets
}

fun adaptiveFontSize(screenWidth: Dp): androidx.compose.ui.unit.TextUnit = when {
    screenWidth < 360.dp -> 14.sp
    screenWidth < 600.dp -> 18.sp
    else -> 24.sp
}

fun adaptiveButtonHeight(screenWidth: Dp): Dp = when {
    screenWidth < 360.dp -> 40.dp
    screenWidth < 600.dp -> 56.dp
    else -> 72.dp
}
@Composable
fun SearchBarWithDebounce(
    modifier: Modifier = Modifier,
    debounceTimeMillis: Long = 500L,
    onSearchTriggered: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    // Debounce logic: called only when user stops typing
    LaunchedEffect(searchText) {
        delay(debounceTimeMillis)
        onSearchTriggered(searchText)
    }

    TextField(
        value = searchText,
        onValueChange = { newText -> searchText = newText },
        label = { Text("Search...") },
        modifier = modifier.fillMaxWidth()
    )
}
