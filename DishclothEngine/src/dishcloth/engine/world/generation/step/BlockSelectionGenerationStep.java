package dishcloth.engine.world.generation.step;

import dishcloth.engine.world.block.BlockIDHelper;
import dishcloth.engine.world.block.BlockRegistry;
import dishcloth.engine.world.level.TerrainChunk;

/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * BlockSelectionGenerationStep.java
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * <p>
 * TODO: Description
 * <p>
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Created by ASDSausage on 10.7.2015
 */

public class BlockSelectionGenerationStep extends ATerrainGenerationStep {
	@Override
	public TerrainChunk onGenerateChunk(TerrainChunk targetChunk, float[] values, long seed, int chunkX, int chunkY) {
		for (int x = 0; x < TerrainChunk.CHUNK_SIZE; x++) {
			for (int y = 0; y < TerrainChunk.CHUNK_SIZE; y++) {
				if (values[x + y * TerrainChunk.CHUNK_SIZE] == 1.1f) {
					targetChunk.setBlock( chunkX * TerrainChunk.CHUNK_SIZE + x,
					                      chunkY * TerrainChunk.CHUNK_SIZE + y,
					                      BlockRegistry.getBlock( BlockIDHelper.getBlockID( "dishcloth:grass" ) ) );
				}
				else if (values[x + y * TerrainChunk.CHUNK_SIZE] != 0f) {
					targetChunk.setBlock( chunkX * TerrainChunk.CHUNK_SIZE + x,
					                      chunkY * TerrainChunk.CHUNK_SIZE + y,
					                      BlockRegistry.getBlock( BlockIDHelper.getBlockID( "dishcloth:dirt"  ) ) );
				}


			}
		}

		return targetChunk;
	}
}
