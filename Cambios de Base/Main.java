public class Main{
	public static void main(String args[]){
		int x = 384739;
		for(int i=2;i<65;i++){
			System.out.println("" + x + " en base "+i+" es "+Integer.toString(x,i));
		}

		String y = "111111";
		for(int i=2;i<65;i++){
			System.out.println(y + " de base " + i + " a base 10 es " + Integer.parseInt(y,i));
		}
	}
}