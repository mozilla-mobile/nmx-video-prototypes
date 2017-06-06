/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class ViewSinglePlaylistViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val videoTitleView = rootView.findViewById(R.id.videoTitleView) as TextView
    val videoURLView = rootView.findViewById(R.id.videoURLView) as TextView
    val videoDeleteView = rootView.findViewById(R.id.videoDeleteView) as ImageView
}

// Note: it's kind of weird we do title updates here - we might want to move it to the activity.
class ViewSinglePlaylistAdapter(private val firebaseRef: DatabaseReference,
                                private val onTitleUpdate: (title: String) -> Unit,
                                private val onVideoSelected: (title: String, url: String) -> Unit):
        RecyclerView.Adapter<ViewSinglePlaylistViewHolder>() {

    private val valueListener = ViewSinglePlaylistValueEventListener(this)

    private var playlist: Playlist = Playlist()
        set(value) { field = value; notifyDataSetChanged() }

    override fun onBindViewHolder(holder: ViewSinglePlaylistViewHolder?, position: Int) {
        if (holder == null) return
        val item = playlist.items[position]
        holder.rootView.tag = PlaylistItemAndIndex(position, item)
        holder.videoTitleView.text = item.title
        holder.videoURLView.text = item.uri
    }

    override fun getItemCount(): Int = playlist.items.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewSinglePlaylistViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.video_item, parent, false)
        val holder = ViewSinglePlaylistViewHolder(view)
        holder.rootView.setOnClickListener { with((view.tag as PlaylistItemAndIndex).item) { onVideoSelected(title, uri) } }
        holder.videoDeleteView.setOnClickListener { with(view.tag as PlaylistItemAndIndex) { deleteItemAtIndex(index) } }
        return holder
    }

    private fun deleteItemAtIndex(index: Int) {
        // Not super efficient but easy to code!
        val mutableItems = playlist.items.toMutableList()
        mutableItems.removeAt(index)
        val newPlaylist = Playlist(playlist.name, mutableItems)
        firebaseRef.setValue(newPlaylist)
    }

    fun onResume() { firebaseRef.addValueEventListener(valueListener) }
    fun onPause() { firebaseRef.removeEventListener(valueListener) }

    private class ViewSinglePlaylistValueEventListener(val adapter: ViewSinglePlaylistAdapter) : ValueEventListener {
        override fun onCancelled(error: DatabaseError?) {
            Log.w(TAG, "SinglePlaylist received error: ${error}")
        }

        override fun onDataChange(snapshot: DataSnapshot?) {
            val playlist = snapshot?.getValue(Playlist::class.java) ?: Playlist()
            adapter.playlist = playlist
            adapter.onTitleUpdate(playlist.name)
        }
    }
}

private data class PlaylistItemAndIndex(val index: Int, val item: PlaylistItem)
