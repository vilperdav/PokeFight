/*
 * DOCUMENT: pokemon.java
 */

// Class for store the pokemon parameters of all the pokemons 
public class pokemon {

    // Private atribute for pokemone class
    private String name, type, img;
    private String[] movements;
    private int health, atack, defense, speed;

    // General constructor of Pokemon
    public pokemon(String pokeName, String pokeType, String pokeImg, int pokeHealt, int pokeAtack, int pokeDefense,
            int pokeSpeed, String[] pokeMovements) {
        name = pokeName;
        type = pokeType;
        img = pokeImg;
        health = pokeHealt;
        atack = pokeAtack;
        defense = pokeDefense;
        speed = pokeSpeed;
        movements = pokeMovements;
    }

    // toString for Pokemon Class
    @Override
    public String toString() {
        return "Pokemon [name=" + name + ", type=" + type + ", img=" + img + ", health=" + health + ", atack=" + atack
                + ", defense=" + defense + ", speed=" + speed + ", movements=" + movements + "]";
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
    public String[] getMovements() {
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
    public void setMovements(String[] movements) {
        this.movements = movements;
    }
}
