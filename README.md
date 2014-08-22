Lupa
====

BigData Clustering Text recommendation

What is recommendation?
-----------------------
A recommendation is a prediction of a rating or preference that a user would grant to a specific object, achieved by using a recommendation system, which is a sub-category of *information filtering system*. The recommendation is based on previously recorded data and its main objective is to boost the conversion rate, foster the cross-selling by proposing complementary products and encourage customer loyalty. It can be implemented on a wide range of products, like videos, news, books and songs, among others and is used by many leading companies such as Amazon, Netflix and Pandora.


Types of automatic recommendation
---------------------------------
It can be distinguished two generic types of recommendation systems in terms of information sources:

* **Collaborative filtering** - takes into account user's past behavior, like items purchased or viewed, as well as collaboration between users and similar decisions made by other users. It predicts user's preferences as a weighted sum of the other users' preferences, where the weights are corresponding to the fraction of correlations of joint set of items assessed by two users.

 The two main disadvantages of this method are that, firstly, it can be used only if there already exists information about the item (at least a couple of users have assessed the product), which means that it cannot recommend a new product. Secondly, this method does not take into account item's characteristics, which can lead to a recommendation of a product from a completely different category


* **Content-based filtering** - uses characteristics of an item to recommend other objects with similar features and is based on user preferences for specific values of product characteristics. Also it can employ importance ratings and feature's trade-offs to construct recommendations. For instance, in movie recommendation it may take into account factors such as genre, actors or director. In the case of music, personalized online radio stations are created on base of fundamental music features like types of instruments or rhythm.

`CLARIFY MK`
 The main disadvantage of this type of recommendation is that it is not able to construct recommendations for users that does not give any preference information. In comparison with collaborative filtering it admits recommendations of totally new products, but it does not take into account the preference similarity through users.

In the absence of any user preferences or a completely anonymous system, a common solution is to assume the user prefers the item that she is viewing or selecting
`CLARIFY MK`


Regardless of the chosen type, an effective recommendation system should be able to use at least one of the following five information sources:

1. A person’s expressed preferences or choices among alternative products
2. Preferences  or choices of other consumers
3. Product characteristics and its preferences
4. Individual characteristics that may predict preferences
5. Expert evaluations

In the case of _Lupa_, the chosen recommendation method is *Content-based filtering*, using item-to-item `EXPAND JJ`
