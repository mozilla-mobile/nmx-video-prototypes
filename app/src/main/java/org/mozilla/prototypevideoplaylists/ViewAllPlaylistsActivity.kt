/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.content.Intent

/** Activity to display all playlists and, when clicked into, their details. */
class ViewAllPlaylistsActivity : AbstractAllPlaylistsActivity(R.string.playlist_activity_title) {

    override fun onPlaylistSelected(id: String, playlist: Playlist) { startViewSinglePlaylistActivity(id) }

    private fun startViewSinglePlaylistActivity(playlistID: String) {
        val intent = Intent(this, ViewSinglePlaylistActivity::class.java)
        intent.putExtra(ViewSinglePlaylistActivity.EXTRA_PLAYLIST_ID, playlistID)
        startActivity(intent)
    }
}
