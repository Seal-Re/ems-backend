package com.seal.framework.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.seal.framework.domain.BaseEntity;

import java.util.Scanner;

/**
 * 用来根据数据库创建对应代码
 */
public class CodeGenerationUtil {
    public static void main(String[] args) {
        String modules       = "message-center";//scanner("模块名称");
        String project       = "message-center";//scanner("项目名称");;
        String dbName        = "rocket";//scanner("数据库实例");//;
        String parentPackage = "com.seal.message";//scanner("父级包名");;//

        autogeneration(modules,project,dbName,parentPackage);

    }

    public static void autogeneration(String modules,
                                      String project,
                                      String dbName,
                                      String parentPackage) {
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/" + project+ "/src/main/java");
        gc.setFileOverride(true);
        gc.setActiveRecord(false);//
        gc.setEnableCache(false);//   XML 二级缓存
        gc.setBaseResultMap(true);//  XML ResultMap
        gc.setBaseColumnList(true);// XML columList
        gc.setAuthor("seal");

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setControllerName("%sController");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("root@123");
        dsc.setUrl("jdbc:mysql://127.0.0.1:3305/" + dbName + "?characterEncoding=utf8&serverTimezone=UTC");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
        strategy.setEntityTableFieldAnnotationEnable(true);//是否添加注解
        strategy.setSuperEntityClass(BaseEntity.class);
        strategy.setSuperEntityColumns("CREATOR","CREATE_TIME","UPDATER","UPDATE_TIME","IS_VALID","REMARK","VERSION");//去除父类属性
        strategy.setTablePrefix("S_","D_","R_","B_");//去除前缀
        strategy.setEntityLombokModel(true);//启用 Lombok
        strategy.setRestControllerStyle(true);//restcontroller
        String is_alltable = scanner("是否全数据库表生成代码（1、生成全库表代码；非1、生成个别表代码）");
        if (!is_alltable.equals("1")) {
            strategy.setInclude(scanner("请输入表名，多表表名以逗号分割").split(","));
        }
        mpg.setStrategy(strategy);

        // 模板配置
        TemplateConfig tc = new TemplateConfig();
//        tc.setEntity("/templates/vm/entity.java");
//        tc.setService("/templates/vm/service.java");
//        tc.setMapper("/templates/vm/mapper.java");
//        tc.setServiceImpl("/templates/vm/serviceImpl.java");

        mpg.setTemplate(tc);

        // 包配置
        PackageConfig pc = new PackageConfig();

        pc.setParent(parentPackage);
        pc.setXml("mapper.mapper");
        mpg.setPackageInfo(pc);
        // 执行生成
        mpg.execute();
    }

    private static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }
}
