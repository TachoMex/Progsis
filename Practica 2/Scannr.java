import java.util.Scanner;

public class Scannr {
	final static int ACP=99, ERR=-1;
	static String token="", pal="";
	static int idx = 0;

	final static int matran[][] ={
	 /*0*/{8, 1, 1, 1, 1, 1, 2, 2, ERR, ERR, 1, 1},
 	/*1*/{1, 1, 1, 1, 1, 1, 1, 1, ACP, ACP, 1, 1}, 
	/*2*/{2, ACP, ACP, 5, ACP, ACP, 2, 2, 3, ACP, ACP, ACP},
	/*3*/{4, ERR, ERR, ERR, ERR, ERR, 4, 4, ERR, ERR, ERR, ERR},
	/*4*/{4, ACP, ACP, 5, ACP, ACP, 4, 4, ACP, ACP, ACP, ACP},
	/*5*/{7, ERR, ERR, ERR, ERR, ERR, 7, 7, ERR, 6, ERR, ERR},
	/*6*/{7, ERR, ERR, ERR, ERR, ERR, 7, 7, ERR, ERR, ERR, ERR},
	/*7*/{7, ACP, ACP, ACP, ACP, ACP, 7, 7, ACP, ACP, ACP, ACP},
	/*8*/{9, ACP, ACP, ACP, ACP, ACP, 9, 2, ACP, ACP, 10, ACP},
	/*9*/{9, ACP, ACP, ACP, ACP, ACP, 9, ACP, ACP, ACP, ACP, ACP},
	/*10*/{11, ERR, 11, 11, 11, ERR, 11, 11, ERR, ERR, ERR, ERR},
	/*11*/{11, ACP, 11, 11, 11, ACP, 11, 11, ACP, ACP, ACP, ACP},
	};

	public static int colCar(char c) {
		if ( c == '0') 
			return 0;
		if ( c == '_') 
			return 1;
		if ( Character.toLowerCase(c) >= 'a' && Character.toLowerCase(c) <= 'd' ) 
			return 2;
		if ( Character.toLowerCase(c) == 'e' ) 
			return 3;
		if ( Character.toLowerCase(c) == 'f' ) 
			return 4;
		if ( Character.toLowerCase(c) >= 'g' &&  Character.toLowerCase(c) <= 'w' ) 
			return 5;
		if ( Character.toLowerCase(c) >= '1' && Character.toLowerCase(c) <= '7' ) 
			return 6;
		if ( Character.toLowerCase(c) >= '8' && Character.toLowerCase(c) <= '9' ) 
			return 7;
		if ( c == '.' ) 
			return 8;
		if ( c == '-' ) 
			return 9;
		if ( Character.toLowerCase(c) == 'x' ) 
			return 10;
		if ( Character.toLowerCase(c) >= 'y' && Character.toLowerCase(c) <= 'z' ) 
			return 11;

		System.out.println("Simbolo Ilegal " + c);
			return ERR;
		}

		public static String lexico() {
			String lexema="";
			int estado = 0, estAnt=0;
			while ( estado != ACP && estado != ERR && idx < pal.length()) {
				char c = pal.charAt(idx++);
				while ( estado == 0 && (c == ' ' || c == '\t') ) 
					c = pal.charAt(idx++);
				if( (estado == 1 || estado == 2 || estado == 4 || estado == 7 || estado == 8 || estado == 9 || estado == 11) && ( c == ' ' || c == '\t')) {
					estAnt = estado;
					estado = ACP;
				}
				if( (estado == 3 || estado == 5 || estado == 6 || estado == 10) &&(c == ' ' || c == '\t') ) {
					estAnt = estado;
					estado = ERR;
				}
				int col = ERR;
				if (estado != ERR && estado != ACP ) 
						col = colCar( c );
				if( col >= 0 && estado != ERR && estado != ACP) {
					estAnt = estado;
					estado = matran[estado][col];
					if( estado != ACP && estado != ERR) 
						lexema += c;
				}
			}
			if( estado == ACP || estado == ERR) idx--;
			token="NOTOKE";
			switch(estAnt) {
				case 1: 
					token = "Ide";
				break; 
				case 2: case 4: case 7: case 8:
					token = "Dec";
				break; 
				case 9: 
					token = "Oct";
				break; 
				case 11: 
					token="Hex"; 
				break;
			}
			return lexema;
		}
	public static void main(String[] args) {
		Scanner lee = new Scanner( System.in );
		System.out.print("Entrada [.]=salir: ");
		pal = lee.nextLine();
		while( !pal.equals(".") ) {
			idx=0;
			while( idx < pal.length()) {
				String lex = lexico();
				System.out.println(token+"\t" + lex);
			}
			System.out.print("Entrada [.]=salir: ");
			pal = lee.nextLine();
		}
		lee.close();
	}
}