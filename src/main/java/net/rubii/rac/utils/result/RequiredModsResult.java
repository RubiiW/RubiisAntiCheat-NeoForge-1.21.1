package net.rubii.rac.utils.result;

import java.util.List;

public class RequiredModsResult {
    public final boolean success;

    public final List<String> missingMods;

    public RequiredModsResult(boolean success, List<String> modIdList) {
        this.success = success;
        this.missingMods = modIdList;
    }
}
