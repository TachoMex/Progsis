/**
*@author Gilberto Vargas Hernández
*Clase que procesa un código de ensamblador
*/

import java.io.*;

public class Ensamblador{
	private String rutaArchivo;
	//Constructor de la clase
	public Ensamblador(String r){
		rutaArchivo = r;
	}

	/**
	 *Funcion que abre el archivo y lo compila. 
	 */
	public void compilar(){
		//Carga el tabop en la clase Tabop
		Tabop tabop= new Tabop("tabop.txt");
		//Inicializa el tabsim, sin etiquetas
		Tabsim tabsim = new Tabsim();
		//Las siguientes variables permiten acceder a los archivos. 
		BufferedReader br = null;
		File ins = null;
		File err = null;
		FileWriter fwi=null;
		FileWriter fwe=null;
		BufferedWriter bwi=null;
		BufferedWriter bwe=null;
		try{
			//br es el archivo de entrada
			br = new BufferedReader(new InputStreamReader(new FileInputStream(rutaArchivo+".asm")));
			String linea;
			boolean end=false,org=false;
			//Bandera que nos dice si ya leimos el comando END y ORG
			//crea los arhivos de errores e instrucciones
			ins = new File(rutaArchivo+".ins");
			err = new File(rutaArchivo+".err");
			fwe = new FileWriter(err.getAbsoluteFile());
			fwi = new FileWriter(ins.getAbsoluteFile());
			if(!ins.exists()) {
				ins.createNewFile();
			}
			if(!err.exists()) {
				err.createNewFile();
			}
			//bwi es el archivo con las instrucciones
			bwi = new BufferedWriter(fwi);
			//bwe es el archivo con los errores
			bwe = new BufferedWriter(fwe);
			//Contador de linea
			int lin=1;
			//Contador de localidades
			int contLoc=0;
			//Mientras no se llegue al fin del archivo
			while((linea = br.readLine())!=null && !end){
				//Se procesa la linea del archivo
				String[] tokens = Clasificador.procesar(linea);
				//Verifica el tamaño del arreglo
				if(tokens!=null && tokens.length==3){
					//Calcula el valor numerico del operando, por si fuera necesario para alguna directiva. 
					int argumento = Clasificador.parseInt(tokens[2]);
					//Si existe un error en la etiqueta se marca aqui
					boolean errorEtiqueta=false;
					//Si la etiqueta no es nula y no es una directiva EQU
					if(!tokens[0].equals("NULL") && !tokens[1].equals("EQU")){
						//Si la etiqueta ya habia sido declarada, nos regresa false y entra al if. 
						if(!tabsim.agregar(tokens[0],contLoc)){
							errorEtiqueta=true;
							//Indicando que hubo un error en la etiqueta. 
							bwe.write("linea "+lin+". Etiqueta '"+tokens[0]+"' redeclarada\n");
						}
					}
					//Se ejecuta un switch, agrupando las directivas por grupos y en el default los codop
					switch(tokens[1]){
						case "END":
							if(!errorEtiqueta){
							//Si no hay error de etiqueta, pone en true a end y escribe el comando
								end=true;
								bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n");
							}
						break;
						case "ORG":
							if(org){
							//Si ya habia sido declarada una directiva org antes, hay error
								bwe.write("Línea "+lin+". La directiva ORG ya fue utilizada previamente\n");
							}else if(argumento<0 || argumento>=65536){
								//Verifica el rango del argumento, si es fuera de rango o esta mal formado. 
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								//Verificamos que no haya un error de etiqueta antes
								if(!errorEtiqueta){
								//No hay errores, entonces decimos que ya leimos un ORG
									org=true;
								//El contador de localidades se iguala al argumento
									contLoc=argumento;
									bwi.write("linea "+ lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n");
								}
							}
						break;
						case "EQU":
							//Si la etiqueta es null, hay error. 
							if(tokens[0]=="NULL"){
								bwe.write("Línea "+lin+". Directiva EQU sin etiqueta\n");
							}else if(argumento<0 || argumento>=65536){
								//Vuelve a Verificar el rango o si esta bien formado
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								//Si la etiqueta ya habia sido declarada hay otro error.
								if(!tabsim.agregar(tokens[0],argumento)){
									bwe.write("linea "+lin+". Etiqueta '"+tokens[0]+"' redeclarada\n");
								}else if(!errorEtiqueta){
								//Si no hay error de etiqueta entonces 
									bwi.write("linea "+ lin+"\t"+Clasificador.intHexWord(argumento)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n");
								}
							}
						break;
						case "DB":
						case "DC.B":
						case "FCB":
							//Al igual que el resto, verifica el rango, que el numero este bien formado.  
							if(argumento<0 || argumento>=256){
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
								if(!errorEtiqueta){
									bwi.write("linea "+ lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n");
									contLoc++;
								}
							}
						break;
						case "DW":
						case "DC.W":
						case "FDB":
							//Al igual que el resto, verifica el rango, que el numero este bien formado.  
							if(argumento<0 || argumento>=65536){
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
								if(!errorEtiqueta){
									bwi.write("linea "+ lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n");
									contLoc+=2;
								}
							}
						break;
						case "FCC":
							//Verifica que el argumento no sea nulo
							if(tokens[2].equals("NULL")){
								bwe.write("Línea "+lin+". Error, no puede ser nulo el argumento.\n");
							}else{
								if(!errorEtiqueta){
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n");
									contLoc+=tokens[2].length();
								}
							}
						break;
						case "DS.B": case "DS": case "RMB":
							//Verifica el rango, que el numero este bien formado.  
							if(argumento<0 || argumento>=256){
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								if(!errorEtiqueta){
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n");
									contLoc+=argumento;
								}
							}
						break;
						case "DS.W": case "RMW":
							//Verifica el rango, que el numero este bien formado.  
							if(argumento<0 || argumento>=65536){
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								if(!errorEtiqueta){
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
								contLoc+=2*argumento;
									bwi.write("linea "+ lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n");
								}
							}
						break;
						default:
							//Obtiene los modos de direccionamiento del codop
							String modos = tabop.dameModos(tokens[1],tokens[2]);
							if(modos.startsWith("Error:")){
								//Si hay error lo muestra
								bwe.write("linea "+lin+" "+modos+"\n");
							}else{	
								//Reduce de varios modos a uno solo
								String modo = tabop.dameModo(tokens[1],tokens[2],modos);
								if(modo.startsWith("Error:")){
									//Si hay un error va al archivo de errores
									bwe.write("linea "+lin+" "+modo+"\n");
								}else if(!errorEtiqueta){
									//Si no hay error en la etiqueta escribe los datos al archivo de instrucciones
									bwi.write("linea "+ lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\t"+modo+"\n");
									Integer bitsPendientes = tabop.bits.get(tokens[1]).get(modo);
									Integer bitsCodop = bitsPendientes>>8;
									bitsPendientes&=255; 
									//Calcula los bits pendientes, los del codigo maquina e incrementa el contador de localidades
									contLoc+=bitsPendientes+bitsCodop;
								}
							}
						break;
					}
				}else if(tokens!=null){
					//Si el error es conocido lo muestra en el archivo de errores
					if(tokens[0]!="LV"){
						bwe.write("linea "+lin+" "+tokens[0]+"\n");
					}
				}else {
					//Si ocurre algun error inesperado tambien ira al archivo de errores
					bwe.write("linea "+lin+" Error desconocido\n");
				}
				lin++;
			}

			if(!end){
				//Si no aparece END en el archivo tambien eso ocasiona un error
				bwe.write("Se esperaba END al final del archivo\n");
			}

			tabsim.guardar(rutaArchivo);
		}catch(Exception e){
			//En caso de que ocurra algun error de ejecucion mostramos el error
			e.printStackTrace();
		}
		try{
			//Intenta cerrar los archivos
			if(br!=null)
				br.close();
			if(bwe!=null)
				bwe.close();
			if(bwi!=null)
				bwi.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
	
 
 
 