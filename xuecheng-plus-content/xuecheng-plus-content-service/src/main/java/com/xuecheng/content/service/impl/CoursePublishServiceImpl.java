package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CoursePublishService coursePublishService;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //课程基本、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        if (courseBaseInfo == null) {

        }

        //课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree == null) {

        }

        //封装
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        //todo：课程师资信息封装

        //返回
        return coursePreviewDto;
    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        /*
            约束：
                1、对已提交审核的课程不允许提交审核。
                2、本机构只允许提交本机构的课程。
                3、没有上传图片不允许提交审核。
                4、没有添加课程计划不允许提交审核。
         */
        //查询课程基本信息&课程营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        String auditStatus = courseBaseInfo.getAuditStatus();
        //1、对已提交审核的课程不允许提交审核。
        if (StringUtils.equals(auditStatus, "202003")) {
            XueChengPlusException.cast("课程已提交审核，审核结束才可再次提交");
        }
        //2、本机构只允许提交本机构的课程。
        if (!courseBaseInfo.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("操作失败，请勿提交其他机构课程");
        }
        //3、没有上传图片不允许提交审核。
        if (courseBaseInfo.getPic().isEmpty()) {
            XueChengPlusException.cast("未添加课程图片，提交失败");
        }

        //查询课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        //4、没有添加课程计划不允许提交审核。
        if (CollectionUtils.isEmpty(teachplanTree)) {
            XueChengPlusException.cast("未添加任何课程计划，提交失败");
        }


        //保存以上所有信息到预发布表，并修改状态为已提交
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //数据封装
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);//课程基本信息
        coursePublishPre.setId(courseBaseInfo.getId());//设置id

        CourseMarket courseMarket = courseMarketMapper.selectById(companyId);
        coursePublishPre.setMarket(JSON.toJSONString(courseMarket));//课程营销信息

        coursePublishPre.setTeachplan(JSON.toJSONString(teachplanTree));//课程计划

        coursePublishPre.setStatus("202003");//状态设为已提交

        //coursePublishPre.setTeachers();todo：封装课程师资信息

        //coursePublishPre.setMtName("");//大分类名称
        //coursePublishPre.setStName("");//小分类名称

        //经典有则更新，无则添加
        CoursePublishPre coursePublishPreBySelect = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreBySelect != null) {
            coursePublishPreMapper.updateById(coursePublishPre);
        } else {
            coursePublishPreMapper.insert(coursePublishPre);
        }

        //更新课程基本信息表的审核状态为“已提交”
        courseBaseInfo.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBaseInfo);
    }

    @Override
    public void publish(Long companyId, Long courseId) {
        //校验
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("课程未提交审核");
        }
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不允许提交其它机构的课程");
        }
        if (!coursePublishPre.getStatus().equals("202004")) {
            XueChengPlusException.cast("课程未通过审核，无法发布");
        }


        //构建课程发布数据的模型
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);//预发布表与发布表结构一致，直接拷贝
        coursePublish.setStatus("203002");//状态改为已发布

        //保存数据到发布表
        CoursePublish coursePublishBySelect = coursePublishMapper.selectById(courseId);
        if (coursePublishBySelect != null) {
            coursePublishMapper.updateById(coursePublish);
        } else {
            coursePublishMapper.insert(coursePublish);
        }
        //更新课程基本信息状态为已发布
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("无该课程");
        }
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);

        //插入一条课程发布消息到消息表
        saveCoursePublishMessage(courseId);

        //删除预发布表中数据
        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public File generateCourseHtml(Long courseId) {

        //声明临时文件
        File htmlFile = null;
        //配置freemarker
        Configuration configuration = new Configuration(Configuration.getVersion());
        try {
            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            String classpath = this.getClass().getResource("/").getPath();

            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));

            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");


            //模型
            CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
            /*if (coursePublish == null) {
                XueChengPlusException.cast("课程未发布");
            }*/
            Map<String, CoursePreviewDto> model = new HashMap<>();
            model.put("model", coursePreviewInfo);


            //生成静态页面，但字符串呈现
            String intoString = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            //将其写出到本地临时文件
            //创建一个空的本地临时文件
            htmlFile = File.createTempFile("course", ".html");
            //通过流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);//输出流
            InputStream inputStream = IOUtils.toInputStream(intoString);//输入流
            //将生成的静态html内容以流的形式写出到本地临时文件
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            log.error("课程静态化异常:{}", e.toString());
            XueChengPlusException.cast("课程静态化异常");
        }

        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {

        MultipartFile fileToUpload = MultipartSupportConfig.getMultipartFile(file);
        String objectName = "course/" + courseId + ".html";

        String course = mediaServiceClient.upload(fileToUpload, null, objectName);
        if (StringUtils.isEmpty(course)) {
            XueChengPlusException.cast("上传静态文件异常");
        }

    }

    /**
     * @param courseId 课程id
     * @return void
     * @description 保存消息表记录，稍后实现
     * @author Mr.M
     * @date 2022/9/20 16:32
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XueChengPlusException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}
