# HourCounter
 多媒体工时小助手，用于帮助计算工时。

- 导入表格：导入通讯录、值班表、常检表，以及对应的有效时间；
- 设置日期：设置结算的起止日期和常检和值班的特殊休息或调换；
- 其他工时：增加一些其他的工时项目，比如测光、参与抽查等等；
- 导出表格：导出工资明细表，然后根据工资明细表生成劳务发放表。

![image-20210603172933755](C:\Users\18196\AppData\Roaming\Typora\typora-user-images\image-20210603172933755.png)



## 环境

以下是推荐配置：

- JDK 1.8
- JavaFX 11
- Maven 3.8
- POI 5.0

在编写项目前请确保上述环境都被正确配置，可以去查Bing。



## 开始

以下说明如何使用Idea打开并编写此项目，有不会的地方可以查Bing：

1. Idea导入已有的Maven项目，选择此项目的根目录`HourCounter`即可导入代码项目；

2. 下载poi包（用来读写excel的），然后项目导入poi解压缩后文件夹里面所有的jar，作为外部jar。注意，根目录下还有一些lib、auxiliary等等的子文件夹，所有子文件夹里面的jar都要导入；

   ![image-20210603174222511](C:\Users\18196\AppData\Roaming\Typora\typora-user-images\image-20210603174222511.png)

3. Idea不报错，基本上就算配置好了。接下来可以自己改代码。



## 导出.jar

导出可执行的.jar文件，Bing搜索：Idea导出JavaFX application

大致步骤是：

1. Idea在此项目下，进入Program structure界面；
2. 选artifact选项卡，点加号，选择JavaFx application；
3. 根据网上资料完成后续步骤。按下面的教程来即可，但是注意前两步根据上面2选择JavaFX application，而不是jar

[IDEA导出jar打包成exe应用程序的小结_java_脚本之家 (jb51.net)](https://www.jb51.net/article/194317.htm)

## 导出exe

下载exe4j软件，用来将这些东西jar转成exe，搜索：多个jar转exe

[exe4j 打包（多个jar打包）_正怒月神的博客-CSDN博客](https://blog.csdn.net/hanjun0612/article/details/100665263)



## 导出单个exe

下载enigma virtual box，根据网上教程将上步的多个文件打包成单个exe

[Qt程序通过enigma virtual box制作成单文件可执行程序*.exe（封包）_龚建波-CSDN博客](https://blog.csdn.net/gongjianbo1992/article/details/80863247)

