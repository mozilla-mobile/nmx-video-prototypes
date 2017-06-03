package org.mozilla.prototypevideoplaylists

import android.graphics.Color
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_single_playlist)
        initFromIntent() // must be called before methods below.
        initToolbar()
        initPlaylistView()
    }

    private fun initFromIntent() {
        val playlistID = intent?.getStringExtra(EXTRA_PLAYLIST_ID)
        if (playlistID == null) {
            Log.w(TAG, "ViewSinglePlaylistActivity: expected playlist ID. finishing...")
            finish()
            return
        }

        val playlistFirebaseRef = getFirebaseRefForUserID(getFirebaseUserID(this)).child(playlistID)
        adapter = ViewSinglePlaylistAdapter(playlistFirebaseRef, onTitleUpdate = { toolbar.title = it }) // todo: holds context?
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
