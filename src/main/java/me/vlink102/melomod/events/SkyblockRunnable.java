package me.vlink102.melomod.events;

import lombok.Setter;
import me.vlink102.melomod.MeloMod;

@Setter
public abstract class SkyblockRunnable implements Runnable {

    private ScheduledTask thisTask;

    public void cancel() {
        MeloMod.INSTANCE.getNewScheduler().cancel(thisTask);
    }
}
