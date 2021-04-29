package com.danegor.podlodkahw.ui.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
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
import com.danegor.podlodkahw.ui.ExitDialog
import com.danegor.podlodkahw.ui.SearchField
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
            SessionsListStateRenderer(
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
fun SessionsListStateRenderer(
    state: SessionsListScreenState,
    onSearchTextChanged: (String) -> Unit,
    onCardClick: (Session) -> Unit,
    onFavouriteClick: (Session, newValue: Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        state.list?.let {
            SessionsList(
                list = it,
                highlightText = state.searchText,
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
    highlightText: String?,
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
                        onFavouriteClick = onFavouriteClick,
                        highlightText = highlightText
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
    onFavouriteClick: (Session, newValue: Boolean) -> Unit,
    highlightText: String? = null
) {
    SessionCard(
        painter = rememberGlidePainter(request = session.imageUrl),
        speaker = session.speaker,
        timeInterval = session.timeInterval,
        description = session.description,
        isFavourite = isFavourite,
        onCardClick = { onCardClick(session) },
        onFavouriteClick = { onFavouriteClick(session, it) },
        highlightText = highlightText
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
    onFavouriteClick: (newValue: Boolean) -> Unit,
    highlightText: String? = null
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
                    getHighlightedString(speaker, highlightText),
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
                    getHighlightedString(description, highlightText),
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

@Composable
fun getHighlightedString(text: String, highlightText: String?): AnnotatedString {
    return if (highlightText == null) {
        buildAnnotatedString { append(text) }
    } else {
        buildAnnotatedString {
            if (text.contains(highlightText, ignoreCase = true)) {
                val lowercase = text.toLowerCase(Locale.current)
                var start = 0
                var i = lowercase.indexOf(highlightText)
                while (i != -1) {
                    append(text.subSequence(start, i).toString())
                    withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                        append(text.subSequence(i, i + highlightText.length).toString())
                    }
                    start = i + highlightText.length
                    i = lowercase.indexOf(highlightText, start)
                }
                append(text.subSequence(start, text.length).toString())
            } else {
                append(text)
            }
        }
    }
}