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

<body package="SmallTalkProject1" selector="right:">right: anObject	right := anObject</body>

<body package="SmallTalkProject1" selector="element">element	^element</body>

<body package="SmallTalkProject1" selector="left:">left: anObject	left := anObject</body>

<body package="SmallTalkProject1" selector="right">right	^right</body>

<body package="SmallTalkProject1" selector="element:">element: anObject	element := anObject</body>

<body package="SmallTalkProject1" selector="left">left	^left</body>
</methods>

<methods>
<class-id>BSTNode</class-id> <category>BSTNode_Ops</category>

<body package="SmallTalkProject1" selector="create:">create: newElement	| temp |	temp:= BSTNode new.	temp element: newElement.	^self</body>
</methods>

<methods>
<class-id>BSTNode</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	element := nil.	left := nil.	right := nil.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


<methods>
<class-id>BSTSet class</class-id> <category>instance creation</category>

<body package="SmallTalkProject1" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>BSTSet</class-id> <category>accessing</category>

<body package="SmallTalkProject1" selector="root">root	^root</body>

<body package="SmallTalkProject1" selector="root:">root: anObject	root := anObject</body>
</methods>

<methods>
<class-id>BSTSet</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="add:and:">add: aNode and: fromNode 	| temp |	temp:=fromNode.aNode element &lt; temp element ifTrue:[ temp left=nil ifTrue: [temp left: aNode.^self]										ifFalse:[temp:=fromNode left. ^(self add: aNode and:temp)]].aNode element&gt; temp element ifTrue:[temp right=nil ifTrue: [temp right: aNode.^self]										ifFalse:[temp:=fromNode right. ^(self add: aNode and: temp)]].^self</body>

<body package="SmallTalkProject1" selector="inOrder:">inOrder: aNodeaNode=nil ifTrue: [^self ].self inOrder: aNode left.Transcript print: aNode element. Transcript show:' '.self inOrder:aNode right.</body>

<body package="SmallTalkProject1" selector="isEmpty">isEmpty ^root=nil</body>

<body package="SmallTalkProject1" selector="display">display^self inOrder: root</body>

<body package="SmallTalkProject1" selector="insert:">insert: newElement	| temp |temp:= BSTNode new.temp element: newElement.self isEmpty ifTrue:[root:=temp] "If the tree is empty then assign the newly created node as the root."		ifFalse:[self add: temp and: root].^self</body>
</methods>

<methods>
<class-id>BSTSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	root := nil.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


<methods>
<class-id>SetCalculator class</class-id> <category>interface specs</category>

<body package="SmallTalkProject1" selector="windowSpec">windowSpec	"Tools.UIPainter new openOnClass: self andSelector: #windowSpec"	&lt;resource: #canvas&gt;	^#(#{UI.FullSpec} 		#window: 		#(#{UI.WindowSpec} 			#label: 'Set Calculator' 			#bounds: #(#{Graphics.Rectangle} 344 160 1022 568 ) ) 		#component: 		#(#{UI.SpecCollection} 			#collection: #(				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 94 0 48 0 210 0 69 0 ) 					#name: #createAS_B 					#label: 'Create ArrayedSet' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 394 0 47 0 519 0 68 0 ) 					#name: #createBS_B 					#label: 'Create Branching Set' 					#defaultable: true ) 				#(#{UI.InputFieldSpec} 					#layout: #(#{Graphics.LayoutFrame} 246 0 85 0 368 0 106 0 ) 					#name: #aSetI ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 98 0 93 0 ) 					#name: #SetValuesL 					#label: 'Insert Set Values' ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 243 0 129 0 368 0 150 0 ) 					#name: #switchSetsB 					#label: 'Switch Sets' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 245 0 167 0 370 0 188 0 ) 					#name: #unionB 					#label: 'Set union' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 244 0 202 0 369 0 223 0 ) 					#name: #intersectB 					#label: 'Intersection' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 395 0 84 0 486 0 105 0 ) 					#name: #'#addArraySetB' 					#label: 'Add Elements' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 244 0 239 0 369 0 260 0 ) 					#name: #copyB 					#label: 'Copy X to Y' 					#defaultable: true ) 				#(#{UI.DocumentViewSpec} 					#layout: #(#{Graphics.LayoutFrame} 98 0 155 0 198 0 255 0 ) 					#name: #arraySetItems 					#isOpaque: true 					#isWrapped: true 					#isReadOnly: true 					#showHiddenCharacters: false 					#showLineStriping: false 					#autoAccept: false 					#showInputCursor: false 					#showSelection: true ) 				#(#{UI.DocumentViewSpec} 					#layout: #(#{Graphics.LayoutFrame} 400 0 150 0 500 0 250 0 ) 					#name: #branchingSetItems 					#isOpaque: true 					#isWrapped: true 					#isReadOnly: true 					#showHiddenCharacters: false 					#showLineStriping: false 					#autoAccept: false 					#showInputCursor: false 					#showSelection: true ) ) ) )</body>
</methods>


<methods>
<class-id>ArrayedSet class</class-id> <category>instance creation</category>

<body package="SmallTalkProject1" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>ArrayedSet</class-id> <category>accessing</category>

<body package="SmallTalkProject1" selector="items:">items: anObject	items := anObject</body>

<body package="SmallTalkProject1" selector="items">items	^items</body>
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="toString">toString	"Method to return all the elements in the set as a String"| tempString |	tempString:= ''. "Declaring a tempString object to hold all the elements in the set"	items do:[:x | tempString:=tempString,' ', (x printString)].  "Converting an item in the ordered Collection to a String "	^tempString</body>

<body package="SmallTalkProject1" selector="insert:">insert: x		items addFirst: x.		^self</body>

<body package="SmallTalkProject1" selector="hasMember:">hasMember: value		items isEmpty ifTrue:[^false]			     ifFalse:[ items do:[:x| value=x ifTrue: [^True]]].		^False</body>

<body package="SmallTalkProject1" selector="traverse">traverse		items isEmpty ifTrue:[^false]			     ifFalse:[self items do:[:x| Transcript print: x. Transcript show: ' ']].		^self</body>
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	items := OrderedCollection new.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>



</st-source>
