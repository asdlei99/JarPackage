//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.blueline.idea.plugin.packagejar.message;

import com.intellij.compiler.impl.ProblemsViewPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.MessageView;
import com.intellij.ui.content.MessageView.SERVICE;

public class Messages {
    private static final String ID = "packing";

    public Messages() {
    }

    public static void clear(Project project) {

        MessageView messageView = SERVICE.getInstance(project);
        messageView.runWhenInitialized(() -> {

            ContentManager contentManager = messageView.getContentManager();
            Content[] contents = contentManager.getContents();

            for (Content content : contents) {
                if ("packing".equals(content.getTabName())) {
                    ProblemsViewPanel viewPanel = (ProblemsViewPanel) content.getComponent();
                    viewPanel.close();
                    break;
                }
            }
        });
    }

    public static void info(Project project, String string) {
        MessageView messageView = SERVICE.getInstance(project);
        messageView.runWhenInitialized(() -> {
            activateMessageWindow(project);

            ProblemsViewPanel packMessages = null;
            Content[] contents = messageView.getContentManager().getContents();

            for (Content content : contents) {
                if ("packing".equals(content.getTabName())) {
                    packMessages = (ProblemsViewPanel) content.getComponent();
                    break;
                }
            }

            if (packMessages == null) {
                packMessages = new ProblemsViewPanel(project);
                Content content = com.intellij.ui.content.ContentFactory.SERVICE.getInstance().createContent(packMessages, ID, true);
                messageView.getContentManager().addContent(content);
                messageView.getContentManager().setSelectedContent(content);
            }
            packMessages.addMessage(3, new String[]{string}, null, -1, -1, null);
        });
    }

    public static void activateMessageWindow(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.MESSAGES_WINDOW);
        if (toolWindow != null) {
            toolWindow.show(null);
        }
    }
}
