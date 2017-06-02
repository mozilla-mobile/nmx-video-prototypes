/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import java.util.*

private val FIREBASE_USERS_TREE = "users"

private val SHARED_PREFS_NAME = "Firebase"
private val KEY_FIREBASE_ID = "firebase-id"

private val firebaseRef = FirebaseDatabase.getInstance().reference

fun getFirebaseUserID(context: Context): String {
    return "test-user" // todo: replace.
    val sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, 0)
    return sharedPrefs.getString(KEY_FIREBASE_ID, null) ?: {
        val newID = UUID.randomUUID().toString()
        sharedPrefs.edit().putString(KEY_FIREBASE_ID, newID).apply()
        newID
    }()
}

fun getFirebaseRefForUserID(userID: String) = firebaseRef.child(FIREBASE_USERS_TREE).child(userID)
