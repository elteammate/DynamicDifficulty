package me.skinnyjeans.gmd.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Difficulty {
    private String difficultyName;
    private String userPrefix;
    private int affinityRequirement;
    private int damageDoneByMobs;
    private int damageDoneOnMobs;
    private int damageDoneOnTamed;
    private int experienceMultiplier;
    private int hungerDrainChance;
    private int doubleLootChance;
    private int maxEnchants;
    private int maxEnchantLevel;
    private int difficultyUntil;
    private int damageByRangedMobs;
    private int doubleDurabilityDamageChance;
    private int minimumStarvationHealth;
    private int maximumHealth;
    private double chanceCancelDeath;
    private double armorDropChance;
    private double chanceToEnchant = Double.NaN;
    private double chanceToHaveArmor;
    private double chanceToHaveWeapon;
    private double weaponDropChance;
    private boolean allowPVP;
    private boolean keepInventory;
    private boolean allowHealthRegen;
    private boolean effectsWhenAttacked;
    private List<String> disabledCommands = new ArrayList<>();
    private List<String> mobsIgnoredPlayers = new ArrayList<>();
    private List<String> commandsOnJoin = new ArrayList<>();
    private List<String> commandsOnSwitchFromPrev = new ArrayList<>();
    private List<String> commandsOnSwitchFromNext = new ArrayList<>();
    private List<MythicMobProfile> mythicMobProfiles = new ArrayList<>();
    private HashMap<EquipmentItems, Double> armorChance = new HashMap<>();
    private HashMap<ArmorTypes, Integer> armorDamageMultipliers = new HashMap<>();

    public Difficulty(String name) { difficultyName = name; }

    public String getDifficultyName() { return difficultyName; }
    public String getPrefix() { return userPrefix; }
    public int getAffinity() { return affinityRequirement; }
    public int getUntil() { return difficultyUntil; }
    public int getDamageByMobs() { return damageDoneByMobs; }
    public int getDamageOnMobs() { return damageDoneOnMobs; }
    public int getDamageOnTamed() { return damageDoneOnTamed; }
    public int getExperienceMultiplier() { return experienceMultiplier; }
    public int getHungerDrain() { return hungerDrainChance; }
    public int getDoubleLoot() { return doubleLootChance; }
    public int getMaxEnchants() { return maxEnchants; }
    public int getMaxEnchantLevel() { return maxEnchantLevel; }
    public int getDamageByRangedMobs() { return damageByRangedMobs; }
    public int getDoubleDurabilityDamageChance() { return doubleDurabilityDamageChance; }
    public int getMinimumStarvationHealth() { return minimumStarvationHealth; }
    public int getMaximumHealth() { return maximumHealth; }
    public int getArmorDamageMultiplier(ArmorTypes type) { return armorDamageMultipliers.getOrDefault(type, -505); }
    public HashMap<ArmorTypes, Integer> getArmorDamageMultiplier() { return armorDamageMultipliers; }
    public HashMap<EquipmentItems, Double> getEnchantChances() { return armorChance; }
    public boolean getAllowPVP() { return allowPVP; }
    public boolean getKeepInventory() { return keepInventory; }
    public boolean getAllowHealthRegen() { return allowHealthRegen; }
    public boolean getEffectsOnAttack() { return effectsWhenAttacked; }
    public List<String> getIgnoredMobs() { return mobsIgnoredPlayers; }
    public List<String> getDisabledCommands() { return disabledCommands; }
    public List<MythicMobProfile> getMythicMobProfiles() { return mythicMobProfiles; }
    public double getEnchantChance(EquipmentItems type) { return armorChance.get(type); }
    public double getArmorDropChance() { return armorDropChance; }
    public double getChanceToEnchant() { return chanceToEnchant; }
    public double getChanceToHaveArmor() { return chanceToHaveArmor; }
    public double getChanceToHaveWeapon() { return chanceToHaveWeapon; }
    public double getWeaponDropChance() { return weaponDropChance; }
    public double getChanceToCancelDeath() { return chanceCancelDeath; }
    public List<String> getCommandsOnJoin() { return commandsOnJoin; }
    public List<String> getCommandsOnSwitchFromPrev() { return commandsOnSwitchFromPrev; }
    public List<String> getCommandsOnSwitchFromNext() { return commandsOnSwitchFromNext; }

    public void setPrefix(String value) { userPrefix = value; }
    public void setAffinity(int value) { affinityRequirement = value; }
    public void setUntil(int value) { difficultyUntil = value; }
    public void setDamageByMobs(int value) { damageDoneByMobs = value; }
    public void setDamageOnMobs(int value) { damageDoneOnMobs = value; }
    public void setDamageOnTamed(int value) { damageDoneOnTamed = value; }
    public void setExperienceMultiplier(int value) { experienceMultiplier = value; }
    public void setHungerDrain(int value) { hungerDrainChance = value; }
    public void setDoubleLoot(int value) { doubleLootChance = value; }
    public void setMaxEnchants(int value) { maxEnchants = value; }
    public void setMaxEnchantLevel(int value) { maxEnchantLevel = value; }
    public void setDamageByRangedMobs(int value) { damageByRangedMobs = value; }
    public void setDoubleDurabilityDamageChance(int value) { doubleDurabilityDamageChance = value; }
    public void setMinimumStarvationHealth(int value) { minimumStarvationHealth = value; }
    public void setMaximumHealth(int value) { maximumHealth = value; }
    public void setArmorDamageMultiplier(HashMap<ArmorTypes, Integer> value) { armorDamageMultipliers = value; }
    public void setAllowPVP(boolean value) { allowPVP = value; }
    public void setKeepInventory(boolean value) { keepInventory = value; }
    public void setEffectsOnAttack(boolean value) { effectsWhenAttacked = value; }
    public void setAllowHealthRegen(boolean value) { allowHealthRegen = value; }

    public void setMythicMobProfiles(List<MythicMobProfile> value) { mythicMobProfiles = value; }
    public void setDisabledCommands(List<String> value) { disabledCommands = value; }
    public void setIgnoredMobs(List<String> value) { mobsIgnoredPlayers = value; }
    public void setEnchantChances(HashMap<EquipmentItems, Double> value) { armorChance = value; }
    public void setArmorDropChance(Double value) { armorDropChance = value; }
    public void setWeaponDropChance(Double value) { weaponDropChance = value; }
    public void setChanceToEnchant(Double value) { chanceToEnchant = value; }
    public void setChanceToHaveArmor(Double value) { chanceToHaveArmor = value; }
    public void setChanceToHaveWeapon(Double value) { chanceToHaveWeapon = value; }
    public void setChanceToCancelDeath(Double value) { chanceCancelDeath = value; }
    public void setCommandsOnJoin(List<String> value) { commandsOnJoin = value; }
    public void setCommandsOnSwitchFromPrevious(List<String> value) { commandsOnSwitchFromPrev = value; }
    public void setCommandsOnSwitchFromNext(List<String> value) { commandsOnSwitchFromNext = value; }
}
