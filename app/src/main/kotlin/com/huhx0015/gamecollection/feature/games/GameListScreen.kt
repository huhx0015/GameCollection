package com.huhx0015.gamecollection.feature.games

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.huhx0015.gamecollection.domain.model.GameSummary
import com.huhx0015.gamecollection.domain.model.RegionFilter

private fun regionLabel(region: RegionFilter): String = when (region) {
    RegionFilter.ALL -> "All regions"
    RegionFilter.NORTH_AMERICA -> "North America"
    RegionFilter.EUROPE -> "Europe"
    RegionFilter.JAPAN -> "Japan"
    RegionFilter.ASIA -> "Asia"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onBack: () -> Unit,
    onGameClick: (Long) -> Unit,
    viewModel: GameListViewModel = hiltViewModel(),
) {
    val filters by viewModel.filters.collectAsStateWithLifecycle()
    val allGenres by viewModel.allGenres.collectAsStateWithLifecycle()
    val pagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Games") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                value = filters.searchQuery,
                onValueChange = viewModel::onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search games") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
            )
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                enumValues<RegionFilter>().forEach { region ->
                    FilterChip(
                        selected = filters.region == region,
                        onClick = { viewModel.onRegionChange(region) },
                        label = { Text(regionLabel(region)) },
                    )
                }
            }
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                allGenres.forEach { genre ->
                    FilterChip(
                        selected = filters.selectedGenreIds.contains(genre.id),
                        onClick = { viewModel.toggleGenre(genre.id) },
                        label = { Text(genre.name) },
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                val refreshState = pagingItems.loadState.refresh
                if (refreshState is LoadState.Error) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        ) {
                            Text(
                                text = refreshState.error.localizedMessage ?: "Could not load games",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Button(
                                onClick = { pagingItems.retry() },
                                modifier = Modifier.padding(top = 8.dp),
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                items(
                    count = pagingItems.itemCount,
                    key = pagingItems.itemKey { it.id },
                ) { index ->
                    val game = pagingItems[index]
                    if (game != null) {
                        GameRow(
                            game = game,
                            onClick = { onGameClick(game.id) },
                        )
                    }
                }
                if (pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                        )
                    }
                }
                if (pagingItems.loadState.append is LoadState.Loading) {
                    item {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GameRow(
    game: GameSummary,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = game.coverImageUrl,
            contentDescription = null,
            modifier = Modifier.size(56.dp, 72.dp),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(game.name, style = MaterialTheme.typography.titleMedium)
            val genres = game.genreNames.joinToString(", ")
            if (genres.isNotEmpty()) {
                Text(
                    genres,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
