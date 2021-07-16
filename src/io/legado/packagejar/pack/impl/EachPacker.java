//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.legado.packagejar.pack.impl;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import io.legado.packagejar.pack.Packager;
import io.legado.packagejar.util.CommonUtils;
import io.legado.packagejar.util.Messages;
import io.legado.packagejar.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class EachPacker extends Packager {
    private final DataContext dataContext;
    private final String exportPath;
    private final Project project;
    private final Module module;
    private final VirtualFile[] virtualFiles;
    private final VirtualFile outPutDir;

    public EachPacker(DataContext dataContext, String exportPath) {
        this.dataContext = dataContext;
        this.exportPath = exportPath;
        project = CommonDataKeys.PROJECT.getData(dataContext);
        module = LangDataKeys.MODULE.getData(dataContext);
        virtualFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        outPutDir = CompilerPaths.getModuleOutputDirectory(module, false);
    }

    @Override
    public void pack() throws Exception {
        HashSet<VirtualFile> directories = new HashSet<>();
        assert virtualFiles != null;
        for (VirtualFile virtualFile : virtualFiles) {
            Util.iterateDirectory(project, directories, virtualFile);
        }

        Iterator<VirtualFile> iterator = directories.iterator();

        while (true) {
            PsiDirectory psiDirectory;
            do {
                if (!iterator.hasNext()) {
                    return;
                }

                VirtualFile directory = iterator.next();
                psiDirectory = PsiManager.getInstance(project).findDirectory(directory);
            } while (psiDirectory == null);

            PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
            VirtualFile pvf = outPutDir;
            String[] packageNames = psiPackage.getQualifiedName().split("\\.");
            for (String n : packageNames) {
                pvf = pvf.findChild(n);
                if (pvf == null) {
                    throw new IOException(n + " 文件夹不存在");
                }
            }
            Set<VirtualFile> allVfs = new HashSet<>();
            CommonUtils.collectExportFilesNest(project, allVfs, pvf);
            List<Path> filePaths = new ArrayList<>();
            List<String> jarEntryNames = new ArrayList<>();
            int outIndex = outPutDir.getPath().length() + 1;
            for (VirtualFile vf : allVfs) {
                filePaths.add(vf.toNioPath());
                jarEntryNames.add(vf.getPath().substring(outIndex));
            }
            CommonUtils.createNewJar(project, Path.of(exportPath, psiPackage.getQualifiedName() + ".jar"), filePaths, jarEntryNames);
        }
    }

    @Override
    public void finished(boolean b, int error, int i1, @NotNull CompileContext compileContext) {
        if (error == 0) {
            try {
                pack();
            } catch (Exception e) {
                Messages.error(project, e.getLocalizedMessage());
                e.printStackTrace();
            }
        } else {
            Project project = CommonDataKeys.PROJECT.getData(this.dataContext);
            Messages.error(project, "compile error");
        }

    }
}
