/**
*@author Gilberto Vargas Hernández
*Clase que nos ayuda a clasificar los tipos de token que puede procesar un ensamblador
*/

import java.util.*;
import java.util.regex.*;

public class Clasificador{

	/**
	 *Funcion que clasifica una string en sus 3 tipos de componentes
	 *@param String s, la cadena a clasificar
	 *@return una arreglo de String, si es de tamaño 1 con el error, tamaño 3 si todo salio bien
	 */
	

	public static String[] procesar(String s){
		s=s.toUpperCase();
		//Retorna un arreglo de cadenas separando las instrucciones de tamaño 3. Si la linea no es correcta retorna un arreglo de tamaño 1 con el error
		//Divide la cadena en 2, la parte que pertenece a un comentario y la parte de la instrucción
		String comentario = Clasificador.obtenerComentario(s);
		String instruccion = Clasificador.removerComentario(s);
		//Crea la variable que vamos a retornar al ginal
		String[] ret=null;
		//En una lista se van a guardar los tokens que hay en la cadena recibida. 
		List<String> tokens = new ArrayList<String>();
		String aux=instruccion;
		String tok1 = "";
		int idx=0;
		//Revisa la cadena hasta el siguiente espacio
		while( idx<aux.length() && aux.charAt(idx)!=' ' && aux.charAt(idx)!='\t'){
			tok1=tok1+aux.charAt(idx);
			idx++;
		}
		//Obtiene el primer token, en caso de no ser nulo
		if(!tok1.equals("")){
			tokens.add(tok1);
			tok1="";
		}
		//Lee los espacios 
		while( idx<aux.length() && (aux.charAt(idx)==' ' || aux.charAt(idx)=='\t')){
			idx++;
		}
		aux=aux.substring(idx);
		idx=0;
		//Vuelve a leer hasta el espacio 
		while( idx<aux.length() && aux.charAt(idx)!=' ' && aux.charAt(idx)!='\t'){
			tok1=tok1+aux.charAt(idx);
			idx++;
		}
		//Calcula el segundo token, si no es nulo
		if(!tok1.equals("")){
			tokens.add(tok1);
			tok1="";
		}
		//Lee los espacios
		while( idx<aux.length() && (aux.charAt(idx)==' ' || aux.charAt(idx)=='\t')){
			idx++;
		}
		aux=aux.substring(idx);
		idx=0;
		//Banderas para saber si estamos leyendo una cadena o si hay un caracter que escapar
		boolean comillas=false;
		boolean escape=false;
		while( idx<aux.length() && ( (aux.charAt(idx)!=' ' && aux.charAt(idx)!='\t')||comillas)){
			if(escape){
				if(aux.charAt(idx)=='\\'){
					tok1=tok1+"\\";
					escape=false;
					idx++;
				}else if(aux.charAt(idx)=='"'){
					tok1=tok1+"\"";
					escape=false;
					idx++;
				}else{
					
					escape=false;
					idx++;
				}
			}else{
				if(aux.charAt(idx)=='\\'){
					escape=true;
					idx++;
				}else if(comillas){
					if(aux.charAt(idx)=='"'){
						idx++;
						comillas=false;
						break;
					}else{
						tok1=tok1+aux.charAt(idx);
						idx++;
					}
				}else{
					if(aux.charAt(idx)=='"'){
						comillas=true;
						idx++;
					}else{
						tok1=tok1+aux.charAt(idx);
						idx++;
					}
				}
			}
		}
		if(comillas){
			String[] ret2=new String[1];
			ret2[0]="Error: Par de \" no fue cerrado"; 
			return ret2;
		}

		if(!tok1.equals("")){
			tokens.add(tok1);
			tok1="";
		}
		while(idx<aux.length()&&(aux.charAt(idx)==' ' || aux.charAt(idx)=='\t')){
			idx++;
		}
		aux=aux.substring(idx);
		idx=0;
		if(!aux.equals("")){
			tokens.add(aux);
		}
		//Dependiendo de la cantidad de tokens extraidos, se evalua la forma de la cadena
		switch(tokens.size()){
			case 1:
				//Para el caso 1, solo podría ser un CODOP y debe iniciar con espacio o tabulador
				if(Clasificador.esCodop(tokens.get(0))){
					if(s.charAt(0)==' ' || s.charAt(0)=='\t'){
						ret = new String[3];
						ret[0] = "NULL";
						ret[1] = tokens.get(0);
						ret[2] = "NULL";
					}else{
						ret = new String[1];
						if(Clasificador.esEtiqueta(tokens.get(0))){
							ret[0] = "Se esperaba un codop al final de la linea";
						}else{
							ret[0] = "La linea debe comenzar con un espacio";
						}
					}
				}else{
					ret = new String[1];
					if(s.charAt(0)==' ' || s.charAt(0)=='\t'){
							ret[0] = Clasificador.errorCodop(tokens.get(0));
					}else if(Clasificador.esEtiqueta(tokens.get(0))){
						ret[0] = "Se esperaba un codop al final de la linea";
					}else{
						ret[0] = "La linea debe comenzar con un espacio";
					}
				}
			break;
			case 2:
				//Cuando hay 2 operandos, en caso de que el primero sea una etiqueta, el segundo debe ser un CODOP
				//Pero si el primero es un CODOP el segundo debe ser un operando
				//Si no se cumple cualquier cosa, significa que hay un error

			if(Clasificador.esEtiqueta(tokens.get(0)) && s.charAt(0)!=' ' && s.charAt(0)!='\t' && Clasificador.esCodop(tokens.get(1))){
				ret = new String[3];
				ret[0] = tokens.get(0);
				ret[1] = tokens.get(1);
				ret[2] = "NULL";
			}else if(Clasificador.esCodop(tokens.get(0)) && Clasificador.esOperando(tokens.get(1)) && (s.charAt(0)==' ' || s.charAt(0)=='\t')){
				ret = new String[3];
				ret[0] = "NULL";
				ret[1] = tokens.get(0);
				ret[2] = tokens.get(1);
			}else  if(Clasificador.esEtiqueta(tokens.get(0)) && s.charAt(0)!=' ' && s.charAt(0)!='\t'){
				ret = new String[1];
				ret[0] = Clasificador.errorCodop(tokens.get(1));
				return ret;
			}else if(s.charAt(0)==' ' || s.charAt(0)=='\t'){
				ret = new String[1];
				ret[0] = Clasificador.errorCodop(tokens.get(1));
				return ret;
			}else if(Clasificador.esCodop(tokens.get(0)) && (s.charAt(0)==' ' || s.charAt(0)=='\t')){
				ret = new String[1];
				ret[0] = Clasificador.errorOperando(tokens.get(1));
				return ret;					
			}else{
				ret = new String[1];
				ret[0] = Clasificador.errorEtiqueta(tokens.get(0));
				return ret;
			}
			break;
			case 3:
				//Si la linea tiene 3 instrucciones, el unico orden en que pueden ir debe ser
				//Etiqueta - CODOP - OPERANDO
				if(Clasificador.esEtiqueta(tokens.get(0))){
					if(!s.startsWith(tokens.get(0))){
						ret = new String[1];
						ret[0] = "Las etiqutas deben aparecer al inicio de la linea";
					}else if(Clasificador.esCodop(tokens.get(1))){
						if(Clasificador.esOperando(tokens.get(2))){
							ret = new String[3];
							ret[0]=tokens.get(0);
							ret[1]=tokens.get(1);
							ret[2]=tokens.get(2);
						}else{
							ret = new String[1];
							ret[0]  = Clasificador.errorOperando(tokens.get(2));;
						}
					}else{
						ret = new String[1];
						ret[0]  = Clasificador.errorCodop(tokens.get(1));;
					}
				}else{
					ret = new String[1];
					ret[0]  = Clasificador.errorEtiqueta(tokens.get(0));;
				}
			break;
			case 0:
				//Si la linea no tiene ninguna instruccion significa que esta vacia
				ret = new String[1];
				ret[0]  = "LV";
			break;
			default:
				//Si la linea tiene demasiadas instrucciones significa que no hay como evaluar la cadena
				ret = new String[1];
				ret[0]="Error en la cantidad de tokens";
		}
		return ret;
	}

