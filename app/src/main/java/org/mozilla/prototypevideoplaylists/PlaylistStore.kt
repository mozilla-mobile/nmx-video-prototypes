/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

import android.support.annotation.WorkerThread
import android.util.Log
import com.beust.klaxon.*
import java.io.File

// TODO: just use Firebase directly, stupid.
class PlaylistStore(val name: String) {

    // {version: <version>,
    //  playlists: [{
    //      "playlist-name",
    //      "playlist-items: { <String>, ... }
    //  }, ...]}
    private val KEY_VERSION = "version"
    private val KEY_PLAYLISTS = "playlists"
    private val KEY_PLAYLIST_NAME = "playlist-name"
    private val KEY_PLAYLIST_ITEMS = "playlist-items"

    private val VERSION = 1
    private val DEFAULT_CONTAINER: JsonObject
        get() = JsonObject(mapOf(KEY_VERSION to VERSION))

    private val file: File
        get() { val file = File("PlaylistStore-${name}"); file.createNewFile(); return file }

    @WorkerThread
    fun getAll(): List<Playlist> {
        val container = getContainerFromDisk()
        return listOf() // todo
    }

    // todo: readability fn?
    @WorkerThread
    fun append(playlist: Playlist) {
        val container = getContainerFromDisk()
        val playlists = container.array<JsonObject>(KEY_PLAYLISTS) ?: JsonArray()
        playlists.add(JsonObject(mapOf(
                KEY_PLAYLIST_NAME to playlist.name,
                KEY_PLAYLIST_ITEMS to JsonArray(playlist.items)
        )))
        container.set(KEY_PLAYLISTS, playlists)

        file.writeText(container.toJsonString(prettyPrint = false))
    }

    private fun isLatestVersion(container: JsonObject) = container.int(KEY_VERSION) == VERSION

    @WorkerThread
    private fun getContainerFromDisk(): JsonObject {
        val fileContents = file.readText()
        return if (fileContents.isEmpty()) {
            DEFAULT_CONTAINER
        } else {
            val containerFromFile = Parser().parse(StringBuilder(fileContents)) as JsonObject
            if (isLatestVersion(containerFromFile)) {
                containerFromFile
            } else {
                Log.w(TAG, "Given playlist file is wrong version - invalidating")
                DEFAULT_CONTAINER
            }
        }
    }
}
