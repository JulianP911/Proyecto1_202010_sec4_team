package view;

import model.logic.Modelo;

public class View 
{
	    /**
	     * Metodo constructor
	     */
	    public View()
	    {
	    	
	    }
	    
		public void printMenu()
		{
			System.out.println("1. Total de comparendos en el archivo");
			System.out.println("2. Mostrar la informacion del comparendo con el mayor OBJECTID encontrado");
			System.out.println("3. La zona Minimax de los comparendos definida como los límites inferior y superior de latitud y longitud en todo el archivo");
			System.out.println("4. Requerimiento 1A - Primer comparendo con la localidad dada");
			System.out.println("5. Requerimiento 1B - Primer comparendo con la infraccion dada");
   		    System.out.println("6. Requerimiento 1C - Mostrar el número de comparendos por cada código INFRACCION en una LOCALIDAD dada, para un periodo de tiempo dado por: FECHA_HORA inicial y FECHA_HORA final.");
			System.out.println("7. Requerimiento 2A - Consultar los comparendos registrados en el archivo dada una FECHA_HORA. Los resultados deben ser presentados de manera ordenada, para este caso de mayor a menor por el código INFRACCION.");
			System.out.println("8. Requerimiento 2B - Consultar los comparendos registrados en el archivo dado un código INFRACCION. Los resultados deben ser presentados de manera ordenada, para este caso de cronológicamente de menor a mayor por la FECHA_HORA.");
			System.out.println("9. Requerimiento 2C - Consultar la información de los N códigos INFRACCION con más infracciones ordenados de mayor a menor en un periodo de tiempo dado por: FECHA_HORA inicial y FECHA_HORA final");
			System.out.println("10. Requerimiento 3A - Comparar los comparendos, por cada código INFRACCION, en dos FECHA_HORA dadas; estas fechas deben ser ingresada en el formato Año/Mes/Día. La comparación solicitada consiste en mostrar el total de comparendos de cada código de INFRACCION para cada FECHA_HORA.");
			System.out.println("11. Requerimiento 3B - Comparar los comparendos por cada código INFRACCION en los TIPO_SERVI \"Particular\" y \"Público\". La comparación consiste en mostrar el total de comparendos de cada código de INFRACCION por cada TIPO_SERVI Particular y Público.");
			System.out.println("12. Requerimiento 3C - Generar una gráfica ASCII (Histograma) que muestre el número total de comparendos por cada LOCALIDAD representados por un String de caracteres ‘*’");
			System.out.println("13. Cerrar la aplicacion");
		}

		public void printMessage(String mensaje) {

			System.out.println(mensaje);
		}		
		
		public void printModelo(Modelo modelo)
		{
			System.out.println(modelo);
		}
}
