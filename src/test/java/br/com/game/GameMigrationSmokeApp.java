package br.com.game;

import br.com.engine.core.ControleBase;
import br.com.engine.platform.lwjgl.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;

/** Run against the packaged jar from an empty working directory. Exits without interaction. */
public final class GameMigrationSmokeApp
{
    public static void main( String[] args ) throws Exception
    {
        System.setProperty( "org.lwjgl.system.memoryBackend", "ffm" );
        var callback = GLFWErrorCallback.createPrint( System.err ).set( );
        if( !glfwInit( ) ) throw new IllegalStateException( "GLFW initialization failed" );
        try
        {
            var script = new javax.script.ScriptEngineManager( ).getEngineByName( "nashorn" );
            if( script == null || ((Number)script.eval( "6 * 7" )).intValue( ) != 42 ) throw new AssertionError( "Packaged Nashorn provider failed" );
            var control = ControleBase.getInstance( );
            var graphics = new VulkanGraphicsContext( );
            graphics.setCanvasSize( (int)control.getScreen( ).getWidth( ), (int)control.getScreen( ).getHeight( ) );
            control.getScreen( ).setGraphicsContext( graphics );
            try( var instance = new LwjglVulkanInstance( "game-migration-smoke" );
                 var window = new LwjglVulkanWindow( instance, 680, 650, "Migration smoke" );
                 var device = new LwjglVulkanDevice( instance, window.getSurface( ) );
                 var renderer = new LwjglVulkanRenderSession( device, window ) )
            {
                if( Boolean.getBoolean( "enginefx.smoke.hidden" ) ) glfwHideWindow( window.getHandle( ) );
                control.setup( );
                for( int pass = 0; pass < 2; pass++ )
                {
                    for( int scene = 0; scene < control.getConfigurations( ).getScenes( ).size( ); scene++ )
                    {
                        control.nextScene( scene );
                        for( int frame = 0; frame < 120; frame++ )
                        {
                            window.pollEvents( );
                            if( window.shouldClose( ) ) throw new AssertionError( "Smoke closed early" );
                            if( frame == 40 ) glfwSetWindowSize( window.getHandle( ), 720, 540 );
                            if( frame == 80 ) glfwSetWindowSize( window.getHandle( ), 680, 650 );
                            control.processLogics( );
                            graphics.beginFrame( );
                            control.renderGraphics( );
                            renderer.drawFrame( graphics );
                        }
                        String expected = control.getConfigurations( ).getScenes( ).get( scene ).getScene( );
                        if( !control.getCurrentScene( ).getClass( ).getName( ).equals( expected ) ) throw new AssertionError( "Scene did not load: " + expected );
                        System.out.println( "PASS " + pass + " " + expected );
                    }
                }
                control.stop( );
            }
            System.out.println( "PASS migration: packaged resources, scripts, six scenes, repeated switches and resize" );
        }
        finally { glfwTerminate( ); glfwSetErrorCallback( null ); callback.free( ); }
    }
}
