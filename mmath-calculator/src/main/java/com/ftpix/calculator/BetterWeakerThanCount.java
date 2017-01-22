package com.ftpix.calculator;

import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.models.FightResult;
import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.sherdogparser.models.SherdogBaseObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by gz on 24-Sep-16.
 */
public class BetterWeakerThanCount {

    private Logger logger = LogManager.getLogger();
    private Map<String, MmathFighter> fighterCache;


    public BetterWeakerThanCount(Map<String, MmathFighter> fighterCache) {
        this.fighterCache = fighterCache;
    }

    public long count(String fighter, Type type) {
        return new Calculator(fighter, type).count();
    }


    private class Calculator {

        private String fighter;
        private Set<String> checked = Collections.synchronizedSet(new HashSet<>());
        private FightResult resultType;


        public Calculator(String fighter, Type type) {
            this.fighter = fighter;
            switch (type) {
                case BETTER_THAN:
                    resultType = FightResult.FIGHTER_1_WIN;
                    break;
                case WEAKER_THAN:
                    resultType = FightResult.FIGHTER_2_WIN;
                    break;
            }
        }


        public long count() {
            Fighter f = fighterCache.get(fighter);

            return innerProcess(f);
        }


        private long innerProcess(SherdogBaseObject sherdogfighter) {
            long count = 0;
            long currentTime = System.currentTimeMillis();

            Optional<Fighter> optFighter = Optional.ofNullable((fighterCache.get(sherdogfighter.getSherdogUrl())));

            if (optFighter.isPresent()) {

                Fighter fighter = optFighter.get();

                List<SherdogBaseObject> fighters = fighter.getFights().parallelStream()
                        .filter(f -> f.getResult() == resultType && !checked.contains(f.getFighter2().getSherdogUrl()))
                        .map(f -> f.getFighter2())
                        .peek(f -> checked.add(f.getSherdogUrl()))
                        .collect(Collectors.toList());

                count += fighters.size();

                count += fighters.parallelStream()
                        .mapToLong(f -> innerProcess(f))
                        .sum();


                logger.info("{} count = {} in {}ms", fighter.getName(), count, (System.currentTimeMillis() - currentTime));
                return count;
            } else {
                return 0;
            }

        }
    }

    public enum Type {
        BETTER_THAN,
        WEAKER_THAN
    }
}
