<?xml version="1.0"?>

<st-source>
<time-stamp>From VisualWorks® Personal Use Edition, 8.2 of July 15, 2016 on February 8, 2017 at 2:49:16 AM</time-stamp>


<component-created>
<name>SmallTalkExp</name> <type>package</type>
</component-created><!-- Package SmallTalkExp* -->


<component-created>
<name>SmallTalkExp</name> <type>package</type>
</component-created>

<class>
<name>MyNode</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>element next </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkExp</package>
</attributes>
</class>

<class>
<name>Stack</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>items </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkExp</package>
</attributes>
</class>

<class>
<name>SimpleCounter</name>
<environment>Smalltalk</environment>
<super>UI.ApplicationModel</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>counter </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkExp</package>
</attributes>
</class>

<class>
<name>QueueLL</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>front back </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkExp</package>
</attributes>
</class>

<class>
<name>BinarySearchTree</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>root </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkExp</package>
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
<package>SmallTalkExp</package>
</attributes>
</class>

<class>
<name>Queue</name>
<environment>Smalltalk</environment>
<super>Core.Object</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>items </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkExp</package>
</attributes>
</class>

<class>
<name>UgosSimpleCounter</name>
<environment>Smalltalk</environment>
<super>UI.ApplicationModel</super>
<private>false</private>
<indexed-type>none</indexed-type>
<inst-vars>counter </inst-vars>
<class-inst-vars></class-inst-vars>
<imports></imports>
<category></category>
<attributes>
<package>SmallTalkExp</package>
</attributes>
</class>

<methods>
<class-id>Queue class</class-id> <category>instance creation</category>

<body package="SmallTalkExp" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>Queue</class-id> <category>accessing</category>

<body package="SmallTalkExp" selector="items:">items: anObject	items := anObject</body>

<body package="SmallTalkExp" selector="items">items	^items</body>
</methods>

<methods>
<class-id>Queue</class-id> <category>QueueOperations</category>

<body package="SmallTalkExp" selector="insert:">insert: val	items add: val.	^self.</body>
</methods>

<methods>
<class-id>Queue</class-id> <category>initialize-release</category>

<body package="SmallTalkExp" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	items := OrderedCollection new.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


<methods>
<class-id>BinarySearchTree class</class-id> <category>instance creation</category>

<body package="SmallTalkExp" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>BinarySearchTree</class-id> <category>accessing</category>

<body package="SmallTalkExp" selector="root">root	^root</body>

<body package="SmallTalkExp" selector="root:">root: anObject	root := anObject</body>
</methods>

<methods>
<class-id>BinarySearchTree</class-id> <category>Operations</category>

<body package="SmallTalkExp" selector="display">display^self inOrder: root</body>

<body package="SmallTalkExp" selector="insert:">insert: newElement	| temp |temp:= BSTNode new.temp element: newElement.self isEmpty ifTrue:[root:=temp] "If the tree is empty then assign the newly created node as the root."		ifFalse:[self add: temp and: root].^self</body>

<body package="SmallTalkExp" selector="add:and:">add: aNode and: fromNode 	| temp |	temp:=fromNode.aNode element &lt; temp element ifTrue:[ temp left=nil ifTrue: [temp left: aNode.^self]										ifFalse:[temp:=fromNode left. ^(self add: aNode and:temp)]].aNode element&gt; temp element ifTrue:[temp right=nil ifTrue: [temp right: aNode.^self]										ifFalse:[temp:=fromNode right. ^(self add: aNode and: temp)]].^self</body>

<body package="SmallTalkExp" selector="isEmpty">isEmpty ^root=nil</body>

<body package="SmallTalkExp" selector="inOrder:">inOrder: aNodeaNode=nil ifTrue: [^self ].self inOrder: aNode left.Transcript print: aNode element. Transcript show:' '.self inOrder:aNode right.</body>
</methods>

<methods>
<class-id>BinarySearchTree</class-id> <category>initialize-release</category>

<body package="SmallTalkExp" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	root := nil.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


<methods>
<class-id>QueueLL class</class-id> <category>instance creation</category>

<body package="SmallTalkExp" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>QueueLL</class-id> <category>QLL_Ops</category>

