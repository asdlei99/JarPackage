//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.legado.idea.plugin.packagejar.pack.impl;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaDirectoryService;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import io.legado.idea.plugin.packagejar.pack.Packager;
import io.legado.idea.plugin.packagejar.util.CommonUtils;
import io.legado.idea.plugin.packagejar.util.Messages;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllPacker extends Packager {
    private final DataContext dataContext;
    private final String exportPath;
    private final String exportJarName;
    private final Project project;
    private final Module module;
    private final VirtualFile[] virtualFiles;

    public AllPacker(DataContext dataContext, Project project, Module module, String exportPath, String exportJarName) {
        this.dataContext = dataContext;
        this.exportPath = exportPath;
        this.exportJarName = exportJarName;
        this.project = project;
        this.module = module;
        virtualFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(this.dataContext);
    }

    @Override
    public void pack() {
        Messages.clear(project);
        VirtualFile outPutDir = CompilerPaths.getModuleOutputDirectory(module, false);

        Set<VirtualFile> allVfs = new HashSet<>();
        for (VirtualFile virtualFile : virtualFiles) {
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
            if (psiDirectory != null) {
                PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
                VirtualFile pvf = outPutDir;
                for (String n : psiPackage.getQualifiedName().split("\\.")) {
                    pvf = pvf.findChild(n);
                }
                CommonUtils.collectExportFilesNest(project, allVfs, pvf);
            }
        }
        List<Path> filePaths = new ArrayList<>();
        List<String> jarEntryNames = new ArrayList<>();
        int outIndex = outPutDir.getPath().length();
        for (VirtualFile vf : allVfs) {
            filePaths.add(vf.toNioPath());
            jarEntryNames.add(vf.getPath().substring(outIndex));
        }
        CommonUtils.createNewJar(project, Path.of(exportPath, exportJarName), filePaths, jarEntryNames);
    }

    @Override
    public void finished(boolean b, int error, int i1, CompileContext compileContext) {
        if (error == 0) {
            this.pack();
        } else {
            Project project = CommonDataKeys.PROJECT.getData(this.dataContext);
            Messages.error(project, "compile error");
        }

    }


}
