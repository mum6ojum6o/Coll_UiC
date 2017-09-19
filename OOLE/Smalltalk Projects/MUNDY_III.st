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

<body package="SmallTalkProject1" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>DoubleSet</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="hasMember:">hasMember:anObject^self shouldNotImplement</body>

<body package="SmallTalkProject1" selector="intersect:">intersect: anObject| temp inter stream|inter:=''.temp:=anObject display.stream:= ReadStream on:temp.stream skipSeparators.[stream atEnd] whileFalse:[| x |x:=(Number readFrom:stream). (self hasMember: x) ifTrue:[inter:=inter,' ',(x printString)] ifFalse:[inter:=inter,'']. stream skipSeparators].^inter</body>

<body package="SmallTalkProject1" selector="display">display^self shouldNotImplement</body>

<body package="SmallTalkProject1" selector="insert:">insert: anObject^self shouldNotImplement</body>

<body package="SmallTalkProject1" selector="union:">union: anObject"Method to list out the determine the union of two sets"	| unionSet temp stream|unionSet:=''.(self isEmpty) ifTrue:[unionSet:=' ']			ifFalse:[unionSet:=unionSet,' ',(self display)].anObject isEmpt ifTrue:[unionSet:=unionSet,' '].	temp:=(anObject display).		stream:= ReadStream on:temp.	stream skipSeparators.[stream atEnd] whileFalse:[| x | x:=(Number readFrom:stream). (self hasMember:x) ifTrue:[unionSet:=unionSet,''] ifFalse:[unionSet:=unionSet,' ',(x printString)]. stream skipSeparators ].^unionSet</body>
</methods>

<methods>
<class-id>DoubleSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.		" *** Replace this comment with the appropriate initialization code *** "	^self</body>
</methods>


<methods>
<class-id>DoubleSet_ class</class-id> <category>interface specs</category>

<body package="SmallTalkProject1" selector="windowSpec">windowSpec	"Tools.UIPainter new openOnClass: self andSelector: #windowSpec"	&lt;resource: #canvas&gt;	^#(#{UI.FullSpec} 		#window: 		#(#{UI.WindowSpec} 			#label: 'SetCalculator' 			#bounds: #(#{Graphics.Rectangle} 463 224 903 504 ) ) 		#component: 		#(#{UI.SpecCollection} 			#collection: #(				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 7 0 44 0 127 0 65 0 ) 					#name: #ActionButton_CreateBS 					#model: #createBS 					#label: 'Create BranchingSet' 					#defaultable: true ) 				#(#{UI.InputFieldSpec} 					#layout: #(#{Graphics.LayoutFrame} 129 0 87 0 249 0 108 0 ) 					#name: #InputField_Uinput 					#model: #UInput 					#type: #string ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 256 0 87 0 319 0 108 0 ) 					#name: #ActionButton_AddItems 					#model: #addItems 					#label: 'Add Items' 					#defaultable: true ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 324 0 135 0 ) 					#name: #Label_YSetValues 					#label: 'YSet' ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 69 0 139 0 ) 					#name: #Label_XSetValues 					#label: 'XSet' ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 170 0 125 0 222 0 146 0 ) 					#name: #ActionButton3_CopySet 					#model: #copySet 					#label: 'Copy' 					#defaultable: true ) 				#(#{UI.TextEditorSpec} 					#layout: #(#{Graphics.LayoutFrame} 30 0 171 0 130 0 271 0 ) 					#name: #xVal 					#model: #xVal 					#isReadOnly: true 					#tabRequiresControl: true ) 				#(#{UI.TextEditorSpec} 					#layout: #(#{Graphics.LayoutFrame} 333 0 173 0 433 0 273 0 ) 					#name: #YVal 					#model: #YVal 					#isReadOnly: true 					#tabRequiresControl: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 291 0 40 0 399 0 61 0 ) 					#name: #ActionButton4_CreateAS 					#model: #createAS 					#label: 'Create ArrayedSet' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 172 0 164 0 224 0 185 0 ) 					#name: #ActionButton5_Union 					#model: #union 					#label: 'Union' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 171 0 201 0 224 0 222 0 ) 					#name: #ActionButton_Intersect 					#model: #intersect 					#label: 'Intersect' 					#defaultable: true ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 66 0 89 0 ) 					#name: #Label_SetValue 					#label: 'Set Values' ) ) ) )</body>
</methods>


