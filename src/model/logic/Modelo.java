package model.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import com.google.gson.Gson;

import Infracciones.Example;
import model.data_structures.*;

/**
 * Definicion del modelo del mundo
 *
 */
public class Modelo 
{
	/**
	 * Lista-pila de tipo Comparendos
	 */
	private LinkedList<Comparendo> datos1;

	/**
	 * Lista-cola de tipo Comparendos
	 */
	private LinkedQueue<Comparendo> datos2;

	/**
	 * Constructor del modelo del mundo
	 */
	public Modelo()
	{	
		Gson gson = new Gson();
		BufferedReader br = null;
		datos1 = new LinkedList<>();
		datos2 = new LinkedQueue<>();

		try
		{
			br = new BufferedReader(new FileReader("./data/comparendos_dei_2018_small.geojson"));
			Example result = gson.fromJson(br, Example.class);

			for(int  i = 0; i < result.getFeatures().size(); i ++)
			{
				int objective = result.getFeatures().get(i).getProperties().getOBJECTID();
				String fecha_hora = result.getFeatures().get(i).getProperties().getFECHAHORA();
				String medio_dete = result.getFeatures().get(i).getProperties().getMEDIODETE();
				String clase_vehi = result.getFeatures().get(i).getProperties().getCLASEVEHI();
				String tipo_servi = result.getFeatures().get(i).getProperties().getTIPOSERVI();
				String infraccion = result.getFeatures().get(i).getProperties().getINFRACCION();
				String des_infrac = result.getFeatures().get(i).getProperties().getDESINFRAC();
				String localidad = result.getFeatures().get(i).getProperties().getLOCALIDAD();
				double cordenada1 = result.getFeatures().get(i).getGeometry().getCoordinates().get(0);
				double cordenada2 = result.getFeatures().get(i).getGeometry().getCoordinates().get(1);

				Comparendo actual = new Comparendo(objective, fecha_hora, medio_dete, clase_vehi, tipo_servi, infraccion, des_infrac, localidad, cordenada1, cordenada2);
				datos1.addNodeFirst(actual);
				datos2.enqueue(actual);
			}
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		finally
		{
			if(br != null)
			{
				try 
				{
					br.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Servicio de consulta de numero de elementos presentes en el modelo de la pila
	 * @return numero de elementos presentes en el modelo
	 */
	public int darTamanoComparendos()
	{
		return datos1.getSize();
	}

	/**
	 * Muestra la informacion con el mayor OBJECTID encontrado en la lista
	 * @return El comparendo con maoyr objectid encontrado
	 */
	public String darObjectidMayor()
	{
		String mensaje = " ";
		Comparendo actual = datos1.seeObjetc(0);

		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			Comparendo elemento = it.next();
			if(elemento.getObjective() > actual.getObjective())
			{
				actual = elemento;
			}
		}

		mensaje = actual.getObjective() + ", " + actual.getFecha_hora() + ", " + actual.getInfraccion() + ", " + 
				actual.getClase_vehi() + ", " + actual.getTipo_servi() + ", " +  actual.getLocalidad();

		return mensaje;
	}

	/**
	 * Determina la sonaMiniMax para despues ser utilizado
	 * @return Los limites teniendo en cuenta el rectangulo (la menor latitud, la menor longitud) y (la mayor latitud, la mayor longitud).
	 */
	public double[] darZonaMiniMax()
	{
		double[] rango = new double[4];

		double mayLa = 0;
		double mayLo = datos1.seeObjetc(0).getCordenadas()[0];
		double menLa = datos1.seeObjetc(0).getCordenadas()[1];
		double menLo = 0;

		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			Comparendo elemento = it.next();
			if(elemento.getCordenadas()[0] > mayLo)
			{
				mayLo = elemento.getCordenadas()[0];
			}
			else if(elemento.getCordenadas()[0] < menLo)
			{
				menLo = elemento.getCordenadas()[0];
			}

			if(elemento.getCordenadas()[1] > mayLa)
			{
				mayLa = elemento.getCordenadas()[1];
			}
			else if(elemento.getCordenadas()[1] < menLa)
			{
				menLa = elemento.getCordenadas()[1];
			}
		}

		rango[0] = menLo;
		rango[1] = mayLo;
		rango[2] = menLa;
		rango[3] = mayLa;
		return rango;
	}

	/**
	 * Retona una lista con los comparendos que se encuntran dentro de la ZonaMiniMax
	 * @param pLongitudIn Longitud Inferior
	 * @param pLatitudSu Latitud Superior
	 * @param pLongitudSu Longitud Superior
	 * @param pLatitudIn Latitud Inferior
	 * @return Lista con los comparendos encontrados
	 */
	public LinkedList<Comparendo> darComparendosZonaMinimax(double pLongitudIn, double pLatitudSu, double pLongitudSu, double pLatitudIn)
	{
		LinkedList<Comparendo> nueva = new LinkedList<Comparendo>();

		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			Comparendo elemento = it.next();
			if(elemento.getCordenadas()[0] >= pLongitudIn && elemento.getCordenadas()[1] <= pLatitudSu && elemento.getCordenadas()[0] <= pLongitudSu && elemento.getCordenadas()[1] >= pLongitudIn)
			{
				nueva.addNodeFirst(elemento);
			}
		}
		return nueva;
	}

	/**
	 * Crea un arreglo comparable de los comparendos para poder ser utilizado en los sorts
	 * @return Arreglo Comparable<Comparendos>
	 */
	public Comparable<Comparendo>[] copiarComparendosArreglo()
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				nuevo[i] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
			}
		}

