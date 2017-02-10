<?xml version="1.0"?>

<st-source>
<time-stamp>From VisualWorks® Personal Use Edition, 8.2 of July 15, 2016 on February 8, 2017 at 3:05:14 PM</time-stamp>


<component-created>
<name>SmallTalkProject1</name> <type>package</type>
</component-created><!-- Package SmallTalkProject1* -->


<component-created>
<name>SmallTalkProject1</name> <type>package</type>
</component-created>

<class>
<name>ArrayedSet</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>items </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkProject1</package>
</attributes>
</class>

<class>
<name>BSTNode</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>element left right </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkProject1</package>
</attributes>
</class>

<class>
<name>BSTSet</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>root </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkProject1</package>
</attributes>
</class>

<class>
<name>SetCalculator</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars></inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkProject1</package>
</attributes>
</class>

<methods>
<class-id>BSTNode</class-id> <category>accessing</category>

<body package="SmallTalkProject1" selector="right:">right: anObject

<body package="SmallTalkProject1" selector="element">element

<body package="SmallTalkProject1" selector="left:">left: anObject

<body package="SmallTalkProject1" selector="right">right

<body package="SmallTalkProject1" selector="element:">element: anObject

<body package="SmallTalkProject1" selector="left">left
</methods>

<methods>
<class-id>BSTNode</class-id> <category>BSTNode_Ops</category>

<body package="SmallTalkProject1" selector="create:">create: newElement
</methods>

<methods>
<class-id>BSTNode</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize
</methods>


<methods>
<class-id>BSTSet class</class-id> <category>instance creation</category>

<body package="SmallTalkProject1" selector="new">new
</methods>


<methods>
<class-id>BSTSet</class-id> <category>accessing</category>

<body package="SmallTalkProject1" selector="root">root

<body package="SmallTalkProject1" selector="root:">root: anObject
</methods>

<methods>
<class-id>BSTSet</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="add:and:">add: aNode and: fromNode 

<body package="SmallTalkProject1" selector="inOrder:">inOrder: aNode

<body package="SmallTalkProject1" selector="isEmpty">isEmpty 

<body package="SmallTalkProject1" selector="display">display

<body package="SmallTalkProject1" selector="insert:">insert: newElement
</methods>

<methods>
<class-id>BSTSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize
</methods>


<methods>
<class-id>SetCalculator class</class-id> <category>interface specs</category>

<body package="SmallTalkProject1" selector="windowSpec">windowSpec
</methods>


<methods>
<class-id>ArrayedSet class</class-id> <category>instance creation</category>

<body package="SmallTalkProject1" selector="new">new
</methods>


<methods>
<class-id>ArrayedSet</class-id> <category>accessing</category>

<body package="SmallTalkProject1" selector="items:">items: anObject

<body package="SmallTalkProject1" selector="items">items
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="toString">toString

<body package="SmallTalkProject1" selector="insert:">insert: x

<body package="SmallTalkProject1" selector="hasMember:">hasMember: value

<body package="SmallTalkProject1" selector="traverse">traverse
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize
</methods>



</st-source>