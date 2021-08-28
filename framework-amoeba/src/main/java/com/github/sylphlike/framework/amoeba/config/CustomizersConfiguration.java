package com.github.sylphlike.framework.amoeba.config;

import com.github.pagehelper.PageInterceptor;
import com.github.sylphlike.framework.amoeba.datasource.DataBasesRoutInterceptor;
import com.github.sylphlike.framework.amoeba.datasource.MultiRouteDataSource;
import com.github.sylphlike.framework.amoeba.datasource.ResultSetHandlerInterceptor;
import com.github.sylphlike.framework.amoeba.datasource.StatementHandlerInterceptor;
import com.github.sylphlike.framework.amoeba.exception.MybatisException;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>  time 17:56 2019/01/29  星期五 </p>
 * <p> email 15923508369@163.com     </P>
 * @author Gopal.pan
 * @version 1.0.0
 */

@Component
public class CustomizersConfiguration extends Configuration implements EnvironmentAware {

    private final MultiRouteDataSource dataSource;
    public CustomizersConfiguration(MultiRouteDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String typeAliasesPackage;
    private String mapperLocations;

    @Override
    public void setEnvironment(Environment environment) {
        Binder binder = Binder.get(environment);
        MybatisConfigSupper mybatisConfigSupper = binder.bind("sylphlike.mybatis", MybatisConfigSupper.class).get();

        this.typeAliasesPackage = mybatisConfigSupper.getTypeAliasesPackage();
        this.mapperLocations = mybatisConfigSupper.getMapperLocations();

        super.setMapUnderscoreToCamelCase(true);
        super.setCacheEnabled(true);



    }



    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        if(!StringUtils.isEmpty(mapperLocations))
            bean.setMapperLocations(resolver.getResources(mapperLocations));

        List<Class<?>> typeAliases = getTypeAliases();
        if (!typeAliases.isEmpty()) {
            Class<?>[] classes = new Class[typeAliases.size()];
            for (int x = 0; x < typeAliases.size(); x++) {
                classes[x] = typeAliases.get(x);
            }
            bean.setTypeAliases(classes);
        }

        //分页插件
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        PageInterceptor pageHelper = new PageInterceptor();
        pageHelper.setProperties(properties);

        DataBasesRoutInterceptor dataBasesRoutInterceptor = new DataBasesRoutInterceptor();
        ResultSetHandlerInterceptor resultSetHandlerInterceptor = new ResultSetHandlerInterceptor();
        StatementHandlerInterceptor statementHandlerInterceptor = new StatementHandlerInterceptor();

        //添加插件
        bean.setPlugins(pageHelper,dataBasesRoutInterceptor,resultSetHandlerInterceptor,statementHandlerInterceptor);
        return bean.getObject();
    }


    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }




    private List<Class<?>> getTypeAliases() throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String[] escapePackages = typeAliasesPackage.split(",");
        String[] originalPackages = typeAliasesPackage.replace(".", "/").split(",");

        List<Class<?>> typeAliasesList = new ArrayList<>();
        for (int x = 0; x < originalPackages.length; x++) {
            Enumeration<URL> resources = classLoader.getResources(originalPackages[x]);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();

                if ("file".equals(protocol)) {
                    File file = new File(resource.getFile());
                    typeAliasesList.addAll(dealClassesFile(file, escapePackages[x]));
                } else if ("jar".equals(protocol)) {
                    String jarPath = resource.getPath();
                    typeAliasesList.addAll(dealJarClassesFile(jarPath, originalPackages[x]));
                }

            }
        }
        return typeAliasesList;

    }


    private static List<Class<?>> dealClassesFile(File directory, String packageName) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if(files == null){
            throw new MybatisException("file protocol not find");
        }
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(dealClassesFile(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;

    }

    /**
     * 从jar文件中读取指定目录下面的所有的class文件
     * @param jarPath  jar文件存放的位置
     * @param filePath 指定的文件目录
     * @return  java.util.List<java.lang.Class<?>>
     * @author  Gopal.pan
     */
    public static List<Class<?>> dealJarClassesFile(String jarPath, String filePath) {
        List<Class<?>> classes = new ArrayList<>();

        JarFile jarFile = null;
        try {

            URL url = new URL("jar", null, 0, jarPath);
            jarFile = ((JarURLConnection) url.openConnection()).getJarFile();

            List<JarEntry> jarEntryList = new ArrayList<JarEntry>();

            Enumeration<JarEntry> ee = jarFile.entries();
            while (ee.hasMoreElements()) {
                JarEntry entry = (JarEntry) ee.nextElement();
                if (entry.getName().startsWith(filePath) && entry.getName().endsWith(".class")) {
                    jarEntryList.add(entry);
                }
            }
            for (JarEntry entry : jarEntryList) {
                String className = entry.getName().replace('/', '.');
                className = className.substring(0, className.length() - 6);

                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (null != jarFile) {
                try {
                    jarFile.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return classes;
    }
}