	/**
	 *Toma el comentario de una cadena
	 *@param String s, la cadena de la cual tomará el comentario
	 *@return Una cadena con el comentario dentro de la cadena, cadena vacia en caso contrario
	 */
	public static String obtenerComentario(String s){
		//El comentario inicia en la primer incidencia de un ';', por lo que una subcadena de donde se encuentra el primer ';' al final es el comentario
		boolean cadena=false;
		for(int i=0;i<s.length();i++){
			if(!cadena && s.charAt(i)==';')
				return s.substring(i);
			if(s.charAt(i)=='"')
				cadena^=true;
		}
		return "";
	}

	/**
	 *Retorna la cadena de entrada quitandole el comentario
	 *@param String s, la cadena a ser procesada
	 *@return la cadena pero sin el comentario
	 */
	public static String removerComentario(String s){
		//Se busca un ';' y se extrae la subcadena hasta la posicion de ';', sino se retorna la cadena completa
		boolean cadena=false;
		for(int i=0;i<s.length();i++){
			if(!cadena && s.charAt(i)==';')
				return s.substring(0,i);
			if(s.charAt(i)=='"')
				cadena^=true;
		}
		return s;
	}

	/**
	 *Comprueba si la cadena es una etiqueta 1
	 *@param String s, la cadena a ser verificada
	 *@return true si la cadena es una etiqueta, falso en caso contrario
	 */
	public static boolean esEtiqueta(String s){
		//La cadena debe ser de longitud 8 como maximo
		//Debe comenzar con una letra
		//Y el resto deben ser letras, numeros o '_'
		Pattern p = Pattern.compile("[a-zA-Z]{1}[a-zA-Z_0-9]{"+(s.length()-1)+"}");
		Matcher m = p.matcher(s);
		return m.find() && s.length()<=8;
	}

