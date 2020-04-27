# Report for Introduction to Artificial Intelligence Assignment 2
##### Prepared by Gainullin Timur


For this project firstly I needed to research for the answer to "What is an art and non-art?" question. 
As I know there is no legitimate criteria was found. Therefore I had to choose from different theories. 
There are Expressionists, Emotionalists, Formalists, Organicists, Intuitionists, Voluntarists, etc. 
For EA I need a function that will define criteria if a given image is art or not-art. 
Formalists ideas are most suitable for that purpose. 
For Formalists, an artist should use elements such as colour, line, shape, texture.
Elements should come together using certain rules of composition, colour theory, etc. 

So for one of the criteria, I choose acceptance of the image with colour theory. 
The program has some basic colour palettes. 
All of them strict with relations of main colours in the colour circle. 
Colours in one palette can be Monochromatic, Complementary, Analogous, Triadic, Tetradic, etc.
The program calculates to which palette the image corresponding and how much.
From that, it gives points to the image.

Final art should look like the given picture, so artwork gets the most points for similarity to the picture. 
To make images look more pleasant I decided to add functionality for extra points 
if colours on image smoothly changing and if it has a nice composition. 
I tried finding most valuable spots in the picture via most contrast, brightest or darkest squares in int.
But this checking composition in the image didn't work well, so I don't use it. 


There are examples of how EA changed given pictures using only points for Color Theory:

[example1]: https://github.com/Tumypmyp/IAI_assignment2/blob/master/AI_images/color_theory_only/example1.png "Example 1"
[example2]: https://github.com/Tumypmyp/IAI_assignment2/blob/master/AI_images/color_theory_only/example2.png "Example 2"

![alt-text-1](example1) 
![alt-text-2](example2)


<p float="left">
  <img src="/AI_images/color_theory_only/example1.png" width="100" />
  <img src="/AI_images/color_theory_only/example2.png" width="100" /> 
  <img src="/AI_images/color_theory_only/example3.png" width="100" />
  <img src="/AI_images/color_theory_only/example4.png" width="100" />
  <img src="/AI_images/color_theory_only/example5.png" width="100" />
</p>

Pop size | Gen num | Avg survived| Time| Score
---|---|---|---|---
||||
300|20|200|5|314
300|39|200|10|323
300|121|200|30|340
300|222|200|60|345
300|327|200|90|348
||||
300|12|150|5|315
300|25|150|10|324
300|40|200|30|338
||||
30|83|15|3|317
30|146|15|5|321
30|276|15|10|328    
||||
||||
30|249|15|10|963
||||
100|100|50|10|996
100|208|50|20|1017
100|317|50|30|1030
||||
300|33|150|10|976
300|63|150|20|1004
||||
300|21|50|10|981
300|42|50|20|1015
300|62|50|30|1030
300|100|50|50|1044
||||
300|40|200|10|973
300|17|75|10|960
200|32|66|10|970
||||        
150|47|110|10|942
150|93|110|20|970
150|280|110|60|1020
||||
120|70|60|10|985
130|60|70|10|982
130|106|70|20|995





used matirials:
[about complimentary colors](https://serennu.com/colour/rgbtohsl.php)

[the code probably used for RGB to HSL](http://biginteger.blogspot.com/2012/01/convert-rgb-to-hsl-and-vice-versa-in.html)

[or this](https://tips4java.wordpress.com/2009/07/05/hsl-color/)

[philosophical question art and non-art](https://medium.com/@christopherwillardauthor/distinguishing-art-from-non-art-discussion-2-part-1-fec2feaa36b4)

[color wheel theory](https://www.canva.com/colors/color-wheel/)