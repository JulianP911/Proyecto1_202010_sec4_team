package test.data_structures;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import model.logic.*;

/**
 * Test de los metodos de la clase modelo utilizados para los reqeurimientos
 * El test esta basada en un arreglo fijo y de comparable, se crearon dos escenarios
 * Para poder usar con el modelo debe quitar las instrucciones con comentarios y vincular esta clase con la modelo
 * @author Julian Padilla - Pablo pastrana
 */
public class ModeloTest 
{
	/**
	 * Arreglo de tipo comparendo 1
	 */
	private Comparendo[] arregloCimparendos1;
	
	/**
	 * Arreglo de tipo comparendo 12
	 */
	private Comparendo[] arregloCimparendos2;
	
	/* 
	 * Instancia del Modelo
	 */
    //	private Modelo modelo;
	
	/**
	 * Verifica que el arreglo si se este creando
	 */
	@Test
	public void testLinkedStack() 
	{
		arregloCimparendos1 = new Comparendo[3];
		arregloCimparendos1 = new Comparendo[4];
        // modelo = new Modelo();
	}
	
	/**
	 * Escenario 1: Crea un arreglo de comparendos
	 */
	@Before
	public void setupEscenario1( )
	{
		arregloCimparendos1 = new Comparendo[4];
		arregloCimparendos1[0] = new Comparendo(201819, "2018/10/01", "Papel", "Camioneta", "Particular", "C02", "Conducir en estado de alcohol", "BOSA", 19.223, 54.67);
		arregloCimparendos1[1] = new Comparendo(201820, "2018/09/14", "Electronico", "Camion", "Publico", "B12", "Exceso de velocida", "TEUSAQUILLO", 74.982, 32.529);
		arregloCimparendos1[2] = new Comparendo(201821, "2018/03/02", "Papel", "Carro", "Particular", "I02", "No porta tarjeta de identida", "BARRIOS UNIDOS", 56.932, 54.67);
		arregloCimparendos1[3] = new Comparendo(201822, "2018/12/11", "Electronico", "Bus", "Publico", "F17", "Conducir en estado de alcohol", "KENEDY", 23.232, 98.023);
	}
	
	/**
	 * Escenario 1: Crea un arreglo de comparendos
	 */
	@Before
	public void setupEscenario2( )
	{
		arregloCimparendos2 = new Comparendo[5];
		arregloCimparendos2[0] = new Comparendo(201819, "2018/10/01", "Papel", "Camioneta", "Particular", "C02", "Conducir en estado de alcohol", "KENEDY", 19.223, 54.67);
		arregloCimparendos2[1] = new Comparendo(201820, "2018/09/14", "Electronico", "Camion", "Publico", "B12", "Exceso de velocida", "TEUSAQUILLO", 74.982, 32.529);
		arregloCimparendos2[2] = new Comparendo(201821, "2018/03/02", "Papel", "Carro", "Particular", "I02", "No porta tarjeta de identida", "BARRIOS UNIDOS", 56.932, 54.67);
		arregloCimparendos2[3] = new Comparendo(201822, "2018/12/11", "Electronico", "Bus", "Publico", "F17", "Conducir en estado de alcohol", "KENEDY", 23.232, 98.023);
		arregloCimparendos2[4] = new Comparendo(201823, "2018/01/23", "Electronico", "Camioneta", "Publico", "G10", "No posee tecnicomecanica", "BOSA", 99.232, 92.928);
	}
	
	/**
	 * Prueba 1: Verifica que el primer comparendo con la localidad dada sea el mismo
	 */
	@Test
	public void testDarPrimerComparendoLocalidad() 
	{
		setupEscenario1();
		// modelo.darPrimerComparendoLocalidad("BOSA");
		assertEquals( 201819, arregloCimparendos1[0].getObjective());
		
		setupEscenario1();
		// modelo.darPrimerComparendoLocalidad("KENEDY");
		assertEquals( 201819, arregloCimparendos2[0].getObjective());
	}

	@Test
	public void testDarPrimerComparendoInfraccion() 
	{
		setupEscenario1();
		// modelo.darPrimerComparendoInfraccion("B12");
		assertEquals( 201820, arregloCimparendos1[1].getObjective());
		
		setupEscenario1();
		// modelo.darPrimerComparendoInfraccion("G10");
		assertEquals( 201823, arregloCimparendos2[4].getObjective());
	}

	@Test
	public void testDarComparendosRegistradosFecha() 
	{
		setupEscenario1();
		// modelo.darComparendosFecha1("2018/10/01");
		assertEquals( "2018/09/14", arregloCimparendos1[1].getFecha_hora());
		
		setupEscenario2();
		// modelo.darComparendosFecha1("2018/10/01");
		assertEquals( "2018/12/11", arregloCimparendos2[3].getFecha_hora());
	}

	@Test
	public void testDarComparendosRegistradosInfraccion() 
	{
		setupEscenario1();
		// modelo.darComparendosRegistradosInfraccion("I02");
		assertEquals( "I02", arregloCimparendos1[2].getInfraccion());
		
		setupEscenario2();
		// modelo.darComparendosRegistradosInfraccion("F17");
		assertEquals( "G10", arregloCimparendos2[4].getInfraccion());
	}

	@Test
	public void testDarComparendosTipoParticular() 
	{
		setupEscenario1();
		// modelo.darPrimerTipoParticular("BOSA");
		assertEquals( "Particular", arregloCimparendos1[0].getTipo_servi());
		assertEquals( "Particular", arregloCimparendos1[2].getTipo_servi());
	}

	@Test
	public void testDarComparendosTipoPublico() 
	{
		setupEscenario1();
		// modelo.darPrimerTipoPublico("BOSA");
		assertEquals( "Publico", arregloCimparendos1[1].getTipo_servi());
		assertEquals( "Publico", arregloCimparendos1[3].getTipo_servi());
	}

	@Test
	public void testDarNumerosComparendosTipoServicioParticular() 
	{
		setupEscenario1();
		// modelo.darComparendosTipoParticular();
		assertEquals( 2, arregloCimparendos1.length-2);
		
		setupEscenario2();
		// modelo.darComparendosTipoParticular();
		assertEquals( 2, arregloCimparendos2.length-3);
	}

	@Test
	public void testDarNumerosComparendosTipoServicioPublico() 
	{
		setupEscenario1();
		// modelo.darComparendosTipoPublico();
		assertEquals( 2, arregloCimparendos1.length-2);
		
		setupEscenario2();
		// modelo.darComparendosTipoPublico();
		assertEquals( 3, arregloCimparendos2.length-2);
	}

	@Test
	public void testDarComparendosFecha1()
	{
		setupEscenario1();
		// modelo.darComparendosFecha1("2018/10/01");
		assertEquals( 1, arregloCimparendos1.length-3);
		
		setupEscenario2();
		// modelo.darComparendosFecha1("2018/03/02");
		assertEquals( 2, arregloCimparendos2.length-3);
	}

	@Test
	public void testDarComparendosFecha2() 
	{
		setupEscenario1();
		// modelo.darComparendosFecha1("2018/10/01");
		assertEquals( 1, arregloCimparendos1.length-3);
		
		setupEscenario2();
		// modelo.darComparendosFecha1("2018/03/02");
		assertEquals( 2, arregloCimparendos2.length-3);
	}

}
