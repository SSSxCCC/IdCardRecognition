# IdCardRecognition

一个基于tesserect的身份证识别安卓app。

介绍博客：https://blog.csdn.net/SSSxCCC/article/details/119000743

chi_sim.traineddata来自：https://github.com/tesseract-ocr/tessdata/blob/master/chi_sim.traineddata

## 运行

下载源代码。

在Android Studio中打开IdCardRecognition项目。

用USB线连接一台安卓手机。

点击“Run 'app'”。

## 演示

扫描身份证（Scan id card）：

![image](https://github.com/SSSxCCC/IdCardRecognition/raw/master/demo/1.jpg)

识别结果（Recognition result）：

![image](https://github.com/SSSxCCC/IdCardRecognition/raw/master/demo/2.jpg)

## 待写

* 要相机正确的全屏显示

* 更换调用相机接口为Camera2

* 保证ocr识别在初始化tessdata完成后进行

* 将java换成kotlin
