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
	public Map<String,Map<String,String> > codigosMaquina;
	public Map<String, Map<String,Integer> > bits;
	Tabop(String s){
		codigosMaquina = new LinkedHashMap<String, Map<String,String> >();
		bits = new LinkedHashMap<String, Map<String,Integer> >();
		BufferedReader tab ;
		try{
			//Se abre un archivo donde esta almacenado el tabop y se lee linea a linea
			tab= new BufferedReader(new InputStreamReader(new FileInputStream(s)));
			String linea;
			while((linea = tab.readLine())!=null){
				//Se obtienen los datos por el delimitador y se extraen los datos. 
				StringTokenizer tok = new StringTokenizer(linea ,"|");
				//Obtiene la siguiente instruccion
				String instruccion = tok.nextToken();
				//El modo de direccionamiento
				String modo = tok.nextToken();
				//Calcula el codigo maquina
				String hex = tok.nextToken();
				//Los bits que escribe el codigo maquina
				tok.nextToken();
				Integer calculados = hex.length()/2;
				//Los bits que escribe en los parametros
				Integer parametros = Integer.parseInt(tok.nextToken());
				//Si no existe codop aun se agrega un nuevo arbol
				if(!codigosMaquina.containsKey(instruccion)){
					LinkedHashMap<String,String> temp= new LinkedHashMap<String,String>();
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
		//Obtiene el map con todos los modos del codop
		Map<String,String> m = codigosMaquina.get(codop);
		//Verifica que exista. 
		if(m!=null){
			String ret="";
			Iterator i = m.keySet().iterator();
			//Extrae los metodos mediante un iterador, en caso de que sea solo 1 modo lo Verifica como caso unico.
			if(m.keySet().size()==1){
				String s = (String)i.next();
				//Basandose en los bits necesarios del argumento Verifica si debe llevar o no argumento. 
				if(op.equals("NULL")){
					return (bits.get(codop).get(s)%256==0?s:"Error: El operando debe ser distinto a NULL");
				}else{
					return (bits.get(codop).get(s)%256!=0?s:"Error: El operando debe ser NULL");
				}
			}else{
				//Para el caso en que es mas de uno, los extrae uno a uno y verifica el codop
				//Si es null y hace que concuerden con los bits necesarios
				while(i.hasNext()){
					String s = (String)i.next();
					if(op.equals("NULL")){
						ret+= (bits.get(codop).get(s)%256==0?s+"|":"");
					}else{
						ret+= (bits.get(codop).get(s)%256!=0?s+"|":"");
					}
				}
				//Si no obtuvo ningun modo, retorna error.
				if(ret.equals("")){
					ret = "Error: El operando debe ser distinto de NULL";
				}
				//Quita el ultimo delimitador
				return ret.substring(0,ret.length()-1);
			}
		}else{
			return "Error: El codop no existe";
		}
	}


	/**
	 *Funcion que obtiene el modo de direccionamiento de un codop. Valida el operando, y si hay algun
	 *error lo retorna.  
	 *@param String codop, String op, String modos, Un codigo de operacion, un operando valido y la lista de modos, calculada quiza por el mmetodo dame modos. 
	 *@return Una cadena con los modos de direccionamiento o un error en su defecto
	 */
	String dameModo(String codop, String op, String cadenaModos, Tabsim tabsim){
		//El codop no tiene un modo de direccionamiento ni debe de retornar ningun error.
		if(codop.equals("END")){
			return "";
		}

		Vector<String> modos = new Vector<String>();
		StringTokenizer tok =new StringTokenizer(cadenaModos,"|");
		//Corta la cadena y obtiene un vector con los modos
		while(tok.hasMoreTokens())
			modos.add(tok.nextToken());
		if(modos.isEmpty()){
			return "";
		}

		//Por cada modo...
		for(int i=0;i<modos.size();i++){
			//Obtiene la cantidad de bits pendientes
			Integer pendientes= bits.get(codop).get(modos.elementAt(i))%256;
			//Verifica el modo de direccionamiento para invocar su verficador
			switch(modos.elementAt(i)){
				case "INH":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoInherente(op) || (pendientes==0 && op.equals("NULL"))){
						return "INH";
					}
				break;
				case "DIR":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoDirecto(op) || (pendientes==0 && op.equals("NULL"))){
						return "DIR";
					}
				break;
				case "EXT":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoExtendido(op) || (pendientes==0 && op.equals("NULL"))|| Clasificador.esEtiqueta(op)){
						return "EXT";	
					}
				break;

				case "IMM8":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoInmediato8b(op) || (pendientes==0 && op.equals("NULL"))){
						return "IMM8";
					}
				break;
				case "IMM16":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoInmediato16b(op) || (pendientes==0 && op.equals("NULL"))){
						return "IMM16";
					}
				break;
				case "IDX":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoIndizado5b(op)||Clasificador.modo_Cremento(op)||Clasificador.modoAcumulador(op) || (pendientes==0 && op.equals("NULL"))){
						return "IDX";
					}
				break;
				case "IDX1":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoIndizado9b(op)|| (pendientes==0 && op.equals("NULL"))){
						return "IDX1";
					}
				break;
				case "IDX2":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoIndizado16b(op)|| (pendientes==0 && op.equals("NULL"))){
						return "IDX2";
					}
				break;
				case "[IDX2]":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoIndirecto16b(op) || (pendientes==0 && op.equals("NULL"))){
						return "[IDX2]";
					}
				case "[D,IDX]":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoAcumuladorIndirecto(op)|| (pendientes==0 && op.equals("NULL"))){
						return "[D,IDX]";
					}
				break;
				case "REL8":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoRelativo8b(op)|| (pendientes==0 && op.equals("NULL")) || Clasificador.esEtiqueta(op)){
						return "REL8";
					}
				break;
				case "REL9":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoRelativo9b(op)|| (pendientes==0 && op.equals("NULL"))|| Clasificador.esEtiqueta(op)){
						return "REL9";
					}
				break;
				case "REL16":
					//Checa mediante su Clasificador o si es null el arugmento y los pendientes son 0. 
					if(Clasificador.modoRelativo16b(op)|| (pendientes==0 && op.equals("NULL"))|| Clasificador.esEtiqueta(op)){
						return "REL16";
					}
				break;

				default:
					//No deberia llegar nunca aqui, pero significaria un error. 
			}
		}

		//Si llega aqui retorna un error calculado por otro metodo
		return "Error: "+modoError(op);
	}

	/**
	 *Funcion que obtiene el modo de direccionamiento de un codop. Valida el operando, y si hay algun
	 *error lo retorna.  
	 *@param String codop, String op,  
	 *@return Una cadena con los modos de direccionamiento o un error en su defecto
	 */
	String modoError(String op){
		//Si comienza con un # el usuario intento un metodo directo 
		if(op.charAt(0)=='#'){
			String num = op.substring(1);
			if(num.equals("")){ //Si el numero es vacio 
				return "Se esperaba un dato numerico";
			}
			//Lo convierte a entero
			int x=Clasificador.parseInt(num);
			//Segun el dato obtenido por la funcion que convierte a entero, es el error. Si el numero esta correcto el unico error restante es el de no compatibilidad
			if(x==(1<<16)+1){
				return "El numero no esta bien Formado";
			}else if(x>=1<<16){
				return "Error de rango";
			}else{
				return "No compatible con modo IMM";
			}
		}else{

			//mediante expresiones regualres el error. Primero las 2 de corchetes no cerrados correctamente
			Pattern p = Pattern.compile("\\[[^\\]]*$");
			Matcher m = p.matcher(op);
			if(m.find()){
				//System.out.println("Debug: "+op);
				return "Se esperaba ']' al final del codop";
			}

			p = Pattern.compile("^[^\\[]*\\]");
			m = p.matcher(op);
			if(m.find()){
				//System.out.println("Debug: "+op);
				return "Se esperaba '[' al inicio del codop";
			}


			//Esta expresion verfica aquellos que tienen mas de 2 argumentos
			p = Pattern.compile(".*,.*,.*");
			m = p.matcher(op);
			if(m.find()){
				//System.out.println("Debug: "+op);
				return "Error en la cantidad de argumentos del codop";
			}


			//Si el dato es una etiqueta bien formada pero la longitud es mayor, entonces hay error
			p = Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]*$");
			m = p.matcher(op);
			if(m.find()&&op.length()>8){
				//System.out.println("Debug: "+op);
				return "Error en la longitud de la etiqueta";
			}

			//Esta expresion verifica que haya post y pre incremento/decremento
			p = Pattern.compile("[+-].*[+-],.*|.*,[+-].*[+-]");
			m = p.matcher(op);
			if(m.find()){
				//System.out.println("Debug: "+op);
				return "No puede haber postincremento y preincremento en una misma instruccion";
			}



			//Si el operando es una coma significa que no hay argumentos
			if(op.equals(",")){
				return "No pueden ser vacios los argumentos en el operando";
			}

			//Funcion que verifica que el segundo argumento sea algo distinto a un acumulador
			p = Pattern.compile("^\\[?.*,[$%@]?[0-9ABDEF]+");
			m = p.matcher(op);
			if(m.find()){
				//System.out.println("Debug: "+op);
				return "El segundo argumento siempre debe ser un acumulador";
			}


			//Para el caso de los modos indexados indirectos, no puede haber un primer argumento vacio.
			p = Pattern.compile("^\\[,.*\\]$");
			m = p.matcher(op);
			if(m.find()){
				return "El primer argumento no puede ser vacio";
			}

			//Verifica si el operando tiene una forma de un modo indexado. Para atacar el error mas especifico 
			p = Pattern.compile("^.*,.*[+-].*$");
			m = p.matcher(op);
			if(m.find()){ //Modo indexado
				int idx = op.indexOf(',');
				int x = Clasificador.parseInt(op.substring(0,idx));
				//Verifia si es fuera de rango el argumento
				if(x!=(1<<16)+1){
					if(x>=8 || x<=0){
						return "Error de rango";
					}
				}
			}



			//Expresion regualr para el modo de direccionamiento indexado
			p = Pattern.compile("^.*,.*$");
			m = p.matcher(op);
			if(m.find()){ 
				int idx = op.indexOf(',');
				//Calcula el primer argumento
				String arg1 = op.substring((op.charAt(0)=='['?1:0),idx);
				//Lo convierte a entero
				int x = Clasificador.parseInt(arg1);
				//Si el numero es fuera de rango
				if(x!=(1<<16)+1){
					if(x>=65536){
						return "Error de rango";
					}
					//Verifica que sea un acumulador valido
				}else if(!arg1.equals("A") && !arg1.equals("B") && !arg1.equals("D")){
					return "El primer parametro no es un numero ni un acumulador valido";
				}
			}

			p = Pattern.compile("^\\[.*,.*\\]$");
			m = p.matcher(op);
			if(m.find()){ 
				int idx = op.indexOf(',');
				int x = Clasificador.parseInt(op.substring(1,idx));
				if(x!=(1<<16)+1){
					if(x>=1<<16){
						return "Error de rango";
					}
				}
			}


			//convierte el dato a entero, para el caso de los relativos, directos y relativos.
			int x=Clasificador.parseInt(op);
			if(x!=(1<<16)+1){
				if(x>=1<<16){
					return "Error de rango";
				}
			}

			//Si resulta que la cadena si esta bien formada, entonces debe ser un problema de incompatibilidad. Se verifica esto con cada modo. 
			if(Clasificador.modoInherente(op)){
				return "No Compatible con modo INH";
			}
			if(Clasificador.modoDirecto(op)){
				return "No Compatible con modo DIR";
			}
			if(Clasificador.modoExtendido(op)){
				return "No Compatible con modo EXT";	
			}

			if(Clasificador.modoInmediato8b(op)){
				return "No Compatible con modo IMM8";
			}
			if(Clasificador.modoInmediato16b(op)){
				return "No Compatible con modo IMM16";
			}
			if(Clasificador.modoIndizado5b(op)||Clasificador.modo_Cremento(op)||Clasificador.modoAcumulador(op)){
				return "No Compatible con modo IDX";
			}
			if(Clasificador.modoIndizado9b(op)){
				return "No Compatible con modo IDX1";
			}
			if(Clasificador.modoIndizado16b(op)){
				return "No Compatible con modo IDX2";
			}
			if(Clasificador.modoIndirecto16b(op)||Clasificador.modoAcumuladorIndirecto(op)){
				return "No Compatible con modo [D,IDX]";
			}
			if(Clasificador.modoRelativo8b(op)){
				return "No Compatible con modo REL8";
			}
			if(Clasificador.modoRelativo9b(op)){
				return "No Compatible con modo REL9";
			}
			if(Clasificador.modoRelativo16b(op)){
				return "No Compatible con modo REL16";
			}


			
		}
		//Si hubiera un caso no previsto retorna un error no conocido. No deberia llegar aqui. 
		return "Error de rango";
	}

	String codigoMaquina(String codop, String modo){
		return codigosMaquina.get(codop).get(modo);
	}
}
