/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_all_playlists.*

/**
 * A base class for the activities which show a list of all playlists.
 *
 * Note: composition over inheritance – I wonder if I thought a little longer if I could simplify this with this
 * principle. But it's a prototype likely to change so I'll pass for now.
 */
abstract class AbstractAllPlaylistsActivity(@StringRes private val toolbarTitleResource: Int) : AppCompatActivity() {

    abstract fun onShareSelected(id: String, playlist: Playlist)
    abstract fun onPlaylistSelected(id: String, playlist: Playlist)

    private lateinit var adapter: AllPlaylistsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_playlists)
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

    private fun initToolbar() {
        toolbar.title = resources.getString(toolbarTitleResource)
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        setSupportActionBar(toolbar)
    }

    private fun initPlaylistView() {
        adapter = AllPlaylistsAdapter(getFirebaseRefForUserID(getFirebaseUserID(this)),
                onPlaylistSelected = this::onPlaylistSelected,
                onShareSelected = this::onShareSelected)
        playlistView.adapter = adapter
        playlistView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        playlistView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) { super.onActivityResult(requestCode, resultCode, data); return }
        when (data.action) {
            AddPlaylistActivity.ACTION_ACTIVITY_RESULT -> onAddPlaylistActivityResult(data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun onAddPlaylistActivityResult(data: Intent) {
        val playlistTitle = data.getStringExtra(AddPlaylistActivity.EXTRA_PLAYLIST_TITLE) ?: ""

        // todo: maybe we should use playlist title in some way for ID.
        val newPlaylistRef = getFirebaseRefForUserID(getFirebaseUserID(this)).push()
        val playlist = Playlist(playlistTitle, items = emptyList())
        newPlaylistRef.setValue(playlist) // UI updates from listener.
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.playlist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return false
        when (item.itemId) {
            R.id.add_playlist -> onAddPlaylistClicked()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onAddPlaylistClicked() {
        val addPlaylistIntent = Intent(this, AddPlaylistActivity::class.java)
        startActivityForResult(addPlaylistIntent, 100)
    }
}
