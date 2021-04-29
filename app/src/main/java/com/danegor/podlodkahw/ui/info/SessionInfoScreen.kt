package com.danegor.podlodkahw.ui.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.glide.rememberGlidePainter

@Composable
fun SessionInfoScreen(sessionId: String?, navController: NavController) {
    if (sessionId == null) {
        navController.popBackStack()
        return
    }

    val viewModel = viewModel(SessionInfoViewModel::class.java)
    val session = viewModel.dataFlow(sessionId)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        SessionInfo(
            painter = rememberGlidePainter(request = session.imageUrl),
            speaker = session.speaker,
            date = session.date,
            timeInterval = session.timeInterval,
            description = session.description
        )
    }
}


@Composable
fun SessionInfo(
    painter: Painter,
    speaker: String,
    date: String,
    timeInterval: String,
    description: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .clip(shape = RoundedCornerShape(125.dp))
                .size(250.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            speaker,
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Image(
                imageVector = Icons.Outlined.CalendarToday,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "$date, $timeInterval",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                ),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            description,
            modifier = Modifier.align(Alignment.Start),
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            ),
        )
    }
}

@Preview
@Composable
fun PreviewSessionInfo() {
    SessionInfo(
        painter = ColorPainter(Color.Black),
        speaker = "speaker",
        date = "date",
        timeInterval = "timeInterval",
        description = "description"
    )
}