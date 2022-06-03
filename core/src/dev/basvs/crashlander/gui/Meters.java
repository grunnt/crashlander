package dev.basvs.crashlander.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import dev.basvs.crashlander.CrashLander;

/**
 * Copyright (c) 2013, Bas van Schoonhoven. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class Meters {
	BitmapFont font42;
	GlyphLayout glyphLayout = new GlyphLayout();
	StringBuilder sb = new StringBuilder();

	public Meters() {
		font42 = CrashLander.getInstance().getAssets().get("data/fonts/iceland42.fnt");
	}

	public void render(float altitude, float xv, float yv, SpriteBatch batch) {
		font42.setColor(Color.WHITE);
		// Show altitude
		sb.setLength(0);
		sb.append("ALT:");
		sb.append((int) (altitude));
		sb.append("m");
		glyphLayout.setText(font42, sb);
		font42.draw(batch, sb, Gdx.graphics.getWidth() / 2 - glyphLayout.width / 2, Gdx.graphics.getHeight());
		// Show horizontal velocity
		sb.setLength(0);
		sb.append("XV:");
		sb.append((int) (xv));
		sb.append("m/s");
		glyphLayout.setText(font42, sb);
		font42.draw(batch, sb, 0, Gdx.graphics.getHeight());
		// Show vertical velocity
		sb.setLength(0);
		sb.append("YV:");
		sb.append((int) (yv));
		sb.append("m/s");
		glyphLayout.setText(font42, sb);
		font42.draw(batch, sb, Gdx.graphics.getWidth() - glyphLayout.width, Gdx.graphics.getHeight());
	}
}
