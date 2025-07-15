package com.example.testtaskworkmate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.testtaskworkmate.R
import com.example.testtaskworkmate.ui.theme.TestTaskWorkmateTheme

// import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val state = homeScreenViewModel.uiState.collectAsStateWithLifecycle()

    TestTaskWorkmateTheme {
        Scaffold(
            topBar = { HomeScreenTopBar() },
            floatingActionButton = {
                IconButton(
                    onClick = {
                        // TODO filter
                    }
                ) {
                    Icon(
                        painter =
                            painterResource(
                                R.drawable.filter_wght400_grad0_opsz24
                            ),
                        contentDescription = "Filter",
                    )
                }
            },
        ) { innerPadding ->
            Column(modifier = modifier.padding(innerPadding).fillMaxWidth()) {

            }
        }
    }
}

@Composable
fun HomeScreenTopBar(modifier: Modifier = Modifier) {
    val input = remember { mutableStateOf("") }

    Row(modifier = modifier.fillMaxWidth()) {
        // Поле для поиска персонажей.
        TextField(
            value = input.value,
            onValueChange = { input.value = it },
            label = { Text("Search characters") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Кнопка для поиска.
        IconButton(
            onClick = {
                // TODO: search
            }
        ) {
            Icon(
                painter =
                    painterResource(R.drawable.search_wght400_grad0_opsz24),
                contentDescription = "Filter",
            )
        }
    }
}
