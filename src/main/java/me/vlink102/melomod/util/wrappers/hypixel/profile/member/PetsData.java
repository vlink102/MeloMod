package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.*;

public class PetsData {
    private final List<Pet> pets;
    private final PetCare petCare;
    private final AutoPet autoPet;

    public PetsData(JsonObject object) {
        this.petCare = new PetCare(SkyblockUtil.getAsJsonObject("pet_care", object));
        this.autoPet = new AutoPet(SkyblockUtil.getAsJsonObject("autopet", object));
        this.pets = new ArrayList<>();
        JsonArray petsArray = SkyblockUtil.getAsJsonArray("pets", object);
        for (JsonElement jsonElement : petsArray) {
            pets.add(new Pet(jsonElement.getAsJsonObject()));
        }
    }

    public AutoPet getAutoPet() {
        return autoPet;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public PetCare getPetCare() {
        return petCare;
    }

    public static class PetCare {
        private final Double coinsSpent;
        private final List<String> petTypesSacrificed;

        public PetCare(JsonObject object) {
            this.coinsSpent = SkyblockUtil.getAsDouble("coins_spent", object);
            this.petTypesSacrificed = new ArrayList<>();
            JsonArray petTypesSacrificedArray = SkyblockUtil.getAsJsonArray("pet_types_sacrificed", object);
            for (JsonElement jsonElement : petTypesSacrificedArray) {
                petTypesSacrificed.add(jsonElement.getAsString());
            }
        }

        public Double getCoinsSpent() {
            return coinsSpent;
        }

        public List<String> getPetTypesSacrificed() {
            return petTypesSacrificed;
        }
    }

    public static class AutoPet {
        private final Integer rulesLimit;
        private final List<AutoPet.AutoPetRule> autoPetRules;
        private final Boolean migrated;
        private final Boolean migrated2;

        public AutoPet(JsonObject object) {
            this.rulesLimit = SkyblockUtil.getAsInteger("rules_limit", object);
            this.autoPetRules = new ArrayList<>();
            JsonArray autoPetRulesArray = SkyblockUtil.getAsJsonArray("rules", object);
            for (JsonElement jsonElement : autoPetRulesArray) {
                autoPetRules.add(new AutoPet.AutoPetRule(jsonElement.getAsJsonObject()));
            }
            this.migrated = SkyblockUtil.getAsBoolean("migrated", object);
            this.migrated2 = SkyblockUtil.getAsBoolean("migrated_2", object);

        }

        public boolean isMigrated() {
            return migrated;
        }

        public boolean isMigrated2() {
            return migrated2;
        }

        public List<AutoPet.AutoPetRule> getAutoPetRules() {
            return autoPetRules;
        }

        public int getRulesLimit() {
            return rulesLimit;
        }

        public static class AutoPetRule {
            private final UUID uuid;
            private final String id;
            private final String name;
            private final UUID uniqueID;
            private final List<AutoPet.AutoPetRule.AutoPetRuleException> autoPetRuleExceptions;
            private final Boolean disabled;
            private final HashMap<String, String> data;

            public AutoPetRule(JsonObject object) {
                this.uuid = SkyblockUtil.fromString(SkyblockUtil.getAsString("uuid", object));
                this.id = SkyblockUtil.getAsString("id", object);
                this.name = SkyblockUtil.getAsString("name", object);
                this.uniqueID = SkyblockUtil.fromString(SkyblockUtil.getAsString("uniqueId", object));
                this.autoPetRuleExceptions = new ArrayList<>();
                JsonArray autoPetRuleExceptionsArray = SkyblockUtil.getAsJsonArray("exceptions", object);
                for (JsonElement jsonElement : autoPetRuleExceptionsArray) {
                    autoPetRuleExceptions.add(new AutoPet.AutoPetRule.AutoPetRuleException(jsonElement.getAsJsonObject()));
                }
                this.disabled = SkyblockUtil.getAsBoolean("disabled", object);
                this.data = new HashMap<>();
                JsonObject dataObject = SkyblockUtil.getAsJsonObject("data", object);
                for (Map.Entry<String, JsonElement> entry : dataObject.entrySet()) {
                    String string = entry.getKey();
                    data.put(string, SkyblockUtil.getAsString(string, dataObject));
                }

            }

            public boolean isDisabled() {
                return disabled;
            }

            public HashMap<String, String> getData() {
                return data;
            }

            public String getId() {
                return id;
            }

            public UUID getUniqueID() {
                return uniqueID;
            }

            public UUID getUuid() {
                return uuid;
            }

            public List<AutoPet.AutoPetRule.AutoPetRuleException> getAutoPetRuleExceptions() {
                return autoPetRuleExceptions;
            }

            public String getName() {
                return name;
            }

            public static class AutoPetRuleException {
                private final String id;
                private final HashMap<String, String> data;

                public AutoPetRuleException(JsonObject object) {
                    this.id = SkyblockUtil.getAsString("id", object);
                    this.data = new HashMap<>();
                    JsonObject dataObject = SkyblockUtil.getAsJsonObject("data", object);
                    for (Map.Entry<String, JsonElement> entry : dataObject.entrySet()) {
                        String string = entry.getKey();
                        data.put(string, SkyblockUtil.getAsString(string, dataObject));
                    }
                }

                public HashMap<String, String> getData() {
                    return data;
                }

                public String getId() {
                    return id;
                }
            }
        }
    }

    public static class Pet {
        private final UUID uuid;
        private final UUID uniqueId;
        private final String type;
        private final Double exp;
        private final Boolean active;
        private final String tier;
        private final String heldItem;
        private final Integer candyUsed;
        private final String skin;

        public Pet(JsonObject object) {
            this.uuid = SkyblockUtil.fromString(SkyblockUtil.getAsString("uuid", object));
            this.uniqueId = SkyblockUtil.fromString(SkyblockUtil.getAsString("uniqueId", object));
            this.type = SkyblockUtil.getAsString("type", object);
            this.exp = SkyblockUtil.getAsDouble("exp", object);
            this.active = SkyblockUtil.getAsBoolean("active", object);
            this.tier = SkyblockUtil.getAsString("tier", object);
            this.heldItem = SkyblockUtil.getAsString("heldItem", object);
            this.candyUsed = SkyblockUtil.getAsInteger("candyUsed", object);
            this.skin = SkyblockUtil.getAsString("skin", object);
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getType() {
            return type;
        }

        public int getCandyUsed() {
            return candyUsed;
        }

        public Double getExp() {
            return exp;
        }

        public String getHeldItem() {
            return heldItem;
        }

        public String getSkin() {
            return skin;
        }

        public String getTier() {
            return tier;
        }

        public UUID getUniqueId() {
            return uniqueId;
        }

        public boolean isActive() {
            return active;
        }
    }
}
