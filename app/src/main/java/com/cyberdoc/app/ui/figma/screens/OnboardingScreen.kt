package com.cyberdoc.app.ui.figma.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cyberdoc.app.ui.theme.Chart2
import com.cyberdoc.app.ui.theme.Chart3
import com.cyberdoc.app.ui.theme.Chart4

private data class OnboardingStepUi(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color,
)

@Composable
fun OnboardingScreen(
    onNext: (done: Boolean) -> Unit,
    onSkip: () -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val steps = remember(primaryColor) {
        listOf(
            OnboardingStepUi(
                icon = Icons.Rounded.FavoriteBorder,
                title = "Welcome to HealthMonitor",
                description = "Your personal wellness companion for tracking health metrics and achieving your wellness goals.",
                color = primaryColor,
            ),
            OnboardingStepUi(
                icon = Icons.Rounded.ShowChart,
                title = "Track Your Health",
                description = "Monitor steps, heart rate, sleep, and activity from one simple dashboard.",
                color = Chart2,
            ),
            OnboardingStepUi(
                icon = Icons.Rounded.TrackChanges,
                title = "Set Your Goals",
                description = "Define personalized targets and follow your progress with clear visual insights.",
                color = Chart3,
            ),
            OnboardingStepUi(
                icon = Icons.Rounded.Shield,
                title = "Local & Private",
                description = "All your health data stays on your device. No cloud sync, no sharing, completely private.",
                color = Chart4,
            ),
        )
    }
    var currentStep by remember { mutableStateOf(0) }
    val step = steps[currentStep]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = "Skip",
                modifier = Modifier.clickable(onClick = onSkip),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = step.color,
                        shape = RoundedCornerShape(28.dp),
                    )
                    .padding(28.dp),
            ) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = step.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(steps.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .background(
                                color = if (index == currentStep) {
                                    primaryColor
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = RoundedCornerShape(999.dp),
                            )
                            .width(if (index == currentStep) 28.dp else 8.dp)
                            .height(8.dp),
                    )
                }
            }

            Button(
                onClick = {
                    if (currentStep == steps.lastIndex) {
                        onNext(true)
                    } else {
                        currentStep += 1
                        onNext(false)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(
                    text = if (currentStep == steps.lastIndex) "Get Started" else "Continue",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.size(6.dp))
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                )
            }
        }
    }
}
