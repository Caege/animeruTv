package eu.kanade.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.SelectableSurfaceDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.DisposableEffectIgnoringConfiguration
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import eu.kanade.presentation.util.ScreenTransition
import eu.kanade.presentation.util.isTabletUi
import kotlinx.serialization.json.JsonNull.content
import tachiyomi.presentation.core.components.AdaptiveSheet as AdaptiveSheetImpl

@OptIn(InternalVoyagerApi::class)
@Composable
fun NavigatorAdaptiveSheet(
	screen: Screen,
	enableSwipeDismiss: (Navigator) -> Boolean = { true },
	onDismissRequest: () -> Unit,
) {
	Navigator(
		screen = screen,
		content = { sheetNavigator ->
			AdaptiveSheet(
				enableSwipeDismiss = enableSwipeDismiss(sheetNavigator),
				onDismissRequest = onDismissRequest,
			) {
				ScreenTransition(
					navigator = sheetNavigator,
					transition = {
						fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
							fadeOut(animationSpec = tween(90))
					},
				)

				BackHandler(
					enabled = sheetNavigator.size > 1,
					onBack = sheetNavigator::pop,
				)
			}
			// Make sure screens are disposed no matter what
			if (sheetNavigator.parent?.disposeBehavior?.disposeNestedNavigators == false) {
				DisposableEffectIgnoringConfiguration {
					onDispose {
						sheetNavigator.items
							.asReversed()
							.forEach(sheetNavigator::dispose)
					}
				}
			}
		},
	)
}

/**
 * Sheet with adaptive position aligned to bottom on small screen, otherwise aligned to center
 * and will not be able to dismissed with swipe gesture.
 *
 * Max width of the content is set to 460 dp.
 */


@Composable
fun AdaptiveSheet(
	onDismissRequest: () -> Unit,
	modifier: Modifier = Modifier,
	enableSwipeDismiss: Boolean = false,
	content: @Composable () -> Unit ,
) {
	val isTabletUi = isTabletUi()
	val (buttonFocus) = remember { FocusRequester.createRefs() }

	Dialog(
		onDismissRequest = onDismissRequest,
		properties = dialogProperties,
	) {


		//		add surface so it gains focus first

			AdaptiveSheetImpl(
				modifier = modifier,
				isTabletUi = isTabletUi,
				enableSwipeDismiss = enableSwipeDismiss,
				onDismissRequest = onDismissRequest,
			) {


					content()


			}


//		var color by remember { mutableStateOf(Green) }
//	Button(onClick = { /*TODO*/ }, Modifier.background(color).onFocusChanged { color = if(it.isFocused) Red else Green
//	}) {
//		Text(text = "In focus bitch")
//	}

	}
}

private val dialogProperties = DialogProperties(
	usePlatformDefaultWidth = false,
	decorFitsSystemWindows = true,
)
