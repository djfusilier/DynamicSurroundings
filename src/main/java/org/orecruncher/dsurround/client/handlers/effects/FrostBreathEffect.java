/*
 * This file is part of Dynamic Surroundings, licensed under the MIT License (MIT).
 *
 * Copyright (c) OreCruncher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.orecruncher.dsurround.client.handlers.effects;

import java.util.List;

import javax.annotation.Nonnull;

import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.capabilities.CapabilitySeasonInfo;
import org.orecruncher.dsurround.client.effects.EntityEffect;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactory;
import org.orecruncher.dsurround.client.effects.IEntityEffectFactoryFilter;
import org.orecruncher.dsurround.client.effects.IEntityEffectHandlerState;
import org.orecruncher.dsurround.client.fx.particle.ParticleBreath;
import org.orecruncher.dsurround.registry.effect.EntityEffectInfo;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FrostBreathEffect extends EntityEffect {

	private static final int PRIME = 311;

	private int seed;

	@Override
	public String name() {
		return "Frost Breath";
	}

	@Override
	public void intitialize(@Nonnull final IEntityEffectHandlerState state) {
		super.intitialize(state);
		this.seed = state.subject().get().getEntityId() * PRIME;
	}

	@Override
	public void update(@Nonnull final Entity subject) {
		if (!ModOptions.effects.showBreath)
			return;

		final int interval = (int) (((getState().getWorldTime() + this.seed) / 10) % 8);
		if (interval < 3 && isPossibleToShow(subject)) {
			final EntityPlayer player = getState().thePlayer();
			if ((subject == player) || (!subject.isInvisibleToPlayer(player) && player.canEntityBeSeen(subject))) {
				getState().addParticle(new ParticleBreath(subject));
			}
		}
	}

	protected boolean isPossibleToShow(final Entity entity) {
		if (entity.isInsideOfMaterial(Material.AIR)) {
			final World world = entity.getEntityWorld();
			final BlockPos entityPos = entity.getPosition();
			return CapabilitySeasonInfo.getCapability(world).showFrostBreath(entityPos);
		}
		return false;
	}

	public static final IEntityEffectFactoryFilter DEFAULT_FILTER = (@Nonnull final Entity e,
			@Nonnull final EntityEffectInfo eei) -> eei.effects.contains("breath");

	public static class Factory implements IEntityEffectFactory {

		@Override
		public List<EntityEffect> create(@Nonnull final Entity entity, @Nonnull final EntityEffectInfo eei) {
			return ImmutableList.of(new FrostBreathEffect());
		}
	}

}
