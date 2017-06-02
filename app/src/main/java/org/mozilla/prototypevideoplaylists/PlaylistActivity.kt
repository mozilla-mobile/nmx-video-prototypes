/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        setSupportActionBar(toolbar)
        val appBar = supportActionBar!!
        appBar.title = resources.getString(R.string.playlist_activity_title)
    }

    private fun initPlaylistView() {
        playlistView.adapter = PlaylistAdapter(this)
        playlistView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
