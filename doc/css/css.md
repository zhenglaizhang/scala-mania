多重样式将层叠为一个
样式表允许以多种方式规定样式信息。样式可以规定在单个的 HTML 元素中，在 HTML 页的头元素中，或在一个外部的 CSS 文件中。甚至可以在同一个 HTML 文档内部引用多个外部样式表。
层叠次序
当同一个 HTML 元素被不止一个样式定义时，会使用哪个样式呢？
一般而言，所有的样式会根据下面的规则层叠于一个新的虚拟样式表中，其中数字 4 拥有最高的优先权。

* 浏览器缺省设置
* 外部样式表
* 部样式表（位于 <head> 标签内部）
* 内联样式（在 HTML 元素内部）
 
因此，内联样式（在 HTML 元素内部）拥有最高的优先权，这意味着它将优先于以下的样式声明：<head> 标签中的样式声明，外部样式表中的样式声明，或者浏览器中的样式声明（缺省值）。


```css
selector {declaration1; declaration2; ... declarationN }

selector {property: value}

h1 {color:red; font-size:14px;}
p { color: #ff0000; }

/*css缩写*/
p { color: #f00; }

/*使用rbg*/
p { color: rgb(255,0,0); }
p { color: rgb(100%,0%,0%); }

/*如果值为若干单词，则要给值加引号*/
p {font-family: "sans serif";}


/*最后一条规则是不需要加分号的，因为分号在英语中是一个分隔符号，不是结束符号。然而，大多数有经验的设计师会在每条声明的末尾都加上分号，这么做的好处是，当你从现有的规则中增减声明时，会尽可能地减少出错的可能性。*/
p {text-align:center; color:red;}	

p {
  text-align: center;
  color: black;
  font-family: arial;
}

body {
  color: #000;
  background: #fff;
  margin: 0;
  padding: 0;
  font-family: Georgia, Palatino, serif;
}

```
* 是否包含空格不会影响 CSS 在浏览器的工作效果，同样，与 XHTML 不同，CSS 对大小写不敏感。不过存在一个例外：如果涉及到与 HTML 文档一起工作的话，class 和 id 名称对大小写是敏感的。


```css
/*选择器的分组*/
h1,h2,h3,h4,h5,h6 {
  color: green;
}


/*根据 CSS，子元素从父元素继承属性。但是它并不总是按此方式工作。*/
body {
     font-family: Verdana, sans-serif;
 }
 /*站点的 body 元素将使用 Verdana 字体（假如访问者的系统中存在该字体的话）*/
 
 /*幸运地是，你可以通过使用我们称为 "Be Kind to Netscape 4" 的冗余法则来处理旧式浏览器无法理解继承的问题。*/

body  {
     font-family: Verdana, sans-serif;
     }

p, td, ul, ol, li, dl, dt, dd  {
     font-family: Verdana, sans-serif;
     }
     
/*你希望段落的字体是 Times。没问题。创建一个针对 p 的特殊规则，这样它就会摆脱父元素的规则：*/
body  {
     font-family: Verdana, sans-serif;
 }

td, ul, ol, ul, li, dl, dt, dd  {
     font-family: Verdana, sans-serif;
 }

p  {
     font-family: Times, "Times New Roman", serif;
 }
```


## 上下文选择器 (contextual selectors) | 派生选择器

```css
/*列表中的 strong 元素变为斜体字，而不是通常的粗体字*/
li strong {
    font-style: italic;
    font-weight: normal;
}



strong {
     color: red;
 }

h2 {
 color: red;
}

h2 strong {
     color: blue;
 }
```



### id 选择器

```css
#red {color:red;}
#green {color:green;}


/*在现代布局中，id 选择器常常用于建立派生选择器。*/
#sidebar p {
	font-style: italic;
	text-align: right;
	margin-top: 0.5em;
}
/*
上面的样式只会应用于出现在 id 是 sidebar 的元素内的段落。这个元素很可能是 div 或者是表格单元，尽管它也可能是一个表格或者其他块级元素。它甚至可以是一个内联元素，比如 <em></em> 或者 <span></span>，不过这样的用法是非法的，因为不可以在内联元素 <span> 中嵌入 <p>* TODO */


/*即使被标注为 sidebar 的元素只能在文档中出现一次，这个 id 选择器作为派生选择器也可以被使用很多次：*/
#sidebar h2 {
	font-size: 1em;
	font-weight: normal;
	font-style: italic;
	margin: 0;
	line-height: 1.5;
	text-align: right;
}

#sidebar {
	border: 1px dotted #000;
	padding: 10px;
}
/*id 为 sidebar 的元素将拥有一个像素宽的黑色点状边框，同时其周围会有 10 个像素宽的内边距（padding，内部空白)*/
```


### CSS 类选择器
