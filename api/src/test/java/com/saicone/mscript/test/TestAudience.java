package com.saicone.mscript.test;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

public class TestAudience implements Audience {

    private Component actualMessage;
    private Component actualActionBar;
    private Title actualTitle;

    public Component actualMessage() {
        final Component result = actualMessage;
        actualMessage = null;
        return result;
    }

    public Component actualActionBar() {
        final Component result = actualActionBar;
        actualActionBar = null;
        return result;
    }

    public Title actualTitle() {
        final Title result = actualTitle;
        actualTitle = null;
        return result;
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        this.actualMessage = message;
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        this.actualActionBar = message;
    }

    @Override
    public void showTitle(@NotNull Title title) {
        this.actualTitle = title;
    }
}
