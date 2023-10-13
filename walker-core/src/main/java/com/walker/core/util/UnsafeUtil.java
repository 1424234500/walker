package com.walker.core.util;


import com.walker.core.mode.Emp;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

/**
 * unsafe问题分析 测试
 * <p>
 * Unsafe类使Java拥有了像C语言的指针一样操作内存空间的能力，一旦能够直接操作内存，这也就意味着（1）不受jvm管理，也就意味着无法被GC，需要我们手动GC，稍有不慎就会出现内存泄漏。（2）Unsafe的不少方法中必须提供原始地址(内存地址)和被替换对象的地址，偏移量要自己计算，一旦出现问题就是JVM崩溃级别的异常，会导致整个JVM实例崩溃，表现为应用程序直接crash掉。（3）直接操作内存，也意味着其速度更快，在高并发的条件之下能够很好地提高效率。
 * <p>
 * 因此，从上面三个角度来看，虽然在一定程度上提升了效率但是也带来了指针的不安全性。
 */
public class UnsafeUtil {
    private static final Unsafe unsafe;

    /**
     * spring工具写法
     */
    static {
        Field f;
        try {
            f = Unsafe.class.getDeclaredField("theUnsafe");
        } catch (NoSuchFieldException var3) {
            throw new RuntimeException(var3);
        }

        f.setAccessible(true);

        try {
            unsafe = (Unsafe) f.get(null);
        } catch (IllegalAccessException var2) {
            throw new RuntimeException(var2);
        }
    }

    public UnsafeUtil() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, InterruptedException {

        testArray();

        testObject();

        testMem();

        testCas();

        testFiled();
    }

    public static void main(String[] argv) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException, NoSuchFieldException, InterruptedException {
        new UnsafeUtil();
    }

    /**
     * 获取unsafe
     * Caused by: java.lang.SecurityException: Unsafe
     */
//    Unsafe unsafe = Unsafe.getUnsafe();
    public static Unsafe getUnsafe1() throws NoSuchFieldException, IllegalAccessException {
        //最简单的使用方式是基于反射获取Unsafe实例
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        return unsafe;
    }

    public static Unsafe getUnsafe() {
        return unsafe;
    }

    /**
     * 常量获取
     * <p>
     * 可以获取地址大小（addressSize），页大小（pageSize），基本类型数组的偏移量
     * （Unsafe.ARRAY_INT_BASE_OFFSET\Unsafe.ARRAY_BOOLEAN_BASE_OFFSET等）、
     * 基本类型数组内元素的间隔（Unsafe.ARRAY_INT_INDEX_SCALE\Unsafe.ARRAY_BOOLEAN_INDEX_SCALE等）
     */
    private void testFiled() throws InterruptedException {

//get os address size
        System.out.println("address size is :" + unsafe.addressSize());
//get os page size
        System.out.println("page size is :" + unsafe.pageSize());
//int array base offset
        System.out.println("unsafe array int base offset:" + Unsafe.ARRAY_INT_BASE_OFFSET);


/**
 * 线程许可
 * 许可线程通过（park），或者让线程等待许可(unpark)，
 */
        Thread packThread = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            //纳秒，相对时间park
            unsafe.park(false, 3000000000L);
            //毫秒，绝对时间park
            //unsafe.park(true,System.currentTimeMillis()+3000);

            System.out.println("main thread end,cost :" + (System.currentTimeMillis() - startTime) + "ms");
        });
        packThread.start();
        TimeUnit.SECONDS.sleep(1);
