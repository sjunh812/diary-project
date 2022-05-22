package org.sjhstudio.diary.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReverseGeocoder(
    val status: RGStatus,
    val results: List<RGResults>
): Parcelable

@Parcelize
data class RGStatus(
    val code: Int,
    val name: String,
    val message: String
): Parcelable

@Parcelize
data class RGResults(
    val name: String,
    val code: Code,
    val region: Region
): Parcelable

@Parcelize
data class Code(
    val id: String,
    val type: String,
    val mappingId: String
): Parcelable

@Parcelize
data class Region(
    val area0: Area0,
    val area1: Area1,
    val area2: Area2,
    val area3: Area3,
    val area4: Area4
): Parcelable

@Parcelize
data class Area0(
    val name: String,
    val coords: Coords
): Parcelable

@Parcelize
data class Area1(
    val name: String,
    val coords: Coords
): Parcelable

@Parcelize
data class Area2(
    val name: String,
    val coords: Coords
): Parcelable

@Parcelize
data class Area3(
    val name: String,
    val coords: Coords
): Parcelable

@Parcelize
data class Area4(
    val name: String,
    val coords: Coords
): Parcelable

@Parcelize
data class Coords(
    val center: Center
): Parcelable

@Parcelize
data class Center(
    val crs: String,
    val x: Float,
    val y: Float
): Parcelable