<methods>
<class-id>DoubleSet_</class-id> <category>aspects</category>

<body package="SmallTalkProject1" selector="UInput">UInput	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^UInput isNil		ifTrue:			[UInput := String new asValue]		ifFalse:			[UInput]</body>

<body package="SmallTalkProject1" selector="YVal">YVal	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^YVal isNil		ifTrue:			[YVal := String new asValue]		ifFalse:			[YVal]</body>

<body package="SmallTalkProject1" selector="xVal">xVal	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^xVal isNil		ifTrue:			[xVal := String new asValue]		ifFalse:			[xVal]</body>
</methods>

<methods>
<class-id>DoubleSet_</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="display">display^self shouldNotImplement</body>

<body package="SmallTalkProject1" selector="insert:">insert: anObject^self shouldNotImplement</body>

<body package="SmallTalkProject1" selector="hasMember:">hasMember:anObject^self shouldNotImplement</body>
</methods>

<methods>
<class-id>DoubleSet_</class-id> <category>actions</category>

<body package="SmallTalkProject1" selector="copySet">copySetTranscript show: 'in Super copySet'.Y:=x copySet.YVal value: Y display.		^self</body>

<body package="SmallTalkProject1" selector="union">union"Method to list out the determine the union of two sets"	| unionSet  stream|unionSet:=''."variable to store the union values"(x isEmpt) ifTrue:[unionSet:=''] "if setX is empty then setting unionSet variable accordingly."			ifFalse:[unionSet:=unionSet,' ',(x display)]. "If set x is not blank then storing its content in unionset"			Transcript cr. Transcript show: unionSet. Transcript cr.Y isEmpt ifTrue:[unionSet:=unionSet,' '].	"temp:=(Y display)." "Storing set Y's contents in a separate variable"		stream:= ReadStream on:(Y display).	[stream atEnd] whileFalse:[| z | z:=(Number readFrom:stream). 						(x hasMember:z) 						ifTrue:[unionSet:=unionSet,''] "If the element in set Y exists in set x then nothing added to unionset"						ifFalse:[unionSet:=unionSet,' ',(z printString)]. stream skipSeparators ]."Detemining the type of set x"x class==BranchingSet ifTrue:[x:=BranchingSet new]					ifFalse:[x:=ArrayedSet new]."Copy values from unionSet to set X"stream:= ReadStream on:unionSet.stream skipSeparators.[stream atEnd] whileFalse:[x insert:(Number readFrom:stream). stream skipSeparators].xVal value: (x display). "displaying the values in the xVal text editor widget in the application"^self</body>

<body package="SmallTalkProject1" selector="intersect">intersect| temp inter stream|inter:=''."Variable to hold the intersect values of both the sets"temp:=Y display. "converting all values toString and storing in a temp variable"stream:= ReadStream on:temp."creating a stream to read each word one by one"stream skipSeparators. "Skip whitespaces"[stream atEnd] whileFalse:[| z |z:=(Number readFrom:stream). (x hasMember: z) ifTrue:[inter:=inter,' ',(z printString)] ifFalse:[inter:=inter,'']. stream skipSeparators]. "Performing intersection""Checking the existing type of set X"(x class==BranchingSet) ifTrue:[x:=BranchingSet new] "Create a fresh copy of set X as a Branching set"					ifFalse:[x:=ArrayedSet new]. "Create a fresh copy of set X as a Arrayed set"stream:= ReadStream on:inter."Creating stream to insert values into X"stream skipSeparators.[stream atEnd] whileFalse:[x insert:(Number readFrom:stream).stream skipSeparators].xVal value: x display.^self</body>

<body package="SmallTalkProject1" selector="createAS">createASx:=ArrayedSet new.xVal value:''.UInput value:''.	^self</body>

<body package="SmallTalkProject1" selector="createBS">createBSTranscript show:'creating branching set'. Transcript cr.x:=BranchingSet new.xVal value:''.UInput value:''.	^self</body>

