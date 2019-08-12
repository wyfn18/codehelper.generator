package com.ccnode.codegenerator.genCode;

import com.ccnode.codegenerator.pojo.GenCodeRequest;
import com.ccnode.codegenerator.pojo.GenCodeResponse;
import com.ccnode.codegenerator.util.LoggerWrapper;
import com.google.common.base.CaseFormat;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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

            genService(response);
            if (response.checkFailure()) {
                return response;
            }
            LOGGER.info("genService done");

            genController(response);
            if (response.checkFailure()) {
                return response;
            }
            LOGGER.info("genController done");

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

        String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, pojoName);

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
        modelString.add("@Table(name = \"" + tableName + "\")");
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

    private static GenCodeResponse genService(GenCodeResponse response) {

        VirtualFile selectedPackage = response.getRequest().getSelectedPackage();

        String basePackageName = response.getRequest().getBasePackage();
        String servicePackageName = basePackageName + ".service";

        String path = selectedPackage.getPath();
        String pojoName = response.getRequest().getPojoNames().get(0);

        String servicePathName = path + response.getPathSplitter() + "service" + response.getPathSplitter() + pojoName + "Service.java";

        Path servicePath = Paths.get(servicePathName);

        if (Files.exists(servicePath)) {
            return response;
        }

        try {

            if (!Files.exists(servicePath.getParent())) {
                Files.createDirectories(servicePath.getParent());
            }

            Files.createFile(servicePath);

        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog("Service create error!", "error");
            response.failure("service create error!");
            return response;
        }

        String pojoField = Character.toLowerCase(pojoName.charAt(0)) + pojoName.substring(1);

        List<String> serviceString = new LinkedList<>();

        serviceString.add("package " + servicePackageName + ";");
        serviceString.add("");
        serviceString.add("import com.uqiauto.uplus.common.dao.BaseDao;");
        serviceString.add("import com.uqiauto.uplus.common.service.BaseService;");
        serviceString.add("import " + basePackageName + ".dao." + pojoName + "Dao;");
        serviceString.add("import " + basePackageName + ".model." + pojoName + ";");
        serviceString.add("import org.springframework.beans.factory.annotation.Autowired;");
        serviceString.add("import org.springframework.stereotype.Service;");
        serviceString.add("import org.springframework.transaction.annotation.Transactional;");
        serviceString.add("");
        serviceString.add("@Service");
        serviceString.add("@Transactional");
        serviceString.add("public class " + pojoName + "Service extends BaseService<" + pojoName + "> {");
        serviceString.add("");
        serviceString.add("    @Autowired");
        serviceString.add("    private " + pojoName + "Dao " + pojoField + "Dao;");
        serviceString.add("");
        serviceString.add("    @Override");
        serviceString.add("    protected BaseDao<" + pojoName + "> getDao() {");
        serviceString.add("        return " + pojoField + "Dao;");
        serviceString.add("    }");
        serviceString.add("");
        serviceString.add("}");

        try {
            Files.write(servicePath ,serviceString);
        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog("Service text create error!", "error");
            response.failure("Service text create error!");
            return response;
        }

        return response;
    }

    private static GenCodeResponse genController(GenCodeResponse response) {

        VirtualFile selectedPackage = response.getRequest().getSelectedPackage();

        String basePackageName = response.getRequest().getBasePackage();
        String controllerPackageName = basePackageName + ".controller";

        String path = selectedPackage.getPath();
        String pojoName = response.getRequest().getPojoNames().get(0);

        String controllerPathName = path + response.getPathSplitter() + "controller" + response.getPathSplitter() + pojoName + "Controller.java";

        Path controllerPath = Paths.get(controllerPathName);

        if (Files.exists(controllerPath)) {
            return response;
        }

        try {

            if (!Files.exists(controllerPath.getParent())) {
                Files.createDirectories(controllerPath.getParent());
            }

            Files.createFile(controllerPath);

        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog("Controller create error!", "error");
            response.failure("Controller create error!");
            return response;
        }

        String pojoField = Character.toLowerCase(pojoName.charAt(0)) + pojoName.substring(1);

        List<String> controllerString = new LinkedList<>();

        controllerString.add("package " + controllerPackageName + ";");
        controllerString.add("");
        controllerString.add("import " + basePackageName + ".service." + pojoName +"Service;");
        controllerString.add("import org.springframework.beans.factory.annotation.Autowired;");
        controllerString.add("import org.springframework.stereotype.Controller;");
        controllerString.add("");
        controllerString.add("/**");
        controllerString.add(" * ");
        controllerString.add(" */");
        controllerString.add("@Controller(value = \"/" + pojoField + ".do\")");
        controllerString.add("public class " + pojoName +"Controller {");
        controllerString.add("");
        controllerString.add("    @Autowired");
        controllerString.add("    private " + pojoName +"Service " + pojoField + "Service;");
        controllerString.add("");
        controllerString.add("");
        controllerString.add("}");


        try {
            Files.write(controllerPath ,controllerString);
        } catch (IOException e) {
            e.printStackTrace();
            Messages.showErrorDialog("Controller text create error!", "error");
            response.failure("Controller text create error!");
            return response;
        }

        return response;
    }

}
