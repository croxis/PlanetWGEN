/*
 * This file is part of PlanetWGEN.
 *
 * Copyright (c) ${project.inceptionYear}-2012, croxis <https://github.com/croxis/>
 *
 * PlanetWGEN is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlanetWGEN is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PlanetWGEN. If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This file is part of UltraWGEN.
 *
 * Copyright (c) ${project.inceptionYear}-2012, croxis <https://github.com/croxis/>
 *
 * UltraWGEN is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UltraWGEN is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with UltraWGEN. If not, see <http://www.gnu.org/licenses/>.
 */
package net.croxis.plugins;


import java.util.HashMap;
import java.util.HashSet;

import org.spout.api.generator.GeneratorPopulator;
import org.spout.api.generator.Populator;
import org.spout.api.generator.biome.BiomeSelector;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.discrete.Point;
import org.spout.api.math.Vector3;
import org.spout.api.util.cuboid.CuboidBlockMaterialBuffer;
import org.spout.vanilla.plugin.material.VanillaMaterials;
import org.spout.vanilla.plugin.material.block.Liquid;
import org.spout.vanilla.plugin.world.generator.VanillaGenerator;
import org.spout.vanilla.plugin.world.generator.VanillaGenerators;
import org.spout.vanilla.plugin.world.generator.normal.NormalGenerator;

public class Planetgenerator implements VanillaGenerator{
	
	Vector3 center = new Vector3(0, 0, 0);
	//int radius = 32;
	//Earth radius values to have a one block thick crust
	int icore = 32;
	int ocore = 92;
	int mantle = 167;
	int crust = 168;
	//Scale factor: How much larger do we want the crust to be?
	int scale = 1;
	int x = 0;
	int y = 0;
	int z = 0;
	Vector3 blockSpot = new Vector3(0, 0, 0);
	
	//debug purposes
	public HashMap<Integer, HashSet<Integer>> unloadedChunks = new HashMap<Integer, HashSet<Integer>>();
	
	//public Planetgenerator(){
		// Precalculate max y values for a chunk
		
	//}

	public void generate(CuboidBlockMaterialBuffer blockData, int chunkX,
			int chunkY, int chunkZ, World world) {
		long time = System.currentTimeMillis();
		/*System.out.println("Starting Chunk: " 
				+ Integer.toString(chunkX) + ", " 
				+ Integer.toString(chunkY) + ", "
				+ Integer.toString(chunkZ));*/
		
		//if (Math.abs(chunkX) + 1 > crust * scale / 16.0)
		//	if (Math.abs(chunkZ) + 1 > crust * scale / 16.0)
		//		if(Math.abs(chunkY) + 1 > crust * scale / 256.0){
		//			System.out.println("Skipping the following chunk: " + Integer.toString(chunkX) + ", " + Integer.toString(chunkY) + ", "+ Integer.toString(chunkZ));
					//return;
		//		}
		
		double distance = 0;
		int trueX = 0;
		int trueZ = 0;
		int trueY = 0;
		
		//for (x = 0; x < 16; x++){
		for (x = 0; x < blockData.getSize().getX(); x++){
			for (z = 0; z < blockData.getSize().getZ(); z++){
				for (y = 0; y < blockData.getSize().getY(); y++){

					trueX = x + chunkX * 16;
					trueZ = z + chunkZ * 16;
					trueY = y + (chunkY << 4);
					blockSpot = new Vector3(trueX, trueY, trueZ);
					distance = blockSpot.distance(center);
					if (distance < icore * scale){
						blockData.set(trueX, trueY, trueZ, VanillaMaterials.IRON_BLOCK);
					} else if (distance <= ocore * scale && distance > icore * scale)
						blockData.set(trueX, trueY, trueZ, VanillaMaterials.LAVA);
					else if (distance <= mantle * scale && distance > ocore * scale)
						blockData.set(trueX, trueY, trueZ, VanillaMaterials.NETHERRACK);
					else if (distance <= crust - 1 * scale && distance > mantle * scale)
						blockData.set(trueX, trueY, trueZ, VanillaMaterials.STONE);
					else if (distance <= crust * scale && distance > crust -1 * scale)
						blockData.set(trueX, trueY, trueZ, VanillaMaterials.DIRT);
					else if (distance < crust * scale && distance > crust * scale - 1)
						blockData.set(trueX, trueY, trueZ, VanillaMaterials.GRASS);
				}
			}
		}
		
		System.out.println("Generation time for Chunk: " 
				+ Integer.toString(chunkX) + ", " 
				+ Integer.toString(chunkY) + ", "
				+ Integer.toString(chunkZ) + ": " 
				+ Long.toString(System.currentTimeMillis() - time) + " ms. Size: "
				+ blockData.getSize().toString());
		
		//We are debugging this bad boy!
		unloadedChunks.get(chunkX).remove(chunkZ);
		if (unloadedChunks.get(chunkX).isEmpty())
			unloadedChunks.remove(chunkX);
		
		//System.out.println(unloadedChunks.toString());
		
	}

	public int[][] getSurfaceHeight(World world, int chunkX, int chunkZ) {
		//From Vanilla
		int[][] heights = new int[Chunk.BLOCKS.SIZE][Chunk.BLOCKS.SIZE];
		for (int x = 0; x < Chunk.BLOCKS.SIZE; x++) {
			for (int z = 0; z < Chunk.BLOCKS.SIZE; z++) {
				heights[x][z] = NormalGenerator.SEA_LEVEL;
			}
		}
		return heights;
	}
	
	private int getHighestSolidBlock(World world, int x, int z) {
		int y = world.getHeight() - 1;
		while (world.getBlockMaterial(x, y, z) == VanillaMaterials.AIR) {
			y--;
			if (y == 0 || world.getBlockMaterial(x, y, z) instanceof Liquid) {
				return -1;
			}
		}
		return ++y;
	}

	public String getName() {
		return "UltraWGEN";
	}

	public Populator[] getPopulators() {
		//return VanillaGenerators.NORMAL.getPopulators();
		return new Populator[0];
	}

	public GeneratorPopulator[] getGeneratorPopulators() {
		//return VanillaGenerators.NORMAL.getGeneratorPopulators();
		return new GeneratorPopulator[0];
	}

	public Point getSafeSpawn(World world) {
		return new Point(world, 0, crust * scale + 3, 0);
		//return new Point(world, 0, 255, 0);
		/*short shift = 0;
		final BiomeSelector selector = getSelector();
		while (LogicUtil.equalsAny(selector.pickBiome(shift, 0, world.getSeed()),
		VanillaBiomes.OCEAN, VanillaBiomes.BEACH, VanillaBiomes.RIVER,
		VanillaBiomes.SWAMP, VanillaBiomes.MUSHROOM_SHORE, VanillaBiomes.MUSHROOM)
		&& shift < 1600) {
		shift += 16;
		}
		final Random random = new Random();
		for (byte attempts = 0; attempts < 32; attempts++) {
		final int x = random.nextInt(256) - 127 + shift;
		final int z = random.nextInt(256) - 127;
		final int y = getHighestSolidBlock(world, x, z);
		if (y != -1) {
		return new Point(world, x, y + 0.5f, z);
		}
		}
		return new Point(world, shift, 80, 0);*/
	}

	private BiomeSelector getSelector() {
		return VanillaGenerators.NORMAL.getSelector();
	}
	

}