		return nuevo;
	}	

	/**
	 * Recibe una localidad dada por parametro y retorna el primer comparendo con esa localidad, si no encuntra manda un mensaje de error
	 * @param pLocalidad Localidad donde se hizo el comparendo
	 * @return Mensaje del comparendo con la informcaion basica si lo encontro, si no un mensaje diciendo que no lo encontro
	 */
	public String darPrimerComparendoLocalidad(String pLocalidad) 
	{
		String mensaje = "";
		boolean encontrado = false;

		Comparable<Comparendo> copia_Comparendos [ ] = copiarComparendosArreglo();
		Comparendo nuevo = null;

		for(int i = 0; i < copia_Comparendos.length && !encontrado; i++)
		{
			nuevo = (Comparendo) copia_Comparendos[i];
			if(nuevo.getLocalidad().equals(pLocalidad))
			{
				mensaje = nuevo.getObjective() + ", " + nuevo.getFecha_hora() + ", " + nuevo.getInfraccion() + ", " + nuevo.getClase_vehi() + ", " + nuevo.getTipo_servi() + ", " + nuevo.getLocalidad();
				encontrado = true;
			}
		}

		if(!encontrado)
		{
			mensaje = "No se encontro en el archivo un comparendo con esta localidad.";
		}
		return mensaje;
	}

	/**
	 * Recibe una localidad dada por parametro y retorna el primer comparendo con esa infraccion, si no encuentra manda un mensaje de error
	 * @param pInfracion Infraccion del comparendo
	 * @return Mensaje del comparendo con la informcaion basica si lo encontro, si no un mensaje diciendo que no lo encontro
	 */
	public String darPrimerComparendoInfraccion(String pInfracion) 
	{
		String mensaje = "";
		boolean encontrado = false;

		Comparable<Comparendo> copia_Comparendos [ ] = copiarComparendosArreglo();
		Comparendo nuevo = null;

		for(int i = 0; i < copia_Comparendos.length && !encontrado; i++)
		{
			nuevo = (Comparendo) copia_Comparendos[i];
			if(nuevo.getInfraccion().equals(pInfracion))
			{
				mensaje = nuevo.getObjective() + ", " + nuevo.getFecha_hora() + ", " + nuevo.getInfraccion() + ", " + nuevo.getClase_vehi() + ", " + nuevo.getTipo_servi() + ", " + nuevo.getLocalidad();
				encontrado = true;
			}
		}

		if(!encontrado)
		{
			mensaje = "No se encontro en el archivo un comparendo con esa infraccion.";
		}
		return mensaje;
	}

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen la localidad igual a recibida por parametro
	 * @param pFecha Fecha de cuando se hizo el comparendo
	 * @return Arrgelo Comparable de comparendos que cumplen la condicion
	 */
	public Comparable<Comparendo>[] darComparendosRegistradosFecha(String pFecha)
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		int j = 0;
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				if(elementoActual.getFecha_hora().equals(pFecha)) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		return nuevo;
	}

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen la infraccion igual a recibida por parametro
	 * @param pInfraccion Infraccion del comparendo
	 * @return Arrgelo Comparable de comparendos que cumplen la condicion
	 */
	public Comparable<Comparendo>[] darComparendosRegistradosInfraccion(String pInfraccion)
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		int j = 0;
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				if(elementoActual.getInfraccion().equals(pInfraccion)) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		return nuevo;
	}

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen tipo de servicio particular
	 * @return Arrgelo Comparable de comparendos que son de tipo servicio particular
	 */
	public Comparable<Comparendo>[] darComparendosTipoParticular()
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		int j = 0;
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				if(elementoActual.getTipo_servi().equals("Particular")) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		return nuevo;
	}

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen tipo de servicio publico
	 * @return Arrgelo Comparable de comparendos que son de tipo servicio publico
	 */
	public Comparable<Comparendo>[] darComparendosTipoPublico()
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		int j = 0;
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				if(elementoActual.getTipo_servi().equals("Público")) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		return nuevo;
	}

	public int darNumeroComparendosInfracion(String pInfraccion)
	{
		int i = 0;
		if(pInfraccion.startsWith("C02"))
		{
			i++;
		}
		return i;
	}

	public String[] darNumerosComparendosTipoServicioParticular()
	{
		Comparable<Comparendo>[] aParticular = darComparendosTipoParticular();
		String infraciones[] = new String[116];

		int A01 = 0;int A02 = 0;int A03 = 0;int A04 = 0;int A05 = 0;int A06 = 0;int A07 = 0;int A08 = 0;int A09 = 0;int A10 = 0;int A11 = 0;int A12 = 0;
		int B01 = 0;int B02 = 0;int B03 = 0;int B04 = 0;int B05 = 0;int B06 = 0;int B07 = 0;int B09 = 0;int B10 = 0;int B11 = 0;int B12 = 0;int B13 = 0;int B14 = 0;int B15 = 0;int B16 = 0;int B17 = 0;int B18 = 0;int B19 = 0;int B20 = 0;int B21 = 0;int B22 = 0;int B23 = 0;
		int C01 = 0;int C02 = 0;int C03 = 0;int C04 = 0;int C05 = 0;int C06 = 0;int C07 = 0;int C08 = 0;int C09 = 0;int C10 = 0;int C11 = 0;int C12 = 0;int C13 = 0;int C14 = 0;int C15 = 0;int C16 = 0;int C17 = 0;int C18 = 0;int C19 = 0;int C20 = 0;int C21 = 0;int C22 = 0;int C23 = 0;int C24 = 0;int C25 = 0;int C26 = 0;int C27 = 0;int C28 = 0;int C29 = 0;int C30 = 0;int C31 = 0;int C32 = 0;int C33 = 0;int C34 = 0;int C35 = 0;int C36 = 0;int C37 = 0;int C38 = 0;int C39 = 0;int C40 = 0;
		int D01 = 0;int D02 = 0;int D03 = 0;int D04 = 0;int D05 = 0;int D06 = 0;int D07 = 0;int D08 = 0;int D09 = 0;int D11 = 0;int D12 = 0;int D13 = 0;int D14 = 0;int D15 = 0;int D16 = 0;int D17 = 0;
		int E02 = 0;int E04 = 0;
		int F01 = 0;int F02 = 0;int F03 = 0;int F04 = 0;int F05 = 0;int F06 = 0;int F07 = 0;int F08 = 0;int F09 = 0;int F11 = 0;int F12 = 0;
		int G01 = 0;
		int H01 = 0;int H02 = 0;int H03 = 0;int H04 = 0;int H05 = 0;int H06 = 0;int H07 = 0;int H08 = 0;int H10 = 0;int H11 = 0;int H13 = 0;
		int I01 = 0;int I02 = 0;

		for(int i = 0; i < aParticular.length ; i++)
		{
			Comparendo actual = (Comparendo) aParticular[i];
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01"))
					A01 ++;
				if(actual.getInfraccion().startsWith("A02"))
					A02 ++;
				if(actual.getInfraccion().startsWith("A03"))
					A03 ++;
				if(actual.getInfraccion().startsWith("A04"))
					A04 ++;
				if(actual.getInfraccion().startsWith("A05"))
					A05 ++;
				if(actual.getInfraccion().startsWith("A06"))
					A06 ++;
				if(actual.getInfraccion().startsWith("A07"))
					A07 ++;
				if(actual.getInfraccion().startsWith("A08"))
					A08 ++;
				if(actual.getInfraccion().startsWith("A09"))
					A09 ++;
				if(actual.getInfraccion().startsWith("A10"))
					A10 ++;
				if(actual.getInfraccion().startsWith("A11"))
					A11 ++;
				if(actual.getInfraccion().startsWith("A12"))
					A12 ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01"))
					B01 ++;
				if(actual.getInfraccion().startsWith("B02"))
					B02 ++;
				if(actual.getInfraccion().startsWith("B03"))
					B03 ++;
				if(actual.getInfraccion().startsWith("B04"))
					B04 ++;
				if(actual.getInfraccion().startsWith("B05"))
					B05 ++;
				if(actual.getInfraccion().startsWith("B06"))
					B06 ++;
				if(actual.getInfraccion().startsWith("B07"))
					B07 ++;
				if(actual.getInfraccion().startsWith("B09"))
					B09 ++;
				if(actual.getInfraccion().startsWith("B10"))
					B10 ++;
				if(actual.getInfraccion().startsWith("B11"))
					B11 ++;
				if(actual.getInfraccion().startsWith("B12"))
					B12 ++;
				if(actual.getInfraccion().startsWith("B13"))
					B13 ++;
				if(actual.getInfraccion().startsWith("B14"))
					B14 ++;
				if(actual.getInfraccion().startsWith("B15"))
					B15 ++;
				if(actual.getInfraccion().startsWith("B16"))
					B16 ++;
				if(actual.getInfraccion().startsWith("B17"))
					B17 ++;
				if(actual.getInfraccion().startsWith("B18"))
					B18 ++;
				if(actual.getInfraccion().startsWith("B19"))
					B19 ++;
				if(actual.getInfraccion().startsWith("B20"))
					B20 ++;
				if(actual.getInfraccion().startsWith("B21"))
					B21 ++;
				if(actual.getInfraccion().startsWith("B22"))
					B22 ++;
				if(actual.getInfraccion().startsWith("B23"))
					B23 ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01"))
					C01 ++;
				if(actual.getInfraccion().startsWith("C02"))
					C02 ++;
				if(actual.getInfraccion().startsWith("C03"))
					C03 ++;
				if(actual.getInfraccion().startsWith("C04"))
					C04 ++;
				if(actual.getInfraccion().startsWith("C05"))
					C05 ++;
				if(actual.getInfraccion().startsWith("C06"))
					C06 ++;
				if(actual.getInfraccion().startsWith("C07"))
					C07 ++;
				if(actual.getInfraccion().startsWith("C08"))
					C08 ++;
				if(actual.getInfraccion().startsWith("C09"))
					C09 ++;
				if(actual.getInfraccion().startsWith("C10"))
					C10 ++;
				if(actual.getInfraccion().startsWith("C11"))
					C11 ++;
				if(actual.getInfraccion().startsWith("C12"))
					C12 ++;
				if(actual.getInfraccion().startsWith("C13"))
					C13 ++;
				if(actual.getInfraccion().startsWith("C14"))
					C14 ++;
				if(actual.getInfraccion().startsWith("C15"))
					C15 ++;
				if(actual.getInfraccion().startsWith("C16"))
					C16 ++;
				if(actual.getInfraccion().startsWith("C17"))
					C17 ++;
				if(actual.getInfraccion().startsWith("C18"))
					C18 ++;
				if(actual.getInfraccion().startsWith("C19"))
					C19 ++;
				if(actual.getInfraccion().startsWith("C20"))
					C20 ++;
				if(actual.getInfraccion().startsWith("C21"))
					C21 ++;
				if(actual.getInfraccion().startsWith("C22"))
					C22 ++;
				if(actual.getInfraccion().startsWith("C23"))
					C23 ++;
				if(actual.getInfraccion().startsWith("C24"))
					C24 ++;
				if(actual.getInfraccion().startsWith("C25"))
					C25 ++;
				if(actual.getInfraccion().startsWith("C26"))
					C26 ++;
				if(actual.getInfraccion().startsWith("C27"))
					C27 ++;
				if(actual.getInfraccion().startsWith("C28"))
					C28 ++;
				if(actual.getInfraccion().startsWith("C29"))
					C29 ++;
				if(actual.getInfraccion().startsWith("C30"))
					C30 ++;
				if(actual.getInfraccion().startsWith("C31"))
					C31 ++;
				if(actual.getInfraccion().startsWith("C32"))
					C32 ++;
				if(actual.getInfraccion().startsWith("C33"))
					C33 ++;
				if(actual.getInfraccion().startsWith("C34"))
					C34 ++;
				if(actual.getInfraccion().startsWith("C35"))
					C35 ++;
				if(actual.getInfraccion().startsWith("C36"))
					C36 ++;
				if(actual.getInfraccion().startsWith("C37"))
					C37 ++;
				if(actual.getInfraccion().startsWith("C38"))
					C38 ++;
				if(actual.getInfraccion().startsWith("C39"))
					C39 ++;
				if(actual.getInfraccion().startsWith("C40"))
					C40 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01"))
					D01 ++;
				if(actual.getInfraccion().startsWith("D02"))
					D02 ++;
				if(actual.getInfraccion().startsWith("D03"))
					D03 ++;
				if(actual.getInfraccion().startsWith("D04"))
					D04 ++;
				if(actual.getInfraccion().startsWith("D05"))
					D05 ++;
				if(actual.getInfraccion().startsWith("D06"))
					D06 ++;
				if(actual.getInfraccion().startsWith("D07"))
					D07 ++;
				if(actual.getInfraccion().startsWith("D08"))
					D08 ++;
				if(actual.getInfraccion().startsWith("D09"))
					D09 ++;
				if(actual.getInfraccion().startsWith("D11"))
					D11 ++;
				if(actual.getInfraccion().startsWith("D12"))
					D12 ++;
				if(actual.getInfraccion().startsWith("D13"))
					D13 ++;
				if(actual.getInfraccion().startsWith("D14"))
					D14 ++;
				if(actual.getInfraccion().startsWith("D15"))
					D15 ++;
				if(actual.getInfraccion().startsWith("D16"))
					D16 ++;
				if(actual.getInfraccion().startsWith("D17"))
					D17 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E02"))
					E02 ++;
				if(actual.getInfraccion().startsWith("E04"))
					E04 ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01"))
					F01 ++;
				if(actual.getInfraccion().startsWith("F02"))
					F02 ++;
				if(actual.getInfraccion().startsWith("F03"))
					F03 ++;
				if(actual.getInfraccion().startsWith("F04"))
					F04 ++;
				if(actual.getInfraccion().startsWith("F05"))
					F05 ++;
				if(actual.getInfraccion().startsWith("F06"))
					F06 ++;
				if(actual.getInfraccion().startsWith("F07"))
					F07 ++;
				if(actual.getInfraccion().startsWith("F08"))
					F08 ++;
				if(actual.getInfraccion().startsWith("F09"))
					F09 ++;
				if(actual.getInfraccion().startsWith("F11"))
					F11 ++;
				if(actual.getInfraccion().startsWith("F12"))
					F12 ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01"))
					G01 ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01"))
					H01 ++;
				if(actual.getInfraccion().startsWith("H02"))
					H02 ++;
				if(actual.getInfraccion().startsWith("H03"))
					H03 ++;
				if(actual.getInfraccion().startsWith("H04"))
					H04 ++;
				if(actual.getInfraccion().startsWith("H05"))
					H05 ++;
				if(actual.getInfraccion().startsWith("H06"))
					H06 ++;
				if(actual.getInfraccion().startsWith("H07"))
					H07 ++;
				if(actual.getInfraccion().startsWith("H08"))
					H08 ++;
				if(actual.getInfraccion().startsWith("H10"))
					H10 ++;
				if(actual.getInfraccion().startsWith("H11"))
					H11 ++;
				if(actual.getInfraccion().startsWith("H13"))
					H13 ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01"))
					I01 ++;
				if(actual.getInfraccion().startsWith("I02"))
					I02 ++;
			}
		}

		infraciones[0] = "A01          | " + A01;infraciones[1] = "A02          | " + A02;infraciones[2] = "A03          | " + A03;infraciones[3] = "A04          | " + A04;infraciones[4] = "A05          | " + A05;
		infraciones[5] = "A06          | " + A06;infraciones[6] = "A07          | " + A07;infraciones[7] = "A08          | " + A08;infraciones[8] = "A09          | " + A09;infraciones[9] = "A10          | " + A10;
		infraciones[10] = "A11          | " + A11;infraciones[11] = "A12          | " + A12;infraciones[12] = "B01          | " + B01;infraciones[12] = "B01          | " + B01;infraciones[13] = "B02          | " + B02;
		infraciones[14] = "B03          | " + B03;infraciones[15] = "B04          | " + B04;infraciones[16] = "B05          | " + B05;infraciones[17] = "B06          | " + B06;infraciones[18] = "B07          | " + B07;
		infraciones[19] = "B09          | " + B09;infraciones[20] = "B10          | " + B10;infraciones[21] = "B11          | " + B11;infraciones[22] = "B12          | " + B12;
		infraciones[23] = "B13          | " + B13;infraciones[24] = "B14          | " + B14;infraciones[25] = "B15          | " + B15;infraciones[26] = "B16          | " + B16;infraciones[27] = "B17          | " + B17;
		infraciones[28] = "B18          | " + B18;infraciones[29] = "B19          | " + B19;infraciones[30] = "B20          | " + B20;infraciones[31] = "B21          | " + B21;infraciones[32] = "B22          | " + B22;
		infraciones[33] = "B23          | " + B23;infraciones[34] = "C01          | " + C01;infraciones[35] = "C02          | " + C02;infraciones[36] = "C03          | " + C03;infraciones[37] = "C04          | " + C04;
		infraciones[37] = "C05          | " + C05;infraciones[38] = "C06          | " + C06;infraciones[39] = "C07          | " + C07;infraciones[40] = "C08          | " + C08;infraciones[41] = "C09          | " + C09;
		infraciones[42] = "C10          | " + C10;infraciones[43] = "C11          | " + C11;infraciones[44] = "C12          | " + C12;infraciones[45] = "C13          | " + C13;infraciones[46] = "C14          | " + C14;
		infraciones[47] = "C15          | " + C15;infraciones[48] = "C16          | " + C16;infraciones[49] = "C17          | " + C17;infraciones[50] = "C18          | " + C18;infraciones[51] = "C19          | " + C19;
		infraciones[52] = "C20          | " + C20;infraciones[53] = "C21          | " + C21;infraciones[54] = "C22          | " + C22;infraciones[55] = "C23          | " + C23;infraciones[56] = "C24          | " + C24;
		infraciones[57] = "C25          | " + C25;infraciones[58] = "C26          | " + C26;infraciones[59] = "C27          | " + C27;infraciones[60] = "C28          | " + C28;infraciones[61] = "C29          | " + C29;
		infraciones[62] = "C30          | " + C30;infraciones[63] = "C31          | " + C31;infraciones[64] = "C32          | " + C32;infraciones[65] = "C33          | " + C33;infraciones[66] = "C34          | " + C34;
		infraciones[67] = "C35          | " + C35;infraciones[68] = "C36          | " + C36;infraciones[69] = "C37          | " + C37;infraciones[70] = "C38          | " + C38;infraciones[71] = "C39          | " + C39;
		infraciones[72] = "C40          | " + C40;infraciones[73] = "D01          | " + D01;infraciones[74] = "D02          | " + D02;infraciones[75] = "D03          | " + D03;infraciones[76] = "D04          | " + D04;
		infraciones[77] = "D05          | " + D05;infraciones[78] = "D06          | " + D06;infraciones[79] = "D07          | " + D07;infraciones[80] = "D08          | " + D08;infraciones[81] = "D09          | " + D09;
		infraciones[82] = "D11          | " + D11;infraciones[83] = "D12          | " + D12;infraciones[84] = "D13          | " + D13;infraciones[85] = "D14          | " + D14;
		infraciones[86] = "D15          | " + D15;infraciones[87] = "D16          | " + D16;infraciones[88] = "D17          | " + D17;infraciones[89] = "E02          | " + E02;infraciones[90] = "E04          | " + E04;
		infraciones[91] = "F01          | " + F01;infraciones[92] = "F02          | " + F02;infraciones[93] = "F03          | " + F03;infraciones[94] = "F04          | " + F04;infraciones[95] = "F05          | " + F05;
		infraciones[96] = "F06          | " + F06;infraciones[97] = "F07          | " + F07;infraciones[98] = "F08          | " + F08;infraciones[99] = "F09          | " + F09;
		infraciones[100] = "F11          | " + F11;infraciones[101] = "F12          | " + F12;infraciones[102] = "G01          | " + G01;infraciones[103] = "H01          | " + H01;
		infraciones[104] = "H02          | " + H02;infraciones[105] = "H03          | " + H03;infraciones[106] = "H04          | " + H04;infraciones[107] = "H05          | " + H05;infraciones[108] = "H06          | " + H06;
		infraciones[109] = "H07          | " + H07;infraciones[110] = "H08          | " + H08;infraciones[111] = "H10          | " + H10;infraciones[112] = "H11          | " + H11;
		infraciones[113] = "H13          | " + H13;infraciones[114] = "I01          | " + I01;infraciones[115] = "I02          | " + I02;

		return infraciones;
	}

	public String[] darNumerosComparendosTipoServicioPublico()
	{
		Comparable<Comparendo>[] aParticular = darComparendosTipoPublico();
		String infraciones[] = new String[116];

		int A01 = 0;int A02 = 0;int A03 = 0;int A04 = 0;int A05 = 0;int A06 = 0;int A07 = 0;int A08 = 0;int A09 = 0;int A10 = 0;int A11 = 0;int A12 = 0;
		int B01 = 0;int B02 = 0;int B03 = 0;int B04 = 0;int B05 = 0;int B06 = 0;int B07 = 0;int B09 = 0;int B10 = 0;int B11 = 0;int B12 = 0;int B13 = 0;int B14 = 0;int B15 = 0;int B16 = 0;int B17 = 0;int B18 = 0;int B19 = 0;int B20 = 0;int B21 = 0;int B22 = 0;int B23 = 0;
		int C01 = 0;int C02 = 0;int C03 = 0;int C04 = 0;int C05 = 0;int C06 = 0;int C07 = 0;int C08 = 0;int C09 = 0;int C10 = 0;int C11 = 0;int C12 = 0;int C13 = 0;int C14 = 0;int C15 = 0;int C16 = 0;int C17 = 0;int C18 = 0;int C19 = 0;int C20 = 0;int C21 = 0;int C22 = 0;int C23 = 0;int C24 = 0;int C25 = 0;int C26 = 0;int C27 = 0;int C28 = 0;int C29 = 0;int C30 = 0;int C31 = 0;int C32 = 0;int C33 = 0;int C34 = 0;int C35 = 0;int C36 = 0;int C37 = 0;int C38 = 0;int C39 = 0;int C40 = 0;
		int D01 = 0;int D02 = 0;int D03 = 0;int D04 = 0;int D05 = 0;int D06 = 0;int D07 = 0;int D08 = 0;int D09 = 0;int D11 = 0;int D12 = 0;int D13 = 0;int D14 = 0;int D15 = 0;int D16 = 0;int D17 = 0;
		int E02 = 0;int E04 = 0;
		int F01 = 0;int F02 = 0;int F03 = 0;int F04 = 0;int F05 = 0;int F06 = 0;int F07 = 0;int F08 = 0;int F09 = 0;int F11 = 0;int F12 = 0;
		int G01 = 0;
		int H01 = 0;int H02 = 0;int H03 = 0;int H04 = 0;int H05 = 0;int H06 = 0;int H07 = 0;int H08 = 0;int H10 = 0;int H11 = 0;int H13 = 0;
		int I01 = 0;int I02 = 0;

		for(int i = 0; i < aParticular.length ; i++)
		{
			Comparendo actual = (Comparendo) aParticular[i];
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01"))
					A01 ++;
				if(actual.getInfraccion().startsWith("A02"))
					A02 ++;
				if(actual.getInfraccion().startsWith("A03"))
					A03 ++;
				if(actual.getInfraccion().startsWith("A04"))
					A04 ++;
				if(actual.getInfraccion().startsWith("A05"))
					A05 ++;
				if(actual.getInfraccion().startsWith("A06"))
					A06 ++;
				if(actual.getInfraccion().startsWith("A07"))
					A07 ++;
				if(actual.getInfraccion().startsWith("A08"))
					A08 ++;
				if(actual.getInfraccion().startsWith("A09"))
					A09 ++;
				if(actual.getInfraccion().startsWith("A10"))
					A10 ++;
				if(actual.getInfraccion().startsWith("A11"))
					A11 ++;
				if(actual.getInfraccion().startsWith("A12"))
					A12 ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01"))
					B01 ++;
				if(actual.getInfraccion().startsWith("B02"))
					B02 ++;
				if(actual.getInfraccion().startsWith("B03"))
					B03 ++;
				if(actual.getInfraccion().startsWith("B04"))
					B04 ++;
				if(actual.getInfraccion().startsWith("B05"))
					B05 ++;
				if(actual.getInfraccion().startsWith("B06"))
					B06 ++;
				if(actual.getInfraccion().startsWith("B07"))
					B07 ++;
				if(actual.getInfraccion().startsWith("B09"))
					B09 ++;
				if(actual.getInfraccion().startsWith("B10"))
					B10 ++;
				if(actual.getInfraccion().startsWith("B11"))
					B11 ++;
				if(actual.getInfraccion().startsWith("B12"))
					B12 ++;
				if(actual.getInfraccion().startsWith("B13"))
					B13 ++;
				if(actual.getInfraccion().startsWith("B14"))
					B14 ++;
				if(actual.getInfraccion().startsWith("B15"))
					B15 ++;
				if(actual.getInfraccion().startsWith("B16"))
					B16 ++;
				if(actual.getInfraccion().startsWith("B17"))
					B17 ++;
				if(actual.getInfraccion().startsWith("B18"))
					B18 ++;
				if(actual.getInfraccion().startsWith("B19"))
					B19 ++;
				if(actual.getInfraccion().startsWith("B20"))
					B20 ++;
				if(actual.getInfraccion().startsWith("B21"))
					B21 ++;
				if(actual.getInfraccion().startsWith("B22"))
					B22 ++;
				if(actual.getInfraccion().startsWith("B23"))
					B23 ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01"))
					C01 ++;
				if(actual.getInfraccion().startsWith("C02"))
					C02 ++;
				if(actual.getInfraccion().startsWith("C03"))
					C03 ++;
				if(actual.getInfraccion().startsWith("C04"))
					C04 ++;
				if(actual.getInfraccion().startsWith("C05"))
					C05 ++;
				if(actual.getInfraccion().startsWith("C06"))
					C06 ++;
				if(actual.getInfraccion().startsWith("C07"))
					C07 ++;
				if(actual.getInfraccion().startsWith("C08"))
					C08 ++;
				if(actual.getInfraccion().startsWith("C09"))
					C09 ++;
				if(actual.getInfraccion().startsWith("C10"))
					C10 ++;
				if(actual.getInfraccion().startsWith("C11"))
					C11 ++;
				if(actual.getInfraccion().startsWith("C12"))
					C12 ++;
				if(actual.getInfraccion().startsWith("C13"))
					C13 ++;
				if(actual.getInfraccion().startsWith("C14"))
					C14 ++;
				if(actual.getInfraccion().startsWith("C15"))
					C15 ++;
				if(actual.getInfraccion().startsWith("C16"))
					C16 ++;
				if(actual.getInfraccion().startsWith("C17"))
					C17 ++;
				if(actual.getInfraccion().startsWith("C18"))
					C18 ++;
				if(actual.getInfraccion().startsWith("C19"))
					C19 ++;
				if(actual.getInfraccion().startsWith("C20"))
					C20 ++;
				if(actual.getInfraccion().startsWith("C21"))
					C21 ++;
				if(actual.getInfraccion().startsWith("C22"))
					C22 ++;
				if(actual.getInfraccion().startsWith("C23"))
					C23 ++;
				if(actual.getInfraccion().startsWith("C24"))
					C24 ++;
				if(actual.getInfraccion().startsWith("C25"))
					C25 ++;
				if(actual.getInfraccion().startsWith("C26"))
					C26 ++;
				if(actual.getInfraccion().startsWith("C27"))
					C27 ++;
				if(actual.getInfraccion().startsWith("C28"))
					C28 ++;
				if(actual.getInfraccion().startsWith("C29"))
					C29 ++;
				if(actual.getInfraccion().startsWith("C30"))
					C30 ++;
				if(actual.getInfraccion().startsWith("C31"))
					C31 ++;
				if(actual.getInfraccion().startsWith("C32"))
					C32 ++;
				if(actual.getInfraccion().startsWith("C33"))
					C33 ++;
				if(actual.getInfraccion().startsWith("C34"))
					C34 ++;
				if(actual.getInfraccion().startsWith("C35"))
					C35 ++;
				if(actual.getInfraccion().startsWith("C36"))
					C36 ++;
				if(actual.getInfraccion().startsWith("C37"))
					C37 ++;
				if(actual.getInfraccion().startsWith("C38"))
					C38 ++;
				if(actual.getInfraccion().startsWith("C39"))
					C39 ++;
				if(actual.getInfraccion().startsWith("C40"))
					C40 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01"))
					D01 ++;
				if(actual.getInfraccion().startsWith("D02"))
					D02 ++;
				if(actual.getInfraccion().startsWith("D03"))
					D03 ++;
				if(actual.getInfraccion().startsWith("D04"))
					D04 ++;
				if(actual.getInfraccion().startsWith("D05"))
					D05 ++;
				if(actual.getInfraccion().startsWith("D06"))
					D06 ++;
				if(actual.getInfraccion().startsWith("D07"))
					D07 ++;
				if(actual.getInfraccion().startsWith("D08"))
					D08 ++;
				if(actual.getInfraccion().startsWith("D09"))
					D09 ++;
				if(actual.getInfraccion().startsWith("D11"))
					D11 ++;
				if(actual.getInfraccion().startsWith("D12"))
					D12 ++;
				if(actual.getInfraccion().startsWith("D13"))
					D13 ++;
				if(actual.getInfraccion().startsWith("D14"))
					D14 ++;
				if(actual.getInfraccion().startsWith("D15"))
					D15 ++;
				if(actual.getInfraccion().startsWith("D16"))
					D16 ++;
				if(actual.getInfraccion().startsWith("D17"))
					D17 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E02"))
					E02 ++;
				if(actual.getInfraccion().startsWith("E04"))
					E04 ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01"))
					F01 ++;
				if(actual.getInfraccion().startsWith("F02"))
					F02 ++;
				if(actual.getInfraccion().startsWith("F03"))
					F03 ++;
				if(actual.getInfraccion().startsWith("F04"))
					F04 ++;
				if(actual.getInfraccion().startsWith("F05"))
					F05 ++;
				if(actual.getInfraccion().startsWith("F06"))
					F06 ++;
				if(actual.getInfraccion().startsWith("F07"))
					F07 ++;
				if(actual.getInfraccion().startsWith("F08"))
					F08 ++;
				if(actual.getInfraccion().startsWith("F09"))
					F09 ++;
				if(actual.getInfraccion().startsWith("F11"))
					F11 ++;
				if(actual.getInfraccion().startsWith("F12"))
					F12 ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01"))
					G01 ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01"))
					H01 ++;
				if(actual.getInfraccion().startsWith("H02"))
					H02 ++;
				if(actual.getInfraccion().startsWith("H03"))
					H03 ++;
				if(actual.getInfraccion().startsWith("H04"))
					H04 ++;
				if(actual.getInfraccion().startsWith("H05"))
					H05 ++;
				if(actual.getInfraccion().startsWith("H06"))
					H06 ++;
				if(actual.getInfraccion().startsWith("H07"))
					H07 ++;
				if(actual.getInfraccion().startsWith("H08"))
					H08 ++;
				if(actual.getInfraccion().startsWith("H10"))
					H10 ++;
				if(actual.getInfraccion().startsWith("H11"))
					H11 ++;
				if(actual.getInfraccion().startsWith("H13"))
					H13 ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01"))
					I01 ++;
				if(actual.getInfraccion().startsWith("I02"))
					I02 ++;
			}
		}

		infraciones[0] = "        | " + A01;infraciones[1] = "         | " + A02;infraciones[2] = "         | " + A03;infraciones[3] = "        | " + A04;infraciones[4] = "       | " + A05;
		infraciones[5] = "       | " + A06;infraciones[6] = "         | " + A07;infraciones[7] = "       | " + A08;infraciones[8] = "         | " + A09;infraciones[9] = "          | " + A10;
		infraciones[10] = "         | " + A11;infraciones[11] = "         | " + A12;infraciones[12] = "         | " + B01;infraciones[12] = "       | " + B01;infraciones[13] = "       | " + B02;
		infraciones[14] = "        | " + B03;infraciones[15] = "        | " + B04;infraciones[16] = "         | " + B05;infraciones[17] = "          | " + B06;infraciones[18] = "        | " + B07;
		infraciones[19] = "          | " + B09;infraciones[20] = "        | " + B10;infraciones[21] = "         | " + B11;infraciones[22] = "          | " + B12;
		infraciones[23] = "          | " + B13;infraciones[24] = "         | " + B14;infraciones[25] = "         | " + B15;infraciones[26] = "          | " + B16;infraciones[27] = "          | " + B17;
		infraciones[28] = "          | " + B18;infraciones[29] = "          | " + B19;infraciones[30] = "         | " + B20;infraciones[31] = "         | " + B21;infraciones[32] = "        | " + B22;
		infraciones[33] = "          | " + B23;infraciones[34] = "         | " + C01;infraciones[35] = "     | " + C02;infraciones[36] = "       | " + C03;infraciones[37] = "          | " + C04;
		infraciones[37] = "          | " + C05;infraciones[38] = "       | " + C06;infraciones[39] = "         | " + C07;infraciones[40] = "        | " + C08;infraciones[41] = "          | " + C09;
		infraciones[42] = "         | " + C10;infraciones[43] = "       | " + C11;infraciones[44] = "          | " + C12;infraciones[45] = "         | " + C13;infraciones[46] = "      | " + C14;
		infraciones[47] = "        | " + C15;infraciones[48] = "          | " + C16;infraciones[49] = "          | " + C17;infraciones[50] = "         | " + C18;infraciones[51] = "        | " + C19;
		infraciones[52] = "          | " + C20;infraciones[53] = "         | " + C21;infraciones[54] = "         | " + C22;infraciones[55] = "         | " + C23;infraciones[56] = "      | " + C24;
		infraciones[57] = "         | " + C25;infraciones[58] = "         | " + C26;infraciones[59] = "         | " + C27;infraciones[60] = "        | " + C28;infraciones[61] = "       | " + C29;
		infraciones[62] = "         | " + C30;infraciones[63] = "      | " + C31;infraciones[64] = "       | " + C32;infraciones[65] = "        | " + C33;infraciones[66] = "         | " + C34;
		infraciones[67] = "      | " + C35;infraciones[68] = "          | " + C36;infraciones[69] = "         | " + C37;infraciones[70] = "       | " + C38;infraciones[71] = "          | " + C39;
		infraciones[72] = "          | " + C40;infraciones[73] = "       | " + D01;infraciones[74] = "       | " + D02;infraciones[75] = "       | " + D03;infraciones[76] = "       | " + D04;
		infraciones[77] = "       | " + D05;infraciones[78] = "        | " + D06;infraciones[79] = "       | " + D07;infraciones[80] = "        | " + D08;infraciones[81] = "          | " + D09;
		infraciones[82] = "          | " + D11;infraciones[83] = "       | " + D12;infraciones[84] = "          | " + D13;infraciones[85] = "          | " + D14;
		infraciones[86] = "          | " + D15;infraciones[87] = "          | " + D16;infraciones[88] = "          | " + D17;infraciones[89] = "          | " + E02;infraciones[90] = "          | " + E04;
		infraciones[91] = "         | " + F01;infraciones[92] = "         | " + F02;infraciones[93] = "        | " + F03;infraciones[94] = "         | " + F04;infraciones[95] = "         | " + F05;
		infraciones[96] = "       | " + F06;infraciones[97] = "       | " + F07;infraciones[98] = "          | " + F08;infraciones[99] = "          | " + F09;
		infraciones[100] = "          | " + F11;infraciones[101] = "         | " + F12;infraciones[102] = "        | " + G01;infraciones[103] = "         | " + H01;
		infraciones[104] = "       | " + H02;infraciones[105] = "      | " + H03;infraciones[106] = "        | " + H04;infraciones[107] = "         | " + H05;infraciones[108] = "         | " + H06;
		infraciones[109] = "         | " + H07;infraciones[110] = "         | " + H08;infraciones[111] = "       | " + H10;infraciones[112] = "          | " + H11;
		infraciones[113]= "       | " + H13;infraciones[114] = "        | " + I01;infraciones[115] = "          | " + I02;

		return infraciones;
	}

	/**
	 * Retorna un arreglo comparable de los comparendos que tienen la Fecha 1
	 * @return Arrgelo Comparable de comparendos con las Fecha1
	 */
	public String[] darComparendosFecha1(String pFecha1)
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		int j = 0;
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				if(elementoActual.getFecha_hora().equals(pFecha1)) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}
		
		Comparable<Comparendo>[] aFecha1 = null;
		aFecha1 = nuevo;
		String infraciones[] = new String[126];

		int A01 = 0;int A02 = 0;int A03 = 0;int A04 = 0;int A05 = 0;int A06 = 0;int A07 = 0;int A08 = 0;int A09 = 0;int A10 = 0;int A11 = 0;int A12 = 0;
		int B01 = 0;int B02 = 0;int B03 = 0;int B04 = 0;int B05 = 0;int B06 = 0;int B07 = 0;int B08 = 0;int B09 = 0;int B10 = 0;int B11 = 0;int B12 = 0;int B13 = 0;int B14 = 0;int B15 = 0;int B16 = 0;int B17 = 0;int B18 = 0;int B19 = 0;int B20 = 0;int B21 = 0;int B22 = 0;int B23 = 0;
		int C01 = 0;int C02 = 0;int C03 = 0;int C04 = 0;int C05 = 0;int C06 = 0;int C07 = 0;int C08 = 0;int C09 = 0;int C10 = 0;int C11 = 0;int C12 = 0;int C13 = 0;int C14 = 0;int C15 = 0;int C16 = 0;int C17 = 0;int C18 = 0;int C19 = 0;int C20 = 0;int C21 = 0;int C22 = 0;int C23 = 0;int C24 = 0;int C25 = 0;int C26 = 0;int C27 = 0;int C28 = 0;int C29 = 0;int C30 = 0;int C31 = 0;int C32 = 0;int C33 = 0;int C34 = 0;int C35 = 0;int C36 = 0;int C37 = 0;int C38 = 0;int C39 = 0;int C40 = 0;
		int D01 = 0;int D02 = 0;int D03 = 0;int D04 = 0;int D05 = 0;int D06 = 0;int D07 = 0;int D08 = 0;int D09 = 0;int D10 = 0;int D11 = 0;int D12 = 0;int D13 = 0;int D14 = 0;int D15 = 0;int D16 = 0;int D17 = 0;
		int E01 = 0;int E02 = 0;int E03 = 0;int E04 = 0;
		int F01 = 0;int F02 = 0;int F03 = 0;int F04 = 0;int F05 = 0;int F06 = 0;int F07 = 0;int F08 = 0;int F09 = 0;int F10 = 0;int F11 = 0;int F12 = 0;
		int G01 = 0;int G02 = 0;
		int H01 = 0;int H02 = 0;int H03 = 0;int H04 = 0;int H05 = 0;int H06 = 0;int H07 = 0;int H08 = 0;int H09 = 0;int H10 = 0;int H11 = 0;int H12 = 0;int H13 = 0;
		int I01 = 0;int I02 = 0;
		int J01 = 0;int J02 = 0; int J03 = 0;int J04 = 0;

		for(int i = 0; i < aFecha1.length ; i++)
		{
			Comparendo actual = (Comparendo) aFecha1[i];
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01"))
					A01 ++;
				if(actual.getInfraccion().startsWith("A02"))
					A02 ++;
				if(actual.getInfraccion().startsWith("A03"))
					A03 ++;
				if(actual.getInfraccion().startsWith("A04"))
					A04 ++;
				if(actual.getInfraccion().startsWith("A05"))
					A05 ++;
				if(actual.getInfraccion().startsWith("A06"))
					A06 ++;
				if(actual.getInfraccion().startsWith("A07"))
					A07 ++;
				if(actual.getInfraccion().startsWith("A08"))
					A08 ++;
				if(actual.getInfraccion().startsWith("A09"))
					A09 ++;
				if(actual.getInfraccion().startsWith("A10"))
					A10 ++;
				if(actual.getInfraccion().startsWith("A11"))
					A11 ++;
				if(actual.getInfraccion().startsWith("A12"))
					A12 ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01"))
					B01 ++;
				if(actual.getInfraccion().startsWith("B02"))
					B02 ++;
				if(actual.getInfraccion().startsWith("B03"))
					B03 ++;
				if(actual.getInfraccion().startsWith("B04"))
					B04 ++;
				if(actual.getInfraccion().startsWith("B05"))
					B05 ++;
				if(actual.getInfraccion().startsWith("B06"))
					B06 ++;
				if(actual.getInfraccion().startsWith("B07"))
					B07 ++;
				if(actual.getInfraccion().startsWith("B08"))
					B08 ++;
				if(actual.getInfraccion().startsWith("B09"))
					B09 ++;
				if(actual.getInfraccion().startsWith("B10"))
					B10 ++;
				if(actual.getInfraccion().startsWith("B11"))
					B11 ++;
				if(actual.getInfraccion().startsWith("B12"))
					B12 ++;
				if(actual.getInfraccion().startsWith("B13"))
					B13 ++;
				if(actual.getInfraccion().startsWith("B14"))
					B14 ++;
				if(actual.getInfraccion().startsWith("B15"))
					B15 ++;
				if(actual.getInfraccion().startsWith("B16"))
					B16 ++;
				if(actual.getInfraccion().startsWith("B17"))
					B17 ++;
				if(actual.getInfraccion().startsWith("B18"))
					B18 ++;
				if(actual.getInfraccion().startsWith("B19"))
					B19 ++;
				if(actual.getInfraccion().startsWith("B20"))
					B20 ++;
				if(actual.getInfraccion().startsWith("B21"))
					B21 ++;
				if(actual.getInfraccion().startsWith("B22"))
					B22 ++;
				if(actual.getInfraccion().startsWith("B23"))
					B23 ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01"))
					C01 ++;
				if(actual.getInfraccion().startsWith("C02"))
					C02 ++;
				if(actual.getInfraccion().startsWith("C03"))
					C03 ++;
				if(actual.getInfraccion().startsWith("C04"))
					C04 ++;
				if(actual.getInfraccion().startsWith("C05"))
					C05 ++;
				if(actual.getInfraccion().startsWith("C06"))
					C06 ++;
				if(actual.getInfraccion().startsWith("C07"))
					C07 ++;
				if(actual.getInfraccion().startsWith("C08"))
					C08 ++;
				if(actual.getInfraccion().startsWith("C09"))
					C09 ++;
				if(actual.getInfraccion().startsWith("C10"))
					C10 ++;
				if(actual.getInfraccion().startsWith("C11"))
					C11 ++;
				if(actual.getInfraccion().startsWith("C12"))
					C12 ++;
				if(actual.getInfraccion().startsWith("C13"))
					C13 ++;
				if(actual.getInfraccion().startsWith("C14"))
					C14 ++;
				if(actual.getInfraccion().startsWith("C15"))
					C15 ++;
				if(actual.getInfraccion().startsWith("C16"))
					C16 ++;
				if(actual.getInfraccion().startsWith("C17"))
					C17 ++;
				if(actual.getInfraccion().startsWith("C18"))
					C18 ++;
				if(actual.getInfraccion().startsWith("C19"))
					C19 ++;
				if(actual.getInfraccion().startsWith("C20"))
					C20 ++;
				if(actual.getInfraccion().startsWith("C21"))
					C21 ++;
				if(actual.getInfraccion().startsWith("C22"))
					C22 ++;
				if(actual.getInfraccion().startsWith("C23"))
					C23 ++;
				if(actual.getInfraccion().startsWith("C24"))
					C24 ++;
				if(actual.getInfraccion().startsWith("C25"))
					C25 ++;
				if(actual.getInfraccion().startsWith("C26"))
					C26 ++;
				if(actual.getInfraccion().startsWith("C27"))
					C27 ++;
				if(actual.getInfraccion().startsWith("C28"))
					C28 ++;
				if(actual.getInfraccion().startsWith("C29"))
					C29 ++;
				if(actual.getInfraccion().startsWith("C30"))
					C30 ++;
				if(actual.getInfraccion().startsWith("C31"))
					C31 ++;
				if(actual.getInfraccion().startsWith("C32"))
					C32 ++;
				if(actual.getInfraccion().startsWith("C33"))
					C33 ++;
				if(actual.getInfraccion().startsWith("C34"))
					C34 ++;
				if(actual.getInfraccion().startsWith("C35"))
					C35 ++;
				if(actual.getInfraccion().startsWith("C36"))
					C36 ++;
				if(actual.getInfraccion().startsWith("C37"))
					C37 ++;
				if(actual.getInfraccion().startsWith("C38"))
					C38 ++;
				if(actual.getInfraccion().startsWith("C39"))
					C39 ++;
				if(actual.getInfraccion().startsWith("C40"))
					C40 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01"))
					D01 ++;
				if(actual.getInfraccion().startsWith("D02"))
					D02 ++;
				if(actual.getInfraccion().startsWith("D03"))
					D03 ++;
				if(actual.getInfraccion().startsWith("D04"))
					D04 ++;
				if(actual.getInfraccion().startsWith("D05"))
					D05 ++;
				if(actual.getInfraccion().startsWith("D06"))
					D06 ++;
				if(actual.getInfraccion().startsWith("D07"))
					D07 ++;
				if(actual.getInfraccion().startsWith("D08"))
					D08 ++;
				if(actual.getInfraccion().startsWith("D09"))
					D09 ++;
				if(actual.getInfraccion().startsWith("D10"))
					D10 ++;
				if(actual.getInfraccion().startsWith("D11"))
					D11 ++;
				if(actual.getInfraccion().startsWith("D12"))
					D12 ++;
				if(actual.getInfraccion().startsWith("D13"))
					D13 ++;
				if(actual.getInfraccion().startsWith("D14"))
					D14 ++;
				if(actual.getInfraccion().startsWith("D15"))
					D15 ++;
				if(actual.getInfraccion().startsWith("D16"))
					D16 ++;
				if(actual.getInfraccion().startsWith("D17"))
					D17 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E01"))
					E01 ++;
				if(actual.getInfraccion().startsWith("E02"))
					E02 ++;
				if(actual.getInfraccion().startsWith("E03"))
					E03 ++;
				if(actual.getInfraccion().startsWith("E04"))
					E04 ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01"))
					F01 ++;
				if(actual.getInfraccion().startsWith("F02"))
					F02 ++;
				if(actual.getInfraccion().startsWith("F03"))
					F03 ++;
				if(actual.getInfraccion().startsWith("F04"))
					F04 ++;
				if(actual.getInfraccion().startsWith("F05"))
					F05 ++;
				if(actual.getInfraccion().startsWith("F06"))
					F06 ++;
				if(actual.getInfraccion().startsWith("F07"))
					F07 ++;
				if(actual.getInfraccion().startsWith("F08"))
					F08 ++;
				if(actual.getInfraccion().startsWith("F09"))
					F09 ++;
				if(actual.getInfraccion().startsWith("F10"))
					F10 ++;
				if(actual.getInfraccion().startsWith("F11"))
					F11 ++;
				if(actual.getInfraccion().startsWith("F12"))
					F12 ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01"))
					G01 ++;
				if(actual.getInfraccion().startsWith("G02"))
					G02 ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01"))
					H01 ++;
				if(actual.getInfraccion().startsWith("H02"))
					H02 ++;
				if(actual.getInfraccion().startsWith("H03"))
					H03 ++;
				if(actual.getInfraccion().startsWith("H04"))
					H04 ++;
				if(actual.getInfraccion().startsWith("H05"))
					H05 ++;
				if(actual.getInfraccion().startsWith("H06"))
					H06 ++;
				if(actual.getInfraccion().startsWith("H07"))
					H07 ++;
				if(actual.getInfraccion().startsWith("H08"))
					H08 ++;
				if(actual.getInfraccion().startsWith("H09"))
					H09 ++;
				if(actual.getInfraccion().startsWith("H10"))
					H10 ++;
				if(actual.getInfraccion().startsWith("H11"))
					H11 ++;
				if(actual.getInfraccion().startsWith("H12"))
					H12 ++;
				if(actual.getInfraccion().startsWith("H13"))
					H13 ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01"))
					I01 ++;
				if(actual.getInfraccion().startsWith("I02"))
					I02 ++;
				
				// Infracciones J

				if(actual.getInfraccion().startsWith("J01"))
					J01 ++;
				if(actual.getInfraccion().startsWith("J02"))
					J02 ++;
				if(actual.getInfraccion().startsWith("I01"))
					J03 ++;
				if(actual.getInfraccion().startsWith("I02"))
					J04 ++;
			}
		}

		infraciones[0] = "A01          | " + A01;infraciones[1] = "A02          | " + A02;infraciones[2] = "A03          | " + A03;infraciones[3] = "A04          | " + A04;infraciones[4] = "A05          | " + A05;
		infraciones[5] = "A06          | " + A06;infraciones[6] = "A07          | " + A07;infraciones[7] = "A08          | " + A08;infraciones[8] = "A09          | " + A09;infraciones[9] = "A10          | " + A10;
		infraciones[10] = "A11          | " + A11;infraciones[11] = "A12          | " + A12;infraciones[12] = "B01          | " + B01;infraciones[12] = "B02          | " + B02;infraciones[13] = "B03          | " + B03;
		infraciones[14] = "B04          | " + B04;infraciones[15] = "B05          | " + B05;infraciones[16] = "B06          | " + B06;infraciones[17] = "B07          | " + B07;infraciones[18] = "B08          | " + B08;
		infraciones[19] = "B09          | " + B09;infraciones[20] = "B10          | " + B10;infraciones[21] = "B11          | " + B11;infraciones[22] = "B12          | " + B12;infraciones[23] = "B13          | " + B13;
		infraciones[24] = "B14          | " + B14;infraciones[25] = "B15          | " + B15;infraciones[26] = "B16          | " + B16;infraciones[27] = "B17          | " + B17;infraciones[28] = "B18          | " + B18;
		infraciones[29] = "B19          | " + B19;infraciones[30] = "B20          | " + B20;infraciones[31] = "B21          | " + B21;infraciones[32] = "B22          | " + B22;infraciones[33] = "B23          | " + B23;
		infraciones[34] = "C01          | " + C01;infraciones[35] = "C02          | " + C02;infraciones[36] = "C03          | " + C03;infraciones[37] = "C04          | " + C04;infraciones[37] = "C05          | " + C05;
		infraciones[38] = "C06          | " + C06;infraciones[39] = "C07          | " + C07;infraciones[40] = "C08          | " + C08;infraciones[41] = "C09          | " + C09;infraciones[42] = "C10          | " + C10;
		infraciones[43] = "C11          | " + C11;infraciones[44] = "C12          | " + C12;infraciones[45] = "C13          | " + C13;infraciones[46] = "C14          | " + C14;infraciones[47] = "C15          | " + C15;
		infraciones[48] = "C16          | " + C16;infraciones[49] = "C17          | " + C17;infraciones[50] = "C18          | " + C18;infraciones[51] = "C19          | " + C19;infraciones[52] = "C20          | " + C20;
		infraciones[53] = "C21          | " + C21;infraciones[54] = "C22          | " + C22;infraciones[55] = "C23          | " + C23;infraciones[56] = "C24          | " + C24;infraciones[57] = "C25          | " + C25;
		infraciones[58] = "C26          | " + C26;infraciones[59] = "C27          | " + C27;infraciones[60] = "C28          | " + C28;infraciones[61] = "C29          | " + C29;infraciones[62] = "C30          | " + C30;
		infraciones[63] = "C31          | " + C31;infraciones[64] = "C32          | " + C32;infraciones[65] = "C33          | " + C33;infraciones[66] = "C34          | " + C34;infraciones[67] = "C35          | " + C35;
		infraciones[68] = "C36          | " + C36;infraciones[69] = "C37          | " + C37;infraciones[70] = "C38          | " + C38;infraciones[71] = "C39          | " + C39;infraciones[72] = "C40          | " + C40;
		infraciones[73] = "D01          | " + D01;infraciones[74] = "D02          | " + D02;infraciones[75] = "D03          | " + D03;infraciones[76] = "D04          | " + D04;infraciones[77] = "D05          | " + D05;
		infraciones[78] = "D06          | " + D06;infraciones[79] = "D07          | " + D07;infraciones[80] = "D08          | " + D08;infraciones[81] = "D09          | " + D09;infraciones[81] = "D10          | " + D10;
		infraciones[82] = "D11          | " + D11;infraciones[83] = "D12          | " + D12;infraciones[84] = "D13          | " + D13;infraciones[85] = "D14          | " + D14;infraciones[86] = "D15          | " + D15;
		infraciones[87] = "D16          | " + D16;infraciones[88] = "D17          | " + D17;infraciones[89] = "E01          | " + E01;infraciones[90] = "E02          | " + E02;infraciones[91] = "E03          | " + E03;
		infraciones[92] = "E04          | " + E04;infraciones[93] = "F01          | " + F01;infraciones[94] = "F02          | " + F02;infraciones[95] = "F03          | " + F03;infraciones[96] = "F04          | " + F04;
		infraciones[97] = "F05          | " + F05;infraciones[98] = "F06          | " + F06;infraciones[99] = "F07          | " + F07;infraciones[100] = "F08          | " + F08;infraciones[101] = "F09          | " + F09;
		infraciones[102] = "F10          | " + F10;infraciones[103] = "F11          | " + F11;infraciones[104] = "F12          | " + F12;infraciones[105] = "G01          | " + G01;infraciones[106] = "G02          | " + G02;
		infraciones[107] = "H01          | " + H01;infraciones[108] = "H02          | " + H02;infraciones[109] = "H03          | " + H03;infraciones[110] = "H04          | " + H04;infraciones[111] = "H05          | " + H05;
		infraciones[112] = "H06          | " + H06;infraciones[113] = "H07          | " + H07;infraciones[114] = "H08          | " + H08;infraciones[115] = "H09          | " + H09;infraciones[116] = "H10          | " + H10;
		infraciones[117] = "H11          | " + H11;infraciones[118] = "H12          | " + H12;infraciones[119] = "H13          | " + H13;infraciones[120] = "I01          | " + I01;infraciones[121] = "I02          | " + I02;
		infraciones[122] = "J01          | " + J01;infraciones[123] = "J02          | " + J02;infraciones[124] = "J03          | " + J03;infraciones[125] = "J04          | " + J04;
	
		return infraciones;
	}
	
	/**
	 * Retorna un arreglo comparable de los comparendos que tienen la Fecha 2
	 * @return Arreglo Comparable de comparendos con las Fecha2
	 */
	public String[] darComparendosFecha2(String pFecha2)
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		int j = 0;
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				if(elementoActual.getTipo_servi().equals(pFecha2)) 
				{
					nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
					j++;
				}
			}
		}

		Comparable<Comparendo>[] aFecha2 = null;
		aFecha2 = nuevo;
		String infraciones[] = new String[126];

		int A01 = 0;int A02 = 0;int A03 = 0;int A04 = 0;int A05 = 0;int A06 = 0;int A07 = 0;int A08 = 0;int A09 = 0;int A10 = 0;int A11 = 0;int A12 = 0;
		int B01 = 0;int B02 = 0;int B03 = 0;int B04 = 0;int B05 = 0;int B06 = 0;int B07 = 0;int B08 = 0;int B09 = 0;int B10 = 0;int B11 = 0;int B12 = 0;int B13 = 0;int B14 = 0;int B15 = 0;int B16 = 0;int B17 = 0;int B18 = 0;int B19 = 0;int B20 = 0;int B21 = 0;int B22 = 0;int B23 = 0;
		int C01 = 0;int C02 = 0;int C03 = 0;int C04 = 0;int C05 = 0;int C06 = 0;int C07 = 0;int C08 = 0;int C09 = 0;int C10 = 0;int C11 = 0;int C12 = 0;int C13 = 0;int C14 = 0;int C15 = 0;int C16 = 0;int C17 = 0;int C18 = 0;int C19 = 0;int C20 = 0;int C21 = 0;int C22 = 0;int C23 = 0;int C24 = 0;int C25 = 0;int C26 = 0;int C27 = 0;int C28 = 0;int C29 = 0;int C30 = 0;int C31 = 0;int C32 = 0;int C33 = 0;int C34 = 0;int C35 = 0;int C36 = 0;int C37 = 0;int C38 = 0;int C39 = 0;int C40 = 0;
		int D01 = 0;int D02 = 0;int D03 = 0;int D04 = 0;int D05 = 0;int D06 = 0;int D07 = 0;int D08 = 0;int D09 = 0;int D10 = 0;int D11 = 0;int D12 = 0;int D13 = 0;int D14 = 0;int D15 = 0;int D16 = 0;int D17 = 0;
		int E01 = 0;int E02 = 0;int E03 = 0;int E04 = 0;
		int F01 = 0;int F02 = 0;int F03 = 0;int F04 = 0;int F05 = 0;int F06 = 0;int F07 = 0;int F08 = 0;int F09 = 0;int F10 = 0;int F11 = 0;int F12 = 0;
		int G01 = 0;int G02 = 0;
		int H01 = 0;int H02 = 0;int H03 = 0;int H04 = 0;int H05 = 0;int H06 = 0;int H07 = 0;int H08 = 0;int H09 = 0;int H10 = 0;int H11 = 0;int H12 = 0;int H13 = 0;
		int I01 = 0;int I02 = 0;
		int J01 = 0;int J02 = 0; int J03 = 0;int J04 = 0;

		for(int i = 0; i < aFecha2.length ; i++)
		{
			Comparendo actual = (Comparendo) aFecha2[i];
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01"))
					A01 ++;
				if(actual.getInfraccion().startsWith("A02"))
					A02 ++;
				if(actual.getInfraccion().startsWith("A03"))
					A03 ++;
				if(actual.getInfraccion().startsWith("A04"))
					A04 ++;
				if(actual.getInfraccion().startsWith("A05"))
					A05 ++;
				if(actual.getInfraccion().startsWith("A06"))
					A06 ++;
				if(actual.getInfraccion().startsWith("A07"))
					A07 ++;
				if(actual.getInfraccion().startsWith("A08"))
					A08 ++;
				if(actual.getInfraccion().startsWith("A09"))
					A09 ++;
				if(actual.getInfraccion().startsWith("A10"))
					A10 ++;
				if(actual.getInfraccion().startsWith("A11"))
					A11 ++;
				if(actual.getInfraccion().startsWith("A12"))
					A12 ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01"))
					B01 ++;
				if(actual.getInfraccion().startsWith("B02"))
					B02 ++;
				if(actual.getInfraccion().startsWith("B03"))
					B03 ++;
				if(actual.getInfraccion().startsWith("B04"))
					B04 ++;
				if(actual.getInfraccion().startsWith("B05"))
					B05 ++;
				if(actual.getInfraccion().startsWith("B06"))
					B06 ++;
				if(actual.getInfraccion().startsWith("B07"))
					B07 ++;
				if(actual.getInfraccion().startsWith("B08"))
					B08 ++;
				if(actual.getInfraccion().startsWith("B09"))
					B09 ++;
				if(actual.getInfraccion().startsWith("B10"))
					B10 ++;
				if(actual.getInfraccion().startsWith("B11"))
					B11 ++;
				if(actual.getInfraccion().startsWith("B12"))
					B12 ++;
				if(actual.getInfraccion().startsWith("B13"))
					B13 ++;
				if(actual.getInfraccion().startsWith("B14"))
					B14 ++;
				if(actual.getInfraccion().startsWith("B15"))
					B15 ++;
				if(actual.getInfraccion().startsWith("B16"))
					B16 ++;
				if(actual.getInfraccion().startsWith("B17"))
					B17 ++;
				if(actual.getInfraccion().startsWith("B18"))
					B18 ++;
				if(actual.getInfraccion().startsWith("B19"))
					B19 ++;
				if(actual.getInfraccion().startsWith("B20"))
					B20 ++;
				if(actual.getInfraccion().startsWith("B21"))
					B21 ++;
				if(actual.getInfraccion().startsWith("B22"))
					B22 ++;
				if(actual.getInfraccion().startsWith("B23"))
					B23 ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01"))
					C01 ++;
				if(actual.getInfraccion().startsWith("C02"))
					C02 ++;
				if(actual.getInfraccion().startsWith("C03"))
					C03 ++;
				if(actual.getInfraccion().startsWith("C04"))
					C04 ++;
				if(actual.getInfraccion().startsWith("C05"))
					C05 ++;
				if(actual.getInfraccion().startsWith("C06"))
					C06 ++;
				if(actual.getInfraccion().startsWith("C07"))
					C07 ++;
				if(actual.getInfraccion().startsWith("C08"))
					C08 ++;
				if(actual.getInfraccion().startsWith("C09"))
					C09 ++;
				if(actual.getInfraccion().startsWith("C10"))
					C10 ++;
				if(actual.getInfraccion().startsWith("C11"))
					C11 ++;
				if(actual.getInfraccion().startsWith("C12"))
					C12 ++;
				if(actual.getInfraccion().startsWith("C13"))
					C13 ++;
				if(actual.getInfraccion().startsWith("C14"))
					C14 ++;
				if(actual.getInfraccion().startsWith("C15"))
					C15 ++;
				if(actual.getInfraccion().startsWith("C16"))
					C16 ++;
				if(actual.getInfraccion().startsWith("C17"))
					C17 ++;
				if(actual.getInfraccion().startsWith("C18"))
					C18 ++;
				if(actual.getInfraccion().startsWith("C19"))
					C19 ++;
				if(actual.getInfraccion().startsWith("C20"))
					C20 ++;
				if(actual.getInfraccion().startsWith("C21"))
					C21 ++;
				if(actual.getInfraccion().startsWith("C22"))
					C22 ++;
				if(actual.getInfraccion().startsWith("C23"))
					C23 ++;
				if(actual.getInfraccion().startsWith("C24"))
					C24 ++;
				if(actual.getInfraccion().startsWith("C25"))
					C25 ++;
				if(actual.getInfraccion().startsWith("C26"))
					C26 ++;
				if(actual.getInfraccion().startsWith("C27"))
					C27 ++;
				if(actual.getInfraccion().startsWith("C28"))
					C28 ++;
				if(actual.getInfraccion().startsWith("C29"))
					C29 ++;
				if(actual.getInfraccion().startsWith("C30"))
					C30 ++;
				if(actual.getInfraccion().startsWith("C31"))
					C31 ++;
				if(actual.getInfraccion().startsWith("C32"))
					C32 ++;
				if(actual.getInfraccion().startsWith("C33"))
					C33 ++;
				if(actual.getInfraccion().startsWith("C34"))
					C34 ++;
				if(actual.getInfraccion().startsWith("C35"))
					C35 ++;
				if(actual.getInfraccion().startsWith("C36"))
					C36 ++;
				if(actual.getInfraccion().startsWith("C37"))
					C37 ++;
				if(actual.getInfraccion().startsWith("C38"))
					C38 ++;
				if(actual.getInfraccion().startsWith("C39"))
					C39 ++;
				if(actual.getInfraccion().startsWith("C40"))
					C40 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01"))
					D01 ++;
				if(actual.getInfraccion().startsWith("D02"))
					D02 ++;
				if(actual.getInfraccion().startsWith("D03"))
					D03 ++;
				if(actual.getInfraccion().startsWith("D04"))
					D04 ++;
				if(actual.getInfraccion().startsWith("D05"))
					D05 ++;
				if(actual.getInfraccion().startsWith("D06"))
					D06 ++;
				if(actual.getInfraccion().startsWith("D07"))
					D07 ++;
				if(actual.getInfraccion().startsWith("D08"))
					D08 ++;
				if(actual.getInfraccion().startsWith("D09"))
					D09 ++;
				if(actual.getInfraccion().startsWith("D10"))
					D10 ++;
				if(actual.getInfraccion().startsWith("D11"))
					D11 ++;
				if(actual.getInfraccion().startsWith("D12"))
					D12 ++;
				if(actual.getInfraccion().startsWith("D13"))
					D13 ++;
				if(actual.getInfraccion().startsWith("D14"))
					D14 ++;
				if(actual.getInfraccion().startsWith("D15"))
					D15 ++;
				if(actual.getInfraccion().startsWith("D16"))
					D16 ++;
				if(actual.getInfraccion().startsWith("D17"))
					D17 ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E01"))
					E01 ++;
				if(actual.getInfraccion().startsWith("E02"))
					E02 ++;
				if(actual.getInfraccion().startsWith("E03"))
					E03 ++;
				if(actual.getInfraccion().startsWith("E04"))
					E04 ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01"))
					F01 ++;
				if(actual.getInfraccion().startsWith("F02"))
					F02 ++;
				if(actual.getInfraccion().startsWith("F03"))
					F03 ++;
				if(actual.getInfraccion().startsWith("F04"))
					F04 ++;
				if(actual.getInfraccion().startsWith("F05"))
					F05 ++;
				if(actual.getInfraccion().startsWith("F06"))
					F06 ++;
				if(actual.getInfraccion().startsWith("F07"))
					F07 ++;
				if(actual.getInfraccion().startsWith("F08"))
					F08 ++;
				if(actual.getInfraccion().startsWith("F09"))
					F09 ++;
				if(actual.getInfraccion().startsWith("F10"))
					F10 ++;
				if(actual.getInfraccion().startsWith("F11"))
					F11 ++;
				if(actual.getInfraccion().startsWith("F12"))
					F12 ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01"))
					G01 ++;
				if(actual.getInfraccion().startsWith("G02"))
					G02 ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01"))
					H01 ++;
				if(actual.getInfraccion().startsWith("H02"))
					H02 ++;
				if(actual.getInfraccion().startsWith("H03"))
					H03 ++;
				if(actual.getInfraccion().startsWith("H04"))
					H04 ++;
				if(actual.getInfraccion().startsWith("H05"))
					H05 ++;
				if(actual.getInfraccion().startsWith("H06"))
					H06 ++;
				if(actual.getInfraccion().startsWith("H07"))
					H07 ++;
				if(actual.getInfraccion().startsWith("H08"))
					H08 ++;
				if(actual.getInfraccion().startsWith("H09"))
					H09 ++;
				if(actual.getInfraccion().startsWith("H10"))
					H10 ++;
				if(actual.getInfraccion().startsWith("H11"))
					H11 ++;
				if(actual.getInfraccion().startsWith("H12"))
					H12 ++;
				if(actual.getInfraccion().startsWith("H13"))
					H13 ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01"))
					I01 ++;
				if(actual.getInfraccion().startsWith("I02"))
					I02 ++;
				
				// Infracciones J

				if(actual.getInfraccion().startsWith("J01"))
					J01 ++;
				if(actual.getInfraccion().startsWith("J02"))
					J02 ++;
				if(actual.getInfraccion().startsWith("I01"))
					J03 ++;
				if(actual.getInfraccion().startsWith("I02"))
					J04 ++;
			}
		}

		infraciones[0] = "          | " + A01;infraciones[1] = "          | " + A02;infraciones[2] = "          | " + A03;infraciones[3] = "          | " + A04;infraciones[4] = "          | " + A05;
		infraciones[5] = "          | " + A06;infraciones[6] = "          | " + A07;infraciones[7] = "          | " + A08;infraciones[8] = "           | " + A09;infraciones[9] = "          | " + A10;
		infraciones[10] = "          | " + A11;infraciones[11] = "          | " + A12;infraciones[12] = "          | " + B01;infraciones[12] = "          | " + B02;infraciones[13] = "          | " + B03;
		infraciones[14] = "          | " + B04;infraciones[15] = "          | " + B05;infraciones[16] = "          | " + B06;infraciones[17] = "          | " + B07;infraciones[18] = "          | " + B08;
		infraciones[19] = "          | " + B09;infraciones[20] = "          | " + B10;infraciones[21] = "          | " + B11;infraciones[22] = "          | " + B12;infraciones[23] = "          | " + B13;
		infraciones[24] = "          | " + B14;infraciones[25] = "          | " + B15;infraciones[26] = "          | " + B16;infraciones[27] = "          | " + B17;infraciones[28] = "          | " + B18;
		infraciones[29] = "          | " + B19;infraciones[30] = "          | " + B20;infraciones[31] = "          | " + B21;infraciones[32] = "          | " + B22;infraciones[33] = "          | " + B23;
		infraciones[34] = "          | " + C01;infraciones[35] = "          | " + C02;infraciones[36] = "          | " + C03;infraciones[37] = "          | " + C04;infraciones[37] = "          | " + C05;
		infraciones[38] = "          | " + C06;infraciones[39] = "          | " + C07;infraciones[40] = "          | " + C08;infraciones[41] = "          | " + C09;infraciones[42] = "          | " + C10;
		infraciones[43] = "          | " + C11;infraciones[44] = "          | " + C12;infraciones[45] = "          | " + C13;infraciones[46] = "          | " + C14;infraciones[47] = "          | " + C15;
		infraciones[48] = "          | " + C16;infraciones[49] = "          | " + C17;infraciones[50] = "          | " + C18;infraciones[51] = "          | " + C19;infraciones[52] = "          | " + C20;
		infraciones[53] = "          | " + C21;infraciones[54] = "          | " + C22;infraciones[55] = "          | " + C23;infraciones[56] = "          | " + C24;infraciones[57] = "          | " + C25;
		infraciones[58] = "          | " + C26;infraciones[59] = "          | " + C27;infraciones[60] = "          | " + C28;infraciones[61] = "          | " + C29;infraciones[62] = "          | " + C30;
		infraciones[63] = "          | " + C31;infraciones[64] = "          | " + C32;infraciones[65] = "          | " + C33;infraciones[66] = "          | " + C34;infraciones[67] = "          | " + C35;
		infraciones[68] = "          | " + C36;infraciones[69] = "          | " + C37;infraciones[70] = "          | " + C38;infraciones[71] = "          | " + C39;infraciones[72] = "          | " + C40;
		infraciones[73] = "          | " + D01;infraciones[74] = "          | " + D02;infraciones[75] = "          | " + D03;infraciones[76] = "          | " + D04;infraciones[77] = "          | " + D05;
		infraciones[78] = "          | " + D06;infraciones[79] = "          | " + D07;infraciones[80] = "          | " + D08;infraciones[81] = "          | " + D09;infraciones[81] = "          | " + D10;
		infraciones[82] = "          | " + D11;infraciones[83] = "          | " + D12;infraciones[84] = "          | " + D13;infraciones[85] = "          | " + D14;infraciones[86] = "          | " + D15;
		infraciones[87] = "          | " + D16;infraciones[88] = "          | " + D17;infraciones[89] = "          | " + E01;infraciones[90] = "          | " + E02;infraciones[91] = "          | " + E03;
		infraciones[92] = "          | " + E04;infraciones[93] = "          | " + F01;infraciones[94] = "          | " + F02;infraciones[95] = "          | " + F03;infraciones[96] = "          | " + F04;
		infraciones[97] = "          | " + F05;infraciones[98] = "          | " + F06;infraciones[99] = "          | " + F07;infraciones[100] = "          | " + F08;infraciones[101] = "          | " + F09;
		infraciones[102] = "          | " + F10;infraciones[103] = "          | " + F11;infraciones[104] = "          | " + F12;infraciones[105] = "          | " + G01;infraciones[106] = "          | " + G02;
		infraciones[107] = "          | " + H01;infraciones[108] = "          | " + H02;infraciones[109] = "          | " + H03;infraciones[110] = "          | " + H04;infraciones[111] = "          | " + H05;
		infraciones[112] = "          | " + H06;infraciones[113] = "          | " + H07;infraciones[114] = "          | " + H08;infraciones[115] = "          | " + H09;infraciones[116] = "          | " + H10;
		infraciones[117] = "          | " + H11;infraciones[118] = "          | " + H12;infraciones[119] = "          | " + H13;infraciones[120] = "          | " + I01;infraciones[121] = "          | " + I02;
		infraciones[122] = "          | " + J01;infraciones[123] = "          | " + J02;infraciones[124] = "          | " + J03;infraciones[125] = "          | " + J04;
	
		return infraciones;
	}
	
	public Comparable<Comparendo>[] darComparendos1C(String pLocalidad, String pFecha1, String pFecha2)
	{
		@SuppressWarnings("unchecked")
		Comparable<Comparendo>[] nuevo = (Comparable<Comparendo>[]) new Comparable[datos1.getSize()];

		int j = 0;
		Iterator<Comparendo> it = datos1.iterator();
		while(it.hasNext())
		{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				if(elementoActual.getLocalidad().equals(pLocalidad) ) 
				{
					if(elementoActual.getFecha_hora().compareTo(pFecha1) < 0 && elementoActual.getFecha_hora().compareTo(pFecha2) < 0)
					{
						nuevo[j] = new Comparendo(elementoActual.getObjective(), elementoActual.getFecha_hora(), elementoActual.getMedio_dete(), elementoActual.getClase_vehi(), elementoActual.getTipo_servi(), elementoActual.getInfraccion(), elementoActual.getDes_infrac(), elementoActual.getLocalidad(), elementoActual.getCordenadas()[0], elementoActual.getCordenadas()[1]);
						j++;
					}
				}
			}
		}
        return nuevo;
	}

	// Metodos de Ordenamientos - MergeSort

	/**
	 * Ordena los elmenentos de acuerdo con el criterio de comparacion
	 * @param a Arreglo de comparendos
	 * @param comp Comparador por el cual se va a organizar
	 */
	public static <E> void sort(E[] a, Comparator<? super E> comp) 
	{
		mergeSort(a, 0, a.length - 1, comp);
	}

	/**
	 * Hace las fusiones de los sub arreglos ordenados para crear un arreglo totalmente ordenado
	 * @param a Arreglo de comparendos
	 * @param from Posicion inical del arreglo
	 * @param to Posicion final del arreglo
	 * @param comp Comparador por el cual se va a organizar
	 */
	private static <E> void mergeSort(E[] a, int from, int to, Comparator<? super E> comp) 
	{
		if (from == to)
			return;
		int mid = (from + to) / 2;
		// Sort the first and the second half
		mergeSort(a, from, mid, comp);
		mergeSort(a, mid + 1, to, comp);
		merge(a, from, mid, to, comp);
	}

	/**
	 * Fusion de los arreglos para ser utilizados por mergeSort
	 * @param a Arreglo de comparendos
	 * @param from Posicion inical del arreglo
	 * @param mid Posicion de la mitad del arreglo
	 * @param to Posicion final del arreglo
	 * @param comp Comparador por el cual se va a organizar
	 */
	@SuppressWarnings("unchecked")
	private static <E> void merge(E[] a, int from, int mid, int to, Comparator<? super E> comp) 
	{
		int n = to - from + 1;
		Object[] values = new Object[n];

		int fromValue = from;

		int middleValue = mid + 1;

		int index = 0;

		while (fromValue <= mid && middleValue <= to) {
			if (comp.compare(a[fromValue], a[middleValue]) < 0) {
				values[index] = a[fromValue];
				fromValue++;
			} else {
				values[index] = a[middleValue];
				middleValue++;
			}
			index++;
		}

		while (fromValue <= mid) {
			values[index] = a[fromValue];
			fromValue++;
			index++;
		}
		while (middleValue <= to) {
			values[index] = a[middleValue];
			middleValue++;
			index++;
		}

		for (index = 0; index < n; index++)
			a[from + index] = (E) values[index];
	}
	
	
	/**
	 * Retorna una gráfica ASCII (Histograma) que muestre el número total de comparendos por cada LOCALIDAD representados por un String de caracteres ‘*’
	 * @return Arreglo de Strings con la información a devolver en cada renglón del histograma
	 */
	public String[] darNComparendos(int n, String fecha1, String fecha2)
	{

		Iterator<Comparendo> it = datos1.iterator();
		ArrayList <Integer> respuestas = new ArrayList <Integer>();
		int nums[] = new int[127];

		for(int i = 0; i < datos1.getSize() ; i++)
		{
			Comparendo actual = it.next();
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[0] ++;
				if(actual.getInfraccion().startsWith("A02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[1] ++;
				if(actual.getInfraccion().startsWith("A03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[2] ++;
				if(actual.getInfraccion().startsWith("A04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[3] ++;
				if(actual.getInfraccion().startsWith("A05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[4] ++;
				if(actual.getInfraccion().startsWith("A06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[5] ++;
				if(actual.getInfraccion().startsWith("A07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[6] ++;
				if(actual.getInfraccion().startsWith("A08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[7] ++;
				if(actual.getInfraccion().startsWith("A09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[8] ++;
				if(actual.getInfraccion().startsWith("A10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[9] ++;
				if(actual.getInfraccion().startsWith("A11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[10] ++;
				if(actual.getInfraccion().startsWith("A12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[11] ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[12] ++;
				if(actual.getInfraccion().startsWith("B02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[13] ++;
				if(actual.getInfraccion().startsWith("B03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[14] ++;
				if(actual.getInfraccion().startsWith("B04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[15] ++;
				if(actual.getInfraccion().startsWith("B05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[16] ++;
				if(actual.getInfraccion().startsWith("B06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[17] ++;
				if(actual.getInfraccion().startsWith("B07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[18]++;
				if(actual.getInfraccion().startsWith("B08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 ) 
					nums[19]++;
				if(actual.getInfraccion().startsWith("B09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[20] ++;
				if(actual.getInfraccion().startsWith("B10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[21] ++;
				if(actual.getInfraccion().startsWith("B11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[22] ++;
				if(actual.getInfraccion().startsWith("B12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[23] ++;
				if(actual.getInfraccion().startsWith("B13") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[24] ++;
				if(actual.getInfraccion().startsWith("B14") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[25] ++;
				if(actual.getInfraccion().startsWith("B15") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[26] ++;
				if(actual.getInfraccion().startsWith("B16") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[27] ++;
				if(actual.getInfraccion().startsWith("B17") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[28] ++;
				if(actual.getInfraccion().startsWith("B18") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[29] ++;
				if(actual.getInfraccion().startsWith("B19") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[30] ++;
				if(actual.getInfraccion().startsWith("B20") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[31] ++;
				if(actual.getInfraccion().startsWith("B21") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[32] ++;
				if(actual.getInfraccion().startsWith("B22") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[33] ++;
				if(actual.getInfraccion().startsWith("B23") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[34] ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[35] ++;
				if(actual.getInfraccion().startsWith("C02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[36] ++;
				if(actual.getInfraccion().startsWith("C03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[37] ++;
				if(actual.getInfraccion().startsWith("C04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[38] ++;
				if(actual.getInfraccion().startsWith("C05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[39] ++;
				if(actual.getInfraccion().startsWith("C06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[40] ++;
				if(actual.getInfraccion().startsWith("C07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[41] ++;
				if(actual.getInfraccion().startsWith("C08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[42] ++;
				if(actual.getInfraccion().startsWith("C09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[43] ++;
				if(actual.getInfraccion().startsWith("C10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[44] ++;
				if(actual.getInfraccion().startsWith("C11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[45] ++;
				if(actual.getInfraccion().startsWith("C12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[46] ++;
				if(actual.getInfraccion().startsWith("C13") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[47] ++;
				if(actual.getInfraccion().startsWith("C14") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[48] ++;
				if(actual.getInfraccion().startsWith("C15") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[49] ++;
				if(actual.getInfraccion().startsWith("C16") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[50] ++;
				if(actual.getInfraccion().startsWith("C17") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[51] ++;
				if(actual.getInfraccion().startsWith("C18") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[52] ++;
				if(actual.getInfraccion().startsWith("C19") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[53] ++;
				if(actual.getInfraccion().startsWith("C20") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[54] ++;
				if(actual.getInfraccion().startsWith("C21") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[55] ++;
				if(actual.getInfraccion().startsWith("C22") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[56] ++;
				if(actual.getInfraccion().startsWith("C23") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[57] ++;
				if(actual.getInfraccion().startsWith("C24") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[58] ++;
				if(actual.getInfraccion().startsWith("C25") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[59] ++;
				if(actual.getInfraccion().startsWith("C26") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[60] ++;
				if(actual.getInfraccion().startsWith("C27") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[61] ++;
				if(actual.getInfraccion().startsWith("C28") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[62] ++;
				if(actual.getInfraccion().startsWith("C29") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[63] ++;
				if(actual.getInfraccion().startsWith("C30") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[64] ++;
				if(actual.getInfraccion().startsWith("C31") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[65] ++;
				if(actual.getInfraccion().startsWith("C32") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[66] ++;
				if(actual.getInfraccion().startsWith("C33") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[67] ++;
				if(actual.getInfraccion().startsWith("C34") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[68] ++;
				if(actual.getInfraccion().startsWith("C35") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[69] ++;
				if(actual.getInfraccion().startsWith("C36") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[70] ++;
				if(actual.getInfraccion().startsWith("C37") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[71] ++;
				if(actual.getInfraccion().startsWith("C38") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[72] ++;
				if(actual.getInfraccion().startsWith("C39") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[73] ++;
				if(actual.getInfraccion().startsWith("C40") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[74] ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[75] ++;
				if(actual.getInfraccion().startsWith("D02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[76] ++;
				if(actual.getInfraccion().startsWith("D03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[77] ++;
				if(actual.getInfraccion().startsWith("D04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[78] ++;
				if(actual.getInfraccion().startsWith("D05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[79] ++;
				if(actual.getInfraccion().startsWith("D06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[80] ++;
				if(actual.getInfraccion().startsWith("D07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[81] ++;
				if(actual.getInfraccion().startsWith("D08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[82] ++;
				if(actual.getInfraccion().startsWith("D09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[83] ++;
				if(actual.getInfraccion().startsWith("D10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )     
					nums[84] ++;
				if(actual.getInfraccion().startsWith("D11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[85] ++;
				if(actual.getInfraccion().startsWith("D12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[86] ++;
				if(actual.getInfraccion().startsWith("D13") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[87] ++;
				if(actual.getInfraccion().startsWith("D14") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[88] ++;
				if(actual.getInfraccion().startsWith("D15") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[89] ++;
				if(actual.getInfraccion().startsWith("D16") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[90] ++;
				if(actual.getInfraccion().startsWith("D17") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[91] ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[92] ++;
				if(actual.getInfraccion().startsWith("E04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[93] ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[94] ++;
				if(actual.getInfraccion().startsWith("F02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[95] ++;
				if(actual.getInfraccion().startsWith("F03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[96] ++;
				if(actual.getInfraccion().startsWith("F04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[97] ++;
				if(actual.getInfraccion().startsWith("F05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[98] ++;
				if(actual.getInfraccion().startsWith("F06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[99] ++;
				if(actual.getInfraccion().startsWith("F07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[100] ++;
				if(actual.getInfraccion().startsWith("F08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[101] ++;
				if(actual.getInfraccion().startsWith("F09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[102] ++;
				if(actual.getInfraccion().startsWith("F10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )    
					nums[103] ++;
				if(actual.getInfraccion().startsWith("F11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[104] ++;
				if(actual.getInfraccion().startsWith("F12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[105] ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[106] ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[107] ++;
				if(actual.getInfraccion().startsWith("H02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[108] ++;
				if(actual.getInfraccion().startsWith("H03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[109] ++;
				if(actual.getInfraccion().startsWith("H04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[110] ++;
				if(actual.getInfraccion().startsWith("H05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[111] ++;
				if(actual.getInfraccion().startsWith("H06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[112] ++;
				if(actual.getInfraccion().startsWith("H07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[113] ++;
				if(actual.getInfraccion().startsWith("H08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[114] ++;
				if(actual.getInfraccion().startsWith("H09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[115] ++;
				if(actual.getInfraccion().startsWith("H10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[116] ++;
				if(actual.getInfraccion().startsWith("H11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[117] ++;
				if(actual.getInfraccion().startsWith("H12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[118] ++;
				if(actual.getInfraccion().startsWith("H13") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[119] ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[120] ++;
				if(actual.getInfraccion().startsWith("I02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 )
					nums[121] ++;
			}
		}
			
		
		for (int i = 0; i < n; i++)
		{
			respuestas.add(0);
		}
		
		for(int i = 0; i < 121; i++)
		{
			for (int j = 0; j < n; j++)
			{
				if (nums[i] > respuestas.get(j))
				{
					respuestas.add(j, nums[i]);
					break;
				}
			}
		}
		
	
		//System.out.println(nums[66]);

		String[] total = new String[n];
		//System.out.println("n=" + n);
		
		//System.out.println(nums[48]);

		for (int i = 0; i < n; i++)
		{
			//System.out.println(respuestas.get(i) + " , " + nums[48]);
			
			if (respuestas.get(i) == nums[0])
			{
				total[i] = "A01" + respuestas.get(i);
				nums[0] = -1;
			}
			
			else if (respuestas.get(i) == nums[1])
			{
				total[i] = "A02" + respuestas.get(i);
				nums[1] = -1;
			}
			
			else if (respuestas.get(i) == nums[2])
			{
				total[i] = "A03" + respuestas.get(i);
				nums[2] = -1;
			}
			
			else if (respuestas.get(i) == nums[3])
			{
				total[i] = "A04" + respuestas.get(i);
				nums[3] = -1;
			}
			
			else if (respuestas.get(i) == nums[4])
			{
				total[i] = "A05" + respuestas.get(i);
				nums[4] = -1;
			}
			
			else if (respuestas.get(i) == nums[5])
			{
				total[i] = "A06" + respuestas.get(i);
				nums[5] = -1;
			}
			
			else if (respuestas.get(i) == nums[6])
			{
				total[i] = "A07" + respuestas.get(i);
				nums[6] = -1;
			}
			
			else if (respuestas.get(i) == nums[7])
			{
				total[i] = "A08" + respuestas.get(i);
				nums[7] = -1;
			}
			
			else if (respuestas.get(i) == nums[8])
			{
				total[i] = "A09" + respuestas.get(i);
				nums[8] = -1;
			}
			
			else if (respuestas.get(i) == nums[9])
			{
				total[i] = "A10" + respuestas.get(i);
				nums[9] = -1;
			}
			
			else if (respuestas.get(i) == nums[10])
			{
				total[i] = "A11" + respuestas.get(i);
				nums[10] = -1;
			}
			
			else if (respuestas.get(i) == nums[11])
			{
				total[i] = "A12" + respuestas.get(i);
				nums[11] = -1;
			}
			
			else if (respuestas.get(i) == nums[12])
			{
				total[i] = "B01" + respuestas.get(i);
				nums[12] = -1;
			}
			
			else if (respuestas.get(i) == nums[13])
			{
				total[i] = "B02" + respuestas.get(i);
				nums[13] = -1;
			}
			
			else if (respuestas.get(i) == nums[14])
			{
				total[i] = "B03" + respuestas.get(i);
				nums[14] = -1;
			}
			
			else if (respuestas.get(i) == nums[15])
			{
				total[i] = "B04" + respuestas.get(i);
				nums[15] = -1;
			}
			
			else if (respuestas.get(i) == nums[16])
			{
				total[i] = "B05" + respuestas.get(i);
				nums[16] = -1;
			}
			
			else if (respuestas.get(i) == nums[17])
			{
				total[i] = "B06" + respuestas.get(i);
				nums[17] = -1;
			}
			
			else if (respuestas.get(i) == nums[18])
			{
				total[i] = "B07" + respuestas.get(i);
				nums[18] = -1;
			}
			
			else if (respuestas.get(i) == nums[19])
			{
				total[i] = "B08" + respuestas.get(i);
				nums[19] = -1;
			}
			
			else if (respuestas.get(i) == nums[20])
			{
				total[i] = "B09" + respuestas.get(i);
				nums[20] = -1;
			}
			
			else if (respuestas.get(i) == nums[21])
			{
				total[i] = "B10" + respuestas.get(i);
				nums[21] = -1;
			}
			
			else if (respuestas.get(i) == nums[22])
			{
				total[i] = "B11" + respuestas.get(i);
				nums[22] = -1;
			}
			
			else if (respuestas.get(i) == nums[23])
			{
				total[i] = "B12" + respuestas.get(i);
				nums[23] = -1;
			}
			
			else if (respuestas.get(i) == nums[24])
			{
				total[i] = "B13" + respuestas.get(i);
				nums[24] = -1;
			}
			
			else if (respuestas.get(i) == nums[25])
			{
				total[i] = "B14" + respuestas.get(i);
				nums[25] = -1;
			}
			
			else if (respuestas.get(i) == nums[26])
			{
				total[i] = "B15" + respuestas.get(i);
				nums[26] = -1;
			}
			
			else if (respuestas.get(i) == nums[27])
			{
				total[i] = "B16" + respuestas.get(i);
				nums[27] = -1;
			}
			
			else if (respuestas.get(i) == nums[28])
			{
				total[i] = "B17" + respuestas.get(i);
				nums[28] = -1;
			}
			
			else if (respuestas.get(i) == nums[29])
			{
				total[i] = "B18" + respuestas.get(i);
				nums[29] = -1;
			}
			
			else if (respuestas.get(i) == nums[30])
			{
				total[i] = "B19" + respuestas.get(i);
				nums[30] = -1;
			}
			
			else if (respuestas.get(i) == nums[31])
			{
				total[i] = "B20" + respuestas.get(i);
				nums[31] = -1;
			}
			
			else if (respuestas.get(i) == nums[32])
			{
				total[i] = "B21" + respuestas.get(i);
				nums[32] = -1;
			}
			
			else if (respuestas.get(i) == nums[33])
			{
				total[i] = "B22" + respuestas.get(i);
				nums[33] = -1;
			}
			
			else if (respuestas.get(i) == nums[34])
			{
				total[i] = "B23" + respuestas.get(i);
				nums[34] = -1;
			}
			
			else if (respuestas.get(i) == nums[35])
			{
				total[i] = "C01" + respuestas.get(i);
				nums[35] = -1;
			}
			
			else if (respuestas.get(i) == nums[36])
			{
				total[i] = "C02" + respuestas.get(i);
				nums[36] = -1;
			}
			
			else if (respuestas.get(i) == nums[37])
			{
				total[i] = "C03" + respuestas.get(i);
				nums[37] = -1;
			}
			
			else if (respuestas.get(i) == nums[38])
			{
				total[i] = "C04" + respuestas.get(i);
				nums[38] = -1;
			}
			
			else if (respuestas.get(i) == nums[39])
			{
				total[i] = "C05" + respuestas.get(i);
				nums[39] = -1;
			}
			
			else if (respuestas.get(i) == nums[40])
			{
				total[i] = "C06" + respuestas.get(i);
				nums[40] = -1;
			}
			
			else if (respuestas.get(i) == nums[41])
			{
				total[i] = "C07" + respuestas.get(i);
				nums[41] = -1;
			}
			
			else if (respuestas.get(i) == nums[42])
			{
				total[i] = "C08" + respuestas.get(i);
				nums[42] = -1;
			}
			
			else if (respuestas.get(i) == nums[43])
			{
				total[i] = "C09" + respuestas.get(i);
				nums[43] = -1;
			}
			
			else if (respuestas.get(i) == nums[44])
			{
				total[i] = "C10" + respuestas.get(i);
				nums[44] = -1;
			}
			
			else if (respuestas.get(i) == nums[45])
			{
				total[i] = "C11" + respuestas.get(i);
				nums[45] = -1;
			}
			
			else if (respuestas.get(i) == nums[46])
			{
				total[i] = "C12" + respuestas.get(i);
				nums[46] = -1;
			}
			
			else if (respuestas.get(i) == nums[47])
			{
				total[i] = "C13" + respuestas.get(i);
				nums[47] = -1;
			}
			
			else if (respuestas.get(i) == nums[48])
			{
				total[i] = "C14" + respuestas.get(i);
				nums[48] = -1;
			}
			
			else if (respuestas.get(i) == nums[49])
			{
				total[i] = "C15" + respuestas.get(i);
				nums[49] = -1;
			}
			
			else if (respuestas.get(i) == nums[50])
			{
				total[i] = "C16" + respuestas.get(i);
				nums[50] = -1;
			}
			
			else if (respuestas.get(i) == nums[51])
			{
				total[i] = "C17" + respuestas.get(i);
				nums[51] = -1;
			}
			
			else if (respuestas.get(i) == nums[52])
			{
				total[i] = "C18" + respuestas.get(i);
				nums[52] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[53])
			{
				total[i] = "C19" + respuestas.get(i);
				nums[53] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[54])
			{
				total[i] = "C20" + respuestas.get(i);
				nums[54] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[55])
			{
				total[i] = "C21" + respuestas.get(i);
				nums[55] = -1;
			}
			
			else if (respuestas.get(i) == nums[56])
			{
				total[i] = "C22" + respuestas.get(i);
				nums[56] = -1;
			}
			
			else if (respuestas.get(i) == nums[57])
			{
				total[i] = "C23" + respuestas.get(i);
				nums[57] = -1;
			}
			
			else if (respuestas.get(i) == nums[58])
			{
				total[i] = "C24" + respuestas.get(i);
				nums[58] = -1;
			}
			
			else if (respuestas.get(i) == nums[59])
			{
				total[i] = "C25" + respuestas.get(i);
				nums[59] = -1;
			}
			
			else if (respuestas.get(i) == nums[60])
			{
				total[i] = "C26" + respuestas.get(i);
				nums[60] = -1;
			}
			
			else if (respuestas.get(i) == nums[61])
			{
				total[i] = "C27" + respuestas.get(i);
				nums[61] = -1;
			}
			
			else if (respuestas.get(i) == nums[62])
			{
				total[i] = "C28" + respuestas.get(i);
				nums[62] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[63])
			{
				total[i] = "C29" + respuestas.get(i);
				nums[63] = -1;
			}
			
			else if (respuestas.get(i) == nums[64])
			{
				total[i] = "C30" + respuestas.get(i);
				nums[64] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[65])
			{
				total[i] = "C31" + respuestas.get(i);
				nums[65] = -1;
			}
			
			else if (respuestas.get(i) == nums[66])
			{
				total[i] = "C32" + respuestas.get(i);
				nums[66] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[67])
			{
				total[i] = "C33" + respuestas.get(i);
				nums[67] = -1;
			}
			
			else if (respuestas.get(i) == nums[68])
			{
				total[i] = "C34" + respuestas.get(i);
				nums[68] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[69])
			{
				total[i] = "C35" + respuestas.get(i);
				nums[69] = -1;
			}
			
			else if (respuestas.get(i) == nums[70])
			{
				total[i] = "C36" + respuestas.get(i);
				nums[70] = -1;
			}
			
			else if (respuestas.get(i) == nums[71])
			{
				total[i] = "C37" + respuestas.get(i);
				nums[71] = -1;
			}
			
			else if (respuestas.get(i) == nums[72])
			{
				total[i] = "C38" + respuestas.get(i);
				nums[72] = -1;
			}
			
			else if (respuestas.get(i) == nums[73])
			{
				total[i] = "C39" + respuestas.get(i);
				nums[73] = -1;
			}
			
			else if (respuestas.get(i) == nums[74])
			{
				total[i] = "C40" + respuestas.get(i);
				nums[74] = -1;
			}
		
			else if (respuestas.get(i) == nums[75])
			{
				total[i] = "D01" + respuestas.get(i);
				nums[75] = -1;
			}
			
			else if (respuestas.get(i) == nums[76])
			{
				total[i] = "D02" + respuestas.get(i);
				nums[76] = -1;
			}
			
			else if (respuestas.get(i) == nums[77])
			{
				total[i] = "D03" + respuestas.get(i);
				nums[77] = -1;
			}
			
			else if (respuestas.get(i) == nums[78])
			{
				total[i] = "D04" + respuestas.get(i);
				nums[78] = -1;
			}
			
			else if (respuestas.get(i) == nums[79])
			{
				total[i] = "D05" + respuestas.get(i);
				nums[79] = -1;
			}
			
			else if (respuestas.get(i) == nums[80])
			{
				total[i] = "D06" + respuestas.get(i);
				nums[80] = -1;
			}
			
			else if (respuestas.get(i) == nums[81])
			{
				total[i] = "D07" + respuestas.get(i);
				nums[81] = -1;
			}
			
			else if (respuestas.get(i) == nums[82])
			{
				total[i] = "D08" + respuestas.get(i);
				nums[82] = -1;
			}
			
			else if (respuestas.get(i) == nums[83])
			{
				total[i] = "D09" + respuestas.get(i);
				nums[83] = -1;
			}
			
			else if (respuestas.get(i) == nums[84])
			{
				total[i] = "D10" + respuestas.get(i);
				nums[84] = -1;
			}
			
			else if (respuestas.get(i) == nums[85])
			{
				total[i] = "D11" + respuestas.get(i);
				nums[85] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[86])
			{
				total[i] = "D12" + respuestas.get(i);
				nums[86] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[87])
			{
				total[i] = "D13" + respuestas.get(i);
				nums[87] = -1;
			}
			
			else if (respuestas.get(i) == nums[88])
			{
				total[i] = "D14" + respuestas.get(i);
				nums[88] = -1;
			}
			
			else if (respuestas.get(i) == nums[89])
			{
				total[i] = "D15" + respuestas.get(i);
				nums[89] = -1;
			}
			
			else if (respuestas.get(i) == nums[90])
			{
				total[i] = "D16" + respuestas.get(i);
				nums[90] = -1;
			}
			
			else if (respuestas.get(i) == nums[91])
			{
				total[i] = "D17" + respuestas.get(i);
				nums[91] = -1;
			}
			
			else if (respuestas.get(i) == nums[92])
			{
				total[i] = "E02" + respuestas.get(i);
				nums[92] = -1;
			}
			
			else 	if (respuestas.get(i) == nums[93])
			{
				total[i] = "E04" + respuestas.get(i);
				nums[93] = -1;
			}
			
			else if (respuestas.get(i) == nums[94])
			{
				total[i] = "F01" + respuestas.get(i);
				nums[94] = -1;
			}
			
			else if (respuestas.get(i) == nums[95])
			{
				total[i] = "F02" + respuestas.get(i);
				nums[95] = -1;
			}
	
			else if (respuestas.get(i) == nums[96])
			{
				total[i] = "F03" + respuestas.get(i);
				nums[96] = -1;
			}
			
			else if (respuestas.get(i)== nums[97])
			{
				total[i] = "F04" + respuestas.get(i);
				nums[97] = -1;
			}
			
			else if (respuestas.get(i) == nums[98])
			{
				total[i] = "F05" + respuestas.get(i);
				nums[98] = -1;
			}
			
			else if (respuestas.get(i) == nums[99])
			{
				total[i] = "F06" + respuestas.get(i);
				nums[99] = -1;
			}
			
			else if (respuestas.get(i) == nums[100])
			{
				total[i] = "F07" + respuestas.get(i);
				nums[100] = -1;
			}
			
			else if (respuestas.get(i) == nums[101])
			{
				total[i] = "F08" + respuestas.get(i);
				nums[101] = -1;
			}
			
			else if (respuestas.get(i) == nums[102])
			{
				total[i] = "F09" + respuestas.get(i);
				nums[102] = -1;
			}
			
			else if (respuestas.get(i)== nums[103])
			{
				total[i] = "F10" + respuestas.get(i);
				nums[103] = -1;
			}
			
			else if (respuestas.get(i) == nums[104])
			{
				total[i] = "F11" + respuestas.get(i);
				nums[104] = -1;
			}
			
			else if (respuestas.get(i) == nums[105])
			{
				total[i] = "F12" + respuestas.get(i);
				nums[105] = -1;
			}
			
			else if (respuestas.get(i) == nums[106])
			{
				total[i] = "G01" + respuestas.get(i);
				nums[106] = -1;
			}
			
			else if (respuestas.get(i) == nums[107])
			{
				total[i] = "H01" + respuestas.get(i);
				nums[107] = -1;
			}
			
			else if (respuestas.get(i) == nums[108])
			{
				total[i] = "H02" + respuestas.get(i);
				nums[108] = -1;
			}
			
			else if (respuestas.get(i) == nums[109])
			{
				total[i] = "H03" + respuestas.get(i);
				nums[109] = -1;
			}
			
			else if (respuestas.get(i) == nums[110])
			{
				total[i] = "H04" + respuestas.get(i);
				nums[110] = -1;
			}
			
			else if (respuestas.get(i) == nums[111])
			{
				total[i] = "H05" + respuestas.get(i);
				nums[111] = -1;
			}
			
			else if (respuestas.get(i) == nums[112])
			{
				total[i] = "H06" + respuestas.get(i);
				nums[112] = -1;
			}
			
			else if (respuestas.get(i)== nums[113])
			{
				total[i] = "H07" + respuestas.get(i);
				nums[113] = -1;
			}
			
			else if (respuestas.get(i) == nums[114])
			{
				total[i] = "H08" + respuestas.get(i);
				nums[114] = -1;
			}
			
			else if (respuestas.get(i) == nums[115])
			{
				total[i] = "H09" + respuestas.get(i);
				nums[115] = -1;
			}
			
			else if (respuestas.get(i) == nums[116])
			{
				total[i] = "H10" + respuestas.get(i);
				nums[116] = -1;
			}
			
			else if (respuestas.get(i) == nums[117])
			{
				total[i] = "H11" + respuestas.get(i);
				nums[117] = -1;
			}
			
			else if (respuestas.get(i) == nums[118])
			{
				total[i] = "H12" + respuestas.get(i);
				nums[118] = -1;
			}
			
			else if (respuestas.get(i) == nums[119])
			{
				total[i] = "H13" + respuestas.get(i);
				nums[119] = -1;
			}
			
			else if (respuestas.get(i) == nums[120])
			{
				total[i] = "I01" + respuestas.get(i);
				nums[120] = -1;
			}
			
			else if (respuestas.get(i) == nums[121])
			{
				total[i] = "I02" + respuestas.get(i);
				nums[121] = -1;
			}
		}
		
		return total;
	}

	
	
	
	/**
	 * Retorna una gráfica ASCII (Histograma) que muestre el número total de comparendos por cada LOCALIDAD representados por un String de caracteres ‘*’
	 * @return Arreglo de Strings con la información a devolver en cada renglón del histograma
	 */
	public String[] darHistograma()
	{
		int[] nums = contarInfracsXLocalidad();
		String[] respuestas = new String[21];
	
		
		for(int i = 0; i < 20; i++)
		{
			respuestas[i] = "";
			//System.out.println(nums[i] );

			while(nums[i] > 0)
			{
				respuestas[i] += "*";
				nums[i] -= 50;
			}
			
			 if (respuestas[i].equals("") )
				 respuestas[i] = "sin comparendos";

		}
		
		respuestas[20] = datos1.getSize() + "";
		
		return respuestas;
		
	}
	
	public int[] contarInfracsXLocalidad()
	{
		int[] nums = new int[21]; 

		Iterator<Comparendo> it = datos1.iterator();
		//while(it.hasNext())
		//{
			for(int i = 0; i < datos1.getSize(); i++)
			{
				Comparendo elementoActual = it.next();
				
				//System.out.println( elementoActual.getLocalidad() );
				
				if (elementoActual.getLocalidad().equals( "ANTONIO NARIÑO" ) )
					nums[0]++;
				
				if (elementoActual.getLocalidad().equals( "BARRIOS UNIDOS" ) )
					nums[1]++;		
				
				if (elementoActual.getLocalidad().equals( "BOSA" ) )
					nums[2]++;	
				
				if (elementoActual.getLocalidad().equals( "CHAPINERO" ) )
					nums[3]++;	
				
				if (elementoActual.getLocalidad().equals( "CIUDAD BOLIVAR" ) )
					nums[4]++;	
				
				if (elementoActual.getLocalidad().equals( "ENGATIVA" ) )
					nums[5]++;	
				
				if (elementoActual.getLocalidad().equals( "FONTIBON" ) )
					nums[6]++;	
				
				if (elementoActual.getLocalidad().equals( "KENNEDY" ) )
					nums[7]++;	
				
				if (elementoActual.getLocalidad().equals( "CANDELARIA" ) )
					nums[8]++;	
				
				if (elementoActual.getLocalidad().equals( "MARTIRES" ) )
					nums[9]++;	
				
				if (elementoActual.getLocalidad().equals( "PUENTE ARANDA" ) )
					nums[10]++;	
				
				if (elementoActual.getLocalidad().equals( "RAFAEL URIBE" ) )
					nums[11]++;	
				
				if (elementoActual.getLocalidad().equals( "SAN CRISTOBAL" ) )
					nums[12]++;	
				
				if (elementoActual.getLocalidad().equals( "SANTA FE" ) )
					nums[13]++;	
				
				if (elementoActual.getLocalidad().equals( "SUBA" ) )
					nums[14]++;	
				
				if (elementoActual.getLocalidad().equals( "SUMAPAZ" ) )
					nums[15]++;	
				
				if (elementoActual.getLocalidad().equals( "TEUSAQUILLO" ) )
					nums[16]++;	
				
				if (elementoActual.getLocalidad().equals( "TUNJUELITO" ) )
					nums[17]++;	
				
				if (elementoActual.getLocalidad().equals( "USAQUEN" ) )
					nums[18]++;	
				
				if (elementoActual.getLocalidad().equals( "USME" ) )
					nums[19]++;					
		}
			
		return nums;

	}
	
	
	public String[] darComparendosPorLocalidadYFecha(String localidad, String fecha1, String fecha2)
	{
		
		String[] respuestas = new String[122];

		if (! (localidad.equals("ANTONIO NARIÑO") || localidad.equals("BARRIOS UNIDOS") ||localidad.equals("BOSA") ||localidad.equals("CHAPINERO") ||localidad.equals("CIUDAD BOLIVAR") ||localidad.equals("ENGATIVA") ||localidad.equals("FONTIBON") ||localidad.equals("KENNEDY") ||localidad.equals("CANDELARIA") ||localidad.equals("MARTIRES") ||localidad.equals("PUENTE ARANDA") ||localidad.equals("RAFAEL URIBE") ||localidad.equals("SAN CRISTOBAL") ||localidad.equals("SANTA FE") ||localidad.equals("SUBA") ||localidad.equals("SUMAPAZ") ||localidad.equals("TEUSAQUILLO") ||localidad.equals("TUNJUELITO") ||localidad.equals("USAQUEN") ||localidad.equals("USME") )  )
			return respuestas;
			
		Iterator<Comparendo> it = datos1.iterator();
		int nums[] = new int[127];

		for(int i = 0; i < datos1.getSize() ; i++)
		{
			Comparendo actual = it.next();
			if(actual != null)
			{
				// Infracciones A

				if(actual.getInfraccion().startsWith("A01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[0] ++;
				if(actual.getInfraccion().startsWith("A02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[1] ++;
				if(actual.getInfraccion().startsWith("A03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[2] ++;
				if(actual.getInfraccion().startsWith("A04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[3] ++;
				if(actual.getInfraccion().startsWith("A05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[4] ++;
				if(actual.getInfraccion().startsWith("A06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[5] ++;
				if(actual.getInfraccion().startsWith("A07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[6] ++;
				if(actual.getInfraccion().startsWith("A08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[7] ++;
				if(actual.getInfraccion().startsWith("A09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[8] ++;
				if(actual.getInfraccion().startsWith("A10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[9] ++;
				if(actual.getInfraccion().startsWith("A11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[10] ++;
				if(actual.getInfraccion().startsWith("A12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[11] ++;

				// Infraciones B

				if(actual.getInfraccion().startsWith("B01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[12] ++;
				if(actual.getInfraccion().startsWith("B02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[13] ++;
				if(actual.getInfraccion().startsWith("B03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[14] ++;
				if(actual.getInfraccion().startsWith("B04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[15] ++;
				if(actual.getInfraccion().startsWith("B05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[16] ++;
				if(actual.getInfraccion().startsWith("B06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[17] ++;
				if(actual.getInfraccion().startsWith("B07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[18]++;
				if(actual.getInfraccion().startsWith("B08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) ) 
					nums[19]++;
				if(actual.getInfraccion().startsWith("B09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[20] ++;
				if(actual.getInfraccion().startsWith("B10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[21] ++;
				if(actual.getInfraccion().startsWith("B11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[22] ++;
				if(actual.getInfraccion().startsWith("B12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[23] ++;
				if(actual.getInfraccion().startsWith("B13") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[24] ++;
				if(actual.getInfraccion().startsWith("B14") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[25] ++;
				if(actual.getInfraccion().startsWith("B15") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[26] ++;
				if(actual.getInfraccion().startsWith("B16") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[27] ++;
				if(actual.getInfraccion().startsWith("B17") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[28] ++;
				if(actual.getInfraccion().startsWith("B18") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[29] ++;
				if(actual.getInfraccion().startsWith("B19") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[30] ++;
				if(actual.getInfraccion().startsWith("B20") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[31] ++;
				if(actual.getInfraccion().startsWith("B21") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[32] ++;
				if(actual.getInfraccion().startsWith("B22") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[33] ++;
				if(actual.getInfraccion().startsWith("B23") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[34] ++;

				// Infracciones C

				if(actual.getInfraccion().startsWith("C01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[35] ++;
				if(actual.getInfraccion().startsWith("C02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[36] ++;
				if(actual.getInfraccion().startsWith("C03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[37] ++;
				if(actual.getInfraccion().startsWith("C04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[38] ++;
				if(actual.getInfraccion().startsWith("C05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[39] ++;
				if(actual.getInfraccion().startsWith("C06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[40] ++;
				if(actual.getInfraccion().startsWith("C07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[41] ++;
				if(actual.getInfraccion().startsWith("C08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[42] ++;
				if(actual.getInfraccion().startsWith("C09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[43] ++;
				if(actual.getInfraccion().startsWith("C10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[44] ++;
				if(actual.getInfraccion().startsWith("C11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[45] ++;
				if(actual.getInfraccion().startsWith("C12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[46] ++;
				if(actual.getInfraccion().startsWith("C13") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[47] ++;
				if(actual.getInfraccion().startsWith("C14") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[48] ++;
				if(actual.getInfraccion().startsWith("C15") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[49] ++;
				if(actual.getInfraccion().startsWith("C16") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[50] ++;
				if(actual.getInfraccion().startsWith("C17") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[51] ++;
				if(actual.getInfraccion().startsWith("C18") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[52] ++;
				if(actual.getInfraccion().startsWith("C19") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[53] ++;
				if(actual.getInfraccion().startsWith("C20") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[54] ++;
				if(actual.getInfraccion().startsWith("C21") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[55] ++;
				if(actual.getInfraccion().startsWith("C22") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[56] ++;
				if(actual.getInfraccion().startsWith("C23") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[57] ++;
				if(actual.getInfraccion().startsWith("C24") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[58] ++;
				if(actual.getInfraccion().startsWith("C25") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[59] ++;
				if(actual.getInfraccion().startsWith("C26") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[60] ++;
				if(actual.getInfraccion().startsWith("C27") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[61] ++;
				if(actual.getInfraccion().startsWith("C28") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[62] ++;
				if(actual.getInfraccion().startsWith("C29") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[63] ++;
				if(actual.getInfraccion().startsWith("C30") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[64] ++;
				if(actual.getInfraccion().startsWith("C31") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[65] ++;
				if(actual.getInfraccion().startsWith("C32") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[66] ++;
				if(actual.getInfraccion().startsWith("C33") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[67] ++;
				if(actual.getInfraccion().startsWith("C34") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[68] ++;
				if(actual.getInfraccion().startsWith("C35") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[69] ++;
				if(actual.getInfraccion().startsWith("C36") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[70] ++;
				if(actual.getInfraccion().startsWith("C37") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[71] ++;
				if(actual.getInfraccion().startsWith("C38") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[72] ++;
				if(actual.getInfraccion().startsWith("C39") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[73] ++;
				if(actual.getInfraccion().startsWith("C40") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[74] ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("D01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[75] ++;
				if(actual.getInfraccion().startsWith("D02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[76] ++;
				if(actual.getInfraccion().startsWith("D03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[77] ++;
				if(actual.getInfraccion().startsWith("D04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[78] ++;
				if(actual.getInfraccion().startsWith("D05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[79] ++;
				if(actual.getInfraccion().startsWith("D06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[80] ++;
				if(actual.getInfraccion().startsWith("D07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[81] ++;
				if(actual.getInfraccion().startsWith("D08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[82] ++;
				if(actual.getInfraccion().startsWith("D09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[83] ++;
				if(actual.getInfraccion().startsWith("D10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )     
					nums[84] ++;
				if(actual.getInfraccion().startsWith("D11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[85] ++;
				if(actual.getInfraccion().startsWith("D12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[86] ++;
				if(actual.getInfraccion().startsWith("D13") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[87] ++;
				if(actual.getInfraccion().startsWith("D14") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[88] ++;
				if(actual.getInfraccion().startsWith("D15") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[89] ++;
				if(actual.getInfraccion().startsWith("D16") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[90] ++;
				if(actual.getInfraccion().startsWith("D17") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[91] ++;

				// Infraciones D

				if(actual.getInfraccion().startsWith("E02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[92] ++;
				if(actual.getInfraccion().startsWith("E04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[93] ++;

				// Infracciones F

				if(actual.getInfraccion().startsWith("F01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[94] ++;
				if(actual.getInfraccion().startsWith("F02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[95] ++;
				if(actual.getInfraccion().startsWith("F03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[96] ++;
				if(actual.getInfraccion().startsWith("F04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[97] ++;
				if(actual.getInfraccion().startsWith("F05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[98] ++;
				if(actual.getInfraccion().startsWith("F06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[99] ++;
				if(actual.getInfraccion().startsWith("F07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[100] ++;
				if(actual.getInfraccion().startsWith("F08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[101] ++;
				if(actual.getInfraccion().startsWith("F09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[102] ++;
				if(actual.getInfraccion().startsWith("F10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )    
					nums[103] ++;
				if(actual.getInfraccion().startsWith("F11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[104] ++;
				if(actual.getInfraccion().startsWith("F12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[105] ++;

				// Infraciones G

				if(actual.getInfraccion().startsWith("G01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[106] ++;

				// Infraciones H

				if(actual.getInfraccion().startsWith("H01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[107] ++;
				if(actual.getInfraccion().startsWith("H02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[108] ++;
				if(actual.getInfraccion().startsWith("H03") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[109] ++;
				if(actual.getInfraccion().startsWith("H04") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[110] ++;
				if(actual.getInfraccion().startsWith("H05") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[111] ++;
				if(actual.getInfraccion().startsWith("H06") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[112] ++;
				if(actual.getInfraccion().startsWith("H07") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[113] ++;
				if(actual.getInfraccion().startsWith("H08") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[114] ++;
				if(actual.getInfraccion().startsWith("H09") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[115] ++;
				if(actual.getInfraccion().startsWith("H10") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[116] ++;
				if(actual.getInfraccion().startsWith("H11") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[117] ++;
				if(actual.getInfraccion().startsWith("H12") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[118] ++;
				if(actual.getInfraccion().startsWith("H13") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[119] ++;

				// Infracciones I

				if(actual.getInfraccion().startsWith("I01") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[120] ++;
				if(actual.getInfraccion().startsWith("I02") && compararFechas(actual.getFecha_hora(), fecha1) >= 0 && compararFechas(actual.getFecha_hora(), fecha2) <= 0 && actual.getLocalidad().equals(localidad) )
					nums[121] ++;
			}
		}
		
		
		respuestas[0] = "A01" + nums[0];
		respuestas[1] = "A02" + nums[1];
		respuestas[2] = "A03" + nums[2];
		respuestas[3] = "A04" + nums[3];
		respuestas[4] = "A05" + nums[4];
		respuestas[5] = "A06" + nums[5];
		respuestas[6] = "A07" + nums[6];
		respuestas[7] = "A08" + nums[7];
		respuestas[8] = "A09" + nums[8];
		respuestas[9] = "A10" + nums[9];
		respuestas[10] = "A11" + nums[10];
		respuestas[11] = "A12" + nums[11];
		respuestas[12] = "B01" + nums[12];
		respuestas[13] = "B02" + nums[13];
		respuestas[14] = "B03" + nums[14];
		respuestas[15] = "B04" + nums[15];
		respuestas[16] = "B05" + nums[16];
		respuestas[17] = "B06" + nums[17];
		respuestas[18] = "B07" + nums[18];
		respuestas[19] = "B08" + nums[19];
		respuestas[20] = "B09" + nums[20];
		respuestas[21] = "B10" + nums[21];
		respuestas[22] = "B11" + nums[22];
		respuestas[23] = "B12" + nums[23];
		respuestas[24] = "B13" + nums[24];
		respuestas[25] = "B14" + nums[25];
		respuestas[26] = "B15" + nums[26];
		respuestas[27] = "B16" + nums[27];
		respuestas[28] = "B17" + nums[28];
		respuestas[29] = "B18" + nums[29];
		respuestas[30] = "B19" + nums[30];
		respuestas[31] = "B20" + nums[31];
		respuestas[32] = "B21" + nums[32];
		respuestas[33] = "B22" + nums[33];
		respuestas[34] = "B23" + nums[34];
		respuestas[35] = "C01" + nums[35];
		respuestas[36] = "C02" + nums[36];
		respuestas[37] = "C03" + nums[37];
		respuestas[38] = "C04" + nums[38];
		respuestas[39] = "C05" + nums[39];
		respuestas[40] = "C06" + nums[40];
		respuestas[41] = "C07" + nums[41];
		respuestas[42] = "C08" + nums[42];
		respuestas[43] = "C09" + nums[43];
		respuestas[44] = "C10" + nums[44];
		respuestas[45] = "C11" + nums[45];
		respuestas[46] = "C12" + nums[46];
		respuestas[47] = "C13" + nums[47];
		respuestas[48] = "C14" + nums[48];
		respuestas[49] = "C15" + nums[49];
		respuestas[50] = "C16" + nums[50];
		respuestas[51] = "C17" + nums[51];
		respuestas[52] = "C18" + nums[52];
		respuestas[53] = "C19" + nums[53];
		respuestas[54] = "C20" + nums[54];
		respuestas[55] = "C21" + nums[55];
		respuestas[56] = "C22" + nums[56];
		respuestas[57] = "C23" + nums[57];
		respuestas[58] = "C24" + nums[58];
		respuestas[59] = "C25" + nums[59];
		respuestas[60] = "C26" + nums[60];
		respuestas[61] = "C27" + nums[61];
		respuestas[62] = "C28" + nums[62];
		respuestas[63] = "C29" + nums[63];
		respuestas[64] = "C30" + nums[64];
		respuestas[65] = "C31" + nums[65];
		respuestas[66] = "C32" + nums[66];
		respuestas[67] = "C33" + nums[67];
		respuestas[68] = "C34" + nums[68];
		respuestas[69] = "C35" + nums[69];
		respuestas[70] = "C36" + nums[70];
		respuestas[71] = "C37" + nums[71];
		respuestas[72] = "C38" + nums[72];
		respuestas[73] = "C39" + nums[73];
		respuestas[74] = "C40" + nums[74];
		respuestas[75] = "D01" + nums[75];
		respuestas[76] = "D02" + nums[76];
		respuestas[77] = "D03" + nums[77];
		respuestas[78] = "D04" + nums[78];
		respuestas[79] = "D05" + nums[79];
		respuestas[80] = "D06" + nums[80];
		respuestas[81] = "D07" + nums[81];
		respuestas[82] = "D08" + nums[82];
		respuestas[83] = "D09" + nums[83];
		respuestas[84] = "D10" + nums[84];
		respuestas[85] = "D11" + nums[85];
		respuestas[86] = "D12" + nums[86];
		respuestas[87] = "D13" + nums[87];
		respuestas[88] = "D14" + nums[88];
		respuestas[89] = "D15" + nums[89];
		respuestas[90] = "D16" + nums[90];
		respuestas[91] = "D17" + nums[91];
		respuestas[92] = "E02" + nums[92];
		respuestas[93] = "E04" + nums[93];
		respuestas[94] = "F01" + nums[94];
		respuestas[95] = "F02" + nums[95];
		respuestas[96] = "F03" + nums[96];
		respuestas[97] = "F04" + nums[97];
		respuestas[98] = "F05" + nums[98];
		respuestas[99] = "F06" + nums[99];
		respuestas[100] = "F07" + nums[100];
		respuestas[101] = "F08" + nums[101];
		respuestas[102] = "F09" + nums[102];
		respuestas[103] = "F10" + nums[103];
		respuestas[104] = "F11" + nums[104];
		respuestas[105] = "F12" + nums[105];
		respuestas[106] = "G01" + nums[106];
		respuestas[107] = "H01" + nums[107];
		respuestas[108] = "H02" + nums[108];
		respuestas[109] = "H03" + nums[109];
		respuestas[110] = "H04" + nums[110];
		respuestas[111] = "H05" + nums[111];
		respuestas[112] = "H06" + nums[112];
		respuestas[113] = "H07" + nums[113];
		respuestas[114] = "H08" + nums[114];
		respuestas[115] = "H09" + nums[115];
		respuestas[116] = "H10" + nums[116];
		respuestas[117] = "H11" + nums[117];
		respuestas[118] = "H12" + nums[118];
		respuestas[119] = "H13" + nums[119];
		respuestas[120] = "I01" + nums[120];
		respuestas[121] = "I02" + nums[121];
		
		
		return respuestas;
		
	}
	
	public int compararFechas(String f1, String f2)
	{
		int año1 = Integer.parseInt( f1.substring(0, 4) );
		int año2 = Integer.parseInt( f2.substring(0, 4) );
		int mes1 = Integer.parseInt( f1.substring(5, 7) );
		int mes2 = Integer.parseInt( f2.substring(5, 7) );
		int dia1 = Integer.parseInt( f1.substring(8, 10) );
		int dia2 = Integer.parseInt( f2.substring(8, 10) );
		
		if (año1 > año2)
			return 1;
		
		if (año1 < año2)
			return -1;
		
		if (mes1 > mes2)
			return 1;
		
		if (mes1 < mes2)
			return -1;
		
		if (dia1 > dia2)
			return 1;
		
		if (dia1 < dia2)
			return -1;
		
		return 0;
		
	}
	
	
	/**
	 * Este metodo se encarga de ordenar bajo el criterio de shellSort
	 * Funcion principal: Shell sort es un algoritmo que primero clasifica los elementos muy separados entre sí y sucesivamente reduce el intervalo entre los elementos a clasificar.
	 * @param a Comparendo de tipo compareble bajo el criterio de comparcion por fecha_hora, objectid depende del caso
	 */
	public static void shellSort(Comparable<Comparendo>[] a) 
	{
		int n = a.length;

		int h = 1;
		while (h < n/3) h = 3*h + 1; 

		while (h >= 1) 
		{
			for (int i = h; i < n; i++) 
			{
				for (int j = i; j >= h && less(a[j], a[j-h]); j -= h) 
				{
					exch(a, j, j-h);
				}
			}
			h /= 3;
		}
	}

	/**
	 * Se encarga de determinar si el comparendo es menor
	 * @param v Comparendo1 de tipo coparable bajo el criterio de comparcion por fecha_hora, objectid depende del caso
	 * @param w Comparendo2 de tipo coparable bajo el criterio de comparcion por fecha_hora, objectid depende del caso
	 * @return True si es menor, false en el caso contrario
	 */
	private static boolean less(Comparable<Comparendo> v, Comparable<Comparendo> w) 
	{
		return v.compareTo((Comparendo) w) < 0;
	}

	/**
	 * Cambia el objeto de una posicion a otra
	 * @param a Objeto a intercambiar en el arreglo
	 * @param i Posicion en i
	 * @param j Posicion en j
	 */
	private static void exch(Object[] a, int i, int j) 
	{
		Object swap = a[i];
		a[i] = a[j];
		a[j] = swap;
	}
}
