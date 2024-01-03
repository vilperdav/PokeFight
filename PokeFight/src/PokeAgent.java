
import java.util.ArrayList;
import java.util.List;


public class PokeAgent implements Cloneable {
	
	public class GameState{
		private ArrayList<pokemon> playerPokemons;
        private ArrayList<pokemon> opponentPokemons;
        private battle batalla;
    private int currentPlayerPokemonIndex;
    private int currentOpponentPokemonIndex;
    private int changePlayerPokemonIndex;
    static int MAX = 500;
    static int MIN = -500;
    Action currentAction=new Action();
	

    public GameState(ArrayList<pokemon> agentPokemons, ArrayList<pokemon> playerPokemons) {
        this.playerPokemons = playerPokemons;
        this.opponentPokemons = opponentPokemons;
        this.currentPlayerPokemonIndex = 0;
        this.currentOpponentPokemonIndex = 0;
        this.changePlayerPokemonIndex=0;

    }



        // Función de utilidad: devuelve una puntuación basada en el estado del juego
        int evaluate(GameState state) {
            int score = 0;
            // Asume que tienes un método getHealth() en tu clase Pokemon
            pokemon pokemon= state.getPlayerPokemons().get(currentPlayerPokemonIndex);
               
                score += pokemon.getHealth();
                score += pokemon.getAtack();
                score += pokemon.getDefense();
                score += pokemon.getSpeed();
                score +=battle.atackEfective(playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(1).toString())*100;
                score += compareTo(pokemon.getType(), state.getOpponentPokemons().get(currentOpponentPokemonIndex).getType());
                
            pokemon= state.getOpponentPokemons().get(currentOpponentPokemonIndex);
            
                score -= pokemon.getHealth();
                score -= pokemon.getAtack();
                score -= pokemon.getDefense(); 
                score -= pokemon.getSpeed();
                score -= battle.atackEfective(opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(1).toString())*100;
                score -= compareTo(pokemon.getType(), state.getPlayerPokemons().get(currentPlayerPokemonIndex).getType());
            
            return score;
        }

         int evaluateChange(pokemon pokeAgent,pokemon pokeUser) {
            int score = 0;
            // Asume que tienes un método getHealth() en tu clase Pokemon
               
                score += pokeAgent.getHealth();
                score += pokeAgent.getAtack();
                score += pokeAgent.getDefense();
                score += pokeAgent.getSpeed();
                score += compareTo(pokeAgent.getType(), pokeUser.getType());
            
            
                score -= pokeUser.getHealth();
                score -= pokeUser.getAtack();
                score -= pokeUser.getDefense(); 
                score -= pokeUser.getSpeed();
                score -= compareTo(pokeUser.getType(), pokeAgent.getType());           
            
            return score;
        }
    
        // Función Minimax con poda alfa-beta. Devuelve la acción óptima
        public Action minimax(int depth, GameState state, boolean isMax) {
            if (state.isGameOver() || depth == 0) {
                Action finalAction = new Action(null, false);
                finalAction.setScore(evaluate(state));
                return finalAction;
            }
    
            List<Action> actions = state.getActions(state);
            Action bestAction = null;
    
            if (isMax) {
                int bestScore = Integer.MIN_VALUE;
                for (Action action : actions) {

                    GameState newState;
                    try {
                        newState = state.doAction(action);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                        return null;
                    }
    
                    Action currentAction = minimax(depth - 1, newState, false);
                    int val = currentAction.getScore();
                    bestScore = Math.max(bestScore, val);
                    if (val >= bestScore) {
                        bestAction = action;
                    }
                }
                bestAction.setScore(bestScore);
                return bestAction;
            } else {
                int bestScore = Integer.MAX_VALUE;
                for (Action action : actions) {
                    GameState newState;
                    try {
                        newState = state.doAction(action);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                        return null;
                    }
    
                    Action currentAction = minimax(depth - 1, newState, true);
                    int val = currentAction.getScore();
                    bestScore = Math.min(bestScore, val);
                    if (val <= bestScore) {
                        bestAction = action;
                    }
                }
                bestAction.setScore(bestScore);
                return bestAction;
            }
        }
    
