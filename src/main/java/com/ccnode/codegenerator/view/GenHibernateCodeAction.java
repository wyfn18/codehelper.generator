package com.ccnode.codegenerator.view;

import com.ccnode.codegenerator.genCode.GenHibernateCodeService;
import com.ccnode.codegenerator.genCode.UserConfigService;
import com.ccnode.codegenerator.pojo.GenCodeRequest;
import com.ccnode.codegenerator.pojo.GenCodeResponse;
import com.ccnode.codegenerator.pojoHelper.GenCodeResponseHelper;
import com.ccnode.codegenerator.util.LoggerWrapper;
import com.google.common.collect.Lists;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

/**
 * What always stop you is what you always believe.
 * <p>
 * Created by zhengjun.du on 2016/04/16 21:30
 */
public class GenHibernateCodeAction extends AnAction {

    private final static Logger LOGGER = LoggerWrapper.getLogger(GenHibernateCodeAction.class);

    // If you register the action from Java code, this constructor is used to set the menu item name
    // (optionally, you can specify the menu description and an icon to display next to the menu item).
    // You can omit this constructor when registering the action in the plugin.xml file.
    public GenHibernateCodeAction() {
        // Set the menu item name.
        super("Text _Boxes");
        // Set the menu item name, description and icon.
        // super("Text _Boxes","Item description",IconLoader.getIcon("/Mypackage/icon.png"));
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);

        if (!file.isDirectory()) {
            Messages.showErrorDialog("Please select a package!", "error");
            return;
        }

        ProjectFileIndex index = ProjectRootManager.getInstance(project).getFileIndex();
        String packageName = index.getPackageNameByDirectory(file);

        if (packageName == null) {
            Messages.showErrorDialog("Please select a package!", "error");
            return;
        }

        VirtualFileManager.getInstance().syncRefresh();
        ApplicationManager.getApplication().saveAll();

        if (project == null) {
            return;
        }
        String projectPath = project.getBasePath();
        UserConfigService.loadUserConfig(projectPath);
        if (projectPath == null) {
            projectPath = StringUtils.EMPTY;
        }
        GenCodeResponse genCodeResponse = new GenCodeResponse();
        GenCodeResponseHelper.setResponse(genCodeResponse);
        try {
            GenCodeRequest request = new GenCodeRequest(Lists.newArrayList(), projectPath,
                    System.getProperty("file.separator"));
            request.setProject(project);
            request.setSelectedPackage(file);
            request.setBasePackage(packageName);

            genCodeResponse = GenHibernateCodeService.genCode(request);

            //waiting file create
            Thread.sleep(500);

            VirtualFileManager.getInstance().syncRefresh();

            VirtualFile selectedPackage = genCodeResponse.getRequest().getSelectedPackage();

            String path = selectedPackage.getPath();
            String pojoName = genCodeResponse.getRequest().getPojoNames().get(0);

            String modelPathName = path + genCodeResponse.getPathSplitter() + "model" + genCodeResponse.getPathSplitter() + pojoName + ".java";

            VirtualFile modelFile = LocalFileSystem.getInstance().findFileByPath(modelPathName);

            PsiFile psiFile = PsiManager.getInstance(project).findFile(modelFile);

            ProjectView.getInstance(project).selectPsiElement(psiFile, true);

            LoggerWrapper.saveAllLogs(projectPath);

//            Messages.showMessageDialog(project, buildEffectRowMsg(genCodeResponse), genCodeResponse.getStatus(), null);

        } catch (Throwable e) {
            LOGGER.error("actionPerformed error", e);
            genCodeResponse.setThrowable(e);
        }
        VirtualFileManager.getInstance().syncRefresh();
    }


}