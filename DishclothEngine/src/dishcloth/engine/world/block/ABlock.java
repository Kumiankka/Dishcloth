package dishcloth.engine.world.block;

import dishcloth.engine.rendering.IRenderer;
import dishcloth.engine.rendering.render2d.SpriteBatch;

/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * ABlock.java
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * <p>
 * TODO: Description
 * <p>
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Created by ASDSausage on 11.6.2015
 */

public abstract class ABlock implements IBlock {

	private BlockID blockID;

	final void setBlockID(BlockID id) {
		this.blockID = id;
	}

	@Override
	public BlockID getBlockID() {
		return blockID;
	}

	@Override
	public int getFrameID() {
		return 0;
	}

	@Override
	public void render(IRenderer renderer, SpriteBatch spriteBatch) {

	}
}
