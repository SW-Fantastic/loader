## Loader

这是一个加载工具框架，目前提供了这样的功能：

#### 从任意位置加载动态链接库

以含有目标动态库的文件夹为参数创建NativeLoader，并调用方法load即可加载。

如果动态链接库含有依赖动态库或者区分32和64位这样的，就需要一些描述文件：
请在目标文件夹内单独创建一个文件夹，用于放置类库，并且编写
module.json 
```json
{
  "name": "动态链接库名",
   "desc": "简要介绍",
   "platforms": [
      {
         "arch": 32 / 64,
         "descriptor": "动态库存放的文件夹名称",
         "platform": "windows或者macos或者linux"
      }
    ]
}
```

编写descriptor.json放置在每一个platforms中descriptor指定的文件夹下

```json
{
  "library": "动态库名称（除了windows之外的系统不包含lib，不要包含文件后缀",
  "dependencies": [], // 依赖的其他dll，这里是数组，每一个元素参照本元素填写，如果没有的话，可以不填dependencies
}
```
请使用含有module.json的文件夹名作为类库名加载这种类库。