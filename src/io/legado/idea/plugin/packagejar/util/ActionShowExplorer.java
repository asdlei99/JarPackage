package io.legado.idea.plugin.packagejar.util;

import com.intellij.ide.actions.RevealFileAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * open the file in system file explorer
 */
public class ActionShowExplorer extends AnAction {
    private final Path filePath;

    private ActionShowExplorer(Path filePath) {
        super(Constants.actionNameExplorer);
        this.filePath = filePath;
    }

    static ActionShowExplorer of(Path filePath) {
        return new ActionShowExplorer(filePath);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (filePath != null && !Files.isDirectory(filePath)) {
            RevealFileAction.openFile(filePath.toFile());
        } else if (filePath != null) {
            RevealFileAction.openDirectory(filePath.toFile());
        }
    }
}