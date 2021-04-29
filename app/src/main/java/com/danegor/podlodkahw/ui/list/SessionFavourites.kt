package com.danegor.podlodkahw.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danegor.podlodkahw.data.Session

@Composable
fun SessionFavouriteTitle() {
    Text(
        "Избранное",
        modifier = Modifier.padding(8.dp),
        style = MaterialTheme.typography.h6
    )
}

@Composable
fun SessionFavouritesUiModel(
    list: List<Session>,
    onCardClick: (Session) -> Unit
) {
    LazyRow {
        items(count = list.size) { index ->
            val session = list[index]
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .size(150.dp, 150.dp)
                    .padding(8.dp)
                    .clickable { onCardClick(session) },
                elevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        session.timeInterval,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                        ),
                    )
                    Text(
                        session.date,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                        ),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        session.speaker,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                        ),
                    )
                    Text(
                        session.description,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                        ),
                    )
                }
            }
        }
    }
}