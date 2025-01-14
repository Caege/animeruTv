package eu.kanade.presentation.browse.anime.components

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.tv.material3.Button
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import eu.kanade.presentation.browse.BrowseSourceLoadingItem
import eu.kanade.presentation.browse.InLibraryBadge
import eu.kanade.presentation.library.components.CommonEntryItemDefaults
import eu.kanade.presentation.library.components.EntryCompactGridItem
import kotlinx.coroutines.flow.StateFlow
import tachiyomi.domain.entries.anime.model.Anime
import tachiyomi.domain.entries.anime.model.AnimeCover
import tachiyomi.presentation.core.util.plus

@Composable
fun BrowseAnimeSourceCompactGrid(
    animeList: LazyPagingItems<StateFlow<Anime>>,
    columns: GridCells,
    contentPadding: PaddingValues,
    onAnimeClick: (Anime) -> Unit,
    onAnimeLongClick: (Anime) -> Unit,
) {
	val context = LocalContext.current
	LaunchedEffect(Unit) {
		Toast.makeText(context,"Compact", Toast.LENGTH_LONG).show()
	}

//	LazyVerticalGrid(
//        columns = columns,
//        contentPadding = contentPadding + PaddingValues(8.dp),
//        verticalArrangement = Arrangement.spacedBy(CommonEntryItemDefaults.GridVerticalSpacer),
//        horizontalArrangement = Arrangement.spacedBy(CommonEntryItemDefaults.GridHorizontalSpacer),
//    ) {
//        if (animeList.loadState.prepend is LoadState.Loading) {
//            item(span = { GridItemSpan(maxLineSpan) }) {
//                BrowseSourceLoadingItem()
//            }
//        }
//
//        items(count = animeList.itemCount) { index ->
//            val anime by animeList[index]?.collectAsState() ?: return@items
//			Surface(selected = false, onClick = { /*TODO*/ }) {
//				BrowseAnimeSourceCompactGridItem(
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
//
//
//    }

//	Column(modifier = Modifier.padding(contentPadding)) {
//		repeat(20){
//				index ->
//			Button(onClick = { /*TODO*/ }) {
//				Text(text = "Item #$index")
//			}
//		}
//	}


	LazyVerticalGrid(
		columns = columns, // Defines 2 columns
		modifier = Modifier
			.fillMaxSize()
			.padding(contentPadding),
		verticalArrangement = Arrangement.spacedBy(CommonEntryItemDefaults.GridVerticalSpacer),
		horizontalArrangement = Arrangement.spacedBy(CommonEntryItemDefaults.GridHorizontalSpacer),
	) {

		if (animeList.loadState.prepend is LoadState.Loading) {
			item(span = { GridItemSpan(maxLineSpan) }) {
				BrowseSourceLoadingItem()
			}
		}


		items(animeList.itemCount) { index ->
			val anime by animeList[index]?.collectAsState() ?: return@items
			Surface(selected = false, onClick = { onAnimeClick(anime) }) {
//				Button(onClick = { /*TODO*/ }) {
//					Text(text = "Item #$index")
//				}

				BrowseAnimeSourceCompactGridItem(
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



@Composable
private fun BrowseAnimeSourceCompactGridItem(
    anime: Anime,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = onClick,
) {
    EntryCompactGridItem(
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