//注释掉下一行后，线程3秒数后进行输出,否则在1秒后输出
        unsafe.unpark(packThread);
    }

    /**
     * CAS操作
     * Compare And Swap（比较并交换），当需要改变的值为期望的值时，那么就替换它为新的值，是原子
     * （不可在分割）的操作。很多并发框架底层都用到了CAS操作，CAS操作优势是无锁，可以减少线程切换耗费
     * 的时间，但CAS经常失败运行容易引起性能问题，也存在ABA问题。在Unsafe中包含compareAndSwapObject、
     * compareAndSwapInt、compareAndSwapLong三个方法，compareAndSwapInt的简单示例如下。
     */
    private void testCas() throws NoSuchFieldException {
        Emp data = new Emp();
        data.setId("1");
        Field id = data.getClass().getDeclaredField("id");
        long l = unsafe.objectFieldOffset(id);
        id.setAccessible(true);
//比较并交换，比如id的值如果是所期望的值1，那么就替换为2，否则不做处理
        unsafe.compareAndSwapLong(data, 1L, 1L, 2L);
        System.out.println(data.getId());
    }

    private void testMem() {


/**
 * 内存操作
 * 可以在Java内存区域中分配内存（allocateMemory），设置内存（setMemory，用于初始化），
 * 在指定的内存位置中设置值（putInt\putBoolean\putDouble等基本类型）
 */
//分配一个8byte的内存
        long address = unsafe.allocateMemory(8L);
//初始化内存填充1
        unsafe.setMemory(address, 8L, (byte) 1);
//测试输出
        System.out.println("add byte to memory:" + unsafe.getInt(address));
//设置0-3 4个byte为0x7fffffff
        unsafe.putInt(address, 0x7fffffff);
//设置4-7 4个byte为0x80000000
        unsafe.putInt(address + 4, 0x80000000);
//int占用4byte
        System.out.println("add byte to memory:" + unsafe.getInt(address));
        System.out.println("add byte to memory:" + unsafe.getInt(address + 4));
    }

    private void testArray() {
        /**
         * 操作数组:
         * 可以获取数组的在内容中的基本偏移量（arrayBaseOffset），获取数组内元素的间隔（比例），
         * 根据数组对象和偏移量获取元素值（getObject），设置数组元素值（putObject），示例如下。
         */
        String[] strings = new String[]{"1", "2", "3"};
        long i = unsafe.arrayBaseOffset(String[].class);
        System.out.println("string[] base offset is :" + i);

//every index scale
        long scale = unsafe.arrayIndexScale(String[].class);
        System.out.println("string[] index scale is " + scale);

//print first string in strings[]
        System.out.println("first element is :" + unsafe.getObject(strings, i));

//set 100 to first string
        unsafe.putObject(strings, i + scale * 0, "100");

//print first string in strings[] again
        System.out.println("after set ,first element is :" + unsafe.getObject(strings, i + scale * 0));


    }

    /**
     * 对象操作
     * 实例化Data
     * <p>
     * 可以通过类的class对象创建类对象（allocateInstance），获取对象属性的偏移量（objectFieldOffset）
     * ，通过偏移量设置对象的值（putObject）
     * <p>
     * 对象的反序列化
     * 当使用框架反序列化或者构建对象时，会假设从已存在的对象中重建，你期望使用反射来调用类的设置函数，
     * 或者更准确一点是能直接设置内部字段甚至是final字段的函数。问题是你想创建一个对象的实例，
     * 但你实际上又不需要构造函数，因为它可能会使问题更加困难而且会有副作用。
     */
    private void testObject() throws IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {

//调用allocateInstance函数避免了在我们不需要构造函数的时候却调用它
        Emp data = (Emp) unsafe.allocateInstance(Emp.class);
        data.setId("1");
        data.setName("unsafe");
        System.out.println(data);

//返回成员属性在内存中的地址相对于对象内存地址的偏移量
        Field nameField = Emp.class.getDeclaredField("name");
        long fieldOffset = unsafe.objectFieldOffset(nameField);
//putLong，putInt，putDouble，putChar，putObject等方法，直接修改内存数据（可以越过访问权限）
        unsafe.putObject(data, fieldOffset, "这是新的值");
        System.out.println(data.getName());


/**
 * 我们可以在运行时创建一个类，比如从已编译的.class文件中。将类内容读取为字节数组，
 * 并正确地传递给defineClass方法；当你必须动态创建类，而现有代码中有一些代理， 这是很有用的
 */
        File file = new File("C:\\workspace\\idea2\\disruptor\\target\\classes\\com\\onyx\\distruptor\\test\\Emp.class");
        FileInputStream input = new FileInputStream(file);
        byte[] content = new byte[(int) file.length()];
        input.read(content);
        Class c = unsafe.defineClass(null, content, 0, content.length, null, null);
        c.getMethod("getId").invoke(c.newInstance(), null);

    }


