/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_view_all_playlists.*

class ViewAllPlaylistsActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    private val adapter = ViewAllPlaylistsAdapter(getFirebaseRefForUserID(getFirebaseUserID(this)), onPlaylistSelected = { id, playlist ->
        startViewSinglePlaylistActivity(id)
    }) // todo: reference to context okay?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all_playlists)
        initToolbar()
        initPlaylistView()
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.signInAnonymously() // todo: we should verify completion or figure out if it blocks.
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
        toolbar.title = resources.getString(R.string.playlist_activity_title)
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        setSupportActionBar(toolbar)
    }

    private fun initPlaylistView() {
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
        newPlaylistRef.setValue(playlist)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.playlist_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) return false
        when (item.itemId) {
            R.id.add_playlist -> onAddPlaylistClicked()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onAddPlaylistClicked() {
        val addPlaylistIntent = Intent(this, AddPlaylistActivity::class.java)
        startActivityForResult(addPlaylistIntent, 100)
    }

    private fun startViewSinglePlaylistActivity(playlistID: String) {
        val intent = Intent(this, ViewSinglePlaylistActivity::class.java)
        intent.putExtra(ViewSinglePlaylistActivity.EXTRA_PLAYLIST_ID, playlistID)
        startActivity(intent)
    }
}
