package br.com.game.niveis.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.engine.componentes.builders.Colisors;
import br.com.engine.componentes.drawable.Cube;
import br.com.engine.core.ControleBase;
import br.com.engine.core.GameObject;
import br.com.engine.core.Scene;
import br.com.engine.core.Vector2;
import br.com.engine.fisica.Colisao;
import br.com.engine.input.KeyBoard;
import br.com.engine.interfaces.CubeColisor;
import br.com.engine.geometry.Rectangle;
import br.com.engine.input.KeyCode;

public class QuedaLivre extends Scene
{
	GameObject player, outro, outro2, outro3;
	Vector2 posicao;
	Vector2 velocidade;
	
	float gravidade;
	
	long ultimoPulo = 0;
	KeyBoard keyboard = KeyBoard.infInstace( );
	
	List<GameObject> plataformas = new ArrayList<>( );
	
	@Override
	public void setup( )
	{
		super.setup( );
		
		gravidade = 200f;
		
		velocidade = new Vector2( 0, -100f );
		
		player = new GameObject( );
		player.addComponente( new Cube( 50, 50, "blue" ) );
		
		posicao = player.getPosition();
		posicao.setPosition( 360f, 300f );
		
		player.addComponente( Colisors.cube( "cuboC" ) );
		
//		player.addComponente( ScriptBuilder.create( time -> {
//			
//		} ) );
		
//		player.addComponente( ScriptBuilder.create( time ->{
//			
//		} ) );
		
		add( player );
		
		
		outro = new GameObject( "outro1" );
		outro.addComponente( new Cube( 100, 100, "red" ) );
		outro.getPosition().setPosition( 400f, 500f );
		outro.addComponente( Colisors.cube( "chaoC" ) );
		add( outro );
		plataformas.add( outro );
		
		outro2 = new GameObject( "outro2" );
		outro2.addComponente( new Cube( 100, 100, "red" ) );
		outro2.getPosition().setPosition( 0f, 500f );
		outro2.addComponente( Colisors.cube( "chaoC" ) );
		add( outro2 );
		plataformas.add( outro2 );
		
		outro3 = new GameObject( "outro3" );
		outro3.addComponente( new Cube( 100, 100, "red" ) );
		outro3.getPosition().setPosition( 200f, 500f );
		outro3.addComponente( Colisors.cube( "chaoC" ) );
		add( outro3 );
		plataformas.add( outro3 );
		
//		player.addComponente( ScriptBuilder.create( time -> 
//		{
//			
//		} ) );
	}
	
	@Override
	public void update( long time )
	{
		super.update( time );

		KeyBoard.infInstace().ifKeyPressed( KeyCode.ESCAPE, ()-> ControleBase.getInstance().goToBootScene() );
		
		//fisica para cair
		float tempo = (time / 1000f);

		keyboard.ifKeyPressed( KeyCode.SPACE, ( ) -> {
			if( ultimoPulo > 300 ) {
				velocidade.plus( 0, -100f );
				ultimoPulo = 0;
			}
		});
		
		float y = velocidade.y * tempo + ((velocidade.y * tempo) + ( gravidade * (float)Math.pow(tempo, 2)) / 2.0f);
		posicao.plus( velocidade.x * tempo, y );
		
		Map<GameObject, CubeColisor> b;
		
		if( !(b = Colisao.inColision( player, plataformas )).isEmpty( ) )
		{
			posicao.less( velocidade.x, y );
			velocidade.y = 0;
			
			CubeColisor cubeColisor = b.get( b.keySet().stream().filter( o -> o != player ).findFirst( ).get( ) );
			Rectangle r = player.getComponent( CubeColisor.class ).getRectangle( );
			
			double y3 = r.getY( ) + r.getHeight( );
			double y2 = cubeColisor.getRectangle().getY();
			
			double y4 = y2 - y3;
			posicao.plus( 0, (float)y4 );
		}

		velocidade.y += gravidade * tempo;
		velocidade.y = (velocidade.y > 300.0f) ? 300.0f : velocidade.y;
		
		keyboard.ifKeyPressed( KeyCode.RIGHT, ( ) -> player.getPosition( ).plus( 100.0f * tempo, 0 ) );
		keyboard.ifKeyPressed( KeyCode.LEFT,  ( ) -> player.getPosition( ).less( 100.0f * tempo, 0 ) );
		
		ultimoPulo += time;
	}
}