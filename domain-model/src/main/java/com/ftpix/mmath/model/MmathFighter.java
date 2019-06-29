package com.ftpix.mmath.model;

import com.ftpix.sherdogparser.models.Fighter;
import com.ftpix.utils.DateUtils;
import com.google.gson.annotations.Expose;
import io.gsonfire.annotations.ExposeMethodResult;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by gz on 24-Sep-16.
 */
public class MmathFighter {


    private String sherdogUrl;


    private LocalDateTime lastUpdate = LocalDateTime.now();


    public List<MmathFight> fights;

    @Expose
    private String name;
    @Expose
    private LocalDate birthday;
    @Expose
    private int draws;
    @Expose
    private int losses;
    @Expose
    private int wins;
    @Expose
    private String weight;
    @Expose
    private String height;
    @Expose
    private String nickname;
    @Expose
    private int nc;


    @Expose
    private int winKo, winSub, winDec, lossKo, lossDec, lossSub;


    private int searchRank;

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getLosses() {
        return losses;
    }

    public int getWins() {
        return wins;
    }

    public void setSherdogUrl(String sherdogUrl) {
        this.sherdogUrl = sherdogUrl;
    }

    public String getSherdogUrl() {
        return sherdogUrl;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }


    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getDraws() {
        return draws;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWeight() {
        return weight;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getHeight() {
        return height;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNc(int nc) {
        this.nc = nc;
    }

    public int getNc() {
        return nc;
    }


    public int getWinKo() {
        return winKo;
    }

    public void setWinKo(int winKo) {
        this.winKo = winKo;
    }

    public int getWinSub() {
        return winSub;
    }

    public void setWinSub(int winSub) {
        this.winSub = winSub;
    }

    public int getWinDec() {
        return winDec;
    }

    public void setWinDec(int winDec) {
        this.winDec = winDec;
    }

    public int getLossKo() {
        return lossKo;
    }

    public void setLossKo(int lossKo) {
        this.lossKo = lossKo;
    }

    public int getLossDec() {
        return lossDec;
    }

    public void setLossDec(int lossDec) {
        this.lossDec = lossDec;
    }

    public int getLossSub() {
        return lossSub;
    }

    public void setLossSub(int lossSub) {
        this.lossSub = lossSub;
    }

    @ExposeMethodResult("record")
    public String getRecord() {
        StringBuilder sb = new StringBuilder();
        sb.append(wins).append("-").append(losses);

        if (draws == 0 && nc > 0) {
            sb.append("-0-" + nc);
        } else if (draws > 0) {
            sb.append("-");
            sb.append(draws);

            if (nc > 0) {
                sb.append("-");
                sb.append(nc);
            }
        }

        return sb.toString();
    }

    @ExposeMethodResult("id")
    public String getIdAsHash() {
        return DigestUtils.md5Hex(getSherdogUrl());
    }

    public static MmathFighter fromSherdong(Fighter f) {
        MmathFighter fighter = new MmathFighter();

        fighter.setSherdogUrl(Utils.cleanUrl(f.getSherdogUrl()));
        Optional.ofNullable(f.getBirthday()).ifPresent(bd -> {
            fighter.setBirthday(DateUtils.toLocalDate(bd));
        });
        fighter.setDraws(f.getDraws());
        fighter.setLosses(f.getLosses());
        fighter.setWins(f.getWins());
        fighter.setWeight(f.getWeight());
        fighter.setHeight(f.getHeight());
        fighter.setName(f.getName());
        fighter.setNickname(f.getNickname());
        fighter.setNc(f.getNc());
        fighter.setWinKo(f.getWinsKo());
        fighter.setWinDec(f.getWinsDec());
        fighter.setWinSub(f.getWinsSub());
        fighter.setLossKo(f.getLossesKo());
        fighter.setLossSub(f.getLossesSub());
        fighter.setLossDec(f.getLossesDec());

        return fighter;
    }

    public List<MmathFight> getFights() {
        return fights;
    }

    public void setFights(List<MmathFight> fights) {
        this.fights = fights;
    }


    public List<GsonFriendlyFight> getGsonFriendlyFights() {
        return fights.stream().map(GsonFriendlyFight::new).collect(Collectors.toList());
    }

    public int getSearchRank() {
        return searchRank;
    }

    public void setSearchRank(int searchRank) {
        this.searchRank = searchRank;
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(sherdogUrl, ((MmathFighter) obj).getSherdogUrl());
    }

}
