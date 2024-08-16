package me.vlink102.melomod.util;

import lombok.Setter;
import me.vlink102.melomod.MeloMod;
import me.vlink102.melomod.util.translation.Feature;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.vlink102.melomod.util.StringUtils.cc;

public class VChatComponent {
    private final List<IChatComponent> components;
    private boolean isDebug = false;
    @Setter
    private boolean ignoreLength = false;

    public boolean isDebug() {
        return isDebug;
    }

    public VChatComponent(MeloMod.MessageScheme scheme) {
        this.components = new ArrayList<>();
        if (scheme == null) return;
        if (scheme == MeloMod.MessageScheme.DEBUG) isDebug = true;
        if (scheme == MeloMod.MessageScheme.RAW) return;
        this.add(scheme.generatePrefix(false), MeloMod.MessageScheme.generatePrefixHover(), "https://discord.gg/NVPUTYSk3u", StringUtils.VComponentSettings.INHERIT_NONE);
        this.addSpace();
        String tag = scheme.generateTag(false);
        if (tag != null) {
            this.add(tag);
            this.addSpace();
        }
    }

    public static VChatComponent empty() {
        return new VChatComponent(MeloMod.MessageScheme.RAW);
    }

    public static VChatComponent raw(String text) {
        return new VChatComponent(MeloMod.MessageScheme.RAW).add(text);
    }

    public static VChatComponent of(MeloMod.MessageScheme scheme, String text) {
        return new VChatComponent(scheme).add(text);
    }

    public static IChatComponent insert(IChatComponent parent, String delimiter, String data) {
        String message = parent.getUnformattedText();
        if (message.contains(delimiter)) {
            MeloMod.addDebug("&8" + Feature.GENERIC_DOES_CONTAIN_DELIMITER + "&r");
            String[] split = message.split("((?<=" + delimiter + ")|(?=" + delimiter + "))", -1);

            VChatComponent componentBuilder = new VChatComponent(MeloMod.MessageScheme.RAW);

            for (String s : split) {
                if (s.equalsIgnoreCase(delimiter)) {
                    componentBuilder.addItem(data);
                } else {
                    componentBuilder.add(s);
                }
            }
            return componentBuilder.build();
        }
        return parent;
    }

    public VChatComponent addSpace() {
        this.add(" ");
        return this;
    }

    public VChatComponent prefix(IChatComponent component) {
        return insertComponent(component, 0);
    }

    public VChatComponent insertComponent(IChatComponent component, int index) {
        this.components.add(index, component);
        return this;
    }

    public VChatComponent add(IChatComponent component, @Nullable HoverEvent hoverEvent, @Nullable ClickEvent clickEvent) {
        ChatStyle style = component.getChatStyle();
        if (hoverEvent != null) style.setChatHoverEvent(hoverEvent);
        if (clickEvent != null) style.setChatClickEvent(clickEvent);
        IChatComponent updated = component.createCopy().setChatStyle(style);
        return add(updated);
    }

    public VChatComponent add(IChatComponent component) {
        this.components.add(component);
        return this;
    }

    public VChatComponent add(String text, @Nullable HoverEvent hoverEvent, @Nullable ClickEvent clickEvent, StringUtils.VComponentSettings settings) {
        ChatStyle style = from(settings);
        if (hoverEvent != null) style.setChatHoverEvent(hoverEvent);
        if (clickEvent != null) style.setChatClickEvent(clickEvent);
        IChatComponent chatComponent = new ChatComponentText(cc(text));
        chatComponent.setChatStyle(style);
        this.components.add(chatComponent);
        return this;
    }

    public VChatComponent add(String text, String hoverText, @Nullable ClickEvent clickEvent, StringUtils.VComponentSettings settings) {
        add(text, new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ChatComponentText(cc(hoverText))
        ), clickEvent, settings);
        return this;
    }

    public VChatComponent add(String text, @Nullable HoverEvent hoverEvent, String clickEvent, StringUtils.VComponentSettings settings) {
        add(text, hoverEvent, new ClickEvent(
                ClickEvent.Action.OPEN_URL,
                clickEvent
        ), settings);
        return this;
    }

    public VChatComponent add(String text, String hoverText, String clickText, StringUtils.VComponentSettings settings) {
        add(text, new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                new ChatComponentText(cc(hoverText))
        ), new ClickEvent(
                ClickEvent.Action.OPEN_URL,
                clickText
        ), settings);
        return this;
    }

    public VChatComponent add(String text) {
        add(text, (HoverEvent) null, (ClickEvent) null, StringUtils.VComponentSettings.INHERIT_NONE);
        return this;
    }

    public VChatComponent add(String text, @Nullable HoverEvent hoverEvent, @Nullable ClickEvent clickEvent) {
        add(text, hoverEvent, clickEvent, StringUtils.VComponentSettings.INHERIT_NONE);
        return this;
    }

    public ChatStyle from(StringUtils.VComponentSettings settings) {
        switch (settings) {
            case INHERIT_NONE:
                return new ChatStyle()
                        .setBold(false)
                        .setColor(null)
                        .setObfuscated(false)
                        .setItalic(false)
                        .setUnderlined(false)
                        .setStrikethrough(false)
                        .setChatHoverEvent(null)
                        .setChatClickEvent(null)
                        .setInsertion(null);
            case INHERIT_FORMAT:
                return new ChatStyle()
                        .setChatHoverEvent(null)
                        .setChatClickEvent(null)
                        .setInsertion(null);
        }
        return new ChatStyle();
    }

    public VChatComponent addItem(String base64) {
        String stringData = ItemSerializer.INSTANCE.deserializeFromBase64(base64);
        ItemStack stackData = ItemSerializer.INSTANCE.deserializeFromNBT(stringData);

        IChatComponent itemComponent = new ChatComponentText(stackData.getDisplayName());
        ChatStyle style = from(StringUtils.VComponentSettings.INHERIT_NONE);
        style.setChatHoverEvent(
                new HoverEvent(
                        HoverEvent.Action.SHOW_ITEM,
                        new ChatComponentText(stringData)
                )
        );
        itemComponent.setChatStyle(style);
        this.components.add(itemComponent);
        return this;
    }

    public IChatComponent build() {
        if (components.isEmpty()) {
            return new ChatComponentText("").setChatStyle(from(StringUtils.VComponentSettings.INHERIT_NONE));
        }
        IChatComponent first = components.get(0);
        VChatComponent overflow = new VChatComponent(MeloMod.MessageScheme.RAW);
        boolean overflowing = false;
        for (int i = 1; i < components.size(); i++) {
            if (overflowing) {
                overflow.add(components.get(i).getUnformattedText());
                continue;
            }
            int length = first.getFormattedText().length() + components.get(i).getFormattedText().length();
            if (length >= 256 && !ignoreLength) {
                overflowing = true;
                overflow.add(components.get(i).getUnformattedText());
            } else {
                first.appendSibling(components.get(i));
            }
        }
        if (!overflow.components.isEmpty() && ignoreLength) {
            throw new RuntimeException("Overflow found with length ignored");
        }
        if (!overflow.components.isEmpty()) {
            MeloMod.addWarn(cc("&e" + Feature.GENERIC_WARNING_DETECTED_OVERSIZED_COMPONENT + "&r"));

            VChatComponent component = new VChatComponent(MeloMod.MessageScheme.RAW);
            component.addSpace();
            component.add("[...]", new HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    overflow.build()
            ), null);
            first.appendSibling(component.build());
        }
        return first;
    }
}
