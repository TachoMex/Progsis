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
			boolean end=false;
			//Bandera que nos dice si ya leimos el comando END
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
			//Mientras no se llegue al fin del archivo
			while((linea = br.readLine())!=null){
				//Se procesa la linea del archivo
				String[] tokens = Clasificador.procesar(linea);
				//Verifica el tamaño del arreglo
				if(tokens!=null && tokens.length==3){
					//Se escriben los comandos al archivo de instrucciones
					bwi.write("linea "+ lin+"\t"+tokens[0]+"\t"+tokens[1]+"\t"+tokens[2]+"\n");
					//Si el comando es END termina
					if(tokens[1].equalsIgnoreCase("END")){
						end=true;
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
	
 
 
 
