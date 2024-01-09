package com.example.pokefight;

import java.util.ArrayList;
import java.util.List;


public class pokeAgent implements Cloneable {
    public class GameState {

        // Declaration of variables
        private ArrayList<pokemon> playerPokemons, agentPokemons;
        private int currentPlayerPokemonIndex, currentAgentPokemonIndex, changeAgentPokemonIndex;
        private boolean permitChange;
        int MAX = 500;
        int MIN = -500;

        // Metod for initilitialize the parameters of the IA
        public GameState(ArrayList<pokemon> agentPokemons, ArrayList<pokemon> playerPokemons) {
            this.playerPokemons = playerPokemons;
            this.agentPokemons = agentPokemons;
            this.currentPlayerPokemonIndex = 0;
            this.currentAgentPokemonIndex = 0;
            this.changeAgentPokemonIndex = 0;
            this.permitChange = true;
        }


        // Función de utilidad: devuelve una puntuación basada en el estado del juego
        int evaluate(GameState state) {

            double scaleFactor = 100;
            int score = 0;

            // TODO - SETEAR LOS POKEMOSN CUANDO MUEREN PARA QUE NO DE NULL POINTER
            // ERROR - java.lang.IndexOutOfBoundsException: Index: 2, Size: 2
            // at java.util.ArrayList.get(ArrayList.java:437)
            // at com.example.pokefight.pokeAgent$GameState.evaluate(pokeAgent.java:40)

            // Obtenemos el pokemon del agente - Afectaria a la ventaja
            pokemon pokemonAgent = state.getAgentPokemons().get(getAgentPokemonIndex());
            pokemon pokemonPlayer = state.getPlayerPokemons().get(getPlayerPokemonIndex());

            score += pokemonAgent.getHealth();
            score += pokemonAgent.getAtack();
            score += pokemonAgent.getDefense();
            score += pokemonAgent.getSpeed();
            score += activity_Fight.atackEfective(pokemonAgent.getType(), pokemonPlayer.getType(), pokemonAgent.getMovements().get(1).toString()) * scaleFactor;

            System.out.println("1º Score " + pokemonAgent.getName() + ": " + score);

            // Obtenemos el pokemon del jugador - Afectaria a la desventaja

            score -= pokemonPlayer.getHealth();
            score -= pokemonPlayer.getAtack();
            score -= pokemonPlayer.getDefense();
            score -= pokemonPlayer.getSpeed();
            score -= activity_Fight.atackEfective(pokemonPlayer.getType(), pokemonAgent.getType(), pokemonPlayer.getMovements().get(1).toString()) * scaleFactor;

            System.out.println("2º Score " + pokemonPlayer.getName() + ": " + score);

            return score;
        }

