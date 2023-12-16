/*
 * DOCUMENT: pokemon.java
 */

// Class for store the pokemon parameters of all the pokemons 
import org.json.simple.JSONArray;

public class pokemon implements Cloneable {

    // Private atribute for pokemone class
    private String name, type, img, imgS;
    private JSONArray movements;
    private int health, atack, defense, speed;

    // General constructor of Pokemon
    public pokemon(String pokeName, String pokeImg, String pokeImgS, String pokeType, int pokeHealt, int pokeAtack,
            int pokeDefense,
            int pokeSpeed, JSONArray pokeMovements) {
        name = pokeName;
        img = pokeImg;
        imgS = pokeImgS;
        type = pokeType;
        health = pokeHealt;
        atack = pokeAtack;
        defense = pokeDefense;
        speed = pokeSpeed;
        movements = pokeMovements;
    }

    // OVERRIDE THE ORIGINAL METHOD OF CLONING
    @Override
    public pokemon clone() throws CloneNotSupportedException {
        pokemon clonedPokemon = (pokemon) super.clone();

        // We clone all the atributes of the pokemon
        clonedPokemon.name = this.name;
        clonedPokemon.img = this.img;
        clonedPokemon.imgS = this.imgS;
        clonedPokemon.type = this.type;
        clonedPokemon.health = this.health;
        clonedPokemon.atack = this.atack;
        clonedPokemon.defense = this.defense;
        clonedPokemon.speed = this.speed;
        clonedPokemon.movements = this.movements;

        return clonedPokemon;
    }

    // TO STRING METHOD
    @Override
    public String toString() {
        return "pokemon [name=" + name + ", type=" + type + ", img=" + img + ", imgS=" + imgS + ", movements="
                + movements + ", health=" + health + ", atack=" + atack + ", defense=" + defense + ", speed=" + speed
                + "]";
    }

    // GETTER FOR NAME
    public String getName() {
        return name;
    }

    // GETTER FOR TYPE
    public String getType() {
        return type;
    }

    // GETTER FOR IMAGES
    public String getImg() {
        return img;
    }

    // GETTER FOR IMAGE SHYNI
    public String getImgS() {
        return imgS;
    }

    // GETTER FOR HEALTH
    public int getHealth() {
        return health;
    }

    // GETTER FOR ATACK
    public int getAtack() {
        return atack;
    }

    // GETTER FOR DEFENSE
    public int getDefense() {
        return defense;
    }

    // GETTER FOR SPEED
    public int getSpeed() {
        return speed;
    }

    // GETTER FOR MOVEMENTS
    public JSONArray getMovements() {
        return movements;
    }

    // SETTER FOR NAME
    public void setName(String name) {
        this.name = name;
    }

    // SETTER FOR TYPE
    public void setType(String type) {
        this.type = type;
    }

    // SETTER FOR IMAGES
    public void setImg(String img) {
        this.img = img;
    }

    // SETTER FOR IMAGES SHYNIS
    public void setImgS(String imgS) {
        this.imgS = imgS;
    }

    // SETTER FOR HEALTH
    public void setHealth(int health) {
        this.health = health;
    }

    // SETTER FOR ATACK
    public void setAtack(int atack) {
        this.atack = atack;
    }

    // SETTER FOR DEFENSE
    public void setDefense(int defense) {
        this.defense = defense;
    }

    // SETTER FOR SPEED
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    // SETTER FOR MOVEMENTS
    public void setMovements(JSONArray movements) {
        this.movements = movements;
    }

}