<body package="SmallTalkExp" selector="add:">add: newElement| temp |temp:= MyNode new.temp element: newElement.temp next: nil.self isEmpty ifTrue:[front:=back:=temp]		ifFalse:[back next:temp. back:=back next].^self</body>

<body package="SmallTalkExp" selector="isEmpty">isEmpty 	^ front = nil</body>
</methods>

<methods>
<class-id>QueueLL</class-id> <category>accessing</category>

<body package="SmallTalkExp" selector="back:">back: anObject	back := anObject</body>

<body package="SmallTalkExp" selector="front">front	^front</body>

<body package="SmallTalkExp" selector="front:">front: anObject	front := anObject</body>

<body package="SmallTalkExp" selector="back">back	^back</body>
</methods>

<methods>
<class-id>QueueLL</class-id> <category>initialize-release</category>

<body package="SmallTalkExp" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	front := nil.	back := nil.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


<methods>
<class-id>SimpleCounter class</class-id> <category>interface specs</category>

<body package="SmallTalkExp" selector="windowSpec">windowSpec	"Tools.UIPainter new openOnClass: self andSelector: #windowSpec"	&lt;resource: #canvas&gt;	^#(#{UI.FullSpec} 		#window: 		#(#{UI.WindowSpec} 			#label: 'Simple Counter' 			#bounds: #(#{Graphics.Rectangle} 627 265 1321 684 ) ) 		#component: 		#(#{UI.SpecCollection} 			#collection: #(				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 120 0 153 0 172 0 174 0 ) 					#name: #ActionButton1 					#model: #increment 					#label: 'Up' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 122 0 195 0 174 0 216 0 ) 					#name: #ActionButton2 					#model: #decrement 					#label: 'Down' 					#defaultable: true ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 481 0 152 0 ) 					#name: #Label1 					#label: '0' ) 				#(#{UI.SliderSpec} 					#layout: #(#{Graphics.LayoutFrame} 247 0 178 0 501 0 197 0 ) 					#name: #Slider1 					#model: #counter 					#orientation: #horizontal 					#start: 0 					#stop: 100 					#step: 1 ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 230 0 153 0 ) 					#name: #Label2 					#label: '100' ) 				#(#{UI.InputFieldSpec} 					#layout: #(#{Graphics.LayoutFrame} 291 0 116 0 422 0 136 0 ) 					#name: #InputField1 					#model: #counter 					#alignment: #center 					#isReadOnly: true 					#type: #number 					#formatString: '0' ) ) ) )</body>
</methods>


<methods>
<class-id>SimpleCounter</class-id> <category>aspects</category>

<body package="SmallTalkExp" selector="counter">counter	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^counter isNil		ifTrue:			[counter := 0 asValue]		ifFalse:			[counter]</body>
</methods>

<methods>
<class-id>SimpleCounter</class-id> <category>actions</category>

<body package="SmallTalkExp" selector="increment">increment	"This stub method was generated by UIDefiner"counter value: (counter value) +1.	^self</body>

<body package="SmallTalkExp" selector="decrement">decrement	"This stub method was generated by UIDefiner"counter value: (counter value) -1.	^self</body>
</methods>


<methods>
<class-id>Stack class</class-id> <category>instance creation</category>

<body package="SmallTalkExp" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>Stack</class-id> <category>accessing</category>

<body package="SmallTalkExp" selector="items:">items: anObject	items := anObject</body>

<body package="SmallTalkExp" selector="items">items	^items</body>
</methods>

<methods>
<class-id>Stack</class-id> <category>stack-operations</category>

<body package="SmallTalkExp" selector="push:">push: x	"comment stating purpose of message"	items addFirst: x.	^self.</body>

<body package="SmallTalkExp" selector="contains:">contains: x	"comment stating purpose of message"	(items isEmpty) ifTrue: [^false].	items do: [:y | (y = x) ifTrue: [^true]].	^false.</body>

<body package="SmallTalkExp" selector="pop">pop	"comment stating purpose of message"	(items isEmpty)		ifTrue: [self error: 'Don''t pop an empty stack, please!'.]         ifFalse: [^items removeFirst.]</body>

<body package="SmallTalkExp" selector="top">top	"comment stating purpose of message"	(items isEmpty)		ifTrue: [self error: 'Don''t pop an empty stack, please!'.]         ifFalse: [^items first.]</body>
</methods>

