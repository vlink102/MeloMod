package me.vlink102.melomod.util;

import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.List;

public class ComponentFlattening {
    public ComponentFlattening() {
    }

    public static IChatComponent flatten(IChatComponent component) {
        List<IChatComponent> e = getChildren(component);
        VChatComponent chatComponent = VChatComponent.empty();
        e.forEach(chatComponent::add);
        return chatComponent.build();
    }

    private static List<IChatComponent> getChildren(IChatComponent component) {
        List<IChatComponent> list = new ArrayList<>(component.getSiblings());

        for (IChatComponent iChatComponent : component.getSiblings()) {
            list.addAll(getChildren(iChatComponent));
        }

        return list;
    }
}
