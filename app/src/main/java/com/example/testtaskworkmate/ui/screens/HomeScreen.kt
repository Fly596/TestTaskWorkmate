package com.example.testtaskworkmate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.testtaskworkmate.R
import com.example.testtaskworkmate.data.model.Character
import com.example.testtaskworkmate.data.model.CharacterLocation
import com.example.testtaskworkmate.ui.theme.TestTaskWorkmateTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val state = homeScreenViewModel.homeUiState

    TestTaskWorkmateTheme {
        when (state) {
            is HomeScreenUiState.Error ->
                ErrorScreen(modifier = modifier.fillMaxSize())

            is HomeScreenUiState.Loading ->
                LoadingScreen(modifier = modifier.fillMaxSize())

            is HomeScreenUiState.Success ->
                Scaffold(
                    modifier = modifier.padding(horizontal = 16.dp),
                    topBar = { HomeScreenTopBar(onSearchClick = { /*TODO: search*/ }) },
                    floatingActionButton = {
                        FilledIconButton(
                            onClick = {
                                // TODO filter
                            },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                painter =
                                    painterResource(R.drawable.filter_wght400_grad0_opsz24),
                                contentDescription = "Filter",
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    },
                ) { innerPadding ->
                    CharactersGridScreen(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(innerPadding),
                        characters = state.characters
                    )

                }
        }
    }
}

@Composable
fun CharactersGridScreen(
    characters: List<Character>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier =
            modifier,
        contentPadding = contentPadding,
    ) {
        items(items = characters, key = { character -> character.id }) { character ->
            CharacterCard(
                character = character,
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
fun CharacterCard(character: Character, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(character.image)
                    .crossfade(true)
                    .build(),
                contentDescription = "Character pfp",
                contentScale = ContentScale.Crop
            )
            /*          Image(
                         painterResource(R.drawable.rick_img),
                         contentDescription = "character_pfp",
                         modifier = Modifier
                             .size(96.dp)
                             .clip(CircleShape),
                         contentScale = ContentScale.Crop,
                     ) */

            Column() {
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(text = character.type)
                Text(text = "Species: ${character.species}")
                Text(text = "Gender: ${character.gender}")

                // Статус.
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor =
                        when (character.status) {
                            "Dead" -> Color.Red
                            "Alive" -> Color.Green
                            else -> Color.Black
                        }
                    Icon(
                        painterResource(
                            R.drawable
                                .circle_24dp_000000_fill1_wght400_grad0_opsz24
                        ),
                        contentDescription = "status indicator",
                        tint = statusColor,
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Status: ${character.status}")
                }
            }
        }
    }
}

@Composable
@Preview
fun CharacterCardPreview() {
    val character =
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin =
                CharacterLocation(
                    name = "Earth (C-137)",
                    url = "",
                ),
            location =
                CharacterLocation(
                    name = "Citadel of Ricks",
                    url = "",
                ),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = listOf("https://rickandmortyapi.com/api/episode/1"),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z",
        )

    TestTaskWorkmateTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            CharacterCard(character = character)
            CharacterCard(character = character)
        }
    }
}

@Composable
fun HomeScreenTopBar(
    modifier: Modifier = Modifier,
    onSearchClick: (String) -> Unit,
) {
    val input = remember { mutableStateOf("") }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Поле для поиска персонажей.
        OutlinedTextField(
            value = input.value,
            onValueChange = { input.value = it },
            label = { Text("Search characters") },
            singleLine = true,
            modifier = Modifier,
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
                tint = Color(50, 50, 50),
            )
        }
    }
}


@Composable
fun LoadingScreen(modifier: Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = "loading",
    )
}

@Composable
fun ErrorScreen(modifier: Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error),
            contentDescription = "",
        )
        Text(text = "loading failed", modifier = Modifier.padding(16.dp))
    }
}

/* @Preview
@Composable
fun HomeScreenTopBarPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        HomeScreenTopBar(onSearchClick = {})
    }
} */
