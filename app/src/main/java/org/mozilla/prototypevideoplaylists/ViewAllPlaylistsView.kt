/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ViewAllPlaylistsViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val titleView = rootView.findViewById(R.id.playlistTitleView) as TextView
}

class ViewAllPlaylistsAdapter(private val firebaseRef: DatabaseReference,
                              private val onPlaylistSelected: (String, Playlist) -> Unit) : RecyclerView.Adapter<ViewAllPlaylistsViewHolder>() {

    private val valueListener = PlaylistValueEventListener(this)

    private var playlists = listOf<PlaylistAndID>()
        set(value) { field = value; notifyDataSetChanged() }

    override fun onBindViewHolder(holder: ViewAllPlaylistsViewHolder?, position: Int) {
        if (holder == null) return
        val playlistAndID = playlists[position]
        holder.rootView.setTag(playlistAndID)
        holder.titleView.text = playlistAndID.playlist.name
    }

    override fun getItemCount(): Int = playlists.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewAllPlaylistsViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.playlist_item, parent, false)
        view.setOnClickListener { view -> with(view.tag as PlaylistAndID) { onPlaylistSelected(id, playlist) } }
        return ViewAllPlaylistsViewHolder(view)
    }

    fun onPause() { firebaseRef.removeEventListener(valueListener) }
    fun onResume() { firebaseRef.addValueEventListener(valueListener) }

    private class PlaylistValueEventListener(val adapter: ViewAllPlaylistsAdapter) : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot?) {
            adapter.playlists = snapshot?.children?.map { PlaylistAndID(it.key, it.getValue(Playlist::class.java)) } ?: emptyList() // todo: will break if ever null.
        }

        override fun onCancelled(error: DatabaseError?) {
            Log.w(TAG, "Database listener failed: ${error}")
        }
    }
}

private data class PlaylistAndID(val id: String, val playlist: Playlist)
