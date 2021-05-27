(define disk '("D" "top"
                   (
                    ("D" "sub1"
                         (
                          ( "F" "file1.txt" 1234)
                          ( "F" "file2.txt" 2345)
                          ( "F" "file3.txt" 3456)
                          )
                         )
                    ("D" "sub1"
                         (
                          ( "F" "file1.txt" 1234)
                          ( "F" "file2.txt" 2345)
                          ( "F" "file3.txt" 3456)
                          )
                         )
                    ("D" "sub2"
                         (
                          ( "F" "file1.txt" 1234)
                          ( "F" "file2.txt" 2345)
                          ( "F" "file3.txt" 3456)
                          )
                         )
                    )
                   )
)

(define (processElement a)
  ;a holds the parent directory's child list, minus the first element
  ;check the first element and pass it to either File or Directory
  ;Then call processElement on the parent directory's child list, minus the first element and second element
  ;this should go through every child until the list is empty

  ;a = ((File || Dir) (File || Dir) ...)

  ;Get first element of a -> Have to do this outside of cond because cond is an expression context
  ;(define childElement (car a))
  ;I had to comment this out since a is an empty list at the end of the recursion and I didnt know how/ didnt want to work with nested conditional statements
  ;so I just used nested car statements
  
  ;childElement = ("D" name children()) || ("F" name size)
  ;********Check for null list ---I know this is going to need to be editted in the future I just don't know why yet
  (cond ((pair? a)
         
         ;Check if the childElement is File or Directory
         (if (eq? (car (car a)) "F") (file (car a)) (directory (car a)))
         ;Recursively call process element on a minus it's first element
         (processElement (cdr a))
         )        
  )
  
)
  
(define (file a)
  ;add file size to totalFileSize
  (set! totalFileSize (+ totalFileSize (car (cdr (cdr a)))))
)

(define (directory a)
  ;This is our root function, from here we will parse the nested directory tree
  ;Check if the current object is a directory
  ;If it is a directory, check all of its children for directories
  ;If a file is found, add its length to sum: If sum is a global variable we dont need to return anything however if sum is local, return sum all the way up the nested tree
  ;repeat this process until we have reached the end of the top directory's list of children

  ;This has to be recursive, so how will the methods work with each other...
  ;Process directory contents could check if file or directory for all children
  ;Process Element could be used to return the size of a file element... or maybe process the list within a directory
  ;What would the file function do? we use directory to instantiate our "disk" list
  ;Everything in scheme is a list...
 
  ;if we are calling this function we are in a directory, so we have to iterate thru all children and check their types
  ;***********Check for empty directory
  ;a is a list

  ;***********Get the Child List
  ;directory objects have 3 parts, a = (dirIdentifier dirName dirContents()), more accurately a = (dirIdentifier . (dirName . (dirContents() . ())))
  ;
  ;to get dirContents we have to! (car (cdr (cdr a))):                                              cdr->            cdr->      ^car^  
  (define dirChild (car (cdr (cdr a))))
  ;dirChild now holds the contents of the DIRECTORY LIST
  ;(("f" "name.txt" size) ("d" "name" dirChildren())...)
  ;*********** Checking for empty directory list
  ;we use pair? to check if dirChild is not null, since lists are pairs
  (cond ((pair? dirChild)
         ; Since dirChild holds a list, we have to somehow recursively check all of the elements
         ;
         ;************* Check type of first element
         ;if File, call file, if Directory... idk yet
         (if (eq? (car(car dirChild)) "F") (file (car dirChild)) (directory (car dirChild))))
        ;
        ;How do we recursively loop through the list of children...
        ;
        ;Im gonna call process Element and pass in dirChild minus the first element
   )
  ;removing first element
  (processElement (cdr dirChild))
)

(define (processDirectoryContents a)
  (eq? "A" "B")
)
;***************************************   GENERAL NOTES ON SYNTAX   *****************************************************************************************************
;OUR SCHEME LANGUAGE VERSION IS R5RS
;
;Let will define a temporary variable, define gives a variable global scope and a constant state that can be mutated multiple times throughout the lifetime of the program
;Set! sets the value of a variable and updates the variable value globally where define only updates the value for that scope
;
;conditionals take the following form (cond [((Conditional Expression) Resulting Function/Behavior)]+ (else Default Function/Behavior)*)
;
;car returns the first element from a PAIR, cdr returns the second element from a PAIR
;
;pairs are used to represent lists ex. (a b c d) is equal to (a . (b . (c . (d . ()))))
;*************************************************************************************************************************************************************************

;Lets define our global file size variable and continuously add to it
(define totalFileSize 0)
(directory disk)
;Prints out the totalFileSize
"total file size:"
totalFileSize
;14100 is desired output for this skeleton