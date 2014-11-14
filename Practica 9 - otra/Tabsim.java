/**
*@author Gilberto Vargas Hernández
*Clase que gestiona las etiquetas y sus valores
*/

import java.io.*;
import java.util.*;
import java.util.regex.*;
public class Tabsim{
	//Se almacenan todas las etiquetas en un Map
	private Map<String,Integer> diccionario;
	//Constructor de la clase
	public Tabsim(){
		diccionario=new TreeMap<String,Integer>();
	}
	/**
	 *Funcion que agrega una etiqueta con su valor.
	 *@param String etiqueta, Integer contLoc, Una etiqueta y su valor. 
	 *@return true si la etiqueta no habia sido definida previamente
	 */
	public boolean agregar(String etiqueta, Integer contLoc){
		//Verfica si existe la etiqueta. 
		if(diccionario.get(etiqueta)==null){
			diccionario.put(etiqueta,contLoc);
			return true;
		}else{
			return false;
		}
	}

	/**
	 *Funcion que escribe todas las etiquetas con su valor en un archivo.
	 *@param String dir, la direccion del archivo.  
	 */
	public void guardar(String dir){
		try{
			//Abre el archivo 
			BufferedWriter arch=new BufferedWriter(new FileWriter((new File(dir+".tabsim")).getAbsoluteFile()));
			//Obtiene un iterador a las etiquetas de tabsim
			Iterator i = diccionario.keySet().iterator();
			//Mientras existan mas etiquetas
			while(i.hasNext()){
				//Se extrae la etiqueta
				String et=(String)i.next();
				//Se escribe y convirtiendo el valor a una cadena hexadecimal de 2 bytes. 
				arch.write(et+"\t\t"+Clasificador.intHexWord(diccionario.get(et))+"\n");
			}
			//Cierra el archivo
			arch.close();
		}catch(Exception e){
			//En caso de error se describe
			e.printStackTrace();
		}
	}

	/**
	  *Funcion que verifica si existe una etiqueta
	  *@param String etiqueta, la etiqueta a ser verificada
	  *@returns true si existe, false en caso contrario
	  */
	boolean existe(String etiqueta){
		return diccionario.get(etiqueta)!=null;
	}

	int valor(String etiqueta){
		return (existe(etiqueta)?diccionario.get(etiqueta):-1);
	}

}