package com.example.mpgenerator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MpGeneratorApplicationTests {

    @Test
    void testGenerator() {
        //1、配置数据源
        FastAutoGenerator
                .create("jdbc:mysql://47.120.7.129:3306/xcplus_content?serverTimezone=Asia/Shanghai"
                        , "root"
                        , "root")
                //2、全局配置
                .globalConfig(builder -> {
                    builder.author("arvin") // 设置作者名
                            .outputDir(System.getProperty("user.dir") + "/src/main/java")   //设置输出路径
                            .commentDate("yyyy-MM-dd hh:mm:ss")
                            .dateType(DateType.ONLY_DATE)   //定义生成的实体类中日期的类型 TIME_PACK=LocalDateTime;ONLY_DATE=Date;
                            .fileOverride()   //覆盖之前的文件
                            .enableSwagger()   //开启 swagger 模式
                            .disableOpenDir();   //禁止打开输出目录，默认打开
                })
                //3、包配置
                .packageConfig(builder -> {
                    builder.parent("com.example") // 设置父包名
                            .moduleName("mpgenerator")   //设置模块包名
                            .entity("po")   //pojo 实体类包名
                            .service("service") //Service 包名
                            .serviceImpl("serviceImpl") // ***ServiceImpl 包名
                            .mapper("mapper")   //Mapper 包名
                            .xml("mapper")  //Mapper XML 包名
                            .controller("controller") //Controller 包名
                            .other("utils") //自定义文件包名
                            //.pathInfo(Collections.singletonMap(
                            //        OutputFile.mapperXml,
                            //        System.getProperty("user.dir") + "/src/main/resources/mapper"))
                    ;//配置 mapper.xml 路径信息：项目的 resources 目录下
                })
                //4、策略配置
                .strategyConfig(builder -> {
                    builder.addInclude("course_base",
                                    "course_market",
                                    "course_teacher",
                                    "course_category",
                                    "teachplan",
                                    "teachplan_media",
                                    "course_publish",
                                    "course_publish_pre") // 设置需要生成的数据表名
                            //实体类策略配置
                            .entityBuilder()
                            .enableLombok() //开启 Lombok
                            .disableSerialVersionUID()  //不实现 Serializable 接口，不生成 SerialVersionUID
                            .logicDeleteColumnName("deleted")   //逻辑删除字段名
                            .naming(NamingStrategy.underline_to_camel)  //数据库表映射到实体的命名策略：下划线转驼峰命
                            .columnNaming(NamingStrategy.underline_to_camel)    //数据库表字段映射到实体的命名策略：下划线转驼峰命
                            .addTableFills(
                                    new Column("create_date", FieldFill.INSERT),
                                    new Column("change_date", FieldFill.INSERT_UPDATE)
                            )   //添加表字段填充，"create_time"字段自动填充为插入时间，"modify_time"字段自动填充为插入修改时间
                            .enableTableFieldAnnotation()      // 开启生成实体时生成字段注解

                            //4.1、Mapper策略配置
                            .mapperBuilder()
                            .superClass(BaseMapper.class)   //设置父类
                            .formatMapperFileName("%sMapper")   //格式化 mapper 文件名称
                            .enableMapperAnnotation()       //开启 @Mapper 注解
                            .formatXmlFileName("%sXml"); //格式化 Xml 文件名称
                })
                //5、模板引擎配置
                .templateEngine(new VelocityTemplateEngine())
                //6、执行
                .execute();
    }

}
