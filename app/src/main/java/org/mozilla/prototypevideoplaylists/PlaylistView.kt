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
import com.google.firebase.database.ValueEventListener

class PlaylistViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
    val titleView = rootView.findViewById(R.id.playlistTitleView) as TextView
}

class PlaylistAdapter(firebaseUserID: String) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private val fbRef = getFirebaseRefForUserID(firebaseUserID)
    private val valueListener = PlaylistValueEventListener(this)

    private var playlists = listOf<Playlist>()
        set(value) { field = value; notifyDataSetChanged() }

    override fun onBindViewHolder(holder: PlaylistViewHolder?, position: Int) {
        if (holder == null) return
        val item = playlists[position]
        holder.titleView.text = item.name
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(view)
    }

    fun onPause() { fbRef.removeEventListener(valueListener) }
    fun onResume() { fbRef.addValueEventListener(valueListener) }

    private class PlaylistValueEventListener(val adapter: PlaylistAdapter) : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot?) {
            adapter.playlists = snapshot?.children?.map { it.getValue(Playlist::class.java) } ?: emptyList() // todo: will break if ever null.
        }

        override fun onCancelled(error: DatabaseError?) {
            Log.w(TAG, "Database listener failed: ${error}")
        }
    }
}
