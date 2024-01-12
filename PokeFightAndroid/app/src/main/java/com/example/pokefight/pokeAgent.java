package com.example.pokefight;

import java.util.ArrayList;
import java.util.List;


public class pokeAgent implements Cloneable {

    /*
     * ********************************************************************************************
     * * GameState                                                                                *
     * ********************************************************************************************
     * */
    //The game state, which see all the pokemon allive and this stats to the AI
    public class GameState {

        // Declaration of variables
        private ArrayList<pokemon> playerPokemons, agentPokemons;
        private int currentPlayerPokemonIndex, currentAgentPokemonIndex, changeAgentPokemonIndex;
        private boolean permitChange;
        int MIN = -500;

        /*
         * ********************************************************************************************
         * * GameState                                                                                *
         * ********************************************************************************************
         * */
        // Metod for initilitialize the parameters of the IA
        public GameState(ArrayList<pokemon> agentPokemons, ArrayList<pokemon> playerPokemons) {
            this.playerPokemons = playerPokemons;
            this.agentPokemons = agentPokemons;
            this.currentPlayerPokemonIndex = 0;
            this.currentAgentPokemonIndex = 0;
            this.changeAgentPokemonIndex = 0;
            this.permitChange = true;
        }

        /*
         * ********************************************************************************************
         * * evaluate                                                                                 *
         * ********************************************************************************************
         * */
        // Function to evaluate the AI pokemon in compare to the player pokemon
        int evaluate(GameState state) {

            double scaleFactor = 100;
            int score = 0;

            // Plus all the stats of the AI pokemon to see his vantages
            // System.out.println("Agent Pokemon Index: " + getAgentPokemonIndex());
            // System.out.println("Player Pokemon Index: " + getPlayerPokemonIndex());

            pokemon pokemonAgent = state.getAgentPokemons().get(getAgentPokemonIndex());
            pokemon pokemonPlayer = state.getPlayerPokemons().get(getPlayerPokemonIndex());

            score += pokemonAgent.getHealth();
            score += pokemonAgent.getAtack();
            score += pokemonAgent.getDefense();
            score += pokemonAgent.getSpeed();
            score += activity_Fight.atackEfective(pokemonAgent.getType(), pokemonPlayer.getType(), pokemonAgent.getMovements().get(1).toString()) * scaleFactor;

            System.out.println("1º Score - Agent: " + pokemonAgent.getName() + ": " + score);

            // Minus all the disadvantages of the player pokemon

            score -= pokemonPlayer.getHealth();
            score -= pokemonPlayer.getAtack();
            score -= pokemonPlayer.getDefense();
            score -= pokemonPlayer.getSpeed();
            score -= activity_Fight.atackEfective(pokemonPlayer.getType(), pokemonAgent.getType(), pokemonPlayer.getMovements().get(1).toString()) * scaleFactor;

            System.out.println("2º Score - Player: " + pokemonPlayer.getName() + ": " + score);

            return score;
        }

        /*
         * ********************************************************************************************
         * * minMax                                                                                  *
         * ********************************************************************************************
         * */
        // Function minMax who creates a decision tree to see the best decision
        public Action minMax(int depth, GameState state, boolean isMax) {

            // Final  of the tree and the actions, now return to compare all the scores
            if (depth == 0) {
                Action finalAction = new Action();
                return finalAction;
            }

            Action bestAction = null;
            List<Action> actions = state.getActions(state);
            // System.out.println("Later than getAction()");

            // We see if it is the max or min of the function
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

                    // Recursive call of minmax to decide the bestAction
                    Action currentAction = minMax(depth - 1, newState, false);
                    int val = currentAction.getScore();
                    bestScore = Math.max(bestScore, val);

                    // Compare to earlier scores
                    if (val >= bestScore) {
                        bestAction = action;
                    }
                }

                bestAction.setScore(bestScore);
                // System.out.println("MinMax return: "+bestAction);

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

                    // Recursive call of minmax to decide the bestAction
                    Action currentAction = minMax(depth - 1, newState, true);
                    int val = currentAction.getScore();
                    bestScore = Math.min(bestScore, val);

