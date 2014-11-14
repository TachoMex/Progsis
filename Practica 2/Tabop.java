/**
*@author Gilberto Vargas Hernández
*Clase que carga el TABOP y organiza la informacion
*/

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Tabop{
	//Los datos se almacenan enmascarados en un int. Como el tamaño no rebaza los 10 bits, puedo
	//Usar los primero 8 bits para un dato y los otro 8 para el segundo usando un solo int. 
	public Map<String,Map<String,Integer> > codigosMaquina;
	public Map<String, Map<String,Integer> > bits;
	Tabop(String s){
		codigosMaquina = new LinkedHashMap<String, Map<String,Integer> >();
		bits = new LinkedHashMap<String, Map<String,Integer> >();
		BufferedReader tab ;
		try{
			//Se abre un archivo donde esta almacenado el tabop y se lee linea a linea
			tab= new BufferedReader(new InputStreamReader(new FileInputStream(s)));
			String linea;
			while((linea = tab.readLine())!=null){
				//Se obtienen los datos por el delimitador y se extraen los datos. 
				StringTokenizer tok = new StringTokenizer(linea ,"|");
				String instruccion = tok.nextToken();
				String modo = tok.nextToken();
				Integer hex = Integer.parseInt(tok.nextToken(),16);
				Integer calculados = Integer.parseInt(tok.nextToken());
				Integer parametros = Integer.parseInt(tok.nextToken());
				//Si no existe codop aun se agrega un nuevo arbol
				if(!codigosMaquina.containsKey(instruccion)){
					LinkedHashMap<String,Integer> temp= new LinkedHashMap<String,Integer>();
					LinkedHashMap<String,Integer> temp2= new LinkedHashMap<String,Integer>();
					codigosMaquina.put(instruccion, temp);
					bits.put(instruccion, temp2);
				}
				//Se agrega el nuevo modo de direccionamiento
				codigosMaquina.get(instruccion).put(modo,hex);
				//Con las máscaras de bits
				bits.get(instruccion).put(modo,(calculados<<8)+parametros);
			}
			tab.close();
		}catch(Exception e){
			//En caso de que ocurra algun error de ejecucion mostramos el error
			e.printStackTrace();
		}
	}
	/**
	 *Funcion que obtiene los modos de direccionamiento de un codop. Valida el operando, y si hay algun
	 *error lo retorna. 
	 *@param String codop, String op, Un codigo de operacion y un operando validos. 
	 *@return Una cadena con los modos de direccionamiento o un error en su defecto
	 */
	String dameModos(String codop, String op){
		Map<String,Integer> m = codigosMaquina.get(codop);
		if(m!=null){
			String ret="";
			Iterator i = m.keySet().iterator();
			if(m.keySet().size()==1){
				String s = (String)i.next();
				if(op.equals("NULL")){
					return (bits.get(codop).get(s)%256==0?s:"Error: El operando debe ser distinto a NULL");
				}else{
					return (bits.get(codop).get(s)%256!=0?s:"Error: El operando debe ser NULL");
				}
			}else{
				while(i.hasNext()){
					String s = (String)i.next();
					if(op.equals("NULL")){
						ret+= (bits.get(codop).get(s)%256==0?s+",":"");
					}else{
						ret+= (bits.get(codop).get(s)%256!=0?s+",":"");
					}
				}
				if(ret.equals("")){
					ret = "Error: El operando debe ser distinto de NULL";
				}
				return ret;
			}
		}else{
			return "Error: El codop no existe";
		}
	}

}