	/**
	 *convierte un entero a cadena Hexadecimal de 16 bits
	 *@param int num, el numero a ser convertido
	 *@return un string con los 4 digitos Hexadecimales
	 */
	public static String intHexWord(int num){
		if(num<0){
			num=65536+num%65536;
		}
		String ret = Integer.toString(num,16);
		while(ret.length()<4)
			ret="0"+ret;
		while(ret.length()>4)
			ret=ret.substring(1);
		return ret;
	}
	public static String intHexByte(int num){
		if(num<0){
			num=256+num%256;
		}
		String ret = Integer.toString(num,16);
		while(ret.length()<2)
			ret="0"+ret;
		while(ret.length()>2)
			ret=ret.substring(1);
		return ret;
	}


	//Funcion que calcula el complemento de un numero octal
	public static String complementoOct(String num){
		String res="";
		for(int i=0;i<num.length();i++){
			int digit = Integer.parseInt(""+num.charAt(i),8);
			String calc=Integer.toString(digit,2);
			while(calc.length()<3)
				calc="0"+calc;
			res+=calc;
		}
		return complementoBin(res);
	}
	//Funcion que calcula el complemento de un numero Hexadecimal
	public static String complementoHex(String num){
		String res="";
		for(int i=0;i<num.length();i++){
			int digit = Integer.parseInt(""+num.charAt(i),16);
			String calc=Integer.toString(digit,2);
			while(calc.length()<4)
				calc="0"+calc;
			res+=calc;
		}
		return complementoBin(res);
	}
	//Funcion que calcula el complemento de un numero Binario
	public static String complementoBin(String num){
		String res="";
		for(int i=0;i<num.length();i++){
			res+=(num.charAt(i)=='0'?'1':'0');
		}
		while(res.length()>2 && res.charAt(0)=='0')
			res=res.substring(1);
		return res;
	}


	/**
	 *Comprueba si la cadena es un codigo de operación 
	 *@param String s, la cadena a ser verificada
	 *@return true si la cadena es un codigo de operacion, falso en caso contrario
	 */
	public static boolean esCodop(String s){
		//La cadena debe ser de longitud 5 como maximo
		//Debe comenzar con una letra
		//El resto de la letra pueden ser letras, numeros o '_'
		//Puede contener un '.' como maximo, la bandera controla la cantidad de '.' que se han encontrado
		int puntos = s.length() - s.replace(".", "").length();
		if(puntos>1){
			return false;
		}
		Pattern p = Pattern.compile("[a-zA-Z]{1}[a-zA-Z_.]{"+(s.length()-1)+"}");
		Matcher m = p.matcher(s);
		return m.find()&&s.length()<=5;

	}

	/**
	 *Comprueba si la cadena es un operando 
	 *@param String s, la cadena a ser verificada
	 *@return true si la cadena es un operando, falso en caso contrario
	 */
	public static boolean esOperando(String s){
		return true;
		/*Pattern p = Pattern.compile("[^0-9a-zA-Z.\\$%@\\[\\]\\,]");
		Matcher m = p.matcher(s);
		return !m.find();*/
	}


