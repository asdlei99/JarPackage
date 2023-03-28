//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.legado.packagejar.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;

import java.util.HashSet;
import java.util.List;

public class Util {
    private Util() {
    }

    public static boolean matchFileNamingConventions(String fileName) {
        return fileName.matches("[^/\\\\<>*?|\"]+");
    }

    public static void iterateDirectory(Project project, HashSet<VirtualFile> directories, VirtualFile directory) {
        PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(directory);
        if (directory != null) {
            directories.add(psiDirectory.getVirtualFile());
            PsiDirectory[] psiDirectories = psiDirectory.getSubdirectories();

            for (PsiDirectory pd : psiDirectories) {
                iterateDirectory(project, directories, pd.getVirtualFile());
            }
        }

    }

    public static String getTheSameStart(List<String> strings) {
        if (strings != null && strings.size() != 0) {
            int max = 888888;

            for (String string : strings) {
                if (string.length() < max) {
                    max = string.length();
                }
            }

            StringBuilder sb = new StringBuilder();
            HashSet<Character> set = new HashSet<>();

            for (int i = 0; i < max; ++i) {

                for (String string : strings) {
                    set.add(string.charAt(i));
                }

                if (set.size() != 1) {
                    break;
                }

                sb.append(set.iterator().next());
                set.clear();
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    private static int getMinorVersion(String vs) {
        int dashIndex = vs.lastIndexOf(95);
        if (dashIndex >= 0) {
            StringBuilder builder = new StringBuilder();

            for (int idx = dashIndex + 1; idx < vs.length(); ++idx) {
                char ch = vs.charAt(idx);
                if (!Character.isDigit(ch)) {
                    break;
                }

                builder.append(ch);
            }

            if (builder.length() > 0) {
                try {
                    return Integer.parseInt(builder.toString());
                } catch (NumberFormatException ignored) {

                }
            }
        }

        return 0;
    }

}
