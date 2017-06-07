/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*

val FIREBASE_KEY_PLAYLIST_ITEMS = "items"

private val FIREBASE_USERS_TREE = "users"

private val SHARED_PREFS_NAME = "Firebase"
private val KEY_FIREBASE_ID = "firebase-id"

private val firebaseRef by lazy {
    val db = FirebaseDatabase.getInstance()
    db.setPersistenceEnabled(true)
    db.reference
}

fun getFirebaseUserID(context: Context): String {
    val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, 0)
    return sharedPrefs.getString(KEY_FIREBASE_ID, null) ?: {
        val newID = UUID.randomUUID().toString()
        sharedPrefs.edit().putString(KEY_FIREBASE_ID, newID).apply()
        newID
    }()
}

fun getFirebaseRefForUserID(userID: String) = firebaseRef.child(FIREBASE_USERS_TREE).child(userID)

private val WEBSITE_URI = "https://prototypevideoplaylists.firebaseapp.com"
fun getFirebaseURIForPlaylist(userID: String, playlistID: String) = "${WEBSITE_URI}/${userID}/${playlistID}"
fun getPlaylistFromFirebaseURI(uri: String): UserPlaylist? {
    // A better impl would use regex.
    val pathComponents = uri.split('/')
    if (!uri.startsWith(WEBSITE_URI) || // verify our url.
            pathComponents.size < 2) { Log.w(TAG, "Unexpected uri type: ${uri}"); return null }
    val importantComponents = pathComponents.takeLast(2)
    return UserPlaylist(importantComponents[0], importantComponents[1])
}

data class UserPlaylist(val userID: String, val playlistID: String)

private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
/** Signs into firebase; should be called from onStart. */
fun onStartSignIntoFirebase() {
    firebaseAuth.signInAnonymously() // todo: block until done or verify blocks other calls until done?
}
