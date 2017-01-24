package com.ftpix.calculator;

import com.ftpix.mmath.dao.FighterDao;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.sherdogparser.models.FightResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

/**
 * Created by gz on 18-Sep-16.
 */
public class BetterThan {
    private Logger logger = LogManager.getLogger();
    private Map<String, MmathFighter> fighterCache;

private final FighterDao fighterDao;

    public BetterThan(Map<String, MmathFighter> fighterCache, FighterDao fighterDao) {
        this.fighterCache = fighterCache;
        this.fighterDao = fighterDao;
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
            logger.info("Is [{}] better than [{}] ?", fighter1, fighter2);


            List<MmathFighter> result = new ArrayList<>();

            //if the target as no losses then no point stressing my little server
            MmathFighter target = fighterCache.get(fighter2);
            if(target.getLosses() == 0){
                return result;
            }

            Queue<TreeNode> queue = new LinkedList<>();

            queue.add(new TreeNode(fighterCache.get(fighter1)));

            Optional<TreeNode> search = Optional.empty();


            while (!queue.isEmpty() && !search.isPresent()) {
                TreeNode current = queue.remove();

                if (current != null && current.getFighter() != null && !checked.contains(current.getFighter().getSherdogUrl())) {
                    logger.info("Current:{}, target: {}", current.getFighter().getSherdogUrl(), fighter2);

                    checked.add(current.getFighter().getSherdogUrl());

                    //let's try to exit As soon as possible
                    if (current.getFighter().getSherdogUrl().equalsIgnoreCase(fighter2)) {
                        logger.info("We found our fighter !");
                        search = Optional.of(current);
                    } else {

                        current.getFighter().getFights().stream()
                                .filter(f -> f.getResult().equals(FightResult.FIGHTER_1_WIN) && !checked.contains(f.getFighter2().getSherdogUrl()))
                                //sorting by most recent fights, might be faster as people most likely to search by recent fighters
                                .sorted(Comparator.comparing(Fight::getDate).reversed())
                                .forEach(f -> {
                                    Optional.ofNullable(fighterCache.get(f.getFighter2().getSherdogUrl())).ifPresent(fighter ->{
                                        queue.add(new TreeNode(fighter, current));
                                    });
                                });
                        logger.info("Not found yet, queue size: {}", queue.size());
                    }
                }
            }

            logger.info("Done 1");

            search.ifPresent(treeNode -> {
                //building back the list
                TreeNode node = treeNode;

                do{
                    result.add(0, node.getFighter());
                    node = node.getParent();
                }while(node.getParent() != null);
                result.add(0, node.fighter);
            });

            logger.info("Done !");
            result.stream().forEach(s -> logger.info("Result item: {}", s.getName()));

            return result;
        }

        private class TreeNode {
            private MmathFighter fighter;
            private TreeNode parent;

            public TreeNode(MmathFighter fighter) {
                this.fighter = fighter;
            }

            public TreeNode(MmathFighter fighter, TreeNode parent) {
                this.fighter = fighter;
                this.parent = parent;
            }

            public MmathFighter getFighter() {
                return fighter;
            }

            public TreeNode getParent() {
                return parent;
            }
        }

        /* private List<MmathFighter> innerProcess2(List<String> chain) {


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
        } */

    }
}
