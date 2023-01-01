/*
 * Decompiled with CFR 0.150.
 */
package org.celestial.client.feature.impl.misc;

import org.celestial.client.feature.Feature;
import org.celestial.client.feature.impl.Type;
import org.celestial.client.helpers.misc.ChatHelper;
import org.celestial.client.settings.impl.BooleanSetting;
import org.celestial.client.settings.impl.NumberSetting;

public class Baritone
extends Feature {
    public static BooleanSetting randomizeRotations;
    public static NumberSetting randomizeValue;
    public static BooleanSetting autoDropTrash;
    public static BooleanSetting noBack;
    public static BooleanSetting inventoryFullLeave;

    public Baritone() {
        super("Baritone", "\u0418\u0441\u043a\u0443\u0441\u0442\u0432\u0435\u043d\u043d\u044b\u0439 \u0438\u043d\u0442\u0435\u043b\u043b\u0435\u043a\u0442, \u043a\u043e\u0442\u043e\u0440\u044b\u0439 \u0431\u0443\u0434\u0435\u0442 \u0434\u0435\u043b\u0430\u0442\u044c \u0437\u0430 \u0432\u0430\u0441 \u0432\u0441\u044e \u0440\u0430\u0431\u043e\u0442\u0443", Type.Misc);
        randomizeRotations = new BooleanSetting("Randomize Rotations", true, () -> true);
        randomizeValue = new NumberSetting("Randomize Value", 0.5f, 0.5f, 2.0f, 0.1f, () -> randomizeRotations.getCurrentValue());
        autoDropTrash = new BooleanSetting("Auto Drop Trash", "\u0411\u043e\u0442 \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0432\u044b\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u0435\u0442 \u043d\u0435 \u043d\u0443\u0436\u043d\u044b\u0435 \u0431\u043b\u043e\u043a\u0438 (\u0430\u043d\u0434\u0435\u0437\u0438\u0442, \u0431\u0443\u043b\u044b\u0436\u043d\u0438\u043a, \u0438 \u0442.\u0434)", true, () -> true);
        inventoryFullLeave = new BooleanSetting("Inventory Full Leave", "\u0411\u043e\u0442 \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0432\u044b\u0445\u043e\u0434\u0438\u0442 \u0441 \u0441\u0435\u0440\u0432\u0435\u0440\u0430 \u043f\u0440\u0438 \u043f\u043e\u043b\u043d\u043e\u043c \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435", false, () -> true);
        noBack = new BooleanSetting("No Back", "\u0411\u043e\u0442 \u043d\u0435 \u0431\u0443\u0434\u0435\u0442 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0442\u044c\u0441\u044f \u0437\u0430 \u0443\u043f\u0430\u0432\u0448\u0438\u043c \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u043c", true, () -> true);
        this.addSettings(randomizeRotations, randomizeValue, autoDropTrash, noBack, inventoryFullLeave);
    }

    @Override
    public void onEnable() {
        ChatHelper.addChatMessage("Please write #help to find out all the baritone-commands");
        ChatHelper.addChatMessage("Please write #stop to deactivate baritone");
    }
}

