package me.vlink102.melomod.events;

import lombok.Setter;
import me.vlink102.melomod.MeloMod;

public abstract class SkyblockRunnable implements Runnable {

    @Setter
    private ScheduledTask thisTask;

    public void cancel() {
        MeloMod.INSTANCE.getNewScheduler().cancel(thisTask);
    }
}
