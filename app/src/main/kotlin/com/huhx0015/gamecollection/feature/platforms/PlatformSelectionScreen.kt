package com.huhx0015.gamecollection.feature.platforms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huhx0015.gamecollection.domain.model.GamePlatform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlatformSelectionScreen(
    onPlatformSelected: (Long) -> Unit,
    viewModel: PlatformSelectionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagingItems = viewModel.platformPaging.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PlatformSelectionEvent.NavigateToGameList ->
                    onPlatformSelected(event.platformId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Choose a console",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.sendIntent(PlatformSelectionIntent.SearchChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search consoles") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
        ) {
            val refresh = pagingItems.loadState.refresh
            if (refresh is LoadState.Error) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Text(
                            text = refresh.error.localizedMessage ?: "Could not load platforms",
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
                val platform = pagingItems[index]
                if (platform != null) {
                    PlatformListRow(
                        platform = platform,
                        onClick = {
                            viewModel.sendIntent(PlatformSelectionIntent.PlatformClicked(platform.id))
                        },
                    )
                }
            }
            if (refresh is LoadState.Loading && pagingItems.itemCount == 0) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            if (pagingItems.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun PlatformListRow(
    platform: GamePlatform,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = platform.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                val subtitle = platform.slug?.takeIf { it.isNotBlank() }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
