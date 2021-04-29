package com.danegor.podlodkahw.ui.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.danegor.podlodkahw.R
import com.danegor.podlodkahw.Route
import com.danegor.podlodkahw.Session
import com.danegor.podlodkahw.ThemedPreview
import com.danegor.podlodkahw.data.SessionsRepository
import com.google.accompanist.glide.rememberGlidePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun SessionsListScreen(
    navController: NavController,
    sessionsRepository: SessionsRepository,
    onExit: () -> Unit
) {
    val viewModelFactory = remember { SessionsListViewModelFactory(sessionsRepository) }
    val viewModel = viewModel(SessionsListViewModel::class.java, factory = viewModelFactory)
    val state = viewModel.stateFlow.collectAsState()

    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(scaffoldState = scaffoldState) {
        SwipeRefresh(
            state = rememberSwipeRefreshState(state.value.isLoading),
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            SessionsList2(
                state = state.value,
                onSearchTextChanged = { viewModel.onSearchText(it) },
                onCardClick = { session ->
                    navController.navigate(Route.Info(session.id).route)
                },
                onFavouriteClick = { session, newValue ->
                    if (!viewModel.setSessionFavourite(session.id, newValue)) {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar("Не удалось добавить сессию в избранное")
                        }
                    }
                })
        }
    }

    val openExitDialog = remember { mutableStateOf(false) }
    BackHandler { openExitDialog.value = true }
    if (openExitDialog.value) {
        ExitDialog(
            onDismiss = { openExitDialog.value = false },
            onConfirm = onExit
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun SessionsList2(
    state: SessionsListScreenState,
    onSearchTextChanged: (String) -> Unit,
    onCardClick: (Session) -> Unit,
    onFavouriteClick: (Session, newValue: Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        state.list?.let {
            SessionsList(
                list = it,
                onSearchTextChanged = onSearchTextChanged,
                onCardClick = onCardClick,
                onFavouriteClick = onFavouriteClick
            )
        }

        if (state.isError) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize()) {
                        Text(
                            "При загрузке данных\nпроизошла ошибка",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(16.dp)
                                .align(Alignment.Center),
                            style = MaterialTheme.typography.h6.copy(color = Color.Red)
                        )
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun SessionsList(
    list: List<SessionUiModel>,
    onSearchTextChanged: (String) -> Unit,
    onCardClick: (Session) -> Unit,
    onFavouriteClick: (Session, newValue: Boolean) -> Unit
) {
    LazyColumn {
        item { SearchField(onTextChanged = onSearchTextChanged) }
        list.forEach { item ->
            when (item) {
                SessionFavouriteTitleUiModel -> item { SessionFavouriteTitle() }
                is SessionFavouritesUiModel -> item {
                    SessionFavouritesUiModel(
                        list = item.list,
                        onCardClick = onCardClick
                    )
                }
                SessionLinksTitleUiModel -> item { SessionLinksTitle() }
                is SessionDateTitleUiModel -> stickyHeader { SessionDateTitle(item.date) }
                is SessionCardUiModel -> item {
                    SessionCardUiModel(
                        session = item.session,
                        isFavourite = item.isFavourite,
                        onCardClick = onCardClick,
                        onFavouriteClick = onFavouriteClick
                    )
                }
            }
        }
    }
}

@Composable
fun ExitDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        text = {
            Text(
                "Вы уверены, что хотите выйти из приложения?",
                style = MaterialTheme.typography.body1
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    "ДА",
                    style = MaterialTheme.typography.button
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "ОТМЕНА",
                    style = MaterialTheme.typography.button
                )
            }
        }
    )
}

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

@Composable
fun SessionLinksTitle() {
    Text(
        "Сессии",
        modifier = Modifier.padding(8.dp),
        style = MaterialTheme.typography.h6
    )
}

@Composable
fun SessionDateTitle(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.background)
            .padding(8.dp)
    ) {
        Text(date, style = MaterialTheme.typography.subtitle2)
    }
}

@Composable
fun SessionCardUiModel(
    session: Session,
    isFavourite: Boolean,
    onCardClick: (Session) -> Unit,
    onFavouriteClick: (Session, newValue: Boolean) -> Unit
) {
    SessionCard(
        painter = rememberGlidePainter(request = session.imageUrl),
        speaker = session.speaker,
        timeInterval = session.timeInterval,
        description = session.description,
        isFavourite = isFavourite,
        onCardClick = { onCardClick(session) },
        onFavouriteClick = { onFavouriteClick(session, it) }
    )
}

@Composable
fun SessionCard(
    painter: Painter,
    speaker: String,
    timeInterval: String,
    description: String,
    isFavourite: Boolean,
    onCardClick: () -> Unit,
    onFavouriteClick: (newValue: Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onCardClick() },
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(32.dp))
                    .size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    speaker,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                )
                Text(
                    timeInterval,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                )
                Text(
                    description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                    ),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .clickable { onFavouriteClick(!isFavourite) }
                    .padding(16.dp)
            ) {
                if (isFavourite) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_favourite_on),
                        contentDescription = null,
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_favourite_off),
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
fun SearchField(
    onTextChanged: (String) -> Unit
) {
    val text = rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                border = BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        TextField(
            value = text.value,
            onValueChange = {
                text.value = it
                onTextChanged(it)
            },
            leadingIcon = {
                Image(
                    imageVector = Icons.Outlined.Search,
                    colorFilter = ColorFilter.tint(Color.LightGray),
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (text.value.isNotEmpty()) {
                    Image(
                        imageVector = Icons.Filled.Clear,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                text.value = ""
                                onTextChanged("")
                            }
                    )
                }
            },
            label = { Text("Поиск") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Preview
@Composable
fun PreviewSessionCard() {
    SessionCard(
        painter = ColorPainter(Color.Black),
        speaker = "speaker",
        timeInterval = "timeInterval",
        description = "description",
        isFavourite = true,
        onCardClick = {},
        onFavouriteClick = {}
    )
}

@Preview
@Composable
fun DarkPreviewSessionCard() {
    ThemedPreview(darkTheme = true) {
        PreviewSessionCard()
    }
}