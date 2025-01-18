package eu.kanade.presentation.browse.anime.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Text
import eu.kanade.presentation.browse.BrowseSourceLoadingItem
import eu.kanade.presentation.browse.InLibraryBadge
import eu.kanade.presentation.library.components.CommonEntryItemDefaults
import eu.kanade.presentation.library.components.EntryComfortableGridItem
import kotlinx.coroutines.flow.StateFlow
import tachiyomi.domain.entries.anime.model.Anime
import tachiyomi.domain.entries.anime.model.AnimeCover
import tachiyomi.presentation.core.util.plus

@Composable
fun BrowseAnimeSourceComfortableGrid(
	modifier: Modifier,
	animeList: LazyPagingItems<StateFlow<Anime>>,
	columns: GridCells,
	contentPadding: PaddingValues,
	onAnimeClick: (Anime) -> Unit,
	onAnimeLongClick: (Anime) -> Unit,
) {
	val context = LocalContext.current
	LaunchedEffect(Unit) {
		Toast.makeText(context, "Comfortable grid", Toast.LENGTH_LONG).show()
	}
	//    LazyVerticalGrid(
	//		modifier = modifier,
	//        columns = columns,
	//        contentPadding = contentPadding + PaddingValues(8.dp),
	////        verticalArrangement = Arrangement.spacedBy(CommonEntryItemDefaults.GridVerticalSpacer),
	////        horizontalArrangement = Arrangement.spacedBy(CommonEntryItemDefaults.GridHorizontalSpacer),
	//		verticalArrangement = Arrangement.spacedBy(8.dp),
	//		horizontalArrangement = Arrangement.spacedBy(8.dp),
	//    ) {
	//        if (animeList.loadState.prepend is LoadState.Loading) {
	//            item(span = { GridItemSpan(maxLineSpan) }) {
	//                BrowseSourceLoadingItem()
	//            }
	//        }
	//
	//        items(count = animeList.itemCount) { index ->
	//            val anime by animeList[index]?.collectAsState() ?: return@items
	//
	//			Card(modifier = Modifier.padding(8.dp),onClick = {  onAnimeClick(anime) }) {
	//				BrowseAnimeSourceComfortableGridItem(
	//					anime = anime,
	//					onClick = { onAnimeClick(anime) },
	//					onLongClick = { onAnimeLongClick(anime) },
	//				)
	//			}
	//
	//        }
	//
	//        if (animeList.loadState.refresh is LoadState.Loading || animeList.loadState.append is LoadState.Loading) {
	//            item(span = { GridItemSpan(maxLineSpan) }) {
	//                BrowseSourceLoadingItem()
	//            }
	//        }
	//
	////		items(20) { index ->
	////			Button(onClick = { /*TODO*/ }) {
	////				Text(text = "Item #$index")
	////			}
	////
	////		}
	//
	//
	//    }
	Column(modifier = Modifier.padding(contentPadding)) {
		LazyVerticalGrid(
			columns = columns, // Defines 2 columns
			modifier = modifier
				.fillMaxSize(),
			//			.padding(contentPadding),
			//		verticalArrangement = Arrangement.spacedBy(CommonEntryItemDefaults.GridVerticalSpacer),
			//		horizontalArrangement = Arrangement.spacedBy(CommonEntryItemDefaults.GridHorizontalSpacer),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
		) {
			if (animeList.loadState.prepend is LoadState.Loading) {
				item(span = { GridItemSpan(maxLineSpan) }) {
					BrowseSourceLoadingItem()
				}
			}

			items(count = animeList.itemCount) { index ->
				val anime by animeList[index]?.collectAsState() ?: return@items

				Card(modifier = Modifier.padding(8.dp), onClick = { onAnimeClick(anime) },colors = CardDefaults.colors(containerColor = Color.Transparent,),) {
					BrowseAnimeSourceComfortableGridItem(
						anime = anime,
						onClick = { onAnimeClick(anime) },
						onLongClick = { onAnimeLongClick(anime) },
					)
				}
			}

			if (animeList.loadState.refresh is LoadState.Loading || animeList.loadState.append is LoadState.Loading) {
				item(span = { GridItemSpan(maxLineSpan) }) {
					BrowseSourceLoadingItem()
				}
			}

		}
	}


}

@Composable
private fun BrowseAnimeSourceComfortableGridItem(
	anime: Anime,
	onClick: () -> Unit = {},
	onLongClick: () -> Unit = onClick,
) {
	EntryComfortableGridItem(
		title = anime.title,
		coverData = AnimeCover(
			animeId = anime.id,
			sourceId = anime.source,
			isAnimeFavorite = anime.favorite,
			url = anime.thumbnailUrl,
			lastModified = anime.coverLastModified,
		),
		coverAlpha = if (anime.favorite) CommonEntryItemDefaults.BrowseFavoriteCoverAlpha else 1f,
		coverBadgeStart = {
			InLibraryBadge(enabled = anime.favorite)
		},
		onLongClick = onLongClick,
		onClick = onClick,
	)
}
