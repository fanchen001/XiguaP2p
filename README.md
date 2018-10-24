# XiguaP2p
【次元番】使用的西瓜视频P2P库，用来缓存  xg://  xgadd://  xgplay://  开头的ftp视频文件连接。并提供本地播放链接代理，实现边下边播的功能

基本使用
========
XiguaProvider已经完成过库的初始化，所以无需重复调用P2PManager.getInstance().init(getContext());  <br/>
P2PManager.getInstance().play("");//开始下载/播放西瓜ftp视频文件<br/>
P2PManager.getInstance().stop("");//停止<br/>
P2PManager.getInstance().remove("");删除<br/>

注意
======
因为[P2PService](https://github.com/fanchen001/XiguaP2p/blob/master/xigua/src/main/java/com/xigua/p2p/P2PService.java)运行于单独的进程，
所以所有对[P2PManager](https://github.com/fanchen001/XiguaP2p/blob/master/xigua/src/main/java/com/xigua/p2p/P2PManager.java)的操作结果，
均以广播的形式反馈给调用者。具体广播参数，请看[P2PMessageWhat](https://github.com/fanchen001/XiguaP2p/blob/master/xigua/src/main/java/com/xigua/p2p/P2PMessageWhat.java)，
其他使用方法，请查看[次元番](https://github.com/fanchen001/Bangumi)

添加依赖
========


Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.fanchen001:XiguaP2p:1.10.25'
	}
