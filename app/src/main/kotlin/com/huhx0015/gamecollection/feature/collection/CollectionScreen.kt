package com.huhx0015.gamecollection.feature.collection

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.huhx0015.gamecollection.domain.model.OwnedGame
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun CollectionScreen(
    onGameClick: (Long, Long) -> Unit,
    viewModel: CollectionViewModel = hiltViewModel(),
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val games by viewModel.displayedGames.collectAsStateWithLifecycle()
    val hasMore by viewModel.hasMoreGames.collectAsStateWithLifecycle()
    val platformOptions by viewModel.platformIdsInCollection.collectAsStateWithLifecycle()
    val platformNames by viewModel.platformNames.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(games.size, hasMore, listState) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            Triple(lastVisible, games.size, hasMore)
        }
            .distinctUntilChanged()
            .collect { (lastIdx, size, more) ->
                if (more && size > 0 && lastIdx >= size - 1) {
                    viewModel.loadMore()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "My collection",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        OutlinedTextField(
            value = ui.searchQuery,
            onValueChange = viewModel::onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search your games") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
        )
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = ui.platformFilter == null,
                onClick = { viewModel.onPlatformFilter(null) },
                label = { Text("All systems") },
            )
            platformOptions.forEach { pid ->
                FilterChip(
                    selected = ui.platformFilter == pid,
                    onClick = {
                        viewModel.onPlatformFilter(if (ui.platformFilter == pid) null else pid)
                    },
                    label = { Text(platformNames[pid] ?: "ID $pid") },
                )
            }
        }
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(games, key = { "${it.igdbGameId}_${it.platformId}" }) { game ->
                CollectionRow(
                    game = game,
                    platformLabel = platformNames[game.platformId],
                    onClick = { onGameClick(game.igdbGameId, game.platformId) },
                )
            }
        }
    }
}

@Composable
private fun CollectionRow(
    game: OwnedGame,
    platformLabel: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AsyncImage(
            model = game.coverUrl,
            contentDescription = null,
            modifier = Modifier.size(56.dp, 72.dp),
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(game.title, style = MaterialTheme.typography.titleMedium)
            Text(
                platformLabel ?: "Platform id: ${game.platformId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
