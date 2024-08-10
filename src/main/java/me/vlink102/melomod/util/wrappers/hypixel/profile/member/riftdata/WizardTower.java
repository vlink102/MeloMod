package me.vlink102.melomod.util.wrappers.hypixel.profile.member.riftdata;

import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

public class WizardTower {
    private final Integer wizardQuestStep;
    private final Integer crumbsLaidOut;

    public WizardTower(JsonObject object) {
        this.wizardQuestStep = SkyblockUtil.getAsInteger("wizard_quest_step", object);
        this.crumbsLaidOut = SkyblockUtil.getAsInteger("crumbs_laid_out", object);
    }

    public int getCrumbsLaidOut() {
        return crumbsLaidOut;
    }

    public int getWizardQuestStep() {
        return wizardQuestStep;
    }
}
