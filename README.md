# Moe-Hammal​ Alpha
### Moe-Hammal是一个使用JavaFX开发的插件式的图片抓取应用，可以很容易实现ACG图片网站和漫画网站的资源抓取，并提供完善的下载功能

![Moe-Hammal Alpha](https://raw.githubusercontent.com/TsukiSeele/Moe-Hammal/master/simple/simple_0.png)
1. 下载

2. 注意事项
如果电脑上未安装Java运行时环境，请到这里下载，并自行配置环境变量。或者下载带运行时环境的版本，点这里。

3. 自定义插件
如果你发现了优秀的站点，可以按以下教程编写相应的插件，并找我发布。
如果你学过JavaScript或者知道JSON这种数据储存格式，那么理解插件结构应该会很轻松。
```
"groupUrl": {
                "selector": "$(div.inner > a).attr(href)",
                "capture": "(.*)",
                "replacement": "https://yande.re/$1"
            }
```

如上图的数据格式你能看懂，那么你可以继续往下看，否则请学习JSON数据格式后继续进行学习
，当然我个人也不赞成用户为编写插件而特地去学这类知识。
如果你不懂这些东西，但仍然想贡献插件的话，可以考虑将站点提供给我，我会抽出空余时间来完成编写并发布。

4. 除此之外，编写规则还需要HTML，CSS选择器的一些基础知识
