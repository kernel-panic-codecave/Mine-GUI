/*
 * Mine GUI
 * Copyright (C) 2022 WitherTech
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.withertech.mine_gui.ninepatch;

import java.util.Objects;

/**
 * Texture regions represent a partial region of a larger texture image.
 *
 * @param <T> the texture type
 */
public final class TextureRegion<T>
{
	/**
	 * The texture from which this region is cut.
	 */
	public final T texture;
	/**
	 * The left edge of this region as a fraction from 0 to 1.
	 */
	public final float u1;
	/**
	 * The top edge of this region as a fraction from 0 to 1.
	 */
	public final float v1;
	/**
	 * The right edge of this region as a fraction from 0 to 1.
	 */
	public final float u2;
	/**
	 * The bottom edge of this region as a fraction from 0 to 1.
	 */
	public final float v2;

	/**
	 * Constructs a texture region.
	 *
	 * @param texture the texture from which this region is cut
	 * @param u1      The left edge of this region as a fraction from 0 to 1
	 * @param v1      the top edge of this region as a fraction from 0 to 1
	 * @param u2      the right edge of this region as a fraction from 0 to 1
	 * @param v2      the bottom edge of this region as a fraction from 0 to 1
	 */
	public TextureRegion(T texture, float u1, float v1, float u2, float v2)
	{
		this.texture = texture;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TextureRegion<?> that = (TextureRegion<?>) o;
		return Float.compare(that.u1, u1) == 0 && Float.compare(that.v1, v1) == 0 && Float.compare(that.u2, u2) == 0 && Float.compare(that.v2, v2) == 0 && Objects.equals(texture, that.texture);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(texture, u1, v1, u2, v2);
	}

	@Override
	public String toString()
	{
		return "TextureRegion{" +
				"texture=" + texture +
				", u1=" + u1 +
				", v1=" + v1 +
				", u2=" + u2 +
				", v2=" + v2 +
				'}';
	}
}
