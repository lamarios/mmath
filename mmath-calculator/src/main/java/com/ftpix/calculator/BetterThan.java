package com.ftpix.calculator;

import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.models.Fight;
import com.ftpix.sherdogparser.models.FightResult;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.Where;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by gz on 18-Sep-16.
 */
public class BetterThan {
    private Logger logger = LogManager.getLogger();

    private final Dao<MmathFight, Long> fightDao;
    private final Dao<MmathFighter, String> fighterDao;

    public BetterThan(Dao<MmathFight, Long> fightDao, Dao<MmathFighter, String> fighterDao) {
        this.fightDao = fightDao;
        this.fighterDao = fighterDao;
    }

    public List<MmathFighter> find(MmathFighter fighter1, MmathFighter fighter2) throws SQLException {
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

        public List<MmathFighter> process() throws SQLException {
            logger.info("Is [{}] better than [{}] ?", fighter1.getName(), fighter2.getName());

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
                    logger.info("Current:{}, target: {}", current.getFighter().getSherdogUrl(), fighter2.getSherdogUrl());

                    checked.add(current.getFighter().getSherdogUrl());

                    //let's try to exit as soon as possible
                    if (current.getFighter().getSherdogUrl().equalsIgnoreCase(fighter2.getSherdogUrl())) {
                        logger.info("We found our fighter !");
                        targetNode = Optional.of(current);
                    } else {


                        getWinningFight(current.getFighter())
                                .stream()
                                //skipping fighters that we already checked to avoid infinite loops.
                                .filter(f ->  !checked.contains(f.getFighter2().getSherdogUrl()))
                                //sorting by most recent fights, might be faster as people most likely to search by recent fighters
                                //.sorted(Comparator.comparing(Fight::getDate).reversed())
                                .forEach(f -> {
                                        MmathFighter fighter = f.getFighter2();
                                            TreeNode fighterNode = new TreeNode(fighter, current);
                                            //Let's try to get out ASAP, it'll save some processing
                                            if (fighter.getSherdogUrl().equalsIgnoreCase(fighter2.getSherdogUrl())) {
                                                targetNode = Optional.of(fighterNode);
                                            } else {
                                                queue.add(fighterNode);
                                            }
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


        /**
         * Get the list of winning fights for a specified fighter
         *
         * @param fighter
         * @return
         */
        private List<MmathFight> getWinningFight(MmathFighter fighter) throws SQLException {
            Where<MmathFight, Long> where = fightDao.queryBuilder().where();
            PreparedQuery<MmathFight> prepare = where.or(
                    where
                        .eq("fighter1_id", fighter.getSherdogUrl())
                        .and()
                        .eq("result", FightResult.FIGHTER_1_WIN)
                    ,

                    where
                        .eq("fighter2_id", fighter.getSherdogUrl())
                        .and()
                        .eq("result", FightResult.FIGHTER_2_WIN)
            ).prepare();

            //Rearaning fights before using it,
            // if the winning fighter is fighter 2 put it to fighter 1
            List<MmathFight> results  = fightDao.query(prepare);
            return results
                    .stream()
                    .filter(f -> f.getFighter1() != null && f.getFighter2() != null)
                    .map(f -> {
                        //our fighter is fighter2, swapping
                        try {
                            if (fighter.getSherdogUrl().equalsIgnoreCase(f.getFighter2().getSherdogUrl())) {
                                f.setFighter2(f.getFighter1());
                                f.setFighter1(fighter);
                                f.setResult(FightResult.FIGHTER_1_WIN);
                            }

                        }catch (NullPointerException e){
                            throw e;
                        }

                        return f;
                    }).collect(Collectors.toList());

        }
    }


}
