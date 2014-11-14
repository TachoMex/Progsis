/**
*@author Gilberto Vargas Hernández
*Clase que procesa un código de ensamblador
*/

import java.io.*;
import java.util.*;

public class Ensamblador{
	private String rutaArchivo;
	Tabsim tabsim;
	Tabop tabop;
	//Constructor de la clase
	public Ensamblador(String r){
		rutaArchivo = r;
		tabop = new Tabop("tabop.txt");
		tabsim = new Tabsim();
	}

	/**
	 *Funcion que abre el archivo y lo compila. 
	 */
	public void compilar(){
		//Bandera que checa si hay errores
		boolean errores=false;
		//Las siguientes variables permiten acceder a los archivos. 
		BufferedReader br = null;
		File ins = null;
		File err = null;
		FileWriter fwi=null;
		FileWriter fwe=null;
		Vector<String> etiquetasPendientes = new Vector<String>();
		BufferedWriter bwi=null;
		BufferedWriter bwe=null;
		try{
			//br es el archivo de entrada
			br = new BufferedReader(new InputStreamReader(new FileInputStream(rutaArchivo+".asm")));
			String linea;
			boolean end=false,org=false;
			//Bandera que nos dice si ya leimos el comando END y ORG
			//crea los arhivos de errores e instrucciones
			ins = new File(rutaArchivo+".tmp");
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
							errores=true;
							bwe.write("linea "+lin+". Etiqueta '"+tokens[0]+"' redeclarada\n");
						}
					}
					//Se ejecuta un switch, agrupando las directivas por grupos y en el default los codop
					switch(tokens[1]){
						case "END":
							if(!errorEtiqueta){
							//Si no hay error de etiqueta, pone en true a end y escribe el comando
								end=true;
								bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\tNULL\n");
							}
						break;
						case "ORG":
							if(org){
							//Si ya habia sido declarada una directiva org antes, hay error
								errores=true;
								bwe.write("Línea "+lin+". La directiva ORG ya fue utilizada previamente\n");
							}else if(argumento<0 || argumento>=65536){
								//Verifica el rango del argumento, si es fuera de rango o esta mal formado. 
								errores=true;
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								//Verificamos que no haya un error de etiqueta antes
								if(!errorEtiqueta){
								//No hay errores, entonces decimos que ya leimos un ORG
									org=true;
								//El contador de localidades se iguala al argumento
									contLoc=argumento;
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\tNULL\n");
								}
							}
						break;
						case "EQU":
							//Si la etiqueta es null, hay error. 
							if(tokens[0]=="NULL"){
								errores=true;
								bwe.write("Línea "+lin+". Directiva EQU sin etiqueta\n");
							}else if(argumento<0 || argumento>=65536){
								//Vuelve a Verificar el rango o si esta bien formado
								errores=true;
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								//Si la etiqueta ya habia sido declarada hay otro error.
								if(!tabsim.agregar(tokens[0],argumento)){
									errores=true;
									bwe.write("linea "+lin+". Etiqueta '"+tokens[0]+"' redeclarada\n");
								}else if(!errorEtiqueta){
								//Si no hay error de etiqueta entonces 
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(argumento)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\tNULL\n");
								}
							}
						break;
						case "DB":
						case "DC.B":
						case "FCB":
							//Al igual que el resto, verifica el rango, que el numero este bien formado.  
							if(argumento<0 || argumento>=256){
								errores=true;
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
								if(!errorEtiqueta){
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\tNULL\n");
									contLoc++;
								}
							}
						break;
						case "DW":
						case "DC.W":
						case "FDB":
							//Al igual que el resto, verifica el rango, que el numero este bien formado.  
							if(argumento<0 || argumento>=65536){
								errores=true;
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
								if(!errorEtiqueta){
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\tNULL\n");
									contLoc+=2;
								}
							}
						break;
						case "FCC":
							//Verifica que el argumento no sea nulo
							if(tokens[2].equals("NULL")){
								errores=true;
								bwe.write("Línea "+lin+". Error, no puede ser nulo el argumento.\n");
							}else{
								if(!errorEtiqueta){
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\tNULL\n");
									contLoc+=tokens[2].length();
								}
							}
						break;
						case "DS.B": case "DS": case "RMB":
							//Verifica el rango, que el numero este bien formado.  
							if(argumento<0 || argumento>=256){
								errores=true;
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								if(!errorEtiqueta){
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\tNULL\n");
									contLoc+=argumento;
								}
							}
						break;
						case "DS.W": case "RMW":
							//Verifica el rango, que el numero este bien formado.  
							if(argumento<0 || argumento>=65536){
								errores=true;
								bwe.write("Línea "+lin+(argumento==65537?". El argumento no es un numero bien formado\n":". Argumento fuera de rango\n"));
							}else{
								if(!errorEtiqueta){
								//Si no hay error en la etiqueta incrementa el Contador de localidades y lo envia al archivo de salida
								contLoc+=2*argumento;
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\tNULL\n");
								}
							}
						break;
						default:
							//Obtiene los modos de direccionamiento del codop
							String modos = tabop.dameModos(tokens[1],tokens[2]);
							if(modos.startsWith("Error:")){
								//Si hay error lo muestra
								errores=true;
								bwe.write("linea "+lin+" "+modos+"\n");
							}else{	
								//Reduce de varios modos a uno solo
								String modo = tabop.dameModo(tokens[1],tokens[2],modos,tabsim);
								if(modo.startsWith("Error:")){
									//Si hay un error va al archivo de errores
									errores=true;
									bwe.write("linea "+lin+" "+modo+"\n");
								}else if(!errorEtiqueta){
									//Si no hay error en la etiqueta escribe los datos al archivo de instrucciones
									if(Clasificador.esEtiqueta(tokens[2]) && !tokens[2].equals("NULL")){
										etiquetasPendientes.add("linea "+lin+"\t"+tokens[2]);
									}
									bwi.write("linea "+lin+"\t"+Clasificador.intHexWord(contLoc)+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\t"+modo+"\n");
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
						errores=true;
						bwe.write("linea "+lin+" "+tokens[0]+"\n");
					}
				}else {
					//Si ocurre algun error inesperado tambien ira al archivo de errores
					errores=true;
					bwe.write("linea "+lin+" Error desconocido\n");
				}
				lin++;
			}

			if(!end){
				//Si no aparece END en el archivo tambien eso ocasiona un error
				errores=true;
				bwe.write("Se esperaba END al final del archivo\n");
			}
			for(int i=0;i<etiquetasPendientes.size();i++){
				StringTokenizer strtok = new StringTokenizer(etiquetasPendientes.elementAt(i),"\t");
				String l = strtok.nextToken();
				String et = strtok.nextToken();
				if(!tabsim.existe(et)){
					bwe.write(l+": etiqueta "+et+" no declarada\n");
					errores=true;
				}
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
		if(!errores){
			parse();
		}
	}

	public static String checkSum(String s){
		int sum=0;
		for(int i=0;i<s.length();i+=2){
			sum+=Integer.parseInt(""+s.charAt(i)+s.charAt(i+1),16);
		}
		sum=255-(sum&0xff);
		return Clasificador.intHexByte(sum);
	} 

	/*
     * Funcion que genera el codigo maquina del codigo
	 */
	public void parse(){
		try{
			BufferedReader arch=new BufferedReader(new FileReader((new File(rutaArchivo+".tmp")).getAbsoluteFile()));
			BufferedWriter salida=new BufferedWriter(new FileWriter((new File(rutaArchivo+".ins")).getAbsoluteFile()));
			BufferedWriter objeto = new BufferedWriter(new FileWriter((new File(rutaArchivo+".s19")).getAbsoluteFile()));
			String linea;
			String lineaObj="S0XX0000";
			String rutaArchivo2=rutaArchivo+".asm\n\r";
			for(int i=0;i<rutaArchivo2.length();i++){
				if(lineaObj.length()>=34){
					lineaObj="S0100000"+lineaObj.substring(8);
					lineaObj=lineaObj+checkSum(lineaObj.substring(2));
					objeto.write(lineaObj+"\n");
					lineaObj="S0XX0000";
				}
				lineaObj+=Clasificador.intHexByte(rutaArchivo2.charAt(i));
			}
			lineaObj="S0"+Clasificador.intHexByte(lineaObj.length()/2-1)+"0000"+lineaObj.substring(8);
			lineaObj=lineaObj+checkSum(lineaObj.substring(2));
			objeto.write(lineaObj+"\n");
			lineaObj="S1XX";
			while((linea=arch.readLine())!=null){
				StringTokenizer strtok = new StringTokenizer(linea,"\t");
				String lin = strtok.nextToken();
				String contLoc = strtok.nextToken();
				String etiqueta = strtok.nextToken();
				String codop = strtok.nextToken();
				String operando = strtok.nextToken();
				if(lineaObj.equals("S1XX")){
					lineaObj+=contLoc;
				}
				if(Clasificador.esEtiqueta(operando) && !operando.equals("NULL")){
					operando="$"+tabsim.valor(operando);
				}
				String modo = strtok.nextToken();
				String codigo ="";
				switch(modo){
					//El inmediato se calcula solo removiendo  el primer caracter (#) y convirtiendo a un hexadecimal de 2 digitos
					case "IMM8":
						codigo = tabop.codigoMaquina(codop,modo)+Clasificador.intHexByte(Clasificador.parseInt(operando.substring(1)));
					break;
					//El inmediato de 16 bits calcula solo removiendo  el primer caracter (#) y convirtiendo a un hexadecimal de 4 digitos
					case "IMM16":
						codigo = tabop.codigoMaquina(codop,modo)+Clasificador.intHexWord(Clasificador.parseInt(operando.substring(1)));
					break;
					case "INH":
					//Solo se saca el codigo maquina
						codigo = tabop.codigoMaquina(codop,modo);
					break;
					//El directo convierte el argumento a un hexadecimal de 2 digitos
					case "DIR":
						codigo = tabop.codigoMaquina(codop,modo)+Clasificador.intHexByte(Clasificador.parseInt(operando));
					break;
					//El extendido convierte el argumento a un hexadecimal de 4 digitos
					case "EXT":
						codigo = tabop.codigoMaquina(codop,modo)+Clasificador.intHexWord(Clasificador.parseInt(operando));
					break;
					//El indexado tiene casos especiales
					case "IDX":
						codigo = tabop.codigoMaquina(codop,modo);
						//el de 5bits
						if(Clasificador.modoIndizado5b(operando)){
							String arg1, arg2;
							int coma = operando.indexOf(',');
							arg1 = operando.substring(0,coma);
							arg2 = operando.substring(coma+1);
							int n = (arg1.equals("")?0:Clasificador.parseInt(arg1));
							String xb="";
							//Se calcula n, que debe estar en complemento a 2 si es negativo, quedando entre 0 y 31
							if(n<0){
								n+=32;
							}
							//Se convierte a binario
							String nstr=Integer.toString(n,2);
							//Se rellena con ceros a la izquierda si es menor a 5 bits
							while(nstr.length()<5)
								nstr="0"+nstr;
							//]Segun el acumulador es el valor de los siguientes bytes, el 0 al final siempre es constante
							xb+=(arg2.equals("X")?"00":(arg2.equals("Y")?"01":(arg2.equals("SP")?"10":"11")))+"0"+nstr;
							//Finalmente se convierte a hexadecimal de 2 digitos. 
							codigo+=Clasificador.intHexByte(Integer.parseInt(xb,2));
						}else if(Clasificador.modo_Cremento(operando)){
							//modo pre/post in/de cremento
							String arg1, arg2;
							//Se busca la coma y se separa en los 2 argumentos 
							int coma = operando.indexOf(',');
							arg1 = operando.substring(0,coma);
							arg2 = operando.substring(coma+1);
							//Se convierte a entero el primer argumento
							int n = (arg1.equals("")?0:Clasificador.parseInt(arg1));
							char prepost;
							char signo;
							//Se verifica si es pre o post
							if(arg2.charAt(0)=='+' || arg2.charAt(0)=='-'){
								//calcula el signo
								signo=arg2.charAt(0);
								prepost='0';
								arg2=arg2.substring(1);
							}else{
								prepost='1';
								//calcula el signo
								signo=arg2.charAt(arg2.length()-1);
								arg2=arg2.substring(0,arg2.length()-1);
							}
							String xb="";
							//Si es negativo se calcula el complemento
							if(signo=='-'){
								n=17-n;
							}
							n--;
							String nstr=Integer.toString(n,2);
							while(nstr.length()<4)
								nstr="0"+nstr;
							//De nuevo, segun el acumulador son los 2 digitos
							xb+=(arg2.equals("X")?"00":(arg2.equals("Y")?"01":(arg2.equals("SP")?"10":"11")))+"1"+prepost+nstr;
							//Finalmente se convierte a hexadecimal de 2 digitos. 
							codigo+=Clasificador.intHexByte(Integer.parseInt(xb,2));
						}else if(Clasificador.modoAcumulador(operando)){
							//Para el modo acumulador se separa de la misma forma en 2 cadenas. 
							String arg1, arg2;
							int coma = operando.indexOf(',');
							arg1 = operando.substring(0,coma);
							arg2 = operando.substring(coma+1);
							//Los primeros 3 bits son 1
							String xb="111";
							//Se calculan los otros bits dependiendo de los acumuladores
							xb+=(arg2.equals("X")?"00":(arg2.equals("Y")?"01":(arg2.equals("SP")?"10":"11")))+"1"+(arg1.equals("A")?"00":(arg1.equals("B")?"01":"10"));
							codigo+=Clasificador.intHexByte(Integer.parseInt(xb,2));	
						} 
					break;
					case "IDX1":
						//Se separa en 2 cadenas. 
						codigo = tabop.codigoMaquina(codop,modo);
						String arg1, arg2;
						int coma = operando.indexOf(',');
						arg1 = operando.substring(0,coma);
						arg2 = operando.substring(coma+1);
						//Conversion a entero del argumento
						int n = (arg1.equals("")?0:Clasificador.parseInt(arg1));
						//Los primeros 3 bits siempre son 1
						String xb="111";
						//Si es negativo se obtiene el complemento a 2
						if(n<0){
							n+=512;
						}
						//Conversion a cadena binaria	
						String nstr=Integer.toString(n,2);
						//Se ajusta el tamaño a 9 bits
						while(nstr.length()<9)
							nstr="0"+nstr;
						//Dependiendo del acumulador se agregan 2 bits
						xb+=(arg2.equals("X")?"00":(arg2.equals("Y")?"01":(arg2.equals("SP")?"10":"11")))+"00";
						xb+=nstr;
						//Se convierte a hexadecimal de 4 digitos
						codigo+=Clasificador.intHexWord(Integer.parseInt(xb,2));
					break;
					case "IDX2":
						//Se separa en 2 cadenas. 
						codigo = tabop.codigoMaquina(codop,modo);
						coma = operando.indexOf(',');
						arg1 = operando.substring(0,coma);
						arg2 = operando.substring(coma+1);
						//Conversion a entero del argumento
						n = (arg1.equals("")?0:Clasificador.parseInt(arg1));
						//Los primeros 3 bits siempre son 1
						xb="111";
						//Si es negativo se obtiene el complemento a 2
						if(n<0){
							n+=65536;
						}
						//Dependiendo del acumulador se agregan 2 bits
						xb="111";
						xb+=(arg2.equals("X")?"00":(arg2.equals("Y")?"01":(arg2.equals("SP")?"10":"11")))+"01"+(n<0?"1":"0");
						codigo+=Clasificador.intHexByte(Integer.parseInt(xb,2))+Clasificador.intHexWord(n);
					break;
					case "[IDX2]":
						//Se separa en 2 cadenas. 
						codigo = tabop.codigoMaquina(codop,modo);
						coma = operando.indexOf(',');
						arg1 = operando.substring(1,coma);
						arg2 = operando.substring(coma+1,operando.length()-1);
						n = (arg1.equals("")?0:Clasificador.parseInt(arg1));
						//Los primeros 3 bits siempre son 1
						xb="111";
						//Si es negativo se obtiene el complemento a 2
						if(n<0){
							n+=65536;
						}
						//Dependiendo del acumulador se agregan 2 bits
						xb+=(arg2.equals("X")?"00":(arg2.equals("Y")?"01":(arg2.equals("SP")?"10":"11")))+"011";
						codigo+=Clasificador.intHexByte(Integer.parseInt(xb,2))+Clasificador.intHexWord(n);
					break;
					case "[D,IDX]":
						//Se separa en 2 cadenas. 
						codigo = tabop.codigoMaquina(codop,modo);
						coma = operando.indexOf(',');
						arg1 = operando.substring(1,coma);
						arg2 = operando.substring(coma+1,operando.length()-1);
						//Los primeros 3 bits siempre son 1
						xb="111";
						//Dependiendo del acumulador se agregan 2 bits, los ultimos 3 son 1
						xb+=(arg2.equals("X")?"00":(arg2.equals("Y")?"01":(arg2.equals("SP")?"10":"11")))+"111";
						codigo+=Clasificador.intHexByte(Integer.parseInt(xb,2));
					break;
					case "REL8":
						codigo=tabop.codigoMaquina(codop,modo);
						Integer bitsPendientes = tabop.bits.get(codop).get(modo);
						Integer bitsCodop = bitsPendientes>>8;
						bitsPendientes&=255; 
						//Calcula los bits pendientes, los del codigo maquina e incrementa el contador de localidades
						Integer pc = bitsPendientes+bitsCodop+Integer.parseInt(contLoc,16);
						Integer rr = Clasificador.parseInt(operando)-pc;
						if(rr<0){
							rr+=256;
						}
						codigo += Clasificador.intHexByte(rr);
					break;
					case "REL9":
					break;
					case "REL16":
						codigo=tabop.codigoMaquina(codop,modo);
						bitsPendientes = tabop.bits.get(codop).get(modo);
						bitsCodop = bitsPendientes>>8;
						bitsPendientes&=255; 
						//Calcula los bits pendientes, los del codigo maquina e incrementa el contador de localidades
						pc = bitsPendientes+bitsCodop+Integer.parseInt(contLoc,16);
						rr = Integer.parseInt(operando)-pc;
						if(rr<0){
							rr+=65536;
						}
						codigo += Clasificador.intHexWord(rr);
					break;
					default:
					break;
				}
				switch(codop){
					case "DB": case "DC.B": case "FCB":
						codigo = Clasificador.intHexByte(Clasificador.parseInt(operando));
					break; 
					case "DW": case "DC.W": case "FDB":
						codigo = Clasificador.intHexWord(Clasificador.parseInt(operando));
					break;
					case "FCC":
						codigo = Clasificador.strToHex(operando);
					break;
					case "DS.B":
						lineaObj="S1"+Clasificador.intHexByte(lineaObj.length()/2-1)+lineaObj.substring(4,8)+lineaObj.substring(8);
						lineaObj=lineaObj+checkSum(lineaObj.substring(2));
						objeto.write(lineaObj+"\n");
						lineaObj="S1XX";
					break;
				}
				salida.write(lin+"\t"+contLoc+"\t"+etiqueta+"\t"+codop+"\t"+operando+"\t"+modo+"\t"+codigo+"\n");
				for(int i=0;i<codigo.length();i++){
					if(lineaObj.length()>=34){
						lineaObj="S110"+lineaObj.substring(4);
						lineaObj=lineaObj+checkSum(lineaObj.substring(2));
						objeto.write(lineaObj+"\n");
						lineaObj="S1XX"+Clasificador.intHexWord(Integer.parseInt(contLoc,16)+codigo.length()/2);
					}
					lineaObj+=codigo.charAt(i);
				}	
			}
			lineaObj="S1"+Clasificador.intHexByte(lineaObj.length()/2-1)+lineaObj.substring(4,8)+lineaObj.substring(8);
			lineaObj=lineaObj+checkSum(lineaObj.substring(2));
			objeto.write(lineaObj+"\n");
			lineaObj="S1XX";
			objeto.write("S9030000FC\n");
			arch.close();
			salida.close();
			objeto.close();
			File viejo = new File(rutaArchivo+".tmp");
			viejo.delete();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
	
 
 
 