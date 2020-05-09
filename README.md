# Report for Introduction to Artificial Intelligence Assignment 2
##### Prepared by Gainullin Timur
##### [github link](https://github.com/Tumypmyp/IAI_assignment2)

To use download or clone this repository and execute:
```
java -jar IAI.jar
```


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

<p float="left">
  <img src="/AI_images/color_theory_only/example1.png" width="130" />
  <img src="/AI_images/color_theory_only/example2.png" width="130" /> 
  <img src="/AI_images/color_theory_only/example3.png" width="130" />
  <img src="/AI_images/color_theory_only/example4.png" width="130" />
  <img src="/AI_images/color_theory_only/example5.png" width="130" />
</p>

In the GitHub repository, you can see the output folder where images sorted by points the program gave them. 
As additional information, the most suitable colour palette is written with image points too.

<p float="left">
  <img src="/input/image04.png" width="130" />
  <img src="/input/image13.png" width="130" />
  <img src="/input/image14.png" width="130" />
  <img src="/input/image16.png" width="130" />
</p>

<p float="left">
  <img src="/AI_images/image04_result.png" width="130" />
  <img src="/AI_images/image13_result.png" width="130" /> 
  <img src="/AI_images/image14_result.png" width="130" />
  <img src="/AI_images/image16_result.png" width="130" />
</p>

There is contest image that I remembered to send to late

<p float="left">
  <img src="/AI_images/contest_image.png" width="200" />
</p>

After some optimizations works much faster, there are the results:

<p float="left">
  <img src="/AI_images/image01.gif" width="200" />
  <img src="/AI_images/clown.gif" width="200" />
  <img src="/AI_images/image02.gif" width="200" />
</p>
<p float="left">
  <img src="/AI_images/image01.png" width="200" />
  <img src="/AI_images/clown.png" width="200" /> 
  <img src="/AI_images/image02.png" width="200" /> 
</p>


In order to choose the best constants for EA which are:  
 - Population size
 - Number of generations
 - Mutation Rate
 - Mutation percentage on population
 - Crossover percentage on population
I tested different ones and filled following table. 

Pop size | Gen num | Avg survived | Time | Score
---|---|---|---|---
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

I think my program did make art. In Formalists way of view, it is not perfect but can be estimated as one.


used matirials:

[Complimentary colors](https://serennu.com/colour/rgbtohsl.php)

[Java Class helping convert from RGB to HSL and backwards](https://tips4java.wordpress.com/2009/07/05/hsl-color/)

[philosophical writing about art and non-art](https://medium.com/@christopherwillardauthor/distinguishing-art-from-non-art-discussion-2-part-1-fec2feaa36b4)

[color wheel theory](https://www.canva.com/colors/color-wheel/)