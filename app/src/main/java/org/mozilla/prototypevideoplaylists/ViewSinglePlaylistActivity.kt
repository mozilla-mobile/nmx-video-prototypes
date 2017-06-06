/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_view_single_playlist.*

class ViewSinglePlaylistActivity : AppCompatActivity() {

    companion object {
        private val INTENT_PREFIX = "org.mozilla.prototypevideoplaylists.ViewSinglePlaylistActivity"

        // Arguments.
        val ACTION_VIEW_LOCAL_PLAYLIST = "${INTENT_PREFIX}.action.viewLocalPlaylist"
        val EXTRA_PLAYLIST_ID = "${INTENT_PREFIX}.extra.playlistID"
    }

    private lateinit var adapter: ViewSinglePlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_playlist)
        initFromIntent() // must be called before methods below.
        initToolbar()
        initPlaylistView()
    }

    override fun onStart() {
        super.onStart()
        onStartSignIntoFirebase()
    }

    override fun onResume() {
        super.onResume()
        adapter.onResume()
    }

    override fun onPause() {
        super.onPause()
        adapter.onPause()
    }

    private fun initFromIntent() {
        val intent = intent
        if (intent == null) { Log.w(TAG, "Intent unexpectedly null."); finish(); return }

        val playlist = when (intent.action) {
            ACTION_VIEW_LOCAL_PLAYLIST -> getPlaylistFromLocalPlaylistIntent(intent)
            else -> getPlaylistFromSharedPlaylistIntent(intent)
        }

        if (playlist == null) {
            Toast.makeText(this, "Error! Unable to get the desired playlist.", Toast.LENGTH_SHORT).show()
            return
        }

        val playlistFirebaseRef = getFirebaseRefForUserID(playlist.userID).child(playlist.playlistID)
        adapter = ViewSinglePlaylistAdapter(playlistFirebaseRef, onTitleUpdate = {
            toolbar.title = it
        }, onVideoSelected = { title, url ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)) // todo: ensure http present?
            startActivity(browserIntent) // todo: helper fn.
        }) // todo: holds context?
    }

    private fun getPlaylistFromSharedPlaylistIntent(intent: Intent): UserPlaylist? {
        val playlist = getPlaylistFromFirebaseURI(intent.dataString ?: "")
        if (playlist == null) { Log.w(TAG, "Unable to create playlist from uri: ${intent.dataString ?: "null"}") }
        return playlist
    }

    private fun getPlaylistFromLocalPlaylistIntent(intent: Intent): UserPlaylist? {
        val playlistID = intent.getStringExtra(EXTRA_PLAYLIST_ID)
        return if (playlistID != null) { UserPlaylist(userID = getFirebaseUserID(this), playlistID = playlistID) }
        else {
            Log.w(TAG, "ViewSinglePlaylistActivity: expected playlist ID.")
            null
        }
    }

    private fun initToolbar() { // todo: add up button.
        toolbar.title = "" // override default application title.
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        setSupportActionBar(toolbar)
    }

    private fun initPlaylistView() {
        playlistView.adapter = adapter
        playlistView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        playlistView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }
}
