package com.butterknife.compiler;


import com.butterknife.annotations.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Author      :ljb
 * Date        :2018/7/19
 * Description :
 */
@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {
    private Filer mFiler;
    private Elements mElementUtils;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
    }

    // 1.指定处理的版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    // 2. 给到需要处理的注解
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        Set<Class<? extends Annotation>> annotations =  getSupportedAnnotatios();
        for (Class<? extends Annotation> annotation : annotations) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotatios() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        // 需要解析的自定义注解 Onclick
        annotations.add(BindView.class);
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("进来没有");
        // process方法代表的是，有注解就都会进来，但是这里面是一团乱麻
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
//        for (Element element : elements) {
//            System.out.println("simpleName: "+element.getSimpleName().toString()); // 得到使用BindView注解的属性的属性名
//            Element enclosingElement = element.getEnclosingElement();
//            System.out.println("enclosingElementSimpleName: "+enclosingElement.getSimpleName().toString());//得到次属性所在的类名
//        }
        // 解析 属性 得到activity --->List<Element>（使用BindView的属性）
        Map<Element, List<Element>> elementsMap = new LinkedHashMap<>();
        for (Element element : elements) {
            Element enclosingElement = element.getEnclosingElement(); // 对应着activity
            List<Element> viewBindElements = elementsMap.get(enclosingElement);
            if (viewBindElements == null) {
                viewBindElements = new ArrayList<>();
                elementsMap.put(enclosingElement, viewBindElements);
            }
            viewBindElements.add(element);
        }
        // 生成代码
        for (Map.Entry<Element, List<Element>> entry : elementsMap.entrySet()) {
            Element enclosingElement = entry.getKey(); // 对应activity
            List<Element> viewBindElements = entry.getValue(); // 对应activity中使用BindView注解的属性的集合
            // activity 类名
            String activityClassNameStr = enclosingElement.getSimpleName().toString();
            ClassName activityClassName = ClassName.bestGuess(activityClassNameStr);
            ClassName unbindClassName = ClassName.get("com.butterknife", "Unbinder");
            // 构建类
            TypeSpec.Builder classBuilder = TypeSpec.classBuilder(activityClassNameStr + "_ViewBinding").addModifiers
                    (Modifier.FINAL, Modifier.PUBLIC)// 构建类名
                    .addField(activityClassName, "target", Modifier.PRIVATE)// 添加属性
                    .addSuperinterface(unbindClassName);//实现接口
            // 实现unbind方法
            ClassName callSuperClassName = ClassName.get("android.support.annotation", "CallSuper");
            MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")//
                    .addAnnotation(Override.class)//
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)//
                    .addAnnotation(callSuperClassName);


            // 构建构造函数
            MethodSpec.Builder constructorMethod = MethodSpec.constructorBuilder()//
                    .addParameter(activityClassName, "target");

            //在构造函数中添加this.target=target代码
            constructorMethod.addStatement("this.target=target");
            // 在unbind方法中添加相关代码
            unbindMethodBuilder.addStatement("$T target = this.target", activityClassName);
            unbindMethodBuilder.addStatement("if (target == null) throw new IllegalStateException(\"Bindings already "
                    + "cleared.\")");
            unbindMethodBuilder.addStatement("this.target = null");

            // findViewById
            for (Element viewBindElement : viewBindElements) {
                // 在构造方法中添加target.textView1 = Utils.findViewById(source,R.id.tv1);代码
                String filedName = viewBindElement.getSimpleName().toString();
                ClassName utilsClassName = ClassName.get("com.butterknife", "Utils");
                int resId = viewBindElement.getAnnotation(BindView.class).value();
                constructorMethod.addStatement("target.$L = $T.findViewById(target,$L)", filedName, utilsClassName, resId);

                // 在unbind方法中添加target.textView1 = null;代码
                unbindMethodBuilder.addStatement("target.$L = null", filedName);
            }

            // 添加unbind方法
            classBuilder.addMethod(unbindMethodBuilder.build());
            // 添加构造方法
            classBuilder.addMethod(constructorMethod.build());

            // 生成类
            try {
                String packageName = mElementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
                JavaFile.builder(packageName, classBuilder.build())//
                        .addFileComment("butterknife 自动生成")//
                        .build()//
                        .writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException: " + e);
            }
        }

        return false;
    }
}
