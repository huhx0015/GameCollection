package com.huhx0015.gamecollection.feature.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.huhx0015.gamecollection.domain.model.GameSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    onBack: () -> Unit,
    onGameClick: (Long) -> Unit,
    viewModel: GameListViewModel = hiltViewModel(),
) {
    val listState by viewModel.state.collectAsStateWithLifecycle()
    val filters = listState.filters
    val allGenres = listState.allGenres
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
                onValueChange = { viewModel.sendIntent(GameListIntent.SearchChanged(it)) },
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
                allGenres.forEach { genre ->
                    FilterChip(
                        selected = filters.selectedGenreIds.contains(genre.id),
                        onClick = { viewModel.sendIntent(GameListIntent.ToggleGenre(genre.id)) },
                        label = { Text(genre.name) },
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
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
                    if (pagingItems.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
                if (pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(48.dp),
                    )
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
        GameCoverThumbnail(url = game.coverImageUrl)
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

private val GameCoverSize = Modifier.size(56.dp, 72.dp)

@Composable
private fun GameCoverThumbnail(url: String?) {
    if (url.isNullOrBlank()) {
        GameCoverPlaceholder(modifier = GameCoverSize)
        return
    }
    val painter = rememberAsyncImagePainter(model = url)
    val state = painter.state.collectAsState().value
    Box(
        modifier = GameCoverSize,
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is AsyncImagePainter.State.Loading,
            is AsyncImagePainter.State.Empty -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(28.dp),
                    strokeWidth = 2.dp,
                )
            }
            is AsyncImagePainter.State.Error -> {
                GameCoverPlaceholder(modifier = Modifier.fillMaxSize())
            }
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

@Composable
private fun GameCoverPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.SportsEsports,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp),
        )
    }
}
