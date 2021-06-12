## Android中的注解与反射

--------
#### 1. 自定义注解
元注解：

元注解是用来定义其他注解的注解(在自定义注解的时候，需要使用到元注解来定义我们的注解)。java.lang.annotation提供了四种元注解：@Retention、 @Target、@Inherited、@Documented。

| 元注解 | 说明 |
| :-----| :---- |
| @Target | 表明我们注解可以出现的地方。是一个ElementType枚举 |
| @Retention | 这个注解的的存活时间 |
| @Document | 表明注解可以被javadoc此类的工具文档化 |
| @Inherited | 是否允许子类继承该注解，默认为false |


| @Target ElementType类型 | 说明 |
| :-----| :---- |
| ElementType.TYPE | 接口、类、枚举、注解 |
| ElementType.FIELD  |  字段  |
| ElementType.METHOD |  方法  |
| ElementType.PARAMETER  | 方法参数 |
| ElementType.CONSTRUCTOR | 构造函数 |
| ElementType.LOCAL_VARIABLE | 局部变量 |
| ElementType.ANNOTATION_TYPE | 注解 |
| ElementType.PACKAGE | 包 |



| @Retention RetentionPolicy类型  | 说明 |
| :-----| :---- |
| RetentionPolicy.SOURCE | 注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃 |
| RetentionPolicy.CLASS  | 注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期 |
| RetentionPolicy.RUNTIME | 注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在 |


#### 2.编写注解

自定义元注解
```java
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectEvent {
    String methodName();
    Class paramType();
}
```

绑定注解
```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindView {
    int value();
}
```

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@InjectEvent(methodName = "setOnClickListener",paramType = View.OnClickListener.class)
public @interface OnClick {

    int[] value();
}
```


#### 3.反射&注解的使用  

```java
public class AnnotationInject {
public static void injectEvent(Object object) {
        if (object == null) {
            return;
        }
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            //获取该方法上所有的注解
            Annotation[] annotations = method.getAnnotations();

            for (Annotation annotation : annotations) {
                //获取注解上的类型
                Class<?> annotationType = annotation.annotationType();
                //获取注解上对应的参数
                InjectEvent injectEvent = annotationType.getAnnotation(InjectEvent.class);
                if (injectEvent == null) {
                    continue;
                }

                //需要注入的方法名例如：setOnClickListener
                String methodName = injectEvent.methodName();
                //注入方法的参数类型，例如：View.OnClickListener.class
                Class listenerClass = injectEvent.paramType();

                Method annotationMethod = null;
                try {
                    annotationMethod = annotationType.getDeclaredMethod("value");
                    int[] resourcesIds = (int[]) annotationMethod.invoke(annotation);

                    for (int i = 0; i < resourcesIds.length; i++) {
                        Method findViewByIdMethod = clazz.getMethod("findViewById", int.class);
                        View view = (View) findViewByIdMethod.invoke(object, resourcesIds[i]);
                        //获取到获取方法setOnClickListener
                        Method setListener = view.getClass().getMethod(methodName, listenerClass);
                        //使用动态代理获取获取到OnClickListener代理对象
                        ListenerHandler eventInvocationHandler = new ListenerHandler(object, method);

                        Object proxy = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                                new Class[]{listenerClass}, eventInvocationHandler);

                        setListener.invoke(view, proxy);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        }

    }
}
```


#### 4.使用注解

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnnotationInject.injectView(this);
        AnnotationInject.injectEvent(this);
    }

    @OnClick(R.id.click1)
    public void onClick(View view){
        Log.v("liufuyi2","@OnClick(R.id.click1)");
    }

    @OnLongClick(R.id.click2)
    public boolean onLongClick(View view){
        Log.v("liufuyi2","@OnLongClick(R.id.click2)");
        return false;
    }
}
```
