package com.lagou.edu.factory;

import com.lagou.edu.factory.Annotation.Service;
import com.lagou.edu.factory.Annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author 应癫
 * <p>
 * 工厂类，生产对象（使用反射技术）
 */
@Slf4j
public class BeanFactory {

    /**
     * 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
     * 任务二：对外提供获取实例对象的接口（根据id获取）
     */

    private static Map<String, Object> map = new HashMap<>();  // 存储对象

    private static Map<String,BeanDefinition> beanDefinitionMap=new LinkedHashMap<>();


    static {
        // 任务一：读取解析xml，通过反射技术实例化对象并且存储待用（map集合）
        // 加载xml
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        // 解析xml
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();

            List<Element> annoFlag =null;
            if ((annoFlag=rootElement.selectNodes("//component-scan")) != null) {
                annotationConfigBeanParser(annoFlag.get(0));
            }

            xmlConfigBeanParser(rootElement);



        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void xmlConfigBeanParser(Element rootElement) throws Exception {
        List<Element> beanList = rootElement.selectNodes("//bean");
        for (int i = 0; i < beanList.size(); i++) {
            Element element = beanList.get(i);
            // 处理每个bean元素，获取到该元素的id 和 class 属性
            String id = element.attributeValue("id");        // accountDao
            String clazz = element.attributeValue("class");  // com.lagou.edu.dao.impl.JdbcAccountDaoImpl
            // 通过反射技术实例化对象
            Class<?> aClass = Class.forName(clazz);
            Object o = aClass.newInstance();  // 实例化之后的对象

            // 存储到map中待用
            map.put(id, o);

        }

        // 实例化完成之后维护对象的依赖关系，检查哪些对象需要传值进入，根据它的配置，我们传入相应的值
        // 有property子元素的bean就有传值需求
        List<Element> propertyList = rootElement.selectNodes("//property");
        // 解析property，获取父元素
        for (int i = 0; i < propertyList.size(); i++) {
            Element element = propertyList.get(i);   //<property name="AccountDao" ref="accountDao"></property>
            String name = element.attributeValue("name");
            String ref = element.attributeValue("ref");

            // 找到当前需要被处理依赖关系的bean
            Element parent = element.getParent();

            // 调用父元素对象的反射功能
            String parentId = parent.attributeValue("id");
            Object parentObject = map.get(parentId);
            // 遍历父对象中的所有方法，找到"set" + name
            Method[] methods = parentObject.getClass().getMethods();
            for (int j = 0; j < methods.length; j++) {
                Method method = methods[j];
                if (method.getName().equalsIgnoreCase("set" + name)) {  // 该方法就是 setAccountDao(AccountDao accountDao)
                    method.invoke(parentObject, map.get(ref));
                }
            }

            // 把处理之后的parentObject重新放到map中
            map.put(parentId, parentObject);
        }
    }

    private static void annotationConfigBeanParser(Element annoFlag) throws Exception {

        Attribute attribute = annoFlag.attribute("base-package");
        String basePacckagePath=attribute.getValue();
        basePacckagePath=basePacckagePath.replaceAll("\\.","/");
        URL resource = Thread.currentThread().getContextClassLoader().getResource(basePacckagePath);
        File file = new File(resource.toURI());
        Set<File> sourceFiles=new LinkedHashSet<File>();
        getSourceFiles(file,sourceFiles);
        for (File f:sourceFiles){

            //log.info(sourceFiles.toString()+"thread"+Thread.currentThread().getId());
            BeanDefinition definition=null;
            String className=genClassNameFromPath(f.getPath(),basePacckagePath);

            //log.info("(((((("+className+"&&"+f+"thread"+Thread.currentThread().getId());

            /*FileSystemClassLoader instance = FileSystemClassLoader.getInstance(className, f);
            Class<?> aClass = instance.loadClass(className);*/
            Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(className);
         /*   if (aClass==null){
                continue;
            }*/
            Annotation[] annotations = aClass.getAnnotations();
            boolean transctional=false;
            for (Annotation a:annotations){
                if ("com.lagou.edu.factory.Annotation.Service".equals(a.annotationType().getName())){
                    Service annotation = aClass.getAnnotation(Service.class);
                    if (annotation==null){
                        log.info(className+aClass.getName());
                    }
                    String value = annotation.value();
                    definition=new BeanDefinition();
                    definition.setClassType(aClass);
                    if (value!=null&&value.length()!=0){
                        definition.setBeanId(value);
                    }else{
                        definition.setBeanId(aClass.getSimpleName().substring(0,1).toLowerCase()+aClass.getSimpleName().substring(1));
                    }
                }

                if ("com.lagou.edu.factory.Annotation.Transactional".equals(a.annotationType().getName())){
                    transctional=true;
                }

            }


            if(definition==null){
                continue;
            }

            if (transctional){
                definition.setTrasational(true);
            }
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field field:declaredFields){
                Annotation[] annotations1 = field.getAnnotations();
                for (Annotation an:annotations1){

                    if ("com.lagou.edu.factory.Annotation.Autowired".equals(an.annotationType().getName())){
                        Map<String,PropertyDescriptor> wireMap=new LinkedHashMap<>();
                        Class<?> type = field.getType();
                        wireMap.put(type.getName(),new PropertyDescriptor(field.getName(),aClass));
                     definition.setAutowiredFieldMap(wireMap);
                    }
                }
            }

            if (definition!=null){
                Class<?>[] interfaces = aClass.getInterfaces();
                for (Class<?> s:interfaces){
                    beanDefinitionMap.put(s.getName(),definition);
                }

                if (interfaces==null||interfaces.length==0){
                    beanDefinitionMap.put(aClass.getName(),definition);
                }


            }
        }


        for (Map.Entry<String,BeanDefinition> bean:beanDefinitionMap.entrySet()){
            BeanDefinition value = bean.getValue();
            createBeanCurv(value,map);
        }


        ProxyFactory proxyFactory=(ProxyFactory)map.get( beanDefinitionMap.get("com.lagou.edu.factory.ProxyFactory").getBeanId());
        for (BeanDefinition definition:beanDefinitionMap.values()){
            if (definition.isTrasational()) {
                Object o = map.get(definition.getBeanId());
                Object jdkProxy = proxyFactory.getJdkProxy(o);
                map.put(definition.getBeanId(),jdkProxy);
            }
        }
    }

