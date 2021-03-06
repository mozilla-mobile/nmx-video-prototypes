/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
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

class AllPlaylistsViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val titleView = rootView.findViewById(R.id.playlistTitleView) as TextView
    val itemsView = rootView.findViewById(R.id.playlistItemsView) as TextView
    val shareView = rootView.findViewById(R.id.playlistShareView) as ImageView
    val deleteView = rootView.findViewById(R.id.playlistDeleteView) as ImageView

    fun setViewTags(tag: Any) {
        listOf(rootView, titleView, shareView, deleteView).forEach { it.setTag(tag) }
    }
}

class AllPlaylistsAdapter(private val firebaseRef: DatabaseReference,
                          private val onPlaylistSelected: (String, Playlist) -> Unit,
                          private val onShareSelected: (id: String, Playlist) -> Unit) : RecyclerView.Adapter<AllPlaylistsViewHolder>() {

    private val valueListener = PlaylistValueEventListener(this)

    private var playlists = listOf<PlaylistAndID>()
        set(value) { field = value; notifyDataSetChanged() }

    override fun onBindViewHolder(holder: AllPlaylistsViewHolder?, position: Int) {
        if (holder == null) return
        val playlistAndID = playlists[position]
        holder.setViewTags(playlistAndID)
        val playlist = playlistAndID.playlist
        holder.titleView.text = "${playlist.name} (${playlist.items.size} videos)" // todo: l10n
        // HACK: these should be separate textViews or SpannableStrings? so that the text doesn't wrap itself.
        holder.itemsView.text = playlist.items.take(3).fold("") { acc, item ->
            val title = item.title
            "${acc}\n${title.substring(0, Math.min(32, title.lastIndex))}..."
        }.drop(1) // rm first newline.

        holder.itemsView.visibility = if (TextUtils.isEmpty(holder.itemsView.text)) View.GONE else View.VISIBLE
    }

    override fun getItemCount(): Int = playlists.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AllPlaylistsViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.playlist_item, parent, false)
        val holder = AllPlaylistsViewHolder(view)
        holder.rootView.setOnClickListener { view -> with (view.tag as PlaylistAndID) { onPlaylistSelected(id, playlist) } }
        holder.shareView.setOnClickListener { view -> with (view.tag as PlaylistAndID) { onShareSelected(id, playlist) } }
        holder.deleteView.setOnClickListener { view -> with (view.tag as PlaylistAndID) { firebaseRef.child(id).removeValue() } }
        return holder
    }

    fun onPause() { firebaseRef.removeEventListener(valueListener) }
    fun onResume() { firebaseRef.addValueEventListener(valueListener) }

    private class PlaylistValueEventListener(val adapter: AllPlaylistsAdapter) : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot?) {
            // Called from main thread, no need to lock.
            adapter.playlists = snapshot?.children?.map { PlaylistAndID(it.key, it.getValue(Playlist::class.java)) } ?: emptyList() // todo: will break if ever null.
        }

        override fun onCancelled(error: DatabaseError?) {
            Log.w(TAG, "Database listener failed: ${error}")
        }
    }
}

private data class PlaylistAndID(val id: String, val playlist: Playlist)