    // Getters
    public  ArrayList<pokemon> getPlayerPokemons() {
        return this.playerPokemons;
    }

    public  ArrayList<pokemon> getOpponentPokemons() {
        return this.opponentPokemons;
    }

    public int getCurrentPlayerPokemonIndex() {
        return this.currentPlayerPokemonIndex;
    }

    public int getCurrentOpponentPokemonIndex() {
        return this.currentOpponentPokemonIndex;
    }
    public int getChangePlayerPokemonIndex(){
        return this.changePlayerPokemonIndex;
    }

    // Setters
    public void setPlayerPokemons( ArrayList<pokemon> playerPokemons) {
        this.playerPokemons = playerPokemons;
    }

    public void setOpponentPokemons( ArrayList<pokemon> opponentPokemons) {
        this.opponentPokemons = opponentPokemons;
    }

    public void setCurrentPlayerPokemonIndex(int currentPlayerPokemonIndex) {
        this.currentPlayerPokemonIndex = currentPlayerPokemonIndex;
    }

    public void setCurrentOpponentPokemonIndex(int currentOpponentPokemonIndex) {
        this.currentOpponentPokemonIndex = currentOpponentPokemonIndex;
    }
    
    public void setChangePlayerPokemonIndex(int changePlayerPokemonIndex){
        this.changePlayerPokemonIndex=changePlayerPokemonIndex;
    }
    
    

    // Este método debería generar todas las posibles acciones en el estado actual del juego
    public List<Action> getActions(GameState state) {
        List<Action> actions = new ArrayList<Action>();
        
        //Para cada movimiento del Pokémon actual del jugador
            Action noCambio=new Action(playerPokemons.get(currentOpponentPokemonIndex), false);
            noCambio.setScore(evaluate(state));
           actions.add(noCambio);
            
        // Para cada Pokémon en el equipo del jugador
        for (pokemon pokemon : playerPokemons) {
            // No puedes cambiar al Pokémon que ya está en la batalla
            if (pokemon != playerPokemons.get(currentPlayerPokemonIndex)) {
                Action cambio=new Action(pokemon, true);
                cambio.setScore(evaluateChange(pokemon,getOpponentPokemons().get(getCurrentOpponentPokemonIndex())));
                actions.add(cambio);

            }
        }

        return actions;
    }

    // Este método debería realizar una acción y devolver el nuevo estado del juego
    public GameState doAction(Action action) throws CloneNotSupportedException {
        GameState newState = clone(); // Asume que tienes un método para clonar el estado del juego
                if (action.isSwitch() && action.getScore()<-12) {
                int opponentTypeIndex = newState.getCurrentOpponentPokemonIndex();
                String opponentType = newState.getOpponentPokemons().get(opponentTypeIndex).getType().trim();

                int bestPokemonIndex = -1;
                int bestTypeEffectiveness = MIN;
        
                for (int i = 0; i < newState.getPlayerPokemons().size(); i++) {
                    String currentPlayerPokemonType = newState.getPlayerPokemons().get(i).getType().trim();
                    int typeEffectiveness = compareTo(currentPlayerPokemonType,opponentType);
                    int evaluate= newState.evaluateChange(newState.getPlayerPokemons().get(i),newState.getOpponentPokemons().get(opponentTypeIndex));

                    typeEffectiveness=evaluate+typeEffectiveness;
                    if (typeEffectiveness > bestTypeEffectiveness) {
                        bestTypeEffectiveness = typeEffectiveness;
                        bestPokemonIndex = i;
                        System.out.println(bestPokemonIndex);
                        System.out.println(newState.getChangePlayerPokemonIndex());
                        
                    }
                }
        
                // Cambia al Pokémon más efectivo encontrado
                if (bestPokemonIndex != -1 && bestPokemonIndex != newState.getCurrentPlayerPokemonIndex()) {
                    setChangePlayerPokemonIndex(bestPokemonIndex);
                }


        } else {
            action.setSwitch(false);
            // Aplica el movimiento del Pokémon
           getBestMove(action);

        }
        return newState;
    }

