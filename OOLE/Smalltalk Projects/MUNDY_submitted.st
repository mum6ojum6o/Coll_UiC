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

<body package="CS474" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>DoubleSet</class-id> <category>Operations</category>

<body package="CS474" selector="hasMember:">hasMember:anObject"abstract method to check the existence of an element in the datastructure"^self subclassResponsibility</body>

<body package="CS474" selector="intersect:">intersect: anObject"method to display the intersection of set x and y"| temp inter stream|inter:=''. "inter will hold the set values in String format"temp:=anObject display."creating a readstream to read the contents of the selector set"stream:= ReadStream on:(anObject display).stream skipSeparators. "Skipping separators"[stream atEnd]whileFalse:[| x |x:=(Number readFrom:stream). (self hasMember: x) "if an element exists in the set"					ifTrue:[inter:=inter,' ',(x printString)] "dont add it to the inter variable"					ifFalse:[inter:=inter,'']. stream skipSeparators]."append it to the inter variable"(self class==BranchingSet) ifTrue:[temp:=BranchingSet new] "create a temp object on the basis of the receiver"					ifFalse:[temp:= ArrayedSet new]. stream:= ReadStream on:inter. "creating a readstream tp parse all the inttersect values into the temp set"stream skipSeparators.[stream atEnd]whileFalse:[temp insert:(Number readFrom:stream). stream skipSeparators ]." inserting intersect values to the temp set"	^temp "returning the temp object"</body>

<body package="CS474" selector="display">display"abstract method to convert the contents of a set to a string"^self subclassResponsibility</body>

<body package="CS474" selector="insert:">insert: anObject"abstract method to insert element inside the receiver set"^self subclassResponsibility</body>

