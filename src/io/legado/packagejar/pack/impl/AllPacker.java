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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
    private final VirtualFile[] virtualFiles;
    private final VirtualFile outPutDir;

    public AllPacker(DataContext dataContext, String exportPath, String exportJarName) {
        this.dataContext = dataContext;
        this.exportPath = exportPath;
        this.exportJarName = exportJarName;
        project = CommonDataKeys.PROJECT.getData(dataContext);
        Module module = LangDataKeys.MODULE.getData(dataContext);
        assert module != null;
        virtualFiles = CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
        outPutDir = CompilerPaths.getModuleOutputDirectory(module, false);
    }

    @Override
    public void pack() throws Exception {
        Set<VirtualFile> allVfs = new HashSet<>();
        for (VirtualFile virtualFile : virtualFiles) {
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
            if (psiDirectory != null) {
                PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(psiDirectory);
                VirtualFile pvf = outPutDir;
                assert psiPackage != null;
                String[] packageNames = psiPackage.getQualifiedName().split("\\.");
                for (String n : packageNames) {
                    pvf = pvf.findChild(n);
                    if (pvf == null) {
                        throw new IOException(n + " 文件夹不存在");
                    }
                }
                CommonUtils.collectExportFilesNest(project, allVfs, pvf);
            }
        }
        List<Path> filePaths = new ArrayList<>();
        List<String> jarEntryNames = new ArrayList<>();
        int outIndex = outPutDir.getPath().length() + 1;
        for (VirtualFile vf : allVfs) {
            filePaths.add(vf.toNioPath());
            jarEntryNames.add(vf.getPath().substring(outIndex));
        }
        CommonUtils.createNewJar(project, Path.of(exportPath, exportJarName), filePaths, jarEntryNames);
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
