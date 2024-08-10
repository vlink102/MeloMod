package me.vlink102.melomod.util.wrappers.hypixel.profile.member;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.vlink102.melomod.util.game.SkyblockUtil;
import me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RiftData {
    private final List<String> lifetimePurchasedBoundaries;
    private final VillagePlaza villagePlaza;
    private final WitherCage witherCage;
    private final BlackLagoon blackLagoon;
    private final DeadCats deadCats;
    private final WizardTower wizardTower;
    private final Enigma enigma;
    private final Gallery gallery;
    private final WestVillage westVillage;
    private final WyldWoods wyldWoods;
    private final Castle castle;
    private final Access access;
    private final Dreadfarm dreadfarm;
    private final Inventory inventory;

    public RiftData(JsonObject object) {
        this.lifetimePurchasedBoundaries = new ArrayList<>();
        JsonArray lifetimePurchasedBoundariesArray = SkyblockUtil.getAsJsonArray("lifetime_purchased_boundaries", object);
        for (JsonElement jsonElement : lifetimePurchasedBoundariesArray) {
            lifetimePurchasedBoundaries.add(jsonElement.getAsString());
        }
        this.villagePlaza = new VillagePlaza(SkyblockUtil.getAsJsonObject("village_plaza", object));
        this.witherCage = new WitherCage(SkyblockUtil.getAsJsonObject("wither_cage", object));
        this.blackLagoon = new BlackLagoon(SkyblockUtil.getAsJsonObject("black_lagoon", object));
        this.deadCats = new DeadCats(SkyblockUtil.getAsJsonObject("dead_cats", object));
        this.wizardTower = new WizardTower(SkyblockUtil.getAsJsonObject("wizard_tower", object));
        this.enigma = new Enigma(SkyblockUtil.getAsJsonObject("enigma", object));
        this.gallery = new Gallery(SkyblockUtil.getAsJsonObject("gallery", object));
        this.westVillage = new WestVillage(SkyblockUtil.getAsJsonObject("west_village", object));
        this.wyldWoods = new WyldWoods(SkyblockUtil.getAsJsonObject("wyld_woods", object));
        this.castle = new Castle(SkyblockUtil.getAsJsonObject("castle", object));
        this.access = new Access(SkyblockUtil.getAsJsonObject("access", object));
        this.dreadfarm = new Dreadfarm(SkyblockUtil.getAsJsonObject("dreadfarm", object));
        this.inventory = new Inventory(SkyblockUtil.getAsJsonObject("inventory", object));
    }

}
