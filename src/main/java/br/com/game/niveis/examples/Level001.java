package br.com.game.niveis.examples;

import java.util.ArrayList;
import java.util.List;

import br.com.engine.componentes.builders.SpriteFontBuilder;
import br.com.engine.componentes.drawable.Sprite;
import br.com.engine.componentes.drawable.SpriteFont;
import br.com.engine.componentes.scripts.Animator;
import br.com.engine.core.GameObject;
import br.com.engine.core.Scene;
import br.com.engine.core.Vector2;
import br.com.engine.input.KeyBoard;
import br.com.game.script.AndarEmTile;
import br.com.engine.graphics.Color;
import br.com.engine.input.KeyCode;


public class Level001 extends Scene
{
	GameObject mapa;
	GameObject player;
	
	long timeElapsed;
	long timeLimit = 500;
	
	int xAtu = 0; 
	int yAtu = 0;
	
	List<String> comandos = new ArrayList<>( );
	
	GameObject sf_comandos;
	
	String txt_comandos = "Comandos: " + comandos.toString( );
	
	boolean play = false;
	private int index = -1;
	
	@Override
	public void setup( )
	{
		super.setup( );

		criarMapa( );
		criaHud( );
		criarJogador( );
	}

	private void criarJogador( )
	{
		Animator anim = new Animator( 100 );
		anim.createAnimation( "run", 0, 3 );
		anim.createAnimation( "stop", 1, 1 );
		anim.execute( "stop" );
		
		player = new GameObject( "plr" );
		player.addComponente( new Sprite( "hero_sheet", 2 , 2 ) );
		player.addComponente( anim );
		player.addComponente( new AndarEmTile( ) );
		add( player );
	}

	private void criaHud( )
	{
		sf_comandos = new SpriteFontBuilder()
				.setColor( Color.BLUE )
				.setSize( 12 )
				.setText( txt_comandos )
				.build( );
		
		add( sf_comandos );
	}

	private void criarMapa() {
		mapa = new GameObject( "mapa" );
		
		Mapa tile_map = new Mapa( 10, 10 );
		mapa.addComponente( tile_map );
		add( mapa );
	}
	
	@Override
	public void update(long time) 
	{
		super.update(time);
		
		if( play )
		{
			Vector2 to = mapa.getComponent( Mapa.class ).calcularTile( player, xAtu, yAtu );
			
			player.getComponent( AndarEmTile.class ).mover( to, new Vector2( 1, 1 ) );
			
			if( !player.getPosition().equals( to ) )
			{
				player.getComponent( Animator.class ).execute( "run" );
			}
			else
			{
				irParaProximoComando( );
				player.getComponent( Animator.class ).execute( "stop" );
			}
		}
		else
		{
			KeyBoard.infInstace().ifKeyPressed( KeyCode.DOWN, ()->{
				if( timeElapsed > timeLimit )
				{
					comandos.add( "DOWN" );
					timeElapsed = 0;
				}
			});
			
			KeyBoard.infInstace().ifKeyPressed( KeyCode.UP, ()->{
				if( timeElapsed > timeLimit )
				{
					comandos.add( "UP" );
					timeElapsed = 0;
				}
			});
			
			KeyBoard.infInstace().ifKeyPressed( KeyCode.RIGHT, ()->{
				if( timeElapsed > timeLimit )
				{
					comandos.add( "RIGHT" );
					timeElapsed = 0;
				}
			});
			
			KeyBoard.infInstace().ifKeyPressed( KeyCode.LEFT, ()->{
				if( timeElapsed > timeLimit )
				{
					comandos.add( "LEFT" );
					timeElapsed = 0;
				}
			});
			
			KeyBoard.infInstace().ifKeyPressed( KeyCode.SPACE, ()->{
				if( timeElapsed > timeLimit )
				{
					play = !play;
					
					timeElapsed = 0;
				}
			});
		}
		
		atualizarSfComandos( );
		
		timeElapsed += time;
	}
	
	private void irParaProximoComando( )
	{
		if( (index+1) < comandos.size( ) )
		{
			String comando = comandos.get( ++index );
			
			switch( comando )
			{
				case "UP":
					yAtu = incrementa( yAtu, -1, 0, false );
					break;
				case "DOWN":
					yAtu = incrementa( yAtu, 1, mapa.getComponent(Mapa.class).getCounty() - 1, true );
					break;
				case "LEFT":
					xAtu = incrementa( xAtu, -1, 0, false );
					break;
				case "RIGHT":
					xAtu = incrementa( xAtu, 1, mapa.getComponent(Mapa.class).getCountx() - 1, true );
					break;
			}
		}
	}

	private int incrementa( int eixo, int incremento, int limite, boolean menor )
	{
		if( (menor && eixo < limite) || (!menor && eixo > limite) )
		{
			return eixo + incremento;
		}
		
		return eixo;
	}

	private void atualizarSfComandos( )
	{
		sf_comandos.getComponent( SpriteFont.class ).setText( txt_comandos + comandos.toString( ) );
	}
}