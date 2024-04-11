# easyexcel-helper

**本项目的目的在于，抽象easyexcel的处理流程，将行为关注点放于与db的交互上**

目录介绍<br>
1. comment: 抽象类或接口，具体处理逻辑的抽象
2. enums: 用来表示实体与处理方法的关联关系
3. handler: 具体实现的demo
4. util: 工具类，需重点关注ExcelHelper


### 已实现:
1. 可用于处理基础的excel的读、写，与db交互
2. 使用者只需关注于db的交互，并对每个excel配置handler,listen(实现具体的接口或抽象类)
3. 对于大数据量操作，提供async多线程处理写入db操作，对于写入excel，提供分页查询db数据，提供多sheet写入
4. 对于非handler,不依赖于spring环境

### 不足: 
1. 对于每个实体，都需要给出excelEntity,handler,listener，这种对于像游戏类旧日志存储时写入，非常麻烦，<br>
项目是动态的，日志表结构随时会增加；
2. 对于写入excel，多sheet情况可使用多线程处理，暂未实现，单线程不确定是否可用多线程
3. 在多线程读取和多线程写入时（单sheet），未保证读取和写入的顺序，可使用临时缓存及间隙下标来实现...


**对于上述不足1已经完成新版本，详情见分支[v2-dynamic](https://github.com/chq1024/easyexcel-helper/tree/v2-dynamic)**

