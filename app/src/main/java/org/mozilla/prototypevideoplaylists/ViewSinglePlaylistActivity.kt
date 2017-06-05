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
import kotlinx.android.synthetic.main.activity_view_single_playlist.*

class ViewSinglePlaylistActivity : AppCompatActivity() {

    companion object {
        private val INTENT_PREFIX = "org.mozilla.prototypevideoplaylists.ViewSinglePlaylistActivity"

        // Arguments.
        val EXTRA_PLAYLIST_ID = "${INTENT_PREFIX}.extra.playlistID"
    }

    private lateinit var adapter: ViewSinglePlaylistAdapter

    // todo: add firebase auth.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_playlist)
        initFromIntent() // must be called before methods below.
        initToolbar()
        initPlaylistView()
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
        val playlistID = intent?.getStringExtra(EXTRA_PLAYLIST_ID)
        if (playlistID == null) {
            Log.w(TAG, "ViewSinglePlaylistActivity: expected playlist ID. finishing...")
            finish()
            return
        }

        val playlistFirebaseRef = getFirebaseRefForUserID(getFirebaseUserID(this)).child(playlistID)
        adapter = ViewSinglePlaylistAdapter(playlistFirebaseRef, onTitleUpdate = {
            toolbar.title = it
        }, onVideoSelected = { title, url ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)) // todo: ensure http present?
            startActivity(browserIntent) // todo: helper fn.
        }) // todo: holds context?
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
