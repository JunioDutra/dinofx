package br.com.game.niveis.examples;

import br.com.engine.componentes.SimpleComponent;
import br.com.engine.componentes.drawable.Cube;
import br.com.engine.componentes.drawable.Sprite;
import br.com.engine.core.ControleBase;
import br.com.engine.core.GameObject;
import br.com.engine.core.Vector2;

public class Mapa extends SimpleComponent
{
	private final int countx;
	private final int county;
	
	float width = 0;
	float height = 0;
	
	private boolean adicionou = false;

	private GameObject[][] mapa;

	public Mapa( int countx, int county )
	{
		this.countx = countx;
		this.county = county;

		width = (float) (ControleBase.getInstance().getScreen().getWidth() / getCountx());
		height = (float) (ControleBase.getInstance().getScreen().getHeight() / getCounty());
		
		mapa = new GameObject[this.county][this.countx];
	}
	
	public Mapa( int countx, int county, float width, float height)
	{
		this.countx = countx;
		this.county = county;

		this.width = width  / getCountx();
		this.height = height / getCounty();
		
		mapa = new GameObject[this.county][this.countx];
	}

	@Override
	public void setup( )
	{
		for( int y = 0; y < getCounty(); y++ )
		{
			for( int x = 0; x < getCountx(); x++ )
			{
				GameObject tile = new GameObject( String.format( "tile_%s_%s", x, y ) );
				tile.addComponente( new Cube( (int)width, (int)height ) );
				tile.getPosition( ).setPosition((float)width*x, (float)height*y);
				
				tile.setup();
				
				mapa[y][x] = tile;
			}
		}
	}

	@Override
	public void update( long time )
	{
		inicializarFilhos( );
	}

	private void inicializarFilhos( )
	{
		if( !adicionou  )
		{
			for (GameObject[] gameObjects : mapa) {
				for (GameObject gameObject : gameObjects) {
					gameObject.setPai(getParent());
				}
			}
			
			adicionou = true;
		}
	}

	@Override
	public void draw( )
	{
		for (GameObject[] gameObjects : mapa) {
			for (GameObject gameObject : gameObjects) {
				gameObject.getComponentes().forEach( cp -> cp.draw() );
			}
		}
	}

	public Vector2 getTile( int xAtu, int yAtu )
	{
		return mapa[yAtu][xAtu].getPosition();
	}
	
	public Vector2 calcularTile( GameObject player, int xAtu, int yAtu )
	{
		Vector2 tile = getTile( xAtu, yAtu );
		
		float w = player.getComponent( Sprite.class ).getWidth( ) / 2;
		float h = player.getComponent( Sprite.class ).getHeight( ) / 2;
		
		float a = tile.x + (width / 2f);
		float b = tile.y + (height / 2f);
		
		return new Vector2( Math.max(a, w) - Math.min(a, w), Math.max(b, h) - Math.min(b, h) );
	}

	public int getCountx() {
		return countx;
	}

	public int getCounty() {
		return county;
	}
}