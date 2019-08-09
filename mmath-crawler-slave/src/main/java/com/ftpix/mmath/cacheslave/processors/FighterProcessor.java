package com.ftpix.mmath.cacheslave.processors;

import com.ftpix.mmath.dao.mysql.*;
import com.ftpix.mmath.model.MmathFight;
import com.ftpix.mmath.model.MmathFighter;
import com.ftpix.sherdogparser.Sherdog;
import com.ftpix.sherdogparser.exceptions.SherdogParserException;
import com.ftpix.sherdogparser.models.Fighter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by gz on 16-Sep-16.
 */
@Component
public class FighterProcessor extends Processor<MmathFighter> {



    @Autowired
    private OrganizationDAO organizationDAO;

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private FightDAO fightDAO;


    @Autowired
    private FighterDAO fighterDAO;

    @Autowired
    private StatsEntryDAO statsEntryDAO;

    @Autowired
    private StatsCategoryDAO statsCategoryDAO;


    @Override
    protected void propagate(MmathFighter obj) {
        obj.getFights().forEach(f -> {
            jmsTemplate.convertAndSend(fighterTopic, f.getFighter2().getSherdogUrl());
            jmsTemplate.convertAndSend(eventTopic, f.getEvent().getSherdogUrl());
        });

    }

    @Override
    protected void insertToDao(MmathFighter obj) throws SQLException {
        try {
            fighterDAO.insert(obj);
        }catch (DuplicateKeyException e){
            logger.info("Fighter already exist, skipping insert");
        }
    }

    @Override
    protected void updateToDao(MmathFighter old, MmathFighter fromSherdog) throws SQLException {
        fromSherdog.setSearchRank(old.getSearchRank());
        fighterDAO.update(fromSherdog);
    }


    @Override
    protected MmathFighter getFromSherdog(String url) throws IOException, ParseException, SherdogParserException {
        Fighter fighter = sherdog.getFighter(url);

        MmathFighter mmathFighter = MmathFighter.fromSherdong(fighter);
        mmathFighter.setFights(fighter.getFights().stream().map(MmathFight::fromSherdog).collect(Collectors.toList()));

        return mmathFighter;
    }

    @Override
    protected LocalDateTime getLastUpdate(MmathFighter obj) {
        return obj.getLastUpdate();
    }

    @Override
    protected Optional<MmathFighter> getFromDao(String url) throws SQLException {
        MmathFighter mmathFighter = fighterDAO.getById(url);
        return Optional.ofNullable(mmathFighter);
    }
}
