/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_playlist.*

class AddPlaylistActivity : AppCompatActivity() {

    companion object {
        private val intentPrefix = "org.mozilla.prototypevideoplaylists"

        // Return value.
        val ACTION_ACTIVITY_RESULT = "${intentPrefix}.action.addedPlaylists"
        val EXTRA_PLAYLIST_TITLE = "${intentPrefix}.extra.playlistTitle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_playlist)
        initButtons()
    }

    private fun initButtons() {
        cancelButton.setOnClickListener { finish() }
        submitButton.setOnClickListener {
            setResult(Activity.RESULT_OK, getResultIntent())
            finish()
        }
    }

    private fun getResultIntent(): Intent {
        val resultIntent = Intent(ACTION_ACTIVITY_RESULT)
        resultIntent.putExtra(EXTRA_PLAYLIST_TITLE, titleEditText.text.toString())
        return resultIntent
    }
}
