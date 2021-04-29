package com.danegor.podlodkahw.ui.list

import com.danegor.podlodkahw.Session

sealed class SessionUiModel
data class SessionCardUiModel(val session: Session, val isFavourite: Boolean) : SessionUiModel()
data class SessionDateTitleUiModel(val date: String) : SessionUiModel()
object SessionLinksTitleUiModel : SessionUiModel()
object SessionFavouriteTitleUiModel : SessionUiModel()
data class SessionFavouritesUiModel(val list: List<Session>) : SessionUiModel()
