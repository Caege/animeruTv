package eu.kanade.tachiyomi.ui.player.viewer.components

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import dev.vivvvek.seeker.Seeker
import dev.vivvvek.seeker.SeekerDefaults
import dev.vivvvek.seeker.Segment
import eu.kanade.tachiyomi.ui.player.PlayerActivity
import eu.kanade.tachiyomi.ui.player.viewer.PlayerControlsView
import eu.kanade.tachiyomi.ui.player.viewer.SeekState
import eu.kanade.tachiyomi.util.view.setComposeContent
import `is`.xyz.mpv.MPVView.Chapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Seekbar(
	private val view: ComposeView,
	private val onValueChange: (Float, Boolean) -> Unit,
	private val onValueChangeFinished: (Float) -> Unit,
) {
	private var duration: Float = 1F
	private var value: Float = 0F
	private var readAheadValue: Float = 0F
	private var chapters: List<Chapter> = listOf()
	private var isDragging: Boolean = false
	fun updateSeekbar(
		duration: Float? = null,
		value: Float? = null,
		readAheadValue: Float? = null,
		chapters: List<Chapter>? = null,
	) {
		if (duration != null) {
			this.duration = duration
		}
		if (value != null) {
			this.value = value
		}
		if (readAheadValue != null) {
			this.readAheadValue = readAheadValue
		}
		if (chapters != null) {
			this.chapters = chapters
		}

		view.setComposeContent {
			SeekbarComposable(
				duration ?: this.duration,
				value ?: this.value,
				readAheadValue ?: this.readAheadValue,
				chapters?.toSegments()
					?: this.chapters.toSegments(),
			)
		}
	}

	@Composable
	private fun SeekbarComposable(
		duration: Float,
		value: Float,
		readAheadValue: Float,
		segments: List<Segment>,
	) {
		val range = 0F..duration
		val validSegments = segments.filter { it.start in range }
		var mutableValue by remember { mutableFloatStateOf(value) }
		val interactionSource = remember { MutableInteractionSource() }
		val isDragging by interactionSource.collectIsDraggedAsState()
		val gap by animateDpAsState(if (isDragging) 5.dp else 2.dp, label = "gap")
		val thumbRadius by animateDpAsState(if (isDragging) 10.dp else 8.dp, label = "thumbRadius")
		val trackHeight by animateDpAsState(
			targetValue = if (isDragging) 6.dp else 4.dp,
			label = "trackHeight",
		)
		val animateRight = remember { Animatable(0f) }
		var isRightKeyPressed by remember { mutableStateOf(false) } // To track if the button is being held
		var isLeftKeyPressed by remember {
			mutableStateOf(false)
		}
		val coroutineScope = rememberCoroutineScope()
		val wasDragging2 =  MutableStateFlow(this.isDragging)
//		var isDragging2 = this.isDragging

		fun setIsDragging(boolean: Boolean){
			this.isDragging = boolean
		}

		fun getWasDragging(): Boolean {
			return this.isDragging
		}

		// To launch coroutines

		LaunchedEffect(isRightKeyPressed, isLeftKeyPressed) {

			Log.d("buttonright3", "${animateRight.value}")
			if (isRightKeyPressed) {
				// Animate the value from current to 1000 over 1 second (1000 ms)
				animateRight.animateTo(
					targetValue = duration,
					animationSpec = tween(durationMillis = 20000),
				)





			}
			if (isLeftKeyPressed){
			animateRight.animateTo(
				targetValue = 0f,
				animationSpec = tween(durationMillis = 20000),
			)




			}



		}








		return Seeker(
			modifier = Modifier.onKeyEvent {
				if (it.key == Key.DirectionRight || it.key == Key.DirectionLeft) {
					if (it.key == Key.DirectionRight) {
						isRightKeyPressed = true
						this.isDragging = true

//						mutableValue = (value + 1f).coerceAtMost(duration)
						val wasDragging = this.isDragging
//						this.isDragging = true
						onValueChange(animateRight.value, wasDragging)
//						onValueChange(mutableValue, wasDragging)
						Log.d("buttonright2", "${animateRight.value}")


					}
					if (it.key == Key.DirectionRight && it.type == KeyEventType.KeyUp) {
						isRightKeyPressed = false
						if (this.isDragging) {
							onValueChangeFinished(animateRight.value.toFloat())

//							onValueChangeFinished(mutableValue)
							this.isDragging = false
						}
						//						Log.d("buttonright", "let go of right button")
					}

					if (it.key == Key.DirectionLeft) {
						isLeftKeyPressed = true
//						mutableValue = (value - 1f).coerceAtLeast(0f)
						val wasDragging = this.isDragging
						this.isDragging = true
						onValueChange(animateRight.value, wasDragging)
//						onValueChange(mutableValue, wasDragging)

					}
					if (it.key == Key.DirectionLeft && it.type == KeyEventType.KeyUp) {
						isLeftKeyPressed = false
						if (this.isDragging) {
							onValueChangeFinished(animateRight.value)
//							onValueChangeFinished(mutableValue)
							this.isDragging = false
						}
					}

					true
				} else {
					false
				}
			},
			//			this is stupid but change value to mutablevalue
//						value = value,
			value = animateRight.value.toFloat(),
			readAheadValue = readAheadValue,
			range = range,
			onValueChangeFinished = {
				//				if (this.isDragging) {
				//					onValueChangeFinished(mutableValue)
				//					this.isDragging = false
				//				}
			},
			onValueChange = {
				Log.d("shit", "${it}")
				//				mutableValue = it
				//				if (isDragging) {
				//					val wasDragging = this.isDragging
				//					this.isDragging = true
				//					onValueChange(mutableValue, wasDragging)
				//				} else {
				//					onValueChangeFinished(mutableValue)
				//				}
			},
			segments = validSegments,
			colors = SeekerDefaults.seekerColors(
				progressColor = MaterialTheme.colorScheme.primary,
				readAheadColor = MaterialTheme.colorScheme.onSurface,
				trackColor = MaterialTheme.colorScheme.surface,
				thumbColor = MaterialTheme.colorScheme.primary,
			),
			dimensions = SeekerDefaults.seekerDimensions(
				trackHeight = trackHeight,
				gap = gap,
				thumbRadius = thumbRadius,
			),
			interactionSource = interactionSource,
		)
	}
}

@Composable
private fun List<Chapter>.toSegments(): List<Segment> {
	return this.sortedBy { it.time }.map {
		// Color for AniSkip chapters
		val color = if (it.index == -2) {
			MaterialTheme.colorScheme.tertiary
		} else {
			Color.Unspecified
		}
		Segment(
			it.title ?: "",
			it.time.toFloat(),
			color,
		)
	}
}