<body package="SmallTalkProject1" selector="addItems">addItems| temp stream|"Obtaining the values inserted in the text field"temp:=UInput value.Transcript print: temp."Creatinf a read stream"stream:=ReadStream on:temp.stream skipSeparators. "Skipping leading whitespaces"Transcript print: temp."to loop till the end of stream"[stream atEnd] whileFalse:[x insert:(Number readFrom:stream).stream skipSeparators]."storing the values held by X"temp:= x display.xVal value: temp.^self</body>
</methods>

<methods>
<class-id>DoubleSet_</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver.""Transcript show: 'In initialize'." "debugging purposes"	super initialize.	x:=nil.	" *** Replace this comment with the appropriate initialization code *** "	^self</body>
</methods>


<methods>
<class-id>BranchingSet class</class-id> <category>instance creation</category>

<body package="SmallTalkProject1" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>BranchingSet</class-id> <category>accessing</category>

<body package="SmallTalkProject1" selector="root">root	^root</body>

<body package="SmallTalkProject1" selector="items">items	^items</body>

<body package="SmallTalkProject1" selector="items:">items: anObject	items:=anObject .</body>

<body package="SmallTalkProject1" selector="root:">root: anObject	root := anObject</body>
</methods>

<methods>
<class-id>BranchingSet</class-id> <category>Operations</category>

<body package="SmallTalkProject1" selector="add:and:">add: aNode and: fromNode 	| temp |	temp:=fromNode.aNode element &lt; temp element ifTrue:[ temp left=nil ifTrue: [temp left: aNode.^self]										ifFalse:[temp:=fromNode left. ^(self add: aNode and:temp)]].aNode element&gt; temp element ifTrue:[temp right=nil ifTrue: [temp right: aNode.^self]										ifFalse:[temp:=fromNode right. ^(self add: aNode and: temp)]].^self</body>

<body package="SmallTalkProject1" selector="display">displayitems:=''.^self inOrder: root</body>

<body package="SmallTalkProject1" selector="inOrder:">inOrder: aNode| temp |temp:=''.aNode=nil ifTrue: [^self ].self inOrder: aNode left."Transcript print: aNode element. Transcript show:' '."temp:=(aNode element printString).items:=items,' ',temp.self inOrder:aNode right.^items</body>

<body package="SmallTalkProject1" selector="copySet">copySet | val |Y:= BranchingSet new.val:=ReadStream on:(self display).val skipSeparators.[val atEnd] whileFalse:[Y insert:(Number readFrom:val). val skipSeparators].YVal update: (Y display).^Y</body>

<body package="SmallTalkProject1" selector="hasMember:">hasMember: val| values |self isEmpt ifTrue:[^false].values:= ReadStream on:items.[values atEnd] whileFalse:[val=(Number readFrom:values) ifTrue:[^true] ifFalse:[values skipSeparators]].^false</body>

<body package="SmallTalkProject1" selector="isEmpt">isEmpt ^root=nil</body>

<body package="SmallTalkProject1" selector="insert:">insert: newElement	| temp|		temp:= BSTNode new.	temp element: newElement.	self isEmpt ifTrue:[root:=temp] "If the tree is empty then assign the newly created node as the root."	ifFalse:[self add: temp and: root].^self</body>
</methods>

<methods>
<class-id>BranchingSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	root := nil.	items:=''.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


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
<class-id>SetCalculator class</class-id> <category>interface specs</category>

