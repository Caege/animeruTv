package eu.kanade.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonNull.content
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.TabText
import tachiyomi.presentation.core.i18n.stringResource

object TabbedDialogPaddings {
	val Horizontal = 24.dp
	val Vertical = 8.dp
}

@Composable
fun TabbedDialogTest(
	onDismissRequest: () -> Unit,
	tabTitles: ImmutableList<String>,
	modifier: Modifier = Modifier,
	tabOverflowMenuContent: (@Composable ColumnScope.(() -> Unit) -> Unit)? = null,
	onOverflowMenuClicked: (() -> Unit)? = null,
	overflowIcon: ImageVector? = null,
	refList: List<FocusRequester>,
	pagerState: PagerState = rememberPagerState { tabTitles.size },
	content: @Composable (Int) -> Unit,
) {
	val (buttonFocus) = remember { FocusRequester.createRefs() }
	var selectedTab by remember { mutableStateOf(0) }


	AdaptiveSheet(
		modifier = modifier,
		onDismissRequest = onDismissRequest,
	) {
		val isSwipeEnabled by remember { mutableStateOf(true) }
		val scope = rememberCoroutineScope()
		val (buttonFocus) = remember { FocusRequester.createRefs() }
		Column {




			//			replace tabs with tabrow for tv
			Row(modifier = Modifier
				.fillMaxWidth()
				.height(32.dp), horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically) {
				TabRow(
					 selectedTabIndex = selectedTab,
					separator = { Spacer(modifier = Modifier.width(64.dp))},
					indicator = { tabPos, doesTab ->
						TabRowDefaults.UnderlinedIndicator(
							currentTabPosition = tabPos[selectedTab],
							doesTabRowHaveFocus = doesTab,
						)
					},

				) {


					tabTitles.forEachIndexed { index, tab ->
						Tab(

							selected = index == selectedTab,
							onFocus = {
								selectedTab = index
							},
							modifier = when {
								index == 0 -> Modifier
									.weight(1f)
									.focusRequester(buttonFocus)
								index == 2 -> Modifier
									.weight(1f)
									.focusProperties { down = refList[2] }
								else -> Modifier.weight(1f)
							},
						) {
							Text(tab, modifier = Modifier.padding( vertical = 8.dp))
						}
					}
				}
			}



			HorizontalDivider()

			Column(modifier = Modifier.animateContentSize()) {
				content(selectedTab)
			}


//			HorizontalPager(
//				userScrollEnabled = false,
//				modifier = Modifier
//					.animateContentSize()
//					.pointerInput(isSwipeEnabled) {
//						//					detectHorizontalDragGestures { _, _ ->
//						//						// Do nothing to disable swipe
//						//					}
//						//					awaitPointerEventScope {
//						//						awaitPointerEvent(PointerEventPass.Initial) // Consuming the event
//						//					}
//					},
//				state = pagerState,
//				verticalAlignment = Alignment.Top,
//				pageContent = { page -> content(page) },
//			)
//			// use something else instead of HorizontalPager
//			Column {
//			}


			LaunchedEffect(Unit) {
				buttonFocus.requestFocus()
			}
		}
	}
}

@Composable
fun TabbedDialog(
	onDismissRequest: () -> Unit,
	tabTitles: ImmutableList<String>,
	modifier: Modifier = Modifier,
	tabOverflowMenuContent: (@Composable ColumnScope.(() -> Unit) -> Unit)? = null,
	onOverflowMenuClicked: (() -> Unit)? = null,
	overflowIcon: ImageVector? = null,
	pagerState: PagerState = rememberPagerState { tabTitles.size },
	content: @Composable (Int) -> Unit,
) {
	val (buttonFocus) = remember { FocusRequester.createRefs() }


	AdaptiveSheet(
		modifier = modifier,
		onDismissRequest = onDismissRequest,
	) {
		val scope = rememberCoroutineScope()
		val (buttonFocus) = remember { FocusRequester.createRefs() }
		Column {
			Row {
				PrimaryTabRow(
					modifier = Modifier.weight(1f),
					selectedTabIndex = pagerState.currentPage,
					containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
					divider = {},
				) {
					tabTitles.fastForEachIndexed { index, tab ->
						Tab(
							selected = pagerState.currentPage == index,
							modifier = if (index == 0) Modifier.focusRequester(buttonFocus) else Modifier,
							onClick = {
								scope.launch {
									pagerState.animateScrollToPage(index)
								}
							},
							text = { TabText(text = tab) },
							unselectedContentColor = MaterialTheme.colorScheme.onSurface,
						)
					}
				}

				MoreMenu(onOverflowMenuClicked, tabOverflowMenuContent, overflowIcon)
			}
			HorizontalDivider()

			HorizontalPager(
				modifier = Modifier.animateContentSize(),
				state = pagerState,
				verticalAlignment = Alignment.Top,
				pageContent = { page -> content(page) },
			)


			LaunchedEffect(Unit) {
				buttonFocus.requestFocus()
			}
		}
	}
}

@Composable
private fun MoreMenu(
	onClickIcon: (() -> Unit)?,
	content: @Composable (ColumnScope.(() -> Unit) -> Unit)?,
	overflowIcon: ImageVector? = null,
) {
	if (onClickIcon == null && content == null) return
	var expanded by remember { mutableStateOf(false) }
	val onClick = onClickIcon ?: { expanded = true }

	Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
		IconButton(onClick = onClick) {
			Icon(
				imageVector = overflowIcon ?: Icons.Default.MoreVert,
				contentDescription = stringResource(MR.strings.label_more),
			)
		}
		if (onClickIcon == null) {
			DropdownMenu(
				expanded = expanded,
				onDismissRequest = { expanded = false },
			) {
				content!! { expanded = false }
			}
		}
	}
}