    private static void createBeanCurv(BeanDefinition value,Map<String, Object> map) throws Exception {
        Object o = value.getClassType().newInstance();
        if (value.getAutowiredFieldMap()!=null){
            Map<String, PropertyDescriptor> autowiredFieldMap = value.getAutowiredFieldMap();
            for (Map.Entry<String,PropertyDescriptor> innerBean:autowiredFieldMap.entrySet()){
                BeanDefinition definition = beanDefinitionMap.get(innerBean.getKey());
                if (!map.containsKey(definition.getBeanId())){
                    createBeanCurv(definition,map);
                }

                PropertyDescriptor value1 = innerBean.getValue();
                value1.getWriteMethod().invoke(o,map.get(definition.getBeanId()));
            }
        }

        map.put(value.getBeanId(),o);
    }

    // 任务二：对外提供获取实例对象的接口（根据id获取）
    public static Object getBean(String id) {
        return map.get(id);
    }

    private static void getSourceFiles(File rootFile,Set<File> result){
        if(rootFile.canRead()){
            for(File file1 :rootFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    if (file.getPath().indexOf("servlet")!=-1){
                        return false;
                    }
                    return true;
                }
            })){
                if (file1.isDirectory()){
                    getSourceFiles(file1,result);
                }else{
                    result.add(file1);
                }
            }
        }

    }

    private static String genClassNameFromPath(String path,String basePackage){
        //log.info("****"+path+"))))))))"+basePackage+"thread"+Thread.currentThread().getId());
        String substring = path.substring(path.indexOf(basePackage.replaceAll("/", Matcher.quoteReplacement(File.separator))));
        String replace = substring.replace(".class", "");
        String[] split = replace.split(Matcher.quoteReplacement(File.separator));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i <split.length ; i++) {
            if (i==split.length-1){
                stringBuilder.append(split[i]);
            }else{
                stringBuilder.append(split[i]).append((char) 46);
            }
        }

        //log.info("+++++++++++"+stringBuilder.toString()+"thread"+Thread.currentThread().getId());

        return stringBuilder.toString();
    }
}
