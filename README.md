Lupa
====

BigData Clustering Text recommendation

What is recommendation?
-----------------------
A recommendation is a preditction of a rating or preferece that a user would grant to a specific object, achieved by using a recommendation system, which is a sub-category of *information filtering system*. The recommendation is based on previously recorded data and its main objective is to boost the conversion rate and the cross-selling by proposing complementary products, as well as the customer's loyalty.


Types of automatic recommendation
---------------------------------
It be distinguish two generic types of recommendations systems in terms of information sources:

1. **Collaborative filtering ~~(User to User)~~** - takes into account user's past behavior, like items purchased or viewed, as well as collaboration between users and similar decisions made by other users. It predicts user's preferences as a combination of other user's preferencesThrough ratings given by other users it can be predicted a rating of another user, and that way, recommend him/her a specific content.

can be used only if at least fer people have evaluated the product


2. **Content-based filtering** - uses characteristics of an item to recommend other objects with similar features.
  * **Item to Item** For example, for movies it may take into account factors such as genre, actors or director.
  * **Topic to Topic** In the case of music, personalized online radio stations considers types of intruments or rythm to define the music category, and then, recommend songs or artist from the same group.


is based on consumer preferences for product attributes
can use importance ratings and attribute trade-offs to make recommendations
do not incorporate the information in preference similarity across indivuals
cannot make recommendations for people who provide no preference information

Regardless of the chosen type, an effective recommendation system should be able to use at least on of the following five information sources:

1. A person’s expressed preferences or choices among alternative products
2. Preferences  or choices of other consumers
3. Expert evaluations
4. Product characteristics and its preferences
5. Individual characteristics that may predict preferences
