package agents;
import java.util.Random;

public class RandomPokeAgent {
	
	private String nombre;
	private int vida;
	private Random azar = new Random();
	private Random azar2= new Random(System.currentTimeMillis());
	
	public RandomPokeAgent(String nombrePok, int vidaPok) {
		nombre = nombrePok;
		vida = vidaPok;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public int getVida() {
		return vida;
	}
	

	public int seleccionarPoke(){
		int i=azar2.nextInt(10);
		System.out.println(i);
		return i;

	}
	public int atacar() {
		
		int ataque = azar.nextInt(11) + 10;
		
		System.out.println("Daño conseguido: " + ataque + " puntos");
		return ataque;
	}

	@Override
	public String toString() {
		return "Nombre: " + nombre + "\nVida: " + vida;
	}

}
