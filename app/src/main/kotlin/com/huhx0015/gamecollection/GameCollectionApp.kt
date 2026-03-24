package com.huhx0015.gamecollection

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huhx0015.gamecollection.feature.collection.CollectionScreen
import com.huhx0015.gamecollection.feature.detail.GameDetailScreen
import com.huhx0015.gamecollection.feature.games.GameListScreen
import com.huhx0015.gamecollection.feature.platforms.PlatformSelectionScreen
import com.huhx0015.gamecollection.navigation.Routes

/** Root UI: bottom navigation, nav host, and feature screens for games and collection. */
@Composable
fun GameCollectionApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route.orEmpty()

    val isCollectionTab = route.startsWith("collection")

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = !isCollectionTab,
                    onClick = {
                        navController.navigate(Routes.PLATFORM_SELECT) {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.SportsEsports, contentDescription = null) },
                    label = { Text("Games") },
                )
                NavigationBarItem(
                    selected = isCollectionTab,
                    onClick = {
                        navController.navigate(Routes.COLLECTION_LIST) {
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.CollectionsBookmark, contentDescription = null) },
                    label = { Text("My Collection") },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.PLATFORM_SELECT,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.PLATFORM_SELECT) {
                PlatformSelectionScreen(
                    onPlatformSelected = { platformId ->
                        navController.navigate(Routes.gameList(platformId))
                    },
                )
            }
            composable(
                route = Routes.GAME_LIST,
                arguments = listOf(navArgument("platformId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val platformId = backStackEntry.arguments?.getLong("platformId") ?: return@composable
                GameListScreen(
                    onBack = { navController.popBackStack() },
                    onGameClick = { gameId ->
                        navController.navigate(Routes.gameDetail(gameId, platformId))
                    },
                )
            }
            composable(
                route = Routes.GAME_DETAIL,
                arguments = listOf(
                    navArgument("gameId") { type = NavType.LongType },
                    navArgument("platformId") { type = NavType.LongType },
                ),
            ) {
                GameDetailScreen(
                    onBack = { navController.popBackStack() },
                )
            }
            composable(Routes.COLLECTION_LIST) {
                CollectionScreen(
                    onGameClick = { gameId, platformId ->
                        navController.navigate(Routes.gameDetail(gameId, platformId))
                    },
                )
            }
        }
    }
}
