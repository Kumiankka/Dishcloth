package dishcloth.engine;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

// For NULL constant
import static org.lwjgl.system.MemoryUtil.*;

import dishcloth.engine.events.EventHandler;
import dishcloth.engine.events.EventRegistry;
import dishcloth.engine.exception.GameInitializationException;
import dishcloth.engine.io.input.InputHandler;
import dishcloth.engine.rendering.ICamera;
import dishcloth.engine.rendering.IRenderer;
import dishcloth.engine.rendering.OrthographicCamera;
import dishcloth.engine.rendering.Renderer;
import dishcloth.engine.util.logger.Debug;
import dishcloth.engine.util.time.Time;
import dishcloth.engine.world.block.BlockRegistry;
import dishcloth.engine.world.level.TerrainRenderer;
import org.lwjgl.opengl.GLContext;

/**
 * ********************************************************************************************************************
 * AGame.java
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * <p>
 * Abstract game class for handling initialization, update-tick, fps synchronization, etc.
 * (This class just shouts "FUCK YOU" at Single Responsibility Principle, but just don't care about it)
 * <p>
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Created by ASDSausage on 12.5.2015.
 */

public abstract class AGame extends ADishclothObject implements IGame {

	protected int screenWidth, screenHeight;
	protected boolean doUpdateTime = true;
	private long windowID;
	private boolean windowShouldExit;
	private IRenderer renderer;
	private ICamera viewportCamera;
	private Timing timing;
	protected AGame() {
		super( true );
	}

	@EventHandler
	public void onPreInitializeEvent(AGameEvents.GamePreInitializationEvent event) {
		// TODO: Figure out some place where to store static class event listener registrations
		// XXX: They are kept here purely for the purpose of example.

		// Note how IDE says that "method onPreInitializeEvent is never used", yet when you start up
		// the game, it is quite obvious that these two lines below this comment are getting called.

		EventRegistry.registerEventListener( BlockRegistry.class );
		EventRegistry.registerEventListener( TerrainRenderer.class );
	}

	public long getWindowID() {
		return windowID;
	}

	public ICamera getViewportCamera() {
		return viewportCamera;
	}

	@Override
	public final void run() {

		Debug.log( "", this );
		Debug.log( "||XX||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||XX||", this );
		Debug.log( "||==|+----------------------------------------------------------------------------------------+|==||", this );
		Debug.log( "||==||                                      Running game...                                   ||==||", this );
		Debug.log( "||==|+----------------------------------------------------------------------------------------+|==||", this );
		Debug.log( "||XX||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||XX||", this );
		Debug.log( "", this );

		Debug.log( "Initializing...", this );

		doInitialize();


		Debug.logOK( "Initializing successful!", this );

		Debug.log( "Loading content...", this );
		doLoadContent();

		Debug.logOK( "Content loading successful!", this );

		Debug.log( "Triggering post-initialize events", this );
		EventRegistry.fireEvent( new AGameEvents.GamePostInitializationEvent( this ) );

		timing = new Timing();

		timing.timestep = 1f / 60f;

		Debug.log( "", this );
		Debug.logNote( "Entering main loop...", this );
		while (glfwWindowShouldClose( windowID ) != GL_TRUE
				&& !windowShouldExit) {

			assert !windowShouldExit;

			timing.tick++;

			// Update time
			float oldTime = timing.currentTime;
			timing.currentTime = (float) glfwGetTime();

			// Calculate delta
			timing.delta = timing.currentTime - oldTime;

			doUpdate();

			doFixedUpdate();

			doRender();

			if (doUpdateTime) {
				Time.update( timing );
			}
		}

		Debug.logNote( "Main loop ended...", this );
		Debug.log( "", this );
		Debug.logWarn( "Unloading content...", this );

		doUnloadContent();
		TerrainRenderer.dispose();

		Debug.logWarn( "Shutting down...", this );
		doShutdown();
	}

	@Override
	public final void doInitialize() {
		try {

			initHardware();

			// Attach inputHandler
			InputHandler.attachToWindow( windowID );

			Debug.log( "Triggering pre-initialize events", this );
			EventRegistry.fireEvent( new AGameEvents.GamePreInitializationEvent( this ) );

			// Call initialize
			initialize();

		} catch (GameInitializationException e) {

			Debug.logException( e, this );
			System.exit( 1 );
		}
	}

	private void initHardware() throws GameInitializationException {
		// Initialize window
		initWindow();

		// Create renderer
		renderer = new Renderer();

		// Create camera
		float halfW = screenWidth / 2f;
		float halfH = screenHeight / 2f;
		viewportCamera = new OrthographicCamera( -halfW, halfW,
		                                         -halfH, halfH,
		                                         1.0f, -1.0f );

		/*viewportCamera = new OrthographicCamera( 0f, screenWidth,
		                                         0f, screenHeight,
		                                         -1.0f, 1.0f );*/
	}


	private void initWindow() throws GameInitializationException {

		// Init GLFW - if glfwInit succeeds, it returns GL_TRUE
		if (glfwInit() != GL_TRUE) {
			throw new GameInitializationException( "glfwInit() failed!" );
		}

		screenWidth = 800;
		screenHeight = 600;

		// Initialize window

		// Set hint flags
		glfwWindowHint( GLFW_RESIZABLE, GL_FALSE );

		// Create dishcloth.window handle
		windowID = glfwCreateWindow( screenWidth, screenHeight, "Dishcloth", NULL, NULL );

		// Validate windowID
		if (windowID == NULL) {
			throw new GameInitializationException( "glfwCreateWindow() failed!" );
		}

		// Make created window active

		glfwMakeContextCurrent( windowID );
		GLContext.createFromCurrent();
		glfwSwapInterval( 1 );
	}

	@Override
	public final void doLoadContent() {
		// Call loadContent()
		loadContent();

		Debug.log("Triggering content-initialization events", this);
		EventRegistry.fireEvent( new AGameEvents.GameContentInitializationEvent( this ) );
	}

	@Override
	public final void doUpdate() {

		// Poll glfw events (input etc.)
		glfwPollEvents();

		// Update EventRegistry
		//EventRegistry.updateEvents();

		// Call update
		update( timing.delta );
	}

	@Override
	public final void doFixedUpdate() {

		timing.simulationTimePool += timing.delta;

		while (timing.simulationTimePool >= timing.timestep) {

			timing.fixedTick++;
			timing.simulationTimePool -= timing.timestep;

			if (doUpdateTime) {
				Time.update( timing );
			}

			fixedUpdate();
		}
	}

	@Override
	public final void doRender() {
		glClear( GL_COLOR_BUFFER_BIT );

		// Call render()
		render( renderer );

		// Swap buffers
		glfwSwapBuffers( windowID );
	}

	@Override
	public final void doUnloadContent() {
		//EventRegistry.fireEvent( AGameEvents.contentUnloadingEvent );
		unloadContent();
	}

	@Override
	public final void doShutdown() {

		//EventRegistry.fireEvent( AGameEvents.shutdownEvent );

		// Call shutdown()
		shutdown();

		// Destroy window
		glfwDestroyWindow( windowID );

		// Terminate glfw
		glfwTerminate();
	}

	public class Timing {
		private float simulationTimePool, timestep, currentTime, delta;
		private int tick, fixedTick;

		public float getSimulationTimePool() {
			return simulationTimePool;
		}

		public float getTimestep() {
			return timestep;
		}

		public float getCurrentTime() {
			return currentTime;
		}

		public float getDelta() {
			return delta;
		}

		public int getTick() {
			return tick;
		}

		public int getFixedTick() {
			return fixedTick;
		}
	}
}
