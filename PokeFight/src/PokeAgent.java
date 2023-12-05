
import java.util.ArrayList;
import java.util.List;


public class PokeAgent {
	
	public class GameState{
		private pokemon[] playerPokemons;
        private pokemon[] opponentPokemons;
    private int currentPlayerPokemonIndex;
    private int currentOpponentPokemonIndex;

	

    public GameState(pokemon[] playerPokemons, pokemon[] opponentPokemons) {
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
            }
            for (pokemon pokemon : state.getOpponentPokemons()) {
                score -= pokemon.getHealth();
            }
        
            return score;
        }
    
        // Función Minimax con poda alfa-beta. Devuelve la acción óptima
        Action minimax(int depth, GameState state, Boolean isMax, int alpha, int beta) throws CloneNotSupportedException {
            // Si el juego ha terminado, devuelve la evaluación del estado del juego
            if (state.isGameOver())
                return new Action(null,false);
    
            // Genera todas las posibles acciones en el estado actual del juego
            List<Action> actions = state.getActions();
    
            // Si este nodo maximizador
            if (isMax) {
                int bestScore = Integer.MIN_VALUE;
                Action bestAction = null;
    
                // Recorre todas las posibles acciones
                for (Action action : actions) {
                    // Realiza la acción y obtiene el nuevo estado del juego
                    GameState newState = state.doAction(action);
    
                    // Llama a minimax recursivamente y elige la acción con la puntuación máxima
                    Action currentAction = minimax(depth + 1, newState, !isMax, alpha, beta);
                    int val = currentAction.getScore();
                    if (val > bestScore) {
                        bestScore = val;
                        bestAction = action;
                    }
                    alpha = Math.max(alpha, bestScore);
    
                    // Poda alfa-beta
                    if (beta <= alpha)
                        break;
                }
    
                bestAction.setScore(bestScore);
                return bestAction;
            }
    
            // Si este nodo minimizador
            else {
                int bestScore = Integer.MAX_VALUE;
                Action bestAction = null;
    
                // Recorre todas las posibles acciones
                for (Action action : actions) {
                    // Realiza la acción y obtiene el nuevo estado del juego
                    GameState newState = state.doAction(action);
    
                    // Llama a minimax recursivamente y elige la acción con la puntuación mínima
                    Action currentAction = minimax(depth + 1, newState, !isMax, alpha, beta);
                    int val = currentAction.getScore();
                    if (val < bestScore) {
                        bestScore = val;
                        bestAction = action;
                    }
                    beta = Math.min(beta, bestScore);
    
                    // Poda alfa-beta
                    if (beta <= alpha)
                        break;
                }
    
                bestAction.setScore(bestScore);
                return bestAction;
            }
        }
    
    
    // Getters
    public pokemon[] getPlayerPokemons() {
        return this.playerPokemons;
    }

    public pokemon[] getOpponentPokemons() {
        return this.opponentPokemons;
    }

    public int getCurrentPlayerPokemonIndex() {
        return this.currentPlayerPokemonIndex;
    }

    public int getCurrentOpponentPokemonIndex() {
        return this.currentOpponentPokemonIndex;
    }

    // Setters
    public void setPlayerPokemons(pokemon[] playerPokemons) {
        this.playerPokemons = playerPokemons;
    }

    public void setOpponentPokemons(pokemon[] opponentPokemons) {
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
           actions.add(new Action(playerPokemons[currentPlayerPokemonIndex], false));
        

        // Para cada Pokémon en el equipo del jugador
        for (pokemon pokemon : playerPokemons) {
            // No puedes cambiar al Pokémon que ya está en la batalla
            if (pokemon != playerPokemons[currentPlayerPokemonIndex]) {
                actions.add(new Action(pokemon, true));
            }
        }

        return actions;
    }

    // Este método debería realizar una acción y devolver el nuevo estado del juego
    public GameState doAction(Action action) throws CloneNotSupportedException {
        GameState newState = (GameState) this.clone(); // Asume que tienes un método para clonar el estado del juego

        if (action.isSwitch()) {
            // Encuentra el índice del Pokémon al que se está cambiando
            for (int i = 0; i < newState.playerPokemons.length; i++) {
                if (newState.playerPokemons[i] == action.getPokemon()) {
                    newState.currentPlayerPokemonIndex = i;
                    break;
                }
            }
        } else {
            // Aplica el movimiento del Pokémon
            // Necesitarás implementar la lógica del juego aquí, como reducir la salud del Pokémon del oponente
            
        }

        return newState;
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
}

public class Action {
   
    private pokemon pokemon;
    private boolean isSwitch;

    public Action(){}

    public void setScore(int bestScore) {
    }

    public int getScore() {
        return 0;
    }

    public Action(pokemon pokemon, boolean isSwitch) {
        this.pokemon = pokemon;
        this.isSwitch = isSwitch;
    }

    // Getters
    public pokemon getPokemon() {
        return this.pokemon;
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
}

}
