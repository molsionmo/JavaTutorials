# markdown语法

## 标题

```markdown
# 这是一级标题
## 这是二级标题
### 这是三级标题
#### 这是四级标题
##### 这是五级标题
###### 这是六级标题
```

效果如下:

# 这是一级标题
## 这是二级标题
### 这是三级标题
#### 这是四级标题
##### 这是五级标题
###### 这是六级标题

## 字体

```markdown
**这是加粗的文字**

*这是倾斜的文字*

***这是斜体加粗的文字***

~~这是加删除线的文字~~
```

效果如下:

**这是加粗的文字**

*这是倾斜的文字*

***这是斜体加粗的文字***

~~这是加删除线的文字~~

## image

```markdown
![blockchain](https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=702257389,1274025419&fm=27&gp=0.jpg "区块链")
```

![blockchain](https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=702257389,1274025419&fm=27&gp=0.jpg "区块链")

## 代码

代码块使用 ```进行包裹

```markdown
    ```java
        public class Person{
            private int age;
            private String name;
        }
    ```
```

效果如下:

```java
public class Person{
    private int age;
    private String name;
}
```

## 表格

```markdown
姓名 | 技能 | 排名
:--:|:--:|:--:
刘备 | 领导 | 老大
关羽 | 打仗 | 老二
张飞 | 打架 | 老三
```

效果如下:
姓名 | 技能 | 排名
:--:|:--:|:--:
刘备 | 领导 | 老大
关羽 | 打仗 | 老二
张飞 | 打架 | 老三

## 流程图

```flow
st=>start: 开始
e=>end: 登录
io1=>inputoutput: 输入用户名密码
sub1=>subroutine: 数据库查询子类
cond=>condition: 是否有此用户
cond2=>condition: 密码是否正确
op=>operation: 读入用户信息

st->io1->sub1->cond
cond(yes,right)->cond2
cond(no)->io1(right)
cond2(yes,right)->op->e
cond2(no)->io1
```

## 数学公式
$$ f(x) = sin(x) +13 $$
$$ \sum_{n=1}^{100} $$
