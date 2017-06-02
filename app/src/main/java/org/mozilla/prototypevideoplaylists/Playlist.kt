/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.prototypevideoplaylists

// We give mutable & default values to be compatible with Firebase's setValue call.
data class Playlist(var name: String = "", var items: List<String> = emptyList())
