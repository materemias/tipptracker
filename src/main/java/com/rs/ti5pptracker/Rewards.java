package com.rs.ti5pptracker;

import java.util.Map;
import java.util.TreeMap;

public class Rewards {
    public static TreeMap<Integer, String> LIST;
    static{
        LIST = new TreeMap<>();
        LIST.put(1600000, "COMPENDIUM COINS");
        LIST.put(2000000, "CURSOR PACK");
        LIST.put(2500000, "ALL STAR VOTE");
        LIST.put(3000000, "IMMORTAL TREASURE I");
        LIST.put(3500000, "ARCANA VOTE");
        LIST.put(4000000, "COMPENDIUM EFFIGIES");
        LIST.put(4500000, "LOADING SCREENS");
        LIST.put(5000000, "EMOTICONS");
        LIST.put(5500000, "THE INTERNATIONAL HUD SKIN");
        LIST.put(6000000, "TAUNT TREASURE");
        LIST.put(6500000, "THE WATCHER BELOW WARD");
        LIST.put(7000000, "IMMORTAL TREASURE II");
        LIST.put(8000000, "DOTA 2 SHORT FILM CONTEST");
        LIST.put(9000000, "WYVERN HATCHLING COURIER");
        LIST.put(10000000, "IMMORTAL TREASURE III");
        LIST.put(11000000, "DESERT TERRAIN");
        LIST.put(12000000, "MUSIC PACK");
        LIST.put(13000000, "ANNOUNCER PACK");
        LIST.put(14000000, "NEW WEATHER EFFECTS");
        LIST.put(15000000, "SPECIAL AXE IMMORTAL & LONGFORM COMIC");
    }

    public static class Reward{
        int money;
        String description;

        public Reward(int money, String description) {
            this.money = money;
            this.description = description;
        }
    }

    public static Reward[] getLastAndNext(int base){
        Reward lastReward = null;
        for(Map.Entry<Integer, String> entry : LIST.entrySet()) {
            Integer key = entry.getKey();
            if (base < key){
                return new Reward[] {lastReward, new Reward(entry.getKey(), entry.getValue())};
            }
            lastReward = new Reward(entry.getKey(), entry.getValue());
        }
        return null;
    }
}
