<?xml version="1.0"?>

<st-source>
<time-stamp>From VisualWorks® Personal Use Edition, 8.2 of July 15, 2016 on February 10, 2017 at 2:49:42 AM</time-stamp>


<component-created>
<name>SmallTalkProject1</name> <type>package</type>
</component-created><!-- Package SmallTalkProject1* -->


<component-created>
<name>SmallTalkProject1</name> <type>package</type>
</component-created>

<class>
<name>SetCalculator</name>
<environment>Smalltalk</environment>
<super>UI.ApplicationModel</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>x UInput setValues xVal YVal </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkProject1</package>
</attributes>
</class>

<class>
<name>DoubleSet_</name>
<environment>Smalltalk</environment>
<super>UI.ApplicationModel</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>x UInput xVal YVal Y </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkProject1</package>
</attributes>
</class>

<class>
<name>ArrayedSet</name>
<environment>Smalltalk</environment>
<super>DoubleSet_</super>
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
<name>DoubleSet1</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>x y </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkProject1</package>
</attributes>
</class>

<class>
<name>DoubleSet</name>
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

<class>
<name>BranchingSet</name>
<environment>Smalltalk</environment>
<super>DoubleSet_</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>root items </inst-vars>
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

<methods>
<class-id>DoubleSet class</class-id> <category>instance creation</category>

<body package="SmallTalkProject1" selector="new">new
</methods>


<methods>
<class-id>DoubleSet</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="hasMember:">hasMember:anObject

<body package="SmallTalkProject1" selector="intersect:">intersect: anObject

<body package="SmallTalkProject1" selector="display">display

<body package="SmallTalkProject1" selector="insert:">insert: anObject

<body package="SmallTalkProject1" selector="union:">union: anObject
</methods>

<methods>
<class-id>DoubleSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize
</methods>


<methods>
<class-id>DoubleSet_ class</class-id> <category>interface specs</category>

<body package="SmallTalkProject1" selector="windowSpec">windowSpec
</methods>


<methods>
<class-id>DoubleSet_</class-id> <category>aspects</category>

<body package="SmallTalkProject1" selector="UInput">UInput

<body package="SmallTalkProject1" selector="YVal">YVal

<body package="SmallTalkProject1" selector="xVal">xVal
</methods>

<methods>
<class-id>DoubleSet_</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="display">display

<body package="SmallTalkProject1" selector="insert:">insert: anObject

<body package="SmallTalkProject1" selector="hasMember:">hasMember:anObject
</methods>

<methods>
<class-id>DoubleSet_</class-id> <category>actions</category>

<body package="SmallTalkProject1" selector="copySet">copySet

<body package="SmallTalkProject1" selector="union">union

<body package="SmallTalkProject1" selector="intersect">intersect

<body package="SmallTalkProject1" selector="createAS">createAS

<body package="SmallTalkProject1" selector="createBS">createBS

<body package="SmallTalkProject1" selector="addItems">addItems
</methods>

<methods>
<class-id>DoubleSet_</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize
</methods>


<methods>
<class-id>BranchingSet class</class-id> <category>instance creation</category>

<body package="SmallTalkProject1" selector="new">new
</methods>


<methods>
<class-id>BranchingSet</class-id> <category>accessing</category>

<body package="SmallTalkProject1" selector="root">root

<body package="SmallTalkProject1" selector="items">items

<body package="SmallTalkProject1" selector="items:">items: anObject

<body package="SmallTalkProject1" selector="root:">root: anObject
</methods>

<methods>
<class-id>BranchingSet</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="add:and:">add: aNode and: fromNode 

<body package="SmallTalkProject1" selector="display">display

<body package="SmallTalkProject1" selector="inOrder:">inOrder: aNode

<body package="SmallTalkProject1" selector="copySet">copySet 

<body package="SmallTalkProject1" selector="hasMember:">hasMember: val

<body package="SmallTalkProject1" selector="isEmpt">isEmpt 

<body package="SmallTalkProject1" selector="insert:">insert: newElement
</methods>

<methods>
<class-id>BranchingSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize
</methods>


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
<class-id>SetCalculator class</class-id> <category>interface specs</category>

<body package="SmallTalkProject1" selector="windowSpec">windowSpec
</methods>


<methods>
<class-id>SetCalculator</class-id> <category>aspects</category>

<body package="SmallTalkProject1" selector="UInput">UInput

<body package="SmallTalkProject1" selector="YVal">YVal

<body package="SmallTalkProject1" selector="xVal">xVal

<body package="SmallTalkProject1" selector="setValues">setValues
</methods>

<methods>
<class-id>SetCalculator</class-id> <category>actions</category>

<body package="SmallTalkProject1" selector="addItems">addItems 

<body package="SmallTalkProject1" selector="createAS">createAS

<body package="SmallTalkProject1" selector="createBS">createBS
</methods>

<methods>
<class-id>SetCalculator</class-id> <category>copying</category>

<body package="SmallTalkProject1" selector="copy">copy
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

<body package="SmallTalkProject1" selector="display">display

<body package="SmallTalkProject1" selector="copySet">copySet

<body package="SmallTalkProject1" selector="hasMember:">hasMember: value

<body package="SmallTalkProject1" selector="toString">toString

<body package="SmallTalkProject1" selector="isEmpt">isEmpt 

<body package="SmallTalkProject1" selector="insert:">insert: val
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize
</methods>



</st-source>