    private void getBestMove(Action accion) {
        

       if (battle.atackEfective(playerPokemons.get(getCurrentOpponentPokemonIndex()).getType(),opponentPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(0).toString())
        >battle.atackEfective(playerPokemons.get(getCurrentOpponentPokemonIndex()).getType(),opponentPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(1).toString())){ 
            
            accion.setagentMove(accion.pokemon.getMovements().get(0).toString().trim());
        }else {

             if(accion.getPokemon().getName()==playerPokemons.get(currentPlayerPokemonIndex).getName()){
            accion.setagentMove(accion.pokemon.getMovements().get(1).toString().trim());
            System.out.println(accion.getagentMove());
           }
        }
    }



    // Este método debería comprobar si el juego ha terminado
    public boolean isGameOver() {
        // El juego termina si todos los Pokémon de cualquier jugador han sido derrotados
        for (pokemon pokemon : playerPokemons) {
            if (pokemon.getHealth() > 0) {
                return false;
            }
        }
        for (pokemon pokemon : opponentPokemons) {
            if (pokemon.getHealth() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public GameState clone() throws CloneNotSupportedException {
        GameState clonedState= new GameState(null, null);
        try {
        // Clonar las listas de Pokémon (asumiendo que los objetos pokemon también son clonables)
        clonedState.playerPokemons = new ArrayList<>(this.playerPokemons.size());
        for (pokemon p : this.playerPokemons) {
            clonedState.playerPokemons.add(p.clone()); // Clonar cada objeto pokemon si es clonable
        }

        clonedState.opponentPokemons = new ArrayList<>(this.opponentPokemons.size());
        for (pokemon p : this.opponentPokemons) {
            clonedState.opponentPokemons.add(p.clone()); // Clonar cada objeto pokemon si es clonable
        }

        // Clonar los demás atributos simples
        clonedState.currentPlayerPokemonIndex = this.currentPlayerPokemonIndex;
        clonedState.currentOpponentPokemonIndex = this.currentOpponentPokemonIndex;
        } catch (CloneNotSupportedException e) {
        // Manejo de la excepción aquí, si es necesario
        e.printStackTrace();
        return null; // O retorna un objeto por defecto
        }

        return clonedState;
    }

    public int compareTo(String aiType,String userType){

        if(aiType==userType){
        return 0;
     }else if ((aiType.equalsIgnoreCase("water") && userType.equalsIgnoreCase("fire")) ||
    (aiType.equalsIgnoreCase("fire") && userType.equalsIgnoreCase("plant")) ||
    (aiType.equalsIgnoreCase("plant") && userType.equalsIgnoreCase("water"))) {
        return 10;
        }else{
        return -10;}
    }

    
}

public class Action {
   
    private pokemon pokemon;
    private boolean isSwitch;
    private int score;
    private String agentMove;
    public Action(){}

    
    public Action(pokemon pokemon, boolean isSwitch) {
        this.pokemon = pokemon;
        this.isSwitch = isSwitch;
    }

    // Getters
    public pokemon getPokemon() {
        return this.pokemon;
    }

    public int getScore() {
        
        return this.score;
    }

    public boolean isSwitch() {

        return this.isSwitch;
    }

   

    // Setters
    public void setPokemon(pokemon pokemon) {
        this.pokemon = pokemon;
    }

    public void setSwitch(boolean isSwitch) {
        this.isSwitch = isSwitch;
    }

    public void setScore(int score) {
        this.score=score;
    }
    
    public String getagentMove(){
        return agentMove;
    }

    public void setagentMove(String agentMove){
        this.agentMove=agentMove;
    }
}

}
