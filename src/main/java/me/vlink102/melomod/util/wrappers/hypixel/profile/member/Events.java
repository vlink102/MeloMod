package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Events {
    private final Easter easter;

    public Events(JsonObject object) {
        this.easter = new Easter(SkyblockUtil.getAsJsonObject("easter", object));
    }

    public Easter getEaster() {
        return easter;
    }

    public static class Easter {
        private final Long chocolate;
        private final Long chocolateSincePrestige;
        private final Long totalChocolate;
        private final HashMap<String, Integer> employees;
        private final Long lastViewedChocolateFactory;
        private final Easter.Rabbits rabbits;
        private final Easter.Shop shop;
        private final Integer rabbitBarnCapacityLevel;
        private final String rabbitSort;
        private final Integer chocolateLevel;
        private final Easter.TimeTower timeTower;
        private final String rabbitFilter;
        private final Integer chocolateMultiplierUpgrades;
        private final Integer clickUpgrades;
        private final Integer rabbitRarityUpgrades;
        private final Integer supremeChocolateBars;
        private final Integer refinedDarkCacaoTruffles;

        public Easter(JsonObject object) {
            this.chocolate = SkyblockUtil.getAsLong("chocolate", object);
            this.chocolateSincePrestige = SkyblockUtil.getAsLong("chocolate_since_prestige", object);
            this.totalChocolate = SkyblockUtil.getAsLong("total_chocolate", object);
            this.employees = new HashMap<>();
            JsonObject employeesObject = SkyblockUtil.getAsJsonObject("employees", object);
            for (Map.Entry<String, JsonElement> entry : employeesObject.entrySet()) {
                String string = entry.getKey();
                employees.put(string, SkyblockUtil.getAsInteger(string, employeesObject));
            }
            this.lastViewedChocolateFactory = SkyblockUtil.getAsLong("last_viewed_chocolate_factory", object);
            this.rabbits = new Easter.Rabbits(SkyblockUtil.getAsJsonObject("rabbits", object));
            this.shop = new Easter.Shop(SkyblockUtil.getAsJsonObject("shop", object));
            this.rabbitBarnCapacityLevel = SkyblockUtil.getAsInteger("rabbit_barn_capacity_level", object);
            this.rabbitSort = SkyblockUtil.getAsString("rabbit_sort", object);
            this.chocolateLevel = SkyblockUtil.getAsInteger("chocolate_level", object);
            this.timeTower = new Easter.TimeTower(SkyblockUtil.getAsJsonObject("time_tower", object));
            this.rabbitFilter = SkyblockUtil.getAsString("rabbit_filter", object);
            this.chocolateMultiplierUpgrades = SkyblockUtil.getAsInteger("chocolate_multiplier_upgrades", object);
            this.clickUpgrades = SkyblockUtil.getAsInteger("click_upgrades", object);
            this.rabbitRarityUpgrades = SkyblockUtil.getAsInteger("rabbit_rarity_upgrades", object);
            this.supremeChocolateBars = SkyblockUtil.getAsInteger("supreme_chocolate_bars", object);
            this.refinedDarkCacaoTruffles = SkyblockUtil.getAsInteger("refined_dark_cacao_truffles", object);
        }

        public HashMap<String, Integer> getEmployees() {
            return employees;
        }

        public int getChocolateLevel() {
            return chocolateLevel;
        }

        public long getChocolate() {
            return chocolate;
        }

        public int getChocolateMultiplierUpgrades() {
            return chocolateMultiplierUpgrades;
        }

        public int getClickUpgrades() {
            return clickUpgrades;
        }

        public int getRabbitBarnCapacityLevel() {
            return rabbitBarnCapacityLevel;
        }

        public int getRabbitRarityUpgrades() {
            return rabbitRarityUpgrades;
        }

        public int getRefinedDarkCacaoTruffles() {
            return refinedDarkCacaoTruffles;
        }

        public int getSupremeChocolateBars() {
            return supremeChocolateBars;
        }

        public long getChocolateSincePrestige() {
            return chocolateSincePrestige;
        }

        public long getLastViewedChocolateFactory() {
            return lastViewedChocolateFactory;
        }

        public long getTotalChocolate() {
            return totalChocolate;
        }

        public Easter.Rabbits getRabbits() {
            return rabbits;
        }

        public Easter.Shop getShop() {
            return shop;
        }

        public String getRabbitFilter() {
            return rabbitFilter;
        }

        public String getRabbitSort() {
            return rabbitSort;
        }

        public Easter.TimeTower getTimeTower() {
            return timeTower;
        }

        public static class TimeTower {
            private final Integer charges;
            private final Long lastChargeTime;
            private final Integer level;
            private final Long activationTime;

            public TimeTower(JsonObject object) {
                this.charges = SkyblockUtil.getAsInteger("charges", object);
                this.lastChargeTime = SkyblockUtil.getAsLong("last_charge_time", object);
                this.level = SkyblockUtil.getAsInteger("level", object);
                this.activationTime = SkyblockUtil.getAsLong("activation_time", object);
            }

            public int getCharges() {
                return charges;
            }

            public int getLevel() {
                return level;
            }

            public long getActivationTime() {
                return activationTime;
            }

            public long getLastChargeTime() {
                return lastChargeTime;
            }
        }

        public static class Shop {
            private final Integer year;
            private final List<String> rabbitsShop;
            private final List<String> rabbitsPurchased;
            private final Long chocolateSpent;
            private final Integer cocoaFortuneUpgrades;

            public Shop(JsonObject object) {
                this.year = SkyblockUtil.getAsInteger("year", object);
                this.rabbitsShop = new ArrayList<>();
                JsonArray rabbitsShopArray = SkyblockUtil.getAsJsonArray("rabbits", object);
                for (JsonElement jsonElement : rabbitsShopArray) {
                    rabbitsShop.add(jsonElement.getAsString());
                }
                this.rabbitsPurchased = new ArrayList<>();
                JsonArray rabbitsPurchasedArray = SkyblockUtil.getAsJsonArray("rabbits_purchased", object);
                for (JsonElement jsonElement : rabbitsPurchasedArray) {
                    rabbitsPurchased.add(jsonElement.getAsString());
                }
                this.chocolateSpent = SkyblockUtil.getAsLong("chocolate", object);
                this.cocoaFortuneUpgrades = SkyblockUtil.getAsInteger("cocoa_fortune_upgrades", object);
            }

            public int getCocoaFortuneUpgrades() {
                return cocoaFortuneUpgrades;
            }

            public int getYear() {
                return year;
            }

            public List<String> getRabbitsPurchased() {
                return rabbitsPurchased;
            }

            public List<String> getRabbitsShop() {
                return rabbitsShop;
            }

            public long getChocolateSpent() {
                return chocolateSpent;
            }
        }

        public static class Rabbits {
            private final HashMap<String, Long> collectedEggs;
            private final HashMap<String, List<String>> collectedLocations;

            public Rabbits(JsonObject object) {
                this.collectedEggs = new HashMap<>();
                JsonObject collectedEggsObject = SkyblockUtil.getAsJsonObject("collected_eggs", object);
                for (Map.Entry<String, JsonElement> entry : collectedEggsObject.entrySet()) {
                    String string = entry.getKey();
                    collectedEggs.put(string, SkyblockUtil.getAsLong(string, collectedEggsObject));
                }
                this.collectedLocations = new HashMap<>();
                JsonObject collectedLocationsObject = SkyblockUtil.getAsJsonObject("collected_locations", object);
                for (Map.Entry<String, JsonElement> entry : collectedLocationsObject.entrySet()) {
                    String string = entry.getKey();
                    collectedLocations.put(string, new ArrayList<>());
                    JsonArray locationArray = collectedLocationsObject.getAsJsonArray(string);
                    for (JsonElement jsonElement : locationArray) {
                        collectedLocations.get(string).add(jsonElement.getAsString());
                    }
                }
            }

            public HashMap<String, List<String>> getCollectedLocations() {
                return collectedLocations;
            }

            public HashMap<String, Long> getCollectedEggs() {
                return collectedEggs;
            }
        }
    }
}
