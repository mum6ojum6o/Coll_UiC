<?xml version="1.0"?>

<st-source>
<time-stamp>From VisualWorks® Personal Use Edition, 8.2 of July 15, 2016 on February 10, 2017 at 4:05:42 PM</time-stamp>


<component-created>
<name>CS474</name> <type>package</type>
</component-created><!-- Package CS474* -->


<component-created>
<name>CS474</name> <type>package</type>
</component-created>

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
<package>CS474</package>
</attributes>
</class>

<class>
<name>ArrayedSet</name>
<environment>Smalltalk</environment>
<super>DoubleSet</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>items </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>CS474</package>
</attributes>
</class>

<class>
<name>BranchingSet</name>
<environment>Smalltalk</environment>
<super>DoubleSet</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>root items </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>CS474</package>
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
<package>CS474</package>
</attributes>
</class>

<class>
<name>SetCalculator</name>
<environment>Smalltalk</environment>
<super>UI.ApplicationModel</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>x UInput setValues xValues yValues y </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>CS474</package>
</attributes>
</class>

<methods>
<class-id>DoubleSet class</class-id> <category>instance creation</category>

<body package="CS474" selector="new">new
</methods>


<methods>
<class-id>DoubleSet</class-id> <category>Operations</category>

<body package="CS474" selector="hasMember:">hasMember:anObject

<body package="CS474" selector="intersect:">intersect: anObject

<body package="CS474" selector="display">display

<body package="CS474" selector="insert:">insert: anObject

<body package="CS474" selector="union:">union: anObject 
</methods>

<methods>
<class-id>DoubleSet</class-id> <category>initialize-release</category>

<body package="CS474" selector="initialize">initialize
</methods>


<methods>
<class-id>ArrayedSet class</class-id> <category>instance creation</category>

<body package="CS474" selector="new">new
</methods>


<methods>
<class-id>ArrayedSet</class-id> <category>accessing</category>

<body package="CS474" selector="items:">items: anObject

<body package="CS474" selector="items">items
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>Operations</category>

<body package="CS474" selector="copySet">copySet

<body package="CS474" selector="hasMember:">hasMember: value

<body package="CS474" selector="display">display

<body package="CS474" selector="insert:">insert: x

<body package="CS474" selector="isEmpt">isEmpt 
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>initialize-release</category>

<body package="CS474" selector="initialize">initialize
</methods>


<methods>
<class-id>BranchingSet class</class-id> <category>instance creation</category>

<body package="CS474" selector="new">new
</methods>


<methods>
<class-id>BranchingSet</class-id> <category>accessing</category>

<body package="CS474" selector="root">root

<body package="CS474" selector="items:">items: anObject

<body package="CS474" selector="items">items

<body package="CS474" selector="root:">root: anObject
</methods>

<methods>
<class-id>BranchingSet</class-id> <category>Operations</category>

<body package="CS474" selector="copySet">copySet 

<body package="CS474" selector="display">display

<body package="CS474" selector="inOrder:">inOrder: aNode

<body package="CS474" selector="add:and:">add: aNode and: fromNode 

<body package="CS474" selector="isEmpt">isEmpt 

<body package="CS474" selector="insert:">insert: newElement

<body package="CS474" selector="hasMember:">hasMember: x
</methods>

<methods>
<class-id>BranchingSet</class-id> <category>initialize-release</category>

<body package="CS474" selector="initialize">initialize
</methods>


<methods>
<class-id>BSTNode</class-id> <category>accessing</category>

<body package="CS474" selector="right:">right: anObject

<body package="CS474" selector="element">element

<body package="CS474" selector="left:">left: anObject

<body package="CS474" selector="right">right

<body package="CS474" selector="element:">element: anObject

<body package="CS474" selector="left">left
</methods>

<methods>
<class-id>BSTNode</class-id> <category>BSTNode_Ops</category>

<body package="CS474" selector="create:">create: newElement
</methods>

<methods>
<class-id>BSTNode</class-id> <category>initialize-release</category>

<body package="CS474" selector="initialize">initialize
</methods>


<methods>
<class-id>SetCalculator class</class-id> <category>interface specs</category>

<body package="CS474" selector="windowSpec">windowSpec
</methods>


<methods>
<class-id>SetCalculator</class-id> <category>actions</category>

<body package="CS474" selector="copySets">copySets

<body package="CS474" selector="createAS">createAS

<body package="CS474" selector="addItems">addItems

<body package="CS474" selector="switch">switch

<body package="CS474" selector="union">union

<body package="CS474" selector="createBS">createBS

<body package="CS474" selector="intersect">intersect
</methods>

<methods>
<class-id>SetCalculator</class-id> <category>aspects</category>

<body package="CS474" selector="xValues">xValues

<body package="CS474" selector="setValues">setValues

<body package="CS474" selector="yValues">yValues

<body package="CS474" selector="UInput">UInput
</methods>



</st-source>