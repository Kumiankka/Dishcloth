package dishcloth.engine.rendering.textures;

import dishcloth.engine.content.AContent;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

/**
 * <b>Texture</b>
 * <p>
 * Wraps OpenGL GL_TEXTURE_2D
 * <p>
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *<br>
 * Created by ASDSausage on 21.5.2015
 *
 * @see TextureAtlasBuilder
 */

public class Texture extends AContent {

	private int width, height;
	private int textureID;

	public Texture(int openGLTexID, int width, int height) {
		this.textureID = openGLTexID;
		this.width = width;
		this.height = height;
	}

	@Override
	public void dispose() {
		glDeleteTextures( textureID );
	}

	public int getGLTexID() {
		return textureID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
