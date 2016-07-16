# Gif转换器

将视频文件转换成gif图片

# 演示

![演示](https://cloud.githubusercontent.com/assets/13044819/14780377/3fced308-0b0f-11e6-8ba1-be25c0e7195e.gif)

# 运行要求

要求最新的java(8u40+)版本

# 实现原理

通过调用ffmpeg命令行将视频文件转换成gif图片，所以现在只能在Windows 64位上使用，当然如果使用不同版本的ffmpeg可执行文件那么就可以实现在任意平台使用

# 代码

/src/com/getting/util/executor/ 目录下是Windows调用控制台的实现

/src/media/ (使用了 /src/com/getting/util/executor/) 目录下是视频转换gif图片的实现
