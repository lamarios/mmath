package com.ftpix.calculator;

import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.mmath.model.MmathModel;
import com.ftpix.sherdogparser.models.FightResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
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


    public BetterThan(Map<String, MmathFighter> fighterCache) {
        this.fighterCache = fighterCache;
    }

    public List<MmathFighter> find(MmathFighter fighter1, MmathFighter fighter2) {
        Calculator calc = new Calculator(fighter1, fighter2);
        return calc.process();
    }


    private final class Calculator {

        private MmathFighter fighter1, fighter2;
        private int maxDepth = 100;

        private List<List<String>> chains;

        private Set<String> checked = new HashSet<>();

        private int depth = 1;
        private List<String> validChain = null;

        Optional<TreeNode> targetNode = Optional.empty();

        public Calculator(MmathFighter fighter1, MmathFighter fighter2) {
            this.fighter1 = fighter1;
            this.fighter2 = fighter2;
        }

        public List<MmathFighter> process() {
            logger.info("Is [{}] better than [{}] ?", fighter1, fighter2);

            long now = System.currentTimeMillis();

            List<MmathFighter> result = new ArrayList<>();

            //if the target as no losses then no point stressing my little server

            if (fighter2.getLosses() == 0) {
                return result;
            }

            Queue<TreeNode> queue = new LinkedList<>();

            queue.add(new TreeNode(fighter1));


            while (!queue.isEmpty() && !targetNode.isPresent()) {
                TreeNode current = queue.remove();

                if (current != null && current.getFighter() != null && !checked.contains(current.getFighter().getSherdogUrl())) {
                    logger.info("Current:{}, target: {}", current.getFighter().getSherdogUrl(), fighter2.getId());

                    checked.add(current.getFighter().getSherdogUrl());

                    //let's try to exit as soon as possible
                    if (current.getFighter().getId().equalsIgnoreCase(fighter2.getId())) {
                        logger.info("We found our fighter !");
                        targetNode = Optional.of(current);
                    } else {

                        current.getFighter().getFights().stream()
                                //skipping fighters that we already checked to avoid infinite loops.
                                .filter(f -> f.getResult().equals(FightResult.FIGHTER_1_WIN) && !checked.contains(f.getFighter2().getSherdogUrl()))
                                //sorting by most recent fights, might be faster as people most likely to search by recent fighters
                                //.sorted(Comparator.comparing(Fight::getDate).reversed())
                                .forEach(f -> {
                                    Optional.ofNullable(fighterCache.get(MmathModel.generateId(f.getFighter2()))).ifPresent(fighter -> {
                                        TreeNode fighterNode = new TreeNode(fighter, current);
                                        //Let's try to get out ASAP, it'll save some processing
                                        if (fighter.getId().equalsIgnoreCase(fighter2.getId())) {
                                            targetNode = Optional.of(fighterNode);
                                        } else {
                                            queue.add(fighterNode);
                                        }
                                    });
                                });
                        logger.info("Not found yet, queue size: {}", queue.size());
                    }
                }
            }

            targetNode.ifPresent(treeNode -> {
                //building back the list
                TreeNode node = treeNode;

                do {
                    result.add(0, node.getFighter());
                    node = node.getParent();
                } while (node.getParent() != null);
                result.add(0, node.fighter);
            });

            logger.info("Request completed in {}ms", System.currentTimeMillis() - now);
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

    }
}
