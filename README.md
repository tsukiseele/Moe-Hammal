# Moe-Hammal​ 
### Moe-Hammal是一个使用JavaFX开发的插件式的在线图片阅读程序，可以提供JSON配置图片的抓取规则
![Moe-Hammal](https://raw.githubusercontent.com/TsukiSeele/Moe-Hammal/master/simple/simple_0.jpg) 
***
1. 下载发布版本

2. 注意事项  
  - 安装Java运行时环境

3. 自定义插件
  - 须知
  编写规则需要**HTML**，**CSS选择器**，**正则表达式**，以及**JSON**等知识。
  ***但如果你满足了以上要求，并有一定耐心，请看下文。当你发现了优秀的站点，可以按以下教程来编写相应的插件。***

  - 准备工作  
在开始编写之前，首先用浏览器打开要编写对应插件的网站

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
// 主页部分定义
"homeSection" : {
    // 索引的URL规则，可以使用{page:a, b}
}
