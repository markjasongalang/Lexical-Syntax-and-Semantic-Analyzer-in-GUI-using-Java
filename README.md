## Lexical-Syntax-and-Semantic-Analyzer-in-GUI-using-Java
### Quick Info
This project consists of the first three phases of compiling a programming language.  
1. <b>Lexical Analysis</b> - breaking the code into tokens (e.g., <INT, 65>, <PLUS, +>, etc...)  
2. <b>Syntax Analysis</b> - checks if the structure of the code written is correct based on the rules determined  
3. <b>Semantic Analysis</b> - examines the meaning of the code itself (e.g., int value = "hello";, is incorrect because it should only store an integer)  
### Made-up Programming Language (to be used)
The programming language that can be used here is only concocted for educational purposes and the 3 keywords are:  
> <b>var</b> - declares a new variable (note: all variables must be declared first before using them)  
 <b>input</b> - reads a variable from the console  
 <b>output</b> - displays (in the console) the value of the variable or a mathematical expression that was specified
### Programming Language Constraints
- All statements will end in a semi-colon
- White is not significant (e.g., a+b is equal to a + b)
- Comments begin with /* and end with */ and can extend in multiple lines
- The value of all variables must be integers or decimals (positive or negative).
- The only operations allowed are: {+, -, *, /, =}
- Parentheses may be used in mathematical expressions
- Identifiers may only consist of uppercase and lowercase letters, underscores, and digits
- An idenfitier may not begin with a number
### Syntax Rules (that I created)
https://drive.google.com/file/d/1_VOKo4xagQBumHLjf8uXAicxHtOG98D9/view?usp=sharing
### 3 Main Parts of the Program
<b>Code Editor</b> - this is where you can use the made-up programming language wherein the compiling phases were based upon  
<b>Lexical Analyzer</b> - this is where you can view the tokens that were extracted from the code  
<b>Console</b> - this is where the result of the code (assuming it's correct) is displayed and it is also interactive  
### Buttons
<b>Compile & Run</b> - once clicked, the code undergoes the first three phases of compiling (Lexical, Syntax, and Semantic Analysis), and then the result of the code is displayed in the console (however, if the code is incorrect, then the error will be displayed instead)  
<b>Save</b> - creates a text file wherein the code will be saved (the name of the file is an input)  
<b>Load</b> - loads a text file into the code editor  
<b>Undo</b> - repeats the previous operation  
<b>Redo</b> - repeats the operation that was undid
### Screenshots
![image](https://user-images.githubusercontent.com/104606066/183290617-5e829661-a9ca-4b9e-b10a-89967c60745f.png)
---
![image](https://user-images.githubusercontent.com/104606066/183290654-dbfffa3d-5178-4f31-a38b-e8f2ec784642.png)
---
![image](https://user-images.githubusercontent.com/104606066/183290878-dfde69c8-e44f-4f95-afa6-4e92333bc741.png)
