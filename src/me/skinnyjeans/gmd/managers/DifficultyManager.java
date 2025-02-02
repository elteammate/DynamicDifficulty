package me.skinnyjeans.gmd.managers;

import me.skinnyjeans.gmd.models.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class DifficultyManager {

    private final MainManager MAIN_MANAGER;

    private final HashMap<UUID, Difficulty> PLAYER_LIST = new HashMap<>();
    private final HashMap<String, Difficulty> DIFFICULTY_LIST = new HashMap<>();
    private final ArrayList<String> DIFFICULTY_LIST_SORTED = new ArrayList<>();

    private DifficultyTypes DifficultyType;

    public DifficultyManager(MainManager mainManager) {
        MAIN_MANAGER = mainManager;

        if(MAIN_MANAGER.getDataManager().getConfig().getBoolean("toggle-settings.force-hard-difficulty", true))
            Bukkit.getWorlds().forEach(world -> {
                if(!MAIN_MANAGER.getDataManager().isWorldDisabled(world.getName()))
                    world.setDifficulty(org.bukkit.Difficulty.HARD);
            });

        Bukkit.getScheduler().runTaskTimerAsynchronously(MAIN_MANAGER.getPlugin(), this::calculateAllPlayers, 20 * 30, 20 * 120);
    }

    public ArrayList<Difficulty> getDifficulties() { return new ArrayList<>(DIFFICULTY_LIST.values()); }

    public ArrayList<String> getDifficultyNames() { return DIFFICULTY_LIST_SORTED; }

    public Difficulty getDifficulty(String name) { return DIFFICULTY_LIST.get(name); }

    public Difficulty getDifficulty(UUID uuid) { return PLAYER_LIST.getOrDefault(uuid, DIFFICULTY_LIST.get(DIFFICULTY_LIST_SORTED.get(0))); }

    public DifficultyTypes getType() { return DifficultyType; }

    public Difficulty calcDifficulty(int affinity) {
        for (String difficulty : DIFFICULTY_LIST_SORTED)
            if(affinity <= DIFFICULTY_LIST.get(difficulty).getUntil())
                return DIFFICULTY_LIST.get(difficulty);
        return DIFFICULTY_LIST.get(DIFFICULTY_LIST_SORTED.get(0));
    }

    public void calculateAllPlayers() {
        MAIN_MANAGER.getPlayerManager().getPlayerList().forEach((key, value) -> calculateDifficulty(key));
    }

    public String getProgress(UUID uuid) {
        int a = getDifficulty(getDifficulty(uuid).getDifficultyName()).getAffinity();
        int b = getNextDifficulty(uuid).getAffinity();

        if(a == b) return "100.0%";
        return Math.round(1000.0 * Math.abs(1.0 - (100.0 / (a - b) * (MAIN_MANAGER.getPlayerManager().getPlayerAffinity(uuid).getAffinity() - b)) / 100.0)) / 10.0 + "%";
    }

    public Difficulty getNextDifficulty(UUID uuid) {
        int index = DIFFICULTY_LIST_SORTED.indexOf(getDifficulty(uuid).getDifficultyName());
        if (index != DIFFICULTY_LIST_SORTED.size() - 1 || index == -1) index++;

        return DIFFICULTY_LIST.get(DIFFICULTY_LIST_SORTED.get(index));
    }

    public void calculateDifficulty(UUID uuid) {
        Difficulty oldDifficulty = getDifficulty(uuid);
        Difficulty difficulty = calculateDifficulty(MAIN_MANAGER.getPlayerManager().getPlayerAffinity(uuid));
        PLAYER_LIST.put(uuid, difficulty);

        handleDifficultyChange(uuid, oldDifficulty, difficulty);
    }

    private void handleDifficultyChange(UUID uuid, Difficulty oldDifficulty, Difficulty newDifficulty) {
        if (oldDifficulty.getDifficultyName().equals(newDifficulty.getDifficultyName())) return;

        int oldDifficultyIndex = DIFFICULTY_LIST_SORTED.indexOf(oldDifficulty.getDifficultyName());
        int newDifficultyIndex = DIFFICULTY_LIST_SORTED.indexOf(newDifficulty.getDifficultyName());

        boolean directionIsUp = oldDifficultyIndex < newDifficultyIndex;
        ArrayList<String> difficultiesInBetween = new ArrayList<>();

        if (directionIsUp) {
            for (int i = oldDifficultyIndex + 1; i <= newDifficultyIndex; i++)
                difficultiesInBetween.add(DIFFICULTY_LIST_SORTED.get(i));
        } else {
            for (int i = oldDifficultyIndex - 1; i >= newDifficultyIndex; i--)
                difficultiesInBetween.add(DIFFICULTY_LIST_SORTED.get(i));
        }

        ArrayList<String> allCommands = new ArrayList<>();

        for (String difficultyName : difficultiesInBetween) {
            Difficulty difficulty = DIFFICULTY_LIST.get(difficultyName);
            List<String> commands = directionIsUp
                    ? difficulty.getCommandsOnSwitchFromPrev()
                    : difficulty.getCommandsOnSwitchFromNext();

            allCommands.addAll(commands);
        }

        MAIN_MANAGER.getCommandManager().dispatchCommandsIfOnline(uuid, allCommands);
    }

    public Difficulty calculateDifficulty(Minecrafter affinity) {
        Difficulty first = calcDifficulty(affinity.getAffinity());
        Difficulty second = DIFFICULTY_LIST.get(getNextDifficulty(affinity.getUUID()).getDifficultyName());

        Difficulty difficulty = new Difficulty(first.getDifficultyName());

        int a = first.getAffinity();
        int b = second.getAffinity();
        double c = (a == b) ? 1.0 : Math.abs(1.0 - (100.0 / (a - b) * (affinity.getAffinity() - b)) / 100.0);

        difficulty.setDoubleLoot(calculatePercentage(first.getDoubleLoot(), second.getDoubleLoot(), c));
        difficulty.setHungerDrain(calculatePercentage(first.getHungerDrain(), second.getHungerDrain(), c));
        difficulty.setDamageByMobs(calculatePercentage(first.getDamageByMobs(), second.getDamageByMobs(), c));
        difficulty.setDamageOnMobs(calculatePercentage(first.getDamageOnMobs(), second.getDamageOnMobs(), c));
        difficulty.setDamageOnTamed(calculatePercentage(first.getDamageOnTamed(), second.getDamageOnTamed(), c));
        difficulty.setArmorDropChance(calculatePercentage(first.getArmorDropChance(), second.getArmorDropChance(), c));
        difficulty.setChanceToHaveArmor(calculatePercentage(first.getChanceToHaveArmor(), second.getChanceToHaveArmor(), c));
        difficulty.setChanceToHaveWeapon(calculatePercentage(first.getChanceToHaveWeapon(), second.getChanceToHaveWeapon(), c));
        difficulty.setDamageByRangedMobs(calculatePercentage(first.getDamageByRangedMobs(), second.getDamageByRangedMobs(), c));
        difficulty.setExperienceMultiplier(calculatePercentage(first.getExperienceMultiplier(), second.getExperienceMultiplier(), c));
        difficulty.setDoubleDurabilityDamageChance(calculatePercentage(first.getDoubleDurabilityDamageChance(), second.getDoubleDurabilityDamageChance(), c));
        difficulty.setMaxEnchants(calculatePercentage(first.getMaxEnchants(), second.getMaxEnchants(), c));
        difficulty.setMaxEnchantLevel(calculatePercentage(first.getMaxEnchantLevel(), second.getMaxEnchantLevel(), c));
        difficulty.setChanceToEnchant(calculatePercentage(first.getChanceToEnchant(), second.getChanceToEnchant(), c));
        difficulty.setWeaponDropChance(calculatePercentage(first.getWeaponDropChance(), second.getWeaponDropChance(), c));
        difficulty.setMinimumStarvationHealth(calculatePercentage(first.getMinimumStarvationHealth(), second.getMinimumStarvationHealth(), c));
        difficulty.setMaximumHealth(calculatePercentage(first.getMaximumHealth(), second.getMaximumHealth(), c));
        difficulty.setChanceToCancelDeath(calculatePercentage(first.getChanceToCancelDeath(), second.getChanceToCancelDeath(), c));
        HashMap<EquipmentItems, Double> equipmentValues = new HashMap<>();
        for(EquipmentItems item : EquipmentItems.values())
            equipmentValues.put(item, calculatePercentage(first.getEnchantChance(item), second.getEnchantChance(item), c));
        difficulty.setEnchantChances(equipmentValues);

        HashMap<ArmorTypes, Integer> armorValues = new HashMap<>();
        for(ArmorTypes item : ArmorTypes.values())
            armorValues.put(item, calculatePercentage(first.getArmorDamageMultiplier(item), second.getArmorDamageMultiplier(item), c));
        difficulty.setArmorDamageMultiplier(armorValues);

        difficulty.setMythicMobProfiles(first.getMythicMobProfiles());
        difficulty.setAllowHealthRegen(first.getAllowHealthRegen());
        difficulty.setPrefix(first.getPrefix());
        difficulty.setAllowPVP(first.getAllowPVP());
        difficulty.setKeepInventory(first.getKeepInventory());
        difficulty.setEffectsOnAttack(first.getEffectsOnAttack());
        difficulty.setDisabledCommands(first.getDisabledCommands());
        difficulty.setIgnoredMobs(first.getIgnoredMobs());
        difficulty.setCommandsOnSwitchFromNext(first.getCommandsOnSwitchFromNext());
        difficulty.setCommandsOnSwitchFromPrevious(first.getCommandsOnSwitchFromPrev());
        difficulty.setCommandsOnJoin(first.getCommandsOnJoin());


        try {
            if (Bukkit.getOfflinePlayer(affinity.getUUID()).isOnline())
                Bukkit.getPlayer(affinity.getUUID()).getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(difficulty.getMaximumHealth());
        } catch (Exception ignored) { }

        return difficulty;
    }

    private static int calculatePercentage(int value1, int value2, double percentage) {
        if(value1 == value2) return value1;
        return (int) Math.round(value1 - ((value1 - value2) * percentage));
    }

    private static double calculatePercentage(double value1, double value2, double percentage) {
        if(value1 == value2) return value1;
        return Math.round(value1 - ((value1 - value2) * percentage));
    }

    public void reloadConfig() {
        DIFFICULTY_LIST.clear();
        DIFFICULTY_LIST_SORTED.clear();

        String type = MAIN_MANAGER.getDataManager().getConfig().getString("toggle-settings.difficulty-type", "player");
        DifficultyType = DifficultyTypes.valueOf(type.substring(0, 1).toUpperCase() + type.substring(1));

        HashMap<Integer, String> tmpMap = new HashMap<>();
        for(String key : MAIN_MANAGER.getDataManager().getConfig().getConfigurationSection("difficulty").getKeys(false)) {
            ConfigurationSection config = MAIN_MANAGER.getDataManager().getConfig().getConfigurationSection("difficulty." + key);

            if(! config.getBoolean("enabled", true)) continue;
            Difficulty difficulty = new Difficulty(key.replace(" ", "_"));

            difficulty.setAffinity(config.getInt("affinity-required", 0));
            difficulty.setDamageByMobs(config.getInt("damage-done-by-mobs", 100));
            difficulty.setDamageOnMobs(config.getInt("damage-done-on-mobs", 100));
            difficulty.setDamageOnTamed(config.getInt("damage-done-on-tamed", 100));
            difficulty.setHungerDrain(config.getInt("hunger-drain-chance", 100));
            difficulty.setDamageByRangedMobs(config.getInt("damage-done-by-ranged-mobs", 100));
            difficulty.setDoubleDurabilityDamageChance(config.getInt("double-durability-damage-chance", 0));
            difficulty.setExperienceMultiplier(config.getInt("experience-multiplier", 100));
            difficulty.setChanceToCancelDeath(config.getDouble("chance-cancel-death", 0.0));
            difficulty.setMaximumHealth(config.getInt("maximum-health", 20));
            difficulty.setMinimumStarvationHealth(config.getInt("minimum-health-starvation", 0));
            difficulty.setDoubleLoot(config.getInt("double-loot-chance", 1));
            difficulty.setKeepInventory(config.getBoolean("keep-inventory", false));
            difficulty.setAllowPVP(config.getBoolean("allow-pvp", true));
            difficulty.setAllowHealthRegen(config.getBoolean("allow-natural-regen", true));
            difficulty.setEffectsOnAttack(config.getBoolean("effects-when-attacked", true));
            difficulty.setPrefix(ChatColor.translateAlternateColorCodes('&', config.getString("prefix", key)));
            if(config.isSet("commands-not-allowed-on-difficulty")) difficulty.setDisabledCommands(config.getStringList("commands-not-allowed-on-difficulty"));
            if(config.isSet("extra-damage-for-certain-armor-types")) {
                HashMap<ArmorTypes, Integer> armorTypes = new HashMap<>();
                for(String armorType : config.getConfigurationSection("extra-damage-for-certain-armor-types").getKeys(false))
                    try {
                        armorTypes.put(ArmorTypes.valueOf(armorType), config.getInt("extra-damage-for-certain-armor-types." + armorType, 1));
                    } catch (Exception ignored) { }
                difficulty.setArmorDamageMultiplier(armorTypes);
            }
            if(config.isSet("mobs-ignore-player")) difficulty.setIgnoredMobs(config.getStringList("mobs-ignore-player"));
            DIFFICULTY_LIST.put(difficulty.getDifficultyName(), difficulty);

            ConfigurationSection enchantData = config.getConfigurationSection("enchanting");
            difficulty.setMaxEnchants(enchantData.getInt("max-enchants", 2));
            difficulty.setMaxEnchantLevel(enchantData.getInt("max-level", 1));
            difficulty.setChanceToHaveArmor(enchantData.getDouble("chance-to-have-armor", 15.0));
            difficulty.setChanceToEnchant(enchantData.getDouble("chance-to-enchant-a-piece", 30.0));
            difficulty.setArmorDropChance(enchantData.getDouble("armor-drop-chance", 15.0));
            difficulty.setWeaponDropChance(enchantData.getDouble("weapon-drop-chance", 10.0));
            difficulty.setChanceToHaveWeapon(enchantData.getDouble("weapon-chance", 5.0));
            HashMap<EquipmentItems, Double> equipmentValues = new HashMap<>();
            for(EquipmentItems item : EquipmentItems.values())
                equipmentValues.put(item, enchantData.getDouble(item.name().toLowerCase() + "-chance", 1.0));
            difficulty.setEnchantChances(equipmentValues);

            if (config.isSet("mythic-mobs")) {
                ConfigurationSection mythicMobConfig = config.getConfigurationSection("mythic-mobs");
                List<MythicMobProfile> profiles = new ArrayList<>();
                for (String mythicMobKey : mythicMobConfig.getKeys(false))
                    profiles.add(new MythicMobProfile(mythicMobKey, mythicMobConfig));

                difficulty.setMythicMobProfiles(profiles);
            }

            if (config.isSet("execute")) {
                ConfigurationSection commands = config.getConfigurationSection("execute");
                difficulty.setCommandsOnJoin(commands.getStringList("on-join"));
                difficulty.setCommandsOnSwitchFromPrevious(commands.getStringList("on-switch-from-previous"));
                difficulty.setCommandsOnSwitchFromNext(commands.getStringList("on-switch-from-next"));
            }

            tmpMap.put(difficulty.getAffinity(), difficulty.getDifficultyName());
        }

        TreeMap<Integer, String> tm = new TreeMap<>(tmpMap);
        String lastKey = null;
        for (int key : tm.keySet()) {
            String thisKey = tmpMap.get(key).replace(" ", "_");
            DIFFICULTY_LIST_SORTED.add(thisKey);
            if(tmpMap.size() == DIFFICULTY_LIST_SORTED.size())
                DIFFICULTY_LIST.get(thisKey).setUntil(Integer.MAX_VALUE);
            if(lastKey != null)
                DIFFICULTY_LIST.get(lastKey).setUntil(key - 1);
            lastKey = thisKey;
        }
    }
}