package com.ccnode.codegenerator.pojo;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

/**
 * What always stop you is what you always believe.
 * <p>
 * Created by zhengjun.du on 2016/05/17 19:54
 */
public class GenCodeRequest extends BaseRequest {

    Project project;
    List<String> pojoNames;
    String projectPath;
    String pathSplitter;

    VirtualFile selectedPackage;
    String basePackage;

    public GenCodeRequest() {
    }

    public GenCodeRequest(List<String> pojoNames, String projectPath, String pathSplitter) {
        this.pojoNames = pojoNames;
        this.projectPath = projectPath;
        this.pathSplitter = pathSplitter;
    }

    public void setPojoNames(List<String> pojoNames) {
        this.pojoNames = pojoNames;
    }

    public List<String> getPojoNames() {
        return pojoNames;
    }


    public String getProjectPath() {
        return projectPath;
    }

    public String getPathSplitter() {
        return pathSplitter;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public VirtualFile getSelectedPackage() {
        return selectedPackage;
    }

    public void setSelectedPackage(VirtualFile selectedPackage) {
        this.selectedPackage = selectedPackage;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}