<body package="CS474" selector="union:">union: anObject """Method to list out the determine the union of two sets"	| unionSet stream temp|	"unionSet will contain the values as in set x "unionSet:=''."Storing the values in set x in unionSet"(self isEmpt) ifTrue:[unionSet:='']			ifFalse:[unionSet:=unionSet,' ',(self display)].anObject isEmpt ifTrue:[unionSet:=unionSet,' '].	"Extracting the values in Set Y"		stream:= ReadStream on:(anObject display).	stream skipSeparators.	"Read the contents in Set Y one by one and check with values present in Set x extracted earlier."	"If a value in set y is not present in unionSet variable, then it is appended to it"[stream atEnd] whileFalse:[| x | x:=(Number readFrom:stream). (self hasMember:x) ifTrue:[unionSet:=unionSet,''] ifFalse:[unionSet:=unionSet,' ',(x printString)]. stream skipSeparators ].(self class==ArrayedSet) ifTrue:[temp:=ArrayedSet new.]"creating a temp object on the basis of the receiver object type"				ifFalse:[temp:=BranchingSet new].				"creating readStream to insert values to the temp object"stream:=ReadStream on:unionSet.stream skipSeparators.[stream atEnd] whileFalse:[temp insert:(Number readFrom:stream).stream skipSeparators].^temp</body>
</methods>

<methods>
<class-id>DoubleSet</class-id> <category>initialize-release</category>

<body package="CS474" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.		" *** Replace this comment with the appropriate initialization code *** "	^self</body>
</methods>


<methods>
<class-id>ArrayedSet class</class-id> <category>instance creation</category>

<body package="CS474" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>ArrayedSet</class-id> <category>accessing</category>

<body package="CS474" selector="items:">items: anObject	items := anObject</body>

<body package="CS474" selector="items">items	^items</body>
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>Operations</category>

<body package="CS474" selector="copySet">copySet"method to copy the contents of the receiver into a new object y"| y |y:= ArrayedSet new."creating a new object"1 to: items size by: 1 do:[:index| y insert: (items at:index)]."copying the contents of the reciever to Y"^y "returning y"</body>

<body package="CS474" selector="hasMember:">hasMember: value	"Method that checks the existence of selctor newElement in the receiver"	items isEmpty ifTrue:[^false] "return false if the receiver is empty"			     ifFalse:[ items do:[:x| value=x ifTrue: [^true]]]. "return true if the selector is present in the receiver"		^false</body>

<body package="CS474" selector="display">display	"Method to return all the elements in the set as a String"| tempString |	tempString:= ''. "Declaring a tempString object to hold all the elements in the set"	items isEmpty ifTrue:[tempString:=''].	items do:[:x | tempString:=tempString,' ', (x printString)].  "Converting an item in the ordered Collection to a String "	^tempString</body>

<body package="CS474" selector="insert:">insert: x"Method to insert an element to the set"(self hasMember: x)ifTrue:[^false] "return false if the selctor is present inthe receiver"			ifFalse:[items addFirst: x]."add the selector to the receiver"^self</body>

<body package="CS474" selector="isEmpt">isEmpt "method that returns true if the receiver is empty. false if it is not"items isEmpty ifTrue:[^true]			ifFalse:[^false].</body>
</methods>

<methods>
<class-id>ArrayedSet</class-id> <category>initialize-release</category>

<body package="CS474" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	items := OrderedCollection new.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


<methods>
<class-id>BranchingSet class</class-id> <category>instance creation</category>

<body package="CS474" selector="new">new	"Answer a newly created and initialized instance."	^super new initialize</body>
</methods>


<methods>
<class-id>BranchingSet</class-id> <category>accessing</category>

<body package="CS474" selector="root">root	^root</body>

<body package="CS474" selector="items:">items: anObject	items:=anObject .</body>

<body package="CS474" selector="items">items	^items</body>

<body package="CS474" selector="root:">root: anObject	root := anObject</body>
</methods>

<methods>
<class-id>BranchingSet</class-id> <category>Operations</category>

<body package="CS474" selector="copySet">copySet | Y stream|"create a new Branchingset object"Y:=BranchingSet new."creating a stream to re"stream:= ReadStream on:(self display).stream skipSeparators.[stream atEnd] whileFalse:[Y insert:(Number readFrom:stream). stream skipSeparators].^Y</body>

<body package="CS474" selector="display">display"method to covert the contents of the receiver to a string"items:=''.^self inOrder: root</body>

<body package="CS474" selector="inOrder:">inOrder: aNode"method to traverse the contents of the BST in an Inorder fashion"| temp |temp:=''.aNode=nil ifTrue: [^self ]. self inOrder: aNode left.temp:=(aNode element printString)."storing the content of the node with no left substree"items:=items,' ',temp. "appending the latest node element value with no subtree"self inOrder:aNode right."traverse the right subtree"^items</body>

<body package="CS474" selector="add:and:">add: aNode and: fromNode "Method inserts elements in the BST"	| temp |	temp:=fromNode.	"Comparing the value to be inserted with the parent nodes"	"if the new element value is less than the parent node value then move to left subtree"aNode element &lt; temp element ifTrue:[ temp left=nil ifTrue: [temp left: aNode.^self]										ifFalse:[temp:=fromNode left. ^(self add: aNode and:temp)]]."if the new element value is greater than the parent node value then move to right subtree"									aNode element&gt; temp element ifTrue:[temp right=nil ifTrue: [temp right: aNode.^self]										ifFalse:[temp:=fromNode right. ^(self add: aNode and: temp)]].^self</body>

<body package="CS474" selector="isEmpt">isEmpt "checks if th receiver is empty"^root=nil</body>

<body package="CS474" selector="insert:">insert: newElement	| temp|	"creating a node to be attached to the BST"	temp:= BSTNode new.	"Assingning the newElement to the newly created node value"	temp element: newElement."Checking if the BST is empty"	self isEmpt ifTrue:[root:=temp] "If the tree is empty then assign the newly created node as the root."	"If BST is not empty, then message the add:and: method"	ifFalse:[self add: temp and: root].^self</body>

<body package="CS474" selector="hasMember:">hasMember: x"method that returns false if the element is present in the receiver"| values |self isEmpt ifTrue:[^false].values:= ReadStream on:items.[values atEnd] whileFalse:[x=(Number readFrom:values) ifTrue:[^true] ifFalse:[values skipSeparators]]."return true if the selector is present in thee receiver items"^false</body>
</methods>

<methods>
<class-id>BranchingSet</class-id> <category>initialize-release</category>

<body package="CS474" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	root := nil.	items:=''.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


<methods>
<class-id>BSTNode</class-id> <category>accessing</category>

<body package="CS474" selector="right:">right: anObject	right := anObject</body>

<body package="CS474" selector="element">element	^element</body>

<body package="CS474" selector="left:">left: anObject	left := anObject</body>

<body package="CS474" selector="right">right	^right</body>

<body package="CS474" selector="element:">element: anObject	element := anObject</body>

<body package="CS474" selector="left">left	^left</body>
</methods>

<methods>
<class-id>BSTNode</class-id> <category>BSTNode_Ops</category>

<body package="CS474" selector="create:">create: newElement	| temp |	temp:= BSTNode new.	temp element: newElement.	^self</body>
</methods>

<methods>
<class-id>BSTNode</class-id> <category>initialize-release</category>

<body package="CS474" selector="initialize">initialize	"Initialize a newly created instance. This method must answer the receiver."	super initialize.	" *** Edit the following to properly initialize instance variables ***"	element := nil.	left := nil.	right := nil.	" *** And replace this comment with additional initialization code *** "	^self</body>
</methods>


<methods>
<class-id>SetCalculator class</class-id> <category>interface specs</category>

<body package="CS474" selector="windowSpec">windowSpec	"Tools.UIPainter new openOnClass: self andSelector: #windowSpec"	&lt;resource: #canvas&gt;	^#(#{UI.FullSpec} 		#window: 		#(#{UI.WindowSpec} 			#label: 'SetCalculator' 			#bounds: #(#{Graphics.Rectangle} 463 224 903 504 ) ) 		#component: 		#(#{UI.SpecCollection} 			#collection: #(				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 24 0 42 0 146 0 63 0 ) 					#name: #'Create BranchingSet_ActionButton' 					#model: #createBS 					#label: 'Create Branching Set' 					#defaultable: true ) 				#(#{UI.InputFieldSpec} 					#layout: #(#{Graphics.LayoutFrame} 151 0 92 0 251 0 113 0 ) 					#name: #AddItems_InputField 					#model: #UInput 					#type: #string ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 269 0 90 0 332 0 113 0 ) 					#name: #AddItems_ActionButton2 					#model: #addItems 					#label: 'Add Items' 					#defaultable: true ) 				#(#{UI.TextEditorSpec} 					#layout: #(#{Graphics.LayoutFrame} 29 0 145 0 129 0 245 0 ) 					#name: #xVal_TextEditor 					#model: #xValues 					#isReadOnly: true 					#tabRequiresControl: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 292 0 42 0 414 0 63 0 ) 					#name: #CreateArrayedSet_ActionButton 					#model: #createAS 					#label: 'Create Arrayed Set' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 182 0 130 0 234 0 151 0 ) 					#name: #copySets_ActionButton 					#model: #copySets 					#label: 'Copy Set' 					#defaultable: true ) 				#(#{UI.TextEditorSpec} 					#layout: #(#{Graphics.LayoutFrame} 320 0 140 0 420 0 240 0 ) 					#name: #yVal_TextEditor 					#model: #yValues 					#isReadOnly: true 					#tabRequiresControl: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 183 0 164 0 235 0 185 0 ) 					#name: #Union_ActionButton 					#model: #union 					#label: 'Union' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 183 0 198 0 235 0 219 0 ) 					#name: #Intersect_ActionButton 					#model: #intersect 					#label: 'Intersect' 					#defaultable: true ) 				#(#{UI.ActionButtonSpec} 					#layout: #(#{Graphics.LayoutFrame} 184 0 229 0 236 0 250 0 ) 					#name: #Switch_ActionButton 					#model: #switch 					#label: 'Switch' 					#defaultable: true ) ) ) )</body>
</methods>


<methods>
<class-id>SetCalculator</class-id> <category>actions</category>

<body package="CS474" selector="copySets">copySets"Copies the values of set x to set y""copySet is a method defined in the superclass DoubleSet"y:= x copySet.yValues value: (y display)."displaying values in yValues text box"^self</body>

<body package="CS474" selector="createAS">createAS"Create an Arrayed Set X"x:= ArrayedSet new.UInput value:''. "clearing the set values field"xValues value:''. "clearing the xValues field that displays the contents of set x"	^self</body>

<body package="CS474" selector="addItems">addItems|values |"Putting the content of the User Input in a readStream"values:= ReadStream on:(UInput value).values skipSeparators.  "Skipping separators""Inserting values in the set X"[values atEnd] whileFalse:[x insert:(Number readFrom:values). values skipSeparators]."To display the values in the text editor(read Only)"xValues value: x display.^self</body>

<body package="CS474" selector="switch">switch"Method to switch sets"|temp | "variable to store the reference of a set"temp:=x."swapping the referenxes for sets x and y"x:=y.xValues value:x display.y:=temp.yValues value: y display.^self</body>

<body package="CS474" selector="union">union"messaging the union method in the abstract Super class."x:= x union: y.xValues value: x display.	"This stub method was generated by UIDefiner"^self</body>

<body package="CS474" selector="createBS">createBS"Method to create a Branching set"x:= BranchingSet new. "create an instance of the branching set class"xValues value:''. "clearing the xValues field which displays the contents of set x Input fields"UInput value:''."clears the set values field "^x</body>

<body package="CS474" selector="intersect">intersect"messaging the instersect method in the abstract Super class."x:= x intersect: y.xValues value:(x display)."displaying the intersect of sets x and y in xValues field"^self</body>
</methods>

<methods>
<class-id>SetCalculator</class-id> <category>aspects</category>

<body package="CS474" selector="xValues">xValues	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^xValues isNil		ifTrue:			[xValues := String new asValue]		ifFalse:			[xValues]</body>

<body package="CS474" selector="setValues">setValues	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^setValues isNil		ifTrue:			[setValues := String new asValue]		ifFalse:			[setValues]</body>

<body package="CS474" selector="yValues">yValues	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^yValues isNil		ifTrue:			[yValues := String new asValue]		ifFalse:			[yValues]</body>

<body package="CS474" selector="UInput">UInput	"This method was generated by UIDefiner.  Any edits made here	may be lost whenever methods are automatically defined.  The	initialization provided below may have been preempted by an	initialize method."	^UInput isNil		ifTrue:			[UInput := String new asValue]		ifFalse:			[UInput]</body>
</methods>



</st-source>
