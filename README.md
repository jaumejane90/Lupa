Lupa
====

BigData Clustering Text recommendation

What is recommendation?
-----------------------
A recommendation is a preditction of a rating or preferece that a user would grant to a specific object, achieved by using a recommendation system, which is a sub-category of *information filtering system*. The recommendation is based on previously recorded data and its main objective is to boost the conversion rate, foster the cross-selling by proposing complementary products and encourage customer's loyalty. It can be implemented on a wide range of products, like videos, news, books and songs, among others and is used by many leader companies such as Amazon, Netflix and Pandora.


Types of automatic recommendation
---------------------------------
It can be distinguished two generic types of recommendations systems in terms of information sources:

* **Collaborative filtering** - takes into account user's past behavior, like items purchased or viewed, as well as collaboration between users and similar decisions made by other users. It predicts user's preferences as a weighted sum of other users' preferences, where the weights are corresponding to fraction of correlations of joint set of items assessed by two users.

 The two main disadvantatges of this method are that, firstly, it can be used if there already exists information about   the item (at least couple of users have assessed the product), which means that it cannot recommend a new product.       Secondly, this method does not take into account item characteristics, which can lead to a recommendation of a product   from a completly disparate category


* **Content-based filtering** - uses characteristics of an item to recommend other objects with similar features and is based on user preferences for product characteristic. Also can employ importance ratings and feature's trade-offs to construct recommendations. For instance, in movies recommendation it may take into account factors such as genre, actors or director. In the case of music, personalized online radio stations are created in base of fundamental features like types of intruments or rhythm.

 The main disadvantage of this type of recommendation is that it is not able to construct recommendations for users that  does not give any preference information. In comparison with collaborative filtering it admits recomendations of totally  new products, but it does not take into account the preference similarity through users
 


Regardless of the chosen type, an effective recommendation system should be able to use at least on of the following five information sources:

1. A person’s expressed preferences or choices among alternative products
2. Preferences  or choices of other consumers
3. Expert evaluations
4. Product characteristics and its preferences
5. Individual characteristics that may predict preferences
