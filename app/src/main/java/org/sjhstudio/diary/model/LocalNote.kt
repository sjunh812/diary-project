package org.sjhstudio.diary.model

import org.sjhstudio.diary.note.Note
import java.io.Serializable

data class LocalNote(
    val weatherIndex: Int,
    val date: String?,
    val address: String,
    val moodIndex: Int,
    val contents: String,
    val filePaths: String,
    val starIndex: Int,
    val updateNote: Note? = null
) : Serializable