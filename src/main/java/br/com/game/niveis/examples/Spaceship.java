package br.com.game.niveis.examples;

import java.util.Random;

import br.com.engine.componentes.builders.Colisors;
import br.com.engine.componentes.builders.ScriptBuilder;
import br.com.engine.componentes.builders.SpriteFontBuilder;
import br.com.engine.componentes.drawable.Sprite;
import br.com.engine.componentes.drawable.SpriteFont;
import br.com.engine.componentes.scripts.Animator;
import br.com.engine.componentes.scripts.Animator.AnimationType;
import br.com.engine.core.ControleBase;
import br.com.engine.core.GameObject;
import br.com.engine.core.Scene;
import br.com.engine.core.Time;
import br.com.engine.core.Vector2;
import br.com.engine.input.KeyBoard;
import br.com.engine.interfaces.CubeColisor;
import br.com.engine.graphics.Color;
import br.com.engine.input.KeyCode;

public class Spaceship extends Scene
{
	int ObsId = 0;
	
	GameObject player;
		
	// Velocidades em pixels por segundo; escaladas por Time.getDeltaTime()
	// para ficarem independentes da taxa de quadros.
	Vector2 velX = new Vector2( 180, 0 );
	Vector2 velY = new Vector2( 0, 180 );
	
	Vector2 velXObs = new Vector2( 0, 240 );
	
	float velTiro = 300;
	
	Random random = new Random( );

	long tempoUltimoTiro = 0;
	
	int pontos = 0;
	SpriteFont pontuacao;
	
	@Override
	public void setup( )
	{
		super.setup( );
		
		criarBackGround( );
		
		criaHUD( );
		
		criarJogador( );
		
		for( int i = 0; i < 10; i++ )
		{
			criarOBS( );
		}
	}

	private void criarBackGround( )
	{
		GameObject bg = new GameObject( "background" );
		bg.addComponente(new Sprite( "BackgroundSpace" ));
		add( bg );
	}

	private void criaHUD( )
	{
		GameObject obj = new SpriteFontBuilder( )
			.setColor( Color.AQUA )
			.setSize( 20 )
			.setText( "Pontos " + pontos )
			.centerX( )
			.build( );
		
		obj.getPosition().y = 10;
		pontuacao = obj.getComponent( SpriteFont.class );
		add( obj );
	}
	
	public void atualizarPontos( )
	{
		pontuacao.setText( "Pontos " + pontos );
	}

	private void criarOBS( )
	{
		Vector2 velXObs = new Vector2( this.velXObs.x, random.nextInt( (int)this.velXObs.y ) + 60 );
		
		GameObject obs = new GameObject("Obs"+(ObsId++));
		obs.addComponente( new Sprite( "enyme" ) );
		obs.getPosition( ).setPosition( random.nextInt( (int)ControleBase.getInstance().getScreen().getWidth() ), 
										(random.nextInt( 400 )*-1)-200 );
		
		obs.addComponente( ScriptBuilder.createNoTime( ( ) -> {
			if( obs.getPosition( ).x <= 0 ||
				obs.getPosition( ).x + obs.getComponent( Sprite.class ).getWidth( ) >= ControleBase.getInstance().getScreen().getWidth() )
			{
				velXObs.reverseY( );
			}
			
			if( obs.getPosition( ).y >= ControleBase.getInstance().getScreen().getHeight() )
			{
				obs.destroy( );
				criarOBS( );
			}
			
			obs.getPosition( ).plus( velXObs.x * Time.getDeltaTime( ), velXObs.y * Time.getDeltaTime( ) );
		} ) );
		
		obs.addComponente( Colisors.simpleSprite( obs.getTag( ) ) );
		
		add( obs );
	}

	private void criarJogador( )
	{
		player = new GameObject( );
		player.addComponente( new Sprite( "nave" ) );
				
		player.addComponente( ScriptBuilder.createNoTime( ( ) -> {
			KeyBoard.infInstace( ).ifKeyPressed( KeyCode.RIGHT, ( ) -> player.getPosition( ).plus( velX.x * Time.getDeltaTime( ), 0f ) );
			KeyBoard.infInstace( ).ifKeyPressed( KeyCode.LEFT,  ( ) -> player.getPosition( ).less( velX.x * Time.getDeltaTime( ), 0f ) );
			KeyBoard.infInstace( ).ifKeyPressed( KeyCode.DOWN,  ( ) -> player.getPosition( ).plus( 0f, velY.y * Time.getDeltaTime( ) ) );
			KeyBoard.infInstace( ).ifKeyPressed( KeyCode.UP,    ( ) -> player.getPosition( ).less( 0f, velY.y * Time.getDeltaTime( ) ) );
		} ) );
		
		player.addComponente( ScriptBuilder.createNoTime( ( ) -> {
			KeyBoard.infInstace( ).ifKeyPressed( KeyCode.SPACE, ( ) -> {
				if( tempoUltimoTiro > 300 ) {
					criaTiros( );
					tempoUltimoTiro = 0;
				}
			} );
		} ) );
		
		player.getPosition().setPosition( (float)ControleBase.getInstance().getScreen().getWidth() / 2 - 25, 
				(float)ControleBase.getInstance().getScreen().getHeight( )- 50);

		// CubeColisor cube = Colisors.cube( "player" );
		// cube.setOnColisionAction( colided -> {
		// 	System.out.println(colided.getTag());
			// GameObject nave = getObject(colided.getTag());
			// criarExplosao( nave.getPosition( ) );
			// nave.destroy();
			// tiro.destroy( );
			// pontos++;
			// atualizarPontos( );
			// criarOBS();
		// });
		// player.addComponente( cube );
		
		add( player );
	}
	
	private void criaTiros( )
	{
		GameObject tiro = new GameObject( );
		tiro.addComponente( new Sprite( "tiro" ) );
				
		tiro.getPosition( ).setPosition( player.getPosition().x, player.getPosition().y );
		
		tiro.addComponente( ScriptBuilder.create( time -> {
			tiro.getPosition( ).less( 0f, velTiro * Time.getDeltaTime( ) );
		} ) );
		
		CubeColisor cube = Colisors.simpleSprite( "bullet" );
		cube.setOnColisionAction( colided -> {
			GameObject nave = getObject(colided.getTag());
			criarExplosao( nave.getPosition( ) );
			nave.destroy();
			tiro.destroy( );
			pontos++;
			atualizarPontos( );
			criarOBS();
		});
		tiro.addComponente( cube );
		
		add( tiro );
	}

	private void criarExplosao( Vector2 vector2 ) 
	{
		Animator animator = new Animator( 10 );
		animator.createAnimation( "run", 0, 24, AnimationType.UNIQUE_DESTROY );
		animator.execute( "run" );
		
		GameObject explosao = new GameObject( );
		explosao.addComponente( new Sprite( "Explosion", 5, 5 ) );
		explosao.addComponente( animator );
		explosao.getPosition( ).setPosition( vector2.x, vector2.y );
		add( explosao );
	}

	@Override
	public void update( long time )
	{
		super.update( time );

		KeyBoard.infInstace().ifKeyPressed( KeyCode.ESCAPE, ()-> ControleBase.getInstance().goToBootScene() );
		
		tempoUltimoTiro += time;
	}
	
	@Override
	public String getName( )
	{
		return "MinhaSena";
	}
}