<methods>
<class-id>Stack</class-id> <category>initialize-release</category>

<body package="SmallTalkExp" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	items := OrderedCollection new.	^self</body>
</methods>


<methods>
<class-id>MyNode class</class-id> <category>instance creation</category>

<body package="SmallTalkExp" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>MyNode</class-id> <category>Ops</category>

<body package="SmallTalkExp" selector="new:">new: newElement^((super new) element: newElement)</body>
</methods>

<methods>
<class-id>MyNode</class-id> <category>accessing</category>

<body package="SmallTalkExp" selector="element:">element: anObject	element := anObject</body>

<body package="SmallTalkExp" selector="next">next	^next</body>

<body package="SmallTalkExp" selector="next:">next: anObject	next := anObject</body>

<body package="SmallTalkExp" selector="element">element	^element</body>
</methods>

<methods>
<class-id>MyNode</class-id> <category>initialize-release</category>

<body package="SmallTalkExp" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	element := nil.	next := nil.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


<methods>
<class-id>UgosSimpleCounter class</class-id> <category>interface specs</category>

<body package="SmallTalkExp" selector="windowSpec">windowSpec	"Tools.UIPainter new openOnClass: self andSelector: #windowSpec"	&lt;resource: #canvas&gt;	^#(#{UI.FullSpec} 		#window: 		#(#{UI.WindowSpec} 			#label: 'Ugo''s simple counter' 			#bounds: #(#{Graphics.Rectangle} 205 365 634 751 ) ) 		#component: 		#(#{UI.SpecCollection} 			#collection: #(				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 56 0 36 0 109 0 54 0 ) 					#name: #ActionButton1 					#model: #increment 					#label: 'UP' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 57 0 84 0 110 0 102 0 ) 					#name: #ActionButton2 					#model: #decrement 					#label: 'DOWN' 					#defaultable: true ) 				#(#{UI.InputFieldSpec} 					#layout: #(#{Graphics.LayoutFrame} 188 0 42 0 288 0 61 0 ) 					#name: #InputField1 					#model: #counter 					#alignment: #right 					#isReadOnly: true 					#type: #number 					#formatString: '0' ) 				#(#{UI.SliderSpec} 					#layout: #(#{Graphics.LayoutFrame} 202 0 96 0 397 0 123 0 ) 					#name: #Slider1 					#model: #counter 					#orientation: #horizontal 					#start: 0 					#stop: 100 					#step: 1 ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 194 0 89 0 ) 					#name: #Label1 					#label: '0' ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 378 0 95 0 ) 					#name: #Label2 					#label: '100' ) ) ) )</body>
</methods>


<methods>
<class-id>UgosSimpleCounter</class-id> <category>aspects</category>

<body package="SmallTalkExp" selector="counter">counter	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^counter isNil		ifTrue:			[counter := 0 asValue]		ifFalse:			[counter]</body>
</methods>

<methods>
<class-id>UgosSimpleCounter</class-id> <category>actions</category>

<body package="SmallTalkExp" selector="decrement">decrement	"This stub method was generated by UIDefiner"	counter value: (counter value) - 1.	^self</body>

<body package="SmallTalkExp" selector="increment">increment	"This stub method was generated by UIDefiner"	counter value: (counter value) + 1.	^self</body>
</methods>


<methods>
<class-id>BSTNode</class-id> <category>accessing</category>

<body package="SmallTalkExp" selector="right:">right: anObject	right := anObject</body>

<body package="SmallTalkExp" selector="element">element	^element</body>

<body package="SmallTalkExp" selector="left:">left: anObject	left := anObject</body>

<body package="SmallTalkExp" selector="right">right	^right</body>

<body package="SmallTalkExp" selector="element:">element: anObject	element := anObject</body>

<body package="SmallTalkExp" selector="left">left	^left</body>
</methods>

<methods>
<class-id>BSTNode</class-id> <category>BSTNode_Ops</category>

<body package="SmallTalkExp" selector="create:">create: newElement	| temp |	temp:= BSTNode new.	temp element: newElement.	^self</body>
</methods>

<methods>
<class-id>BSTNode</class-id> <category>initialize-release</category>

<body package="SmallTalkExp" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	element := nil.	left := nil.	right := nil.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>



</st-source>
