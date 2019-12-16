package com.preapm.agent.util;

import com.preapm.agent.bean.AopExpressBean;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class YmlConfigUtil {

    /**
     * key:文件名索引
     * value:配置文件内容
     */
    private static Map<String, LinkedHashMap> ymls = new HashMap<>();

    /**
     * string:当前线程需要查询的文件名
     */
    private static ThreadLocal<String> nowFileName = new ThreadLocal<>();

    /**
     * 加载配置文件
     * @param fileName
     */
    public static void loadYml(String fileName) {
        nowFileName.set(fileName);
        if (!ymls.containsKey(fileName)) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(new File(ClassLoaderUtil.getJARPath() + "/" + fileName));
            } catch (FileNotFoundException e) {
                ymls.put(fileName, new Yaml().loadAs(YmlConfigUtil.class.getResourceAsStream("/" + fileName), LinkedHashMap.class));
            }
            ymls.put(fileName, new Yaml().loadAs(fileInputStream, LinkedHashMap.class));
        }
    }

    public static Object getValue(String key){
        // 首先将key进行拆分
        String[] keys = key.split("[:]");

        // 将配置文件进行复制
        Map ymlInfo = (Map) ymls.get(nowFileName.get()).clone();
        for (int i = 0; i < keys.length; i++) {
            Object value = ymlInfo.get(keys[i]);
            if (i < keys.length - 1) {
                ymlInfo = (Map) value;
            } else if (value == null) {
//                throw new Exception("key不存在");
                return null;
            } else {
                return value;
            }
        }
        throw new RuntimeException("不可能到这里的...");
    }

    public static Object getValue(String fileName, String key) throws Exception {
        // 首先加载配置文件
        loadYml(fileName);
        return getValue(key);
    }


    public static List<AopExpressBean> getAopExpressBeanList() throws Exception {

        YmlConfigUtil.loadYml("test.yml");
        LinkedHashMap value = (LinkedHashMap) YmlConfigUtil.getValue("target:set");

        ArrayList<AopExpressBean> aopExpressBeans = new ArrayList<>();

        value.keySet().forEach(key ->{
            try {
                AopExpressBean aopExpressBean = new AopExpressBean();
                String includeStr = "target:set" + ":" + key + ":includeMethods";

                String includeMethodsStr = (String) YmlConfigUtil.getValue(includeStr);
                String[] includeMethodArr = includeMethodsStr.split(",");
                Set<String> includeMethodSet = new LinkedHashSet<>();
                for (int i = 0; i < includeMethodArr.length; i++) {
                    includeMethodSet.add(includeMethodArr[i]);
                }

                String excludeStr = "target:set" + ":" + key + ":excludeMethods";
                String excludeMethodsStr = (String) YmlConfigUtil.getValue(excludeStr);
                String[] excludeMethodsArr = excludeMethodsStr.split(",");
                Set<String> excludeMethodSet = new LinkedHashSet<>();
                for (int i = 0; i < excludeMethodsArr.length; i++) {
                    excludeMethodSet.add(excludeMethodsArr[i]);
                }
                aopExpressBean.setIncludeMethods(includeMethodSet);
                aopExpressBean.setExcludeMethods(excludeMethodSet);
                aopExpressBeans.add(aopExpressBean);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return aopExpressBeans;
    }


    public static void main(String[] args) throws Exception {

        YmlConfigUtil.loadYml("test.yml");

        LinkedHashMap value = (LinkedHashMap) YmlConfigUtil.getValue("target:set");

        value.keySet().forEach(key ->{
            try {

                System.out.println(key);
                AopExpressBean aopExpressBean = new AopExpressBean();
                String includeStr = "target:set" + ":" + key + ":includeMethods";

                String includeMethodsStr = (String) YmlConfigUtil.getValue(includeStr);
                String[] includeMethodArr = includeMethodsStr.split(",");
                Set<String> includeMethodSet = new LinkedHashSet<>();
                for (int i = 0; i < includeMethodArr.length; i++) {
                    includeMethodSet.add(includeMethodArr[i]);
                }

                String excludeStr = "target:set" + ":" + key + ":excludeMethods";
                String excludeMethodsStr = (String) YmlConfigUtil.getValue(excludeStr);
                String[] excludeMethodsArr = excludeMethodsStr.split(",");
                Set<String> excludeMethodSet = new LinkedHashSet<>();
                for (int i = 0; i < excludeMethodsArr.length; i++) {
                    excludeMethodSet.add(excludeMethodsArr[i]);
                }
                aopExpressBean.setIncludeMethods(includeMethodSet);
                aopExpressBean.setExcludeMethods(excludeMethodSet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