	/**
	 *Calcula el error que tiene la cadena al ser evaluada como etiqueta
	 *@param String s, la cadena a ser verificada
	 *@return Una cadena con la descripción del errot
	 */
	public static String errorEtiqueta(String s){
		if(s.length()>8){
			return "El tamaño de la etiqueta exede el limite (8)";
		}	
		if(!Character.isLetter(s.charAt(0))){
			return "La etiqueta no comienza con letra";
		}
		Pattern p = Pattern.compile("[^a-zA-Z_0-9]");
		Matcher m = p.matcher(s);
		if(m.find()){
			return "La etiqueta contiene simbolos no permitidos";
		}
		return "Error desconocido";
	}

	/**
	 *Calcula el error que tiene la cadena al ser evaluada como codop
	 *@param String s, la cadena a ser verificada
	 *@return Una cadena con la descripción del errot
	 */
	public static String errorCodop(String s){
		if(s.length()>5){
			return "El tamaño del codop exede el limite (5)";
		}		
		int puntos = s.length() - s.replace(".", "").length();
		if(puntos>1){
			return "El codop tiene mas de un '.'";
		}
		if(!Character.isLetter(s.charAt(0))){
			return "El codop no comienza con letra";
		}
		Pattern p = Pattern.compile("[^a-zA-Z._]");
		Matcher m = p.matcher(s);
		if(m.find()){
			return "El codop contiene simbolos no permitidos";
		}
		return "Error desconocido";


	}

	/**
	 *Calcula el error que tiene la cadena al ser evaluada como operando
	 *@param String s, la cadena a ser verificada
	 *@return Una cadena con la descripción del errot
	 */
	public static String errorOperando(String s){
		return "El operando contiene simbolos no permitidos";
	}

	/**
	 *Verifica si la cadena dada es un numero Hexadecimal 
	 *@param String s, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valida
	 */
	public static boolean esHex(String s){
		if(s.charAt(0)=='-' || s.charAt(0)=='+')
			s=s.substring(1);
		//La cadena es evaluada bajo la expresion regular. 
		Pattern p = Pattern.compile("[a-fA-F0-9]{"+(s.length())+"}");
		Matcher m = p.matcher(s);
		return m.find();
	}

	/**
	 *Verifica si la cadena dada es un numero Octal 
	 *@param String s, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valida 
	 */
	public static boolean esOctal(String s){
		if(s.charAt(0)=='-' || s.charAt(0)=='+')
			s=s.substring(1);
		Pattern p = Pattern.compile("[0-7]{"+(s.length())+"}");
		Matcher m = p.matcher(s);
		return m.find();
	}

	/**
	 *Verifica si la cadena dada es un numero Binario 
	 *@param String s, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valida 
	 */
	public static boolean esBinario(String s){
		if(s.charAt(0)=='-' || s.charAt(0)=='+')
			s=s.substring(1);
		Pattern p = Pattern.compile("[0-1]{"+(s.length())+"}");
		Matcher m = p.matcher(s);
		return m.find();
	}

	/**
	 *Verifica si la cadena dada es un numero entero en base 10
	 *@param String s, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valida 
	 */
	public static boolean esEntero(String s){
		if(s.charAt(0)=='-' || s.charAt(0)=='+')
			s=s.substring(1);
		Pattern p = Pattern.compile("[0-9]{"+(s.length())+"}");
		Matcher m = p.matcher(s);
		return m.find();
	}


