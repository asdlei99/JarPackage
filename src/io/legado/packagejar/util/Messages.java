//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.legado.packagejar.util;

import com.intellij.compiler.impl.ProblemsViewPanel;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.MessageView;
import com.intellij.util.containers.ContainerUtil;

import java.util.List;

public class Messages implements Constants {
    private static final String ID = "packing";

    private Messages() {
    }

    public static void clear(Project project) {

        MessageView messageView = MessageView.getInstance(project);
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

    public static void info(Project project, String text) {
        message(project, text, 3);
    }

    public static void error(Project project, String text) {
        message(project, text, 4);
        notify(NotificationType.ERROR, "PackageJar error", text);
    }

    public static void message(Project project, String text, int type) {
        MessageView messageView = MessageView.getInstance(project);
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
                Content content = com.intellij.ui.content.ContentFactory.getInstance().createContent(packMessages, ID, true);
                messageView.getContentManager().addContent(content);
                messageView.getContentManager().setSelectedContent(content);
            }
            packMessages.addMessage(type, new String[]{text}, null, -1, -1, null);
        });
    }

    public static void activateMessageWindow(Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ToolWindowId.MESSAGES_WINDOW);
        if (toolWindow != null) {
            toolWindow.show(null);
        }
    }

    /**
     * show info notification popup
     *
     * @param title   title
     * @param message message showing in popup, can be html snippet
     */
    public static void notify(NotificationType type, String title, String message) {
        notify(type, title, message, List.of());
    }

    /**
     * show info notification popup with actions
     *
     * @param title   title
     * @param message content
     * @param actions actions show in popup and event log window
     */
    public static void notify(NotificationType type, String title, String message, List<AnAction> actions) {
        final Notification notification = new Notification(actionName, title, message, type);
        ContainerUtil.notNullize(actions).forEach(notification::addAction);
        Notifications.Bus.notify(notification);
    }

}
