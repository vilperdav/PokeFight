
import java.util.ArrayList;
import java.util.List;


public class PokeAgent implements Cloneable {
	
	public class GameState{
		private ArrayList<pokemon> playerPokemons;
        private ArrayList<pokemon> opponentPokemons;
        private battle batalla;
    private int currentPlayerPokemonIndex;
    private int currentOpponentPokemonIndex;
    static int MAX = 500;
    static int MIN = -500;
    Action currentAction=new Action();
	

    public GameState(ArrayList<pokemon> agentPokemons, ArrayList<pokemon> playerPokemons) {
        this.playerPokemons = playerPokemons;
        this.opponentPokemons = opponentPokemons;
        this.currentPlayerPokemonIndex = 0;
        this.currentOpponentPokemonIndex = 0;

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
                score +=battle.atackEfective(opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(0).toString());
                score +=battle.atackEfective(opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(1).toString());
            
            pokemon= state.getOpponentPokemons().get(currentOpponentPokemonIndex);
             System.out.println(pokemon.getName());
                score -= pokemon.getHealth();
                score -= pokemon.getAtack();
                score -= pokemon.getDefense(); 
                score -= pokemon.getSpeed();
                score -= battle.atackEfective(opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(0).toString());
                score -= battle.atackEfective(opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(1).toString());
            
            
            return score;
        }
    
        // Función Minimax con poda alfa-beta. Devuelve la acción óptima
        Action minimax(int depth, GameState state, boolean isMax, int alpha, int beta) {
            if (state.isGameOver() || depth>7) {
                Action finalAction = new Action(null, false);
                finalAction.setScore(evaluate(state));
                return finalAction;
            }

            List<Action> actions = state.getActions();

            int bestScore = isMax ? MIN : MAX;
            Action bestAction = null;

            for (Action action : actions) {
                GameState newState;
                try {
                    newState = state.doAction(action);
                } catch (CloneNotSupportedException e) {
                    // Manejo del error aquí, si es necesario
                    e.printStackTrace();
                    return null; // O retorna un valor por defecto
                }

                Action currentAction = minimax(depth + 1, newState, !isMax, alpha, beta);
                int val = currentAction.getScore();

                if (isMax && val > bestScore) {
                    bestScore = val;
                    bestAction = action;
                    alpha = Math.max(alpha, bestScore);
                } else if (!isMax && val < bestScore) {
                    bestScore = val;
                    bestAction = action;
                    beta = Math.min(beta, bestScore);
                }

                if (beta <= alpha)
                    break;
            }

            if (bestAction != null)
                bestAction.setScore(bestScore);
                
            return bestAction;
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
    
    

    // Este método debería generar todas las posibles acciones en el estado actual del juego
    public List<Action> getActions() {
        List<Action> actions = new ArrayList<Action>();
        
        //Para cada movimiento del Pokémon actual del jugador
           actions.add(new Action(playerPokemons.get(currentOpponentPokemonIndex), false));

        // Para cada Pokémon en el equipo del jugador
        for (pokemon pokemon : playerPokemons) {
            // No puedes cambiar al Pokémon que ya está en la batalla
            if (pokemon != playerPokemons.get(currentOpponentPokemonIndex)) {
                actions.add(new Action(pokemon, true));
            }
        }

        return actions;
    }

    // Este método debería realizar una acción y devolver el nuevo estado del juego
    public GameState doAction(Action action) throws CloneNotSupportedException {
        GameState newState = clone(); // Asume que tienes un método para clonar el estado del juego
           
            int a=evaluate(newState);
                if (action.isSwitch() || a<-20) {
                    
                int opponentTypeIndex = newState.getCurrentOpponentPokemonIndex();
                String opponentType = newState.getOpponentPokemons().get(opponentTypeIndex).getType().trim();

                int bestPokemonIndex = -1;
                int bestTypeEffectiveness = MIN;
        
                for (int i = 0; i < newState.getPlayerPokemons().size(); i++) {
                    
                    String currentPlayerPokemonType = newState.getPlayerPokemons().get(i).getType().trim();
                    int typeEffectiveness = compareTo(currentPlayerPokemonType,opponentType);
                    if (typeEffectiveness > bestTypeEffectiveness) {
                        bestTypeEffectiveness = typeEffectiveness;
                        bestPokemonIndex = i;
                       
                    }
                }
        
                // Cambia al Pokémon más efectivo encontrado
                if (bestPokemonIndex != -1) {
              
                    newState.setCurrentPlayerPokemonIndex(bestPokemonIndex);
                }
        } else {
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
            accion.setagentMove(accion.pokemon.getMovements().get(1).toString().trim());
           
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
        return 1;
        }else{
        return -1;}
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
