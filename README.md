This is My version of JLox from the book [craftinginterpreters.](http://www.craftinginterpreters.com/)   
It is mostly the same as presented in the book, but I have made a few changes:  
- Integers
  - int values are supported alongside doubles.
  - when doing math, if both operands are ints, then integer math is used, otherwise both operands are cast to doubles.
- String concatenation  
  - concat is done using the `..` operator and not the `+`  operator.  
  - concat operator converts all operands to a String representation.  
- some keywords have been changed  
  - var -> stash
  - class -> designation
  - func -> functi
  - print -> say
  - this -> self
- internal changes
  - code has been organized along a more java enterprise style of coding.
  - I used java 13 features such as enhanced switch and instanceof operators when applicable.
  - I used streams rather than loops where applicable.
  - reorganized some code to be in a separate method/class. reorganized some switch cases. did a few other small miscellaneous code changes.
