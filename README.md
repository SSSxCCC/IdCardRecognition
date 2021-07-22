# IdCardRecognition

An android id card recognition app based on tesserect.

## Run

Download source code.

Open IdCardRecognition project in Android Studio.

Connect an android phone to your PC by USB.

Click "Run 'app'".

---

一个基于tesserect的身份证识别安卓app。

注：chi_sim.traineddata来自https://github.com/tesseract-ocr/tessdata/blob/master/chi_sim.traineddata

## 运行

下载源代码。

在Android Studio中打开IdCardRecognition项目。

用USB线连接一台安卓手机。

点击“Run 'app'”。

## 示例

扫描身份证（Scan id card）：

![image](https://github.com/SSSxCCC/IdCardRecognition/raw/master/demo/1.jpg)

识别结果（Recognition result）：

![image](https://github.com/SSSxCCC/IdCardRecognition/raw/master/demo/2.jpg)

## 待写

* 要相机正确的全屏显示

* 更换调用相机接口为Camera2

* 保证ocr识别在初始化tessdata完成后进行

* 将java换成kotlin
