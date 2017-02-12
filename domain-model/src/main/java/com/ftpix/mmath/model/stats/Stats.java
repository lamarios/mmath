package com.ftpix.mmath.model.stats;

import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by gz on 12-Feb-17.
 */
public class Stats {
    private String id;
    private int count = 0;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Generate a fight ID by sorting the ids and concatenating them
     * @param id1
     * @param id2
     * @return
     */
    public static String generateFightId(String id1, String id2) {
        TreeSet<String> set = new TreeSet<>();
        set.add(id1.toLowerCase());
        set.add(id2.toLowerCase());
        return set.stream().collect(Collectors.joining("-"));
    }
}
