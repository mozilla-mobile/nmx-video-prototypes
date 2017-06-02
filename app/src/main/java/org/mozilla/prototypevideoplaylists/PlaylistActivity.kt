/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_playlist.*

class PlaylistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)
        initToolbar()
        initPlaylistView()
    }

    private fun initToolbar() {
        toolbar.title = resources.getString(R.string.playlist_activity_title)
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        setSupportActionBar(toolbar)
    }

    private fun initPlaylistView() {
        playlistView.adapter = PlaylistAdapter(this)
        playlistView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
        startActivity(addPlaylistIntent)
    }
}

private class PlaylistAdapter(val context: Context) : RecyclerView.Adapter<PlaylistViewHolder>() {
    override fun onBindViewHolder(holder: PlaylistViewHolder?, position: Int) {
        if (holder == null) return
        holder.titleView.text = "Test title"
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }
}

private class PlaylistViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val titleView = rootView.findViewById(R.id.playlistTitleView) as TextView
}
