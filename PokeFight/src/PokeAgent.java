
import java.util.ArrayList;
import java.util.List;


public class PokeAgent {
	
	public class GameState{
		private ArrayList<pokemon> playerPokemons;
        private ArrayList<pokemon> opponentPokemons;
        private battle batalla;
    private int currentPlayerPokemonIndex;
    private int currentOpponentPokemonIndex;
    static int MAX = 500;
    static int MIN = -500;

	

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
            for (pokemon pokemon : state.getPlayerPokemons()) {
                score += pokemon.getHealth();
                score += pokemon.getAtack();
                score += pokemon.getDefense();
                score +=battle.atackEfective(opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(0).toString());
                score +=battle.atackEfective(opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(1).toString());
               
            }
            for (pokemon pokemon : state.getOpponentPokemons()) {
                score -= pokemon.getHealth();
                score -= pokemon.getAtack();
                score -= pokemon.getDefense(); 
                score -= battle.atackEfective(opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(0).toString());
                score -= battle.atackEfective(opponentPokemons.get(getCurrentOpponentPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getType(),playerPokemons.get(getCurrentPlayerPokemonIndex()).getMovements().get(1).toString());
            }
            return score;
        }
    
        // Función Minimax con poda alfa-beta. Devuelve la acción óptima
       Action minimax(int depth, GameState state, boolean isMax, int alpha, int beta) throws CloneNotSupportedException {
    if (state.isGameOver())
        return new Action(null, false); // Supongo que Action tiene un constructor con un pokemon y un score

    List<Action> actions = state.getActions();
        System.out.println(playerPokemons);

    if (isMax) {
        int bestScore = Integer.MIN_VALUE;
        Action bestAction = null;

        for (Action action : actions) {
            GameState newState = state.doAction(action);
             if(depth==15){
             try{
            Action currentAction = minimax(depth + 1, newState, !isMax, alpha, beta);
        }catch (Exception e){}}
            //int val = currentAction.getScore();
            int val=1;
            if (val > bestScore) {
                bestScore = val;
                bestAction = action;
            }

            alpha = Math.max(alpha, bestScore);

            if (beta <= alpha)
                break;
        }

        if (bestAction != null)
            bestAction.setScore(bestScore);

        return bestAction;
    } else {
        int bestScore = Integer.MAX_VALUE;
        Action bestAction = null;

        for (Action action : actions) {
            GameState newState = state.doAction(action);
            if(depth==15){
             try{
            Action currentAction = minimax(depth + 1, newState, !isMax, alpha, beta);
        }catch (Exception e){}}
            //int val = currentAction.getScore();
            int val=1;
            if (val < bestScore) {
                bestScore = val;
                bestAction = action;
            }

            beta = Math.min(beta, bestScore);

            if (beta <= alpha)
                break;
        }

        if (bestAction != null)
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
        if (action.isSwitch()) {
            // Encuentra el índice del Pokémon al que se está cambiando
            for (int i = 0; i < newState.playerPokemons.size(); i++) {
                if (newState.playerPokemons.get(i) == action.getPokemon()) {
                    newState.currentPlayerPokemonIndex = i;
                    break;
                }
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
            accion.setagentMove(accion.pokemon.getMovements().get(0).toString());
            
        }else {
            accion.setagentMove(accion.pokemon.getMovements().get(1).toString());
           
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
     

        return clonedState;
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

    public void setScore(int bestScore) {
    }
    
    public String getagentMove(){
        return agentMove;
    }

    public void setagentMove(String agentMove){
        this.agentMove=agentMove;
    }
}

}
