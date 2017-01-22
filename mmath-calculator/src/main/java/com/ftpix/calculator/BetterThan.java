package com.ftpix.calculator;

import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.sherdogparser.models.SherdogBaseObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by gz on 18-Sep-16.
 */
public class BetterThan {
    private Logger logger = LogManager.getLogger();
    private Map<String, MmathFighter> fighterCache;


    public BetterThan(Map<String, MmathFighter> fighterCache) {
        this.fighterCache = fighterCache;
    }

    public List<MmathFighter> find(String fighter1, String fighter2) {
        Calculator calc = new Calculator(fighter1, fighter2);
        return calc.process();
    }


    private final class Calculator {

        private String fighter1, fighter2;
        private int maxDepth = 100;

        private List<List<String>> chains;

        private Set<String> checked = new HashSet<>();

        private int depth = 1;
        private List<String> validChain = null;

        public Calculator(String fighter1, String fighter2) {
            this.fighter1 = fighter1;
            this.fighter2 = fighter2;
        }

        public List<MmathFighter> process() {
            List<String> chain = new ArrayList<String>();

            chain.add(fighter1);

            return innerProcess2(chain);
        }


        private List<MmathFighter> innerProcess2(List<String> chain) {


            chains = new ArrayList<>();
            chains.add(chain);

            while (validChain == null && depth < maxDepth) {
                logger.info("Checking depth {}", depth);
                List<List<String>> tmpChains = new ArrayList<>();
                chains.parallelStream().forEach(c -> {
                    if (c.size() == depth) {
                        String toCheck = c.get(c.size() - 1);

                        Optional<Fighter> opt = Optional.ofNullable(fighterCache.get(toCheck));
                        //Optional<Fighter> opt = dao.get(toCheck);

                        if (opt.isPresent()) {
                            Fighter fighter = opt.get();
                            logger.info("Checking fighter {}", fighter.getName());


                            Collections.reverse(fighter.getFights());
                            //Getting all the eligible to continue fighters
                            List<SherdogBaseObject> fighters = fighter.getFights().parallelStream()
                                    .filter(f -> {
                                        return f.getResult() == FightResult.FIGHTER_1_WIN && !checked.contains(f.getFighter2().getSherdogUrl()) && !c.contains(f.getFighter2().getSherdogUrl());
                                    })
                                    .map(f -> f.getFighter2())
                                    .collect(Collectors.toList());


                            //Checking if we have a winner
                            Optional<SherdogBaseObject> optFighter = fighters.parallelStream()
                                    .filter(f -> {
                                        checked.add(f.getSherdogUrl());
                                        logger.info("DEPTH: {} Checking {} against {}", depth, f.getSherdogUrl(), fighter2);
                                        return f.getSherdogUrl().equalsIgnoreCase(fighter2);
                                    })
                                    .findFirst();

                            //We have a winner
                            if (optFighter.isPresent()) {
                                logger.info("Fighter 2 found !");
                                c.add(optFighter.get().getSherdogUrl());
                                validChain = c;
                            } else {

                                //If no winner, we save all the chains and go a level deeper
                                // we create create a new chain for each win, add it to the tmp list that will replace the one we're going to parse
                                fighters.forEach(f -> {
                                    List<String> newChain = new ArrayList<String>(c);
                                    newChain.add(f.getSherdogUrl());
                                    tmpChains.add(newChain);
                                });
                            }
                        }

                    }
                });

                logger.info("Checked everything on depth {}, we now have {} to check for depth {}", depth, tmpChains.size(), depth + 1);
                depth++;
                chains = tmpChains;

            }

            if (validChain != null) {
                return validChain.stream().map(s -> new MmathFighter(fighterCache.get(s))).peek(f-> f.setFights(new ArrayList<>())).collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        }

    }
}
