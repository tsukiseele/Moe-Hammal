var page = require('webpage').create();
var system = require('system');
// 写入文件，用来测试。正式版本可以注释掉用来提高速度。
// var fs = require("fs");
// 计算加载时间
var time;
var address;

// 读取命令行参数，也就是js文件路径。
if (system.args.length === 1) {
    console.log('Usage: loadspeed.js <some URL>');
    //这行代码很重要。凡是结束必须调用。否则phantomjs不会停止  
    phantom.exit();
}
page.settings.loadImages = false;  //为了提升加载速度，不加载图片
page.settings.resourceTimeout = 15000;//超过15秒放弃加载

time = Date.now();
address = system.args[1];
page.open(address, function(status) {
    if (status !== 'success') {
        console.log('FAIL to load the address');
    } else {
        time = Date.now() - time;
        console.log('Loading time: ' + time + ' msec');
        console.log(page.content);
        setTimeout(function() {
                phantom.exit();
            }, 6000);
    }
    phantom.exit();
});