                    // Compare to earlier scores
                    if (val <= bestScore) {
                        bestAction = action;
                    }
                }
                bestAction.setScore(bestScore);
                return bestAction;
            }
        }

        /*
         * ********************************************************************************************
         * * getActions                                                                               *
         * ********************************************************************************************
         * */
        // This method generates all of posible actions of the AI
        public List<Action> getActions(GameState state) {
            List<Action> actions = new ArrayList<Action>();

            // An Action for the move
            Action noCambio = new Action(agentPokemons.get(currentAgentPokemonIndex), false);
            noCambio.setScore(state.evaluate(state));
            actions.add(noCambio);

            // The Action to change
            for (pokemon pokemon : agentPokemons) {
                // We cannot change to the same pokemon
                if (pokemon != agentPokemons.get(currentAgentPokemonIndex)) {
                    Action changePokemon = new Action(pokemon, true);
                    changePokemon.setScore(state.evaluate(state));
                    actions.add(changePokemon);
                }
            }

            return actions;
        }

        /*
         * ********************************************************************************************
         * * doAction                                                                                 *
         * ********************************************************************************************
         * */
        // In this method we decide what to do, attack or change
        public GameState doAction(Action action) throws CloneNotSupportedException {
            GameState newState = clone();

            if (action.getIsSwitch() && permitChange) {

                int playerTypeIndex = newState.getPlayerPokemonIndex();
                String playerType = newState.getPlayerPokemons().get(playerTypeIndex).getType().trim();

                int bestPokemonIndex = -1;
                double bestTypeEffectiveness = MIN;

                // Change of the AI Pokemon
                for (int i = 0; i < newState.getAgentPokemons().size(); i++) {

                    String currentAgentPokemonType = newState.getAgentPokemons().get(i).getType().trim();
                    double typeEffectiveness = activity_Fight.atackEfective(currentAgentPokemonType, playerType, newState.getAgentPokemons().get(i).getMovements().get(1).toString());
                    typeEffectiveness = typeEffectiveness * 100;

                    int evaluate = newState.evaluate(newState);

                    // If both type are equal, the AI type and player type
                    if (evaluate == 0) {
                        evaluate = 1;
                    }

                    typeEffectiveness = evaluate + typeEffectiveness;

                    // Compare to see the best type to change
                    if (typeEffectiveness > bestTypeEffectiveness) {
                        bestTypeEffectiveness = typeEffectiveness;
                        bestPokemonIndex = i;
                    }
                }

                // Change to the most efficiently pokemon
                if (bestPokemonIndex != -1 && bestPokemonIndex != newState.getAgentPokemonIndex()) {
                    setChangeAgentPokemonIndex(bestPokemonIndex);

                } else if (bestPokemonIndex == newState.getAgentPokemonIndex()) {
                    // If results the best pokemon is already in battle decide to attack
                    action.setSwitch(false);
                    getBestMove(action);
                }

            } else {
                // If results the best pokemon is already in battle decide to attack
                action.setSwitch(false);
                getBestMove(action);
            }

            return newState;
        }

        /*
         * ********************************************************************************************
         * * getBestMove                                                                              *
         * ********************************************************************************************
         * */
        // Function who return the best move for pokemon
        private void getBestMove(Action action) {

            double firstAttack = activity_Fight.atackEfective(agentPokemons.get(getAgentPokemonIndex()).getType(), playerPokemons.get(getPlayerPokemonIndex()).getType(), agentPokemons.get(getAgentPokemonIndex()).getMovements().get(0).toString());
            double secondAttack = activity_Fight.atackEfective(agentPokemons.get(getAgentPokemonIndex()).getType(), playerPokemons.get(getPlayerPokemonIndex()).getType(), agentPokemons.get(getAgentPokemonIndex()).getMovements().get(1).toString());

            // System.out.println("FirstAttack: " + firstAttack);
            // System.out.println("secondAttack: " + secondAttack);

            if (firstAttack > secondAttack) {
                action.setAttack(0);

            } else {
                action.setAttack(1);
            }
        }

        /*
         * ********************************************************************************************
         * * clone                                                                                    *
         * ********************************************************************************************
         * */
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

                clonedState.currentAgentPokemonIndex = this.currentAgentPokemonIndex;
                clonedState.currentPlayerPokemonIndex = this.currentPlayerPokemonIndex;

            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                return null;
            }

            return clonedState;
        }

        /*
         * ********************************************************************************************
         * * Getter and Setter                                                                        *
         * ********************************************************************************************
         * */
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

    /*
     * ********************************************************************************************
     * * Action                                                                                   *
     * ********************************************************************************************
     * */
    //Class who contains the action of the pokemon who is in the battle
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

        /*
         * ********************************************************************************************
         * * Getter and Setter                                                                        *
         * ********************************************************************************************
         * */

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