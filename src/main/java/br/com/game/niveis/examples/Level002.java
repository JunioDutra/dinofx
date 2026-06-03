package br.com.game.niveis.examples;

import br.com.engine.componentes.drawable.Sprite;
import br.com.engine.componentes.drawable.TmxMap;
import br.com.engine.componentes.scripts.Animator;
import br.com.engine.core.ControleBase;
import br.com.engine.core.GameObject;
import br.com.engine.core.Scene;
import br.com.engine.core.Time;
import br.com.engine.core.Vector2;
import br.com.engine.input.KeyBoard;
import br.com.engine.input.Mouse;
import br.com.game.script.AndarEmTile;
import br.com.engine.input.KeyCode;


public class Level002 extends Scene
{
	GameObject mapa;
	GameObject player;
	
	long timeElapsed;
	long timeLimit = 100;
	
	int xAtu = 5; 
	int yAtu = 5;
	
	final private Vector2 velocidade = new Vector2( 10f, 10f );
	
	@Override
	public void setup( )
	{
		super.setup( );

		criarMapa( );
		criarJogador( );
		
		GameObject camera = getObject("default_camera");
		
		Mouse.infInstace().addListener( event ->{
			float valX = camera.getPosition().getX( );
			valX = valX < 0 ? valX * -1 : valX;
			
			float valY = camera.getPosition().getX( );
			valY = valY < 0 ? valY * -1 : valY;
			
			System.out.println( "Location: " + ( event.getX( ) - valX ) 
				+ ", " + ( event.getY( ) - valY ) );
			
			System.out.println( "Scene: " + event.getSceneX( ) + ", "+ event.getSceneY( ) );
		});
	}
	
	private void criarMapa( )
	{
		mapa = new GameObject( "mapa" );

		TmxMap mapinha = new TmxMap( "ola_mapa" );
		Mapa tile_map = new Mapa( 512/10, 512/10, 1280, 1280 );
		mapa.addComponente( tile_map );
		mapa.addComponente(mapinha);
		
		add( mapa );
	}

	private void criarJogador( )
	{
		Animator anim = new Animator( 100 );
		anim.createAnimation( "run", 0, 5 );
		anim.createAnimation( "stop", 0, 1 );
		anim.execute( "stop" );
		
		player = new GameObject( "player" );
		player.addComponente( new Sprite( "Sonic_anim", 1 , 5 ) );
		player.addComponente( anim );
		player.addComponente( new AndarEmTile( ) );
		player.getPosition().setPosition(354.0f, 336.5f);
		add( player );
		
		getObject("default_camera").setPai(player);
	}
	
	@Override
	public void update(long time) 
	{
		super.update(time);

		KeyBoard.infInstace().ifKeyPressed( KeyCode.ESCAPE, ()-> ControleBase.getInstance().goToBootScene() );
		
		KeyBoard.infInstace().ifKeyPressed( KeyCode.DOWN, ()->{
			if( timeElapsed > timeLimit )
			{
				yAtu = incrementa( yAtu, 1, mapa.getComponent(Mapa.class).getCounty() - 1, true );
				timeElapsed = 0;
			}
		});
		
		KeyBoard.infInstace().ifKeyPressed( KeyCode.UP, ()->{
			if( timeElapsed > timeLimit )
			{
				yAtu = incrementa( yAtu, -1, 0, false );
				timeElapsed = 0;
			}
		});
		
		KeyBoard.infInstace().ifKeyPressed( KeyCode.RIGHT, ()->{
			if( timeElapsed > timeLimit )
			{
				xAtu = incrementa( xAtu, 1, mapa.getComponent(Mapa.class).getCountx() - 1, true );
				timeElapsed = 0;
			}
		});
		
		KeyBoard.infInstace().ifKeyPressed( KeyCode.LEFT, ()->{
			if( timeElapsed > timeLimit )
			{
				xAtu = incrementa( xAtu, -1, 0, false );
				timeElapsed = 0;
			}
		});
		
		// Velocidade em pixels por segundo; escalada pelo delta time para ficar
		// independente da taxa de quadros (movimento liso em qualquer FPS).
		float velocidadeCamera = 600f * Time.getDeltaTime( );

		KeyBoard.infInstace().ifKeyPressed( KeyCode.D, ()-> 
			getObject("default_camera").getPosition().plus(velocidadeCamera, 0f) );
		
		KeyBoard.infInstace().ifKeyPressed( KeyCode.A, ()-> 
			getObject("default_camera").getPosition().less(velocidadeCamera, 0f) );
		
		KeyBoard.infInstace().ifKeyPressed( KeyCode.W, ()-> 
			getObject("default_camera").getPosition().less(0f, velocidadeCamera) );
		
		KeyBoard.infInstace().ifKeyPressed( KeyCode.S, ()-> 
			getObject("default_camera").getPosition().plus(0f, velocidadeCamera) );
		
		KeyBoard.infInstace().ifKeyPressed( KeyCode.SPACE, ()-> 
			getObject("default_camera").getPosition().setPosition(0f, 0f) );
		
		Vector2 to = mapa.getComponent( Mapa.class ).calcularTile( player, xAtu, yAtu );
		
		player.getComponent( AndarEmTile.class ).mover( to, velocidade );
		
		if( !player.getPosition().equals( to ) )
		{
			player.getComponent( Animator.class ).execute( "run" );
		}
		else
		{
			player.getComponent( Animator.class ).execute( "stop" );
		}
		
		timeElapsed += time;
	}
	
	private int incrementa( int eixo, int incremento, int limite, boolean menor )
	{
		if( (menor && eixo < limite) || (!menor && eixo > limite) )
		{
			return eixo + incremento;
		}
		
		return eixo;
	}
}