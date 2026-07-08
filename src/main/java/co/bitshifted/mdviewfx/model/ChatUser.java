/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.model;

/**
 * Represents a chat user with a display name and image URL.
 *
 * @param name display name of the user
 * @param imageUrl URL or resource path to the user's avatar image
 */
public record ChatUser(String name, String imageUrl) {}