//    在这里我们看到Unsafe的初始化方法主要是通过getUnsafe方法的单例模式实现的，调用JVM本地方法registerNatives()和sun.reflect.Reflection，通过Reflection的getCallerClass判断当前调用的类是否是主类加载器(BootStrap classLoader)加载的，否则的话抛出一个SecurityException。这也证明了一个问题，那就是只有由主类加载器(BootStrap classLoader)加载的类才能调用这个类中的方法。
//
//            2、操作属性方法
//
//（1）public native Object getObject(Object o, long offset);
//
//    通过给定的Java变量获取引用值。这里实际上是获取一个Java对象o中，获取偏移地址为offset的属性的值，此方法可以突破修饰符的抑制，也就是无视private、protected和default修饰符。类似的方法有getInt、getDouble等等。同理还有putObject方法。
//
//            （2）public native Object getObjectVolatile(Object o, long offset);
//
//    强制从主存中获取属性值。类似的方法有getIntVolatile、getDoubleVolatile等等。同理还有putObjectVolatile。
//
//            （3）public native void putOrderedObject(Object o, long offset, Object x);
//
//    设置o对象中offset偏移地址offset对应的Object型field的值为指定值x。这是一个有序或者有延迟的putObjectVolatile方法，并且不保证值的改变被其他线程立即看到。只有在field被volatile修饰并且期望被修改的时候使用才会生效。类似的方法有putOrderedInt和putOrderedLong。
//
//            （4）public native long staticFieldOffset(Field f);
//
//    返回给定的静态属性在它的类的存储分配中的位置(偏移地址)。
//
//            （5）public native long objectFieldOffset(Field f);
//
//    返回给定的非静态属性在它的类的存储分配中的位置(偏移地址)。
//
//            （6）public native Object staticFieldBase(Field f);
//
//    返回给定的静态属性的位置，配合staticFieldOffset方法使用。
//
//            3、操作数组
//
//（1）public native int arrayBaseOffset(Class arrayClass);
//
//    返回数组类型的第一个元素的偏移地址(基础偏移地址)。
//
//            （2）public native int arrayIndexScale(Class arrayClass);
//
//    返回数组中元素与元素之间的偏移地址的增量。
//
//    这两个方法配合使用就可以定位到任何一个元素的地址。
//
//            4、内存管理
//
//（1）public native int addressSize();
//
//    获取本地指针的大小(单位是byte)，通常值为4或者8。常量ADDRESS_SIZE就是调用此方法。
//
//            （2）public native int pageSize();
//
//    获取本地内存的页数，此值为2的幂次方。
//
//            （3）public native long allocateMemory(long bytes);
//
//    分配一块新的本地内存，通过bytes指定内存块的大小(单位是byte)，返回新开辟的内存的地址。
//
//            （4）public native long reallocateMemory(long address, long bytes);
//
//    通过指定的内存地址address重新调整本地内存块的大小，调整后的内存块大小通过bytes指定(单位为byte)。
//
//            （5）public native void setMemory(Object o, long offset, long bytes, byte value);
//
//    将给定内存块中的所有字节设置为固定值(通常是0)。
//
//            5、线程挂起和恢复
//
//（1）public native void unpark(Object thread);
//
//    释放被park创建的在一个线程上的阻塞。由于其不安全性，因此必须保证线程是存活的。
//
//            （2）public native void park(boolean isAbsolute, long time);`
//
//阻塞当前线程，一直等道unpark方法被调用。
//
//6、内存屏障
//
//（1）public native void loadFence();
//
//在该方法之前的所有读操作，一定在load屏障之前执行完成。
//
//（2）public native void storeFence();
//
//在该方法之前的所有写操作，一定在store屏障之前执行完成
//
//（3）public native void fullFence();
//
//在该方法之前的所有读写操作，一定在full屏障之前执行完成，这个内存屏障相当于上面两个(load屏障和store屏障)的合体功能。
}
