/**
*@author Gilberto Vargas Hernández
*Clase principal que utiliza a la clase Ensamblador. 
*/
public class Main{
	public static void main(String[] args){
		Ensamblador e = new Ensamblador(args[0]);
		e.compilar();
	}	
}