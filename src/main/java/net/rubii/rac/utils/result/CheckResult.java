package net.rubii.rac.utils.result;

import net.minecraft.network.chat.Component;

public class CheckResult {
    public boolean success;
    public Component reason;

    public CheckResult(boolean success, Component reason) {
        this.success = success;
        this.reason = reason;
    }
}