<body package="SmallTalkProject1" selector="windowSpec">windowSpec	"Tools.UIPainter new openOnClass: self andSelector: #windowSpec"	&lt;resource: #canvas&gt;	^#(#{UI.FullSpec} 		#window: 		#(#{UI.WindowSpec} 			#label: 'SetCalculator' 			#bounds: #(#{Graphics.Rectangle} 770 221 1210 501 ) ) 		#component: 		#(#{UI.SpecCollection} 			#collection: #(				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 7 0 44 0 127 0 65 0 ) 					#name: #ActionButton1 					#model: #createBS 					#label: 'Create BranchingSet' 					#defaultable: true ) 				#(#{UI.InputFieldSpec} 					#layout: #(#{Graphics.LayoutFrame} 129 0 87 0 249 0 108 0 ) 					#name: #InputField1 					#model: #UInput 					#type: #string ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 256 0 87 0 319 0 108 0 ) 					#name: #ActionButton2 					#model: #addItems 					#label: 'Add Items' 					#defaultable: true ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 324 0 135 0 ) 					#name: #Label1 					#label: 'YSet' ) 				#(#{UI.LabelSpec} 					#layout: #(#{Graphics.LayoutOrigin} 69 0 139 0 ) 					#name: #Label2 					#label: 'XSet' ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 162 0 125 0 214 0 146 0 ) 					#name: #ActionButton3 					#model: #copy 					#label: 'Copy' 					#defaultable: true ) 				#(#{UI.TextEditorSpec} 					#layout: #(#{Graphics.LayoutFrame} 30 0 171 0 130 0 271 0 ) 					#name: #xVal 					#model: #xVal 					#isReadOnly: true 					#tabRequiresControl: true ) 				#(#{UI.TextEditorSpec} 					#layout: #(#{Graphics.LayoutFrame} 333 0 173 0 433 0 273 0 ) 					#name: #YVal 					#model: #YVal 					#isReadOnly: true 					#tabRequiresControl: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 291 0 40 0 399 0 61 0 ) 					#name: #ActionButton4 					#model: #createAS 					#label: 'Create ArrayedSet' 					#defaultable: true ) ) ) )</body>
</methods>


<methods>
<class-id>SetCalculator</class-id> <category>aspects</category>

<body package="SmallTalkProject1" selector="UInput">UInput	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^UInput isNil		ifTrue:			[UInput := String new asValue]		ifFalse:			[UInput]</body>

<body package="SmallTalkProject1" selector="YVal">YVal	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^YVal isNil		ifTrue:			[YVal := String new asValue]		ifFalse:			[YVal]</body>

<body package="SmallTalkProject1" selector="xVal">xVal	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^xVal isNil		ifTrue:			[xVal := String new asValue]		ifFalse:			[xVal]</body>

<body package="SmallTalkProject1" selector="setValues">setValues	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^setValues isNil		ifTrue:			[setValues := String new asValue]		ifFalse:			[setValues]</body>
</methods>

<methods>
<class-id>SetCalculator</class-id> <category>actions</category>

<body package="SmallTalkProject1" selector="addItems">addItems |values |values:= ReadStream on:UInput value.values skipSeparators.[values atEnd] whileFalse:[ x insert:(Number readFrom:values). values skipSeparators].values := x display.Transcript show: values.xVal value: values.^self</body>

<body package="SmallTalkProject1" selector="createAS">createASx:= ArrayedSet new.xVal value: ''.YVal value: ''.^x</body>

<body package="SmallTalkProject1" selector="createBS">createBSx:= BranchingSet new.xVal value: ''.YVal value: ''.^x</body>
</methods>

<methods>
<class-id>SetCalculator</class-id> <category>copying</category>

<body package="SmallTalkProject1" selector="copy">copy| y yval|y:=x copySet.yval:=y display.Transcript print:yval.YVal value: yval.</body>
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

<body package="SmallTalkProject1" selector="display">display	"Method to return all the elements in the set as a String"| tempString |	tempString:= ''. "Declaring a tempString object to hold all the elements in the set"	items isEmpty ifTrue:[tempString:=''].	items do:[:z | tempString:=(z printString),' ',tempString].  "Converting an item in the ordered Collection to a String "^tempString</body>

<body package="SmallTalkProject1" selector="copySet">copySet| z |z:= ArrayedSet new.1 to: items size by: 1 do:[:index| z insert: (items at:index)].^z</body>

<body package="SmallTalkProject1" selector="hasMember:">hasMember: value		items isEmpty ifTrue:[^false]			     ifFalse:[ items do:[:z| value=z ifTrue: [^true]]].		^false</body>

<body package="SmallTalkProject1" selector="toString">toString	"Method to return all the elements in the set as a String"| tempString |	tempString:= ''. "Declaring a tempString object to hold all the elements in the set"	items do:[:z | tempString:=tempString,' ', (z printString)].  "Converting an item in the ordered Collection to a String "	^tempString</body>

<body package="SmallTalkProject1" selector="isEmpt">isEmpt items isEmpty ifTrue:[^true]			ifFalse:[^false].</body>

<body package="SmallTalkProject1" selector="insert:">insert: val(self hasMember: val) ifFalse:[items addFirst:val].^self</body>
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>initialize-release</category>

<body package="SmallTalkProject1" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	items := OrderedCollection new.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>



</st-source>
