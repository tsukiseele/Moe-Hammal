# <center>**Moe-Hammal​ Alpha**</center>
### Moe-Hammal是一个使用JavaFX开发的插件式的轻量级图片抓取应用，可以很容易实现ACG图片网站和漫画网站的资源抓取，并提供完善的下载功能。
![Moe-Hammal Alpha](https://raw.githubusercontent.com/TsukiSeele/Moe-Hammal/master/simple/simple_0.png)
***
1. 下载  

2. 注意事项  
  - 已安装Java运行时环境的，请跳到下一节。
如果电脑上未安装Java运行时环境，请点击[这里](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)下载，并自行配置好环境变量，方可正常运行本软件。

  - 推荐不熟悉的用户直接下载带运行时环境的整合版，请点击这里。

3. 自定义插件
  1. 温馨提示
编写规则需要**HTML**，**CSS选择器**，**正则表达式**，以及**JSON**的一些基础知识。如果你不懂相关编程知识，请不要尝试编写插件，亦或者为编写插件而特地去学这些知识，这只会浪费你宝贵的时间。  
  ***如果你满足了以上要求，并有一定耐心，请看下文。当你发现了优秀的站点，可以按以下教程编写相应的插件，在测试通过上传。***

  - 准备工作  
在开始编写之前，我们首先需要做的就是先用浏览器打开要写插件的网站

  - 规则基本信息  
这些信息定义在规则的最外层
```js
"title": "标题" // 定义站点的标题
"id": "规则标识码" // 规则的唯一标识
"version": "版本" // 规则版本号，用于更新
"remarks": "备注" // 规则备注，描述站点信息
```
  - 定义全局请求头
```js
// 链接请求头，可以在这里定义Cookie以供登陆
"requestHeaders": {
  "键": "值"
} 
```
  - 定义内容选择器  
所有的资源获取都是依靠内容选择器完成
```js
"homeSection" : {
  // 索引的URL规则，可以使用{page:a, b}以及{keyword:}占位符
  "indexUrl": "",
    // 在这里包裹画廊的选择器
    "gallerySelectors": {
      // 下面这行描述了获取封面图片链接的方式
      "previewUrl": {
        "selector": "$(.thumb > img).attr(src)"
      }
      // 在下面定义更多的内容选择器
    }
  }
}
```
- 作者的话  
如果你不懂这些内容，但仍然想贡献插件的话，可以考虑将站点提供给我，我会抽出空闲时间来完成编写并上传。