	/**
	 *Funcion que convierte un numero en cualquier base con los formatos establecidos a entero 
	 *@param String s, la cadena a ser verificada
	 *@return Un entero con el resulado, si existen errores retorna un numero fuera de rango 
	 */
	public static int parseInt(String op){
		//Si hubiera alguna excepcion significa que el numero no esta bien formado
		try{
			int num;
			String numStr=op.substring(1);
			//Dependiendo el primer digito es la base numerica. 
			switch(op.charAt(0)){
				//Base octal
				case '@':
					//Si no esta bien formado, retorna el numero fuera de rango.
					if(!Clasificador.esOctal(numStr)){
						return (1<<16)+1;
					}
					if(numStr.charAt(0)=='7'){
						num = -Integer.parseInt(complementoOct(numStr),2)-1;
					}else{
						num = Integer.parseInt(numStr,8);
					}
				break;
				//Base Hexadecimal
				case '$':
					//Si no esta bien formado, retorna el numero fuera de rango.
					if(!Clasificador.esHex(op.substring(1))){
						return (1<<16)+1;
					}
					if(numStr.charAt(0)=='F'){
						num = -Integer.parseInt(complementoHex(numStr),2)-1;
					}else{
						num = Integer.parseInt(numStr,16);
					}
				break;
				//Base binaria
				case '%':
					//Si no esta bien formado, retorna el numero fuera de rango.
					if(! Clasificador.esBinario(op.substring(1))){
						return (1<<16)+1;
					}
					if(numStr.charAt(0)=='1'){
						num = -Integer.parseInt(complementoBin(numStr),2)-1;
					}else{
						num = Integer.parseInt(numStr,2);
					}
				break;

				//Base diez
				case'-': case '+':
				case '0': case '1': case '2': case '3': case '4': 
				case '5': case '6': case '7': case '8': case '9':
					//Si no esta bien formado, retorna el numero fuera de rango.
					if(! Clasificador.esEntero(op.substring(0))){
						return (1<<16)+1;
					}
					num = Integer.parseInt(op.substring(0));
				break;
				//Caracter no valido
				default:
					return (1<<16)+1;
			}
			//Verifica que este dentro del rango
			return (num>=65536 || num<= -(1<<15)?65536:num);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return 65536;
		}
	}

/**
	 *Funcion que convierte un numero en cualquier base con los formatos establecidos a entero 
	 *@param String s, la cadena a ser verificada
	 *@return Un entero con el resulado, si existen errores retorna un numero fuera de rango 
	 */
	public static int parseIntNoComplemento(String op){
		//Si hubiera alguna excepcion significa que el numero no esta bien formado
		try{
			int num;
			String numStr=op.substring(1);
			//Dependiendo el primer digito es la base numerica. 
			switch(op.charAt(0)){
				//Base octal
				case '@':
					//Si no esta bien formado, retorna el numero fuera de rango.
					if(!Clasificador.esOctal(numStr)){
						return (1<<16)+1;
					}
					num = Integer.parseInt(numStr,8);
				
				break;
				//Base Hexadecimal
				case '$':
					//Si no esta bien formado, retorna el numero fuera de rango.
					if(!Clasificador.esHex(op.substring(1))){
						return (1<<16)+1;
					}
					num = Integer.parseInt(numStr,16);
				break;
				//Base binaria
				case '%':
					//Si no esta bien formado, retorna el numero fuera de rango.
					if(! Clasificador.esBinario(op.substring(1))){
						return (1<<16)+1;
					}
					num = Integer.parseInt(numStr,2);
				break;

				//Base diez
				case'-': case '+':
				case '0': case '1': case '2': case '3': case '4': 
				case '5': case '6': case '7': case '8': case '9':
					//Si no esta bien formado, retorna el numero fuera de rango.
					if(! Clasificador.esEntero(op.substring(0))){
						return (1<<16)+1;
					}
					num = Integer.parseInt(op.substring(0));
				break;
				//Caracter no valido
				default:
					return (1<<16)+1;
			}
			//Verifica que este dentro del rango
			return (num>=65536 || num<= -(1<<15)?65536:num);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return 65536;
		}
	}	


	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento inherente
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoInherente(String op){
		//El unico modo en que puede ser inherente es que no hay argumento. Salvo casos especiales, pero esos son tomados en cuenta en otra parte
		return op.equals("NULL");
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento inmediato de 8 bits 
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoInmediato8b(String op){
		//Si no comienza con # es falso. 
		if(op.charAt(0)!='#')
			return false;
		//Convierte el numero a entero y verifica rangos. Si no fuera valido la funcion retorna un numero fuera de rango
		int x = Clasificador.parseInt(op.substring(1));
		return x>=-256 && x <=255;
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento inmediato de 16 bits
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoInmediato16b(String op){
		//Si no comienza con # es falso. 
		if(op.charAt(0)!='#')
			return false;
		//Convierte el numero a entero y verifica rangos. Si no fuera valido la funcion retorna un numero fuera de rango
		int x = Clasificador.parseInt(op.substring(1));
		return x>=-32768 && x <=65535;
	}
	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento directo
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoDirecto(String op){
		//Convierte el numero a entero y verifica rangos. Si no fuera valido la funcion retorna un numero fuera de rango
		int num=Clasificador.parseInt(op);
		return num>=0 && num<=255;
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento extendido
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoExtendido(String op){
		//Convierte el numero a entero y verifica rangos. Si no fuera valido la funcion retorna un numero fuera de rango
		int num=Clasificador.parseInt(op);	
		return num>=-32768 && num<=65535 || esEtiqueta(op);
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento indizado de 5 bits
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoIndizado5b(String op){
		int idx = op.indexOf(',');
		//Si no hay coma entonces es erroneo
		if(idx<0)
			return false;
		String n = op.substring(0,idx); //Obtiene el primer argumento
		String r = op.substring(idx+1); //Y el segundo
		int a = (n.equals("")?0:Clasificador.parseInt(n));
		return a>=-16 && a <=15 && (r.equals("X") || r.equals("Y") || r.equals("SP") || r.equals("PC"));
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento indizado de 9 bits
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoIndizado9b(String op){
		int idx = op.indexOf(',');
		//Si no hay coma entonces es erroneo
		if(idx<0)
			return false;
		String n = op.substring(0,idx); //Obtiene el prime argumento
		String r = op.substring(idx+1); //Y el segundo
		int a = (n.equals("")?0:Clasificador.parseInt(n)); //Convierte a entero
		//Verifica el rango y que el acumulador sea valido
		return a>=-256 && a <=255 && (r.equals("X") || r.equals("Y") || r.equals("SP") || r.equals("PC"));
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento indizado de 16 bits
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoIndizado16b(String op){
		int idx = op.indexOf(',');
		//si no hay una coma entonces debe ser erroneo
		if(idx<0){
			return false;
		}
		String n = op.substring(0,idx); //Obtiene el primer argumento
		String r = op.substring(idx+1); //Y el segundo
		int a = (n.equals("")?0:Clasificador.parseInt(n)); //convierte a entero
		//Verifica el rango y que sea un acumulador valido
		return a>=0 && a <=65535 && (r.equals("X") || r.equals("Y") || r.equals("SP") || r.equals("PC"));
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento Indirecto de 16 bits
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoIndirecto16b(String op){
		return op.charAt(0)=='[' && op.charAt(op.length()-1)==']' && modoIndizado16b(op.substring(1,op.length()-1));
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento con pre-post incremento/decremento
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */

	public static boolean modo_Cremento(String op){
		if(op.length()<3)
			return false;
		int idx = op.indexOf(',');
		if(idx<0)
			return false;
		String n = op.substring(0,idx);
		String r = op.substring(idx+1);
		int a = (n.equals("")?0:Clasificador.parseInt(n));
		if(r.charAt(0) == '-' || r.charAt(0)== '+'){
			r=r.substring(1);
			return a>=1 && a <=8 && (r.equals("X") || r.equals("Y") || r.equals("SP"));
		}else if(r.charAt(r.length()-1)=='-' || r.charAt(r.length()-1)=='+'){
			r=r.substring(0,r.length()-1);
			return a>=1 && a <=8 && (r.equals("X") || r.equals("Y") || r.equals("SP"));
		}
		return false;
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento por acumuladores
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoAcumulador(String op){
		int idx = op.indexOf(',');
		if(idx<0)
			return false;
		String n = op.substring(0,idx);
		String r = op.substring(idx+1);
		return (n.equals("A")||n.equals("B")||n.equals("D")) && (r.equals("X") || r.equals("Y") || r.equals("SP") || r.equals("PC"));
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento por acumuladores Indirecto
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoAcumuladorIndirecto(String op){
		if(op.charAt(0)!='[' || op.charAt(op.length()-1)!=']'){
			return false;
		}
		op=op.substring(1,op.length()-1);
		int idx = op.indexOf(',');
		if(idx<0)
			return false;
		String n = op.substring(0,idx);
		String r = op.substring(idx+1);
		return (n.equals("D")) && (r.equals("X") || r.equals("Y") || r.equals("SP") || r.equals("PC"));
	}

	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento relativo de 8 bits
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoRelativo8b(String op){
		if(Clasificador.esEtiqueta(op))
			return true;
		int n = parseInt(op);
		return n>=-128 && n<=127;
	}
	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento 
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoRelativo9b(String op){
		if(Clasificador.esEtiqueta(op))
			return true;
		int n = parseInt(op);
		return n>=-256 && n<=255;
	}
	/**
	 *Verifica si la cadena dada es un operando valido para el modo de direccionamiento relativo de 16 bits
	 *@param String op, la cadena a ser verificada
	 *@return Verdadero en caso de que sea valido 
	 */
	public static boolean modoRelativo16b(String op){
		if(Clasificador.esEtiqueta(op))
			return true;
		int n = parseInt(op);
		return n>=-32768 && n<=65535;
	}

}