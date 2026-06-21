package net.rubii.rac.utils.result;

import java.io.File;
import java.util.List;

public class GetModsResult {
    public final boolean success;

    public final List<File> mods;

    public GetModsResult(boolean success, List<File> mods) {
        this.success = success;
        this.mods = mods;
    }
}
