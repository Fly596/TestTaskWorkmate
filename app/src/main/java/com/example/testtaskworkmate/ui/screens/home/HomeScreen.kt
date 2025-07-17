package com.example.testtaskworkmate.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.testtaskworkmate.R
import com.example.testtaskworkmate.data.source.network.CharacterLocation
import com.example.testtaskworkmate.data.source.network.NetworkCharacter
import com.example.testtaskworkmate.ui.theme.TestTaskWorkmateTheme

@Composable
fun HomeScreenNew(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    onCharacterClick: (Int) -> Unit = {},
) {
    val state = homeScreenViewModel.uiState.collectAsStateWithLifecycle()

    TestTaskWorkmateTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                HomeScreenTopBar(
                    onSearchClick = {
                        homeScreenViewModel.onSearchByNameQuerySubmitted(it)
                    }
                )
            },
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {

                // Фильтры.
                Row(modifier = Modifier.fillMaxWidth()) {
                    FilterDropdown(
                        label = "Status",
                        selected = state.value.status,
                        menuItems = listOf("alive", "dead", "unknown"),
                        onSelected = {
                            homeScreenViewModel.typeFilterChanged(it)
                        },
                    )
                    FilterDropdown(
                        label = "Species",
                        selected = state.value.species,
                        onSelected = {
                            homeScreenViewModel.speciesFilterChanged(it)
                        },
                        menuItems = listOf("human", "alien"),
                    )
                    FilterDropdown(
                        menuItems =
                            listOf("male", "female", "unknown", "genderless"),
                        label = "Gender",
                        selected = state.value.gender,
                        onSelected = {
                            homeScreenViewModel.genderFilterChanged(it)
                        },
                    )
                    Button(
                        onClick = {
                            // TODO filter
                        }
                    ) {
                        Text("Apply filters")
                    }
                }
                CharactersGridScreen(
                    modifier = Modifier,
                    networkCharacters = state.value.characters,
                    onCharacterClick = onCharacterClick,
                )
            }

            /*             Column(modifier = Modifier.padding(innerPadding)) {
                Row(modifier = Modifier.fillMaxWidth()){
                    DropdownMenu(menuItems = listOf("alive", "dead", "unknown"), filterValue = "Status")
                    Spacer(modifier = Modifier.width(16.dp))
                    DropdownMenu(menuItems = listOf("human", "alien"), filterValue = "Species")
                    Spacer(modifier = Modifier.width(16.dp))
                }
                CharactersGridScreen(
                    modifier = Modifier.fillMaxWidth().padding(innerPadding),
                    networkCharacters = state.value.characters,
                )
            } */
        }
    }
}

@Composable
fun FilterDropdown(
    label: String,
    selected: String?,
    menuItems: List<String> = emptyList(),
    onSelected: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val checkedStates = remember {
        mutableStateListOf<Boolean>().apply {
            repeat(menuItems.size) { add(false) }
        }
    }

    Box(modifier = Modifier.padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = selected ?: "Choose $label")
            IconButton(onClick = { expanded = true }) {
                Icon(
                    painter =
                        painterResource(R.drawable.arrow_dropdown),
                    contentDescription = "Filter",
                    modifier = Modifier.size(28.dp),
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("Not selected") },
                onClick = {
                    onSelected(null)
                    expanded = false
                },
            )
            menuItems.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
            /*             menuItems.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = checkedStates[index],
                        onCheckedChange = { isChecked ->
                            checkedStates[index] = isChecked
                        },
                    )
                    Text(text = option)
                }
            } */
        }
    }
}

@Composable
fun CharactersGridScreen(
    networkCharacters: List<NetworkCharacter>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onCharacterClick: (Int) -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        items(items = networkCharacters, key = { character -> character.id }) { character ->
            CharacterCard(
                networkCharacter = character,
                modifier = Modifier,
                onCharacterClick = { onCharacterClick(character.id) },
            )
        }
    }
}

@Composable
fun CharacterCard(
    networkCharacter: NetworkCharacter,
    modifier: Modifier = Modifier,
    onCharacterClick: (Int) -> Unit = {},
) {
    Card(
        modifier =
            modifier
                .padding(8.dp)
                .clickable(onClick = { onCharacterClick(networkCharacter.id) }),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        AsyncImage(
            model =
                ImageRequest.Builder(context = LocalContext.current)
                    .data(networkCharacter.image)
                    .crossfade(true)
                    .build(),
            contentDescription = "Character pfp",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .size(256.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = networkCharacter.name,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(text = networkCharacter.type)
            Text(text = "Species: ${networkCharacter.species}")
            Text(text = "Gender: ${networkCharacter.gender}")

            // Статус.
            Row(verticalAlignment = Alignment.CenterVertically) {
                val statusColor =
                    when (networkCharacter.status) {
                        "Dead" -> Color.Red
                        "Alive" -> Color.Green
                        else -> Color.Black
                    }
                Icon(
                    painterResource(
                        R.drawable.circle_24dp_000000_fill1_wght400_grad0_opsz24
                    ),
                    contentDescription = "status indicator",
                    tint = statusColor,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Status: ${networkCharacter.status}")
            }
        }
    }
}

@Composable
@Preview
fun CharacterCardPreview() {
    val networkCharacter =
        NetworkCharacter(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = CharacterLocation(name = "Earth (C-137)", url = ""),
            location = CharacterLocation(name = "Citadel of Ricks", url = ""),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = listOf("https://rickandmortyapi.com/api/episode/1"),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z",
        )

    TestTaskWorkmateTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            CharacterCard(networkCharacter = networkCharacter)
            CharacterCard(networkCharacter = networkCharacter)
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
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
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
                onSearchClick(input.value)
                // TODO: search
            }
        ) {
            Icon(
                painter =
                    painterResource(R.drawable.search_wght400_grad0_opsz24),
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.onBackground,
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