        // Función Minimax con poda alfa-beta. Devuelve la acción óptima
        public Action minimax(int depth, GameState state, boolean isMax) {

            if (depth == 0) {
                Action finalAction = new Action();
                return finalAction;
            }

            Action bestAction = null;
            List<Action> actions = state.getActions(state);

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

        // Este método debería generar todas las posibles acciones en el estado actual del juego
        public List<Action> getActions(GameState state) {
            List<Action> actions = new ArrayList<Action>();

            // Para cada movimiento del Pokémon actual del jugador
            Action noCambio = new Action(agentPokemons.get(currentAgentPokemonIndex), false);
            noCambio.setScore(state.evaluate(state));
            actions.add(noCambio);

            // Para cada Pokémon en el equipo del agente
            for (pokemon pokemon : agentPokemons) {
                // No puedes cambiar al Pokémon que ya está en la batalla
                if (pokemon != agentPokemons.get(currentAgentPokemonIndex)) {
                    Action cambio = new Action(pokemon, true);
                    cambio.setScore(state.evaluate(state));
                    actions.add(cambio);
                }
            }
            return actions;
        }

        // Este método debería realizar una acción y devolver el nuevo estado del juego
        public GameState doAction(Action action) throws CloneNotSupportedException {
            GameState newState = clone();

            if (action.getIsSwitch() && permitChange) {

                int playerTypeIndex = newState.getPlayerPokemonIndex();
                String playerType = newState.getPlayerPokemons().get(playerTypeIndex).getType().trim();

                int bestPokemonIndex = -1;
                double bestTypeEffectiveness = MIN;

                // Cambio de pokemon de la IA
                for (int i = 0; i < newState.getAgentPokemons().size(); i++) {

                    String currentAgentPokemonType = newState.getAgentPokemons().get(i).getType().trim();
                    double typeEffectiveness = activity_Fight.atackEfective(currentAgentPokemonType, playerType, newState.getAgentPokemons().get(i).getMovements().get(1).toString());
                    typeEffectiveness = typeEffectiveness * 100;

                    //int evaluate = newState.evaluate(newState.getAgentPokemons().get(i), newState.getPlayerPokemons().get(playerTypeIndex));
                    int evaluate = newState.evaluate(newState);

                    // Opcion en el caso de que nos deje igual a igual
                    if (evaluate == 0) {
                        evaluate = 1;
                    }

                    typeEffectiveness = evaluate + typeEffectiveness;
                    if (typeEffectiveness > bestTypeEffectiveness) {
                        bestTypeEffectiveness = typeEffectiveness;
                        bestPokemonIndex = i;
                    }
                }

                // Cambia al Pokémon más efectivo encontrado evitando que cambie al mismo
                if (bestPokemonIndex != -1 && bestPokemonIndex != newState.getAgentPokemonIndex()) {
                    setChangeAgentPokemonIndex(bestPokemonIndex);
                } else if (bestPokemonIndex == newState.getAgentPokemonIndex()) {
                    action.setSwitch(false);
                    // Aplica el movimiento del Pokémon
                    getBestMove(action);
                }

            } else {
                action.setSwitch(false);
                // Aplica el movimiento del Pokémon
                getBestMove(action);
            }

            return newState;
        }

        private void getBestMove(Action action) {

            double firstAttack = activity_Fight.atackEfective(agentPokemons.get(getAgentPokemonIndex()).getType(), playerPokemons.get(getPlayerPokemonIndex()).getType(), agentPokemons.get(getAgentPokemonIndex()).getMovements().get(0).toString());
            double secondAttack = activity_Fight.atackEfective(agentPokemons.get(getAgentPokemonIndex()).getType(), playerPokemons.get(getPlayerPokemonIndex()).getType(), agentPokemons.get(getAgentPokemonIndex()).getMovements().get(1).toString());

            System.out.println("FirstAttack: " + firstAttack);
            System.out.println("secondAttack: " + secondAttack);

            if (firstAttack > secondAttack) {
                action.setAttack(0);

            } else {
                action.setAttack(1);
            }
        }

        @Override
        public GameState clone() {

            GameState clonedState = new GameState(null, null);
            try {

                clonedState.agentPokemons = new ArrayList<>(this.agentPokemons.size());
                for (pokemon p : this.agentPokemons) {
                    clonedState.agentPokemons.add(p.clone());
                }

                clonedState.playerPokemons = new ArrayList<>(this.playerPokemons.size());
                for (pokemon p : this.playerPokemons) {
                    clonedState.playerPokemons.add(p.clone());
                }

                // Clonar los demás atributos simples
                clonedState.currentAgentPokemonIndex = this.currentAgentPokemonIndex;
                clonedState.currentPlayerPokemonIndex = this.currentPlayerPokemonIndex;

            } catch (CloneNotSupportedException e) {
                // Manejo de la excepción aquí, si es necesario
                e.printStackTrace();
                return null; // O retorna un objeto por defecto
            }

            return clonedState;
        }

        // Getters
        public ArrayList<pokemon> getPlayerPokemons() {

            return this.playerPokemons;
        }

        public ArrayList<pokemon> getAgentPokemons() {

            return this.agentPokemons;
        }

        public int getPlayerPokemonIndex() {

            return this.currentPlayerPokemonIndex;
        }

        public int getAgentPokemonIndex() {
            return this.currentAgentPokemonIndex;
        }

        public int getAgentPokemonChangeIndex() {

            return this.changeAgentPokemonIndex;
        }


        // Setters
        public void setPlayerPokemons(ArrayList<pokemon> playerPokemons) {
            this.playerPokemons = playerPokemons;
        }

        public void setAgentPokemons(ArrayList<pokemon> agentPokemons) {
            this.agentPokemons = agentPokemons;
        }

        public void setAgentPokemonIndex(int agentPokemonIndex) {
            this.currentAgentPokemonIndex = agentPokemonIndex;
        }

        public void setPlayerPokemonIndex(int playerPokemonIndex) {
            this.currentPlayerPokemonIndex = playerPokemonIndex;
        }

        public void setChangeAgentPokemonIndex(int changeAgentPokemonIndex) {
            this.changeAgentPokemonIndex = changeAgentPokemonIndex;
        }

        public void setPermitChange(boolean permitChange) {
            this.permitChange = permitChange;
        }

    }

    public class Action {

        private pokemon pokemon;
        private boolean isSwitch;
        private int score;
        private int attack = 0;

        public Action() {
        }


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

        public boolean getIsSwitch() {

            return this.isSwitch;
        }

        public int getAttack() {
            return attack;
        }

        // Setters
        public void setPokemon(pokemon pokemon) {

            this.pokemon = pokemon;
        }

        public void setSwitch(boolean isSwitch) {

            this.isSwitch = isSwitch;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public void setAttack(int attack) {

            this.attack = attack;
        }
    }

}
