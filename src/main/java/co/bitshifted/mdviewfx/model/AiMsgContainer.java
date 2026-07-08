/*
 * Copyright © 2026, Bitshift ED <https://bitshifted.co>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package co.bitshifted.mdviewfx.model;

import io.jstach.jstache.JStache;

/**
 * Container record for AI message metadata rendered by AI by templates.
 *
 * @param id unique id of the container element
 * @param aiLogoUrl URL or resource path to the AI's logo image
 * @param aiName display name of the AI
 */
@JStache(path = "templates/ai-msg-container-template.mustache")
public record AiMsgContainer(String id, String aiLogoUrl, String aiName) {}
