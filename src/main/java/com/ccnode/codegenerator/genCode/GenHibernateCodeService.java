package com.ccnode.codegenerator.genCode;

import com.ccnode.codegenerator.exception.BizException;
import com.ccnode.codegenerator.pojo.GenCodeRequest;
import com.ccnode.codegenerator.pojo.GenCodeResponse;
import com.ccnode.codegenerator.pojo.OnePojoInfo;
import com.ccnode.codegenerator.pojo.PojoFieldInfo;
import com.ccnode.codegenerator.pojoHelper.OnePojoInfoHelper;
import com.ccnode.codegenerator.util.GenCodeConfig;
import com.ccnode.codegenerator.util.IOUtils;
import com.ccnode.codegenerator.util.LoggerWrapper;
import com.google.common.collect.Lists;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.ccnode.codegenerator.genCode.GenDaoService.genDAO;

/**
 * What always stop you is what you always believe.
 * <p>
 * Created by zhengjun.du on 2016/05/17 19:52
 */
public class GenHibernateCodeService {

    private final static Logger LOGGER = LoggerWrapper.getLogger(GenHibernateCodeService.class);

    public static GenCodeResponse genCode(GenCodeRequest request) {
        GenCodeResponse response = new GenCodeResponse();
        try {

            response.setUserConfigMap(UserConfigService.userConfigMap);

            LOGGER.info("UserConfigService.readConfigFile done");

            response.setRequest(request);

            response.setPathSplitter(request.getPathSplitter());

            //get pojos and init location
            response = UserConfigService.initConfig(response);
            if (response.checkFailure()) {
                return response;
            }
            LOGGER.info("UserConfigService.initConfig done");

//            genPackage(response);

            response = genPojo(response);
            if (response.checkFailure()) {
                return response;
            }
            LOGGER.info("genPojo done");

            genDao(response);
            if (response.checkFailure()) {
                return response;
            }
            LOGGER.info("genDao done");
            GenServiceService.genService(response);
            if (response.checkFailure()) {
                return response;
            }
            LOGGER.info("UserConfigService.genService done");
            GenMapperService.genMapper(response);
            if (response.checkFailure()) {
                return response;
            }
            LOGGER.info("UserConfigService.genMapper done");
            for (OnePojoInfo onePojoInfo : response.getPojoInfos()) {
                OnePojoInfoHelper.flushFiles(onePojoInfo, response);
            }
            if (response.checkFailure()) {
                return response;
            }
            LOGGER.info("UserConfigService.flushFiles done");
            response.success();

        } catch (Exception e) {
            LOGGER.error("gen code failure ", e);
            response.failure("gen code failure ", e);
        }
        return response;
    }

    private static GenCodeResponse genPojo(GenCodeResponse response) {

        VirtualFile selectedPackage = response.getRequest().getSelectedPackage();

        String basePackageName = response.getRequest().getBasePackage();
        String modelPackageName = basePackageName + ".model";

        String path = selectedPackage.getPath();
        String pojoName = response.getRequest().getPojoNames().get(0);

        String modelPathName = path + response.getPathSplitter() + "model" + response.getPathSplitter() + pojoName + ".java";

        Path modelPath = Paths.get(modelPathName);

        if (Files.exists(modelPath)) {
            return response;
        }

        try {

            if (!Files.exists(modelPath.getParent())) {
                Files.createDirectories(modelPath.getParent());
            }

            Files.createFile(modelPath);

        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog("Model create error!", "error");
            response.failure("Model create error!");
            return response;
        }

        List<String> modelString = new LinkedList<>();

        modelString.add("package " + modelPackageName + ";");
        modelString.add("");
        modelString.add("import lombok.Data;");
        modelString.add("");
        modelString.add("import javax.persistence.*;");
        modelString.add("import java.io.Serializable;");
        modelString.add("");
        modelString.add("/**");
        modelString.add(" * ");
        modelString.add(" */");
        modelString.add("@Data");
        modelString.add("@Entity");
        modelString.add("@Table(name = \"" + pojoName.toLowerCase() + "\")");
        modelString.add("public class " + pojoName + " implements Serializable {");
        modelString.add("");
        modelString.add("    @Id");
        modelString.add("    @GeneratedValue(strategy = GenerationType.AUTO)");
        modelString.add("    private Long id;");
        modelString.add("");
        modelString.add("}");

        try {
            Files.write(modelPath ,modelString);
        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog("Model text create error!", "error");
            response.failure("Model text create error!");
            return response;
        }

        return response;
    }

    private static GenCodeResponse genDao(GenCodeResponse response) {

        VirtualFile selectedPackage = response.getRequest().getSelectedPackage();

        String basePackageName = response.getRequest().getBasePackage();
        String daoPackageName = basePackageName + ".dao";

        String path = selectedPackage.getPath();
        String pojoName = response.getRequest().getPojoNames().get(0);

        String daoPathName = path + response.getPathSplitter() + "dao" + response.getPathSplitter() + pojoName + "Dao.java";

        Path daoPath = Paths.get(daoPathName);

        if (Files.exists(daoPath)) {
            return response;
        }

        try {

            if (!Files.exists(daoPath.getParent())) {
                Files.createDirectories(daoPath.getParent());
            }

            Files.createFile(daoPath);

        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog("Dao create error!", "error");
            response.failure("Dao create error!");
            return response;
        }

        List<String> daoString = new LinkedList<>();

        daoString.add("package " + daoPackageName + ";");
        daoString.add("");
        daoString.add("import com.uqiauto.uplus.common.dao.HibernateBaseDao;");
        daoString.add("import " + basePackageName + ".model." + pojoName + ";");
        daoString.add("import org.springframework.stereotype.Repository;");
        daoString.add("");
        daoString.add("@Repository");
        daoString.add("public class " + pojoName + "Dao extends HibernateBaseDao<" + pojoName + "> {");
        daoString.add("");
        daoString.add("}");

        try {
            Files.write(daoPath ,daoString);
        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog("Dao text create error!", "error");
            response.failure("Dao text create error!");
            return response;
        }

        return response;
    }

}
