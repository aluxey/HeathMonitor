package com.cyberdoc.app.ui.figma.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    onNext: (done: Boolean) -> Unit,
    onSkip: () -> Unit,
) {
    val steps = remember {
        listOf(
            "Welcome to HealthMonitor" to "Your personal wellness companion for tracking health metrics and achieving goals.",
            "Track Your Health" to "Monitor steps, heart rate, sleep and activity in one simple dashboard.",
            "Set Your Goals" to "Define personalized goals and follow your progress with clear visual insights.",
            "Local and Private" to "All your health data stays on your device. No cloud sync, no sharing.",
        )
    }
    var currentStep by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(shape = RoundedCornerShape(30.dp), color = MaterialTheme.colorScheme.primary) {
                Box(modifier = Modifier.size(116.dp), contentAlignment = Alignment.Center) {
                    Text("HM", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = steps[currentStep].first,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = steps[currentStep].second,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(steps.size) { index ->
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(8.dp)
                        .width(if (index == currentStep) 28.dp else 8.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = if (index == currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                ) {}
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
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text(if (currentStep == steps.lastIndex) "Get Started" else "Continue")
        }
    }
}
