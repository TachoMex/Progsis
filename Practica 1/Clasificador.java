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
		//Retorna un arreglo de cadenas separando las instrucciones de tamaño 3. Si la linea no es correcta retorna un arreglo de tamaño 1 con el error
		//Divide la cadena en 2, la parte que pertenece a un comentario y la parte de la instrucción
		String comentario = Clasificador.obtenerComentario(s);
		String instruccion = Clasificador.removerComentario(s);
		//Crea la variable que vamos a retornar al ginal
		String[] ret=null;
		//En una lista se van a guardar los tokens que hay en la cadena recibida. 
		List<String> tokens = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(instruccion, "\t ");
		//Extrae los tokens
		while(tok.hasMoreTokens()){
			tokens.add(tok.nextToken());
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
					if(!Character.isLetter(s.charAt(0))){
						ret = new String[1];
						ret[0] = "Las etiqutaes deben aparecer al inicio de la linea";
					}
					if(Clasificador.esCodop(tokens.get(1))){
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
		int idx = s.indexOf(';');
		return (idx>=0?s.substring(idx+1):"");
	}

	/**
	 *Retorna la cadena de entrada quitandole el comentario
	 *@param String s, la cadena a ser procesada
	 *@return la cadena pero sin el comentario
	 */
	public static String removerComentario(String s){
		//Se busca un ';' y se extrae la subcadena hasta la posicion de ';', sino se retorna la cadena completa
		int idx = s.indexOf(';');
		return (idx>=0?s.substring(0,idx):s);
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
		Pattern p = Pattern.compile("[a-zA-Z]{1}[a-zA-Z_0-9.]{"+(s.length()-1)+"}");
		Matcher m = p.matcher(s);
		return m.find()&&s.length()<=5;

	}

	/**
	 *Comprueba si la cadena es un operando 
	 *@param String s, la cadena a ser verificada
	 *@return true si la cadena es un operando, falso en caso contrario
	 */
	public static boolean esOperando(String s){
		//Para esta practica no hay una regla de operandos
		return true;
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
		Pattern p = Pattern.compile("[^a-zA-Z._0-9]");
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
		//Al no haber ninguna restriccion aun nunca caera aqui. 
		return "";
	}

}