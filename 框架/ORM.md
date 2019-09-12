# ORM

Object Relational Mapping, 对象关系映射. 专门解决JDBC的持久化的抽象方案,业界较为出名的是Hibernate与Mybatis.

JDBC使用的基本过程

1. 建立connection, 根据不同的数据库产商的实现类与账户密码相关
2. 操作connection, 打开Statement对象
3. 通过Statement执行SQL, 返回结果至ResultSet对象
4. ResultSet对象转化成具体的POJO对象

## Hibernate

它是建立在POJO与数据库模型的直接映射关系上的,只需要提供

* POJO
* POJO与对应数据库表的映射关系( 一般分2层,一个面向数据库链接hibernate.cfg.xml,一个面向POJO映射关系 T-Role.xml )

由于封装了SQL层,用户是没法去操作SQL层的东西的,只能通过它的HQL与POJO映射关系控制SQL执行计划,故而有以下缺点

* 无法自主管理与优化SQL
* 全表映射带来的不便, 更新时需要发送所有字段

## Mybatis

相比与Hibernate的全自动映射框架, mybatis是半自动映射,它所需要的包含3个部分,比Hibernate多了SQL,但这层也多了一份可控的灵活度

* SQL
* POJO
* 映射规则(xml+mapper接口;如果SQL查询的列名与POJO字段名一致,可以省去映射规则, 如 select id,name form user where id=1,POJO中字段也未id,name )

即使从用户角度看是有配置了POJO,SQL,映射规则(xml+mapper接口),实际中还是进行了JDBC的执行

* 建立connection,session就持有一个连接,connection应该是维护在连接池中
* 打开statement对象, xml+mapper接口通过被动态代理生成对应的类与其实体, 实体里面使用connection代开对应的statement对象
* 动态生成的类将使用statement对象执行解析出来xml中的sql语句然后取得其ResultSet
* 根据sql中的select字段信息schmema, 然后反射需要生成的POJO对象,根据字段映射进行Field的setter,最后返回对应的POJO实体
