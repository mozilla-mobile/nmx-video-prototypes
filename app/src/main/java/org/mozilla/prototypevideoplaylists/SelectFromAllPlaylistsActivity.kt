/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.content.Intent
import android.text.TextUtils
import android.util.Log

/** Activity to select one playlist from a list of all of them. */
class SelectFromAllPlaylistsActivity : AbstractAllPlaylistsActivity(R.string.select_from_all_activity_title) {

    override fun onPlaylistSelected(id: String, playlist: Playlist) {
        // todo: notify user of errors. maybe in onCreate.
        if (intent == null) {
            Log.w(TAG, "Given intent is null - nothing to do!")
            return
        }

        val pageTitle = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        val videoURI = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (TextUtils.isEmpty(videoURI)) {
            Log.w(TAG, "Given intent does not have URI data - nothing to do!")
            return
        }

        addVideoToPlaylist(pageTitle, videoURI, id, playlist)
        finish() // todo: confirmation animation.
    }

    private fun addVideoToPlaylist(videoTitle: String, videoURI: String, id: String, playlist: Playlist) {
        val usersFirebase = getFirebaseRefForUserID(getFirebaseUserID(this))
        val selectedPlaylist = usersFirebase.child(id)

        // Less efficient than adding a single element, but it was easier for me to write quickly.
        val newPlaylistItem = PlaylistItem(videoTitle, videoURI)
        val newPlaylist = playlist.withAppendedItem(newPlaylistItem)
        selectedPlaylist.setValue(newPlaylist) // todo: I assume it's fine if this runs in bg?
    }
}
