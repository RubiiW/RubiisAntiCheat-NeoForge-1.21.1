package net.rubii.rac.utils.result;

import java.util.List;

public class ForbiddenModsResult {
    public final boolean success;

    public final List<String> forbiddenMods;

    public ForbiddenModsResult(boolean success, List<String> modIdList) {
        this.success = success;
        this.forbiddenMods = modIdList;
    }
}
